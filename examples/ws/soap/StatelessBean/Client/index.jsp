<%-- 
    Document   : index
    Created on : Jan 26, 2011, 12:57:14 AM
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
        <h2> I prove you the SOAP service is stateless </h2>
        <hr/>
    <%
    try {
	com.flydragontech.HelloWorldSOAPService service = new com.flydragontech.HelloWorldSOAPService();
	com.flydragontech.HelloWorldSOAP port = service.getHelloWorldSOAPPort();
	java.lang.String result = port.getName();
	out.println("Get the default name = "+result);
    } catch (Exception ex) {
	out.println(ex);
    }
    %>
    <%-- end web service invocation --%><hr/>
    <%-- start web service invocation --%><hr/>
    <%
    try {
	com.flydragontech.HelloWorldSOAPService service = new com.flydragontech.HelloWorldSOAPService();
	com.flydragontech.HelloWorldSOAP port = service.getHelloWorldSOAPPort();
	java.lang.String aStr = "Yan";
	// TODO process result here
	java.lang.String result = port.setName(aStr);
	out.println("Set the name to "+result);
    } catch (Exception ex) {
	out.println(ex);
    }
    %>
    <%-- end web service invocation --%><hr/>
    <%-- start web service invocation --%><hr/>
    <%
    try {
	com.flydragontech.HelloWorldSOAPService service = new com.flydragontech.HelloWorldSOAPService();
	com.flydragontech.HelloWorldSOAP port = service.getHelloWorldSOAPPort();
	java.lang.String result = port.getName();
	out.println("Get the name again = "+result);
    } catch (Exception ex) {
	out.println(ex);
    }
    %>
    <%-- end web service invocation --%><hr/>

    </body>
</html>
