package com.example.android.booklisting;

/**
 * Created by sahil on 11/3/18.
 */

public class Book {
    private String mImageUrl;
    private String mTitle;
    private String mAuthor;
    private String mUrl;

    public Book(String imageUrl, String title, String author, String url) {
        mImageUrl = imageUrl;
        mTitle = title;
        mAuthor = author;
        mUrl = url;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }
    public String getmUrl()
    {
        return mUrl;
    }
}
