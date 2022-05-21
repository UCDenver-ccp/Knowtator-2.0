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

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import java.util.Optional;
import java.util.Set;

/** The type Profile collection. */
public class ProfileCollection extends KnowtatorCollection<Profile> {

  private final Profile defaultProfile;

  /**
   * Instantiates a new Profile collection.
   *
   * @param model the model
   */
  public ProfileCollection(KnowtatorModel model) {
    super(model);
    defaultProfile = new Profile(model, "Default");
    add(defaultProfile);
  }

  /**
   * Gets default profile.
   *
   * @return the default profile
   */
  public Profile getDefaultProfile() {
    return defaultProfile;
  }

  @Override
  public Optional<Profile> getOnlySelected() {
    // Profile should never be null
    if (!super.getOnlySelected().isPresent()) {
      selectOnly(defaultProfile);
    }
    return super.getOnlySelected();
  }

  @Override
  public void add(Profile profile) {
    if (!get(profile.getId()).isPresent()) {
      super.add(profile);
    }
  }

  @Override
  public void remove(Profile profile) {
    if (profile.equals(defaultProfile)) {
      return;
    }
    super.remove(profile);
    selectNext();
  }

  public void verifyHighlighters(Set<String> owlClasses) {
    forEach(profile -> profile.verifyHighLighters(owlClasses));
  }
}
