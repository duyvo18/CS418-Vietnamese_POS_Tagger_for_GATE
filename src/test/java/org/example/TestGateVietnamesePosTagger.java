package org.example;

import gate.Document;
import gate.Factory;
import gate.LanguageAnalyser;
import gate.util.GateException;
import gate.test.GATEPluginTests;
import org.junit.Test;


/**
 * Using this class automatically prepares GATE and the plugin for testing.
 * 
 * This class automatically initializes GATE and loads the plugin. 
 * Any method in this class with the "@Test" annotation will then get
 * run with the plugin already properly loaded.
 * 
 */
public class TestGateVietnamesePosTagger extends GATEPluginTests {

  @Test
  public void testSomething() throws GateException {
    LanguageAnalyser pr = (LanguageAnalyser)Factory.createResource("org.example.GateVietnamesePosTagger");
    try {
      Document doc = Factory.newDocument("https://vi.wikipedia.org/wiki/Ti%E1%BA%BFng_Vi%E1%BB%87t");
      pr.setDocument(doc);
      pr.execute();
    } finally {
      Factory.deleteResource(pr);
    }
  }
}
