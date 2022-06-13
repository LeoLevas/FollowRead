package com.example.followread;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
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

    private static final String COLUMN_HIGHLIGHT = "highlight_content";
    private static final String COLUMN_HIGHLIGHT_TITLE = "highlight_title";
    private static final String COLUMN_HIGHLIGHT_PAGE = "highlight_page";
    private static final String COLUMN_HIGHLIGHT_CHAPTER = "highlight_chapter";
    private static final String COLUMN_HIGHLIGHT_IMAGE = "highlight_image";


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

    @SuppressLint("Range")
    void addBook(String title, String author, int pages, int position, int chapter, int chapter_position,
                 int finished_day, int finished_month, int finished_year, byte[] book_cover,
                 String last_entry_day, String last_entry_month, String last_entry_year, int expected_page_today, int expected_chap_today,
                 Integer last_entry_page, Integer last_entry_chap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //region put content values
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
        //endregion
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast .LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added successfully!", Toast.LENGTH_SHORT).show();
        }

        createHighlightDB(title, db);
    }

    void addHighlight(String table_name, String title, String chapter, String page, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HIGHLIGHT, content);
        cv.put(COLUMN_HIGHLIGHT_CHAPTER, chapter);
        cv.put(COLUMN_HIGHLIGHT_PAGE, page);
        cv.put(COLUMN_HIGHLIGHT_TITLE, title);
        cv.put(COLUMN_HIGHLIGHT_IMAGE, (byte[]) null);

        long result = db.insert(table_name, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast .LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Added highlight successfully!", Toast.LENGTH_SHORT).show();
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

    void deleteBookTable(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        table_name = table_name.replaceAll("\\s+", "_");

        db.execSQL("DROP TABLE " + table_name);
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = readAllData();


        int lastID = 0;
        if(cursor.moveToLast()){
            lastID = cursor.getInt(0);
        }

        if(Integer.parseInt(row_id) == lastID) {
            long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
            if (result == -1) {
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            int nextID = 0;
            ContentValues cv = new ContentValues();
            cursor.moveToPosition(Integer.parseInt(row_id) - 1);
            cursor.moveToNext();
            nextID = cursor.getInt(0);
            cv.put(COLUMN_TITLE, cursor.getString(1));
            cv.put(COLUMN_AUTHOR, cursor.getString(2));
            cv.put(COLUMN_PAGES, cursor.getInt(3));
            cv.put(COLUMN_POSITION, cursor.getInt(4));
            cv.put(COLUMN_CHAPTER, cursor.getInt(5));
            cv.put(COlUMN_CHAPTER_POSITION, cursor.getInt(6));
            cv.put(COLUMN_FINISHED_DAY, cursor.getInt(7));
            cv.put(COLUMN_FINISHED_MONTH, cursor.getInt(8));
            cv.put(COLUMN_FINISHED_YEAR, cursor.getInt(9));
            cv.put(COLUMN_LAST_ENTRY_DAY, cursor.getString(10));
            cv.put(COLUMN_LAST_ENTRY_MONTH, cursor.getString(11));
            cv.put(COLUMN_LAST_ENTRY_YEAR, cursor.getString(12));
            cv.put(COLUMN_COVER, cursor.getBlob(13));
            cv.put(COLUMN_EXPECTED_PAGE_TODAY, cursor.getString(14));
            cv.put(COLUMN_EXPECTED_CHAP_TODAY, cursor.getString(15));
            cv.put(COLUMN_LAST_ENTRY_PAGE, cursor.getString(16));
            cv.put(COLUMN_LAST_ENTRY_CHAP, cursor.getString(17));
            cursor.close();
            long update = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
            deleteOneRow(String.valueOf(nextID));
        }
    }

    void deleteAllData(){
         SQLiteDatabase db = this.getWritableDatabase();
         db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    void createHighlightDB(String title, SQLiteDatabase db){
        title = title.replaceAll("\\s+", "_");
        String query = "CREATE TABLE IF NOT EXISTS " + title +
                " (" + COLUMN_ID +" INTEGER PRIMARY KEY, " +
                COLUMN_HIGHLIGHT + " TEXT, " +
                COLUMN_HIGHLIGHT_PAGE + " TEXT, " +
                COLUMN_HIGHLIGHT_TITLE + " TEXT, " +
                COLUMN_HIGHLIGHT_CHAPTER + " TEXT, " +
                COLUMN_HIGHLIGHT_IMAGE + " BLOB);";
        db.execSQL(query);
    }

    public ArrayList<String> IdArray() {
        ArrayList<String> stringList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT rowid FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                String id = (c.getString(0));

                stringList.add(id);
                c.moveToNext();
            }
        }
        return stringList;
    }
}
