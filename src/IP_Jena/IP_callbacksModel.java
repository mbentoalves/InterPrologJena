/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import static IP_Jena.GenericLib.generatekey;
import static IP_Jena.GenericLib.newline;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author mba
 */
public class IP_callbacksModel extends IP_callbacks{

    InfModel infModel = null;

    public IP_callbacksModel(InfModel infModel) {
        this.infModel = infModel;
        setPrefixes();
    }
    
    public IP_callbacksModel(Map<String, String> prefixes) {
        this.prefixes = prefixes;
    }
    
    public IP_callbacksModel() {
    }

    public long[] getPrologTime() {
        long[] retV = {0, 0}; //To send the values of nanotime back
        return retV;
    }
    
    public boolean testIsClosed() {
        return infModel.isClosed();
    }

    public InfModel getInfModel() {
        return infModel;
    }
    
    private void setPrefixes() {
        HashMap<String, String> prefixes = new HashMap<String, String>();
        if(infModel != null){
            for (Entry<String, String> prefix :  infModel.getNsPrefixMap().entrySet()){
                prefixes.put(prefix.getKey(), prefix.getValue());
            }
            setPrefixes(prefixes);
        }
    }
    
    public void setInfModel(InfModel infModel) {
        this.infModel = infModel;
    }

    public String[] isNotBlankNode(String r){
        String retV[] = null;
        
        
        if(!(r.contains(":-") || r.contains("_:"))){
            String retV2[] = {"true"};
            retV = retV2;
        }
        
        return retV;
    }
    
    public String[] isResource(String r){
        String retV[] = null;
        
        
        if(!infModel.getResource(r).toString().startsWith("_:")){
            String retV2[] = {"true"};
            retV = retV2;
        }
        
        return retV;
    }
    
    public String[] queryRDF(String pSubject, String pProperty, String pObject) {
        String retV[] = null;
        
        if(infModel != null) {
           retV = queryRDFModel(pSubject, pProperty, pObject);
           //retV = queryRDFModel2(pSubject, pProperty, pObject);
        }
        /*
        if(retV != null && retV.length > 0){
            System.out.println(pSubject+";"+pProperty+";"+pObject);
            System.out.println("Length -> "+retV.length);
        }
        */
        return retV;
    }

    public String[] queryRDF2(String pSubject, String pProperty, String pObject) {
        String retV[] = null;

        
        if(infModel != null) {
           //retV = queryRDFModel(pSubject, pProperty, pObject);
           retV = queryRDFModel2(pSubject, pProperty, pObject);
        }
        /*
        if(retV != null && retV.length > 0){
            System.out.println(pSubject+";"+pProperty+";"+pObject);
            System.out.println("Length -> "+retV.length);
        }
        */
        return retV;
    }

    
    public String[] queryRDFModel2(String pSubject, String pProperty, String pObject){
        String retV [] = null;
        
        //System.out.println(pSubject+";"+pProperty+";"+pObject);
        
                    
        Resource s = null;
        Property p = null;
        RDFNode o = null;
        
        if(!(pSubject.compareTo("null")==0)){
            if(pSubject.startsWith("<") && pSubject.endsWith(">")) {
                pSubject = pSubject.substring(1).substring(0, pSubject.length()-2);
            }
            s = infModel.getResource(pSubject);
        }
        
        if(!(pProperty.compareTo("null")==0)){
            if(pProperty.startsWith("<") && pProperty.endsWith(">")) {
                pProperty = pProperty.substring(1).substring(0, pProperty.length()-2);
            }
            p = infModel.getProperty(pProperty);
        }
        
        if(!(pObject.compareTo("null")==0)){
            if(pObject.startsWith("<") && pObject.endsWith(">")) {
                pObject = pObject.substring(1).substring(0, pObject.length()-2);
            }
            Node n = null;
            if(pObject.contains(":-"))
                n = NodeFactory.createAnon(AnonId.create(pObject));
            else if(pObject.contains(":")){
                n = NodeFactory.createURI(pObject);
                /*
                if(model.containsResource(model.asRDFNode(n_uri))){
                    n = n_uri;
                }
                */
            }
            else {
                n = NodeFactory.createVariable(pObject);
            }
            
            o = infModel.getRDFNode(n);
        }
       
        if(s!=null && p!= null && o != null){
            if(infModel.contains(s, p, o)){
                retV = new String[1];
                retV[0] = "true";
            }
        }
        else {
            Selector selector = new SimpleSelector(s, p, o);
            ArrayList<Statement> l1 = new ArrayList<>();

            StmtIterator iter = infModel.listStatements(selector);

            int nRec =0;
            while(iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                l1.add(stmt);
                nRec++;
            } 
            int nNullFields = ((s==null?1:0) + (p==null?1:0)+(o==null?1:0));
            retV = new String[nRec * nNullFields];
            int i=0;
            for(Statement stmt : l1){
                int i2 = 0;
                if(s==null) {
                    retV[i] = stmt.getSubject().toString();
                    i2++;
                }
                if(p==null){
                    retV[i+i2] = stmt.getPredicate().toString();
                    i2++;
                }
                if(o==null){
                    retV[i+i2] = stmt.getObject().toString();
                }
                i+=nNullFields;
            }

            
        }
        
        return retV;
    }
    
