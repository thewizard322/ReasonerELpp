package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o = man.loadOntologyFromOntologyDocument(new File("ReasonerELpp/data/CR1_2_3_TEST.owx"));
        Test myTest = new Test(o);
        myTest.test1(); //true
        myTest.test2(); //true
        myTest.test3(); //false
        myTest.test4(); //true
        myTest.test5(); //true
        myTest.test6(); //true
        myTest.test7(); //false
        myTest.test8(); //true
        myTest.test9(); //false
        myTest.test10(); //false
    }
}