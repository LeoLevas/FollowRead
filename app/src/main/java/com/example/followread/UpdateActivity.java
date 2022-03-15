package com.example.followread;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private Button dateButton;

    EditText title_input, author_input, pages_input, position_input;
    Button update_button, delete_button, finish_date_button;

    String id, title, author, pages, position, finished_day, finished_month, finished_year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        title_input = findViewById(R.id.title_input2);
        author_input = findViewById(R.id.author_input2);
        pages_input = findViewById(R.id.pages_input2);
        position_input = findViewById(R.id.position_input2);

        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        finish_date_button = findViewById(R.id.finishDate_button2);

        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setTitle(title);
        }

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabaseHelper myDB = new myDatabaseHelper(UpdateActivity.this);

                ArrayList<String> values = new ArrayList<String>();

                title = title_input.getText().toString().trim();
                author = author_input.getText().toString().trim();
                pages = pages_input.getText().toString().trim();
                position = position_input.getText().toString().trim();

/*                if(title.equals("")){title = String.valueOf(((TextInputLayout)title_input.getParent()).getHint()); }
                if(author.equals("")){author = String.valueOf(((TextInputLayout)author_input.getParent()).getHint()); }
                if(pages.equals("")){pages = String.valueOf(((TextInputLayout)pages_input.getParent()).getHint()); }
                if(position.equals("")){position = String.valueOf(((TextInputLayout)position_input.getParent()).getHint()); }*/

                myDB.updateData(id, title, author, pages, position, finished_day, finished_month, finished_year);
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
        if (getIntent().hasExtra("id") &&
                getIntent().hasExtra("title") &&
                getIntent().hasExtra("author") &&
                getIntent().hasExtra("pages") &&
                getIntent().hasExtra("position") &&
                getIntent().hasExtra("finished_day") &&
                getIntent().hasExtra("finished_month") &&
                getIntent().hasExtra("finished_year")){

            //Getting Data from intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            pages = getIntent().getStringExtra("pages");
            position = getIntent().getStringExtra("position");
            finished_day = getIntent().getStringExtra("finished_day");
            finished_month = getIntent().getStringExtra("finished_month");
            finished_year = getIntent().getStringExtra("finished_year");

            //setting Intent Data
            title_input.setText(title);
            author_input.setText(author);
            pages_input.setText(pages);
            position_input.setText(position);

            finish_date_button.setHint(finished_day + "/" + finished_month /*String.valueOf(Integer.parseInt(finished_month) + 1)*/ + "/" + finished_year);

        }else{
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