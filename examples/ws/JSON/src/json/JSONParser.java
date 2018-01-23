
package json;

/**
 * @Desc The main class for parsing a JSON via remote or local.
 * @author jash, haotao
 */
public class JSONParser {
    
    public static void main(String [] args){
//        JSONParserLocal ojsonLocal = new JSONParserLocal();
//        ojsonLocal.localJsonParser();
        
        JSONParserUrl ojsonUrl = new JSONParserUrl();
        ojsonUrl.remoteJsonParser();
    }
    
}
