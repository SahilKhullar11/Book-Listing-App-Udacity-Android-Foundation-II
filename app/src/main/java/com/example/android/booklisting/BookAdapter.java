package com.example.android.booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by sahil on 11/3/18.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    private static final String LOG_TAG = BookAdapter.class.getName();
    private static ImageView imageView;
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        Book currentItem = getItem(position);
        imageView = (ImageView) convertView.findViewById(R.id.image_view);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.author_text_view);
        String url = currentItem.getmImageUrl();
        new DownloadImageTask(imageView).execute(url);
        titleTextView.setText(currentItem.getmTitle());
        authorTextView.setText(currentItem.getmAuthor());
        return convertView;
    }
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

        ImageView bmImage;
        InputStream inputStream;
        public DownloadImageTask(ImageView imageView)
        {
            bmImage = imageView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                if (urlConnection.getResponseCode() != 200)
                {
                    return null;
                }
                inputStream = urlConnection.getInputStream();
                if (inputStream != null)
                {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG,e.getMessage());
                e.printStackTrace();
            }
            finally {
                if(urlConnection != null)
                {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }
}
