package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.annotation.TextSourceManager;
import edu.ucdenver.ccp.knowtator.configuration.ConfigProperties;
import edu.ucdenver.ccp.knowtator.io.xml.XmlUtil;
import org.apache.log4j.Logger;


/**
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorManager {
    private static final Logger log = Logger.getLogger(KnowtatorManager.class);

    private XmlUtil xmlUtil;
    private ConfigProperties configProperties;
    private TextSourceManager textSourceManager;
    private ProfileManager profileManager;

    public KnowtatorManager() {
        super();
        initManagers();
        loadConfig();

        log.warn("Knowtator manager initialized");
    }

    private void initManagers() {
        textSourceManager = new TextSourceManager(this);
        profileManager = new ProfileManager();  //manipulates profiles and colors
        xmlUtil = new XmlUtil(this);  //reads and writes to XML
    }

    private void loadConfig() {
        configProperties = new ConfigProperties();
        xmlUtil.read("config.xml", true);
    }


    public ProfileManager getProfileManager() {
        return profileManager;
    }
    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }
    public ConfigProperties getConfigProperties() {
        return configProperties;
    }
    public TextSourceManager getTextSourceManager() {
        return textSourceManager;
    }

    // I want to keep this method in case I want to autoLoad ontologies eventually
//    public void loadOntologyFromLocation(String classID) {
//        List<String> ontologies = owlModelManager.getActiveOntologies().stream().map(ontology -> {
//            OWLOntologyID ontID = ontology.getOntologyID();
//            Optional<IRI> ontIRI = ontID.getOntologyIRI();
//            if(ontIRI.isPresent()) {
//                return ontIRI.get().toURI().toString();
//            } else {
//                return null;
//            }
//        }).collect(Collectors.toList());
//
//        String ontologyLocation = OntologyTranslator.translate(classID);
//        if (!ontologies.contains(ontologyLocation)) {
//            owlModelManager.loadOntologyFromPhysicalURI(URI.create(OntologyTranslator.whichOntologyToUse(ontologyLocation)));
//        }
//
//    }

    public static void main(String[] args) {
        KnowtatorManager manager = new KnowtatorManager();
        manager.simpleTest();
    }

    private void simpleTest() {
        getXmlUtil().read("file/test_project.xml", true);
        getXmlUtil().write(String.format("%stest_project.xml", configProperties.getDefaultSaveLocation()));
    }
}
