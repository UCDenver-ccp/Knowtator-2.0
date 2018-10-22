# Knowtator
Plugin for Protege


## Table of Contents
- [Installation](#installation)
  - [Windows](#windows)
  - [Mac](#mac)
  - [Source](#source)
- [Setup](#setup)
- [Concept Annotation](#concept-annotation)
- [Profiles](#profiles)
- [Search](#search)
- [Inter Annotator Agreement (IAA)](#inter-annotator-agreement)
- [Relation Annotation](#relation-annotation)
- [Acknowledgements](#acknowledgements)

## Installation

### Windows
1. Download the [Knowtator][knowtator link] jar file
2. Install [Desktop Protege 5.X.X][protege link]
3. Copy Knowtator-2.X.X.jar to Protege-5.X.X/plugins/
4. Restart Protege


### Mac
1. Download [Knowtator][knowtator link] jar file
2. Install [Desktop Protege 5.X.X][protege link]
3. In Finder, right click the Protege 5 app icon
4. Click "Show Package Contents"
5. Go to Contents -> Java -> Plugins
6. You should see the other plugins included in Protege 5 in that folder. If so, copy the Knowtator-2.X.X.jar into it. (Important: Be sure that you are copying the jar file itself and not a link to the jar file)
7. Restart Protege


(See [this comment thread][mac osx plugin installation comment thread] for more help) 


### Source
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

## Concept Annotation


Protege is first and foremost, an ontology editor. This plugin is intended to make use of Protege's built-in OWL-API to annotate text with OWL entities. To do so, simply select a term from the Class Hierarchy (Window -> Views -> Class views -> Class hierarchy), highlight a span of text in a document, and click the ![][plus] button and select "Add new conecpt". 

To remove the selected icon, click the ![][remove] button.

To change the color assignment of an OWL class, click the ![][change color] button.

Annotations will appear as highlights in the text. Each annotation can have multiple discontinuous text spans. To add a span to an annotation, select the text to include in the span, then click the ![][plus] button and select "Add span to concept".

The other properties associated with an annotation are an annotation ID which is automatically generated when the annotation is made and an annotator which is discussed [below](#Profiles).

## Profiles

Knowtator uses profile system to keep track of who is making each annotation. This allows for the calculation of [inter-annotator agreement)[inter-annotator-agreement] metrics. A default annotator is used for each project unless the user creates their own profile. To manage profiles, click ![][menu] and then "Profile". Here you can enter a profile name and add it, remove profiles, and see the color choices for each concept.

To change to a different active profile, simply select it in the profile list.

To change the color associated with a concept, click on the concept in the color list.

To view only annotation made by the active profile, in the tool bar above the text pane, select the "Profile" check box. This will filter out all annotation not made by the active profile.

## Search

For large documents or large ontologies, it can be a challenge to find similar spans of text or to find the right concept. Knowtator provides some search tools to make the annotation process faster and easter. At the top of the text pane, there is a search box. Typing text in here and clicking "Next" or "Previous" will search the current document forward or backward respectively from the current cursor location. Alternatively, clicking "Find in ontology" will open Protege's ontology search tool. This allows you to find text in the document and then find the right concept for that text. 

Instead of typing text directly, you can also simply select text in the document. You will see this text appear automatically in the search bar.

Searches can be filtered to only search text that has been associated with an annotation by clicking the "Only in annotations" check box.

## Inter-Annotator Agreement

IAA between the annotators in the same project can be run. 

To calculate IAA, click ![][menu] the "IAA". Select the types of IAA you wish to run, then click "Run IAA" and choose a folder for the results to be written to.


## Relation Annotation

In addition to concept annotation in text, Knowtator has also been updated to support relation annotation. Relation annotations are made by connecting two concept annotations with an OWL object property. Just like the RDF triple subject-predicate-object model, relation annotations assert a relationship between two entities. These relationships form a directed graph.

To begin making relation annotations, open the graph viewer by clicking ![][graph viewer]. The graph viewer is used to create graph spaces and display relations. Selecting a concept annotation in the text pane, and then clicking the ![][plus] in the graph viewer will bring that concept annotation into the current graph space. It will display as a rectangle witht the color of the concept annotation and contain each of the concept annotation's the text spans. Once two or more concept annotations are in the graph space, select an object property (Window -> Views -> Object property views -> Object property hierarchy), then hover your mouse over a concept annotation node until it becomes a pointer finger. Click and drag the arrow to another concept annotation node. A window will appear allowing you to choose a quantifier or negate the relation.


## Acknowledgements

All icons used are from https://icons8.com/

Thank you to Philip Ogren, the author of the original Knowtator.

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
