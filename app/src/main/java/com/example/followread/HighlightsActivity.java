package com.example.followread;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HighlightsActivity extends Activity {

    private List<highLights_Item> itemList;
    myDatabaseHelper myDB;
    private Context context;
    Button popup_add_HL, add_HL;

    EditText HL_page_input, HL_chapter_input, HL_content_input;
    AutoCompleteTextView HL_title_input;


    RecyclerView recyclerView;
    CustomAdapterHL customAdapterHL;

    String table_name;

    ArrayList<String> hl_id, hl_content, hl_page, hl_chapter, hl_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlights);

        recyclerView = findViewById(R.id.recyclerView_hl);
        popup_add_HL = findViewById(R.id.popup_add_highlights_button);


        myDB = new myDatabaseHelper(HighlightsActivity.this);
        hl_id = new ArrayList<>();
        hl_content = new ArrayList<>();
        hl_page = new ArrayList<>();
        hl_chapter = new ArrayList<>();
        hl_title = new ArrayList<>();

        itemList = new ArrayList<>();

        table_name = getIntent().getStringExtra("title");
        table_name = table_name.replaceAll("\\s+", "_");

        storeDataInList(table_name);
        //storeDataInArrays(table_name);
        itemList = sortListByTitle(itemList);

//        customAdapterHL = new CustomAdapterHL(HighlightsActivity.this, this,
//                hl_id, hl_content, hl_page, hl_chapter, hl_title);
        customAdapterHL = new CustomAdapterHL(HighlightsActivity.this, this, itemList);
        recyclerView.setAdapter(customAdapterHL);
        recyclerView.setLayoutManager(new LinearLayoutManager(HighlightsActivity.this));


        popup_add_HL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.add_highlight, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                add_HL = popupView.findViewById(R.id.HL_add_highlight_button);
                HL_page_input = popupView.findViewById(R.id.HL_page_input);
                HL_title_input = popupView.findViewById(R.id.HL_title_input);
                HL_content_input = popupView.findViewById(R.id.HL_content_input);
                HL_chapter_input = popupView.findViewById(R.id.HL_chapter_input);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(add_HL.getContext(), R.layout.simple_list_item_1, hl_title);

                HL_title_input.setAdapter(adapter);

                add_HL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String string_chapter = String.valueOf(HL_chapter_input.getText());
                        String string_page = String.valueOf(HL_page_input.getText());
                        String string_title = String.valueOf(HL_title_input.getText());
                        String string_content = String.valueOf(HL_content_input.getText());

                        myDB.addHighlight(table_name, string_title, string_chapter, string_page, string_content);
                    }
                });


                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
    }

    private void storeDataInList(String table_name) {
        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = readHLSData(db, table_name);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No highlights at the moment.", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                String hl_id, hl_content, hl_page, hl_chapter, hl_title;

                hl_id = cursor.getString(0);
                hl_content =cursor.getString(1);
                hl_page = cursor.getString(2);
                hl_title = cursor.getString(3);
                hl_chapter = cursor.getString(4);
                itemList.add(new highLights_Item(hl_page, hl_chapter, hl_id, hl_title, hl_content));
            }
        }
    }

    private List<highLights_Item> sortListByTitle(List<highLights_Item> itemList){
        for(int i = 0; i < itemList.size(); i++){
            String actualTitle = itemList.get(i).getHLTitle();
            if (i >= 1){
                for( int x = 0; x < i; x++){
                    if((itemList.get(x).getHLTitle()).equals(actualTitle)){
                        highLights_Item toMove = itemList.get(i);
                        itemList.set(i, itemList.get(x+1));
                        itemList.set(x+1, toMove);
                    }
                }
            }
        }
        return itemList;
    }

    void storeDataInArrays(String table_name){
        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = readHLSData(db, table_name);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No highlights at the moment.", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                hl_id.add(cursor.getString(0));
                hl_content.add(cursor.getString(1));
                hl_page.add(cursor.getString(2));
                hl_title.add(cursor.getString(3));
                hl_chapter.add(cursor.getString(4));
            }
        }
    }

    Cursor readHLSData(SQLiteDatabase db, String table_name){
        String query = "SELECT * FROM " + table_name;
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);
        return  cursor;
    }
}
