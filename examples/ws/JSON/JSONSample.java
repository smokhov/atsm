package JSON;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//This class demonstrates parsing a JSON file with the json-simple-1.1.1 library which must be included as an external jar.

public class JSONSample {

    public static void main(String[] args) {

        JSONParser jParser = new JSONParser(); // Creates a parser.

        try {

            Object obj = jParser.parse(new FileReader("Complete path to the Cars.json"));
            System.out.println();

            JSONObject jObj = (JSONObject) obj;
            System.out.println(jObj);

            String name = (String) jObj.get("RacingCar");
            System.out.println(name);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
