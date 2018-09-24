package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class Chooser<K extends KnowtatorObject> extends JComboBox<K> implements KnowtatorCollectionListener<K> {

	private ActionListener al;
	KnowtatorCollection<K> collection;
	KnowtatorView view;
	private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;

	Chooser(KnowtatorView view) {
		this.view = view;

		al = e -> {
			JComboBox comboBox = (JComboBox) e.getSource();
			if (comboBox.getSelectedItem() != null) {
				this.collection.setSelection(getItemAt(getSelectedIndex()));
			}
		};

		conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
			@Override
			public void updated(ConceptAnnotation updatedItem) {

			}

			@Override
			public void noSelection(ConceptAnnotation previousSelection) {

			}

			@Override
			public void selected(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
				reactToAnnotationChange(previousSelection, currentSelection);
			}

			@Override
			public void added(ConceptAnnotation addedObject) {

			}

			@Override
			public void removed(ConceptAnnotation removedObject) {

			}

			@Override
			public void emptied(ConceptAnnotation object) {

			}

			@Override
			public void firstAdded(ConceptAnnotation object) {

			}
		};

		TextSourceCollectionListener textSourceCollectionListener = new TextSourceCollectionListener() {
			@Override
			public void updated(TextSource updatedItem) {

			}

			@Override
			public void noSelection(TextSource previousSelection) {

			}

			@Override
			public void selected(TextSource previousSelection, TextSource currentSelection) {
				reactToTextSourceChange(previousSelection, currentSelection);
			}

			@Override
			public void added(TextSource addedObject) {

			}

			@Override
			public void removed(TextSource removedObject) {

			}

			@Override
			public void emptied(TextSource object) {

			}

			@Override
			public void firstAdded(TextSource object) {

			}
		};

		view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
	}

	public void setCollection(KnowtatorCollection<K> collection) {
		if (this.collection != null) {
			this.collection.removeCollectionListener(this);
		}

		this.collection = collection;
		this.collection.addCollectionListener(this);

		addActionListener(al);
	}

	public void dispose() {
		removeAllItems();
	}

	@Override
	public void added(K addedObject) {
		addItem(addedObject);
	}

	@Override
	public void removed(K removedObject) {
		removeItem(removedObject);
	}

	@Override
	public void emptied(K object) {
		setEnabled(false);
	}

	@Override
	public void firstAdded(K object) {
		setEnabled(true);
	}

	@Override
	public void noSelection(K previousSelection) {
		setSelectedIndex(0);
	}

	@Override
	public void selected(K previousSelection, K currentSelection) {
		removeActionListener(al);
		setSelectedItem(currentSelection);
		addActionListener(al);
	}

	@Override
	public void updated(K updatedItem) {
		removeActionListener(al);
		setSelectedItem(updatedItem);
		addActionListener(al);
	}

	abstract void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection);
	abstract void reactToAnnotationChange(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection);

}
