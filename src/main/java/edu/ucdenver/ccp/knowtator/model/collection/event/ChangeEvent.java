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

package edu.ucdenver.ccp.knowtator.model.collection.event;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Change event.
 *
 * @param <O> the type parameter
 */
public class ChangeEvent<O> {

  private final List<O> newObject;

  private ChangeEvent() {
    this.newObject = new ArrayList<>();
  }

  /**
   * Instantiates a new Change event.
   *
   * @param newObject the new object
   */
  public ChangeEvent(List<O> newObject) {
    this();
    this.newObject.addAll(newObject);
  }

  public ChangeEvent(O newObject) {
    this();
    this.newObject.add(newObject);
  }

  /**
   * Gets new.
   *
   * @return the new
   */
  public List<O> getNew() {
    return newObject;
  }
}
