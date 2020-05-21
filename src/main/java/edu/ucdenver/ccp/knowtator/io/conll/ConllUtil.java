/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.io.conll;

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.Quantifier;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

public class ConllUtil {
  private static final Logger log = Logger.getLogger(KnowtatorModel.class);

  void readToStructureAnnotations(KnowtatorModel model, File file) {

    model
        .getTextSources()
        .get(file.getName().replace(".tree.conllu", "").replace(".conll", ""))
        .ifPresent(
            textSource -> {
              log.info(String.format("Reading structures from %s", file.getName()));
              textSource.getKnowtatorModel().removeModelListener(textSource);
              List<Map<ConllUField, String>> sentence = new ArrayList<>();
              List<List<Map<ConllUField, String>>> sentences = new ArrayList<>();
              try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while (true) {
                  line = br.readLine();
                  if (line == null) {
                    break;
                  } else if (line.equals("") && sentence.size() > 0) {
                    sentences.add(sentence);
                    sentence = new ArrayList<>();
                  } else {
                    String[] fields = line.split("\t");
                    Map<ConllUField, String> fieldMap = new HashMap<>();
                    for (int i = 0; i < fields.length; i++) {
                      fieldMap.put(ConllUField.values()[i], fields[i]);
                    }
                    sentence.add(fieldMap);
                  }
                }
              } catch (IOException e) {
                e.printStackTrace();
              }

              final int[] start = {0};
              java.util.function.Function<String, int[]> findEnd =
                  (text) -> {
                    if (text.length() == 2) {
                      text = text.replace("``", "\"").replace("''", "\"");
                    } else if (text.length() == 5) {
                      text = text.replace("-LRB-", "[").replace("-RRB-", "]");
                    } else if (text.length() == 3) {
                      text = text.replace("...", "…");
                    } else if (text.length() == 1) {
                      text =
                          text.replace("(", "[")
                              .replace(")", "]")
                              .replace("{", "[")
                              .replace("}", "]");
                    }
                    int start1;
                    int end;
                    try {
                      for (int i = 0; i < 9; i++) {
                        start1 = start[0] - i;
                        end = start[0] + text.length() - i;
                        end = checkRealText(textSource, start, text, start1, end);
                        if (-1 < end) {
                          return new int[] {start1, end};
                        }
                        start1 = start[0] + i;
                        end = start[0] + text.length() + i;
                        end = checkRealText(textSource, start, text, start1, end);
                        if (-1 < end) {
                          return new int[] {start1, end};
                        }
                      }

                      throw new Exception(
                          String.format(
                              "%s not found near %d %d", text, start[0], start[0] + text.length()));
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                    return null;
                  };

              for (int i1 = 0; i1 < sentences.size(); i1++) {
                List<Map<ConllUField, String>> sentence1 = sentences.get(i1);
                GraphSpace graphSpace =
                    new GraphSpace(
                        textSource, String.format("%s-Sentence %d", textSource.getId(), i1));
                textSource.add(graphSpace);
                List<AnnotationNode> nodes =
                    sentence1.stream()
                        .map(
                            fields -> {
                              ConceptAnnotation conceptAnnotation =
                                  new ConceptAnnotation(
                                      textSource,
                                      null,
                                      fields.get(ConllUField.DEPREL),
                                      model.getDefaultProfile(),
                                      null,
                                      "",
                                      new HashSet<>(Collections.singletonList("Structures")));
                              int[] range = findEnd.apply(fields.get(ConllUField.FORM));

                              Span span = new Span(conceptAnnotation, null, Objects.requireNonNull(range)[0], range[1]);
                              conceptAnnotation.add(span);
                              return conceptAnnotation;
                            })
                        .peek(
                            conceptAnnotation ->
                                textSource.getConceptAnnotations().add(conceptAnnotation))
                        .map(
                            conceptAnnotation ->
                                new AnnotationNode(null, conceptAnnotation, 0, 0, graphSpace))
                        .peek(graphSpace::addCellToGraph)
                        .collect(Collectors.toList());
                for (int i = 0; i < sentence1.size(); i++) {
                  AnnotationNode source = nodes.get(i);
                  int targetIdx = Integer.parseInt(sentence1.get(i).get(ConllUField.HEAD)) - 1;
                  if (targetIdx >= 0) {
                    AnnotationNode target = nodes.get(targetIdx);
                    graphSpace.addTriple(
                        source,
                        target,
                        null,
                        model.getDefaultProfile(),
                        "depends_on",
                        Quantifier.some,
                        "",
                        false,
                        "");
                  } else if (!(sentence1.get(i).get(ConllUField.DEPREL).equals("root")
                      || sentence1.get(i).get(ConllUField.DEPREL).equals("ROOT"))) {
                    try {
                      throw new Exception("excluding root");
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
                }
              }
              textSource.getKnowtatorModel().addModelListener(textSource);
            });
  }

  private int checkRealText(TextSource textSource, int[] start, String text, int start1, int end) {
    String realText;
    realText = textSource.getContent();

    if (end <= realText.length()) {
      realText = realText.substring(start1, end);
      end += realText.split("·").length - 1;
      realText =
          textSource
              .getContent()
              .substring(start1, end)
              .replace("(", "[")
              .replace(")", "]")
              .replace("}", "]")
              .replace("{", "[")
              .replace("″", "\"");

      if (realText.replace("·", "").equals(text)) {
        start[0] = end + 1;
        return end;
      }
    }
    return -1;
  }
}
