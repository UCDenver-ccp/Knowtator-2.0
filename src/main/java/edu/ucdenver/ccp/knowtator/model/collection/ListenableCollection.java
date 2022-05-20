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
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * The type Listenable collection.
 *
 * @param <K> the type parameter
 * @param <C> the type parameter
 */
public abstract class ListenableCollection<K extends ModelObject, C extends Collection<K>>
    implements Iterable<K> {
  /** The Model. */
  final BaseModel model;
  /** The Collection. */
  final C collection;

  /**
   * Instantiates a new Listenable collection.
   *
   * @param model the model
   * @param collection the collection
   */
  ListenableCollection(BaseModel model, C collection) {
    this.model = model;
    this.collection = collection;
  }

  /**
   * Add.
   *
   * @param objectToAdd the object to add
   */
  void add(K objectToAdd) {
    collection.add(objectToAdd);

    if (model != null) {
      model.fireModelEvent(new ChangeEvent<>(objectToAdd));
    }
  }

  /**
   * Remove.
   *
   * @param objectToRemove the object to remove
   */
  void remove(K objectToRemove) {
    collection.remove(objectToRemove);
    if (model != null) {
      model.fireModelEvent(new ChangeEvent<>(objectToRemove));
    }
  }

  /**
   * Get optional.
   *
   * @param id the id
   * @return the optional
   */
  public Optional<K> get(String id) {
    for (K obj : collection) {
      if (obj.getId().equals(id)) {
        return Optional.of(obj);
      }
    }
    return Optional.empty();
  }

  /**
   * Contains boolean.
   *
   * @param objToFind the obj to find
   * @return the boolean
   */
  @SuppressWarnings("unused")
  public boolean contains(K objToFind) {
    return containsID(objToFind.getId());
  }

  /**
   * Contains id boolean.
   *
   * @param idToFind the id to find
   * @return the boolean
   */
  public boolean containsID(String idToFind) {
    for (K id : collection) {
      if (id.getId().equals(idToFind)) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Nonnull
  public Iterator<K> iterator() {
    return collection.iterator();
  }

  /**
   * Stream stream.
   *
   * @return the stream
   */
  public Stream<K> stream() {
    return collection.stream();
  }

  /**
   * Size int.
   *
   * @return the int
   */
  public int size() {
    return collection.size();
  }

  /** Dispose. */
  public void dispose() {
    forEach(ModelObject::dispose);
    collection.clear();
  }

  /**
   * Gets collection.
   *
   * @return the collection
   */
  public C getCollection() {
    return collection;
  }
}
