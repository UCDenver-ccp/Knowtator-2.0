package edu.ucdenver.ccp.knowtator;

import org.apache.log4j.Logger;

import java.io.*;

public class KnowtatorDocumentHandler {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public static InputStream getFileInputStream(String fileName, Boolean fromResources) throws FileNotFoundException {
        if (fromResources) {
            return KnowtatorManager.class.getResourceAsStream(fileName);
        } else {
            try {
                return new FileInputStream(fileName);
            } finally {
                log.warn(String.format("Could not open %s", fileName));
            }
        }
    }

    public static Reader getReader(String fileName, Boolean fromResources) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(getFileInputStream(fileName, fromResources)));
    }

}
