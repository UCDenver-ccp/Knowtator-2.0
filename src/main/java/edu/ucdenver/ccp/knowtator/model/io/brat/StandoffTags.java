package edu.ucdenver.ccp.knowtator.model.io.brat;


/**
 Based on http://brat.nlplab.org/standoff.html
 **/
@SuppressWarnings("WeakerAccess")
public class StandoffTags {

    final static String columnDelimiter = "\t";

    public final static char TEXTBOUNDANNOTATION = 'T';
    public final static char RELATION = 'R';
    public final static char EVENT = 'E';
    private final static char ATTRIBUTE = 'A';
    private final static char MODIFICATION = 'M';
    private final static char NORMALIZATION = 'N';
    private final static char NOTE = '#';

    // Note that this is not a standard tag. It is included to pass the document ID.
    public final static char DOCID = 'D';

    final static char[] tagList = {
            TEXTBOUNDANNOTATION,
            RELATION,
            EVENT,
            ATTRIBUTE,
            MODIFICATION,
            NORMALIZATION,
            NOTE,
            DOCID
    };


    public final static String spanDelimiter = ";";
    public final static String textBoundAnnotationTripleDelimiter = " ";
    public final static String relationTripleDelimiter = " ";
    public final static String relationTripleRoleIDDelimiter = ":";
}
