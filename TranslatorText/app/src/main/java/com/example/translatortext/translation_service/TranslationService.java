package com.example.translatortext.translation_service;

import android.content.res.Resources;
import android.util.JsonReader;

import androidx.appcompat.app.AppCompatActivity;

import com.example.translatortext.R;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

// the class responsible for interacting with the site API
public class TranslationService {
    String apiBaseUrl;

    public TranslationService(String apiUrl) {
        apiBaseUrl = apiUrl;
    }

    // getting a list of available text conversion modes
    public List<String> getModeList() {
        List<String> result = new ArrayList<String>();

        Thread internetThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // Create connection
                    HttpsURLConnection apiConnection = null;

                    apiConnection = (HttpsURLConnection) new URL(apiBaseUrl + "mode-list").openConnection();

                    InputStreamReader responseBodyReader = new InputStreamReader(apiConnection.getInputStream());
                    Scanner s = new Scanner(responseBodyReader).useDelimiter("\\A");

                    JsonArray response = Jsoner.deserialize(s.hasNext() ? s.next() : "", new JsonArray());

                    for (int j = 0; j < response.size(); j++) {
                        result.add(response.get(j).toString());
                    }

                    apiConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        internetThread.start();
        try {
            internetThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    // sending data for conversion to the site and receiving a response
    public String translate(String text, String mode) {
        String result = "";

        TranslatorThread translatorThread = new TranslatorThread();
        translatorThread.text = text;
        translatorThread.mode = mode;
        translatorThread.apiBaseUrl = apiBaseUrl;
        Thread internetThread = new Thread(translatorThread);

        internetThread.start();
        try {
            internetThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = translatorThread.result;

        return result;
    }
}
