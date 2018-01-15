/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flydragontecch;

import javax.jws.WebService;
import schematemplates.Fault;
import wsdltemplates.FaultMsg;

/**
 *
 * @author umroot
 */
@WebService(serviceName = "Service", portName = "Port", endpointInterface = "wsdltemplates.Interface", targetNamespace = "urn:WSDLTemplates", wsdlLocation = "WEB-INF/wsdl/NewWebServiceFromWSDL/faultSample.wsdl")
public class NewWebServiceFromWSDL {

    public java.lang.String op1(java.lang.String input) throws FaultMsg {
         if(input.equals("exception")){
             Fault fault = new Fault();
             fault.setReason("This is the reason");
            throw new FaultMsg("WS exception", fault);
        }
         return "Hello "+input + "(without exception)";
    }

}
