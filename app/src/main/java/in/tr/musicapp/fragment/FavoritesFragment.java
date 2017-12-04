/*
 * Created by Mohamed Ibrahim N
 * Created on : 18/11/17 2:46 AM
 * File name : FavoritesFragment.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 18/11/17 2:46 AM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import in.tr.musicapp.R;
import in.tr.musicapp.adapter.DownloadedItemAdapter;
import in.tr.musicapp.database.DBHandler;
import in.tr.musicapp.interfaces.OnNavigationListener;
import in.tr.musicapp.model.Music;
import in.tr.musicapp.utility.MusicAlphabeticComparator;
import in.tr.musicapp.utility.MusicDateComparator;
import in.tr.musicapp.utility.MusicSizeComparator;

import com.facebook.ads.*;

public class FavoritesFragment extends Fragment {
    private ListView mListView;
    DownloadedItemAdapter dataAdapter;
    List<Music> musicList;
    OnNavigationListener onNavigationListener;
    DBHandler dbHandler;
    private AdView adView;

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
        onNavigationListener.onNavigationItemClickedListener(getString(R.string.favorites));
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        setHasOptionsMenu(true);
        dbHandler = DBHandler.getInstance(getActivity());
        mListView = view.findViewById(R.id.listView);
        AppCompatImageView emptyView = view.findViewById(R.id.empty);
        mListView.setEmptyView(emptyView);  //Set empty view when the size of listView is zero
        //fb banner ads
        adView = new AdView(getActivity(), getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = view.findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
        onListItemClickListener();
        fetchList();
        return view;
    }

    public void fetchList() {
        musicList = dbHandler.getFavoriteAudioFromDevice(getActivity());
        //displaying the array into listview
        loadListData();
    }

    public void onListItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onNavigationListener.onListItemClickedListener(musicList.get(position));
            }
        });
    }

    public void loadListData() {
        dataAdapter = new DownloadedItemAdapter(getActivity(), R.layout.downloaded_song_item, musicList);
        mListView.setAdapter(dataAdapter);
        dataAdapter.setCallback(onNavigationListener);
        dataAdapter.sort(new MusicAlphabeticComparator());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.downloads_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        //searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                dataAdapter.search(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // this is your adapter that will be filtered
                dataAdapter.search(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_az) {
            dataAdapter.sort(new MusicAlphabeticComparator());
        } else if (id == R.id.filter_date) {
            dataAdapter.sort(new MusicDateComparator());
        } else if (id == R.id.filter_size) {
            dataAdapter.sort(new MusicSizeComparator());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
