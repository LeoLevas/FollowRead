package com.example.followread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.invoke.LambdaConversionException;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    Activity activity;
    private ArrayList book_id, book_title, book_author, book_pages,
            book_page_position, book_chapter, book_chapter_position, book_finished_day, book_finished_month, book_finished_year,
            book_cover, book_last_entry_day, book_last_entry_month, book_last_entry_year, book_last_entry_date,
            book_expected_page_today, book_expected_chap_today, book_last_entry_page, book_last_entry_chap;
    private String FinishedDateString;
    LocalDate actualDate, FinishDate, last_entry_date;

    CustomAdapter(Activity activity, Context context,
                  ArrayList book_id,
                  ArrayList book_title,
                  ArrayList book_author,
                  ArrayList book_pages,
                  ArrayList book_page_position,
                  ArrayList book_chapter,
                  ArrayList book_chapter_position,
                  ArrayList book_finished_day,
                  ArrayList book_finished_month,
                  ArrayList book_finished_year,
                  ArrayList book_cover,
                  ArrayList book_last_entry_day,
                  ArrayList book_last_entry_month,
                  ArrayList book_last_entry_year,
                  ArrayList book_last_entry_date,
                  ArrayList book_expected_page_today,
                  ArrayList book_expected_chap_today,
                  ArrayList book_last_entry_page,
                  ArrayList book_last_entry_chap){
        this.activity = activity;
        this.context = context;
        this.book_id = book_id;
        this.book_title = book_title;
        this.book_author = book_author;
        this.book_pages = book_pages;
        this.book_page_position = book_page_position;
        this.book_chapter = book_chapter;
        this.book_chapter_position = book_chapter_position;
        this.book_finished_day = book_finished_day;
        this.book_finished_month = book_finished_month;
        this.book_finished_year = book_finished_year;
        this.book_cover = book_cover;
        this.book_last_entry_day = book_last_entry_day;
        this.book_last_entry_month = book_last_entry_month;
        this.book_last_entry_year = book_last_entry_year;
        this.book_last_entry_date = book_last_entry_date;
        this.book_expected_page_today = book_expected_page_today;
        this.book_expected_chap_today = book_expected_chap_today;
        this.book_last_entry_page = book_last_entry_page;
        this.book_last_entry_chap = book_last_entry_chap;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);

        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        // Set data of books to home pages
        holder.book_title_txt.setText(String.valueOf(book_title.get(position)));
        holder.book_author_txt.setText(String.valueOf(book_author.get(position)));

        holder.pages_position_txt.setText(((String.valueOf(book_page_position.get(position))) + "/" + String.valueOf(book_pages.get(position))));
        holder.chapter_position_txt.setText(((String.valueOf(book_chapter_position.get(position))) + "/" + String.valueOf(book_chapter.get(position))));

        //"book_cover.get(position)" returns Byte[]

        byte[] bookImage_bytes = (byte[]) book_cover.get(position);
        Bitmap bm = BitmapFactory.decodeByteArray(bookImage_bytes, 0, bookImage_bytes.length);

        holder.book_cover_view.setImageBitmap(bm);



        //region Calculate days between actual date and finish date
        actualDate = java.time.LocalDate.now();
        //year-month-day

        String month = String.valueOf(book_finished_month.get(position));
        String day = String.valueOf(book_finished_day.get(position));
        if (Integer.parseInt(month) < 10){
            month = "0" + month;
        }
        if (Integer.parseInt(day) < 10){
            day = "0" + day;
        }

        FinishedDateString = (String.valueOf(book_finished_year.get(position)) + "-" +
                month + "-" + day);
        FinishDate = LocalDate.parse(FinishedDateString);

        float nbOfDaysLeft = ChronoUnit.DAYS.between(actualDate, FinishDate);

        //endregion

        float nbOfPageLeft = Integer.parseInt(String.valueOf(book_pages.get(position))) - Integer.parseInt(String.valueOf(book_page_position.get(position)));
        int pageToDoToday = (int) Math.ceil(nbOfPageLeft / nbOfDaysLeft);

        float nbChapterLeft = Integer.parseInt(String.valueOf(book_chapter.get(position))) - Integer.parseInt(String.valueOf(book_chapter_position.get(position)));
        int chapterToDoToday = (int) Math.ceil(nbChapterLeft/nbOfDaysLeft);

        int completeAt = (int) ((Float.parseFloat((String) book_page_position.get(position)) / Float.parseFloat((String) book_pages.get(position)))*100);
        holder.complete_progress_bar.setProgress(completeAt);

        String last_entry_day, last_entry_month, last_entry_year;

        if (book_last_entry_day.get(position) != null && book_last_entry_month.get(position) != null && book_last_entry_year.get(position) != null){
            last_entry_day = String.valueOf(book_last_entry_day.get(position));
            last_entry_month = String.valueOf(book_last_entry_month.get(position));
            last_entry_year = String.valueOf(book_last_entry_year.get(position));
            if (Integer.parseInt(last_entry_month) < 10){
                last_entry_month = "0" + last_entry_month;
            }
            if (Integer.parseInt(last_entry_day) < 10){
                last_entry_day = "0" + last_entry_day;
            }
            last_entry_date = LocalDate.parse(last_entry_year+"-"+last_entry_month+"-"+last_entry_day);
            holder.last_entry_view.setText("Last entry on " + last_entry_day +"/"+last_entry_month+"/"+last_entry_year);
        }

        try {
            if (!book_last_entry_date.get(position).toString().equals(actualDate.toString())) {
                myDatabaseHelper myDB = new myDatabaseHelper(context);
                myDB.putLastEntry(book_id.get(position).toString(), book_page_position.get(position).toString(), book_chapter_position.get(position).toString());
            }
        } catch (IndexOutOfBoundsException IOOBE){
            Toast.makeText(context,"index out of bound", Toast.LENGTH_SHORT );
        }


        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.modify:
                                //handle menu1 click
                                Intent intent = new Intent(context, UpdateActivity.class);
                                /*String currentId = String.valueOf(book_id.get(position));
                                String lastId = String.valueOf(book_id.get(book_id.size() - 1));
                                boolean isLast = true;
                                if (!lastId.equals(currentId)){
                                    isLast = false;
                                }
                                if(!isLast){
                                    Bundle extras = new Bundle();
                                    extras.putString("new_title", String.valueOf(book_title.get(position + 1)));
                                    extras.putString("new_author", String.valueOf(book_author.get(position + 1)));
                                    extras.putString("new_pages", String.valueOf(book_pages.get(position + 1)));
                                    extras.putString("new_pages_position", String.valueOf(book_page_position.get(position + 1)));
                                    extras.putString("new_chapter", String.valueOf(book_chapter.get(position + 1)));
                                    extras.putString("new_chapter_position", String.valueOf(book_chapter_position.get(position + 1)));
                                    extras.putString("new_finished_day", String.valueOf(book_finished_day.get(position + 1)));
                                    extras.putString("new_finished_month", String.valueOf(book_finished_month.get(position + 1)));
                                    extras.putString("new_finished_year", String.valueOf(book_finished_year.get(position + 1)));
                                    extras.putString("new_book_cover", String.valueOf(book_cover.get(position + 1)));
                                    extras.putString("new_last_entry_day", String.valueOf(book_last_entry_day.get(position + 1)));
                                    extras.putString("new_last_entry_month", String.valueOf(book_last_entry_month.get(position + 1)));
                                    extras.putString("new_last_entry_year", String.valueOf(book_last_entry_year.get(position + 1)));
                                    extras.putString("new_expected_page", String.valueOf(book_expected_page_today.get(position + 1)));
                                    extras.putString("new_expected_chap", String.valueOf(book_expected_chap_today.get(position + 1)));
                                    extras.putString("new_last_entry_page", String.valueOf(book_last_entry_page.get(position + 1)));
                                    extras.putString("new_last_entry_chap", String.valueOf(book_last_entry_chap.get(position + 1)));
                                    intent.putExtras(extras);
                                }*/
                                intent.putExtra("id", String.valueOf(book_id.get(position)));
                                intent.putExtra("title", String.valueOf(book_title.get(position)));
                                intent.putExtra("author", String.valueOf(book_author.get(position)));
                                intent.putExtra("pages", String.valueOf(book_pages.get(position)));
                                intent.putExtra("pages_position", String.valueOf(book_page_position.get(position)));
                                intent.putExtra("chapter", String.valueOf(book_chapter.get(position)));
                                intent.putExtra("chapter_position", String.valueOf(book_chapter_position.get(position)));
                                intent.putExtra("finished_day", String.valueOf(book_finished_day.get(position)));
                                intent.putExtra("finished_month", String.valueOf(book_finished_month.get(position)));
                                intent.putExtra("finished_year", String.valueOf(book_finished_year.get(position)));
                                intent.putExtra("book_cover", String.valueOf(book_cover.get(position)));
                                //intent.putExtra("is_last", isLast);
                                activity.startActivityForResult(intent,1);
                                return true;
                            case R.id.progress:
                                //handle menu2 click
                                intent = new Intent(context, ProgressActivity.class);
                                intent.putExtra("id", String.valueOf(book_id.get(position)));
                                intent.putExtra("title", String.valueOf(book_title.get(position)));
                                intent.putExtra("author", String.valueOf(book_author.get(position)));
                                intent.putExtra("pages", String.valueOf(book_pages.get(position)));
                                intent.putExtra("pages_position", String.valueOf(book_page_position.get(position)));
                                intent.putExtra("chapter", String.valueOf(book_chapter.get(position)));
                                intent.putExtra("chapter_position", String.valueOf(book_chapter_position.get(position)));
                                intent.putExtra("finished_day", String.valueOf(book_finished_day.get(position)));
                                intent.putExtra("finished_month", String.valueOf(book_finished_month.get(position)));
                                intent.putExtra("finished_year", String.valueOf(book_finished_year.get(position)));
                                intent.putExtra("book_cover", String.valueOf(book_cover.get(position)));
                                intent.putExtra("days_left", String.valueOf(nbOfDaysLeft));
                                activity.startActivityForResult(intent,1);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });


        holder.nb_page_today_txt.setText(String.valueOf(chapterToDoToday) + " chapter to do today :D");

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProgressActivity.class);

                Bundle extras = new Bundle();
                extras.putString("id", String.valueOf(book_id.get(position)));
                extras.putString("title", String.valueOf(book_title.get(position)));
                extras.putString("author", String.valueOf(book_author.get(position)));
                extras.putString("pages", String.valueOf(book_pages.get(position)));
                extras.putString("pages_position", String.valueOf(book_page_position.get(position)));
                extras.putString("chapter", String.valueOf(book_chapter.get(position)));
                extras.putString("chapter_position", String.valueOf(book_chapter_position.get(position)));
                extras.putString("finished_day", String.valueOf(book_finished_day.get(position)));
                extras.putString("finished_month", String.valueOf(book_finished_month.get(position)));
                extras.putString("finished_year", String.valueOf(book_finished_year.get(position)));
                extras.putString("book_cover", String.valueOf(book_cover.get(position)));
                extras.putString("last_entry_day", String.valueOf(book_last_entry_day.get(position)));
                extras.putString("last_entry_month", String.valueOf(book_last_entry_month.get(position)));
                extras.putString("last_entry_year", String.valueOf(book_last_entry_year.get(position)));
                extras.putString("expected_page", String.valueOf(book_expected_page_today.get(position)));
                extras.putString("expected_chap", String.valueOf(book_expected_chap_today.get(position)));
                extras.putString("last_entry_page", String.valueOf(book_last_entry_page.get(position)));
                extras.putString("last_entry_chap", String.valueOf(book_last_entry_chap.get(position)));
                intent.putExtras(extras);

                myDatabaseHelper myDB = new myDatabaseHelper(context);
                if (actualDate != last_entry_date ){
                    myDB.putExpectAvancement(book_id.get(position).toString(), String.valueOf(Integer.parseInt(book_last_entry_page.get(position).toString()) + pageToDoToday),
                            String.valueOf(Integer.parseInt(book_last_entry_chap.get(position).toString()) + chapterToDoToday));
                }
                activity.startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return book_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView book_title_txt, book_author_txt, pages_position_txt, chapter_position_txt, nb_page_today_txt, buttonViewOption, last_entry_view;
        ImageView book_cover_view;
        LinearLayout mainLayout;
        ProgressBar complete_progress_bar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_title_txt = itemView.findViewById(R.id.book_title_txt);
            book_author_txt = itemView.findViewById(R.id.book_author_txt);
            pages_position_txt = itemView.findViewById(R.id.pages_position_txt);
            chapter_position_txt = itemView.findViewById(R.id.chapter_position_txt);
            book_cover_view = itemView.findViewById(R.id.book_cover_view);
            nb_page_today_txt = itemView.findViewById(R.id.NbPageToDoToday);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
            complete_progress_bar = itemView.findViewById(R.id.complete_bar_row);
            last_entry_view = itemView.findViewById(R.id.last_entry_view);
        }
    }
}
