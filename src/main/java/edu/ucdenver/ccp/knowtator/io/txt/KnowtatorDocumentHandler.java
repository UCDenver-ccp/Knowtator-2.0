package edu.ucdenver.ccp.knowtator.io.txt;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return stream.collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
