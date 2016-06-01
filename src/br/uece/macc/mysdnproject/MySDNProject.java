package br.uece.macc.mysdnproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by inacio on 01/06/16.
 */
public class MySDNProject {
    public static void main(String[] args) throws IOException {

        try {
            //dados e formato para acesso o rest
            URL url = new URL("http://localhost:8080/wm/core/switch/all/flow/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            //timeout
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            //retorno do rest, já convertendo o inputStream em bufferReader
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            //converter a string de retorno do rest em json
            String jsonString = org.apache.commons.io.IOUtils.toString(br);
            JSONObject jsonObj = new JSONObject(jsonString);
            System.out.println("String:\n"+jsonString+"\n\n");

            //buscar quais switchs tiveram retorno
            String[] masterList = getNames(jsonObj);
            for (String string : masterList) {
                System.out.println("DPID: "+string);
                JSONArray flows = (JSONArray) ((JSONObject) jsonObj.get(string)).get("flows");

                //retorno o atributos interno ao JsonArray
                for (int i = 0; i < flows.length(); i++) {
                    JSONObject jsonTemp = flows.getJSONObject(i);
                    System.out.println("\tpacketCount: " + jsonTemp.get("packetCount"));
                }
            }
        } catch (JSONException e) {	
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator<?> i = jo.keys();
        String[] names = new String[length];
        int j = 0;
        while (i.hasNext()) {
            names[j] = (String) i.next();
            j += 1;
        }
        return names;
    }
}
