package com.example.filmgratis;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.filmgratis.adapter.CardViewCursorAdapter;

import static com.example.filmgratis.provider.DatabaseContract.CONTENT_URI;



public class FavoritFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private CardViewCursorAdapter adapter;

    public FavoritFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalogue, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RecyclerView rvCategory;

        rvCategory = view.findViewById(R.id.rv_category);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardViewCursorAdapter(getContext());
        rvCategory.setAdapter(adapter);

        getLoaderManager().initLoader(0, new Bundle(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(), CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.setListFilms(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setListFilms(null);
    }
}
