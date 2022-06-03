package com.example.followread;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProgressActivity extends AppCompatActivity{
    myDatabaseHelper myDB;


    byte[] cover_bytes;
    ImageView cover_view;

    EditText chapter_input, page_input;
    TextView total_chapter_view, total_page_view, title_view, author_view, page_to_do_today_view, chap_to_do_today_view;
    String id, title, author, pages, page_position, chapter, chapter_position, finished_day, finished_month, finished_year,
            last_entry_day, last_entry_month, last_entry_year, entry_day, entry_month, entry_year,
            page_expected_today, chap_expected_today, last_entry_page, last_entry_chap;

    ProgressBar complete_progressBar, page_obj_bar, chapter_obj_bar;

    CheckBox finished_reading_box;

    LocalDate actualDate, FinishDate, last_entry_date;

    Button update_button, add_highlights_button;

    Integer pageDoneToday, chapDoneToday, pageCompleteAt, chapCompleteAt;
    Float advancementPage, advancementChap;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        myDB = new myDatabaseHelper(ProgressActivity.this);

        chapter_input = findViewById(R.id.chapter_input_progress);
        page_input = findViewById(R.id.page_input_progress);

        total_chapter_view = findViewById(R.id.total_chapter_progress);
        total_page_view = findViewById(R.id.total_pages_progress);
        title_view = findViewById(R.id.title_progress);
        author_view = findViewById(R.id.author_progress);

        page_to_do_today_view = findViewById(R.id.page_to_do_view);
        chap_to_do_today_view = findViewById(R.id.chapter_to_do_view);

        finished_reading_box = findViewById(R.id.finished_reading_progress);

        complete_progressBar = findViewById(R.id.complete_progressBar);
        page_obj_bar = findViewById(R.id.page_daily_obj_bar);
        chapter_obj_bar = findViewById(R.id.chap_daily_obj_bar);

        add_highlights_button = findViewById(R.id.add_highlights);

        add_highlights_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //region but image in background
        cover_view = findViewById(R.id.cover_progress);

        String query = "SELECT * FROM my_library;";

        SQLiteDatabase DB = myDB.getReadableDatabase();

        Cursor cursor = DB.rawQuery(query, null);

        cursor.move(Integer.parseInt(getIntent().getStringExtra("id")));

        cover_bytes = cursor.getBlob(13);
        Bitmap bm = BitmapFactory.decodeByteArray(cover_bytes,0, cover_bytes.length);
        cover_view.setImageBitmap(bm);
        cursor.close(); // close your cursor when you don't need it anymore

        //endregion

        getAndSetIntentData();

        //region Calculate days between actual date and finish date

        actualDate = java.time.LocalDate.now();
        //year-month-day

        String month = finished_month;
        String day = finished_day;
        if (Integer.parseInt(month) < 10){
            month = "0" + month;
        }
        if (Integer.parseInt(day) < 10){
            day = "0" + day;
        }

        FinishDate = LocalDate.parse(String.valueOf(finished_year) + "-" +
                month + "-" + day);

        float nbOfDaysLeft = ChronoUnit.DAYS.between(actualDate, FinishDate);

        float nbOfPageLeft = Float.parseFloat(pages) - Float.parseFloat(page_position);
        int pageToDoToday = (int) Math.ceil(nbOfPageLeft / nbOfDaysLeft);

        float nbChapterLeft = Float.parseFloat(chapter) - Float.parseFloat(chapter_position);
        int chapterToDoToday = (int) Math.ceil(nbChapterLeft/nbOfDaysLeft);

        page_to_do_today_view.setText(String.valueOf(pageToDoToday + "page to do today"));
        chap_to_do_today_view.setText(String.valueOf(chapterToDoToday + "chapter to do today"));

        //endregion

        update_button = findViewById(R.id.progress_button);

        //region add text watcher to the input box
        page_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    if (!update_button.isEnabled()){
                        update_button.setEnabled(false);
                    }
                }
                else {
                    update_button.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        chapter_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    if (!update_button.isEnabled()){
                        update_button.setEnabled(false);
                    }
                }
                else {
                    update_button.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion
        //region fill progress bars at starting of activity
        pageDoneToday = Integer.parseInt(page_position) - Integer.parseInt(last_entry_page);
        advancementPage = (Float.parseFloat(String.valueOf(pageDoneToday)) / Float.parseFloat(String.valueOf(pageToDoToday))) * 100;

        chapDoneToday = Integer.parseInt(chapter_position) - Integer.parseInt(last_entry_chap);
        advancementChap = (Float.parseFloat(String.valueOf(chapDoneToday)) / Float.parseFloat(String.valueOf(chapterToDoToday)))*100;

        page_to_do_today_view.setText(String.valueOf(pageDoneToday +"/"+pageToDoToday+" to do today"));

        chap_to_do_today_view.setText(String.valueOf(chapDoneToday +"/"+chapterToDoToday+" to do today"));

        if (Integer.parseInt(page_expected_today) < Integer.parseInt(page_position)){
            page_obj_bar.setProgress(100);
        }
        else{
            page_obj_bar.setMax(pageToDoToday);
            page_obj_bar.setProgress(pageDoneToday);
        }

        if (chapterToDoToday < chapDoneToday){
            chapter_obj_bar.setProgress(100);
        }
        else{
            chapter_obj_bar.setMax(chapterToDoToday);
            chapter_obj_bar.setProgress(chapDoneToday);
        }
        //endregion

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabaseHelper myDB = new myDatabaseHelper(ProgressActivity.this);

                actualDate = java.time.LocalDate.now();

                entry_day = String.valueOf(actualDate.getDayOfMonth());
                entry_month = String.valueOf(actualDate.getMonthValue());
                entry_year = String.valueOf(actualDate.getYear());

                if(!String.valueOf(page_input.getText()).equals("")){page_position = String.valueOf(page_input.getText());}
                if(!String.valueOf(chapter_input.getText()).equals("")){chapter_position = String.valueOf(chapter_input.getText());}

                //region progress bar advancement

                pageDoneToday = Integer.parseInt(page_position) - Integer.parseInt(last_entry_page);
                advancementPage = (Float.parseFloat(String.valueOf(pageDoneToday)) / Float.parseFloat(String.valueOf(pageToDoToday))) * 100;

                chapDoneToday = Integer.parseInt(chapter_position) - Integer.parseInt(last_entry_chap);
                advancementChap = (Float.parseFloat(String.valueOf(chapDoneToday)) / Float.parseFloat(String.valueOf(chapterToDoToday)))*100;

                page_to_do_today_view.setText(String.valueOf(pageDoneToday +"/"+pageToDoToday+" to do today"));

                chap_to_do_today_view.setText(String.valueOf(chapDoneToday +"/"+chapterToDoToday+" to do today"));

                if (Integer.parseInt(page_expected_today) < Integer.parseInt(page_position)){
                    page_obj_bar.setProgress(100);
                }
                else{
                    page_obj_bar.setMax(pageToDoToday);
                    page_obj_bar.setProgress(pageDoneToday);
                }

                if (chapterToDoToday < chapDoneToday){
                    chapter_obj_bar.setProgress(100);
                }
                else{
                    chapter_obj_bar.setMax(chapterToDoToday);
                    chapter_obj_bar.setProgress(chapDoneToday);
                }

                int completeAt = (int) ((Float.parseFloat(page_position) / Float.parseFloat(pages))*100);
                complete_progressBar.setProgress(completeAt);

                //endregion
                myDB.updateProgress(id, page_position, chapter_position, entry_day, entry_month, entry_year);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    void getAndSetIntentData(){

        Bundle intentExtras = getIntent().getExtras();

        id = intentExtras.getString("id");
        title = intentExtras.getString("title");
        author = intentExtras.getString("author");
        pages = intentExtras.getString("pages");
        page_position = intentExtras.getString("pages_position");
        chapter = intentExtras.getString("chapter");
        chapter_position = intentExtras.getString("chapter_position");
        finished_day = intentExtras.getString("finished_day");
        finished_month = intentExtras.getString("finished_month");
        finished_year = intentExtras.getString("finished_year");
        last_entry_day = intentExtras.getString("last_entry_day");
        last_entry_month = intentExtras.getString("last_entry_month");
        last_entry_year = intentExtras.getString("last_entry_year");
        page_expected_today = intentExtras.getString("expected_page");
        chap_expected_today = intentExtras.getString("expected_chap");
        last_entry_page = intentExtras.getString("last_entry_page");
        last_entry_chap = intentExtras.getString("last_entry_chap");

        //setting Intent Data
        title_view.setText(title);
        author_view.setText(author);
        total_page_view.setText(page_position+"/"+ pages);
        total_chapter_view.setText( chapter_position+"/"+chapter);
        page_input.setHint(page_position);
        chapter_input.setHint(chapter_position);

        int completeAt = (int) ((Float.parseFloat(page_position) / Float.parseFloat(pages))*100);
        complete_progressBar.setProgress(completeAt);
    }
}
