package edu.ucdenver.ccp.knowtator.io.brat;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BratStandoffUtil implements BasicIOUtil<BratStandoffIO> {
  private static final Logger log = Logger.getLogger(BratStandoffUtil.class);

  private static Map<Character, List<String[]>> collectAnnotations(Stream<String> standoffStream) {
    Map<Character, List<String[]>> annotationCollector = createAnnotationCollector();

    standoffStream.forEach(
        line -> {
          if (!line.trim().isEmpty()) {
            String[] entries = line.split(StandoffTags.columnDelimiter);
            char annotationType = entries[0].charAt(0);
            annotationCollector.get(annotationType).add(entries);
          }
        });

    return annotationCollector;
  }

  private static Map<Character, List<String[]>> createAnnotationCollector() {
    Map<Character, List<String[]>> annotationCollector = new HashMap<>();
    IntStream.range(0, StandoffTags.tagList.length)
        .mapToObj(i -> StandoffTags.tagList[i])
        .forEach(tag -> annotationCollector.put(tag, new ArrayList<>()));

    return annotationCollector;
  }

  @Override
  public void read(BratStandoffIO textSourceManager, File file) {
    try {
      if (textSourceManager instanceof TextSourceManager) {
        Stream<String> standoffStream = Files.lines(Paths.get(file.toURI()));

        Map<Character, List<String[]>> annotationMap = collectAnnotations(standoffStream);

        annotationMap
            .get(StandoffTags.DOCID)
            .add(new String[] {FilenameUtils.getBaseName(file.getName())});

        textSourceManager.readFromBratStandoff(file, annotationMap, null);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void write(BratStandoffIO savable, File file) {
    Map<String, Map<String, String>> visualConfig = new HashMap<>();

    visualConfig.put(StandoffTags.visualLabels, new HashMap<>());
    visualConfig.put(StandoffTags.visualDrawing, new HashMap<>());

    Map<String, Map<String, String>> annotationConfig = new HashMap<>();
    annotationConfig.put(StandoffTags.annotationsEvents, new HashMap<>());
    annotationConfig.put(StandoffTags.annotationsRelations, new HashMap<>());
    annotationConfig.put(StandoffTags.annotationsEntities, new HashMap<>());
    annotationConfig.put(StandoffTags.annotationsAttributes, new HashMap<>());

    if (savable instanceof TextSourceManager) {
      ((TextSourceManager) savable)
          .getTextSourceCollection()
          .getCollection()
          .forEach(
              textSource -> {
                File outputFile =
                    new File(file.getAbsolutePath() + File.separator + textSource.getId() + ".ann");
                writeToOutputFile(textSource, outputFile, annotationConfig, visualConfig);
              });

      writeVisualConfiguration(file, visualConfig);
      writeAnnotationsConfiguration(file, annotationConfig);

    } else if (savable instanceof TextSource) {
      writeToOutputFile((TextSource) savable, file, annotationConfig, visualConfig);
    }
  }

  private void writeAnnotationsConfiguration(
      File file, Map<String, Map<String, String>> annotationConfig) {
    try {

      BufferedWriter annotationConfigWriter =
          new BufferedWriter(
              new FileWriter(file.getAbsolutePath() + File.separator + "annotation.conf"));

      annotationConfig.forEach(
          (key, map) -> {
            try {
              annotationConfigWriter.append(String.format("[%s]\n", key));
              if (key.equals(StandoffTags.annotationsEntities)) {
                map.forEach(
                    (classID, value) -> {
                      try {
                        annotationConfigWriter.append(classID).append("\n");
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          });

      annotationConfigWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeVisualConfiguration(File file, Map<String, Map<String, String>> visualConfig) {
    try {
      BufferedWriter visualConfigWriter =
          new BufferedWriter(
              new FileWriter(file.getAbsolutePath() + File.separator + "visual.conf"));
      visualConfig.forEach(
          (key, map) -> {
            try {
              visualConfigWriter.append(String.format("[%s]\n", key));
              if (key.equals(StandoffTags.visualLabels)) {
                map.forEach(
                    (classID, label) -> {
                      try {
                        visualConfigWriter.append(String.format("%s | %s\n", classID, label));
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
              } else if (key.equals(StandoffTags.visualDrawing)) {
                map.forEach(
                    (classID, color) -> {
                      try {
                        visualConfigWriter.append(String.format("%s \t %s\n", classID, color));
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    });
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          });

      visualConfigWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeToOutputFile(
      TextSource textSource,
      File file,
      Map<String, Map<String, String>> annotationConfig,
      Map<String, Map<String, String>> visualConfig) {
    try {
      log.warn("Writing to " + file.getAbsolutePath());
      BufferedWriter bw =
          new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
      textSource.writeToBratStandoff(bw, annotationConfig, visualConfig);
      bw.close();
      File textFileCopy = new File(file.getParentFile(), textSource.getTextFile().getName());
      Files.copy(textSource.getTextFile().toPath(), textFileCopy.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
