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

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;

/**
 * The type Cyclable collection.
 *
 * @param <K> the type parameter
 */
public abstract class CyclableCollection<K extends ModelObject>
    extends ListenableCollection<K, TreeSet<K>> {

  /**
   * Instantiates a new Cyclable collection.
   *
   * @param model the model
   * @param collection the collection
   */
  CyclableCollection(BaseModel model, TreeSet<K> collection) {
    super(model, collection);
  }

  /**
   * Gets previous.
   *
   * @param current the current
   * @return the previous
   */
  K getPrevious(K current) {

    K previous;
    try {
      previous =
          getCollection().contains(current) ? getCollection().lower(current) : getCollection().floor(current);
    } catch (NullPointerException npe) {
      previous = null;
    }
    if (previous == null) {
      previous = getCollection().last();
    }

    return previous;
  }

  /**
   * Gets next.
   *
   * @param current the current
   * @return the next
   */
  K getNext(K current) {
    K next;
    try {
      next =
          getCollection().contains(current) ? getCollection().higher(current) : getCollection().ceiling(current);
    } catch (NullPointerException npe) {
      next = null;
    }
    if (next == null) {
      next = getCollection().first();
    }

    return next;
  }

  /**
   * First optional.
   *
   * @return the optional
   */
  public Optional<K> first() {
    try {
      return Optional.ofNullable(getCollection().first());
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }
}
