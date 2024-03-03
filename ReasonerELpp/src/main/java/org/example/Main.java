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
        OWLOntology o = man.loadOntologyFromOntologyDocument(new File("ReasonerELpp/data/CR1_2_TEST.owx"));
        OWLDataFactory df = man.getOWLDataFactory();
        MyReasoner myReasoner = new MyReasoner(o);
        IRI IOR = o.getOntologyID().getOntologyIRI().get();
        OWLClass class1 = df.getOWLClass(IOR + "#A");
        OWLClass class2 = df.getOWLClass(IOR + "#B");
        OWLClass class3 = df.getOWLClass(IOR + "#D");
        OWLIndividual individualD = df.getOWLNamedIndividual(IOR + "#d");
        OWLObjectOneOf objectOneOfD = df.getOWLObjectOneOf(individualD);
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,class3);
        myReasoner.doQuery(query);
        //Set<OWLAxiom> subClassOfAxioms = o.getTBoxAxioms(Imports.EXCLUDED);
//        OWLClass tempClass1 = df.getOWLClass(IRI.create("#TEMP1"));
//        OWLClass tempClass2 = df.getOWLClass(IRI.create("#TEMP2"));
//        OWLClass tempClass3 = df.getOWLClass(IRI.create("#TEMP3"));
//        OWLObjectIntersectionOf intersectionOf1 = df.getOWLObjectIntersectionOf(tempClass2,tempClass1);
//        OWLObjectIntersectionOf intersectionOf2 = df.getOWLObjectIntersectionOf(tempClass1,tempClass2);
//        OWLObjectIntersectionOf intersectionOf3 = df.getOWLObjectIntersectionOf(tempClass1,tempClass3);
//        System.out.println(intersectionOf1.equals(intersectionOf3));
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