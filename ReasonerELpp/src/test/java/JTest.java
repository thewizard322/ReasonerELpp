import org.example.MyReasoner;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class JTest {

    private MyReasoner reasoner;
    private OWLDataFactory df;
    private IRI IOR;

    @Before
    public void setUp() throws Exception {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology o = man.loadOntologyFromOntologyDocument(new File("data/CR1_2_3_TEST.owx"));
        this.df = man.getOWLDataFactory();
        this.IOR = o.getOntologyID().getOntologyIRI().get();
        this.reasoner = new MyReasoner(o);
    }

    @org.junit.Test
    public void test1(){
        OWLClass class1 = df.getOWLClass(IOR + "#M");
        OWLClass class2 = df.getOWLClass(IOR + "#N");
        OWLClass inclusionClass = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,inclusionClass);
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test2(){
        OWLClass class1 = df.getOWLClass(IOR + "#M");
        OWLClass class2 = df.getOWLClass(IOR + "#N");
        OWLClass inclusionClass = df.getOWLThing();
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,inclusionClass);
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test3(){
        OWLClass class1 = df.getOWLClass(IOR + "#M");
        OWLClass class2 = df.getOWLClass(IOR + "#N");
        OWLClass inclusionClass = df.getOWLThing();
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(class1,class2);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(inclusionClass,intersectionOf);
        assertFalse(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test4(){
        Set<OWLClass> set1 = new HashSet<>();
        set1.add(df.getOWLClass(IOR + "#M"));
        set1.add(df.getOWLClass(IOR + "#N"));
        set1.add(df.getOWLClass(IOR + "#I"));
        set1.add(df.getOWLClass(IOR + "#P"));
        OWLClass inclusionClass = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(set1);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOf,inclusionClass);
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test
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
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test6(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLClass classI = df.getOWLClass(IOR + "#I");
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,classI);
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test7(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,classA);
        assertFalse(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test8(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r3");
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLClass classB = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(classA,classB);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,intersectionOf);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,objectSomeValuesFrom);
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test9(){
        OWLIndividual individual = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOf = df.getOWLObjectOneOf(individual);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r2");
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLClass classB = df.getOWLClass(IOR + "#B");
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(classA,classB);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,intersectionOf);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(objectOneOf,objectSomeValuesFrom);
        assertFalse(reasoner.doQuery(query));
    }

    @org.junit.Test
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
        assertFalse(reasoner.doQuery(query));
    }

    @org.junit.Test
    public void test11(){
        OWLIndividual individualX = df.getOWLNamedIndividual(IOR + "#x");
        OWLObjectOneOf objectOneOfX = df.getOWLObjectOneOf(individualX);
        OWLIndividual individualY = df.getOWLNamedIndividual(IOR + "#y");
        OWLObjectOneOf objectOneOfY = df.getOWLObjectOneOf(individualY);
        OWLObjectProperty r = df.getOWLObjectProperty(IOR + "#r4");
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLClass classB = df.getOWLClass(IOR + "#B");
        OWLClass classG = df.getOWLClass(IOR + "#G");
        OWLObjectIntersectionOf intersectionOfSub = df.getOWLObjectIntersectionOf(objectOneOfX,objectOneOfY);
        OWLObjectIntersectionOf intersectionOfSuper = df.getOWLObjectIntersectionOf(classA,classB,classG);
        OWLObjectSomeValuesFrom objectSomeValuesFrom = df.getOWLObjectSomeValuesFrom(r,intersectionOfSuper);
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(intersectionOfSub,objectSomeValuesFrom);
        assertTrue(reasoner.doQuery(query));
    }

    @org.junit.Test (expected = IllegalArgumentException.class)
    public void testException1(){
        OWLClass classA = df.getOWLClass(IOR + "#A");
        OWLSubClassOfAxiom query = df.getOWLSubClassOfAxiom(classA,df.getOWLNothing());
        reasoner.doQuery(query);
    }

}
