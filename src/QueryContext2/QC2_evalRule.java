/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext2;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author mba
 */
public class QC2_evalRule {
    private final QC2_rule vQC2_rule;
    private final RuleContext context;
    ArrayList<HashMap<Object, Node>> results;
    
    public QC2_evalRule(QC2_rule vQC2_rule, RuleContext context) {
        this.vQC2_rule = vQC2_rule;
        this.context = context;
        evalRule();
    }
    
    public void evalRule() {
        results = evalRule(1, new HashMap<Object, Node>());
    }
    
    private ArrayList<HashMap<Object, Node>> evalRule(int term, HashMap<Object, Node> tmpResults){
        ArrayList<HashMap<Object, Node>> retV = 
           new ArrayList<HashMap<Object, Node>>();
        
        
        QC2_triple vQC2_triple = vQC2_rule.getTriple(term);
        
        if(vQC2_triple != null) {
            Node subject = getSearchNode(vQC2_triple.getSubject(), tmpResults);
            Node property = getSearchNode(vQC2_triple.getProperty(), tmpResults);
            Node object = getSearchNode(vQC2_triple.getObject(), tmpResults);

            ClosableIterator<Triple> getTriples = context.find(subject, property, object);

            if(getTriples.hasNext()){
                while(getTriples.hasNext()) {
                    Triple t = getTriples.next();
                    HashMap<Object, Node> tmpResults2 = new HashMap<Object, Node> ();
                    tmpResults2.putAll(tmpResults);
                    addTmpresult(vQC2_triple.getSubject(), tmpResults2, t.getSubject());
                    addTmpresult(vQC2_triple.getProperty(), tmpResults2, t.getPredicate());
                    addTmpresult(vQC2_triple.getObject(), tmpResults2, t.getObject());
                    retV.addAll(evalRule(term+1, tmpResults2));
                 }
            }
        }
        else {
            retV.add(tmpResults);
        }
        return retV;
    }
    
    private void addTmpresult(Object node1, HashMap<Object, Node> tmpResults2, Node node2){
        if(node1 instanceof String && vQC2_rule.isVar((String) node1)){
                tmpResults2.put(node1, node2);
            }
    }
    private Node getSearchNode(Object node, HashMap<Object, Node> tmpResults) {
        if(node instanceof Node) {
            return (Node) node;
        }
        else {
            if(tmpResults.containsKey(node)) {
                return tmpResults.get(node);
            }
            else {
                return Node.ANY;
            }
        }
    }
    
    public ArrayList<HashMap<Object, String>> getResults() {
        ArrayList<HashMap<Object, String>> retV = null;
        
        if(results!=null && results.size()>0) {
            retV = new ArrayList<HashMap<Object, String>>();
            for(HashMap<Object, Node> result : results) {
                HashMap<Object, String> retVTriple = new HashMap<Object, String>();
                for(Entry<Object, Node> entry : result.entrySet()){
                    retVTriple.put(entry.getKey(), entry.getValue().toString());
                }
                retV.add(retVTriple);
            } 
        }
        return retV;
    }
}
