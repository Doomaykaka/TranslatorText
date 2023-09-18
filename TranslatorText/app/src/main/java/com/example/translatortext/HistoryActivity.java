package com.example.translatortext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.translatortext.translator_history.DataHelper;
import com.example.translatortext.translator_history.History;

import java.util.ArrayList;
import java.util.List;

// history page
public class HistoryActivity extends AppCompatActivity {

    SQLiteDatabase db;
    DataHelper dataHelper;

    ListView historyListView;
    ArrayAdapter<String> historyListAdapter;
    Button toMain;
    Button deleteItem;
    Button clearHistory;

    String nodeData;
    List<String> dataForPrinting;

    // history initialization
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toMain = (Button) findViewById(R.id.mainButton);
        historyListView = (ListView) findViewById(R.id.historyView);
        deleteItem = (Button) findViewById(R.id.deleteItemButton);
        clearHistory = (Button) findViewById(R.id.clearHistoryButton);

        nodeData = "";

        dataHelper = new DataHelper(getApplicationContext());

        db = dataHelper.getWritableDatabase();

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = historyListView.getItemAtPosition(position);
                nodeData = listItem.toString();
            }
        });

        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = historyListView.getItemAtPosition(position);
                nodeData = listItem.toString();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("node", nodeData);
                clipboard.setPrimaryClip(clip);

                finish();

                return false;
            }
        });

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nodeData.isEmpty()) {

                    History currentHistory = new History();

                    String[] data = new String[4];

                    data[0] = nodeData.split("\n")[0];
                    data[1] = nodeData.split("\n")[1];
                    data[2] = nodeData.split("\n")[2];
                    data[3] = nodeData.split("\n")[3];

                    currentHistory.setId(Integer.parseInt(data[0]));
                    currentHistory.setInputValue(data[1]);
                    currentHistory.setMode(data[2]);
                    currentHistory.setOutputValue(data[3]);

                    dataHelper.removeNote(db, currentHistory);

                    updateList();
                } else {
                    Toast message = Toast.makeText(getApplicationContext(),
                            "Choose note", Toast.LENGTH_SHORT);
                    message.show();
                }
            }
        });

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataForPrinting.size() != 0) {
                    dataHelper.removeAllNotes(db);

                    updateList();
                }
            }
        });

        List<History> data = dataHelper.getAllNotes(db);
        dataForPrinting = new ArrayList<String>();

        for (History currentHistory : data) {
            String[] noteView = new String[4];

            noteView[0] = Integer.toString(currentHistory.getId());
            noteView[1] = currentHistory.getInputValue();
            noteView[2] = currentHistory.getMode();
            noteView[3] = currentHistory.getOutputValue();

            dataForPrinting.add(noteView[0] + "\n" + noteView[1] + "\n" + noteView[2] + "\n" + noteView[3]);
        }

        historyListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataForPrinting);
        historyListView.setAdapter(historyListAdapter);
    }

    // updating the list with records of previous operations
    public void updateList() {
        dataForPrinting.clear();
        List<History> data = dataHelper.getAllNotes(db);

        for (History currentHistory : data) {
            String[] noteView = new String[4];

            noteView[0] = Integer.toString(currentHistory.getId());
            noteView[1] = currentHistory.getInputValue();
            noteView[2] = currentHistory.getMode();
            noteView[3] = currentHistory.getOutputValue();

            dataForPrinting.add(noteView[0] + "\n" + noteView[1] + "\n" + noteView[2] + "\n" + noteView[3]);
        }

        historyListAdapter.notifyDataSetChanged();
    }
}
