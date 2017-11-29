package edu.ucdenver.ccp.knowtator.io.txt;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class KnowtatorDocumentHandler {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public static InputStream getFileInputStream(String fileName, Boolean fromResources) throws NullPointerException {
        if (fromResources) {
            return KnowtatorManager.class.getResourceAsStream("/" + fileName);
        } else {
            try {
                return new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
