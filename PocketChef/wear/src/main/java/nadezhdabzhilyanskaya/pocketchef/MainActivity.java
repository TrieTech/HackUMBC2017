package nadezhdabzhilyanskaya.pocketchef;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;
import android.speech.RecognizerIntent;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends WearableActivity {
    public ListView mList;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    private final int TITLE_SCREEN = -2;
    private final int INGREEDIENT_SCREEN = -1;

    private Recipe rec;
    private int currentStep;
    private int totalSteps;
    private int lastStep;
    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        //mClockView = (TextView) findViewById(R.id.clock);

        currentStep = TITLE_SCREEN;
        lastStep = 0;
        firstTime = true;

        String name = "Easy Homemade Mashed Potatoes";
        String[] ingredientName = {"potatoes","salt","butter","pepper","hot milk"};
        String[] ingredientAmount = {"8", "1", "2","1","1/4"};
        Recipe.ServingType[] types= {Recipe.ServingType.NONE, Recipe.ServingType.TEASPOON, Recipe.ServingType.TABLESPOON, Recipe.ServingType.TEASPOON, Recipe.ServingType.CUP};
        String[] steps = {"Place potatoes in large saucepan; add enough water to cover. Add 3/4 teaspoon of the salt. Bring to a boil. Reduce heat to medium-low; cover loosely and boil gently for 15 to 20 minutes or until potatoes break apart easily when pierced with fork. Drain well.",
                "Return potatoes to saucepan; shake saucepan gently over low heat for 1 to 2 minutes to evaporate any excess moisture.",
                "Mash potatoes with potato masher until no lumps remain. Add margarine, pepper and remaining 1/4 teaspoon salt; continue mashing, gradually adding enough milk to make potatoes smooth and creamy."
        };
        String description = "Enjoy a classic all-time favorite - smooth and creamy mashed potatoes make the perfect side dish for any meal.";
        int prepTime = 25;
        int fullTime = 45;

        totalSteps = steps.length;
        rec = new Recipe(name,ingredientName,ingredientAmount,types,steps,description,prepTime,fullTime);


        startVoiceRecognitionActivity();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }



    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "What recipe do you want to make?");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            TextView text = (TextView) findViewById(R.id.recipe);
            text.setText(rec.getNameDescription());

           // getRequest();

           /* if (matches.contains("information")) {
                informationMenu();
            }*/
        }
    }

   /* public void getRequest() {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream in = null;
        try {
            URL url = new URL("http://chefaccesor.mybluemix.net/recipes");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.connect();
            //TextView text = (TextView) findViewById(R.id.recipe);
            //text.setText(urlConnection.toString());
            try {
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.getInputStream();
            }finally{
                urlConnection.disconnect();
            }
            //StringWriter writer = new StringWriter();
            //IOUtils.copy(in,writer);
            //String str = writer.toString();

            //TextView text = (TextView) findViewById(R.id.recipe);
            //text.setText(str);
            //readStream(in);
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            //Do nothing
        } catch (IOException e) {}
        /*try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            readStream(in);
        } finally {
            urlConnection.disconnect();
        }*/
    /*}

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(firstTime)
        {
            firstTime = false;
            currentStep++;
            changeStep();
            return true;
        }
        if(currentStep != INGREEDIENT_SCREEN && currentStep !=TITLE_SCREEN){lastStep = currentStep;}
        switch (keyCode) {
            case KeyEvent.KEYCODE_NAVIGATE_NEXT:
                if(currentStep == INGREEDIENT_SCREEN){currentStep = lastStep;}
                else {currentStep++;}
                changeStep();
                return true;
            case KeyEvent.KEYCODE_NAVIGATE_PREVIOUS:
                currentStep--;
                changeStep();
                return true;
            case KeyEvent.KEYCODE_NAVIGATE_IN:
                currentStep = INGREEDIENT_SCREEN;
                changeStep();
                return true;
            case KeyEvent.KEYCODE_NAVIGATE_OUT:
                currentStep = INGREEDIENT_SCREEN;
                changeStep();
                return true;
            default:
                //do nothing
                return super.onKeyDown(keyCode, event);
        }
    }

    private void changeStep()
    {
        TextView text = (TextView) findViewById(R.id.recipe);

        if(currentStep == totalSteps)
        {
            currentStep--;
            return;
        }
        if(currentStep < TITLE_SCREEN)
        {
            currentStep = TITLE_SCREEN;
            return;
        }
        if(currentStep == TITLE_SCREEN) {text.setText(rec.getNameDescription());}
        else if(currentStep == INGREEDIENT_SCREEN) {text.setText(rec.ingredentListToString());}
        else{text.setText(rec.stepsToString(currentStep));}
    }

}

