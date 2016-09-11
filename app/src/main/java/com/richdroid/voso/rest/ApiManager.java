package com.richdroid.voso.rest;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.richdroid.voso.model.AllSearchResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by richa.khanna on 5/11/16.
 */
public class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();
    //Base Url for TMDB
//    public static final String BASE_URL = "http://demo1054051.mockable.io/";

    public static final String BASE_URL = "http://119.81.52.188:5078/";

    private static ApiManager mInstance;
    private Context mContext;
    private ApiService mApiService;

    private ApiManager(Context context) {
        mContext = context;
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (mInstance == null) {
            Log.v(TAG, "Creating api manager instance");
            mInstance = new ApiManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void init() {
        mApiService = getApiService();
    }

    private ApiService getApiService() {
        if (mApiService == null) {
            //The Retrofit class generates an implementation of the ApiService interface.
            mApiService = ApiClient.getClient().create(ApiService.class);
        }
        return mApiService;
    }

    /**
     * Get the list of search data for the requested input.
     *
     * @param wRequester
     */
    public void getSearchData(final WeakReference<ApiRequester> wRequester, String textQuery, String gifUrl) {
        Log.v(TAG, "Api call : get search data");

        final Callback<AllSearchResponse> objectCallback = new Callback<AllSearchResponse>() {
            @Override
            public void onResponse(Call<AllSearchResponse> call, Response<AllSearchResponse> response) {
                Log.v(TAG, "onResponse : get search data returned a response");

                ApiRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }

                if (req != null) {
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response != null && response.isSuccessful()) {
                        req.onSuccess(response);
                    } else {
                        int statusCode = response.code();
                        // handle response errors yourself
                        ResponseBody errorBody = response.errorBody();
                        try {
                            Log.e(TAG, "onResponse status code : " + statusCode + " , error message : " + errorBody.string());
                        } catch (IOException e) {
                            Log.e(TAG, "onResponse exception message : " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AllSearchResponse> call, Throwable t) {
                // handle execution failures like no internet connectivity
                // timeout exception here IOException or SocketTOException
                Log.v(TAG, "onFailure : get search data api failed");

                ApiRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                if (req != null) {
                    req.onFailure(t);
                }
            }
        };
        Call<AllSearchResponse> call = null;

        if (!TextUtils.isEmpty(textQuery)) {
            Log.v(TAG, "Calling : get requested gifs api");
            call = mApiService.getRequestedGifs("true", textQuery);
        } else if (!TextUtils.isEmpty(gifUrl)) {
            Log.v(TAG, "Calling : get similar gifs api");
            call = mApiService.getSimilarGifs("true", gifUrl);
        }

        call.enqueue(objectCallback);

    }
}
