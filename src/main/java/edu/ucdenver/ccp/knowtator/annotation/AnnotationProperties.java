package edu.ucdenver.ccp.knowtator.annotation;

public class AnnotationProperties {
    public static final String DATE = "date";
    public static String TEXT_SOURCE = "textSource";
    public static String ANNOTATOR = "profile";
    private static String SPAN = "span";
    public static String SPAN_START = String.format("%s-start", SPAN);
    public static String SPAN_END = String.format("%s-end", SPAN);
}
