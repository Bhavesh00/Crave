package com.crave.crave;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FindNearby extends AppCompatActivity {
    private static RequestQueue requestQueue;
    private LocationManager manager;
    private LocationListener listener;
    private Location location;
    private boolean receivedLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        final String searchVal = getIntent().getStringExtra("SearchValue");
        requestQueue = Volley.newRequestQueue(this);
        // get location here and pass it through to the API call
        checkPermission();
        manager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location setLocation) {
                if (!receivedLocation) {
                    Log.d("Location: ", setLocation.toString());
                    location = setLocation;
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    receivedLocation = true;
                    startAPICall(searchVal, longitude, latitude);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0, listener);
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
        // City id = 685
        //https://developers.zomato.com/api/v2.1/search?entity_type=city&q=pizza&count=10&lat=36.391087&lon=-117.857827&radius=5000&sort=rating&order=desc
        String url = "https://developers.zomato.com/api/v2.1/search?entity_type=city&q=" + searchVal + "&count=20&lat="
                + latit + "&lon=" + longit + "&radius=5000&order=desc";
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            // Toast.makeText(getApplicationContext(), "Response!", Toast.LENGTH_LONG).show();
                            // Log.d("response_JSON", "" + response);
                            try {
                                JSONArray restaurantList = response.getJSONArray("restaurants");
                                for (int i = 0; i < restaurantList.length(); i++) {
                                    //Get each object from list
                                    JSONObject obj = restaurantList.getJSONObject(i);
                                    //Get the restaurant
                                    JSONObject restaurant = obj.getJSONObject("restaurant");
                                    //Get Name
                                    String nameVal = restaurant.getString("name");
                                    // Get URL:
                                    String url = restaurant.getString("url");
                                    //Get Address
                                    JSONObject location = restaurant.getJSONObject("location");
                                    String address = location.getString("address");
                                    String locality = location.getString("locality");
                                    String city = location.getString("city");
                                    //Get Rating
                                    JSONObject rating = restaurant.getJSONObject("user_rating");
                                    String aggregateRating = rating.getString("aggregate_rating") + "/5";
                                    String votes = rating.getString("votes");
                                    //Get Image
                                    String image = restaurant.getString("featured_image");
                                    Log.d("image_restaurant", image);
                                    // Get menu
                                    String menu = restaurant.getString("menu_url");
                                    //Set Layout
                                    setLayout(nameVal, "Address: " + address + ", " + locality + ", " + city, "Rating: " + aggregateRating + " (" + votes + " votes" + ")", menu, image, url);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error:" + error.toString(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user-key", "a7d51f7199c934dd65a3207602073127");
                    params.put("Accept", "application/json");
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    void checkPermission() {
        if (ActivityCompat.checkSelfPermission(FindNearby.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FindNearby.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FindNearby.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
    }

    public void setLayout(String name, String address, String rating, String menu, final String image, String url) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View childLayout = inflater.inflate(R.layout.find_nearby_list, null, false);
        TextView nameView = childLayout.findViewById(R.id.restaurant_name);
        nameView.setText(name);

        TextView addressView = childLayout.findViewById(R.id.restaurant_address);
        // Example: String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8");
        String addressCopy = address;
        String map = "";
        try {
            map = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(addressCopy, StandardCharsets.UTF_8.name());
        } catch(Exception e) {
            Log.getStackTraceString(e);
        }
        addressView.setText(Html.fromHtml("<a href=\""+ map + "\">" + address + "</a>"));
        addressView.setClickable(true);
        addressView.setMovementMethod(LinkMovementMethod.getInstance());


        TextView ratingView = childLayout.findViewById(R.id.restaurant_rating);
        ratingView.setText(Html.fromHtml("<a href=\""+ url + "\">" + rating + "</a>"));
        ratingView.setClickable(true);
        ratingView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView menuView = childLayout.findViewById(R.id.restaurant_menu);
        menuView.setText(Html.fromHtml("<a href=\""+ menu + "\">" + "Menu" + "</a>"));
        menuView.setClickable(true);
        menuView.setMovementMethod(LinkMovementMethod.getInstance());

        LinearLayout frame = findViewById(R.id.find_nearby_frame);
        frame.addView(childLayout);
    }
}