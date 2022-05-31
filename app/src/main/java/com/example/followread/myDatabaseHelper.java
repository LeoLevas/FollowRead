package com.example.followread;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;

class myDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "BookLibrary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_library";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "book_title";
    private static final String COLUMN_AUTHOR = "book_author";
    private static final String COLUMN_PAGES = "book_pages";
    private static final String COLUMN_POSITION = "book_position";
    private static final String COLUMN_CHAPTER = "book_chapter";
    private static final String COlUMN_CHAPTER_POSITION = "book_chapter_position";
    private static final String COLUMN_FINISHED_DAY = "book_finished_day";
    private static final String COLUMN_FINISHED_MONTH = "book_finished_month";
    private static final String COLUMN_FINISHED_YEAR = "book_finished_year";
    private static final String COLUMN_COVER = "book_cover";
    private static final String COLUMN_LAST_ENTRY_DAY = "book_last_entry_day";
    private static final String COLUMN_LAST_ENTRY_MONTH = "book_last_entry_month";
    private static final String COLUMN_LAST_ENTRY_YEAR = "book_last_entry_year";
    private static final String COLUMN_EXPECTED_PAGE_TODAY = "book_expect_page_today";
    private static final String COLUMN_EXPECTED_CHAP_TODAY = "book_expect_chap_today";
    private static final String COLUMN_LAST_ENTRY_PAGE = "book_last_entry_page";
    private static final String COLUMN_LAST_ENTRY_CHAP = "book_last_entry_chap";


    myDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_AUTHOR + " TEXT, " +
                    COLUMN_PAGES + " INTEGER, " +
                    COLUMN_POSITION + " INTEGER, " +
                    COLUMN_CHAPTER + " INTEGER, " +
                    COlUMN_CHAPTER_POSITION + " INTEGER, " +
                    COLUMN_FINISHED_DAY + " INTEGER, " +
                    COLUMN_FINISHED_MONTH + " INTEGER, " +
                    COLUMN_FINISHED_YEAR + " INTEGER, " +
                    COLUMN_LAST_ENTRY_DAY + " TEXT, " +
                    COLUMN_LAST_ENTRY_MONTH + " TEXT, " +
                    COLUMN_LAST_ENTRY_YEAR + " TEXT, " +
                    COLUMN_COVER + " BLOB, " +
                    COLUMN_EXPECTED_PAGE_TODAY + " TEXT, " +
                    COLUMN_EXPECTED_CHAP_TODAY + " TEXT, " +
                    COLUMN_LAST_ENTRY_PAGE + " TEXT, " +
                    COLUMN_LAST_ENTRY_CHAP + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addBook(String title, String author, int pages, int position, int chapter, int chapter_position,
                 int finished_day, int finished_month, int finished_year, byte[] book_cover,
                 String last_entry_day, String last_entry_month, String last_entry_year, int expected_page_today, int expected_chap_today,
                 Integer last_entry_page, Integer last_entry_chap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_PAGES, pages);
        cv.put(COLUMN_POSITION, position);
        cv.put(COLUMN_CHAPTER, chapter);
        cv.put(COlUMN_CHAPTER_POSITION, chapter_position);
        cv.put(COLUMN_FINISHED_DAY, finished_day);
        cv.put(COLUMN_FINISHED_MONTH, finished_month);
        cv.put(COLUMN_FINISHED_YEAR, finished_year);
        cv.put(COLUMN_COVER, book_cover);
        cv.put(COLUMN_LAST_ENTRY_DAY, last_entry_day);
        cv.put(COLUMN_LAST_ENTRY_MONTH, last_entry_month);
        cv.put(COLUMN_LAST_ENTRY_YEAR, last_entry_year);
        cv.put(COLUMN_EXPECTED_PAGE_TODAY, expected_page_today);
        cv.put(COLUMN_EXPECTED_CHAP_TODAY, expected_chap_today);
        cv.put(COLUMN_LAST_ENTRY_PAGE, last_entry_page);
        cv.put(COLUMN_LAST_ENTRY_CHAP, last_entry_chap);
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast .LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateData(String row_id, String title, String author, String pages, String position, String chapter, String chapter_position,
                    String finished_day, String finished_month, String finished_year, byte[] book_cover){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_PAGES, pages);
        cv.put(COLUMN_POSITION, position);
        cv.put(COLUMN_CHAPTER, chapter);
        cv.put(COlUMN_CHAPTER_POSITION, chapter_position);
        cv.put(COLUMN_FINISHED_DAY, finished_day);
        cv.put(COLUMN_FINISHED_MONTH, finished_month);
        cv.put(COLUMN_FINISHED_YEAR, finished_year);
        cv.put(COLUMN_COVER, book_cover);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to update.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

    void updateProgress(String row_id, String page_position, String chapter_position, String entry_day, String entry_month, String entry_year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_POSITION, page_position);
        cv.put(COlUMN_CHAPTER_POSITION, chapter_position);
        cv.put(COLUMN_LAST_ENTRY_DAY, entry_day);
        cv.put(COLUMN_LAST_ENTRY_MONTH, entry_month);
        cv.put(COLUMN_LAST_ENTRY_YEAR, entry_year);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to update.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

    void putExpectAvancement(String row_id, String expected_page_done, String expected_chapter_done){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXPECTED_PAGE_TODAY, expected_page_done);
        cv.put(COLUMN_EXPECTED_CHAP_TODAY, expected_chapter_done);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
    }
    void putLastEntry(String row_id, String last_page_entry, String last_chapter_entry){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LAST_ENTRY_PAGE, last_page_entry);
        cv.put(COLUMN_LAST_ENTRY_CHAP, last_chapter_entry);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (result == -1){
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
         SQLiteDatabase db = this.getWritableDatabase();
         db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
