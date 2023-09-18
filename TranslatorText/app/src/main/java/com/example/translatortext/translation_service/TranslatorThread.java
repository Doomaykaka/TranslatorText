package com.example.translatortext.translation_service;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

// a thread for working with the network
public class TranslatorThread implements Runnable {
    String result;

    String text;
    String mode;
    String apiBaseUrl;

    // thread body
    @Override
    public void run() {
        try {
            // Create connection
            HttpsURLConnection apiConnection = null;
            apiConnection = (HttpsURLConnection) new URL(apiBaseUrl + "translate").openConnection();

            // Create the data
            String textJson = "{\n" +
                    "  \"text\": \"" + text + "\",\n" +
                    "  \"mode\": \"" + mode + "\"\n" +
                    "}";

            // Enable writing
            apiConnection.setDoOutput(true);
            apiConnection.setDoInput(true);
            // Set method
            apiConnection.setRequestMethod("POST");
            apiConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // Write the data

            OutputStream out;
            out = apiConnection.getOutputStream();
            out.write(textJson.getBytes("UTF-8"));

            out.flush();
            out.close();

            //get result

            InputStreamReader responseBodyReader = new InputStreamReader(apiConnection.getInputStream());
            Scanner s = new Scanner(responseBodyReader).useDelimiter("\\A");

            JsonObject response = Jsoner.deserialize(s.hasNext() ? s.next() : "", new JsonObject());
            if (response.containsKey("result"))
                result = response.getString(Jsoner.mintJsonKey("result", ""));

            apiConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException i) {
            i.printStackTrace();
        }
    }
}
