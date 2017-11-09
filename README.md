# Knowtator
Plugin for Protege

## Table of Contents
- [Installation (Windows):](#)
- [Installation (Mac):](#)
- [Setup:](#)
- [Features](#)
	- [TextAnnotation highlighting](#)
	- [Load plain text documents](#)
	- [TextAnnotation profiles](#)
	- [Save textAnnotations to XML file](#)
	- [Reload textAnnotations](#)
- [Known bugs](#)

## Installation (Windows):

1. Install [Desktop Protege 5.X.X][protege link]
2. Copy Knowtator-2.0.jar to Protege-5.X.X/plugins/
3. Restart Protege

## Installation (Mac):
1. Install [Desktop Protege 5.X.X][protege link]
2. In Finder, right click the Protege 5 app icon
3. Click "Show Package Contents"
4. Go to Contents -> Java -> Plugins
5. You should see the other plugins included in Protege 5 in that folder. If so, copy the Knowtator-2.0.jar into it.
6. Restart Protege

(See [this comment thread][mac osx plugin intallation comment thread] for more help) 

## Setup:
1. Launch Protege.exe
2. Create a new tab (Window -> Create new tab...)
3. Add a Class Hierarchy view (Window -> Views -> Class views -> Class Hierarchy)
4. Add an Annotations view (Window -> Views -> TextAnnotation property views -> Annotations)
5. Add a Knowtator view (Window -> Miscellaneous views -> Knowtator)
6. Load an ontology (e.g. [GO-Basic][ontology example])

(A sample file and its textAnnotation xml file can be found [here][sample files location])

![After installation][installation image]
Add as a View to a Protege tab by going to Window -> Views -> Miscellaneous Views -> Knowtator

## Features

### TextAnnotation highlighting
Click on OWL class in Class hierarchy to select a term. Annotate the text by highlighting it and then clicking "Add TextAnnotation". 

### Load plain text documents

### TextAnnotation profiles
Create textAnnotation profiles and assign them different colors. Switch between them quickly with by selecting them on the right.

### Save textAnnotations to XML file
Annotations can be saved as an XML file. This stores the textAnnotation instance, the text textSpan, the ontology term ID and the ontology term RDFS label.

### Reload textAnnotations
Annotations are reloaded and automatically highlight the text using the color of the current annotator.

## Known bugs
1. When the first ontology is loaded, all current work and profiles are lost.
2. When a new textAnnotation annotator is added, it is not displayed until view panes are resized.
3. Can only add textAnnotations to one document at a time since the textAnnotations do not yet know for which file they apply to.
4. Cannot turn off textAnnotation annotator to remove highlighting.
5. When Knowtator view is added, the split between panes is too far to the left.
6. Cannot remove textAnnotations or textAnnotation profiles.

[protege link]:http://protege.stanford.edu/products.php#desktop-protege
[installation image]:installation_image.jpg
[ontology example]:http://purl.obolibrary.org/obo/go/go-basic.obo
[mac osx plugin intallation comment thread]:http://protege-project.136.n4.nabble.com/Installing-Plugins-on-Protege-5-MacOSX-td4665874.html
[sample files location]:https://github.com/tuh8888/Knowtator-2.0/tree/master/src/main/resources/file

