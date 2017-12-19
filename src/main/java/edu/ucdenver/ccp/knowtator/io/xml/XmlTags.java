package edu.ucdenver.ccp.knowtator.io.xml;

@SuppressWarnings("unused")
class XmlTags {

    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    // ***************NEW Tags*************
    // Start of every project
    static final String KNOWTATOR_PROJECT = "knowtator-project";

    // For each document in the project
    static final String DOCUMENT = "document";
    static final String DOCUMENT_ID = "textSource";  // also in OLD

    // Document text
    static final String TEXT = "text";

    // Meta data for every type of annotation
    static final String ANNOTATION_ID = "id";
    static final String ANNOTATOR = "annotator";  // also in OLD
    static final String DATE = "date";

    // Compositional annotation specific
    static final String COMPOSITIONAL_ANNOTATION = "compositional-annotation";
    static final String COMPOSITIONAL_ANNOTATION_GRAPH_TITLE = "graph";
    static final String COMPOSITIONAL_ANNOTATION_SOURCE = "source";
    static final String COMPOSITIONAL_ANNOTATION_TARGET = "target";
    static final String COMPOSITIONAL_ANNOTATION_RELATIONSHIP = "relationship";

    // Concept annotation specific
    static final String CONCEPT_ANNOTATION = "concept-annotation";
    static final String SPAN = "span";  // also in OLD
    static final String SPAN_START = "start";  // also in OLD
    static final String SPAN_END = "end";  // also in OLD
    static final String CLASS_NAME = "class-name";
    static final String CLASS_ID = "class-id";

    // Identity annotation specific
    static final String IDENTITY_ANNOTATION = "identity-annotation";
    static final String COREFERRENCE = "conferrence";

    // Used for saving profile settings
    static final String PROFILE = "profile";
    static final String PROFILE_ID = "id";
    static final String HIGHLIGHTER = "highlighter";
    static final String COLOR = "color";

    // User settings
    static final String CONFIG = "config";
    static final String AUTO_LOAD_ONTOLOGIES = "auto-load-ontologies";
    static final String FORMAT = "format";
    static final String DEFAULT_SAVE_LOCATION = "default-save-location";

    // ******************OLD Tags*****************
    static final String ANNOTATIONS = "annotations";

    // Annotations
    static final String ANNOTATION = "annotation";
    static final String MENTION = "mention";
    static final String MENTION_ID = "id";
    static final String SPANNED_TEXT = "spannedText";

    // Class assigned to named entity
    static final String CLASS_MENTION = "classMention";
    static final String CLASS_MENTION_ID = "id";  // Should correspond to a mention id
    static final String MENTION_CLASS = "mentionClass";  // The named entity label
    static final String MENTION_CLASS_ID = "id";  // The named entity id
    static final String HAS_SLOT_MENTION = "hasSlotMention";
    public static final String HAS_SLOT_MENTION_ID = "id";

    // Complex Slot Mentions
    static final String COMPLEX_SLOT_MENTION = "complexSlotMention";
    static final String MENTION_SLOT = "mentionSlot";
    static final String MENTION_SLOT_ID = "id";
    static final String COMPLEX_SLOT_MENTION_ID = "id";
    static final String COMPLEX_SLOT_MENTION_VALUE = "complexSlotMentionValue";
    static final String COMPLEX_SLOT_MENTION_VALUE_VALUE = "value";

    // Coreference specific IDs
    static final String MENTION_CLASS_ID_IDENTITY = "IDENTITY chain";
    static final String MENTION_CLASS_IDENTITY = "IDENTITY chain";
    static final String MENTION_SLOT_ID_COREFERENCE = "Coreferring strings";
    static final String COMPLEX_SLOT_MENTION_ID_APPOS_HEAD = "APPOS Head";
    static final String COMPLEX_SLOT_MENTION_ID_APPOS_ATTRIBUTES = "APPOS Attributes";

}
