package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.events.GraphSpaceChangeEvent;
import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.*;
import edu.ucdenver.ccp.knowtator.model.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotationGraphSpaceChooser extends Chooser<GraphSpace>
    implements AnnotationSelectionListener,
        GraphSpaceSelectionListener,
        TextSourceSelectionListener,
        GraphSpaceCollectionListener,
		GraphSpaceListener {

  private KnowtatorView view;

  public AnnotationGraphSpaceChooser(KnowtatorView view) {
    super(view);
    this.view = view;
  }

  @Override
  public void added(GraphSpace graphSpace) {
    if (graphSpace.containsAnnotation(
        view.getController().getSelectionManager().getSelectedAnnotation())) {
      addItem(graphSpace);
    }
  }

  @Override
  public void selectedAnnotationChanged(AnnotationChangeEvent e) {
    setModel(
        new DefaultComboBoxModel<>(
            view.getController()
                .getSelectionManager()
                .getActiveTextSource()
                .getAnnotationManager()
                .getGraphSpaceCollection()
                .stream()
                .filter(graphSpace -> graphSpace.containsAnnotation(e.getNew()))
                .toArray(GraphSpace[]::new)));
  }

  @Override
  public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
    e.getOld().removeGraphSpaceListener(this);
  	if (e.getNew()
        .containsAnnotation(view.getController().getSelectionManager().getSelectedAnnotation())) {
      setSelectedItem(e.getNew());
      e.getNew().addGraphSpaceListener(this);
    }

  }

  @Override
  public void activeTextSourceChanged(TextSourceChangeEvent e) {

    if (e.getOld() != null) {
      e.getOld().getAnnotationManager().getGraphSpaceCollection().removeListener(this);
    }
    e.getNew().getAnnotationManager().getGraphSpaceCollection().addListener(this);
  }

	@Override
	public void graphTextChanged(TextSource textSource, int start, int end) {

	}

	@Override
	public void annotationNodeAdded(GraphSpace graphSpace, AnnotationNode node) {
		if (node.getAnnotation().equals(view.getController().getSelectionManager().getSelectedAnnotation())) {
			setModel(
					new DefaultComboBoxModel<>(
							view.getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.getGraphSpaceCollection()
									.stream()
									.filter(graphSpace1 -> graphSpace1.containsAnnotation(view.getController().getSelectionManager().getSelectedAnnotation()))
									.toArray(GraphSpace[]::new)));
		}
	}

	@Override
	public void annotationNodeRemoved(GraphSpace graphSpace, AnnotationNode node) {
		if (node.getAnnotation().equals(view.getController().getSelectionManager().getSelectedAnnotation())) {
			setModel(
					new DefaultComboBoxModel<>(
							view.getController()
									.getSelectionManager()
									.getActiveTextSource()
									.getAnnotationManager()
									.getGraphSpaceCollection()
									.stream()
									.filter(graphSpace1 -> graphSpace1.containsAnnotation(view.getController().getSelectionManager().getSelectedAnnotation()))
									.toArray(GraphSpace[]::new)));
		}
	}
}
