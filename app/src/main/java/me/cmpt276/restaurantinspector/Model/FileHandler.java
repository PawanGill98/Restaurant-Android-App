package me.cmpt276.restaurantinspector.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class FileHandler {

    // Referenced from: https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    // Referenced from: https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static void loadIteration1Data(InputStream restaurantsIS, InputStream inspectionsIS, FileOutputStream restaurantsOutputStream, FileOutputStream inspectionsOutputStream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(restaurantsIS, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            BufferedWriter restaurantsBufferedWriter = new BufferedWriter( new OutputStreamWriter(restaurantsOutputStream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                FileHandler.writeToInternalMemory(restaurantsBufferedWriter, line);
            }
            restaurantsBufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader2 = new BufferedReader(
                new InputStreamReader(inspectionsIS, Charset.forName("UTF-8"))
        );

        try {
            BufferedWriter inspectionsBufferedWriter = new BufferedWriter( new OutputStreamWriter(inspectionsOutputStream, "UTF-8"));
            while ((line = reader2.readLine()) != null) {
                FileHandler.writeToInternalMemory(inspectionsBufferedWriter, line);
            }
            inspectionsBufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String readInspectionsDateModified() throws IOException, JSONException {
        JSONObject jsonFile = readJsonFromUrl("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
        JSONObject result = jsonFile.getJSONObject("result");
        JSONArray resourcesArray = (JSONArray) result.get("resources");
        JSONObject resourcesArrayFirstElement = resourcesArray.getJSONObject(0);
        String date = resourcesArrayFirstElement.get("last_modified").toString();
        return date;
    }

    public static void writeToInternalMemory(BufferedWriter inspectionsBufferedWriter, String inspectionsLine) {
        try {
            inspectionsBufferedWriter.write(inspectionsLine);
            inspectionsBufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}