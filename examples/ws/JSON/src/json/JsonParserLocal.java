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
public class JsonParserLocal {

    public void localJsonParser(){

        JSONParser jParser = new JSONParser();
        
        String pathJson = "Complete path to the Cars.json";
        //String pathJson = "/home/jash/Downloads/atsm/examples/ws/JSON/Cars.json";

        try {

            Object obj = jParser.parse(new FileReader(pathJson));

            JSONObject jObj = (JSONObject) obj;
            System.out.println(jObj);
            
            JSONObject cars = (JSONObject) jObj.get("cars");
            
            JSONObject racingCar = (JSONObject) cars.get("RacingCar");
            System.out.println(racingCar);
            
            String name = racingCar.get("name").toString();
            System.out.println(name);
            
            String engine = racingCar.get("engine").toString();
            System.out.println(engine);
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

