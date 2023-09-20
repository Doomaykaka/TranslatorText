package com.example.translatortext.translation_service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// the class responsible for interacting with the site API
public class TranslationService {
    String apiBaseUrl;
    private OkHttpClient.Builder client;
    private Retrofit retrofit;

    public TranslationService(String apiUrl) {
        apiBaseUrl = apiUrl;
        client = new OkHttpClient.Builder();
        retrofit = new Retrofit.Builder().baseUrl(apiUrl).addConverterFactory(GsonConverterFactory.create()).client(client.build()).build();
    }

    // getting a list of available text conversion modes
    public List<String> getModeList() {
        List<String> result = new ArrayList<String>();

        TranslationTextService service = retrofit.create(TranslationTextService.class);
        Call<List<String>> callSync = service.getModeList();

        TranslatorThread threadRunnable = new TranslatorThread();
        threadRunnable.mode = "getModeList";
        threadRunnable.callSyncModeList = callSync;

        Thread threadRunnableImpl = new Thread(threadRunnable);
        threadRunnableImpl.start();
        try {
            threadRunnableImpl.join();
            result = threadRunnable.modeListResult;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        /*Thread internetThread = new Thread(new Runnable() {

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
        }*/

        return result;
    }

    // sending data for conversion to the site and receiving a response
    public String translate(String text, String mode) {
        String result = "";

        JSONObject postParams = new JSONObject();
        try {
            postParams.put("text", text);
            postParams.put("mode", mode);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String JSON = postParams.toString();

        TranslationTextService service = retrofit.create(TranslationTextService.class);
        Call<TranslateResult> callSync = service.translate(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (JSON)));

        TranslatorThread threadRunnable = new TranslatorThread();
        threadRunnable.mode = "postTranslate";
        threadRunnable.callSyncTranslate = callSync;


        Thread threadRunnableImpl = new Thread(threadRunnable);
        threadRunnableImpl.start();
        try {
            threadRunnableImpl.join();
            result = threadRunnable.translateResult;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        /* TranslatorThread translatorThread = new TranslatorThread();
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

        result = translatorThread.result;*/

        return result;
    }
}
