package org.example;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyReasoner{

    private OWLOntology ontology;
    private OWLClass bottom;

    public MyReasoner(OWLOntology o) {
        this.ontology=o;
        transformToGCI();
    }

    private Set<OWLSubClassOfAxiom> transformToGCI() {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = man.getOWLDataFactory();

        Set<OWLAxiom> tBoxAxioms = this.ontology.getTBoxAxioms(Imports.EXCLUDED);
        Set<OWLSubClassOfAxiom> subClassOfAxioms = new HashSet<>();

        for(OWLAxiom ax : tBoxAxioms){
            if(ax.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)){
                subClassOfAxioms.addAll(equivalentClassesToSubclassOf(df,ax));
            }
            else if(ax.getAxiomType().equals(AxiomType.DISJOINT_CLASSES)){
                subClassOfAxioms.addAll(disjointClassesToSubclassOf(df,ax));
            }
            else if(ax.getAxiomType().equals(AxiomType.SUBCLASS_OF)){
                OWLSubClassOfAxiom eq = (OWLSubClassOfAxiom) ax;
                subClassOfAxioms.add(eq);
            }
        }
        return subClassOfAxioms;
    }

    private Set<OWLSubClassOfAxiom> equivalentClassesToSubclassOf(OWLDataFactory df, OWLAxiom ax){
        Set<OWLSubClassOfAxiom> set = new HashSet<>();
        ArrayList<OWLClassExpression> tempEquivalentArray = new ArrayList<>();
        OWLEquivalentClassesAxiom eq = (OWLEquivalentClassesAxiom) ax;
        tempEquivalentArray.addAll(eq.getOperandsAsList());
        OWLSubClassOfAxiom ax1 = df.getOWLSubClassOfAxiom(tempEquivalentArray.get(0), tempEquivalentArray.get(1));
        OWLSubClassOfAxiom ax2 = df.getOWLSubClassOfAxiom(tempEquivalentArray.get(1), tempEquivalentArray.get(0));
        set.add(ax1);
        set.add(ax2);
        tempEquivalentArray.clear();
        return set;
    }

    private Set<OWLSubClassOfAxiom> disjointClassesToSubclassOf(OWLDataFactory df, OWLAxiom ax){
        Set<OWLSubClassOfAxiom> set = new HashSet<>();
        ArrayList<OWLClassExpression> tempDisjointArray = new ArrayList<>();
        OWLDisjointClassesAxiom eq = (OWLDisjointClassesAxiom) ax;
        tempDisjointArray.addAll(eq.getOperandsAsList());
        OWLObjectIntersectionOf intersectionOf = df.getOWLObjectIntersectionOf(tempDisjointArray.get(0),tempDisjointArray.get(1));
        OWLSubClassOfAxiom subAxiom = df.getOWLSubClassOfAxiom(intersectionOf,this.bottom);
        set.add(subAxiom);
        tempDisjointArray.clear();
        return set;
    }
}