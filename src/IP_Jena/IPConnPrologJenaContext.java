/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import com.declarativa.interprolog.PrologEngine;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;

/**
 *
 * @author mba
 */
public class IPConnPrologJenaContext extends IPConnPrologJena{

    private IP_callbacksContext vIP_callbacks;

    public void init(String XSBloc, String fprologfile) {
        vIP_callbacks = new IP_callbacksContext();
        inicIPConn(XSBloc, fprologfile, vIP_callbacks);
    }

    public void init(PrologEngine engine, String fprologfile) {
        vIP_callbacks = new IP_callbacksContext();
        inicIPConn(engine, fprologfile, vIP_callbacks);
    }

    public void init(String XSBloc, String fprologfile, RuleContext context) {
        vIP_callbacks = new IP_callbacksContext();
        vIP_callbacks.setContext(context);
        inicIPConn(XSBloc, fprologfile, vIP_callbacks);
    }

    public void setContext(RuleContext context) {
        vIP_callbacks.setContext(context);
    }
}
