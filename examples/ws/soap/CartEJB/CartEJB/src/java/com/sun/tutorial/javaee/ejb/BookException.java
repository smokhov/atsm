/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.tutorial.javaee.ejb;

/**
 *
 * @author steve
 */
public class BookException extends Exception
{
   public BookException(String msg)   {
       super(msg);
   }

   public BookException(String msg, Throwable th){
       super(msg, th);
   }

}
