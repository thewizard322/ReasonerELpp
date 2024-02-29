package org.example;

import javafx.util.Pair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyReasoner{

    private OWLOntology ontology;
    private OWLDataFactory df;
    private int universalTempCount = 0;

    public MyReasoner(OWLOntology o) {
        this.ontology=o;
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        this.df = man.getOWLDataFactory();
        normalization();
    }

    private void normalization(){

        Set<OWLAxiom> subClassOfAxioms = this.ontology.getTBoxAxioms(Imports.EXCLUDED);
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> leftPair;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> rightPair;
        Set<OWLSubClassOfAxiom> resultSet = new HashSet<>();

        for(OWLAxiom ax : subClassOfAxioms){

            OWLSubClassOfAxiom cast = (OWLSubClassOfAxiom) ax;
            OWLClassExpression subClass = cast.getSubClass();
            OWLClassExpression superClass = cast.getSuperClass();

            leftPair = subClassNormalization(subClass);
            rightPair = superClassNormalization(superClass);

            resultSet.addAll(leftPair.getKey());    //Aggiungo al resultSet il set delle normalizzazioni
            resultSet.addAll(rightPair.getKey());   //Aggiungo al resultSet il set delle normalizzazioni
            OWLSubClassOfAxiom normalizedSubClass = this.df.getOWLSubClassOfAxiom(leftPair.getValue(),rightPair.getValue());
            resultSet.add(normalizedSubClass);

            System.out.println(cast);
            System.out.println("FIRST");
            System.out.println(leftPair.getKey());
            System.out.println("SECOND");
            System.out.println(leftPair.getValue());

            System.out.println("FIRST");
            System.out.println(rightPair.getKey());
            System.out.println("SECOND");
            System.out.println(rightPair.getValue());

            System.out.println("NORMALIZED CLASS");
            System.out.println(normalizedSubClass);
            break;
        }
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> subClassNormalization(OWLClassExpression subClass){
        Set<OWLSubClassOfAxiom> set = new HashSet<>();

        if(subClass.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)){ //Verifica se è una classe semplice
            return new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(set,subClass);
        } else if(subClass.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)){ //Verifica se è intersezione
            OWLObjectIntersectionOf intersectionOf = (OWLObjectIntersectionOf) subClass;
            int subClassSize = intersectionOf.getOperandsAsList().size();

            if(subClassSize == 2){
                return new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(set,subClass);
            } else {
                return normalizeIntersectionOf(intersectionOf); //Il pair è ritornato dalla funzione chiamata
            }
        }
        return null;
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> superClassNormalization(OWLClassExpression superClass){
        Set<OWLSubClassOfAxiom> set = new HashSet<>();
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair;

        if(superClass.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)){ //Verifica se è una classe semplice
            return new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(set,superClass);
        } else if(superClass.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)){ //Verifica se è intersezione
            OWLObjectIntersectionOf intersectionOf = (OWLObjectIntersectionOf) superClass;
            returnPair = normalizeIntersectionOf(intersectionOf);

            OWLClass tempClass = createTempClass();
            OWLObjectIntersectionOf lastInterstection = (OWLObjectIntersectionOf) returnPair.getValue();
            ArrayList<OWLClassExpression> arrayListOfExpressions = new ArrayList<>(lastInterstection.getOperandsAsList());
            returnPair.getKey().addAll(normalizeSingleIntersectionOf(arrayListOfExpressions.get(0),
                    arrayListOfExpressions.get(1),tempClass));
            return new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(returnPair.getKey(),tempClass);
        }
        return null;
    }




    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> normalizeIntersectionOf(OWLObjectIntersectionOf intersectionOf){

        ArrayList<OWLClassExpression> arrayListOfExpressions = new ArrayList<>(intersectionOf.getOperandsAsList());
        int size = arrayListOfExpressions.size();

        Set<OWLClass> setTempClasses = new HashSet<>();
        Set<OWLSubClassOfAxiom> returnSet = new HashSet<>();
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair = null;

        if(size == 2){
            return new Pair<>(returnSet,intersectionOf);
        }

        for(int i=0; i<size; i++){

            if(i%2!=0){
                OWLClass tempClass = createTempClass();
                setTempClasses.add(tempClass); //Necessario per creare intersezione per chiamata ricorsiva
                returnSet.addAll(normalizeSingleIntersectionOf(arrayListOfExpressions.get(i-1),arrayListOfExpressions.get(i),tempClass));
            }
        }

        if(size%2!=0){
            if(arrayListOfExpressions.get(size-1).getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
                setTempClasses.add((OWLClass)arrayListOfExpressions.get(size-1));
            }
        }

        OWLObjectIntersectionOf intersectionRecur = this.df.getOWLObjectIntersectionOf(setTempClasses); //Creo intersezione per ricorsione
        returnPair = normalizeIntersectionOf(intersectionRecur); //RICORSIONE
        returnPair.getKey().addAll(returnSet); //Aggiunta elementi al set (solo SubClasses)
        return returnPair;
    }




    private OWLClass createTempClass(){
        IRI IOR = ontology.getOntologyID().getOntologyIRI().get();
        OWLClass tempClass = this.df.getOWLClass(IRI.create(IOR + "#TEMP" + universalTempCount));
        universalTempCount++;
        return tempClass;
    }




    private Set<OWLSubClassOfAxiom> normalizeSingleIntersectionOf(OWLClassExpression prev,
                                                                   OWLClassExpression curr, OWLClass tempClass){
        Set<OWLSubClassOfAxiom> returnSet = new HashSet<>();
        OWLSubClassOfAxiom sub1 = this.df.getOWLSubClassOfAxiom(tempClass,prev);
        OWLSubClassOfAxiom sub2 = this.df.getOWLSubClassOfAxiom(tempClass,curr);
        OWLObjectIntersectionOf intersectionPair = this.df.getOWLObjectIntersectionOf(prev,curr);
        OWLSubClassOfAxiom sub3 = this.df.getOWLSubClassOfAxiom(intersectionPair,tempClass);
        returnSet.add(sub1);
        returnSet.add(sub2);
        returnSet.add(sub3);
        return returnSet;
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
        OWLSubClassOfAxiom subAxiom = df.getOWLSubClassOfAxiom(intersectionOf,df.getOWLNothing());
        set.add(subAxiom);
        tempDisjointArray.clear();
        return set;
    }
}