    public String[] queryRDFModel(String pSubject, String pProperty, String pObject) {
        String retV[] = null;
  
        /*
        System.out.println(pSubject + ";" + pProperty + ";" + pObject);
        if(pSubject.compareTo("null")!=0 && !pSubject.startsWith("http") && !pSubject.startsWith("<http")) {
        */
            
        //if(pSubject.compareTo("null")!=0 && model.containsResource(model.asRDFNode(NodeFactory.createURI(pSubject)))) {
            SparqlCmd sparqlCmd = constructSparqlCmd(pSubject, pProperty, pObject);

            if(sparqlCmd.ask) {
                if(executeAskCommand(sparqlCmd)) {
                    //String retV2[] = {pSubject, pProperty, pObject};
                    String retV2[] = {"true"};
                    retV = retV2;
                }
            } else {
                ResultSet execSel = executeSelectCommand(sparqlCmd);
                retV = extractResult(execSel, sparqlCmd);
            }
        //}
        
        return retV;
        
    }
    
    public String[] queryRDF(String [] triples) {
        String retV[] = null;
        
        if(infModel != null) {
            retV = queryRDFModel(triples);
        }
 
        return retV;
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
        
    
    
    public String[] queryRDFModel(String [] triples) {
        String retV[] = null;
        SparqlCmd sparqlCmd = constructSparqlCmd(triples);
        if(sparqlCmd.ask) {
            boolean ans = executeAskCommand(sparqlCmd);
            String retV2[] = {"ask", String.valueOf(ans)};
            retV = retV2;
        } else {           
            ResultSet execSel = executeSelectCommand(sparqlCmd);
            retV = extractResult(execSel, sparqlCmd);
        }      
        return retV;
        
    }
    
    public String[] execSparql1(String p_sparqlCmd) {
        String retV[] = new String[1];
        
        Object execSel = executeSparqlCmd(p_sparqlCmd);

        if(execSel instanceof Boolean) {
            retV[0] = String.valueOf((boolean) execSel);
        }
        else if(execSel instanceof String){
            retV[0] = (String) execSel;
        }
       
        return retV;
    }
    
    public String[] execSparql(String p_sparqlCmd) {
        String retV[] = null;
        Object execSel = executeSparqlCmd(p_sparqlCmd);
        if(execSel instanceof Boolean) {
            retV = new String[1];
            retV[0] = String.valueOf((boolean) execSel);
        }
        else if(execSel instanceof ResultSet){
            retV = extractResult3((ResultSet) execSel);
        }
        
        return retV;
    }       
    
    private boolean executeAskCommand (SparqlCmd sparqlCmd) {
        QuerySolutionMap vQuerySolutionMap = createQuerySolutionMap(sparqlCmd, infModel);
        //System.out.println(sparqlCmd);
        QueryExecution qe 
                   = QueryExecutionFactory.create(sparqlCmd.cmd, infModel, vQuerySolutionMap);
        return qe.execAsk();
    }
    
    private ResultSet executeSelectCommand(SparqlCmd sparqlCmd) {
        QuerySolutionMap vQuerySolutionMap = createQuerySolutionMap(sparqlCmd, infModel);
        QueryExecution qe 
                   = QueryExecutionFactory.create(sparqlCmd.cmd, infModel, vQuerySolutionMap);


        return qe.execSelect();
        
    }
    
    private QuerySolutionMap createQuerySolutionMap(SparqlCmd sparqlCmd, Model model){
        QuerySolutionMap vQuerySolutionMap = new QuerySolutionMap();
        for(Entry<String, String> entry : sparqlCmd.instantiatedValues.entrySet()){
            Node n = getNodeToSelect(model, entry.getValue());
            vQuerySolutionMap.add(entry.getKey(), model.asRDFNode(n));
        }
        return vQuerySolutionMap;
    }
    
    private Object executeSparqlCmd1(String sparqlCmd) {
        QueryExecution qe 
                   = QueryExecutionFactory.create(sparqlCmd, infModel);
        if(qe.getQuery().isAskType()) return qe.execAsk();
        else if(qe.getQuery().isSelectType()) {
            ResultSet rs = qe.execSelect();
            if(rs.hasNext()) {
                return "true";
            }
            else {
                return null;
            }
        }
        else return null;
    }
    
    private Object executeSparqlCmd(String sparqlCmd) {
        QueryExecution qe 
                   = QueryExecutionFactory.create(sparqlCmd, infModel);
        if(qe.getQuery().isAskType()) return qe.execAsk();
        else if(qe.getQuery().isSelectType()) return qe.execSelect();
        else return null;
    }            
            
    private SparqlCmd constructSparqlCmd(String pSubject, String pProperty, String pObject){
        SparqlCmd sparqlCmd = new SparqlCmd();
        
        String lSelect = "";
        String lWhere = "";
        
        if(pSubject.compareTo("null")==0) {
            String newVar = generatekey(10);
            lSelect += "?" + newVar + " ";
            lWhere += "?" + newVar+ " ";
            sparqlCmd.vars.add("?" + newVar);
        } 
        else {
            if(pSubject.startsWith("http://")){
                lWhere += "<" + pSubject + ">" + " ";
            }
            else {
                //lWhere += pSubject + " ";  
                Node nS = getNodeToSelect(infModel, pSubject);
                if(nS.isBlank())
                    lWhere += nS.getBlankNodeId().getLabelString();  
                else
                    lWhere += pSubject + " ";  
            }        
        }
        
        if(pProperty.compareTo("null")==0) {
            String newVar = generatekey(10);
            lSelect += "?" + newVar + " ";
            lWhere += "?" + newVar+ " ";
            sparqlCmd.vars.add("?" + newVar);
        }
        else {
            if(pProperty.startsWith("http://")){
                lWhere += "<" + pProperty + ">" + " ";
            }
            else {
                lWhere += pProperty + " ";       
            }        
        }
        

        if(pObject.compareTo("null")==0) {
            String newVar = generatekey(10);
            lSelect += "?" + newVar + " ";
            lWhere += "?" + newVar+ " ";
            sparqlCmd.vars.add("?" + newVar);
        }
        else {
            if(pObject.startsWith("http://")){
                lWhere += "<" + pObject + ">" + " ";
            }
            else {
                lWhere += pObject + " ";        
            }        
        }
        
        if(lSelect.length()==0) {
            sparqlCmd.ask = true;
            sparqlCmd.cmd = " ASK {" + lWhere + "}";
        } else {
            sparqlCmd.cmd = " SELECT "  + lSelect + " WHERE {" + lWhere + "}";
        }
        
        return sparqlCmd;
    }
    private class Param1 {
        String lSelect = "";
        String lWhere = "";
    }
    
    private void constructSparqlCmd (String r, Param1 param, SparqlCmd sparqlCmd) {
        if(r.startsWith("null")) {
            String v = r;
            String var = null;
            if(sparqlCmd.genKeys.containsKey(v)) {
                var = sparqlCmd.genKeys.get(v);
            } else {
                var = generatekey(10);
                sparqlCmd.genKeys.put(v, var);
            }
            param.lSelect += "?" + var + " ";
            param.lWhere += "?" + var+ " ";
        }    
        else if(r.startsWith("?")) {
            param.lWhere += r + " ";
        } 
        else if(r.contains("http:") || r.contains(":")){
            if(r.contains("http:") && !r.startsWith("<")){
                param.lWhere += "<" + r + "> ";
            }
            else {
                param.lWhere += r + " ";
            }
        }
        else {
            String var = generatekey(10);
            param.lWhere += "?" + var+ " ";
            sparqlCmd.setVarValue(var, r);
        }
        
        /*
        else if(r.startsWith("_:")) {
            param.lWhere += r + " ";
        }    
        else{
            param.lWhere += "<" + r + "> ";
        }
                */
    }
    
    private SparqlCmd constructSparqlCmd(String [] triples){
        SparqlCmd sparqlCmd = new SparqlCmd();
        
        Param1 param = new Param1();
        
         for(int i=0; i<triples.length; i=i+3) {
            String pSubject = triples[i];
            String pProperty = triples[i+1];
            String pObject = triples[i+2];
            
            constructSparqlCmd(pSubject, param, sparqlCmd);
            constructSparqlCmd(pProperty, param, sparqlCmd);
            constructSparqlCmd(pObject, param, sparqlCmd);
            
            param.lWhere += " . ";
        }
        if(param.lSelect.length()==0) {
            sparqlCmd.ask = true;
            sparqlCmd.cmd = " ASK {" + param.lWhere + "}";
        } else {
            sparqlCmd.cmd = " SELECT "  + param.lSelect + " WHERE {" + param.lWhere + "}";
        }
        
        return sparqlCmd;
    }
    
    private Node getNodeToSelect(Model m, String v) {
        Node n; 
        Node n_bn = NodeFactory.createAnon(AnonId.create(v));
        if(m.containsResource(m.asRDFNode(n_bn))) {
            n = n_bn;
        }
        else {
            Node n_uri = NodeFactory.createURI(v);
            if(m.containsResource(m.asRDFNode(n_uri))){
                n = n_uri;
            }
            else if (!v.contains(":")){
                n = NodeFactory.createVariable(v);
            }
            else {
                n = n_uri;
            }
        }
        return n;
     }
    
}
