/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author mba
 */
public class QC_BuiltIn extends QC_RuleClause {
    private static final HashMap<String, String> builtInFunctions;
    static {
        builtInFunctions = new HashMap<String, String>();
        builtInFunctions.put("notEqual", "QC_BuiltInFunctions.notEqual");
    }
    
    public static boolean isBuiltInFunction(String func) {
        return builtInFunctions.containsKey(func);
    }
    
    public static String getClassBuiltInFunction(String func) {
        return builtInFunctions.get(func);
    }
    
    private String functionName;
    private ArrayList<String> vars = new ArrayList<String>(); 
    
    public QC_BuiltIn(String functionName, ArrayList<String> vars) {
        this.functionName = functionName;
        this.vars = vars;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public ArrayList<String> getVariables() {
        return vars;
    }
    
    public String toString() {
        String retV = functionName + " - vars: ";
        for(String var : vars) {
            retV += var + ",";
        }
        
        return retV;
    }
}
