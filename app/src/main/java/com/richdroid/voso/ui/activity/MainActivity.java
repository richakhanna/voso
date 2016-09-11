package com.richdroid.voso.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.richdroid.voso.R;
import com.richdroid.voso.adapter.MovieGridAdapter;
import com.richdroid.voso.app.AppController;
import com.richdroid.voso.model.AllSearchResponse;
import com.richdroid.voso.model.SearchGif;
import com.richdroid.voso.rest.ApiManager;
import com.richdroid.voso.rest.ApiRequester;
import com.richdroid.voso.utils.NetworkUtils;
import com.richdroid.voso.utils.ProgressBarUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MovieGridAdapter.ShowSimilarInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SPEECH_REQUEST_CODE = 0;
    private ApiManager mApiMan;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SearchGif> mDatasetList;
    private ProgressBarUtil mProgressBar;

    @BindView(R.id.iv_gif_container)
    ImageView ivGifContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //if we want to support older APIs like options-menu, otherwise no need
        setSupportActionBar(toolbar);

        AppController app = ((AppController) getApplication());
        mApiMan = app.getApiManager();

        mFab.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        getSupportActionBar().setTitle(getResources().getString(R.string.search_using_voice));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager with default vertical orientation
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mDatasetList = new ArrayList<SearchGif>();

        // specify an adapter
        mAdapter = new MovieGridAdapter(this, mDatasetList, false, this);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = new ProgressBarUtil(this);


        Ion.with(ivGifContainer)
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.placeholder)
                .animateGif(AnimateGifMode.ANIMATE)
                .load("https://media.giphy.com/media/9fbYYzdf6BbQA/giphy.gif");
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            mProgressBar.show();
            Toast.makeText(MainActivity.this, "spokenText : " + spokenText, Toast.LENGTH_LONG).show();
            mApiMan.getSearchData(
                    new WeakReference<ApiRequester>(mSearchRequester), spokenText, "");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ApiRequester mSearchRequester = new ApiRequester() {

        @Override
        public void onFailure(Throwable error) {
            if (isFinishing()) {
                return;
            }

            mProgressBar.hide();
            // Log error here since request failed
            Log.v(TAG, "Failure : search onFailure : " + error.toString());

            mDatasetList.clear();
            mAdapter.notifyDataSetChanged();
            NetworkUtils.showSnackbar(mRecyclerView, getResources().getString(R.string.unable_to_reach_server));
            ivGifContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(Response response) {
            if (isFinishing()) {
                return;
            }

            mProgressBar.hide();
            mDatasetList.clear();
            mAdapter.notifyDataSetChanged();
            Log.v(TAG, "Success : search data : " + new Gson().toJson(response).toString());
            AllSearchResponse allSearchResponse = (AllSearchResponse) response.body();

            if (allSearchResponse != null && allSearchResponse.getResults() != null &&
                    allSearchResponse.getResults().getGifs() != null && allSearchResponse.getResults().getGifs().size() > 0) {
                List<SearchGif> searchGifsList = allSearchResponse.getResults().getGifs();
                for (SearchGif searchGif : searchGifsList) {
                    mDatasetList.add(searchGif);
                }
                mAdapter.notifyDataSetChanged();
//                Toast.makeText(MainActivity.this, "spokenText : " + "onSuccess called", Toast.LENGTH_LONG).show();
                ivGifContainer.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                displaySpeechRecognizer();
                break;
        }
    }

    @Override
    public void callApiToShowSimilarGif(String url) {
        mApiMan.getSearchData(
                new WeakReference<ApiRequester>(mSearchRequester), "", url);
    }
}
