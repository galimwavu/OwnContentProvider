package com.example.martin.owncontentprovider;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Custom_word extends ActionBarActivity {

    // WordsProvider wordsProvider;
    //private final String SELECTION  = wordsProvider.ENG_NAME + " LIKE ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CustomWordFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_word, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
//    public static class CustomWordFragment extends Fragment {
//
//        public CustomWordFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.activity_custom_word, container, false);
//            return rootView;
//        }
//    }

    public void onClickAddTitle(View view) {
//---add a book---

            if(((EditText)findViewById(R.id.eng_name_text2)).getText().toString().equals("")
                    && ((EditText)findViewById(R.id.eng_meaning_text2)).getText().toString().equals("")){

                Toast.makeText(getBaseContext(), "english name and the meaning should not be empty", Toast.LENGTH_LONG).show();
            }else {
                ContentValues values = new ContentValues();
                values.put(WordsProvider.ENG_NAME, ((EditText)
                        findViewById(R.id.eng_name_text2)).getText().toString());
                values.put(WordsProvider.ENG_MEANING, ((EditText)
                        findViewById(R.id.eng_meaning_text2)).getText().toString());
                values.put(WordsProvider.LUG_NAME, ((EditText)
                        findViewById(R.id.lug_name_text2)).getText().toString());
                values.put(WordsProvider.LUG_MEANING, ((EditText)
                        findViewById(R.id.lug_meaning_text2)).getText().toString());
                Uri uri = getContentResolver().insert(
                        WordsProvider.CONTENT_URI, values);
                Toast.makeText(getBaseContext(), uri.toString(),
                        Toast.LENGTH_LONG).show();
            }


    }
    //back button
    public void onClickBack(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
