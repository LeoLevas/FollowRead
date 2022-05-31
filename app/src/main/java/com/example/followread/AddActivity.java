package com.example.followread;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    //region declare variable

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private Button dateButton;
    byte[] bookImage_bytes;

    EditText title_input, author_input, pages_input, pages_position_input, chapter_input, chapter_position_input;
    public int finished_day, finished_month, finished_year;

    private String finishDateString;
    LocalDate actualDate, finishDate;

    ImageView bookImageView;

    Button add_button, add_image_button;

    String last_entry_day, last_entry_month, last_entry_year, entry_day, entry_month, entry_year,
            page_expected_today, chap_expected_today, last_entry_page, last_entry_chap;

    Integer pages_input_int, pages_position_input_int, chapter_input_int, chapter_position_input_int;

    //endregion

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //region assign variable

        title_input = findViewById(R.id.title_input);
        author_input = findViewById(R.id.author_input);
        pages_input = findViewById(R.id.pages_input);
        pages_position_input = findViewById(R.id.pages_position_input);
        chapter_input = findViewById(R.id.chapter_input);
        chapter_position_input = findViewById(R.id.chapter_position_input);


        dateButton = findViewById(R.id.finishDate_button);
        add_button = findViewById(R.id.add_button);
        add_image_button = findViewById(R.id.addImage);
        bookImageView = findViewById(R.id.bookImage);

        //endregion

        actualDate = java.time.LocalDate.now();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabaseHelper myDB = new myDatabaseHelper(AddActivity.this);

                last_entry_day = String.valueOf(actualDate.getDayOfMonth());
                last_entry_month = String.valueOf(actualDate.getMonthValue());
                last_entry_year = String.valueOf(actualDate.getYear());

                pages_input_int = Integer.parseInt(String.valueOf(pages_input.getText()).trim());
                pages_position_input_int = Integer.parseInt(String.valueOf(pages_position_input.getText()).trim());
                chapter_input_int = Integer.parseInt(String.valueOf(chapter_input.getText()).trim());
                chapter_position_input_int = Integer.parseInt(String.valueOf(chapter_position_input.getText()).trim());

                String finish_month = String.valueOf(finished_month);
                String finish_day = String.valueOf(finished_day);
                if (Integer.parseInt(finish_month) < 10){
                    finish_month = "0" + finish_month;
                }
                if (Integer.parseInt(finish_day) < 10){
                    finish_day = "0" + finish_day;
                }
                finishDate = LocalDate.parse(String.valueOf(finished_year)+"-"+finish_month+"-"+finish_day);

                float nbOfDaysLeft = ChronoUnit.DAYS.between(actualDate, finishDate);

                float nbOfPageLeft = pages_input_int - pages_position_input_int;
                int pageToDoToday = (int) Math.ceil(nbOfPageLeft / nbOfDaysLeft);

                float nbChapterLeft = chapter_input_int - chapter_position_input_int;
                int chapterToDoToday = (int) Math.ceil(nbChapterLeft/nbOfDaysLeft);


                myDB.addBook(title_input.getText().toString().trim(),
                        author_input.getText().toString().trim(),
                        pages_input_int,
                        pages_position_input_int,
                        chapter_input_int,
                        chapter_position_input_int,
                        finished_day,
                        finished_month + 1,
                        finished_year,
                        bookImage_bytes,
                        last_entry_day,
                        last_entry_month,
                        last_entry_year,
                        pageToDoToday,
                        chapterToDoToday,
                        pages_position_input_int,
                        chapter_position_input_int);
            }
        });

        // the problem here is that there is not pagePosition or chapterPosition


        add_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRuntimePermission();
            }
        });

        dateButton = findViewById(R.id.finishDate_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
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

            Bitmap bitmapBook = null;
            try {
                bitmapBook = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmapBook != null) {
                bookImage_bytes = imagemTratada(getBitmapAsByteArray(bitmapBook));
            }
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    //resize image to fit in db
    private byte[] imagemTratada(byte[] imagem_img){

        while (imagem_img.length > 500000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagem_img, 0, imagem_img.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imagem_img = stream.toByteArray();
        }
        return imagem_img;

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

    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        dateButton.setHint(date);
        finished_day = dayOfMonth;
        finished_month = month;
        finished_year = year;
    }
}