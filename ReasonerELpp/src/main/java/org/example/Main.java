package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o = man.loadOntologyFromOntologyDocument(new File("C:\\Users\\chris\\Desktop\\TEST_CLASS_1.owx"));
        OWLDataFactory df = man.getOWLDataFactory();
        MyReasoner myReasoner = new MyReasoner(o);
    }
}