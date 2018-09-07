package edu.ucdenver.ccp.knowtator.io.brat;

import edu.ucdenver.ccp.knowtator.io.BasicIO;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface BratStandoffIO extends Serializable, BasicIO {
    void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationMap, String content);

    void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) throws IOException;
}
