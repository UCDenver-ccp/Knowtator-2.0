# Knowtator 2 File Format

The Knowtator 2 XML file format contains records. Each record has an associated ID which is used to refer to other records.
- Knowtator project: A container for documents
 - Document: A container for annotations and graph spaces
  - Annotation: A container for spans and classes. Also has an annotator ID.
   - Span: Has a start and an end. Spanned text is also provided for readability.
   - Class: A label is provided for readability
  - Graph Space: A container for nodes and triples
   - Node: Has a reference to an annotation as well as x and y coordinates for visualization.
   - Triple: Has a subject node and an object node, as well as a property that links the two which may be modified by a quantifier and a value for that quantifier. Also has an annotator ID.