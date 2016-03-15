/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;

/**
 *
 * @author mba
 */
public final class IPConnPrologJenaModel extends IPConnPrologJena {
    private InfModel infModel = null;
    private IP_callbacksModel vIP_callbacksModel = null;
    
    public void init(String XSBLoc, String fprologfile, InfModel infModel) {
        this.infModel = infModel;
        vIP_callbacksModel = new IP_callbacksModel(infModel);
        inicIPConn(XSBLoc, fprologfile, vIP_callbacksModel);
    }
    
    public void init(String XSBLoc, String fprologfile, IP_callbacksModel pIP_callbacksModel) {
        inicIPConn(XSBLoc, fprologfile, pIP_callbacksModel);
    }

    public void registerPrefixesFromModel(){
        if(infModel!=null) {
            registerPrefixesFromModel(infModel);
        }
        if(vIP_callbacksModel!=null) {
           registerPrefixesFromModel(vIP_callbacksModel.getInfModel());
        }
        
    }
    
    private void registerPrefixesFromModel(Model m){
        m.getNsPrefixMap().entrySet().stream().forEach((prefix) -> {
            if(prefix.getKey().length()>0) {
                this.registerPrefix(prefix.getKey(), prefix.getValue());
            }
        });
    }
}
