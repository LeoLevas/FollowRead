package com.example.followread;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity /*implements DatePickerDialog.OnDateSetListener*/{

    private Button dateButton;

    EditText title_input, author_input, pages_input;
    Button add_button;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        title_input = findViewById(R.id.title_input);
        author_input = findViewById(R.id.author_input);
        pages_input = findViewById(R.id.pages_input);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabaseHelper myDB = new myDatabaseHelper(AddActivity.this);
                myDB.addBook(title_input.getText().toString().trim(),
                        author_input.getText().toString().trim(),
                        Integer.parseInt(pages_input.getText().toString().trim()));
            }
        });

//        dateButton = findViewById(R.id.finishDate_button);
//        dateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDatePickerDialog();
//            }
//        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void showDatePickerDialog(){
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                this,
//                this,
//                Calendar.getInstance().get(Calendar.YEAR),
//                Calendar.getInstance().get(Calendar.MONTH),
//                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//        );
//        datePickerDialog.show();
//    }
//
//    @Override
//    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
//        String date = dayOfMonth + "/" + month + "/" + year;
//        dateButton.setText(date);
//    }
}