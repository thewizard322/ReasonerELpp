package org.example;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

public class MyReasoner {

    private OWLOntology ontology;

    public MyReasoner(OWLOntology o){
        ontology=o;
    }

    private OWLOntology pre_normalization(){
        return null;
    }

}
