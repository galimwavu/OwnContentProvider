    package com.example.martin.owncontentprovider;

    import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

    /**
     * Created by Martin on 17/03/2015.
     */
    public class WordsProvider extends ContentProvider {

        static final String PROVIDER_NAME =
                "com.example.martin.provider.words";
        static final Uri CONTENT_URI =
                Uri.parse("content://" + PROVIDER_NAME + "/words");
        static final String _ID = "_id";
        static final String ENG_NAME = "eng_name";
        static final String ENG_MEANING = "eng_meaning";
        static final String LUG_NAME = "lug_name";
        static final String LUG_MEANING = "lug_meaning";

        static final int WORDS = 1;
        static final int WORD_ID = 2;
        private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        static {
            uriMatcher.addURI(PROVIDER_NAME, "words", WORDS);
            uriMatcher.addURI(PROVIDER_NAME, "words/#", WORD_ID);
        }

        //---for database use---
        SQLiteDatabase wordsDB;
        static final String DATABASE_NAME = "dictionary";
        static final String DATABASE_TABLE = "words";
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_CREATE =
                "create table "+ DATABASE_TABLE + "(_id integer primary key autoincrement, "
                        + "eng_name text not null, eng_meaning text not null, " +
                        "lug_name text not null, lug_meaning text not null);";

        private static class DatabaseHelper extends SQLiteOpenHelper{
            DatabaseHelper(Context context){
                super(context,DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(DATABASE_CREATE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                Log.w("Database", "Upgrading database" + oldVersion + " to " + newVersion );
                db.execSQL("DROP TABLE IF EXISTS titles");
                onCreate(db);
            }
        }

        @Override
        public boolean onCreate() {
            Context context = getContext();
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            wordsDB = dbHelper.getWritableDatabase();
            return (wordsDB == null)? false:true;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
            sqlBuilder.setTables(DATABASE_TABLE);
            if (uriMatcher.match(uri) == WORD_ID)
            //---if getting a particular word---
                sqlBuilder.appendWhere(
                        _ID + " = " + uri.getPathSegments().get(1));
            if (sortOrder==null || sortOrder.equals(""))
                sortOrder = ENG_NAME;
            Cursor c = sqlBuilder.query(
                    wordsDB,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    //sortOrder
                    null);
            //---register to watch a content URI for changes---
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }

        @Override
        public String getType(Uri uri) {
            switch (uriMatcher.match(uri)){
            //---get all words---
                case WORDS:
                    return "vnd.android.cursor.dir/vnd.marting.words ";
            //---get a particular word---
                case WORD_ID:
                    return "vnd.android.cursor.item/vnd.marting.words ";
                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            //---add a new word---
            long rowID = wordsDB.insert(
                    DATABASE_TABLE,
                    "",
                    values);
            //---if added successfully---
            if (rowID>0)
            {
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            // arg0 = uri
            // arg1 = selection
            // arg2 = selectionArgs
            int count=0;
            switch (uriMatcher.match(uri)){
                case WORDS:
                    count = wordsDB.delete(
                            DATABASE_TABLE,
                            selection,
                            selectionArgs);
                    break;
                case WORD_ID:
                    String id = uri.getPathSegments().get(1);
                    count = wordsDB.delete(
                            DATABASE_TABLE,
                            _ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""),
                    selectionArgs);
                    break;
                default: throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }


        @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            int count = 0;
            switch (uriMatcher.match(uri)){
                case WORDS:
                    count = wordsDB.update(
                            DATABASE_TABLE,
                            values,
                            selection,
                            selectionArgs);
                    break;
                case WORD_ID:
                    count = wordsDB.update(
                            DATABASE_TABLE,
                            values,
                            _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                        selection + ')' : ""),
                    selectionArgs);
                    break;
                default: throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }
