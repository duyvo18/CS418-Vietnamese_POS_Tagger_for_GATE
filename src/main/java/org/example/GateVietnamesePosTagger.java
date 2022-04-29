package org.example;

import gate.*;
import gate.annotation.DefaultAnnotationFactory;
import gate.creole.*;
import gate.creole.metadata.*;

import gate.relations.RelationSet;
import gate.util.InvalidOffsetException;
import org.apache.log4j.Logger;
import vn.pipeline.*;
import vn.pipeline.Annotation;

import java.io.IOException;

@CreoleResource(
    name = "Vietnamese POS Tagger",
    comment = "(A short one-line description of this PR, suitable for a tooltip in the GUI)")
public class GateVietnamesePosTagger extends AbstractLanguageAnalyser {

  private static final Logger log = Logger.getLogger(GateVietnamesePosTagger.class);

  /**
   * Annotation set name from which this PR will take its input annotations,
   * is defaulted to ANNIE's Sentence Splitter Annotation
   */
  private String inputASName = ANNIEConstants.SENTENCE_ANNOTATION_TYPE;

  /**
   * Annotation set name into which this PR will create new annotations.
   */
  private String outputASName;


  public String getInputASName() {
    return inputASName;
  }

  // TODO: Set inputASName is not runtime
  @Optional
  @RunTime
  @CreoleParameter(comment = "The annotation set used for input annotations")
  public void setInputASName(String inputASName) {
    this.inputASName = inputASName;
  }

  public String getOutputASName() {
    return outputASName;
  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "The annotation set used for output annotations")
  public void setOutputASName(String outputASName) {
    this.outputASName = outputASName;
  }

  /**
   * Initialize this Vietnamese POS Tagger.
   * @return this resource.
   * @throws ResourceInstantiationException if an error occurs during init.
   */
  public Resource init() throws ResourceInstantiationException {
    log.debug("Vietnamese POS Tagger is initializing");

    // your initialization code here

    return this;
  }

  /**
   * Execute this Vietnamese POS Tagger over the current document.
   * @throws ExecutionException if an error occurs during processing.
   */
  public void execute() throws ExecutionException {
    // check the interrupt flag before we start - in a long-running PR it is
    // good practice to check this flag at appropriate key points in the
    // execution, to allow the user to interrupt processing if it takes too
    // long.
    if(isInterrupted()) {
      throw new ExecutionInterruptedException("Execution of Vietnamese POS Tagger has been interrupted!");
    }
    interrupted = false;

    Corpus corpus = getCorpus();

    for (Document doc : corpus) {
      AnnotationSet annoSet = doc.getAnnotations();
      AnnotationSet sentenceAnnoSet = annoSet.get(inputASName);

      for (gate.Annotation sentenceAnno  : sentenceAnnoSet) {
        try {
          String rawSentence = doc.getContent().getContent(
                  sentenceAnno.getStartNode().getOffset(),
                  sentenceAnno.getEndNode().getOffset()
          ).toString();

          VnCoreNLP pipeline = new VnCoreNLP(new String[]{"wseg", "pos"});
          vn.pipeline.Annotation anno = new Annotation(rawSentence);
          pipeline.annotate(anno);

          for (Word tokenizedWord : anno.getWords()) {
            Long startOffset = getStartOffset(tokenizedWord, doc, sentenceAnno);
            Long endOffset = getEndOffset(tokenizedWord, doc, sentenceAnno);

            sentenceAnnoSet.add(
                    startOffset,
                    endOffset,
                    tokenizedWord.getPosTag(),
                    Factory.newFeatureMap()
            );
          }
        } catch (InvalidOffsetException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private Long getStartOffset(Word tokenizedWord, gate.Document doc, gate.Annotation sentenceAnno) {
    // TODO: implement
    try {
      String rawSentence = doc.getContent().getContent(
              sentenceAnno.getStartNode().getOffset(),
              sentenceAnno.getEndNode().getOffset()
      ).toString();

      String word = tokenizedWord.getForm();

      int idx = rawSentence.indexOf(word);

      return sentenceAnno.getStartNode().getOffset() + idx;
    } catch (InvalidOffsetException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Long getEndOffset(Word tokenizedWord, gate.Document doc, gate.Annotation sentenceAnno) {
    // TODO: implement
    try {
      String rawSentence = doc.getContent().getContent(
              sentenceAnno.getStartNode().getOffset(),
              sentenceAnno.getEndNode().getOffset()
      ).toString();

      String word = tokenizedWord.getForm();

      int idx = rawSentence.indexOf(word);

      return sentenceAnno.getStartNode().getOffset() + idx + word.length();
    } catch (InvalidOffsetException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {

  }
}

