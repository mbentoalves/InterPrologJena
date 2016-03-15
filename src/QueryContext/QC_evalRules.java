/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import static QueryContext.QC_BuiltIn.getClassBuiltInFunction;
import static QueryContext.QC_genericLib.getClauseInstValues;
import static QueryContext.QC_genericLib.getClauseVars;
import static QueryContext.QC_genericLib.getInternalName;
import static QueryContext.QC_genericLib.getVars3;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mba
 */
public class QC_evalRules {
    private RuleContext context;
    private String [] getVars;
    QC_RuleSet ruleSet;
    String query;
    
    public QC_evalRules(RuleContext context, QC_RuleSet ruleSet, String query) {
        this.context = context;
        this.getVars = getVars3(query);
        this.ruleSet = ruleSet;
        this.query = query;
    }
    
    
    public ArrayList<QC_row> getResult() {
        HashMap<Integer, Node> headInstValues = getClauseInstValues(query);
        HashMap<Integer, String> headVars = getClauseVars(query);

        String ruleToExecute = getInternalName(query);        
        ArrayList<QC_row> retV = execRule(ruleToExecute, headInstValues, headVars);
        
        return retV;
    }

    private ArrayList<QC_row> execBuilInFunction(String builtFunctionToExecute, HashMap<Integer, Node> headInstValues, HashMap<Integer, String> headVars){
        ArrayList<QC_row> retV = null;
        

        ArrayList<Node> funtionParams = new ArrayList<Node>();
        
        for(Entry<Integer, Node> entry : headInstValues.entrySet()){
            funtionParams.add(entry.getKey()-1, entry.getValue());
        }
        
        for(int i : headVars.keySet()) {
            funtionParams.add(i-1, Node.ANY);
        }
        
        if(execBuilInFunction(builtFunctionToExecute, funtionParams)) {
            retV = new ArrayList<QC_row>();
            
            if(headVars.size()>0) {
                QC_row newRow = new QC_row();
                for(int i : headVars.keySet()) {
                    newRow.insertValue(headVars.get(i), funtionParams.get(i));
                }
                retV.add(newRow);
            }

        }    
        
        return retV;
    }
        
