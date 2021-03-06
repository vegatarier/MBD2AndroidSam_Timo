package nl.samtimo.pubcrawl.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import nl.samtimo.pubcrawl.ui.ColorActivity;

/**
 * Created by admin on 10-04-16.
 */
public class ColorAppCompatActivity extends AppCompatActivity implements ColorActivity {
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        updateBackgroundColor();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateBackgroundColor();
    }

    public void updateBackgroundColor(){
        //setting background
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String backgroundColor = preferences.getString("background", "#ffffff");
        getWindow().getDecorView().setBackgroundColor(Color.parseColor(backgroundColor));
    }
}
