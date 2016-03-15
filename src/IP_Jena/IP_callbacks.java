/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import static IP_Jena.GenericLib.asSortedList;
import static IP_Jena.GenericLib.generatekey;
import static IP_Jena.GenericLib.newline;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.sparql.util.NodeToLabelMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 *
 * @author mba
 */
public abstract class IP_callbacks {
    
    
    Map<String, String> prefixes = null;
    NodeToLabelMap bnodes = new NodeToLabelMap() ;

    
    public IP_callbacks(Map<String, String> prefixes) {
        this.prefixes = prefixes;
    }

    
    public IP_callbacks() {
        prefixes = new HashMap<>();
    }

    
    public void setPrefixes(Map<String, String> prefixes){
        this.prefixes = prefixes;
    }
    
    public Map<String, String> getPrefixes() {
        return this.prefixes;
    }

    public void registerPrefix(String prefix, String url){
        this.prefixes.put(prefix, url);
    }

    public void sendMsg(String msg){
        System.out.println(msg);
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
    
   
    protected Node getFactoryNode (String value, Integer p) {
        Node n = Node.ANY;
        
        if(value.startsWith("null")) {
            n = NodeFactory.createVariable(value);
        } 
        else {
            if(value.startsWith("http://")) {
                n = NodeFactory.createURI(value);
            }
            else if(value.contains(":-")) {
               n = NodeFactory.createAnon(AnonId.create(value));
            }
            /*
            else if(value.contains(":-")) {
                NodeFactory.
                n = NodeFactory.createAnon(value);
            }
            */        
            else if (p==2) {
                n = NodeFactory.createLiteral(value);            
            }
            else if (p==1) {
                n = NodeFactory.createAnon(AnonId.create(value));
            }
        }
        return n;
    }
    
  
    public String getQCPrefixes() {
        String str_prefixes = "";
        
        if(prefixes != null) {
            for(Entry<String, String> prefix : prefixes.entrySet()){
                str_prefixes += "@prefix "+prefix.getKey() + ": <"+prefix.getValue()+"> ."+newline;
            }
        }
        return str_prefixes;
    }    
        
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
        
    

    
    protected String[]  extractResult(ResultSet execSel, SparqlCmd sparqlCmd) {
        if(sparqlCmd.vars.size()>0) return extractResult1(execSel, sparqlCmd);
        else if(sparqlCmd.genKeys.size()>0) return extractResult2(execSel, sparqlCmd);
        else return null;
    }
    
    
    
    protected String[]  extractResult1(ResultSet execSel, SparqlCmd sparqlCmd) {
        String retV [] = null;
        ArrayList<QuerySolution> solutions = new ArrayList<QuerySolution>();
        while(execSel.hasNext()) solutions.add(execSel.next());
         
        int nRec = solutions.size();
        if(nRec > 0) {
            retV = new String[nRec * sparqlCmd.vars.size()];
            int posRec = 0;
            for(QuerySolution q : solutions) {
                for(String field : sparqlCmd.vars) {
                    if(field.startsWith("?")) {
                        if(q.get(field.substring(1)).isAnon()){
                            retV[posRec] = bnodes.asString(q.get(field.substring(1)).asNode());
                            //retV[posRec] = q.get(field.substring(1)).toString();
                        }
                        else {
                            retV[posRec] = q.get(field.substring(1)).toString();
                        }
                    }
                    else {
                        retV[posRec] = field;
                    }
                    posRec++;
                }
            }
        }
        return retV;
    }
    
    protected String[]  extractResult2_tmp(ResultSet execSel, SparqlCmd sparqlCmd) {
        String retV [] = null;
        int nFields = execSel.getResultVars().size();
        ArrayList<QuerySolution> solutions = new ArrayList<>();
        while(execSel.hasNext()) solutions.add(execSel.next());
         
        int nRec = solutions.size();
        
        Collection<String> tmpKeys = sparqlCmd.genKeys.keySet();
        List<String> keys = asSortedList(tmpKeys);
         
        if(nRec > 0) {
            retV = new String[nRec * nFields];
            int posRec = 0;
            for(QuerySolution q : solutions) {
                for(int i=0; i<keys.size(); i++) {
                    String fieldOut = keys.get(i);
                    String fieldIn = sparqlCmd.genKeys.get(fieldOut);
                    if(q.get(fieldIn).isAnon()){
                        retV[posRec] = bnodes.asString(q.get(fieldIn).asNode());
                    }
                    else {
                        retV[posRec] = q.get(fieldIn).toString();
                    }
                    posRec++;
                }
            }
        }
  
        return retV;
    }
     
    protected String[] extractResult2(ResultSet execSel, SparqlCmd sparqlCmd) {
        Collection<String> tmpKeys = sparqlCmd.genKeys.keySet();
        List<String> keys = asSortedList(tmpKeys);
        
        String retV [] = null;
        ArrayList<String> retVAL = new ArrayList<String>();
        while(execSel.hasNext()){
            QuerySolution q = execSel.next();
            for(int i=0; i<keys.size(); i++) {
                String fieldOut = keys.get(i);
                String fieldIn = sparqlCmd.genKeys.get(fieldOut);
                if(q.get(fieldIn).isAnon()){
                    retVAL.add(bnodes.asString(q.get(fieldIn).asNode()));
                }
                else {
                    String v = q.get(fieldIn).toString();
                    v = replaceUrlPrefix(v);
                    retVAL.add(v);
                }
            }
        }
        
        retV = new String[retVAL.size()];
        int posVect = 0;
        for(String str:retVAL) {
            retV[posVect++] = str;
        }
          
        return retV;
    }
    
    protected String[] extractResult3(ResultSet execSel) {

        String retV [] = null;
        List<String> ResultVars = execSel.getResultVars();
        int nFields = ResultVars.size();
        ArrayList<String> retVAL = new ArrayList<String>();
        while(execSel.hasNext()){
            QuerySolution q = execSel.next();
            for(String field : ResultVars) {
                if(q.get(field).isAnon()){
                    retVAL.add(bnodes.asString(q.get(field).asNode()));
                }
                else {
                    String v = q.get(field).toString();
                    v = replaceUrlPrefix(v);
                    retVAL.add(v);
                }
                
            }             
        }
        
        retV = new String[retVAL.size()+ 1];
        retV[0] = String.valueOf(nFields);
        int posVect = 0;
        for(String str:retVAL) {
            retV[++posVect] = str;
        }
          
        return retV;
    }
    
               
    protected String[] extractResult3_tmp2(ResultSet execSel) {

        String retV [] = null;
        List<String> ResultVars = execSel.getResultVars();
        int nFields = ResultVars.size();
        ArrayList<QuerySolution> solutions = new ArrayList<QuerySolution>();
        while(execSel.hasNext()) solutions.add(execSel.next());
         
        int nRec = solutions.size();
          
        if(nRec > 0) {
            retV = new String[nRec * nFields + 1];
            retV[0] = String.valueOf(nFields);
            int posRec = 1;
            for(QuerySolution q : solutions) {
                for(String field : ResultVars) {
                    if(q.get(field).isAnon()){
                        retV[posRec] = bnodes.asString(q.get(field).asNode());
                    }
                    else {
                        retV[posRec] = q.get(field).toString();
                    }
                    posRec++;
                }
            }
        }
  
        return retV;
    }            
     
    private String replaceUrlPrefix(String v) {
        if(v.startsWith("<") && v.endsWith(">")) {
            v = v.substring(1, v.length()-1);
        }
        for(Entry<String, String> prefix : prefixes.entrySet()){
            if(v.startsWith(prefix.getValue())){
                v = v.replace(prefix.getValue(), prefix.getKey()+":");
            }
        }
        return v;
    }
}
