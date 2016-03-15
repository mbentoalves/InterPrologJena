/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import static IP_Jena.GenericLib.asSortedList;
import static IP_Jena.GenericLib.generatekey;
import static IP_Jena.GenericLib.newline;
import QueryContext.QC_RuleSet;
import QueryContext.QC_evalRules;
import QueryContext.QC_row;
import QueryContext2.QC2_evalRule;
import QueryContext2.QC2_rule;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author mba
 */
public class IP_callbacksContext extends IP_callbacks{
    
    RuleContext context = null;
    ArrayList<String> bnodes = new ArrayList<>();
    HashMap<String, Node> map_bn = new HashMap<>();
    
    public IP_callbacksContext(Map<String, String> prefixes) {
        this.prefixes = prefixes;
    }
    
    public IP_callbacksContext(RuleContext context) {
        this.context = context;
    }
    
    public IP_callbacksContext() {

    }

 
    public void setContext(RuleContext context) {
        this.context = context;
    }
    

    /*
    public String[] queryRDF(String pSubject, String pProperty, String pObject) {
        String retV[] = {
            "miguel", "fam:hasParent", "jose",
            "carla", "fam:hasParent", "jose"    
        };
        
        return retV;
    }
    */
    
    
    public String[] queryRDF(String pSubject, String pProperty, String pObject) {
        //System.out.println(pSubject +";"+pProperty+";"+pObject);
        
        String retV[] = null;

        if(context != null) {
            retV = queryRDFContext(pSubject, pProperty, pObject);
        }

        return retV;
    }
    
    public String[] queryRDF2(String pSubject, String pProperty, String pObject) {
        String retV[] = null;

        if(context != null) {
            retV = queryRDFContext(pSubject, pProperty, pObject);
        }
            
        return retV;
    }
    
    public String[] queryRDFContext(String pSubject, String pProperty, String pObject) {

        boolean[] isVar = new boolean[3];
        Node SubjNode = getFactoryNode2(pSubject, 1);
        Node PropNode = getFactoryNode2(pProperty, 1);
        Node ObjNode = getFactoryNode2(pObject, 2);

        isVar[0] = SubjNode.isVariable();
        isVar[1] = PropNode.isVariable();
        isVar[2] = ObjNode.isVariable();
        
        int nFreevars = (isVar[0] ? 1 : 0) + (isVar[1] ? 1 : 0) + (isVar[2] ? 1 : 0);
                        
        ArrayList<Triple> resultTriples= new ArrayList<>();
  
        
        Iterator<Triple> triples = context.find(SubjNode, PropNode, ObjNode);
        
        while(triples.hasNext()) {
            Triple t = triples.next();
            resultTriples.add(t);
            addbNodes(t.getSubject());
            addbNodes(t.getPredicate());
            addbNodes(t.getObject());
        }
        String retV[] = null;
        
        if(nFreevars == 0 && resultTriples.size() > 0){
            retV = new String[1];
            retV[0] = "true";
        }
        else{
            int nRows = resultTriples.size();
            if(nRows > 0){
                retV = new String[nRows * nFreevars];
                int posRec = 0;
                for(Triple t : resultTriples){
                    if(isVar[0]) {
                        retV[posRec++] = t.getSubject().toString();
                    }
                    if(isVar[1]) {
                        retV[posRec++] = t.getPredicate().toString();
                    }
                    if(isVar[2]) {                   
                        retV[posRec++] = t.getObject().toString();
                    }
                }
            }
        }
        return retV;
        
    }
    
    private void addbNodes(Node n) {
        if(n.isBlank() && !bnodes.contains(n.getBlankNodeLabel())) {
            bnodes.add(n.getBlankNodeLabel());
            map_bn.put(n.getBlankNodeLabel(), n);
        }
        
    }
    
    private Node getFactoryNode2(String value, Integer p){
        if(bnodes.contains(value)) {
            //return NodeFactory.createAnon(AnonId.create(value));
            return map_bn.get(value);
        }
        else {
            return getFactoryNode(value,p);
        }
    }
    
    public String[] queryRDF(String [] triples) {
        String retV[] = null;
    
        if(context != null) {
            retV = queryRDFContext(triples);
        }
            
        return retV;
    }
    
    public String[] queryRDFContext(String [] triples) {
        String retV[] = null;
        
        QC2_rule vQC2_rule = createQC2Rule(triples);
        QC2_evalRule vQC2_evalRule = new QC2_evalRule(vQC2_rule, context);
        ArrayList<HashMap<Object, String>> result = vQC2_evalRule.getResults();
        
        HashMap<String, String> keys = vQC2_rule.getExternalKeys();
                            
        if(result != null) {
            int nRows = result.size() * keys.size();
            if(nRows == 0) {
                retV = new String [2];
                retV[0] = "ask"; retV[1] = "true";                
            }
            else {
                retV = new String [nRows];
                int pos = 0;
                for(HashMap<Object, String> resultLine : result){
                    for(Entry<String, String> key : keys.entrySet()) {      
                        retV[pos++] = resultLine.get(key.getValue());
                        //retV[pos++] = res.getValue(key).toString();                   
                    }
                }
            }    
        }
        else {
            retV = new String [2];
            retV[0] = "ask"; retV[1] = "false";
        }

        return retV;
    }
    
