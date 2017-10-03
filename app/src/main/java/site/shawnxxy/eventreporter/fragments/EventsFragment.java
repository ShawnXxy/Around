package site.shawnxxy.eventreporter.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import site.shawnxxy.eventreporter.Event;
import site.shawnxxy.eventreporter.R;
import site.shawnxxy.eventreporter.activity.ReportEventActivity;
import site.shawnxxy.eventreporter.adapter.EventListAdapter;
//import android.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";

    // call recycler view in fragment
    private RecyclerView recyclerView;
//    private RecyclerView.Adapter mAdapter;
    private EventListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference database;
    private List<Event> events;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     *  Add Connections between Event Activity and EventReportActivity
     */
    private FloatingActionButton mImageViewAdd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_events, container, false);
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        mImageViewAdd = (FloatingActionButton) view.findViewById(R.id.img_event_add);
        mImageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventReportIntent = new Intent(getActivity(), ReportEventActivity.class);
                startActivity(eventReportIntent);
            }
        });

        // call recycler view in fragment
        recyclerView = (RecyclerView) view.findViewById(R.id.event_recycler_view);
        database = FirebaseDatabase.getInstance().getReference();
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        setAdapter();
        return view;
    }

    public void setAdapter() {
        events = new ArrayList<>();
        database.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Event event = noteDataSnapshot.getValue(Event.class);
                    events.add(event);
                }
                // Add context to list adapter
                mAdapter = new EventListAdapter(events, getActivity());
                recyclerView.setAdapter(mAdapter);
                setUpAndLoadNativeExpressAds();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: do something
            }
        });
    }

    /**
     *  Implement a function that allows recycler view to do lazy loading
     */
    private void setUpAndLoadNativeExpressAds() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = getActivity().getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                for (int i = 0; i < mAdapter.getEventList().size(); i ++) {
                    if (mAdapter.getMap().containsKey(i)) {
                        final NativeExpressAdView adView = mAdapter.getMap().get(i);
                        final CardView cardView = (CardView) getActivity().findViewById(R.id.ad_card_view);
                        final int adWidth = cardView.getWidth() - cardView.getPaddingLeft() - cardView.getPaddingRight();
                        AdSize adSize = new AdSize((int) (adWidth / scale), 150);
                        adView.setAdSize(adSize);
                        adView.setAdUnitId(AD_UNIT_ID);
                        loadNativeExpressAd(i, adView);
                    }
                }
            }
        });
    }

    /**
     *  loads native ads
     * @param index
     * @param adView
     */
    private void loadNativeExpressAd(final int index, NativeExpressAdView adView ) {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load the next ad in the items list.
                Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }


}
