package json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.lang.model.element.NestingKind;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//This class demonstrates parsing a JSON file with the json-simple-1.1.1 library which must be included as an external jar.

public class JSONSample {

    public static void main(String[] args) {

        JSONParser jParser = new JSONParser(); // Creates a parser.

        try
        {

            JSONParser ojsonParser = new JSONParser();
            String strPathJson = "/Users/ERIC_LAI/IdeaProjects/soen487/src/json/Cars.json";
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
