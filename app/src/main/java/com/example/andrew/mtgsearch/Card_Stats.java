package com.example.andrew.mtgsearch;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.ArrayList;
import java.util.Iterator;

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
        CardObject card;
            if (savedInstanceState != null) {
                card = savedInstanceState.getParcelable("CARD");
            }
            else {
                card = getIntent().getParcelableExtra("CARD");
            }
            ((TextView)findViewById(R.id.cardName)).setText(card.name);
            ((TextView)findViewById(R.id.cardCost)).setText(card.manaCost);
            ((TextView) findViewById(R.id.cardPT)).setText(card.power + "/"
                        + card.toughness);
            ((TextView)findViewById(R.id.cardText)).setText(card.text);
            ((TextView)findViewById(R.id.cardType)).setText(card.type);
            if(getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) {
                setCardImage(card.imageURL);
            }
        ArrayList<RulingObject> rulings = card.rulings;
        TableLayout table = findViewById(R.id.tableLayout);

        TableRow.LayoutParams dateParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        dateParams.setMargins(1,1,1,1);
        TableRow.LayoutParams rulingParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        rulingParams.setMargins(1,1,1,1);
        if (rulings.size() == 0) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            row.setBackgroundColor(Color.BLACK);
            TextView date = new TextView(this);
            date.setLayoutParams(dateParams);
            date.setBackgroundColor(Color.WHITE);
            TextView rulingView = new TextView(this);
            rulingView.setLayoutParams(rulingParams);
            rulingView.setBackgroundColor(Color.WHITE);

            date.setText("N/A");
            rulingView.setText("No rulings for this card");
            row.addView(date);
            row.addView(rulingView);
            table.addView(row);
        }
        else {
            for (RulingObject ruling : rulings) {

                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                row.setBackgroundColor(Color.BLACK);
                TextView date = new TextView(this);
                date.setLayoutParams(dateParams);
                date.setBackgroundColor(Color.WHITE);
                TextView rulingView = new TextView(this);
                rulingView.setLayoutParams(rulingParams);
                rulingView.setBackgroundColor(Color.WHITE);

                date.setText(ruling.date);
                rulingView.setText(ruling.ruling);
                row.addView(date);
                row.addView(rulingView);
                table.addView(row);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("CARD", getIntent().getParcelableExtra("CARD"));
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
