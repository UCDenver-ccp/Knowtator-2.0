package edu.ucdenver.ccp.knowtator.io.uima;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

/** The type Knowtator to uima annotator. */
public class KnowtatorToUIMAAnnotator extends JCasAnnotator_ImplBase {
  /**
   * Process.
   *
   * @param aJCas the a j cas
   * @throws AnalysisEngineProcessException the analysis engine process exception
   */
  @SuppressWarnings("RedundantThrows")
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {}
}
