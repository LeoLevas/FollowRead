package com.example.followread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //get all var
    RecyclerView recyclerView;
    FloatingActionButton add_button;

    myDatabaseHelper myDB;
    ArrayList<String> book_id;
    ArrayList<String> book_title;
    ArrayList<String> book_author;
    ArrayList<String> book_pages;
    ArrayList<String> book_page_position;
    ArrayList<String> book_chapter;
    ArrayList<String> book_chapter_position;
    ArrayList<String> book_finished_day;
    ArrayList<String> book_finished_month;
    ArrayList<String> book_finished_year;
    ArrayList<byte[]> book_cover;
    ArrayList<String> book_last_entry_day;
    ArrayList<String> book_last_entry_month;
    ArrayList<String> book_last_entry_year;
    ArrayList<LocalDate> book_last_entry_date;
    ArrayList<String> book_last_entry_page;
    ArrayList<String> book_last_entry_chap;
    ArrayList<String> book_expected_page_today;
    ArrayList<String> book_expected_chap_today;
    CustomAdapter customAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        myDB = new myDatabaseHelper(MainActivity.this);
        book_id = new ArrayList<>();
        book_title = new ArrayList<>();
        book_author = new ArrayList<>();
        book_pages = new ArrayList<>();
        book_page_position = new ArrayList<>();
        book_chapter = new ArrayList<>();
        book_chapter_position = new ArrayList<>();
        book_finished_day = new ArrayList<>();
        book_finished_month = new ArrayList<>();
        book_finished_year = new ArrayList<>();
        book_cover = new ArrayList<byte[]>();
        book_last_entry_day = new ArrayList<>();
        book_last_entry_month = new ArrayList<>();
        book_last_entry_year = new ArrayList<>();
        book_last_entry_date = new ArrayList<>();
        book_last_entry_page = new ArrayList<>();
        book_last_entry_chap = new ArrayList<>();
        book_expected_page_today = new ArrayList<>();
        book_expected_chap_today = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this, this,
                book_id, book_title, book_author, book_pages, book_page_position, book_chapter, book_chapter_position,
                book_finished_day,book_finished_month, book_finished_year, book_cover,
                book_last_entry_day, book_last_entry_month, book_last_entry_year, book_last_entry_date,
                book_expected_page_today, book_expected_chap_today, book_last_entry_page, book_last_entry_chap);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                book_id.add(cursor.getString(0));
                book_title.add(cursor.getString(1));
                book_author.add(cursor.getString(2));
                book_pages.add(cursor.getString(3));
                book_page_position.add(cursor.getString(4));
                book_chapter.add(cursor.getString(5));
                book_chapter_position.add(cursor.getString(6));
                book_finished_day.add(cursor.getString(7));
                book_finished_month.add(cursor.getString(8));
                book_finished_year.add(cursor.getString(9));
                book_last_entry_day.add(cursor.getString(10));
                book_last_entry_month.add(cursor.getString(11));
                book_last_entry_year.add(cursor.getString(12));
                book_cover.add(cursor.getBlob(13));
                book_expected_page_today.add(cursor.getString(14));
                book_expected_chap_today.add(cursor.getString(15));
                book_last_entry_page.add(cursor.getString(16));
                book_last_entry_chap.add(cursor.getString(17));

                if((cursor.getString(10) != null) && (cursor.getString(11) != null) && (cursor.getString(12) != null)){
                    String last_entry_day = cursor.getString(10);
                    String last_entry_month = cursor.getString(11);
                    String last_entry_year = cursor.getString(12);


                    if (Integer.parseInt(last_entry_month) < 10){
                        last_entry_month = "0" + last_entry_month;
                    }
                    if (Integer.parseInt(last_entry_day) < 10){
                        last_entry_day = "0" + last_entry_day;
                    }

                    book_last_entry_date.add(LocalDate.parse(last_entry_year+"-"+last_entry_month+"-"+last_entry_day));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete_all){
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all ?");
        builder.setMessage("Are you sure you wat to delete all data ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDatabaseHelper myDB = new myDatabaseHelper(MainActivity.this);
                myDB.deleteAllData();
                //refresh Activity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}