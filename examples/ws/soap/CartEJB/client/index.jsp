<%-- 
    Document   : index
    Created on : Jan 30, 2013, 1:02:56 PM
    Author     : umroot
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>    <%-- start web service invocation --%>
        <h2>This is to test the EJB stateful bean as an implementation of SOAP service </h2>
        <hr/>
    <%
    try {
	com.sun.tutorial.javaee.ejb.CartWebServiceService service = new com.sun.tutorial.javaee.ejb.CartWebServiceService();
	com.sun.tutorial.javaee.ejb.CartWebService port = service.getCartWebServicePort();
        port.initialize("Yuhong");
	java.lang.String title = "UUU";
	port.addBook(title);
        title = "KKKK";
        port.addBook(title);
        //port.remove();
    } catch (com.sun.tutorial.javaee.ejb.BookException_Exception ex) {
           out.println("Caught a BookException: " + ex.getMessage());
       } catch (Exception ex) {
           out.println("Caught an unexpected exception!");
           ex.printStackTrace();
       }
    %>
    <%-- end web service invocation --%><hr/>
    <%-- start web service invocation --%><hr/>
    <%
    try {
	com.sun.tutorial.javaee.ejb.CartWebServiceService service = new com.sun.tutorial.javaee.ejb.CartWebServiceService();
	com.sun.tutorial.javaee.ejb.CartWebService port = service.getCartWebServicePort();
	// TODO process result here
	java.util.List<java.lang.String> result = port.getContents();
	out.println("Result = "+result);
    } catch (Exception ex) {
	out.println(ex);
    }
    %>
    <%-- end web service invocation --%><hr/>

    </body>
</html>
