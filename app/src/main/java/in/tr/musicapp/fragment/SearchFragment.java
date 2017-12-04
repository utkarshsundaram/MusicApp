/*
 * Created by Mohamed Ibrahim N
 * Created on : 18/11/17 1:25 AM
 * File name : SearchFragment.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 18/11/17 1:25 AM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import in.tr.musicapp.R;
import in.tr.musicapp.adapter.OnlineItemAdapter;
import in.tr.musicapp.interfaces.OnNavigationListener;
import in.tr.musicapp.model.Music;
import in.tr.musicapp.rest.Api;
import in.tr.musicapp.rest.RestUtil;
import in.tr.musicapp.utility.SuggestionProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.facebook.ads.*;

public class SearchFragment extends Fragment {
    private ListView mListView;
    OnlineItemAdapter dataAdapter;
    List<Music> musicList;
    OnNavigationListener onNavigationListener;
    ProgressBar progressBar;
    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onNavigationListener = (OnNavigationListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onNavigationListener.onNavigationItemClickedListener(getString(R.string.search_music));
        View view = inflater.inflate(R.layout.fragment_search, container, false);   // Inflate the layout for this fragment
        setHasOptionsMenu(true);    //Enabling menu for this fragment
        progressBar = view.findViewById(R.id.progressBar);
        mListView = view.findViewById(R.id.listView);
        AppCompatImageView emptyView = view.findViewById(R.id.empty);
        mListView.setEmptyView(emptyView);  //Set empty view when the size of listView is zero
        // Instantiate an InterstitialAd object
        interstitialAd = new InterstitialAd(getActivity(), getString(R.string.fb_fullscreen_ad));
        setFullScreenAds();
        //fb banner ads
        adView = new AdView(getActivity(), getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = view.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();
        onListItemClickListener();

        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
        return view;
    }

    public void fetchList(String keyword) {
        Retrofit retrofit = RestUtil.getRetrofit(); //Setting the Retrofit Client

        Api api = retrofit.create(Api.class);   //Creating API using Retrofit Client
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Music>> call = api.getMusics(keyword);    //Making Rest calls for search
        call.enqueue(new Callback<List<Music>>() {
            @Override
            public void onResponse(@NonNull Call<List<Music>> call, @NonNull Response<List<Music>> response) {
                musicList = response.body();    //Building the list of objects from the response
                loadListData();     //Populating the data into listview
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Music>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onListItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onNavigationListener.onListItemClickedListener(musicList.get(position).getStreamLink(),
                        musicList.get(position).getName());
            }
        });
    }

    public void setFullScreenAds() {
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }

    public void loadListData() {
        dataAdapter = new OnlineItemAdapter(getActivity(), R.layout.downloaded_song_item, musicList);
        mListView.setAdapter(dataAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                //Search when text changes in the focus
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Making the search with the entered keyword
                fetchList(query);
                searchView.clearFocus();
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}
