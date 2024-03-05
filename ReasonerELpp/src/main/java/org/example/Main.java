package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o = man.loadOntologyFromOntologyDocument(new File("ReasonerELpp/data/CR1_2_3_TEST.owx"));
        OWLDataFactory df = man.getOWLDataFactory();
        MyReasoner myReasoner = new MyReasoner(o);
        IRI IOR = o.getOntologyID().getOntologyIRI().get();
        OWLClass class1 = df.getOWLClass(IOR + "#F");
        OWLClass class2 = df.getOWLClass(IOR + "#A");
        OWLClass class3 = df.getOWLClass(IOR + "#B");
        OWLIndividual individualD = df.getOWLNamedIndividual(IOR + "#d");
        OWLObjectOneOf objectOneOfD = df.getOWLObjectOneOf(individualD);
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(class1,class2);
        System.out.println(myReasoner.doQuery(query));
    }
}