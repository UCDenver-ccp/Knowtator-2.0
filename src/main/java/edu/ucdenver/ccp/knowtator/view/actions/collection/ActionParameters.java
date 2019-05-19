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

package edu.ucdenver.ccp.knowtator.view.actions.collection;

/** The type Action parameters. */
public class ActionParameters {
  private final CollectionActionType actionType;
  private final KnowtatorCollectionType collectionType;

  /**
   * Parameters for actions.
   *
   * @param actionType The action type
   * @param collectionType The collection type
   */
  public ActionParameters(CollectionActionType actionType, KnowtatorCollectionType collectionType) {

    this.actionType = actionType;
    this.collectionType = collectionType;
  }

  /**
   * Gets action type.
   *
   * @return the action type
   */
  CollectionActionType getActionType() {
    return actionType;
  }

  /**
   * Gets collection type.
   *
   * @return the collection type
   */
  KnowtatorCollectionType getCollectionType() {
    return collectionType;
  }
}
