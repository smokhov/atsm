import java.io.File;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*; 
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SAXSample {
	
	    public static void main(String[] args) {
	        
	        try {
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            SAXParser saxParser = factory.newSAXParser();

	            DefaultHandler handler = new DefaultHandler() {

	                StringBuilder value;
	                
	                @Override
	                public void startElement(String uri, String localName,
	                        String qName,Attributes attribtues)
	                        throws SAXException {
	                    value = new StringBuilder();
	                }

	                @Override
	                public void endElement(String uri, String localName,
	                        String qName) throws SAXException {
	                    if ("fileContent".equalsIgnoreCase(qName)) {
	                        String decodedValue = new String(DatatypeConverter.parseBase64Binary(value.toString()));
	                        System.out.println(qName + " = " + decodedValue);
	                    } else {
	                        System.out.println(qName + " = " + value.toString());
	                    }
	                    value = new StringBuilder();
	                }

	                @Override
	                public void characters(char ch[], int start, int length)
	                        throws SAXException {
	                    value.append(new String(ch, start, length));
	                }

	            };

	            saxParser.parse(new File("cars.xml"), handler);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

}
