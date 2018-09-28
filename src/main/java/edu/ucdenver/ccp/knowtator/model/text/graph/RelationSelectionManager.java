package edu.ucdenver.ccp.knowtator.model.text.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import java.util.Arrays;

public class RelationSelectionManager {
    private String selectedRelationQuantifier;
    private boolean selectedNegation;
    private String selectedRelationQuantifierValue;
    private KnowtatorController controller;
    private GraphSpace graphSpace;
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(RelationSelectionManager.class);


    RelationSelectionManager(KnowtatorController controller, GraphSpace graphSpace) {
        this.controller = controller;
        this.graphSpace = graphSpace;
    }

    public boolean isSelectedNegation() {
        return selectedNegation;
    }

    public void setSelectedOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        controller.getOWLModel().setSelectedOWLEntity(owlObjectProperty);
    }

    public String getSelectedRelationQuantifier() {
        return selectedRelationQuantifier;
    }

    void setSelectedPropertyQuantifer(String relationQuantifier) {
        selectedRelationQuantifier = relationQuantifier;
        Arrays.stream(graphSpace.getSelectionCells()).forEach(cell -> {
            if (cell instanceof RelationAnnotation) {
                ((RelationAnnotation) cell).setQuantifier(selectedRelationQuantifier);
            }
        });
        graphSpace.getTextSource().getGraphSpaceCollection().refresh();
    }

    String getSelectedRelationQuantifierValue() {
        return selectedRelationQuantifierValue;
    }

    void setSelectedRelationQuantifierValue(String selectedRelationQuantifierValue) {
        this.selectedRelationQuantifierValue = selectedRelationQuantifierValue;
        Arrays.stream(graphSpace.getSelectionCells()).forEach(cell -> {
            if (cell instanceof RelationAnnotation) {
                ((RelationAnnotation) cell).setQuantifierValue(selectedRelationQuantifierValue);
            }
        });

    }

    void setNegatation(boolean selectedNegation) {
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
