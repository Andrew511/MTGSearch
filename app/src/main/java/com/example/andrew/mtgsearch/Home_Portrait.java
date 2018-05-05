package com.example.andrew.mtgsearch;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Home_Portrait extends AppCompatActivity implements RecentCardFragment.OnListFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int PICK_IMAGE = 2;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/com.example.andrew.mtgsearch/";
    RecentCardFragment recents =  new RecentCardFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_home_landscape);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.viewedCards, recents);
            fragmentTransaction.commit();
        } else {
            setContentView(R.layout.activity_home_portrait);
        }
    }


    public void searchCard(View v) {

        RequestQueue searchQueue = Volley.newRequestQueue(this);
        String cardName = ((EditText) findViewById(R.id.cardName)).getText().toString().replace(' ', '+');
        String url = "https://api.magicthegathering.io/v1/cards?name=%22" + cardName + "%22";// + "&" + ":page=1&:pageSize=1";
        JsonObjectRequest cardRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray data = response.getJSONArray("cards");
                            JSONObject cardData = data.getJSONObject(data.length() - 1);
                            RecentCardDBContract.RecentCardDBHelper dbHelper = new RecentCardDBContract.RecentCardDBHelper(getApplicationContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            String cardName = cardData.opt("name").toString();
                            String cardManaCost = cardData.opt("manaCost").toString();
                            String cardPower = cardData.opt("power") != null ? cardData.opt("power").toString() : "No Power";
                            String cardToughness = cardData.opt("toughness") != null ?  cardData.opt("toughness").toString() : "No Toughness";
                            String cardText = cardData.opt("originalText") != null ? cardData.opt("originalText").toString() : cardData.opt("text").toString();
                            String cardType = cardData.opt("originalType") != null ? cardData.opt("originalType").toString() : cardData.opt("type").toString();
                            String cardImgURL = cardData.opt("imageUrl").toString();

                            //insert card into viewed card database
                            ContentValues cardValues = new ContentValues();
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_NAME,
                                    cardName);
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_MANACOST,
                                    cardManaCost);
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_POWER,
                                    cardPower);
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_TOUGHNESS,
                                    cardToughness);
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_TEXT,
                                    cardText);
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_TYPE,
                                    cardType);
                            cardValues.put(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_URL,
                                    cardImgURL);
                            long cardId = db.insert(
                                    RecentCardDBContract.RecentCardEntry.TABLE_NAME,
                                    null,
                                    cardValues);



                            String ruling;
                            String date;
                            ArrayList<RulingObject> rulings = new ArrayList<RulingObject>();
                            try {
                                JSONArray array = cardData.getJSONArray("rulings");


                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject row = array.getJSONObject(i);
                                    date = row.getString("date");
                                    ruling = row.getString("text");
                                    //insert rulings into database for card


                                    ContentValues rulingValues = new ContentValues();
                                    rulingValues.put(RecentCardDBContract.RulingsEntry.COLUMN_NAME_CARDID,
                                            cardId);
                                    rulingValues.put(RecentCardDBContract.RulingsEntry.COLUMN_NAME_DATE,
                                            date);
                                    rulingValues.put(RecentCardDBContract.RulingsEntry.COLUMN_NAME_RULING,
                                            ruling);
                                    db.insert(
                                            RecentCardDBContract.RulingsEntry.TABLE_NAME,
                                            null,
                                            rulingValues);


                                    rulings.add(new RulingObject(date, ruling));
                                }
                            }
                            catch (JSONException e) {
                                rulings = new ArrayList<RulingObject>();
                                rulings.add(new RulingObject("", "No rulings for this card"));
                            }

                            CardObject card = new CardObject(cardName, cardManaCost, cardPower, cardToughness, cardText, cardType, cardImgURL, rulings);

                            Intent cardFound = new Intent(getBaseContext(), Card_Stats.class);
                            cardFound.putExtra("CARD", card);
                            startActivity(cardFound);
                        } catch (Exception e) {
                            Log.e("CardParse", "Error parsing json object for card object");
                            Toast.makeText(getApplicationContext(), "Error retrieving card data, Card may not exist or may be entered incorrectly.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error retrieving card data", Toast.LENGTH_SHORT).show();
            }
        });

        searchQueue.add(cardRequest);
    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == PICK_IMAGE) && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //ToDo Hook up OCR library to analyze photo returned here.

        }
    }

    public void selectPhoto(View v) {
           Intent intent = new Intent();
           intent.setType("image/*");
           intent.setAction(Intent.ACTION_GET_CONTENT);
           startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }


    private String extractText(Bitmap bitmap) throws Exception
    {
        TessBaseAPI tessBaseApi = new TessBaseAPI();
        tessBaseApi.init(DATA_PATH, "eng");
        tessBaseApi.setImage(bitmap);
        String extractedText = tessBaseApi.getUTF8Text();
        tessBaseApi.end();
        return extractedText;
    }


    //public void ItemClickListener

    //ToDo add Recent query results to landscape in local database
}
