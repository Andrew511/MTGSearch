package com.example.andrew.mtgsearch;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;

public class Card_Stats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation==
                Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_stats_landscape);

        } else{
            setContentView(R.layout.activity_stats_portrait);
        }

        try {
            JSONObject card = new JSONObject(getIntent().getStringExtra("CARD"));
            // ToDo fix word wrapping in text fields
            // ToDo Add auto repeating text fields for each set of rulings
        ((TextView)findViewById(R.id.cardName)).setText(card.opt("name").toString());
        ((TextView)findViewById(R.id.cardCost)).setText(card.opt("manaCost").toString());
        ((TextView)findViewById(R.id.cardPT)).setText(card.opt("power").toString() + "/"
                + card.opt("toughness").toString());
        ((TextView)findViewById(R.id.cardText)).setText(card.opt("originalText").toString());
        ((TextView)findViewById(R.id.cardType)).setText(card.opt("originalType").toString());
        if(getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
            setCardImage(card.opt("imageUrl").toString());
        }

        }
        catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error extracting card data, please search again", Toast.LENGTH_LONG).show();
        }
    }

    public void setCardImage(String url) {

        RequestQueue imageQueue = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        ((ImageView)findViewById(R.id.cardImage)).setImageBitmap(response);

                    }
                },
                300, // Image width
                400, // Image height
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error retrieving card data", Toast.LENGTH_SHORT).show();
            }
        });

        imageQueue.add(imageRequest);

    }
}
