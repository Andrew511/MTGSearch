package com.example.andrew.mtgsearch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecentCardFragment extends Fragment implements MyRecentCardRecyclerViewAdapter.ItemClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecentCardRecyclerViewAdapter mAdapter;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<CardObject> cards;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecentCardFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recentcard_list, container, false);

        // Set the adapter
        if (mAdapter == null) {
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mLayoutManager = new LinearLayoutManager(context);
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(mLayoutManager);


            if (savedInstanceState != null) {
                cards = savedInstanceState.getParcelableArrayList("CARDS");
            }
            else {
                cards = new ArrayList<>();
                //build cards from database

                RecentCardDBContract.RecentCardDBHelper recentDBHelper;
                SQLiteDatabase rdb;

                recentDBHelper = new RecentCardDBContract.RecentCardDBHelper(context);
                rdb = recentDBHelper.getReadableDatabase();

                Cursor cursor = rdb.query(
                        RecentCardDBContract.RecentCardEntry.TABLE_NAME,   // The table to query
                        null,             // The array of columns to return (pass null to get all)
                        null,              // no filtering
                        null,          // don't filter by anything
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        RecentCardDBContract.RecentCardEntry._ID + " DESC"               // The sort order
                );
                while (cursor.moveToNext()) {
                    String cardName = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_NAME));
                    String cardManaCost = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_MANACOST));
                    String cardPower = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_POWER));
                    String cardToughness = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_TOUGHNESS));
                    String cardText = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_TEXT));
                    String cardType = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_TYPE));
                    String cardImageURL = cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry.COLUMN_NAME_URL));

                    ArrayList<RulingObject> rulings = new ArrayList<>();

                    String[] selectionArgs = {cursor.getString(cursor.getColumnIndex(RecentCardDBContract.RecentCardEntry._ID))};

                    Cursor rulingCursor = rdb.query(
                            RecentCardDBContract.RulingsEntry.TABLE_NAME,   // The table to query
                            null,             // The array of columns to return (pass null to get all)
                            RecentCardDBContract.RulingsEntry.COLUMN_NAME_CARDID + " = ?",              // no filtering
                            selectionArgs,          // don't filter by anything
                            null,                   // don't group the rows
                            null,                   // don't filter by row groups
                            null               // The sort order
                    );

                    while (rulingCursor.moveToNext()) {
                        RulingObject ruling = new RulingObject(
                                rulingCursor.getString(rulingCursor.getColumnIndex(RecentCardDBContract.RulingsEntry.COLUMN_NAME_DATE)),
                                rulingCursor.getString(rulingCursor.getColumnIndex(RecentCardDBContract.RulingsEntry.COLUMN_NAME_RULING)));
                        rulings.add(ruling);
                    }
                    cards.add(new CardObject(cardName, cardManaCost, cardPower, cardToughness, cardText, cardType, cardImageURL, rulings));
                }
            }


            mAdapter = new MyRecentCardRecyclerViewAdapter(context, cards);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }

        }
        return view;
    }

    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("LAYOUT_STATE", mLayoutManager.onSaveInstanceState());
        outState.putParcelableArrayList("CARDS", cards);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("LAYOUT_STATE"));
            cards = savedInstanceState.getParcelableArrayList("CARDS");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void cardsUpdated() {
        mAdapter.notifyDataSetChanged();
    }

    public void onItemClick(View view, int position) {
        Intent cardFound = new Intent(getContext(), Card_Stats.class);
        CardObject card = cards.get(position);
        cardFound.putExtra("CARD", card);
        startActivity(cardFound);
    }

    //add a new card to card array for adapter from  outside fragment
    public void addCard(CardObject card) {
        cards.add(0, card);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnListFragmentInteractionListener {}
}
