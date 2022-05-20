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
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import java.util.ArrayList;
import java.util.List;

/** The type Graph space collection. */
public class GraphSpaceCollection extends KnowtatorCollection<GraphSpace> {

  /**
   * Instantiates a new Graph space collection.
   *
   * @param model the model
   */
  public GraphSpaceCollection(KnowtatorModel model) {
    super(model);
  }

  @Override
  public void selectOnly(GraphSpace graphSpace) {
    if (graphSpace != null) {
      super.selectOnly(graphSpace);
    }
  }

  /**
   * Verify id string.
   *
   * @param id the id
   * @param idPrefix the id prefix
   * @return the string
   */
  public String verifyID(String id, String idPrefix) {
    if (id == null) {
      id = String.format("%s_0", idPrefix);
    }
    List<String> ids = new ArrayList<>();
    for (GraphSpace graphSpace : this) {
      for (Object cell : graphSpace.getChildVertices(graphSpace.getDefaultParent())) {
        ids.add(((ModelObject) cell).getId());
      }
    }

    while (ids.contains(id)) {
      int vertexIdIndex = Integer.parseInt(id.split(String.format("%s_", idPrefix))[1]);
      id = String.format("%s_%d", idPrefix, ++vertexIdIndex);
    }

    return id;
  }
}
