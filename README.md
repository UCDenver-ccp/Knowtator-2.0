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
	- [Save annotations to XML file](#)
	- [Reload annotations](#)
- [Known bugs](#)

## Installation (Windows):

1. Install [Desktop Protege 5.X.X][protege link]
2. Copy Knowtator-2.0.0.jar to Protege-5.X.X/plugins/
3. Restart Protege

## Installation (Mac):
1. Install [Desktop Protege 5.X.X][protege link]
2. In Finder, right click the Protege 5 app icon
3. Click "Show Package Contents"
4. Go to Contents -> Java -> Plugins
5. You should see the other plugins included in Protege 5 in that folder. If so, copy the Knowtator-2.0.0.jar into it.
6. Restart Protege

(See [this comment thread][mac osx plugin intallation comment thread] for more help) 

## Setup:
1. Launch Protege.exe
2. Add a Knowtator view (Window -> Miscellaneous views -> Knowtator). Click somewhere in Protege to add it in.
3. Open a txt file (Project -> Open Document)
4. Add a profile (Profile -> New Profile)
5. To save (Project -> Save Project)
6. To open a pre-existing project (Project -> Open Project)
7. To load annotations from the old Knowtator (Project -> Open Project) 

![After installation][installation image]
Add as a View to a Protege tab by going to Window -> Views -> Miscellaneous Views -> Knowtator

## Features

### Annotate with ontology terms

Protege is first and foremost, an ontology editor. This plugin is intended to make use of Protege's built-in OWL-API
to annotate text with OWL classes. To do so, simply select a term from the Class Hierarchy (Window -> Views -> Class views -> Class hierarchy),
highlight a span of text in a document, and click the "+" button. To remove the selected icon, click the "-" button.

### Inter-Annotator Agreement (IAA)

IAA between the annotators in the same project can be run. Select the types of IAA you wish to run from the IAA menu
and click IAA -> Run IAA. Select a folder for the results to be written to.

### Graphical Annotations

Open the graph viewer by clicking Graph -> Show graph viewer. To add the selected annotation to the graph viewer click
Graph -> Add annotation no to graph. You will see the node apear in the viewer. Select an object property (these can be found
in the Object property hierarchy view in Window -> Views -> Object property views -> Object property hierarchy), then hover
over a node in the viewer until a pointer hand appears. Click and drag between nodes to make a connection. Graphs are saved
in the project (Project -> Save Project).

### Annotation Information

Selecting a span will show its meta-data in the right-hand side of the view.

All icons used are from https://icons8.com/

[protege link]:http://protege.stanford.edu/products.php#desktop-protege
[installation image]:installation_image.PNG
[ontology example]:http://purl.obolibrary.org/obo/go/go-basic.obo
[mac osx plugin intallation comment thread]:http://protege-project.136.n4.nabble.com/Installing-Plugins-on-Protege-5-MacOSX-td4665874.html
[sample files location]:https://github.com/tuh8888/Knowtator-2.0/tree/master/src/main/resources/file

