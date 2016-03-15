/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import static IP_Jena.GenericLib.newline;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mba
 */
public class QC_InternalRule {
        ArrayList<String> variables = new ArrayList<String>();
        QC_ruleBody rulesBody;
        
        
        public QC_InternalRule(HashMap<String, String> prefixes){
             rulesBody = new QC_ruleBody(prefixes);
        }
        
        public void addVariable(String var) {
            variables.add(var);
        }
        
        public void addBodyClause(String bodyClause) {
            rulesBody.insertRuleBody(bodyClause);
        }
        
        public QC_ruleBody getRuleBody() {
            return rulesBody;
        }
        
        public ArrayList<String> getVariables() {
            return variables;
        }
        
        public String toString() {
            String msg1 = "variables : ";
            for(String var : variables) {
                msg1 += var + ",";
            }
            
            msg1+=newline + rulesBody.toString();
            return msg1;
        }
        
    }
