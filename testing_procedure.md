1. New project
    - Open file chooser dialog
    - Create project folder with name
    - project folder
        - create project_name.knowtator
        - create Annotations folder. TextSourceManager should know this location
        - create Profiles folder. ProfileManager should know this location.
        - create Articles folder. TextSourceManager should know this location.
        - create Ontologies folder. OwlModelManager should know this location.
        - ProjectManager/Controller should know the project file folder location
    - All collections should be cleared.
    - All manager listeners should be cleared.
    - "Initial state" of View should be restored
2. Loading
    - From Knowtator XML
        - project.knowtator
            - Check file exists
            - Get file folder. This is the project folder
        - Folder
            - Check project folder exists
            - Make Annotations folder
            - Make Profiles folder
            - Make Articles folder
            - Make Ontologies folder
            - Check if all above exist under project folder
        - Annotations
            - Find all *.xml files
            - Parse XML
            - Find "knowtator-project" (root) tag
            - Find all "document" tags
            - document
                - Get document id. Should be the same as the id of an article. Should open file chooser if not found and cancel loading if no file is chosen
                - Validate document id
                - Find all "annotation" tags
                - annotation
                    - Annotations should have an annotator and type
                    - Validate annotation id
                    - Find all "class" and "span" tags
                    - class
                        - Validate id
                        - Text is the label
                    - check only one class
                    - span
                        - Start
                        - End
                        - start should be less than or equal to the end
                        - text should be the same as the text in the corresponding article
                    - check number of spans
                - check number of annotations
                - check total number of spans
                - get all "graph-space" tags
                - graph-space
                    - validate graph space id
                    - get all "vertex" tags
                    - vertex
                        - validate id
                        - "annotation" should correspond to id of an annotation
                        - x and y should be numbers
                    - get all "triple" tags
                        - validate id
                        - check annotator
                        - object should be a vertex
                        - subject should be a vertex
                        - quantifier should be "some" "all" "max" "min" or "only"
                        - value should be "" or a number
                    - check number of vertices
                    - check number of triples
                    - check vertices are in the location specified by their x and y
                - check number of graph spaces
        - Profiles
            - Find all *.xml files
            - Parse XML
            - Find "knowtator-project" (root) tag
            - get all "profile" tags
            - profile
                - validate id
                - get all highlighter tags
                - highlighter
                    - "class" should correspond to the id of an owl class
                    - color should be a six digit hexadecimal value starting with #
                    - color should parse to an actual color
                - check number of highlighters
            - check number of profiles
        - Articles
            - get all *.txt files
            - File names should correspond to ids in annotations
            - Should be all text and readable into a string
        - Ontologies
            - get all *.owl files
            - should attempt to load each unless owl model manager doesn't exist
        - loading should not take more than a minute even for large projects
        - No saving should happen until after loading is finished

2. Saving
    - State of project after should be same as state of project before
    - Should load to the same state as before saving
    - Saves should occur after any change
3. Selection
    - annotation
        - should select corresponding owl class
        - textpane should refresh
        - selecting a text span should only highlight the text in the text pane
        - click in a location spanned by an annotation should select that annotation
        - click in a location not spanned by an annotation should no select a different annotation
        - click in a location spanned by mutiple annotations should make a popup containing the class names of those annotations
        - when selected
            - All spans of selected annotation should be boxed in black
            - All annotation buttons should all be active
            - Annotation info should be in info pane
                - annotation id
                - annotator name
                - class id or class id and label
                - all of the annotation's spans should be listed
                - all graph spaces contained the annotation should be listed
            - should select all vertices in graph space that have the selected annotation
        - when not selected
            - No spans should be boxed in black
            - Remove annotation button should not be active
            - No spans should be boxed in black
    - span
        - when selected
            - all span buttons should be active
            - span in info pane should be active
        - when not selected
            - all span buttons should be inactive
            - span in info pane should not be active
            - no annotation should be selected
    - text source
        - textpane should refresh
        - when selected
            - text source in TextSourceShooser should be active
            - all text source buttons should be active
            - content should be shown
        - when not selected
            - remove text source button and show graph viewer button should be inactive
            - no annotation or span buttons should be active
    - graph space
        - when selected
            - graph space in GraphSpaceChooser should be active
            - all graph space buttons should be active
        - when not selected
            - remove graph space button inactive
            - add graph space button active
            - no graph space editing buttons should be active
    - graph space node
        - when selected
            - all graph space editing buttons should be active
        - when not selected
            - add graph space node button should be active
            - remove graph space node button should not be active
    - profile
        - should always be selected
    - text selection
        - start
        - end
        - start <= end
    - filter
        - when selected
            - profile filter
                - deselect annotation if not by selected profile
                - when retrieving spans or annotations, they should be limited to the have the current annotator
            - class filter
                - deselect annotation if not in selected class range
                - when retrieving spans or annotations, they should be limited to the have the current annotator
        - when not selected
            - both
                - get all annotations or spans
    - triple
        - should select corresponding owl property
       
4. Addition
    - text source
    - annotation
        - add with at least one span
        - should save
        - output file should have the additional annotation
        - textpane should refresh
        - only if owl class selected
    - span
        - add to current annotation
    - graph space
    - annotation node
    - triple
        - only if owl property is selected
    - profile
5. Deletion
    
    
    
        
        

1. Adding annotations
    -Does it map to OWL class
    -Does the file save
    -Is it selected
2. Selecting annotations
3. Removing annotations
4. Adding spans
5. Selecting spans
    -Can different spans be selected in the same annotations
    -Can overlapping spans be selected
6. Displaying spans
    -Are overlapping annotations displayed correctly
7. Removing spans
8. Changing OWL class IRI
9. Changing OWL class annotation property
10. Removing OWL class
11. Moving OWL class
12. Changing OWL class color
    -Do colors get applied
13. Changing rendering
    -Text source should save once
