package org.example;

import gate.*;
import gate.creole.*;
import gate.creole.metadata.*;

import gate.util.InvalidOffsetException;

import rdrsegmenter.RDRsegmenter;
import marmot.tokenize.openlp.OpenNlpTokenizer;

import java.io.IOException;

@CreoleResource(
    name = "Vietnamese POS Tagger",
    comment = "(A short one-line description of this PR, suitable for a tooltip in the GUI)")
public class GateVietnamesePosTagger extends AbstractLanguageAnalyser {

//  private static final Logger log = Logger.getLogger(GateVietnamesePosTagger.class);

//  /**
//   * Annotation set name from which this PR will take its input annotations.
//   */
//  private String inputASName;
//
//  /**
//   * Annotation set name into which this PR will create new annotations.
//   */
//  private String outputASName;
//
//
//  public String getInputASName() {
//    return inputASName;
//  }
//
//  @Optional
//  @RunTime
//  @CreoleParameter(comment = "The annotation set used for input annotations")
//  public void setInputASName(String inputASName) {
//    this.inputASName = inputASName;
//  }
//
//  public String getOutputASName() {
//    return outputASName;
//  }
//
//  @Optional
//  @RunTime
//  @CreoleParameter(comment = "The annotation set used for output annotations")
//  public void setOutputASName(String outputASName) {
//    this.outputASName = outputASName;
//  }

  /**
   * Initialize this Vietnamese POS Tagger.
   * @return this resource.
   * @throws ResourceInstantiationException if an error occurs during init.
   */
  public Resource init() throws ResourceInstantiationException {
//    log.debug("Vietnamese POS Tagger is initializing");

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

//    Document doc = getDocument();
//    if(doc != null) {
//      AnnotationSet inputAS = doc.getAnnotations(inputASName);
//      AnnotationSet outputAS = doc.getAnnotations(outputASName);
//
//      // do your processing here - take annotations from the inputAS and put
//      // results into the outputAS
//    }




    // get the document
    Document doc = getDocument();
    if (doc != null)
    {
      // get all sentences as segmented by ANNIE Sentence Splitter
      AnnotationSet sentenceAnnoSet = doc.getAnnotations(ANNIEConstants.SENTENCE_ANNOTATION_TYPE);
      for (Annotation sentenceAnno : sentenceAnnoSet) {
        try {
          String sentence = doc.getContent().getContent(sentenceAnno.getStartNode().getOffset(), sentenceAnno.getEndNode().getOffset()).toString();
          RDRsegmenter segmenter = new RDRsegmenter();

          String segmentedSentence = segmenter.segmentRawString(sentence);
          System.out.println(segmentedSentence);
        } catch (InvalidOffsetException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}

