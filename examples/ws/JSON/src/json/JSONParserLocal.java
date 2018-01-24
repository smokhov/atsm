package json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Desc This class demonstrates parsing a local JSON file using Java.
 * @author jash, haotao
 */
public class JSONParserLocal {
    
    /**
     * This method returns various entities from the parsed local JSON file such as name, description etc.
     */

    public void localJsonParser(){

        JSONParser ojsonParser = new JSONParser();
        
        String strPathJson = "Complete path to the Cars.json";
        //String pathJson = "/home/jash/Downloads/atsm/examples/ws/JSON/Cars.json";

        try {

            Object oObject = ojsonParser.parse(new FileReader(strPathJson));

            JSONObject ojsonObj = (JSONObject) oObject;
            System.out.println(ojsonObj);
            
            JSONObject oCars = (JSONObject) ojsonObj.get("cars");
            
            JSONObject oRacingCar = (JSONObject) oCars.get("RacingCar");
            System.out.println(oRacingCar);
            
            String strName = oRacingCar.get("name").toString();
            System.out.println(strName);
            
            String strEngine = oRacingCar.get("engine").toString();
            System.out.println(strEngine);
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

