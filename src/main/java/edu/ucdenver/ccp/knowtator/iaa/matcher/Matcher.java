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

package edu.ucdenver.ccp.knowtator.iaa.matcher;

import edu.ucdenver.ccp.knowtator.iaa.Iaa;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import java.util.Set;

/** The interface Matcher. */
public interface Matcher {

  /**
   * Match concept annotation.
   *
   * @param conceptAnnotation the concept that is to be matched
   * @param compareSetName the set of annotations that we will look for a match in
   * @param excludeConceptAnnotations a set of annotations that cannot be the return value
   * @param iaa an instance of Iaa from which to getAnnotation information about annotations
   * @param matchResult instantiate a new MatchType and pass it to the method. The method must set
   *     the result to one of the four result values given in MatchResult.
   * @return the concept that was matched. If none exists then return null.
   */
  ConceptAnnotation match(
      ConceptAnnotation conceptAnnotation,
      String compareSetName,
      Set<ConceptAnnotation> excludeConceptAnnotations,
      Iaa iaa,
      MatchResult matchResult);

  /**
   * Gets name.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets description.
   *
   * @return the description
   */
  String getDescription();

  /**
   * Returns trivials boolean.
   *
   * @return the boolean
   */
  @SuppressWarnings("SameReturnValue")
  boolean returnsTrivials();
}
