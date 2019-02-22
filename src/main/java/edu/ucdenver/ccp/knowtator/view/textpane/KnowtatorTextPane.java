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

package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import org.apache.log4j.Logger;

/** The text pane used for annotating and displaying concept annotations in Knowtator projects. */
public class KnowtatorTextPane extends AnnotatableTextPane
    implements KnowtatorComponent, ModelListener {

  @SuppressWarnings("unused")
  private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

  private final KnowtatorView view;
  private final JCheckBox onlyInAnnotationsCheckBox;
  private final JCheckBox regexCheckBox;
  private final JCheckBox caseSensitiveCheckBox;

  /**
   * Instantiates a new Knowtator text pane.
   *
   * @param view A Knowtator view
   * @param searchTextField A text field to use to search the text pane
   * @param onlyInAnnotationsCheckBox A check box specifying whether to search in annotations or all
   *     text
   * @param regexCheckBox A check box specifying if the search pattern is a regular expression
   * @param caseSensitiveCheckBox A check box specifying if the search should be case sensitive
   */
  public KnowtatorTextPane(
      KnowtatorView view,
      JTextField searchTextField,
      JCheckBox onlyInAnnotationsCheckBox,
      JCheckBox regexCheckBox,
      JCheckBox caseSensitiveCheckBox) {
    super(view, searchTextField);
    this.view = view;
    this.onlyInAnnotationsCheckBox = onlyInAnnotationsCheckBox;
    this.regexCheckBox = regexCheckBox;
    this.caseSensitiveCheckBox = caseSensitiveCheckBox;
    regexCheckBox.addItemListener(e -> makePattern());
    caseSensitiveCheckBox.addItemListener(e -> makePattern());
    onlyInAnnotationsCheckBox.addItemListener(e -> makePattern());
  }

  /**
   * Gets screen shot.
   *
   * @return An image of the text pane
   */
  public BufferedImage getScreenShot() {

    BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    // call the Component's paint method, using
    // the Graphics object of the image.
    paint(image.getGraphics()); // alternately use .printAll(..)
    return image;
  }

  @Override
  protected boolean shouldUpdateSearchTextFieldCondition() {
    return !regexCheckBox.isSelected();
  }

  @Override
  protected boolean keepSearchingCondition(Matcher matcher) {
    return view.getModel()
        .flatMap(BaseModel::getSelectedTextSource)
        .map(
            textSource ->
                (!onlyInAnnotationsCheckBox.isSelected()
                    || !(textSource.getSpans(matcher.start()).size() == 0)))
        .orElse(false);
  }

  @Override
  protected int getPatternFlags() {
    return (regexCheckBox.isSelected() ? 0 : Pattern.LITERAL)
        | (caseSensitiveCheckBox.isSelected() ? 0 : Pattern.CASE_INSENSITIVE);
  }
}
