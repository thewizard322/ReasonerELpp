package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o = man.loadOntology(IRI.create("http://purl.obolibrary.org/obo/go.owl"));
        OWLDataFactory df = man.getOWLDataFactory();
        for(OWLEquivalentClassesAxiom owl: o.getAxioms(AxiomType.EQUIVALENT_CLASSES)){
            System.out.println(owl);
        }
    }
}