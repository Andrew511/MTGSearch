package com.example.andrew.mtgsearch;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Home_Portrait extends AppCompatActivity implements RecentCardFragment.OnListFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int PICK_IMAGE = 2;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory() + "/MTGSearch/";
    private static final String TESSDATA = "tessdata";
    RecentCardFragment recents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_home_landscape);
            recents = (RecentCardFragment) getSupportFragmentManager().findFragmentById(R.id.viewedCards);

            if (recents == null) {
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                recents = new RecentCardFragment();
                fragmentTransaction.add(R.id.viewedCards, recents);
                fragmentTransaction.commit();
            }
        } else {
            setContentView(R.layout.activity_home_portrait);
        }
    }


    public void submitSearch(View v) {
        String cardName = ((EditText) findViewById(R.id.cardName)).getText().toString();
        searchCard(cardName);
    }

    public void searchCard(String cardName) {

        RequestQueue searchQueue = Volley.newRequestQueue(this);
        try {
            cardName = URLEncoder.encode(cardName, "utf-8");
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
                                String cardManaCost = cardData.opt("manaCost") != null ? cardData.opt("manaCost").toString() : "No Mana Cost";
                                String cardPower = cardData.opt("power") != null ? cardData.opt("power").toString() : "No Power";
                                String cardToughness = cardData.opt("toughness") != null ? cardData.opt("toughness").toString() : "No Toughness";
                                String cardText = cardData.opt("originalText") != null ? cardData.opt("originalText").toString() : cardData.opt("text").toString();
                                String cardType = cardData.opt("originalType") != null ? cardData.opt("originalType").toString() : cardData.opt("type").toString();
                                String cardImgURL = cardData.opt("imageUrl").toString();

                                //insert card into viewed card database
                                boolean duplicate = false;
                                long cardId = 0;
                                try {
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
                                    cardId = db.insertOrThrow(
                                            RecentCardDBContract.RecentCardEntry.TABLE_NAME,
                                            null,
                                            cardValues);
                                } catch (SQLiteConstraintException e) {
                                    Log.e("database", "Card already exists in database");
                                    duplicate = true;
                                }


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

                                        if (!duplicate) {
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
                                        }


                                        rulings.add(new RulingObject(date, ruling));
                                    }
                                } catch (JSONException e) {
                                    rulings = new ArrayList<RulingObject>();
                                    rulings.add(new RulingObject("", "No rulings for this card"));
                                }

                                CardObject card = new CardObject(cardName, cardManaCost, cardPower, cardToughness, cardText, cardType, cardImgURL, rulings);
                                if (!duplicate) {
                                    recents.addCard(card);
                                    recents.cardsUpdated();
                                }

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
        catch (UnsupportedEncodingException e) {
            Log.e("urlEncoding", cardName + " could not be URL encoded with the given encoding");
        }
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

            Uri uri = data.getData();
            Log.i("home_portrait", "onActivityResult: file path : " + uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 750, 1050, false);
                String cardName = extractText(bitmap);
                ((EditText)findViewById(R.id.cardName)).setText(cardName);
                searchCard(cardName);

            } catch (Exception e) {
                Log.e("ocr", "Error extracting card name from image");
                Toast.makeText(this, "Error extracting card name from text, please enter name manually", Toast.LENGTH_SHORT).show();
            }

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
        prepareTesseract();
        bitmap = cropCardToTitle(bitmap);
        bitmap = imageProcessToBlackWhite(bitmap);
        TessBaseAPI tessBaseApi = new TessBaseAPI();
        tessBaseApi.init(DATA_PATH, "eng");
        tessBaseApi.setImage(bitmap);
        String extractedText = tessBaseApi.getUTF8Text();
        tessBaseApi.end();
        return extractedText;
    }

    private void prepareDirectory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("prepareDirectory", "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i("prepareDirectory", "Created directory " + path);
        }
    }


    private void prepareTesseract() {
        try {
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyTessDataFiles(TESSDATA);
    }

    /**
     * Copy tessdata files (located on assets/tessdata) to destination directory
     *
     * @param path - name of directory with .traineddata files
     */
    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = getAssets().list(path);

            for (String fileName : fileList) {

                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = getAssets().open(path + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.d("ocr", "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e("ocr", "Unable to copy files to tessdata " + e.toString());
        }
    }


    public static Bitmap imageProcessToBlackWhite(Bitmap image){

        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap convertedImage = Bitmap.createBitmap(width, height, image.getConfig());
        int alpha, red, green, blue;
        int pixel;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                // get pixel color
                pixel = image.getPixel(i, j);
                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                //if color adds up to over 128 set to black otherwise white
                int gray = (int) (0.2989 * red + 0.5870 * green + 0.1140 * blue);
                if (gray > 155) {
                    gray = 255;
                }
                else{
                    gray = 0;
                }
                // set new pixel color to output bitmap
                convertedImage.setPixel(i, j, Color.argb(alpha, gray, gray, gray));
            }
        }
        return convertedImage;
    }

    public static Bitmap cropCardToTitle(Bitmap card) {
        //scale card to titlebox
        Bitmap newCard = Bitmap.createBitmap(card, 60,60,410,45);
        return newCard;
    }


    //public void ItemClickListener

    //ToDo add Recent query results to landscape in local database
}
