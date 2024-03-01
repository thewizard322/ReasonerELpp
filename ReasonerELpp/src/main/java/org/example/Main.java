package org.example;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.File;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o = man.loadOntologyFromOntologyDocument(new File("ReasonerELpp/data/SOME_TEST_1.owx"));
        OWLDataFactory df = man.getOWLDataFactory();
        MyReasoner myReasoner = new MyReasoner(o);
        Set<OWLAxiom> subClassOfAxioms = o.getTBoxAxioms(Imports.EXCLUDED);
//        for(OWLAxiom ax : subClassOfAxioms){
//            OWLSubClassOfAxiom cast = (OWLSubClassOfAxiom) ax;
//            OWLClassExpression subClass = cast.getSubClass();
//            if(subClass.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
//                OWLObjectSomeValuesFrom obj = (OWLObjectSomeValuesFrom) subClass;
//                System.out.println(obj.getFiller());
//            }
//        }
    }
}