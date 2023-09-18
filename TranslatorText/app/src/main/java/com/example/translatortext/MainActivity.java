package com.example.translatortext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.translatortext.translation_service.TranslationService;
import com.example.translatortext.translator_history.DataHelper;

import java.util.List;

// main page
public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;

    ConstraintLayout back;
    Button toHistory;
    ListView modeListView;
    View previousElement;
    Drawable backgroundElementColorSelected;
    Drawable backgroundElementColorNotSelected;
    int textNotSelectedColor = 1;
    int textSelectedColor = 2;
    EditText source;
    Button translate;
    TextView result;
    ArrayAdapter<String> modeListAdapter;
    String mode;

    // main page initialization
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        back = (ConstraintLayout) findViewById(R.id.root);
        toHistory = (Button) findViewById(R.id.historyButton);
        modeListView = (ListView) findViewById(R.id.modeList);
        source = (EditText) findViewById(R.id.editTextSource);
        translate = (Button) findViewById(R.id.translateButton);
        result = (TextView) findViewById(R.id.resultView);

        mode = "";

        TranslationService translationService = new TranslationService(getString(R.string.api_url));
        List<String> modeList = translationService.getModeList();
        modeListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modeList);
        modeListView.setAdapter(modeListAdapter);

        backgroundElementColorSelected = translate.getBackground();
        backgroundElementColorNotSelected = back.getBackground();
        textSelectedColor = translate.getCurrentTextColor();
        textNotSelectedColor = ((textSelectedColor & 0xFF000000) | (~textSelectedColor & 0x00FFFFFF));

        toHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, HistoryActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view != previousElement){
                    Object listItem = modeListView.getItemAtPosition(position);
                    mode = listItem.toString();
                    view.setBackground(backgroundElementColorSelected);
                    ((TextView) view).setTextColor(textSelectedColor);
                    if (previousElement != null) {
                        previousElement.setBackground(backgroundElementColorNotSelected);
                        ((TextView) previousElement).setTextColor(textNotSelectedColor);
                    }
                    previousElement = view;
                }
            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!source.getText().toString().isEmpty()) && (!mode.isEmpty())) {
                    String inputText = source.getText().toString();
                    String resultText = translationService.translate(inputText, mode);
                    result.setText(resultText);

                    DataHelper dataHelper = new DataHelper(getApplicationContext());

                    db = dataHelper.getWritableDatabase();

                    dataHelper.addNote(db, inputText, mode, resultText);
                } else {
                    Toast message = Toast.makeText(getApplicationContext(),
                            "Choose mode and write text", Toast.LENGTH_SHORT);
                    message.show();
                }
            }
        });

        source.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String inputText = source.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("source", inputText);
                clipboard.setPrimaryClip(clip);

                Toast message = Toast.makeText(getApplicationContext(),
                        "Source is copied", Toast.LENGTH_SHORT);
                message.show();

                return true;
            }
        });

        result.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String outputText = result.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("result", outputText);
                clipboard.setPrimaryClip(clip);

                Toast message = Toast.makeText(getApplicationContext(),
                        "Result is copied", Toast.LENGTH_SHORT);
                message.show();

                return true;
            }
        });
    }

    // getting the data selected on the request history page
    @Override
    protected void onResume() {
        super.onResume();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            if (clip.getItemAt(0) != null) {
                ClipData.Item item = clip.getItemAt(0);
                if (item.getText() != null) {
                    String text = item.getText().toString();
                    if ((text.contains("\n")) && (text.split("\n").length >= 4)) {
                        mode = text.split("\n")[2];
                        source.setText(text.split("\n")[1]);
                        result.setText(text.split("\n")[3]);
                    }
                }
            }
        }
    }
}