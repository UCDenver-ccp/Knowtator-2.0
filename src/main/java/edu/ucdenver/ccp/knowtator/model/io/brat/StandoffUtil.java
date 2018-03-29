/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.io.brat;

import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.BasicIOUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StandoffUtil implements BasicIOUtil {

    @Override
    public void read(Savable textSourceManager, File file) {
        try {
            Stream<String> standoffStream = Files.lines(Paths.get(file.toURI()));

            Map<Character, List<String[]>> annotationMap = collectAnnotations(standoffStream);

            annotationMap.get(StandoffTags.DOCID).add(new String[]{FilenameUtils.getBaseName(file.getName())});

            textSourceManager.readFromBratStandoff(annotationMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Character, List<String[]>> collectAnnotations(Stream<String> standoffStream) {
        Map<Character, List<String[]>> annotationCollector = createAnnotationCollector();

        standoffStream.forEach(line -> {
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
        IntStream.range(0, StandoffTags.tagList.length).mapToObj(i -> StandoffTags.tagList[i])
                .forEach(tag -> annotationCollector.put(tag, new ArrayList<>()));

        return annotationCollector;
    }

    @Override
    public void write(Savable savable, File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            savable.writeToBratStandoff(bw);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
