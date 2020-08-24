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

package edu.ucdenver.ccp.knowtator.view.list;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import java.util.Optional;

/** The type Profile list. */
public class ProfileList extends KnowtatorList<Profile> {

  /**
   * Instantiates a new Knowtator list.
   *
   * @param view The view
   */
  public ProfileList(KnowtatorView view) {
    super(view);
  }

  @Override
  public void reactToClick() {
    // TODO: Something is going wrong when double clicking a profile with no colors.
    // java.lang.ArrayIndexOutOfBoundsException: null
    Optional<Profile> profileOptional = Optional.ofNullable(getSelectedValue());
    profileOptional.ifPresent(
        profile -> view.getModel().ifPresent(model -> model.getProfiles().setSelection(profile)));
  }

  @Override
  protected void addElementsFromModel() {
    view.getModel()
        .ifPresent(
            model ->
                model.getProfiles().forEach(profile -> getDefaultListModel().addElement(profile)));
  }

  @Override
  protected Optional<Profile> getSelectedFromModel() {
    return view.getModel().flatMap(BaseModel::getSelectedProfile);
  }

  // I am overriding here because the base method adds this as a collection listener to its
  // collection,
  // but that generates a concurrent modification exception during "added" events
  //    @Override
  //    protected void setCollection(KnowtatorCollection<Profile> collection) {
  //        dispose();
  //        this.collection = collection;
  //        collection.forEach(k -> ((DefaultListModel<Profile>)
  // getKnowtatorModel()).addElement(k));
  //    }

}
