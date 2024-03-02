package org.example;

import javafx.util.Pair;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.*;

public class MyReasoner{

    private OWLOntology ontology;
    private OWLDataFactory df;
    private int universalTempCount = 0;
    private Set<OWLSubClassOfAxiom> normalizedAxiomsSet = null;
    private Map<OWLClassExpression,Set<OWLClassExpression>> S = null;
    private Map<OWLObjectPropertyExpression,Set<Pair<OWLClassExpression,OWLClassExpression>>> R = null;

    public MyReasoner(OWLOntology o) {
        this.ontology=o;
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        this.df = man.getOWLDataFactory();
        this.normalizedAxiomsSet = normalization();
        this.S = new HashMap<>();
        this.R = new HashMap<>();
        initializeMapping();
        System.out.println(this.S);
        System.out.println(this.R);
        //System.out.println(normalizedAxiomsSet);
    }

//    private void fit(){
//        Set<OWLClassExpression> set = new HashSet<>();
//        this.S = new HashMap<>();
//        for(OWLSubClassOfAxiom ax : normalizedAxiomsSet){
//            OWLClassExpression subClass = ax.getSubClass();
//            OWLClassExpression superClass = ax.getSuperClass();
//            if(ax.getAxiomType())
//        }
//    }

    private void initializeMapping() {
        for (OWLSubClassOfAxiom ax : normalizedAxiomsSet) {
            Set<OWLClassExpression> setS = new HashSet<>();
            Set<Pair<OWLClassExpression,OWLClassExpression>> setR = new HashSet<>();

            OWLClassExpression subClass = ax.getSubClass();
            OWLClassExpression superClass = ax.getSuperClass();
            initializeSingleMapping(subClass);
            initializeSingleMapping(superClass);
        }
    }

    private void initializeSingleMapping(OWLClassExpression expression) {
        Set<OWLClassExpression> setS = new HashSet<>();
        if (!expression.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
            setS.add(expression);
            setS.add(this.df.getOWLThing());
            S.put(expression, setS);
        } else {
            Set<Pair<OWLClassExpression, OWLClassExpression>> setR = new HashSet<>();
            OWLObjectSomeValuesFrom cast = (OWLObjectSomeValuesFrom) expression;
            R.put(cast.getProperty(), setR);
            setS.add(cast.getFiller()); //Aggiungo al setS la classe (o singleton) dell'esistenziale
            setS.add(this.df.getOWLThing()); //Aggiungo il TOP
            S.put(cast.getFiller(), setS); //Inserisco nella mappa S la classe (o singleton) dell'esistenziale e il setS creato per essa
        }
    }

