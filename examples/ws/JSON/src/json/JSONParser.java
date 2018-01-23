
package json;

/**
 * @Desc The main class for JSON parsing.
 * @author jash, haotao
 */
public class JsonParse {
    
    public static void main(String [] args){
//        JsonParserLocal j = new JsonParserLocal();
//        j.localJsonParser();
        
        JsonParserUrl j = new JsonParserUrl();
        j.remoteJsonParser();
    }
    
}
