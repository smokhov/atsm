/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.tutorial.javaee.ejb;

/**
 *
 * @author steve
 */
public class IdVerifier {
    public IdVerifier(){
        
    }

    public boolean validate(String id){
        return "myid".equals(id);
    }


}
