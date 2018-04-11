package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.ProfileManager;
import edu.ucdenver.ccp.knowtator.model.ProjectManager;
import edu.ucdenver.ccp.knowtator.model.SelectionManager;
import edu.ucdenver.ccp.knowtator.model.TextSourceManager;
import edu.ucdenver.ccp.knowtator.model.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

import java.util.prefs.Preferences;

public class KnowtatorController {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KnowtatorController.class);

	private final Preferences prefs = Preferences.userRoot().node("knowtator");
	private ProjectManager projectManager;
	private TextSourceManager textSourceManager;
	private ProfileManager profileManager;
	private SelectionManager selectionManager;
	private OWLAPIDataExtractor owlDataExtractor;

	private KnowtatorView view;

	public KnowtatorController() {
		initManagers();
	}

	/**
	 * @param view The view that spawned this controller
	 */
	public KnowtatorController(KnowtatorView view) {
		this();
		this.view = view;
		if (view.getOWLWorkspace() != null) {
			setUpOWL(view.getOWLWorkspace());
		}
	}

	public static void main(String[] args) {
	}

	private void initManagers() {
		projectManager = new ProjectManager(this); // reads and writes to XML
		selectionManager = new SelectionManager(this);
		textSourceManager = new TextSourceManager(this);
		profileManager = new ProfileManager(this); // manipulates profiles and colors
		owlDataExtractor = new OWLAPIDataExtractor(this);
	}

	private void setUpOWL(OWLWorkspace owlWorkspace) {
		owlDataExtractor.setUpOWL(owlWorkspace);
		owlWorkspace.getOWLModelManager().addOntologyChangeListener(textSourceManager);
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	/**
	 * GETTERS
	 */
	public OWLAPIDataExtractor getOWLAPIDataExtractor() {
		return owlDataExtractor;
	}

	public TextSourceManager getTextSourceManager() {
		return textSourceManager;
	}

	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	public KnowtatorView getView() {
		return view;
	}

	public Preferences getPrefs() {
		return prefs;
	}
}
