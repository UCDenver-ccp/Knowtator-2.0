package edu.ucdenver.ccp.knowtator.io.brat;

/**
 * Based on http://brat.nlplab.org/standoff.html
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class StandoffTags {

	public static final char TEXTBOUNDANNOTATION = 'T';
	public static final char RELATION = 'R';
	public static final char EVENT = 'E';
	// Note that this is not a standard tag. It is included to pass the document ID.
	public static final char DOCID = 'D';
	public static final String spanDelimiter = ";";
	public static final String textBoundAnnotationTripleDelimiter = " ";
	public static final String relationTripleDelimiter = " ";
	public static final String relationTripleRoleIDDelimiter = ":";
	static final String columnDelimiter = "\t";
	private static final char ATTRIBUTE = 'A';
	private static final char MODIFICATION = 'M';
	public static final char NORMALIZATION = 'N';
	private static final char NOTE = '#';
	static final char[] tagList = {
			TEXTBOUNDANNOTATION, RELATION, EVENT, ATTRIBUTE, MODIFICATION, NORMALIZATION, NOTE, DOCID
	};

	// --Commented out by Inspection (9/29/2018 9:43 PM):public static final String visual = "visual";
	public static final String visualLabels = "labels";
	public static final String visualDrawing = "drawing";
	// --Commented out by Inspection (9/29/2018 9:43 PM):public static final String annotations = "annotations";
	public static final String annotationsEntities = "entities";
	public static final String annotationsRelations = "relations";
	public static final String annotationsAttributes = "attributes";
	public static final String annotationsEvents = "events";
}
