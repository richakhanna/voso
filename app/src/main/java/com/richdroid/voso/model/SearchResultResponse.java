package com.richdroid.voso.model;

import java.util.ArrayList;

/**
 * Created by richa.khanna on 9/10/16.
 */
public class SearchResultResponse {

    private ArrayList<SearchVideo> video;

    private ArrayList<SearchGif> gifs;

    public ArrayList<SearchVideo> getVideo() {
        return video;
    }

    public ArrayList<SearchGif> getGifs() {
        return gifs;
    }
}


