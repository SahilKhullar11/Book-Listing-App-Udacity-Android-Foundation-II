package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private BookAdapter mAdapter;
    private static final int BOOK_LOADER_ID = 1;
    private static final String BOOK_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private ProgressBar progressBar;
    private TextView connectionTextView;
    private String url;
    private EditText editText;
    private ListView listView;
    private static int count = 0;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            if (getLoaderManager().getLoader(BOOK_LOADER_ID) != null) {
                getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
            }
        }
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        connectionTextView = findViewById(R.id.connection_text_view);
        connectionTextView.setVisibility(View.GONE);
        listView = findViewById(R.id.list);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);
        final LoaderManager loaderManager = getLoaderManager();
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    connectionTextView.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    editText = findViewById(R.id.edit_text);
                    url = editText.getText().toString();
                    url = BOOK_URL + url;
                    url = url.trim().replace(" ", "");
                    Log.e(LOG_TAG, url);
                    if (count == 0) {
                        loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        count++;
                    } else {
                        loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        count++;
                    }
                } else {
                    listView.setVisibility(View.GONE);
                    connectionTextView.setVisibility(View.VISIBLE);
                    connectionTextView.setText(R.string.connection);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book currentItem = mAdapter.getItem(i);
                Uri bookUri = Uri.parse(currentItem.getmUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mAdapter.clear();
        progressBar.setVisibility(View.GONE);
        if (books != null && !books.isEmpty()) {
            listView.setVisibility(View.VISIBLE);
            mAdapter.addAll(books);
            listView.setSelection(0);
        } else {
            connectionTextView.setVisibility(View.VISIBLE);
            connectionTextView.setText(R.string.no_data_found);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }


    private static class BookLoader extends AsyncTaskLoader<List<Book>> {
        String mUrl;

        public BookLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Book> loadInBackground() {
            if (mUrl == null) {
                return null;
            }

            List<Book> result = QueryUtils.fetchBookData(mUrl);
            return result;
        }
    }
}
