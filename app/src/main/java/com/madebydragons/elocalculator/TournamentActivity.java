package com.madebydragons.elocalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.EditText;
import android.widget.ImageButton;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Vector;

public class TournamentActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TournamentActivity";

    private int insertionRow = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tournament, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.tournament:
                intent = new Intent(this, MainActivity.class);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);
        TableLayout tl = (TableLayout) findViewById(R.id.tournament_table);
        EditText initial_score = (EditText)findViewById(R.id.your_rating);
        initial_score.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                recalculateFinalElo();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        // The last row (child of table) is the add button, therefore when a row is added is immediately before it
        insertionRow = tl.getChildCount()-1;
        insertRow(null);
    }
    @Override
    public void onResume(){
        super.onResume();
        recalculateFinalElo();

    }

    public void recalculateFinalElo(){
        Vector<Integer> elos = new Vector<Integer>();
        ArrayList<Double> results = new ArrayList<Double>();
        TableLayout tl = (TableLayout) findViewById(R.id.tournament_table);
        // starting with FIRST_MATCH_COlUMN, the first cell of every row in the table is
        // an elo score for a contender. Loop through, accumulating elos as ints in vector
        // similarly, the third cell of every row in the table is
        // an slider with the result of the match. Loop through, accumulating results as as doubles in vector
        for(int i=3;i < tl.getChildCount()-1;i++){
            assert tl.getChildAt(i) instanceof TableRow;
            TableRow tr = (TableRow) tl.getChildAt(i);
            assert tr.getChildAt(0) instanceof EditText;
            elos.add(EditTextToIntValue((EditText) tr.getChildAt(0)));
            assert tr.getChildAt(2) instanceof SeekBar;
            results.add(SeekBarToDoubleValue((SeekBar)tr.getChildAt(2)));
        }
        int finalRating = 0;
        try {
            int initialRating = EditTextToIntValue((EditText) findViewById(R.id.your_rating));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean use_standard_k = prefs.getBoolean("use_standard_k", true);
            String KFactorPreference;
            KFactor k;
            if(use_standard_k){
                KFactorPreference = prefs.getString("standard_k",KFactorFactory.STANDARD_K_FACTOR_CHESS_COM);
                k = KFactorFactory.createKFactor(KFactorPreference);
            }
            else {
                try {
                    k = KFactorFactory.createKFactor(Double.parseDouble(prefs.getString("custom_k", "16.0")));
                } catch (NumberFormatException e) {
                    // Todo: maybe tell user not to try to break app by changing preference values to unparsable numbers
                    // despite the very reasonable input verifications.
                    k = KFactorFactory.createKFactor(16.0);
                }
            }


            EloCalculator elo = new EloCalculator(k);
            finalRating = (int) elo.tournament(
                    initialRating, elos.toArray(new Integer[elos.size()]), results.toArray(new Double[results.size()]));
            // If nothing has been really populated yet, keep final rating same as initial
            if(elos.size()==1 && elos.get(0).intValue()==0)
                finalRating=initialRating;

        } catch (InvalidTournamentData invalidTournamentData) {
            invalidTournamentData.printStackTrace();
        }
        catch (UnknownKFactorIdentifierException unknownKFactorIdentifierException) {
            Log.d(LOG_TAG,"Something is seriously wrong. K factor IDs should be static and in string.xml");
            unknownKFactorIdentifierException.printStackTrace();
        }
        catch (InvalidStaticKFactorValueException invalidStaticKFactorValue) {
            Log.d(LOG_TAG,"An invalid value for custom K factor was not caught by validation.");
            invalidStaticKFactorValue.printStackTrace();
        }
        if(EditTextToIntValue((EditText)findViewById(R.id.your_rating))>0)
            ((EditText) findViewById(R.id.your_rating_after)).setText(new Integer(finalRating).toString());
    }

    /* Convert value inside an edit text to integer. Empty or invalid formatting is returned as
     the int 0 instead of an exception */
    private static int EditTextToIntValue(EditText a){
        int value = 0;
        String s = a.getText().toString();
        if(s.isEmpty())
            return value;
        try {
            value = Integer.parseInt(s);
        }
        catch(NumberFormatException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return value;
    }

    /* Convert value on a seekbar on range [0,2] to a double of value 0, 0.5, or 1, corresponding
     to the outcome of the match (0 loose, .5 draw)  . Empty or invalid formatting is return as 0.0
     instead of an exception. */
    private static double SeekBarToDoubleValue(SeekBar s){
        if(s == null)
            return 0.0;
        if(s.getProgress()==1)
            return 0.5;
        else if(s.getProgress()==2)
            return 1.0;
        return 0.0;
    }

    public int findRowIndex(TableLayout tl, TableRow tr){
        int result = -1;
        for(int i=0; result==-1 && i < tl.getChildCount();i++)
            if(tl.getChildAt(i)==tr)
                result = i;
        return result;
    }

    public void deleteRow(View view)
    {
        ImageButton b = null;
        if(view instanceof ImageButton){
            b = (ImageButton) view;
            if(b.getParent() instanceof TableRow){
                TableRow tr = (TableRow)b.getParent();
                TableLayout tl = (TableLayout) findViewById(R.id.tournament_table);
                if(findRowIndex(tl,tr)!=-1)
                    tl.removeViewAt(findRowIndex(tl,tr));
                insertionRow--;
            }
        }
        recalculateFinalElo();
    }

    public void insertRow(View view)
    {
        TableLayout tl = (TableLayout) findViewById(R.id.tournament_table);
        TableRow tr =  new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        populateWithNewContents(tr);
        tl.addView(tr,insertionRow,new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //TODO: fine tune enable button for last match standing
        insertionRow++;

    }


    public void populateWithNewContents(TableRow tr){
        EditText opponent_score = new EditText(this);
        opponent_score.setEnabled(true);
        opponent_score.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                recalculateFinalElo();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        opponent_score.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        opponent_score.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        tr.addView(opponent_score);

        ImageView loose = new ImageView(this);
        loose.setImageResource(R.drawable.ic_loose);
        loose.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(loose);

        SeekBar sb = new SeekBar(this);
        sb.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        sb.setMinimumWidth(80);
        sb.setProgress(1);
        sb.setIndeterminate(false);
        sb.setMax(2);
        sb.setBottom(0);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               recalculateFinalElo();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });


        tr.addView(sb);

        ImageView win = new ImageView(this);
        win.setImageResource(R.drawable.ic_win);
        win.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(win);

        ImageButton b = new ImageButton(this);
        b.setImageResource(android.R.drawable.ic_delete);
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        b.setBackgroundColor(android.R.color.transparent);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRow(v);
            }
        });
        tr.addView(b);
        opponent_score.requestFocus();
    }
}
