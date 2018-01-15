package mashup.crossdomain.workaround;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Ticker
 */
public class Ticker extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Ticker() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
      throws ServletException, IOException {
    	String jsonData = "{\"symbol\" : \"IBM\", \"price\" : \"91.42\"}";
    	String output = req.getParameter("callback") + "(" + jsonData + ");";

    	resp.setContentType("text/javascript");
              
    	PrintWriter out = resp.getWriter();
    	out.println(output);
    	// prints: jsonp1232617941775({"symbol" : "IBM", "price" : "91.42"});
    }
     


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
