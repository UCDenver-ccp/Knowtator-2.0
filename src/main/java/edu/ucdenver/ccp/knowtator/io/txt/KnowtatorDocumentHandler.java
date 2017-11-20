package edu.ucdenver.ccp.knowtator.io.txt;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import java.io.*;

public class KnowtatorDocumentHandler {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    public static InputStream getFileInputStream(String fileName, Boolean fromResources) throws FileNotFoundException {
        if (fromResources) {
            return KnowtatorManager.class.getResourceAsStream("/" + fileName);
        } else {
            return new FileInputStream(fileName);
        }
    }

    private static BufferedReader getReader(String fileName, Boolean fromResources) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(getFileInputStream(fileName, fromResources)));
    }

    public static String read(String fileName, Boolean fromResources) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = getReader(fileName, fromResources);
            while(reader.readLine() != null) {
                stringBuilder.append(reader.readLine());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
