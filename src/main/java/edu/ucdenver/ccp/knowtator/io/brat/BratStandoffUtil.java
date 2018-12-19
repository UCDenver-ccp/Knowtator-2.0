/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.io.brat;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BratStandoffUtil implements BasicIOUtil<BratStandoffIO> {
    private static final Logger log = Logger.getLogger(BratStandoffUtil.class);

    private static Map<Character, List<String[]>> collectAnnotations(Stream<String> standoffStream) {
        Map<Character, List<String[]>> annotationCollector = createAnnotationCollector();

        standoffStream
                .filter(line -> !line.trim().isEmpty())
                .map(line -> line.split(StandoffTags.columnDelimiter))
                .forEach(entries -> annotationCollector.get(entries[0].charAt(0)).add(entries));

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
            if (textSourceManager instanceof TextSourceCollection) {
                Stream<String> standoffStream = Files.lines(Paths.get(file.toURI()));

                Map<Character, List<String[]>> annotationMap = collectAnnotations(standoffStream);

                annotationMap
                        .get(StandoffTags.DOCID)
                        .add(new String[]{FilenameUtils.getBaseName(file.getName())});

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

        File outputFile = new File(file.getParentFile(), String.format("%s.ann", file.getName()));
        writeToOutputFile(savable, outputFile, visualConfig, annotationConfig);

        writeVisualConfiguration(file, visualConfig);
        writeAnnotationsConfiguration(file, annotationConfig);
    }

    private void writeAnnotationsConfiguration(File file, Map<String, Map<String, String>> annotationConfig) {
        try {
            BufferedWriter annotationConfigWriter = new BufferedWriter(new FileWriter(String.format("%s%sconcept.conf", file.getAbsolutePath(), File.separator)));

            annotationConfig.forEach((key, map) -> {
                        try {
                            annotationConfigWriter.append(String.format("[%s]\n", key));
                            if (key.equals(StandoffTags.annotationsEntities)) {
                                map.forEach((classID, value) -> {
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
                            new FileWriter(String.format("%s%svisual.conf", file.getAbsolutePath(), File.separator)));
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

    private void writeToOutputFile(BratStandoffIO textSource, File file, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) {
        try {
            log.info(String.format("Writing to %s", file.getAbsolutePath()));
            BufferedWriter bw =
                    new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            textSource.writeToBratStandoff(bw, annotationConfig, visualConfig);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
