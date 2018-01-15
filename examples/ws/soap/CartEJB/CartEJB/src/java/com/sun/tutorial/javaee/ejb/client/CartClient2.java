/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.tutorial.javaee.ejb.client;

import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.util.Hashtable;
import com.sun.tutorial.javaee.ejb.*;
import java.util.Properties;
/**
 *
 * @author umroot
 */
public class CartClient2 {
    public static void main(String[] arg){
       try
       {
           Properties env = new Properties();
           env.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
           env.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
           env.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
           env.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
           env.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
           Context initial =  new InitialContext(env);
           Cart cartRemote =(Cart) initial.lookup("ejb/CartSessionBean");

           cartRemote.initialize("YYH");

           cartRemote.addBook("Book1");
           cartRemote.addBook("Book2");
           cartRemote.addBook("Book3");
           List bookList = cartRemote.getContents();
           ListIterator enumer = bookList.listIterator();
           while (enumer.hasNext()) {
              String title = (String) enumer.next();
              System.out.println(title);
           }
           //cartRemote.removeBook("Alice in Wonderland");
           //cartRemote.remove();
           cartRemote.initialize("YYH2");

           cartRemote.addBook("Book12");
           cartRemote.addBook("Book22");
           cartRemote.addBook("Book32");
           bookList = cartRemote.getContents();
           enumer = bookList.listIterator();
           while (enumer.hasNext()) {
              String title = (String) enumer.next();
              System.out.println(title);
           }
           cartRemote.removeBook("Alice in Wonderland");
           //cartRemote.remove();


       } catch (BookException ex) {
           System.err.println("Caught a BookException: " + ex.getMessage());
       } catch (Exception ex) {
           System.err.println("Caught an unexpected exception!");
           ex.printStackTrace();
       }
   }


}
