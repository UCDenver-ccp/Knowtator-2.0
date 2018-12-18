/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class Loader extends SwingWorker implements ModelListener {

	private final KnowtatorView view;
	private final File file;
	private float maxVal;

	public Loader(KnowtatorView view, File file) {
		this.view = view;
		this.file = file;
		File annotationsDir = new File(file.getParentFile(), "Annotations");
		File profilesDir = new File(file.getParentFile(), "Profiles");
		maxVal = 1;
		if (annotationsDir.isDirectory() && profilesDir.isDirectory()) {
			maxVal = Objects.requireNonNull(annotationsDir.listFiles()).length + Objects.requireNonNull(profilesDir.listFiles()).length;
		}
	}

	@Override
	protected Object doInBackground() throws Exception {
		view.loadProject(file.getParentFile(), this);
		return null;
	}

	@Override
	public void filterChangedEvent() {
	}

	@Override
	public void modelChangeEvent(ChangeEvent<ModelObject> event) {
		event.getNew()
				.filter(modelObject -> modelObject instanceof TextSource || modelObject instanceof Profile)
				.ifPresent(textSource -> view.getModel().ifPresent(model -> {
					Float x = (model.getNumberOfTextSources() + model.getNumberOfProfiles()) / maxVal * 100;
					setProgress(Math.min(100, x.intValue()));
				}));
	}

	@Override
	public void colorChangedEvent() {
	}
}
