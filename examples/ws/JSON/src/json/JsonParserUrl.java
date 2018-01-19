package json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * @Desc This class demonstrates parsing a JSON from a remote source.
 * @author jash
 */
public class JsonParserUrl {
    
    /**
     * This method prints the name of the repositories of the specified user using the Github API.
     */

    public void remoteJsonParser() {

        JSONParser jParser = new JSONParser();
        String in;

        try {
            URL remoteURL = new URL("https://api.github.com/users/smokhov/repos");
            URLConnection connectURL = remoteURL.openConnection();

            BufferedReader readUrl = new BufferedReader(new InputStreamReader(connectURL.getInputStream()));

            while ((in = readUrl.readLine()) != null) {
                //System.out.println(in);
                JSONArray arrJson = (JSONArray) jParser.parse(in);

                for (Object obj : arrJson) {
                    JSONObject jObj = (JSONObject) obj;
                    String name = jObj.get("name").toString();

                    System.out.println(name);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
