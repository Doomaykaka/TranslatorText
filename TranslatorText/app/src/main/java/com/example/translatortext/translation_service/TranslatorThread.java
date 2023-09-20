package com.example.translatortext.translation_service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

// a thread for working with the network
public class TranslatorThread implements Runnable {
    Call<List<String>> callSyncModeList;
    Call<TranslateResult> callSyncTranslate;
    List<String> modeListResult = new ArrayList<String>();
    String translateResult = "";
    String mode;


    // thread body
    @Override
    public void run() {
        switch (mode) {
            case "getModeList":
                getModeList();
                break;
            case "postTranslate":
                postTranslate();
                break;
        }
    }

    private void getModeList() {
        try {
            Response<List<String>> response = callSyncModeList.execute();
            modeListResult = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void postTranslate() {
        try {
            Response<TranslateResult> response = callSyncTranslate.execute();
            translateResult = ((TranslateResult) response.body()).getResult();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
