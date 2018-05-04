package com.example.andrew.mtgsearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.andrew.mtgsearch.RecentCardFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CardObject} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRecentCardRecyclerViewAdapter extends RecyclerView.Adapter<MyRecentCardRecyclerViewAdapter.ViewHolder> {

    private List<CardObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecentCardRecyclerViewAdapter(Context context, List<CardObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_recentcard, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String cardName = mData.get(position).name;
        String imageURL = mData.get(position).imageURL;
        holder.cardNameView.setText(cardName);


        RequestQueue imageQueue = Volley.newRequestQueue(holder.cardImageView.getContext());
        ImageRequest imageRequest = new ImageRequest(imageURL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        holder.cardImageView.setImageBitmap(response);

                    }
                },
                300, // Image width
                400, // Image height
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(holder.cardImageView.getContext(), "Error retrieving card data", Toast.LENGTH_SHORT).show();
                    }
                });

        imageQueue.add(imageRequest);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cardNameView;
        ImageView cardImageView;

        ViewHolder(View itemView) {
            super(itemView);
            cardNameView = itemView.findViewById(R.id.recentCardName);
            cardImageView = itemView.findViewById(R.id.recentCardImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    CardObject getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