    private boolean execBuilInFunction(String builtFunctionToExecute, ArrayList<Node> funtionParams) {

        boolean retV = false;
        
        //QC_BuiltInFunctions.notEqual v = new QC_BuiltInFunctions.notEqual();

        String classBuiltInFunction = getClassBuiltInFunction(builtFunctionToExecute);

        try {
            Object obj = Class.forName(classBuiltInFunction).newInstance();
            Method method = obj.getClass().getMethod("bodyCall", funtionParams.getClass());
            Object ret = method.invoke(obj, funtionParams);
            retV = ret.equals(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(QC_evalRules.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retV;
    }
    
    private ArrayList<QC_row> execRule(String ruleToExecute, HashMap<Integer, Node> headInstValues, HashMap<Integer, String> headVars) {
        ArrayList<QC_row> result = null;
        QC_rule qcRule = ruleSet.getQCRule(ruleToExecute);
        
        for(QC_InternalRule rule : qcRule.getRules()) {
            ArrayList<QC_row> tmpresult = execRule(rule, headInstValues);
            
            ArrayList<String> vars = rule.getVariables();
            if(tmpresult != null) {
                if(result == null) {
                    result = new ArrayList<QC_row>();
                }
                for(QC_row row : tmpresult) {
                    QC_row newRow = new QC_row();

                    for(Entry<String, Node> entry : row.getRow().entrySet()) {
                        int posIndex = vars.indexOf(entry.getKey()) + 1;
                        if(headVars.containsKey(posIndex)){
                            newRow.insertValue(headVars.get(posIndex), entry.getValue());
                        }
                    }
                    if(!result.contains(newRow)){
                        result.add(newRow);
                    }
                }
            }
        }
        
        return result;
    }
    
    private ArrayList<QC_row> execRule(QC_InternalRule rule, HashMap<Integer, Node> headInstValues){
        ArrayList<QC_row> retV = new ArrayList<QC_row>();
        
        QC_row row = new QC_row();
        
        for(Entry<Integer, Node> entry : headInstValues.entrySet()){
            row.insertValue(rule.variables.get(entry.getKey()-1), entry.getValue());
        }
        
        if (getResult(retV, rule.getRuleBody(), row, 0)) {
            return retV;
        }
        else {
            return null;
        }
    }
     
    private boolean getResult(ArrayList<QC_row> result, QC_ruleBody ruleBody, QC_row row, int nClause){
        boolean retV = false;
        if(nClause == ruleBody.getNClauses()) {
            result.add(row);
            retV = true;
        }
        else {
            QC_RuleClause ruleClause = ruleBody.getClause(nClause);
            ArrayList<QC_row> resultTmp2 = null;
            if(ruleClause instanceof QC_BuiltIn) {
                QC_BuiltIn ruleClauseRule = (QC_BuiltIn) ruleClause;
                ArrayList<String> vars = ruleClauseRule.getVariables();
                ArrayList<Integer> usedVars = new ArrayList<Integer>();
                
                HashMap<Integer, Node> headInstValues = new HashMap<Integer, Node>();
                for(Entry<String, Node> entry : row.getRow().entrySet()){
                    if(vars.contains(entry.getKey())) {
                        headInstValues.put(vars.indexOf(entry.getKey())+1, entry.getValue());
                        usedVars.add(vars.indexOf(entry.getKey()));
                    }
                }
                
                HashMap<Integer, String> headVars = new HashMap<Integer, String>();
                
                for(int i=0; i<vars.size(); i++) {
                    if(!usedVars.contains(i)) {
                        headVars.put(i+1, vars.get(i));
                    }
                }
                
                resultTmp2 = execBuilInFunction(ruleClauseRule.getFunctionName(), headInstValues, headVars);
                
            }
            else if(ruleClause instanceof QC_RuleClauseRule) {
                QC_RuleClauseRule ruleClauseRule = (QC_RuleClauseRule) ruleClause;
                ArrayList<String> vars = ruleClauseRule.getVariables();
                ArrayList<Integer> usedVars = new ArrayList<Integer>();
                
                HashMap<Integer, Node> headInstValues = new HashMap<Integer, Node>();
                for(Entry<String, Node> entry : row.getRow().entrySet()){
                    if(vars.contains(entry.getKey())) {
                        headInstValues.put(vars.indexOf(entry.getKey())+1, entry.getValue());
                        usedVars.add(vars.indexOf(entry.getKey()));
                    }
                }
                
                HashMap<Integer, String> headVars = new HashMap<Integer, String>();
                
                for(int i=0; i<vars.size(); i++) {
                    if(!usedVars.contains(i)) {
                        headVars.put(i+1, vars.get(i));
                    }
                }
                
                resultTmp2 = execRule(ruleClauseRule.getRuleName(), headInstValues, headVars);
            }
            else if(ruleClause instanceof QC_Triple) {
                QC_Triple newTriple = instatiateTriple(row, (QC_Triple) ruleClause);
                //System.out.println(newTriple);
                resultTmp2 = executeTriple(newTriple);
            }
            if(resultTmp2 != null) {
                if(resultTmp2.size() > 0) {
                    for(QC_row tmpResult2 : resultTmp2) {
                        QC_row newRow = new QC_row();
                        newRow.insertValue(row);
                        newRow.insertValue(tmpResult2);
                        boolean res = getResult(result, ruleBody, newRow, nClause + 1);
                        retV = retV || res;
                    }
                }
                else {
                    retV = getResult(result, ruleBody, row, nClause + 1);
                }
            }

        }
        return retV;
    }
    
 
    private ArrayList<QC_row> executeTriple(QC_Triple triple) {
        ArrayList<QC_row> retResult = null;
        
        String fields [] = {"", "", ""};
        
        Node subject = Node.ANY;
        Node property = Node.ANY;
        Node object = Node.ANY;
        
        Node subject2 = Node.ANY;
        
        if(triple.subjectIsNode()) {
            if(triple.getSubjectNode().toString().contains(":-")) {
                subject = NodeFactory.createAnon(AnonId.create(triple.getSubjectNode().toString()));
            }
            else {
                subject = triple.getSubjectNode();
            }
        }
        else {
            fields[0] = triple.getSubjectId();
        }
        
        if(triple.propertyIsNode()) {
            if(triple.getPropertyNode().toString().contains(":-")) {
                property = NodeFactory.createAnon(AnonId.create(triple.getPropertyNode().toString()));
            }
            else {
                property = triple.getPropertyNode();
            }
        }
        else {
            fields[1] = triple.getPropertyId();
        }
        
        if(triple.objectIsNode()) {
          if(triple.getObjectNode().toString().contains(":-")) {
                object = NodeFactory.createAnon(AnonId.create(triple.getObjectNode().toString()));
            }
            else {
                object = triple.getObjectNode();
            }
 
        }
        else {
            fields[2] = triple.getObjectId();
        }
 
        /*
        Node ntmp = NodeFactory.createURI("http://www.example.org/myEvent#Event1");
        
        ClosableIterator<Triple> getTriples2 = context.find(ntmp, Node.ANY, Node.ANY);
        
        System.out.println("tmp1 --- ");
        System.out.println("inic --- "+triple.toString());
        while(getTriples2.hasNext()) {
            Triple t = getTriples2.next();
            System.out.println(t.toString());
            Node obj = t.getObject();
            if(obj.isBlank()) {
                 System.out.println("Test1");
                ClosableIterator<Triple> getTriples3 = context.find(obj, Node.ANY, Node.ANY);
                while(getTriples3.hasNext()) {
                    Triple t3 = getTriples3.next();
                    System.out.println("    " + t3.toString());
                }  
                Node obj2 = NodeFactory.createAnon(AnonId.create(obj.toString()));
                
                System.out.println("Test2");
                ClosableIterator<Triple> getTriples4 = context.find(obj2, Node.ANY, Node.ANY);
                while(getTriples4.hasNext()) {
                    Triple t4 = getTriples4.next();
                    System.out.println("    " + t4.toString());
                }  
                System.out.println("end test2");
                
            }
        }
        System.out.println("tmp1 --- ");
        
        */

        /*
        Node p1 = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Node o1 = NodeFactory.createURI("http://www.example.org/EventTime#TemporalInterval");
        ClosableIterator<Triple> getTriples3 = context.find(Node.ANY, Node.ANY, Node.ANY);
        System.out.println("begin*******getTriples3**********");
        int ii=0;
        if(getTriples3.hasNext()) {
            while(getTriples3.hasNext()){
                System.out.println(getTriples3.next().toString());
                ii++;
            }
        }
        System.out.println("end*******getTriples3**********");
        */        
        
        ClosableIterator<Triple> getTriples = context.find(subject, property, object);
        
        if(getTriples.hasNext()) {
            retResult = new ArrayList<QC_row>();       
            while(getTriples.hasNext()){
                QC_row newRow = new QC_row();
                Triple new_t = getTriples.next();
                if(fields[0].length()>0) {
                    newRow.insertValue(fields[0], new_t.getSubject());
                }
                if(fields[1].length()>0) {
                    newRow.insertValue(fields[1], new_t.getPredicate());
                }
                if(fields[2].length()>0) {
                    newRow.insertValue(fields[2], new_t.getObject());
                }
                retResult.add(newRow);
            }
        }
        
        return retResult;
    }
    
    private QC_Triple instatiateTriple(QC_row row, QC_Triple triple) {
        QC_Triple newTriple = new QC_Triple();
        
        Object subject = triple.getSubject();
        if(subject instanceof Node) {
            newTriple.setSubject(subject);
        }
        else {
            String field = (String) subject;
            if(row.hasField(field)) {
                newTriple.setSubject(row.getValue(field));
            }
            else {
                newTriple.setSubject(field);
            }
        }

        Object property = triple.getProperty();
        if(property instanceof Node) {
            newTriple.setProperty(property);
        }
        else {
            String field = (String) property;
            if(row.hasField(field)) {
                newTriple.setProperty(row.getValue(field));
            }
            else {
                newTriple.setProperty(field);
            }
        }
        
        Object object = triple.getObject();
        if(object instanceof Node) {
            newTriple.setObject(object);
        }
        else {
            String field = (String) object;
            if(row.hasField(field)) {
                newTriple.setObject(row.getValue(field));
            }
            else {
                newTriple.setObject(field);
            }
        }
        return newTriple;
    }
    
  
}
