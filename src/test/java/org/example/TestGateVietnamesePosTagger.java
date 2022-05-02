package org.example;

import org.junit.Test;

import vn.pipeline.Annotation;
import vn.pipeline.VnCoreNLP;
import vn.pipeline.Word;

import java.io.IOException;

/**
 * Using this class automatically prepares GATE and the plugin for testing.
 * 
 * This class automatically initializes GATE and loads the plugin. 
 * Any method in this class with the "@Test" annotation will then get
 * run with the plugin already properly loaded.
 * 
 */
public class TestGateVietnamesePosTagger {

  @Test
  public void testSomething() {
    try {
      // testing code goes here
      String str = "Tiếng Việt, cũng gọi là tiếng Việt Nam hay Việt ngữ "
              + "là ngôn ngữ của người Việt và là ngôn ngữ chính thức tại Việt Nam. "
              + "Đây là tiếng mẹ đẻ của khoảng 85% dân cư Việt Nam cùng với hơn 4 triệu Việt kiều. "
              + "Tiếng Việt còn là ngôn ngữ thứ hai của các dân tộc thiểu số tại Việt Nam "
              + "và là ngôn ngữ dân tộc thiểu số được công nhận tại Cộng hòa Séc.";

      String[] sentences = str.split("\\. ");

      VnCoreNLP pipeline = new VnCoreNLP(new String[]{"wseg", "pos"});
      for (String sentence : sentences) {
        System.out.println("\n\n" + sentence);

        Annotation vnCoreAnno = new Annotation(sentence);
        pipeline.annotate(vnCoreAnno);

        for (Word word : vnCoreAnno.getWords()) {
          int idx = sentence.indexOf(word.getForm().replace("_", " "));
          System.out.println(word.getForm() + " at " + idx + " - " + (idx + word.getForm().length()));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