    private void checkBottom(OWLClassExpression expression){
        Set<OWLClassExpression> set = new HashSet<>();
        set = expression.getNestedClassExpressions();
        for(OWLClassExpression ex : set){
            if(ex.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)){
                OWLClass cl = (OWLClass) ex;
                if(cl.isBottomEntity()){
                    throw new IllegalArgumentException("Trovato bottom in posizione non consentita");
                }
            }
        }
    }

    private void subAndSuperCheckBottom(OWLClassExpression subClass, OWLClassExpression superClass){
        checkBottom(subClass);
        if(!superClass.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)){
            checkBottom(superClass);
        }
    }

    private Set<OWLSubClassOfAxiom> normalization(){

        Set<OWLAxiom> subClassOfAxioms = this.ontology.getTBoxAxioms(Imports.EXCLUDED);
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> leftPair;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> rightPair;
        Set<OWLSubClassOfAxiom> resultSet = new HashSet<>();

        for(OWLAxiom ax : subClassOfAxioms){

            OWLSubClassOfAxiom cast = (OWLSubClassOfAxiom) ax;
            System.out.println(cast);
            OWLClassExpression subClass = cast.getSubClass();
            OWLClassExpression superClass = cast.getSuperClass();
            subAndSuperCheckBottom(subClass,superClass);

            leftPair = subClassNormalization(subClass);
            rightPair = superClassNormalization(superClass);

            resultSet.addAll(leftPair.getKey());    //Aggiungo al resultSet il set delle normalizzazioni
            resultSet.addAll(rightPair.getKey());   //Aggiungo al resultSet il set delle normalizzazioni
            if(leftPair.getValue().getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) &&
                    rightPair.getValue().getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                leftPair = reduceToClass(leftPair.getValue());
            }
            OWLSubClassOfAxiom normalizedSubClass = this.df.getOWLSubClassOfAxiom(leftPair.getValue(),rightPair.getValue());
            resultSet.add(normalizedSubClass);
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
        }
        return resultSet;
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> subClassNormalization(OWLClassExpression subClass){
        Set<OWLSubClassOfAxiom> set = new HashSet<>();
        ClassExpressionType typeSubClass = subClass.getClassExpressionType();
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair = null;

        if(typeSubClass.equals(ClassExpressionType.OWL_CLASS) || typeSubClass.equals(ClassExpressionType.OBJECT_ONE_OF)){ //Verifica se è una classe semplice
            returnPair = new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(set,subClass);
        } else if(typeSubClass.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)){ //Verifica se è intersezione
            OWLObjectIntersectionOf intersectionOf = (OWLObjectIntersectionOf) subClass;
            int subClassSize = intersectionOf.getOperandsAsList().size();
            returnPair = normalizeIntersectionOf(intersectionOf); //Il pair è ritornato dalla funzione chiamata
        }else if(typeSubClass.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
            OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) subClass;
            returnPair = normalizeObjectSomeValueFrom(objectSomeValuesFrom);
        }
        return returnPair;
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> superClassNormalization(OWLClassExpression superClass){
        Set<OWLSubClassOfAxiom> set = new HashSet<>();
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> tempPair;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair = null;

        if(superClass.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS) ||
                superClass.getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)){ //Verifica se è una classe semplice
            returnPair = new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(set,superClass);
        } else if(superClass.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)){ //Verifica se è intersezione
            OWLObjectIntersectionOf intersectionOf = (OWLObjectIntersectionOf) superClass;
            tempPair = normalizeIntersectionOf(intersectionOf);
            returnPair = reduceToClass(tempPair.getValue());
            returnPair.getKey().addAll(tempPair.getKey());
            return returnPair;
        }else if(superClass.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
            OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) superClass;
            returnPair = normalizeObjectSomeValueFrom(objectSomeValuesFrom);
        }
        return returnPair;
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> normalizeIntersectionOf(OWLObjectIntersectionOf intersectionOf){
        ArrayList<OWLClassExpression> arrayListOfExpressions = new ArrayList<>(intersectionOf.getOperandsAsList());
        int size = arrayListOfExpressions.size();

        List<OWLClassExpression> setTempClasses = new ArrayList<>();
        Set<OWLSubClassOfAxiom> returnSet = new HashSet<>();
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair = null;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> tempPair = null;

        if(size == 2){
            if(arrayListOfExpressions.get(0).getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) arrayListOfExpressions.get(0);
                tempPair = normalizeSomeValuesFromAsClass(objectSomeValuesFrom);
                returnSet.addAll(tempPair.getKey());
                arrayListOfExpressions.set(0,tempPair.getValue());
            }
            if(arrayListOfExpressions.get(1).getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) arrayListOfExpressions.get(1);
                tempPair = normalizeSomeValuesFromAsClass(objectSomeValuesFrom);
                returnSet.addAll(tempPair.getKey());
                arrayListOfExpressions.set(1,tempPair.getValue());
            }
            OWLObjectIntersectionOf newIntersectionOf = this.df.getOWLObjectIntersectionOf(arrayListOfExpressions.get(0),arrayListOfExpressions.get(1));
            return new Pair<>(returnSet,newIntersectionOf);
        }

        for(int i=0; i<size; i++){
            if(arrayListOfExpressions.get(i).getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) arrayListOfExpressions.get(i);
                tempPair = normalizeSomeValuesFromAsClass(objectSomeValuesFrom);
                returnSet.addAll(tempPair.getKey());
                arrayListOfExpressions.set(i,tempPair.getValue()); //La classe generata viene assegnata ad arrayListOfExpressions[i]
            }
            if(i%2!=0){
                OWLClass tempClass = createTempClass();
                setTempClasses.add(tempClass); //Necessario per creare intersezione per chiamata ricorsiva
                returnSet.addAll(normalizeSingleIntersectionOf(arrayListOfExpressions.get(i-1),arrayListOfExpressions.get(i),tempClass));
            }
        }

        if(size%2!=0){
            setTempClasses.add(arrayListOfExpressions.get(size-1));
        }

        OWLObjectIntersectionOf intersectionRecur = this.df.getOWLObjectIntersectionOf(setTempClasses); //Creo intersezione per ricorsione
        returnPair = normalizeIntersectionOf(intersectionRecur); //RICORSIONE
        returnPair.getKey().addAll(returnSet); //Aggiunta elementi al set (solo SubClasses)
        return returnPair;
    }

    //TORNA ESISTENZIALE DI UNA CLASSE (Exist(r.C))
    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> normalizeObjectSomeValueFrom(OWLObjectSomeValuesFrom someValuesFrom){
        OWLObjectPropertyExpression relation = someValuesFrom.getProperty();
        OWLClassExpression filler = someValuesFrom.getFiller();

        Set<OWLSubClassOfAxiom> returnSet = new HashSet<>();
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> tempPair = null;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> reduceToClassPair = null;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair = null;

        if(filler.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS) ||
                filler.getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)){
            return new Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression>(returnSet,someValuesFrom);
        }
        else if(filler.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)){
            tempPair = normalizeIntersectionOf((OWLObjectIntersectionOf) filler); //Torna pair di set e un and singolo
            reduceToClassPair = reduceToClass(tempPair.getValue()); //Prende l'and singolo e lo riduce ad una classe TEMP
            reduceToClassPair.getKey().addAll((tempPair.getKey()));
            OWLObjectSomeValuesFrom normalizedSomeValuesFrom = this.df.getOWLObjectSomeValuesFrom(relation,reduceToClassPair.getValue());
            returnPair = new Pair<>(reduceToClassPair.getKey(),normalizedSomeValuesFrom);
        }
        //REMINDER: TORNARE ESISTENZIALE DI CLASSE (REDUCETOCLASSPAIR POTREBBE ESSERE VUOTO DOPO IF)
        else if(filler.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
            tempPair = normalizeObjectSomeValueFrom((OWLObjectSomeValuesFrom) filler); //Torna un set e una classe temp o esistenziale
            OWLClassExpression expression = tempPair.getValue(); //Prendo l'espressione a destra della coppia (che è esistenziale di una classe)
            reduceToClassPair = new Pair<>(tempPair.getKey(),expression); //Inizializzo Pair con contenuto uguale a tempPair

            if(expression.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                reduceToClassPair = reduceToClass(expression); //trasformo l'esistenziale nuovo in una variabile temp
                tempPair.getKey().addAll(reduceToClassPair.getKey()); //Aggiungo nel Set gli assiomi di reduceToClass (se non entrato in if non aggiunge nulla)
            }
            //Creo esistenziale con quello di ora con il temp creato prima
            OWLObjectSomeValuesFrom normalizedSomeValuesFrom = this.df.getOWLObjectSomeValuesFrom(relation,reduceToClassPair.getValue());
            returnPair = new Pair<>(tempPair.getKey(),normalizedSomeValuesFrom); //Creo il Pair di ritorno con insieme di assiomi + esistenziale normalizzato
        }
        return returnPair;
    }

    private OWLClass createTempClass(){
        //IRI IOR = ontology.getOntologyID().getOntologyIRI().get();
        //OWLClass tempClass = this.df.getOWLClass(IRI.create(IOR + "#TEMP" + universalTempCount));
        OWLClass tempClass = this.df.getOWLClass(IRI.create("#TEMP" + universalTempCount));
        universalTempCount++;
        return tempClass;
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> normalizeSomeValuesFromAsClass(OWLObjectSomeValuesFrom objectSomeValuesFrom){
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> tempPair = null;
        Set<OWLSubClassOfAxiom> returnSet = new HashSet<>();

        tempPair = normalizeObjectSomeValueFrom(objectSomeValuesFrom); //Norm. Exist. torna Pair di assiomi e esistenziale (Exist(r.C))
        returnSet.addAll(tempPair.getKey()); //Aggiungo gli assiomi generati durante la normalizzazione al set di assiomi globale
        tempPair = reduceToClass(tempPair.getValue()); //Riduco a classe l'esistenziale attuale (perché siamo in una serie di and)
        returnSet.addAll(tempPair.getKey()); //Aggiungo gli assiomi generati durante la riduzione a classe dell'esistenziale

        return new Pair<>(returnSet,tempPair.getValue());
    }

    private Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> reduceToClass(OWLClassExpression expression){
        OWLClass tempClass = createTempClass();
        ArrayList<OWLClassExpression> arrayListOfExpressions;
        Pair<Set<OWLSubClassOfAxiom>,OWLClassExpression> returnPair = null;
        if(expression.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)){
            OWLObjectIntersectionOf intersectionOf = (OWLObjectIntersectionOf) expression;
            arrayListOfExpressions = new ArrayList<>(intersectionOf.getOperandsAsList());
            returnPair = new Pair<>(normalizeSingleIntersectionOf(arrayListOfExpressions.get(0),
                    arrayListOfExpressions.get(1),tempClass),tempClass);
        }
        else if(expression.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
            OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) expression;
            returnPair = new Pair<>(normalizeSingleObjectSomeValuesFrom(objectSomeValuesFrom,tempClass),tempClass);
        }
        return returnPair;
    }

    private Set<OWLSubClassOfAxiom> normalizeSingleObjectSomeValuesFrom(OWLObjectSomeValuesFrom objectSomeValuesFrom, OWLClass tempClass){
        Set<OWLSubClassOfAxiom> returnSet = new HashSet<>();
        OWLSubClassOfAxiom sub1 = this.df.getOWLSubClassOfAxiom(tempClass,objectSomeValuesFrom);
        OWLSubClassOfAxiom sub2 = this.df.getOWLSubClassOfAxiom(objectSomeValuesFrom,tempClass);
        returnSet.add(sub1);
        returnSet.add(sub2);
        return returnSet;
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