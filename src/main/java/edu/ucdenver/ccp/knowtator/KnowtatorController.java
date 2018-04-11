package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
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

	private Set<ProjectListener> projectListeners;
	private KnowtatorView view;

	public KnowtatorController() {
		initListeners();
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
		selectionManager = new SelectionManager(this);
		textSourceManager = new TextSourceManager(this);
		profileManager = new ProfileManager(this); // manipulates profiles and colors
		projectManager = new ProjectManager(this); // reads and writes to XML
		owlDataExtractor = new OWLAPIDataExtractor();
	}

	private void initListeners() {
		projectListeners = new HashSet<>();
	}

	public void close(File file) {
		initManagers();
		projectManager.loadProject(file);
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

	/**
	 * ADDERS
	 */
	public void addProjectListener(ProjectListener listener) {
		projectListeners.add(listener);
	}

	/**
	 * EVENTS
	 */
	public void projectLoadedEvent() {
		projectListeners.forEach(ProjectListener::projectLoaded);
	}

	public void propertyChangedEvent(Object value) {
		if (value instanceof OWLProperty) {
			view.owlEntitySelectionChanged((OWLObjectProperty) value);
		} else if (value instanceof String) {
			view.owlEntitySelectionChanged(owlDataExtractor.getOWLObjectPropertyByID((String) value));
		}
	}

	public KnowtatorView getView() {
		return view;
	}

	public Preferences getPrefs() {
		return prefs;
	}
}
