/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import static IP_Jena.GenericLib.newline;
import static QueryContext.QC_BuiltIn.isBuiltInFunction;
import static QueryContext.QC_genericLib.getInternalName;
import static QueryContext.QC_genericLib.getRuleName;
import static QueryContext.QC_genericLib.getVars;
import static QueryContext.QC_genericLib.getVars2;
import com.hp.hpl.jena.graph.NodeFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author mba
 */
public class QC_ruleBody {
    private ArrayList<QC_RuleClause> RuleClauses = new ArrayList<QC_RuleClause>();
    private HashMap<String, String> prefixes;
    
    public QC_ruleBody (HashMap<String, String> prefixes) {
        this.prefixes = prefixes;
    }
    
    public void insertRuleBody(String ruleClause) {
        ruleClause = ruleClause.trim();
        if(ruleClause.charAt(0)=='(') {
            insertRuleBodyTriple(ruleClause.substring(1, ruleClause.length()-1).trim());
        }
        else if (isBuiltInFunction(getRuleName(ruleClause))) {
            insertBuiltInFunction(ruleClause);
        }
        else {
            insertRuleBodyRule(ruleClause);
        }
    }
    
    private void insertBuiltInFunction(String ruleClause) {
        String functionName = getRuleName(ruleClause);
        String vars[] = getVars2(ruleClause);
        QC_BuiltIn vClauseRule = new QC_BuiltIn(functionName, new ArrayList<String>(Arrays.asList(vars)));
        RuleClauses.add(vClauseRule);
    }
    
    private void insertRuleBodyRule(String ruleClause) {
        String internalName = getInternalName(ruleClause);
        String vars[] = getVars2(ruleClause);
        QC_RuleClauseRule vClauseRule = new QC_RuleClauseRule(internalName, new ArrayList<String>(Arrays.asList(vars)));
        RuleClauses.add(vClauseRule);
    }
    
    
    private void insertRuleBodyTriple(String ruleClause) {
        while(ruleClause.contains("  ")) {
            ruleClause = ruleClause.replace("  ", " ");
        }
        
        String items [] = ruleClause.split(" ");
        
        Object subject = getObject(items[0]);
        Object property = getObject(items[1]);
        Object object = getObject(items[2]);
        
        insertTriple(subject, property, object); 
        
    }
    
    public void insertTriple(QC_Triple triple) {
        RuleClauses.add(triple);
    }
   
    public void insertTriple(Object subject, Object property, Object object) {
        RuleClauses.add(new QC_Triple(subject, property, object));
    }
    
    public int getNClauses() {
        return RuleClauses.size();
    }
    
    public QC_RuleClause getClause(int n) {
        return RuleClauses.get(n);
    }
    
    private Object getObject(String elem) {
        Object retValue = null;
        if(elem.startsWith("?")) {
            retValue = elem;
        }
        else {
            if(elem.contains(":")) {
                String prefix = elem.substring(0, elem.indexOf(":"));
                if(prefixes.containsKey(prefix)) {
                    elem = elem.replace(prefix + ":", prefixes.get(prefix));
                }
            }
            retValue = NodeFactory.createURI(elem);
        }
        return retValue;
    }
    
    public String toString() {
        String retV = "";
        
        for(QC_RuleClause ruleClause : RuleClauses) {
            retV+=ruleClause.toString() + newline;
        }
        return retV;
    }
}
