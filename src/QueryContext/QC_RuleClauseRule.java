/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import java.util.ArrayList;

/**
 *
 * @author mba
 */
public class QC_RuleClauseRule extends QC_RuleClause{
    private String internalRuleName;
    private ArrayList<String> vars = new ArrayList<String>(); 
    
    public QC_RuleClauseRule(String internalRuleName, ArrayList<String> vars) {
        this.internalRuleName = internalRuleName;
        this.vars = vars;
    }
    
    public String getRuleName() {
        return internalRuleName;
    }
    
    public ArrayList<String> getVariables() {
        return vars;
    }
    
    public String toString() {
        String retV = internalRuleName + " - vars: ";
        for(String var : vars) {
            retV += var + ",";
        }
        
        return retV;
    }
}
