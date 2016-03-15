/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import static IP_Jena.GenericLib.newline;
import static QueryContext.QC_BuiltIn.isBuiltInFunction;
import static QueryContext.QC_genericLib.extractBodyClauses;
import static QueryContext.QC_genericLib.getInternalName;
import static QueryContext.QC_genericLib.getRuleName;
import static QueryContext.QC_genericLib.trimStringVect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author mba
 */
public class QC_RuleSet {
    
    HashMap<String, QC_rule> RuleSet = new HashMap<String, QC_rule>();
    HashMap<String, String> prefixes = new HashMap<String, String>();

        
    public QC_RuleSet(String ruleSet) {
        parseRules(ruleSet);
    }
    
    public QC_rule getQCRule(String rule) {
        if(RuleSet.containsKey(rule)) {
            return RuleSet.get(rule);
        }
        else {
            return null;
        }
    }
    
    private void parseRules(String ruleSet) {
        HashMap<Integer, ArrayList<String>> hm_RuleSet = new HashMap<Integer, ArrayList<String>>();
        HashMap<Integer, ArrayList<Integer>> hm_rulesDependencies = new HashMap<Integer, ArrayList<Integer>>();
        HashMap<String, ArrayList<Integer>> mapRule_hm = new HashMap<String, ArrayList<Integer>>();
         
        extractRules(ruleSet, hm_RuleSet, hm_rulesDependencies, mapRule_hm, prefixes);
        
        int parserOrder[] = defineParseOrder(hm_RuleSet.size(), hm_rulesDependencies);
        
        for(int i=0; i<parserOrder.length; i++) {
            ArrayList<String> rule = hm_RuleSet.get(parserOrder[i]); 
            String internalName = rule.remove(0);
            if(RuleSet.containsKey(internalName)) {
                RuleSet.get(internalName).addRule(rule);
            } 
            else {
                RuleSet.put(internalName, new QC_rule(internalName, rule, prefixes));
            }
                
        }
    }
    
    
    private void extractRules(String ruleSet, HashMap<Integer, ArrayList<String>> hm_RuleSet, HashMap<Integer, ArrayList<Integer>> hm_rulesDependencies, HashMap<String, ArrayList<Integer>> mapRule_hm, HashMap<String, String> prefixes) {
        int ruleID = 0; 
        String rules[] = ruleSet.split(newline);
        
        rules = trimStringVect(rules);
        
        for(int i=0; i<rules.length; i++) {
            rules[i] = rules[i].substring(0, rules[i].length()-1);
        }
        
        for(String rule : rules) {
            if(rule.substring(0, 7).toLowerCase().compareTo("@prefix")==0) {
                getPrefix(prefixes, rule);
            }
            else {
                preParseRule(rule, hm_RuleSet, hm_rulesDependencies, mapRule_hm, ruleID);
                ruleID++;
            }            
        }
    }
    
    private void getPrefix(HashMap<String, String> prefixes, String rule){
        rule = rule.substring(7).trim();
        int posDoubleDot = rule.indexOf(":");
        String prefix = rule.substring(0, posDoubleDot).trim();
        String url = rule.substring(posDoubleDot+1).trim();
        url = url.substring(1, url.length()-1);
        prefixes.put(prefix, url);
    }
    
    private void preParseRule(String rule, HashMap<Integer, ArrayList<String>> hm_RuleSet, HashMap<Integer, ArrayList<Integer>> hm_rulesDependencies, HashMap<String, ArrayList<Integer>> mapRule_hm, int ruleID) {
        int posSymbol = rule.indexOf("<-");
        String head = rule.substring(0, posSymbol).trim();
        String body = rule.substring(posSymbol+2);
        
        String internalRuleName = getInternalName(head);
        
        if(mapRule_hm.containsKey(internalRuleName)) {
            mapRule_hm.get(internalRuleName).add(ruleID);
        }
        else {
            mapRule_hm.put(internalRuleName, new ArrayList<Integer>(Arrays.asList(ruleID)));
        }
        
        hm_RuleSet.put(ruleID, new ArrayList<String>(Arrays.asList(internalRuleName, head)));
        
        //String bodyClauses[] = body.split(",");
       
        ArrayList<String> bodyClauses = extractBodyClauses(body);
        
        for(String bodyClause : bodyClauses) {
            bodyClause = bodyClause.trim();
            hm_RuleSet.get(ruleID).add(bodyClause);
            if(!bodyClause.startsWith("(") && !isBuiltInFunction(getRuleName(bodyClause))) {
                String internalName = getInternalName(bodyClause);
                ArrayList<Integer> internalNameRules = mapRule_hm.get(internalName);
                if(hm_rulesDependencies.containsKey(ruleID)) {
                    hm_rulesDependencies.get(ruleID).addAll(internalNameRules);
                }
                else {
                    hm_rulesDependencies.put(ruleID, new ArrayList<Integer>(internalNameRules));
                }
            }
        }
        
    }
    
    
    
    private int[] defineParseOrder(int nRules, HashMap<Integer, ArrayList<Integer>> hm_rulesDependencies) {
        int parseOrder[] = new int[nRules];
        
        int nrule = 0;
        
        for(int i=0; i<nRules; i++) {
            if(!hm_rulesDependencies.containsKey(i)) {
                parseOrder[nrule] = i;
                nrule++;
                for(ArrayList<Integer> dep : hm_rulesDependencies.values()) {
                    dep.remove(i);
                }
            }
        }
        
        boolean anyChange = true;
        
        while(anyChange && hm_rulesDependencies.size()>0) {
            anyChange = false;

            Set<Entry<Integer, ArrayList<Integer>>> allValues = hm_rulesDependencies.entrySet();
            
            HashMap<Integer, ArrayList<Integer>> tmpEntry = 
                    new HashMap<Integer, ArrayList<Integer>>();
            
            for(Entry<Integer, ArrayList<Integer>> value : allValues) {
                tmpEntry.put(value.getKey(), value.getValue());
            }
            
            for(Entry<Integer, ArrayList<Integer>> value : tmpEntry.entrySet()) {
                if(value.getValue().isEmpty()) {
                    parseOrder[nrule] = value.getKey();
                    nrule++;
                    for(ArrayList<Integer> dep : hm_rulesDependencies.values()) {
                        dep.remove(value.getKey());
                    }
                    hm_rulesDependencies.remove(value.getKey());
                }
            }
        }
        
        return parseOrder;
    }
    
    public String toString() {
        String retV = "";
        
        for(Entry<String, QC_rule> rule : RuleSet.entrySet()) {
            retV += rule.getKey() + newline;
            retV += rule.getValue().toString() + newline;
        }
        
        return retV;
    }
}
