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

package edu.ucdenver.ccp.knowtator.view;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Settings that should be consistent across the Knowtator view. */
public class KnowtatorDefaultSettings {
  /** The constant FONT. */
  public static final Font FONT = new Font("Verdana", Font.PLAIN, 10);

  /** The constant COLORS. */
  public static final List<Color> COLORS =
      Collections.unmodifiableList(
          Arrays.asList(
              new Color(0, 255, 255),
              new Color(255, 255, 0),
              new Color(255, 102, 102),
              new Color(50, 205, 50),
              new Color(238, 130, 238),
              new Color(255, 0, 0),
              new Color(95, 158, 160),
              new Color(222, 184, 135),
              new Color(255, 165, 0),
              new Color(127, 255, 0),
              new Color(210, 105, 30),
              new Color(0, 191, 255)));
}