    public String[] queryRDFContext_bak(String [] triples) {
        String retV[] = null;
        
        String rule = "";
        
        rule += getQCPrefixes();
   
        SparqlCmd sparqlCmd = new SparqlCmd();
        //HashMap<String, String> genkeys =  new HashMap<String, String>();
        
        createBodyRule(triples, sparqlCmd);
        
        Collection<String> tmpKeys = sparqlCmd.genKeys.keySet();
        List<String> keys = asSortedList(tmpKeys);
        
        String head = "";
        
        for(int i=0; i<keys.size();i++) {
            head += sparqlCmd.genKeys.get(keys.get(i)) + (i<(keys.size()-1) ? "," : ""); 
        }
        String query = null;
        if(head.length()>0){
            query = "result(" + head + ")";
            head = "result(" + head + ") <- ";
        }
        else {
            query = "result(1)";
            head = "result(1) <- ";
        }    

        rule = rule + head + sparqlCmd.cmd;

        QC_RuleSet ruleSet = new QC_RuleSet(rule);
        QC_evalRules vQC_evalRules = new QC_evalRules(context, ruleSet, query);
        
        ArrayList<QC_row> result = vQC_evalRules.getResult();
        
        if(result != null) {
            int nRows = result.size() * keys.size();
            if(nRows == 0) {
                retV = new String [2];
                retV[0] = "ask"; retV[1] = "true";                
            }
            else {
                retV = new String [nRows];
                int pos = 0;
                for(QC_row res : result){
                    for(String key : keys) {      
                        retV[pos++] = res.getValue(sparqlCmd.genKeys.get(key)).toString();
                        //retV[pos++] = res.getValue(key).toString();                   
                    }
                }
            }    
        }
        else {
            retV = new String [2];
            retV[0] = "ask"; retV[1] = "false";
        }

        return retV;
    }
    
    @Override
    public String getQCPrefixes() {
        String str_prefixes = "";
        
        if(prefixes != null) {
            for(Entry<String, String> prefix : prefixes.entrySet()){
                str_prefixes += "@prefix "+prefix.getKey() + ": <"+prefix.getValue()+"> ."+newline;
            }
        }
        return str_prefixes;
    }    
        
    @Override
    public void createBodyRule(String [] triples, SparqlCmd sparqlCmd) {
        
        HashMap<String, String> genkeys = sparqlCmd.genKeys;
        
        for(int i=0; i<triples.length; i=i+3) {
            String pSubject = triples[i];
            String pProperty = triples[i+1];
            String pObject = triples[i+2];
            
            //Subject
            if(pSubject.startsWith("null")) {
                String v = pSubject;
                if(genkeys.containsKey(v)) {
                    pSubject = genkeys.get(v);
                } else {
                    pSubject = "?" + generatekey(10);
                    genkeys.put(v, pSubject);
                }
            }
            
            //Property
            if(pProperty.startsWith("null")) {
                String v = pProperty;
                if(genkeys.containsKey(v)) {
                    pProperty = genkeys.get(v);
                } else {
                    pProperty = "?" + generatekey(10);
                    genkeys.put(v, pProperty);
                }
            }
 
            //Object
           if(pObject.startsWith("null")) {
                String v = pObject;
                if(genkeys.containsKey(v)) {
                    pObject = genkeys.get(v);
                } else {
                    pObject = "?" + generatekey(10);
                    genkeys.put(v, pObject);
                }
            }
           
            sparqlCmd.cmd += "(" + pSubject + " " + pProperty +" " + pObject+  ")"  + ",";
        }
        
        sparqlCmd.cmd = sparqlCmd.cmd.substring(0, sparqlCmd.cmd.length()-1) + ".";
        
        sparqlCmd.cmd += newline;
    }
        

      
    public QC2_rule createQC2Rule(String [] triples) {
        
        QC2_rule VQC2_rule = new QC2_rule();

        for(int i=0; i<triples.length; i=i+3) {
            Object pSubject = QC2RuleGenerateField(triples[i], VQC2_rule, 1);
            Object pProperty = QC2RuleGenerateField(triples[i+1], VQC2_rule, 1);
            Object pObject = QC2RuleGenerateField(triples[i+2], VQC2_rule, 2);

            VQC2_rule.addTriple(pSubject, pProperty, pObject);
            
        }
        return VQC2_rule; 
    }
    
    private Object QC2RuleGenerateField(String node, QC2_rule VQC2_rule, int type) {
        Object nodeRet = null;
        
        if(node.startsWith("null")){
            nodeRet = VQC2_rule.getExternalVarKey(node);
        }            
        else if(node.startsWith("?")) {
            nodeRet = VQC2_rule.getVarKey(node);
        }
        else {
            nodeRet = getFactoryNode2(node, type);
        }
        
        return nodeRet;
    }    
 
}
