/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import static IP_Jena.GenericLib.newline;
import static QueryContext.QC_genericLib.getVars;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mba
 */
public class QC_rule {
  
    private HashMap<String, String> prefixes;
    private String internalRuleName = "";

    private ArrayList<QC_InternalRule> rules = new ArrayList<QC_InternalRule>();
    
    public QC_rule(String internalRuleName, ArrayList<String> rulesClause, HashMap<String, String> prefixes) {
        this.internalRuleName = internalRuleName;
        this.prefixes = prefixes;
        addRule(rulesClause);
    }
         
    
    public ArrayList<QC_InternalRule> getRules() {
        return rules;
    }
    
    public void addRule(ArrayList<String> ruleClauses) {
        String head = ruleClauses.remove(0);
        QC_InternalRule rule = new QC_InternalRule(prefixes);
        addHeadVars(rule, head);
        for(String bodyClause : ruleClauses) {
            rule.addBodyClause(bodyClause);
        }
        rules.add(rule);
    } 
    
    
    private void addHeadVars(QC_InternalRule rule, String head) {
        int posPOpen = head.indexOf("(");
        int posPClose = head.indexOf(")");
        
        if(posPOpen >=0 && head.substring(posPOpen+1, posPClose).trim().length()>0) {
              for(String var : getVars(head.substring(posPOpen+1, posPClose))){
                rule.addVariable(var);
            }
        } 
    }
    
    
    
    public String getInternalRulename() {
        return internalRuleName;
    }

    
    public String toString() {
        String retV = internalRuleName + newline;
        int i=0;
        for(QC_InternalRule rule : rules) {
            retV += "declaration "+(++i)+newline;
            retV += rule.toString();
        }
        return retV;
    }
}
