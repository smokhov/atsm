/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.tutorial.javaee.ejb;

import java.util.List;
import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.xml.ws.RequestWrapper;

/**
 *
 * @author steve
 */
@WebService()
@Stateless()
public class CartWebService {
    @EJB
    private Cart ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    //private Cart ejbRef = new CartBean();

    @WebMethod(operationName = "initialize")
    public void initialize(@WebParam(name = "person")
    String person) throws BookException {
        ejbRef.initialize(person);
    }

    @WebMethod(operationName = "initialize_1")
    @RequestWrapper(className = "initialize_1")
    public void initialize(@WebParam(name = "person")
    String person, @WebParam(name = "id")
    String id) throws BookException {
        ejbRef.initialize(person, id);
    }

    @WebMethod(operationName = "addBook")
    public void addBook(@WebParam(name = "title")
    String title) throws BookException {
        ejbRef.addBook(title);
    }

    @WebMethod(operationName = "removeBook")
    public void removeBook(@WebParam(name = "title")
    String title) throws BookException {
        ejbRef.removeBook(title);
    }

    @WebMethod(operationName = "getContents")
    public List<String> getContents() {
        return ejbRef.getContents();
    }

    @WebMethod(operationName = "remove")
    @Oneway
    public void remove() {
        ejbRef.remove();
    }

}
