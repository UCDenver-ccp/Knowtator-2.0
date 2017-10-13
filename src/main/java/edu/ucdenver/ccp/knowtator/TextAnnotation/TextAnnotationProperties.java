package edu.ucdenver.ccp.knowtator.TextAnnotation;

public class TextAnnotationProperties {
    public static String TEXTSOURCE = "textSource";
    public static String ID = "id";
    public static String ANNOTATOR = "annotator";
    public static String ANNOTATOR_ID = String.format("%s-%s", ANNOTATOR, ID);
    public static String SPAN = "span";
    public static String SPAN_START = String.format("%s-start", SPAN);
    public static String SPAN_END = String.format("%s-end", SPAN);

    public static String CLASS_NAME = "class";
    public static String CLASS_ID = String.format("%s-%s", CLASS_NAME, ID);
}
