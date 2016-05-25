package com.madebydragons.elocalculator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.Intent;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    // An arbitrary value set by Elo to make final numbers behave in a human-readable range
    // see wikipedia article
    private static double M_VALUE = 400.0;
    private static String LOG_TAG= "MainActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.tournament:
                intent = new Intent(this, TournamentActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        updateWithNewRatings();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText p1 = (EditText) findViewById(R.id.your_rating);
        final EditText p2 = (EditText) findViewById(R.id.opponents_rating);

        p1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateWithNewRatings();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }


        });
        p2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateWithNewRatings();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }

        });
        updateWithInvalidRatings();

    }

    public void updateWithNewRatings() {
        String p1_s = ((EditText) findViewById(R.id.your_rating)).getText().toString();
        String p2_s = ((EditText) findViewById(R.id.opponents_rating)).getText().toString();

        if(p1_s.isEmpty() || p2_s.isEmpty()) {
            updateWithInvalidRatings();
            return;
        }

        double p1 = Double.parseDouble(p1_s);
        double p2 = Double.parseDouble(p2_s);

        Button w = ((Button)findViewById(R.id.win_button));
        Button d = ((Button)findViewById(R.id.draw_button));
        Button l = ((Button)findViewById(R.id.loose_button));

        w.setText(new Integer(new Double(points(p1, p2, 1)).intValue()).toString());
        d.setText(new Integer(new Double(points(p1, p2, 0.5)).intValue()).toString());
        l.setText(new Integer(new Double(points(p1, p2,0)).intValue()).toString());
        w.setEnabled(true);
        l.setEnabled(true);
        d.setEnabled(true);
    }

    public void updateWithInvalidRatings(){
        Button w = ((Button)findViewById(R.id.win_button));
        w.setText("");
        w.setEnabled(false);
        Button d = ((Button)findViewById(R.id.draw_button));
        d.setText("");
        d.setEnabled(false);
        Button l = ((Button)findViewById(R.id.loose_button));
        l.setText("");
        l.setEnabled(false);
    }

    public double points(double p1,double p2,double actual){
        double expected_p1 = 1.0 / (1 + Math.pow(10.0, (p2-p1)/M_VALUE));
        KFactor k = null;
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String KFactorPreference = prefs.getString("standard_k", KFactorFactory.STANDARD_K_FACTOR_APPROX_FIDE);
            k = KFactorFactory.createKFactor(KFactorPreference);
        }
        catch (UnknownKFactorIdentifierException unknownKFactorIdentifierException) {
            Log.d(LOG_TAG, "Something is seriously wrong. K factor IDs should be static and always found");
            unknownKFactorIdentifierException.printStackTrace();
        }
        assert k != null;
        return Math.round(k.K((int)p1)*(actual - expected_p1));
    }

    // 1.0 for win, 0 loose, 0.5 draw
    public void updateAfterMatch(double actual){
        String p1_s = ((EditText) findViewById(R.id.your_rating)).getText().toString();
        String p2_s = ((EditText) findViewById(R.id.opponents_rating)).getText().toString();
        if(p1_s.isEmpty() || p2_s.isEmpty()) {
            updateWithInvalidRatings();
            return;
        }

        double p1 = Double.parseDouble(p1_s);
        double p2 = Double.parseDouble(p2_s);

        // notice that points  returns negative values for loses
        double updated_p1 = p1 + new Double(points(p1, p2,actual)).intValue();
        double updated_p2 = p2 - new Double(points(p1, p2,actual)).intValue();
        ((EditText) findViewById(R.id.opponents_rating)).setText(new Integer(new Double(updated_p2).intValue()).toString());
        ((EditText) findViewById(R.id.your_rating)).setText(new Integer(new Double(updated_p1).intValue()).toString());
    }

    public void updateWithLoss(View view) {
        updateAfterMatch(0);
    }

    public void updateWithDraw(View view) {
        updateAfterMatch(0.5);
    }

    public void updateWithWin(View view) {
        updateAfterMatch(1.0);
    }

}
