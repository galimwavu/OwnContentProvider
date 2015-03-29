package com.example.martin.owncontentprovider;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    WordsProvider wordsProvider;
    private final String SELECTION  = wordsProvider.ENG_NAME + " LIKE ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WordFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.next){
            //Intent intent =
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//    }


    public void onClickRetrieveTitles(View view) {
        //---retrieve the titles---
//        Uri allTitles = Uri.parse("content://com.example.martin.provider.words/words");
        //---retrieve an item
        Uri item = Uri.parse("content://com.example.martin.provider.words/words");
        Cursor c;

        //array containing the search arguments
        String[] mSelectionArgs = {"%" + ((EditText) findViewById(R.id.search_input)).getText().toString() + "%"};
        //mSelectionArgs[0] = "%Boy%";

        if (android.os.Build.VERSION.SDK_INT < 11) {
            //---before Honeycomb---

            c = managedQuery(item, null, null, null, null);
        } else {
            //---Honeycomb and later---

            CursorLoader cursorLoader = new CursorLoader(
                    this,
                    item, null, SELECTION, mSelectionArgs,
                    null);
            c = cursorLoader.loadInBackground();
        }

        ArrayList<String> dataList = new ArrayList<>();
        //ArrayList<String> searchList = wordsProvider.SearchList();

        try {
            if (c.moveToFirst()) {
                do {
                    dataList.add(c.getString(c.getColumnIndex(WordsProvider.ENG_NAME)));
                    dataList.add(c.getString(c.getColumnIndex(WordsProvider.ENG_MEANING)));
                    dataList.add(c.getString(c.getColumnIndex(WordsProvider.LUG_NAME)));
                    dataList.add(c.getString(c.getColumnIndex(WordsProvider.LUG_MEANING)));

                } while (c.moveToNext());
            }

            ((TextView) findViewById(R.id.eng_name_view)).setText("English meaning of "+ dataList.get(0));
            ((TextView) findViewById(R.id.eng_meaning_view)).setText(dataList.get(1));
            ((TextView) findViewById(R.id.lug_name_view)).setText("Luganda Meaning of " + dataList.get(2));
            ((TextView) findViewById(R.id.lug_meaning_view)).setText(dataList.get(3));

        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Word Not Found", Toast.LENGTH_LONG).show();
            ((TextView) findViewById(R.id.eng_name_view)).setText("");
            ((TextView) findViewById(R.id.eng_meaning_view)).setText("");
            ((TextView) findViewById(R.id.lug_name_view)).setText("");
            ((TextView) findViewById(R.id.lug_meaning_view)).setText("");


        }
    }

    public void onClickAddTitle(View view) {
//---go to the next screen to insert a new word---
        Intent intent = new Intent(getApplicationContext(),Custom_word.class);
        startActivity(intent);
    }
}
