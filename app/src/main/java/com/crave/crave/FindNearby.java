package com.crave.crave;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;



public class FindNearby extends AppCompatActivity {
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        final String searchVal = getIntent().getStringExtra("SearchValue");
        TextView searchValue = findViewById(R.id.search_val);
        searchValue.setText(searchVal);
        requestQueue = Volley.newRequestQueue(this);
        // get location here and pass it through to the API call
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location;
        try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.d("locationlog", "locationlog" + ":" + location + ":" + latitude);
            startAPICall(searchVal, longitude, latitude);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Please enable app location permissions.", Toast.LENGTH_LONG).show();
        }
        final FindNearby thisActivity = this;
        SearchView searchView = findViewById(R.id.find_nearby_search_bar);
        searchView.setQueryHint("What else are you craving?");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent toOptions = new Intent(thisActivity, Options.class);
                toOptions.putExtra("SearchValue", s);
                startActivity(toOptions);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        Button findNearbyButton = findViewById(R.id.make_yourself_instead);
        findNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFindNearby = new Intent(thisActivity, MakeYourself.class);
                toFindNearby.putExtra("SearchValue", searchVal);
                startActivity(toFindNearby);
            }
        });
    }

    /* Make API Call */
    void startAPICall(String searchVal, double longit, double latit) {
        Map<String, Object> params = new HashMap<>();
        params.put("term", searchVal);
        params.put("latitude", latit);
        params.put("longitude", longit);
        JSONObject obj = new JSONObject(params);
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://api.yelp.com/v3/businesses/search?types=term,latitude,longitude", obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Response!", Toast.LENGTH_LONG).show();
                            Log.d("response_gotten", "response received");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error:" + error.toString(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("num4t4uRfzaxGhlkucxxIg", "ZjV189V_w5Y--0G0e6YvhipNOOkPRGTNHabOLOM3v8LKlNvs2xyivtTiFlJPB451rEbEulehGDhdlQwq_QjV9ed7OtsXOl-m6bbR_ZxJENLc2TNQ-nHeP-b-9_0CXHYx");
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
