package org.example;

import gate.*;
import gate.creole.*;
import gate.creole.metadata.*;

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
     * Annotation set name from which this PR will take its input annotations.
     */
    // Input Annotation Set are ANNIE's "Sentence" Annotations
    // Require running ANNIE's RegEx Sentence Splitter Annotation beforehand
    private String inputASName = ANNIEConstants.SENTENCE_ANNOTATION_TYPE;

//    /**
//     * Annotation set name into which this PR will create new annotations.
//     */
//    private String outputASName;
//
//
//    public String getInputASName() {
//        return inputASName;
//    }
//
//    // TODO: Set inputASName is not runtime
//    @Optional
//    @RunTime
//    @CreoleParameter(comment = "The annotation set used for input annotations")
//    public void setInputASName(String inputASName) {
//        this.inputASName = inputASName;
//    }
//
//    public String getOutputASName() {
//        return outputASName;
//    }
//
//    @Optional
//    @RunTime
//    @CreoleParameter(comment = "The annotation set used for output annotations")
//    public void setOutputASName(String outputASName) {
//        this.outputASName = outputASName;
//    }

    /**
     * Initialize this Vietnamese POS Tagger.
     *
     * @return this resource.
     * @throws ResourceInstantiationException if an error occurs during init.
     */
    public Resource init() throws ResourceInstantiationException {
        log.info("Vietnamese POS Tagger is initializing");

        // your initialization code here

        return this;
    }

    /**
     * Execute this Vietnamese POS Tagger over the current document.
     *
     * @throws ExecutionException if an error occurs during processing.
     */
    public void execute() throws ExecutionException {
        // check the interrupt flag before we start - in a long-running PR it is
        // good practice to check this flag at appropriate key points in the
        // execution, to allow the user to interrupt processing if it takes too
        // long.
        if (isInterrupted()) {
            throw new ExecutionInterruptedException("Execution of Vietnamese POS Tagger has been interrupted!");
        }
        interrupted = false;

        Corpus corpus = getCorpus();

        for (Document doc : corpus) {
            AnnotationSet annoSet = doc.getAnnotations();
            AnnotationSet sentenceAnnoSet = annoSet.get(inputASName);

            for (gate.Annotation sentenceAnno : sentenceAnnoSet) {
                try {
                    // Get raw str from "Sentence" annotation
                    String rawSentence = doc.getContent().getContent(
                            sentenceAnno.getStartNode().getOffset(),
                            sentenceAnno.getEndNode().getOffset()
                    ).toString();

                    // Apply VnCoreNLP Pipeline for Word Segmentation and POS Tagging
                    VnCoreNLP pipeline = new VnCoreNLP(new String[]{"wseg", "pos"});
                    vn.pipeline.Annotation anno = new Annotation(rawSentence);
                    pipeline.annotate(anno);

                    Long sentenceOffsetInDoc = sentenceAnno.getStartNode().getOffset();
                    int wordOffsetInSentence = 0;

                    // Adding custom Annotation according to VnCoreNLP POS Tags
                    for (Word tokenizedWord : anno.getWords()) {
                        // tmpOffset to remove trailing spaces etc.
                        int tmpOffset = rawSentence.indexOf(
                                tokenizedWord.getForm().replace('_', ' ')
                        );

                        if (tmpOffset == -1) {
                            log.info("\n> WARNING: Cannot find word in sentence:");
                            log.info(String.format("\"%s\"", tokenizedWord.getForm()));
                            log.info(String.format("\"%s\"", rawSentence));

                            continue;
                        }

                        // Add custom POS Tag to Document's Annotation Set
                        annoSet.add(
                                sentenceOffsetInDoc + wordOffsetInSentence + tmpOffset,
                                sentenceOffsetInDoc + wordOffsetInSentence + tmpOffset + tokenizedWord.getForm().length(),
                                tokenizedWord.getPosTag(),
                                Factory.newFeatureMap()
                        );

                        wordOffsetInSentence += tmpOffset + tokenizedWord.getForm().length();

                        // Cutting sentence to limit repeating words
                        rawSentence = rawSentence.substring(tmpOffset + tokenizedWord.getForm().length());
                    }
                } catch (InvalidOffsetException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}

