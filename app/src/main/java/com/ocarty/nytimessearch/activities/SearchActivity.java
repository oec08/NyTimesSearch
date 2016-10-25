package com.ocarty.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ocarty.nytimessearch.model.Article;
import com.ocarty.nytimessearch.adapters.ArticleArrayAdapter;
import com.ocarty.nytimessearch.R;
import com.ocarty.nytimessearch.utils.EndlessScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private EditText etQuery;
    private GridView gvResults;
    private Button btnSearch;
    private ArrayList<Article> articles;
    private ArticleArrayAdapter arrayAdapter;
    private final int REQUEST_CODE = 1;
    private boolean isArtsChecked;
    private boolean isFashionAndDesignChecked;
    private boolean isSportsChecked;
    private String selectedSpinnerValue = "";
    private boolean hasFilterBeenSelected = false;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getApplicationContext().getResources().getString(R.string.news_search_toolbar));
        setSupportActionBar(toolbar);

        setUpViews();
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                if(page > 0) {
                    page = page - 1;
                }
                final int  offset = page;
                Handler handler = new Handler();
                Runnable runnableCode = new Runnable() {
                    @Override
                    public void run() {
                        // Do something here on the main thread
                        Log.d("Waiting a second", "Called on main thread");
                        loadNextDataFromApi(offset);
                    }
                };
                handler.postDelayed(runnableCode, 1000);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });


    }

    public void setUpViews() {
        etQuery = (EditText)findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        btnSearch = (Button)findViewById(R.id.btSearch);
        articles = new ArrayList<>();
        arrayAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(arrayAdapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

    }
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        String query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key","13b08993afaa49358d604825af7155b1");
        params.put("page", offset);
        params.put("q", query);
        if (hasFilterBeenSelected) {
            String begin_date = "";
            if(date != null) {
                begin_date = date;
            }
            params.put("begin_date", begin_date);
            params.put("sort", selectedSpinnerValue);
            boolean newsDeskFlag = false;
            if(isSportsChecked || isArtsChecked || isFashionAndDesignChecked) {
                newsDeskFlag = true;
            }
            StringBuilder newsDeskString = new StringBuilder();
            newsDeskString.append("news_desk:(");
            if (isArtsChecked) {
                newsDeskString.append("\"Arts\"");
            }
            if (isSportsChecked) {
                newsDeskString.append("\"Sports\"");
            }
            if (isFashionAndDesignChecked) {
                newsDeskString.append("\"Fashion & Style\"");
            }

            newsDeskString.append(")");
            if(newsDeskFlag) {
                params.put("fq", newsDeskString.toString().trim());
            }
        }

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Log.d("DEBUG", articleJsonResults.toString());
                    if(articleJsonResults.length() == 0) {
                        Toast.makeText(getApplicationContext(), "There are no results for your search", Toast.LENGTH_LONG).show();
                    }
                    arrayAdapter.addAll(Article.fromJsonArray(articleJsonResults));
                    arrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        if(!isDeviceOnline()) {
            Toast.makeText(getApplicationContext(), "Please get Internet Access before Searching", Toast.LENGTH_LONG).show();
        }
        else if(query.isEmpty()) {
            return;
        }
        articles.clear();
        arrayAdapter.notifyDataSetChanged();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key","13b08993afaa49358d604825af7155b1");
        params.put("page", 0);
        params.put("q", query);
        if (hasFilterBeenSelected) {
            String begin_date = "";
            if(date != null) {
                begin_date = date;
            }
            params.put("begin_date", begin_date);
            params.put("sort", selectedSpinnerValue);
            boolean newsDeskFlag = false;
            if(isSportsChecked || isArtsChecked || isFashionAndDesignChecked) {
                newsDeskFlag = true;
            }
            StringBuilder newsDeskString = new StringBuilder();
            newsDeskString.append("news_desk:(");
            if (isArtsChecked) {
                newsDeskString.append("\"Arts\"");
            }
            if (isSportsChecked) {
                newsDeskString.append("\"Sports\"");
            }
            if (isFashionAndDesignChecked) {
                newsDeskString.append("\"Fashion & Style\"");
            }

            newsDeskString.append(")");
            if(newsDeskFlag) {
                params.put("fq", newsDeskString.toString().trim());
            }
        }

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    if(articleJsonResults.length() == 0) {
                        Toast.makeText(getApplicationContext(), "There are no results for your search", Toast.LENGTH_LONG).show();
                    }
                    arrayAdapter.addAll(Article.fromJsonArray(articleJsonResults));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            hasFilterBeenSelected = true;
            isArtsChecked = data.getExtras().getBoolean("isArtsChecked");
            isFashionAndDesignChecked = data.getExtras().getBoolean("isFashionAndDesignChecked");
            isSportsChecked = data.getExtras().getBoolean("isSportsChecked");
            selectedSpinnerValue = data.getExtras().getString("selectedSpinnerValue");
            date = (data.getExtras().getString("date"));
        }
    }

    /**
     * Checks for internet access
     * @return true if internet access is there, false otherwise
     */
    public boolean isDeviceOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
