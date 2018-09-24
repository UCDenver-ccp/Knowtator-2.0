package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.RelationAnnotation;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.Arrays;

public class RelationSelectionManager {
	private OWLObjectProperty selectedOWLObjectProperty;
	private String selectedRelationQuantifier;
	private boolean selectedNegation;
	private String selectedRelationQuantifierValue;
	private KnowtatorController controller;
	private GraphSpace graphSpace;


	public RelationSelectionManager(KnowtatorController controller, GraphSpace graphSpace) {
		this.controller = controller;
		this.graphSpace = graphSpace;
	}

	public boolean isSelectedNegation() {
		return selectedNegation;
	}

	public void setSelectedRelationQuantifierValue(String selectedRelationQuantifierValue) {
		this.selectedRelationQuantifierValue = selectedRelationQuantifierValue;
		Arrays.stream(graphSpace.getSelectionCells()).forEach(cell -> {
			if (cell instanceof RelationAnnotation) {
				((RelationAnnotation) cell).setQuantifierValue(selectedRelationQuantifierValue);
			}
		});

	}

	public void setSelectedOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
		this.selectedOWLObjectProperty = owlObjectProperty;
		Arrays.stream(graphSpace.getSelectionCells()).forEach(cell -> {
			if (cell instanceof RelationAnnotation) {
				((RelationAnnotation) cell).setProperty(owlObjectProperty);
			}
		});
		controller.getOWLManager().setSelectedOWLEntity(owlObjectProperty);

	}


	public OWLObjectProperty getSelectedOWLObjectProperty() {
		return selectedOWLObjectProperty;
	}

	public String getSelectedRelationQuantifier() {
		return selectedRelationQuantifier;
	}

	public void setSelectedPropertyQuantifer(String relationQuantifier) {
		selectedRelationQuantifier = relationQuantifier;
		Arrays.stream(graphSpace.getSelectionCells()).forEach(cell -> {
			if (cell instanceof RelationAnnotation) {
				((RelationAnnotation) cell).setQuantifier(selectedRelationQuantifier);
			}
		});
		graphSpace.getTextSource().getGraphSpaceCollection().refresh();
	}

	public String getSelectedRelationQuantifierValue() {
		return selectedRelationQuantifierValue;
	}

	public void setNegatation(boolean selectedNegation) {
		this.selectedNegation = selectedNegation;
		Arrays.stream(graphSpace.getSelectionCells()).forEach(cell -> {
			if (cell instanceof RelationAnnotation) {
				((RelationAnnotation) cell).setNegation(selectedNegation);
			}
		});
	}

	public void dispose() {
	}
}
