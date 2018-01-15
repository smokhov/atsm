/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flydragontech;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author umroot
 */
@WebService()
public class HelloWorldSOAP {

    private String name = "default Name";

    /**
     * Web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name")
    String name) {
        this.name = name;
        return "Hello "+name+ "(without exception)";
    }



        /**
     * Web service operation
     */
    @WebMethod(operationName = "helloExcpt")
    public String helloExcpt(@WebParam(name = "name")
    String name) throws RuntimeException {
        if(name.equals("exception"))
            throw new RuntimeException("WS exception");
        return "Hello "+name + "(with exception)";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getName")
    public String getName() {
        //TODO write your implementation code here:
        return name;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "setName")
    public String setName(@WebParam(name = "aStr")
    String aStr) {
        name = aStr;
        return name;
    }

}
