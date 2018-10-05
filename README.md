# Knowtator
Plugin for Protege


## Table of Contents
- [Installation Windows](#installation-windows)
- [Installation Mac](#installation-mac)
- [Installation from source](#installation-from-source)
- [Setup](#setup)
- [Features](#features)
	- [Annotate with ontology terms](#annotate-with-ontology-terms)
	- [Inter Annotator Agreement (IAA)](#inter-annotator-agreement)
	- [Graphical Annotations](#graphical-annotations)
	- [Annotation Information](#annotation-information)


## Installation Windows
1. Download the [Knowtator][knowtator link] jar file
2. Install [Desktop Protege 5.X.X][protege link]
3. Copy Knowtator-2.X.X.jar to Protege-5.X.X/plugins/
4. Restart Protege


## Installation Mac
1. Download [Knowtator][knowtator link] jar file
2. Install [Desktop Protege 5.X.X][protege link]
3. In Finder, right click the Protege 5 app icon
4. Click "Show Package Contents"
5. Go to Contents -> Java -> Plugins
6. You should see the other plugins included in Protege 5 in that folder. If so, copy the Knowtator-2.X.X.jar into it. (Important: Be sure that you are copying the jar file itself and not a link to the jar file)
7. Restart Protege


(See [this comment thread][mac osx plugin installation comment thread] for more help) 


## Installation from source
1. Install Maven
2. Install [Desktop Protege 5.X.X][protege link]
3. Set PROTEGE_HOME environment variable to location of Protege installation
4. Download source files
```console
$ git clone https://github.com/UCDenver-ccp/Knowtator-2.0.git
```
5. Change to project directory
```console
$ cd Knowtator-2.0
```
6. Install
```console
$ mvn clean install
```
7. Restart Protege


## Setup
1. Launch Protege
2. Click the "Entities" tab
3. Add a Knowtator view (Window -> Miscellaneous views -> Knowtator). Position your cursor in the Protege window so that the blue outline fills the right side of the window. Click to place the view.
4. Make a new project (Knowtator Project -> New Project. Note: The Knowtator menu is inside the Knowtator view). Enter a name for the project. Select parent directory. Knowtator will make
a directory tree that looks like this:
Project
-Annotations
-Articles
-Ontologies
-profiles
-project_name.knowtator
5. Load a document (![][plus] at bottom of Knowtator)
7. To open a pre-existing project (![][menu] then "New")

![After installation][installation image]
Add as a View to a Protege tab by going to Window -> Views -> Miscellaneous Views -> Knowtator


## Features


### Annotate with ontology terms


Protege is first and foremost, an ontology editor. This plugin is intended to make use of Protege's built-in OWL-API to annotate text with OWL classes. To do so, simply select a term from the Class Hierarchy (Window -> Views -> Class views -> Class hierarchy), highlight a span of text in a document, and click the ![][plus] button. 

To remove the selected icon, click the ![][remove] button.

To change the color assignment of an OWL class, click the ![][change color] button


### Inter-Annotator Agreement


IAA between the annotators in the same project can be run. Select the types of IAA you wish to run from the IAA menu and click IAA -> Run IAA. Select a folder for the results to be written to.


### Graphical Annotations

Open the graph viewer by clicking ![][graph viewer]. 

To add the selected annotation to the graph viewer, simply click ![][plus] in the graph viewer. You will see the node apear in the viewer.

To add a relation (edge), first select an object property (these can be found in the Object property hierarchy view in Window -> Views -> Object property views -> Object property hierarchy), then hover over a node in the viewer until a pointer hand appears. Click and drag between nodes to make a connection.


### Annotation Information


Selecting a span will show its meta-data in the right-hand side of the view.


All icons used are from https://icons8.com/

[knowtator link]:https://github.com/UCDenver-ccp/Knowtator-2.0/releases/latest
[protege link]:http://protege.stanford.edu/products.php#desktop-protege
[installation image]:installation_image.PNG
[ontology example]:http://purl.obolibrary.org/obo/go/go-basic.obo
[mac osx plugin installation comment thread]:http://protege-project.136.n4.nabble.com/Installing-Plugins-on-Protege-5-MacOSX-td4665874.html
[sample files location]:https://github.com/tuh8888/Knowtator-2.0/tree/master/src/test/resources
[plus]:src/main/resources/icon/icons8-plus-24.png
[menu]:src/main/resources/icon/icons8-menu-24.png
[remove]:src/main/resources/icon/icons8-delete-24.png
[change color]:src/main/resources/icon/icons8-color-dropper-filled-50.png
[change color]:src/main/resources/icon/icons8-tree-structure-32.png


<!--stackedit_data:
eyJoaXN0b3J5IjpbMTE5NTc3MTE5M119
-->