package edu.ucdenver.ccp.knowtator.io.knowtator;

public class KnowtatorXMLAttributes {
	public static final String ID = "id";

	public static final String ANNOTATOR = "annotator"; // also in OLD
	public static final String TYPE = "type";
	public static final String SPAN_START = "start";
	public static final String SPAN_END = "end";

	// Graph data
	public static final String TRIPLE_SUBJECT = "subject";
	public static final String TRIPLE_OBJECT = "object";
	public static final String TRIPLE_PROPERTY = "property";
	public static final String TRIPLE_QUANTIFIER = "quantifier";
	public static final String TRIPLE_VALUE = "value";
	public static final String NEGATED = "complement-of";

	// Used for saving profile settings
	public static final String COLOR = "color";
	public static final String CLASS_ID = "class";
	public static final String FILE = "text-file";
	public static final String LABEL = "label";
}
