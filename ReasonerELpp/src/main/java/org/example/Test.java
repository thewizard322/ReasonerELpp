package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Test{

    private OWLOntology ontology;
    OWLDataFactory df;
    MyReasoner reasoner;
    IRI IOR;

    private int i = 1;

    public Test(OWLOntology o){
        this.ontology = o;
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        this.df = man.getOWLDataFactory();
        this.reasoner = new MyReasoner(o);
        this.IOR = o.getOntologyID().getOntologyIRI().get();
    }

    public void test1(){
        OWLClass class1 = df.getOWLClass(IOR + "#M");
        OWLClass class2 = df.getOWLClass(IOR + "#N");
        OWLClass inclusionClass = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,inclusionClass);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test2(){
        OWLClass class1 = df.getOWLClass(IOR + "#M");
        OWLClass class2 = df.getOWLClass(IOR + "#N");
        OWLClass inclusionClass = df.getOWLThing();
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,inclusionClass);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test3(){
        OWLClass class1 = df.getOWLClass(IOR + "#M");
        OWLClass class2 = df.getOWLClass(IOR + "#N");
        OWLClass inclusionClass = df.getOWLThing();
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(inclusionClass,intersectionOf);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test4(){
        Set<OWLClass> set1 = new HashSet<>();
        set1.add(df.getOWLClass(IOR + "#M"));
        set1.add(df.getOWLClass(IOR + "#N"));
        set1.add(df.getOWLClass(IOR + "#I"));
        set1.add(df.getOWLClass(IOR + "#P"));
        OWLClass inclusionClass = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(set1);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,inclusionClass);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test5(){
        Set<OWLClass> set1 = new HashSet<>();
        Set<OWLClass> set2 = new HashSet<>();
        set1.add(df.getOWLClass(IOR + "#M"));
        set1.add(df.getOWLClass(IOR + "#N"));
        set1.add(df.getOWLClass(IOR + "#I"));
        set2.add(df.getOWLClass(IOR + "#P"));
        set2.add(df.getOWLClass(IOR + "#B"));
        OWLObjectIntersectionOf intersectionOfSub = df.getOWLObjectIntersectionOf(set1);
        OWLObjectIntersectionOf intersectionOfSuper = df.getOWLObjectIntersectionOf(set2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOfSub,intersectionOfSuper);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test6(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLClass classI = df.getOWLClass(IOR + "#I");
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,classI);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test7(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,classA);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test8(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r3");
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLClass classB = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(classA,classB);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,intersectionOf);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,objectSomeValuesFrom);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test9(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r2");
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLClass classB = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(classA,classB);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,intersectionOf);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,objectSomeValuesFrom);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void test10(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r2");
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLClass classB = df.getOWLClass(IOR + "#B");
        OWLClass classI = df.getOWLClass(IOR + "#I");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(classA,classB,classI);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,intersectionOf);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,objectSomeValuesFrom);
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }

    public void testException(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r4");
        OWLClass classA = df.getOWLClass(IOR + "#S");
        OWLClass classB = df.getOWLClass(IOR + "#Q");
        OWLClass classI = df.getOWLClass(IOR + "#I");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(classA,classB,classI);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,classB);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(classA,df.getOWLNothing());
        System.out.println(query);
        System.out.println("Test " + i + ": " + reasoner.doQuery(query));
        i++;
    }
}
