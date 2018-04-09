package com.example.andrew.mtgsearch;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class Home_Portrait extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int PICK_IMAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_home_landscape);
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
                            JSONObject jobj = new JSONObject(response.toString());
                            JSONArray data = response.getJSONArray("cards");
                            JSONObject card = data.getJSONObject(0);

                            Intent cardFound = new Intent(getBaseContext(), Card_Stats.class);
                            cardFound.putExtra("CARD", card.toString());
                            startActivity(cardFound);
                        } catch (Exception e) {
                            Log.e("CardParse", "Error parsing json object for card object");
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

    //ToDo add Recent query results to landscape in local database
}
