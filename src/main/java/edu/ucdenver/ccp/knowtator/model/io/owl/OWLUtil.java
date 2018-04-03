/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.io.owl;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.owl.OWLAPIDataExtractor;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class OWLUtil implements BasicIOUtil {

    private static final Logger log = Logger.getLogger(OWLUtil.class);

    @Override
    public void read(Savable savable, File file) throws IOException {
        if (savable instanceof OWLAPIDataExtractor) {
            if (file.isDirectory()) {
                Files.newDirectoryStream(Paths.get(file.toURI()),
                        path -> path.toString().endsWith(".owl"))
                        .forEach(path1 -> loadOntologyFromLocation((OWLAPIDataExtractor) savable, path1.toFile().toURI().toString()));
            }
        }
    }

    private void loadOntologyFromLocation(OWLAPIDataExtractor dataExtractor, String ontologyLocation) {
        OWLModelManager owlModelManager = dataExtractor.getOwlModelManager();
        if (owlModelManager != null) {
            List<String> ontologies = owlModelManager.getActiveOntologies().stream().map(ontology -> {
                OWLOntologyID ontID = ontology.getOntologyID();
                //noinspection Guava
                Optional<IRI> ontIRI = ontID.getOntologyIRI();
                if (ontIRI.isPresent()) {
                    return ontIRI.get().toURI().toString();
                } else {
                    return null;
                }
            }).collect(Collectors.toList());

//        String ontologyLocation = OntologyTranslator.translate(classID);
            if (!ontologies.contains(ontologyLocation)) {
                log.warn("Loading ontology: " + ontologyLocation);
                try {
                    OWLOntology newOntology = owlModelManager.getOWLOntologyManager().loadOntology((IRI.create(ontologyLocation)));
                    owlModelManager.setActiveOntology(newOntology);
                } catch (OWLOntologyCreationException e) {
                    log.warn("Knowtator: OWLAPIDataExtractor: Ontology already loaded");
                }
            }
        }

    }

    @Override
    public void write(Savable savable, File file) {

    }
}
