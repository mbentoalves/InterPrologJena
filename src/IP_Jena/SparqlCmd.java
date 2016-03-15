/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mba
 */
public class SparqlCmd {
        String cmd = "";
        ArrayList<String> vars = new ArrayList<>();
        HashMap<String, String> genKeys = new HashMap<>();
        HashMap<String, String> instantiatedValues = new HashMap<>();
        boolean ask = false;
        
        public String toString() {
            return cmd;
        }
        
        public void setVarValue(String var, String value) {
            instantiatedValues.put(var, value);
        }
        
        public HashMap<String, String> getVarsValues() {
            return instantiatedValues;
        }
    }