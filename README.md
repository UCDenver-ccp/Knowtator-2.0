# MechAnIC
Plugin for Protege: Mechanism Annotation and Inference Console

## Installation and Setup:

1. Install [Desktop Protege 5.X.X][protege link]
2. Copy Knowtator-2.0.jar to Protege-5.X.X/plugins/
3. Launch Protege.exe
4. Create a new tab (Window -> Create new tab...)
5. Add a Class Hierarchy view (Window -> Views -> Class views -> Class Hierarchy)
6. Add an Annotations view (Window -> Views -> Annotation property views -> Annotations)
7. Add a MechAnIC view (Window -> Ontology views -> MechAnIC)
8. Load an ontology (e.g. [GO-Basic][ontology example])

![After installation][installation image]
Add as a View to a Protege tab by going to Menu -> Views -> Ontology Views -> MechAnIC

## Features

#### Annotation highlighting
Click on OWL class in Class hierarchy to select a term. Annotate the text by highlighting it and then clicking "Add TextAnnotation". 

#### Load plain text documents

#### Annotation profiles
Create annotation profiles and assign them different colors. Switch between them quickly with by selecting them on the right.

#### Save annotations to XML file
Annotations can be saved as an XML file. This stores the annotation instance, the text span, the ontology term ID and the ontology term RDFS label.

#### Reload annotations
Annotations are reloaded and automatically highlight the text using the color of the current profile.

### Known bugs
1. When the first ontology is loaded, all current work and profiles are lost.
2. When a new annotation profile is added, it is not displayed until view panes are resized.
3. Can only add annotations to one document at a time since the annotations do not yet know for which file they apply to.
4. Cannot turn off annotation profile to remove highlighting.
5. When MechAnIC view is added, the split between panes is too far to the left.
6. Cannot remove annotations or annotation profiles.

[protege link]:http://protege.stanford.edu/products.php#desktop-protege
[installation image]:installation_image.jpg
[ontology example]:http://purl.obolibrary.org/obo/go/go-basic.obo

