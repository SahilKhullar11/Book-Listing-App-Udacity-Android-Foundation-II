package com.example.android.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil on 11/3/18.
 */

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {

    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        if (requestUrl == null) {
            return null;
        }
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error while creating url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code = " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while making http request", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    output.append(line);
                    line = bufferedReader.readLine();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading from stream", e);
        }
        return output.toString();
    }

    private static List<Book> extractFromJson(String bookJson) {
        if (TextUtils.isEmpty(bookJson)) {
            return null;
        }
        List<Book> bookList = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(bookJson);
            JSONArray itemsArray = rootObject.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject jsonObject = itemsArray.getJSONObject(i);
                JSONObject volumeInfoObject = jsonObject.getJSONObject("volumeInfo");
                String title = "";
                if(volumeInfoObject.has("title"))
                {
                    title = volumeInfoObject.getString("title");
                }
                String authors = "";
                if(volumeInfoObject.has("authors"))
                {
                    JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                    for (int j = 0; j < authorsArray.length(); j++) {
                        if (j == 0) {
                            authors = authorsArray.getString(0);
                        }
                        if (j > 0) {
                            authors = authors + ", " + authorsArray.getString(j);
                        }
                    }
                }
                String imageUrl = null, url = null;
                if(volumeInfoObject.has("imageLinks"))
                {
                    JSONObject imageLinks = volumeInfoObject.getJSONObject("imageLinks");
                    imageUrl = imageLinks.getString("thumbnail");
                }
                if(volumeInfoObject.has("previewLink"))
                {
                    url = volumeInfoObject.getString("previewLink");
                }
                Book book = new Book(imageUrl, title, authors, url);
                bookList.add(book);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error extracting data", e);
        }
        return bookList;
    }

    public static List<Book> fetchBookData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Thread sleep error", e);
        }
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making http request", e);
        }
        return extractFromJson(jsonResponse);
    }
}
