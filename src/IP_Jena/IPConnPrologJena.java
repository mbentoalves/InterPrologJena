/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.XSBSubprocessEngine;
import java.io.File;
import java.util.Map;

/**
 *
 * @author mba
 */
public abstract class IPConnPrologJena {
    protected PrologEngine  engine; 
    private IP_callbacks callback;
    
    protected Map<String, String> prefixes = null;

    
    protected void inicIPConn(PrologEngine engine, IP_callbacks pIP_callbacks) {
        this.engine = engine;
        inicIPConn(pIP_callbacks);
    } 
    
    protected void inicIPConn(PrologEngine engine, String fprologfile, IP_callbacks pIP_callbacks) {
        this.engine = engine;
        engine.consultAbsolute(new File(fprologfile)); 
        inicIPConn(pIP_callbacks);
    } 
    
    protected void inicIPConn(String XSBloc, String fprologfile, IP_callbacks pIP_callbacks) {
        engine = new XSBSubprocessEngine(XSBloc); 
        engine.consultAbsolute(new File(fprologfile)); 
        inicIPConn(pIP_callbacks);
    } 
    
    private void inicIPConn(IP_callbacks pIP_callbacks) {
        this.callback = pIP_callbacks;
        engine.command("retractall(ipObject(_))");
        int objID = engine.registerJavaObject(pIP_callbacks);
        this.engine.command("assert(ipObject(" + objID +"))");
    } 
    
    
    public void registerPrefixesFromProlog() {
        String clause1 = "setof(p(Prefix, Url), prefixes(Prefix, Url), L), buildTermModel(L,TM)";
 
        String clause2 = "[TM]";
        
        Object [] bindings = this.engine.deterministicGoal(clause1, clause2);
        if(bindings!= null){
        
            TermModel list = (TermModel)bindings[0];

            while(list.getChildCount()>0) {
                String v = list.getChild(0).toString();
                v = v.substring("p(".length(), v.length()-1);
                int posVirg = v.indexOf(",");
                String prefix = v.substring(0, posVirg).trim();
                String url = v.substring(posVirg+1);
                callback.registerPrefix(prefix, url);
                list = (TermModel) list.getChild(1);
            }
        }
    } 
    
    public void setCallBack(IP_callbacks pIP_callbacks) {
         int objID = engine.registerJavaObject(pIP_callbacks);
         engine.command("retractall(ipObject(_))");
         engine.command("assert(ipObject(" + objID +"))");
    }
 
    public Object[] run(String clause1, String clause2) {
        Object[] bindings =  engine.deterministicGoal(clause1, clause2);

        return bindings;
    }
    
    public void registerPrefix(String prefix, String url){
        this.engine.command("registerPrefix('" + prefix +"','" + url +"')");
        callback.registerPrefix(prefix, url);
    }
    
    public void runCommand(String command){
        engine.command(command);
    }
    
    public PrologEngine getPrologEngine() {
        return this.engine;
    }
    
    public IP_callbacks getCallback() {
        return this.callback;
    }
    
    public Map<String, String> getPrefixes() {
        return callback.prefixes;
    }
        
    public void close() {
        engine.command("retractall(ipObject(_))");
        this.engine.shutdown();
    }   
}
