package com.example.followread;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    myDatabaseHelper myDB;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Button dateButton;

    EditText title_input, author_input, pages_input, page_position_input, chapter_input, chapter_position_input;
    Button update_button, delete_button, finish_date_button, addImage;

    String id, title, author, pages, page_position, chapter, chapter_position, finished_day, finished_month, finished_year;
    String new_title, new_author, new_pages, new_page_position, new_chapter, new_chapter_position,
            new_finished_day, new_finised_month, new_finished_year, new_book_cover, new_last_entry_day, new_last_entry_month, new_last_entry_year,
            new_expected_page, new_expected_chap, new_last_entry_page, new_last_entry_chap;
    byte[] bookImage_bytes;
    ImageView bookImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        myDB = new myDatabaseHelper(UpdateActivity.this);

        title_input = findViewById(R.id.title_input2);
        author_input = findViewById(R.id.author_input2);
        pages_input = findViewById(R.id.pages_input2);
        page_position_input = findViewById(R.id.pages_position_input2);
        chapter_input = findViewById(R.id.chapter_input2);
        chapter_position_input = findViewById(R.id.chapter_position_input2);

        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        finish_date_button = findViewById(R.id.finishDate_button2);
        addImage = findViewById(R.id.addImage2);

        bookImageView = findViewById(R.id.bookImage2);

        String query = "SELECT * FROM my_library;";

        SQLiteDatabase DB = myDB.getReadableDatabase();

        Cursor cursor = DB.rawQuery(query, null);

        cursor.move(Integer.parseInt(getIntent().getStringExtra("id")));

        bookImage_bytes = cursor.getBlob(13);
        Bitmap bm = BitmapFactory.decodeByteArray(bookImage_bytes,0, bookImage_bytes.length);
        bookImageView.setImageBitmap(bm);
        cursor.close(); // close your cursor when you don't need it anymore

        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setTitle(title);
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRuntimePermission();
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabaseHelper myDB = new myDatabaseHelper(UpdateActivity.this);

                ArrayList<String> values = new ArrayList<String>();

                title = title_input.getText().toString().trim();
                author = author_input.getText().toString().trim();
                pages = pages_input.getText().toString().trim();
                page_position = page_position_input.getText().toString().trim();
                chapter = chapter_input.getText().toString().trim();
                chapter_position = chapter_position_input.getText().toString().trim();


                myDB.updateData(id, title, author, pages, page_position,chapter,chapter_position, finished_day, finished_month, finished_year, bookImage_bytes);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        finish_date_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });


    }
    void getAndSetIntentData(){
        //Extras are added to Intent in CustomAdapter.java
        try {
            //Getting Data from intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            pages = getIntent().getStringExtra("pages");
            page_position = getIntent().getStringExtra("pages_position");
            chapter = getIntent().getStringExtra("chapter");
            chapter_position = getIntent().getStringExtra("chapter_position");
            finished_day = getIntent().getStringExtra("finished_day");
            finished_month = getIntent().getStringExtra("finished_month");
            finished_year = getIntent().getStringExtra("finished_year");


            //setting Intent Data
            title_input.setText(title);
            author_input.setText(author);
            pages_input.setText(pages);
            page_position_input.setText(page_position);
            chapter_input.setText(chapter);
            chapter_position_input.setText(chapter_position);

            finish_date_button.setHint(finished_day + "/" + finished_month /*String.valueOf(Integer.parseInt(finished_month) + 1)*/ + "/" + finished_year);

        }catch (Exception ex){
            Toast.makeText(this,"No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you wat to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myDatabaseHelper myDB = new myDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);
                myDB.deleteBookTable(title);
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

    //Image code block

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to view
            bookImageView.setImageURI(data.getData());

            Uri uri = data.getData();

            try {
                InputStream iStream = getContentResolver().openInputStream(uri);
                bookImage_bytes = getBytes(iStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getPath(Uri uri) {
        if(uri == null)return null;
        String projection = (MediaStore.Images.Media.DATA);
        Cursor cursor = managedQuery(uri, new String[]{projection},null,null,null);
        if(cursor!= null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private void checkRuntimePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else{
                pickImageFromGallery();
            }
        }
        else{
            pickImageFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Date dialog code block

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        month += 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        finish_date_button.setHint(date);
        finished_day = String.valueOf(dayOfMonth);
        finished_month = String.valueOf(month);
        finished_year = String.valueOf(year);
    }
}