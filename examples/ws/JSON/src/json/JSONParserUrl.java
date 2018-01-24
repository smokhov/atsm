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
public class JSONParserUrl {
    
    /**
     * This method prints the name of the repositories of the specified user using the Github API.
     */

    public void remoteJsonParser() {

        JSONParser ojsonParser = new JSONParser();
        String strIn;

        try {
            URL oRemoteURL = new URL("https://api.github.com/users/smokhov/repos");
            URLConnection oConnectURL = oRemoteURL.openConnection();

            BufferedReader oReadUrl = new BufferedReader(new InputStreamReader(oConnectURL.getInputStream()));

            while ((strIn = oReadUrl.readLine()) != null) {
                //System.out.println(in);
                JSONArray oArrJson = (JSONArray) ojsonParser.parse(strIn);

                for (Object oObject : oArrJson) {
                    JSONObject ojsonObj = (JSONObject) oObject;
                    String strName = ojsonObj.get("name").toString();

                    System.out.println(strName);
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
