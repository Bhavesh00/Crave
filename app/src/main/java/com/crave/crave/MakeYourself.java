package com.crave.crave;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MakeYourself extends AppCompatActivity {
    /*
    private JSONArray hits;
    private JSONObject recipe;
    private ArrayList<String> labels; // loop through and add labels
    private ArrayList<String> imageUrl; // loop through and add url's
    private JSONArray ingredientLines;
    private Map<String, ArrayList<String>> ingredients; // loop through
    */

    private static RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_yourself);
        final String searchVal = getIntent().getStringExtra("SearchValue");

        requestQueue = Volley.newRequestQueue(this);
        final MakeYourself thisActivity = this;
        SearchView searchView = findViewById(R.id.make_yourself_search_bar);
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

        Button findNearbyButton = findViewById(R.id.find_nearby_instead);
        findNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFindNearby = new Intent(thisActivity, FindNearby.class);
                toFindNearby.putExtra("SearchValue", searchVal);
                startActivity(toFindNearby);
            }
        });
        startAPICall(searchVal);
    }
    /* Make API Call */
    void startAPICall(String searchVal) {
        String url = "https://api.edamam.com/search?app_id=8bacdab9&app_key=824756907419ada9db6852f47da00811&q=" + searchVal;
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Response!", Toast.LENGTH_LONG).show();
                            Log.d("response_JSON", "" + response);
                            try {
                                // FINISH THE DATA HANDLING HERE:
                                JSONArray hits = response.getJSONArray("hits");
                                for (int i = 0; i < hits.length(); i++) {
                                    JSONObject obj = hits.getJSONObject(i);
                                    // Get Recipe
                                    JSONObject recipe = obj.getJSONObject("recipe");
                                    // Get Label
                                    String label = recipe.getString("label");
                                    // Get Image url
                                    String image = recipe.getString("image");
                                    // Get recipe instructions
                                    String instruction_url = recipe.getString("url");
                                    Log.d("Recipe url", instruction_url);
                                    // Get Array of Ingredients
                                    JSONArray ingredients = recipe.getJSONArray("ingredientLines");
                                    String ingredString = "";
                                    for (int j = 0; j < ingredients.length(); j++) {
                                        ingredString = ingredString + ingredients.getString(j);
                                        if (j != ingredients.length() - 1) {
                                            ingredString += ", ";
                                        }
                                    }
                                    // Get Array of Health Labels
                                    JSONArray health_labels = recipe.getJSONArray("healthLabels");
                                    String healthString = "";
                                    for (int j = 0; j < health_labels.length(); j++) {
                                        healthString = healthString + health_labels.getString(j);
                                        if (j != health_labels.length() - 1) {
                                            healthString += ", ";
                                        }
                                    }
                                    // Get calories:
                                    String calories = recipe.getString("calories");
                                    // Get fat:
                                    JSONObject nutrients = recipe.getJSONObject("totalNutrients");
                                    JSONObject fat = nutrients.getJSONObject("FAT");
                                    String fatQuantity = fat.getString("quantity");
                                    // Get Sugar:
                                    JSONObject sugar = nutrients.getJSONObject("SUGAR");
                                    String sugarQuantity = fat.getString("quantity");
                                    // Get Protein:
                                    JSONObject protein = nutrients.getJSONObject("PROCNT");
                                    String proteinQuantity = protein.getString("quantity");

                                    String nutriString = "Calories: " + calories + "\n" + "Fat: " + fatQuantity + "\n" + "Sugar: " + sugarQuantity + "\n" + "Protein: " + proteinQuantity;
                                    setLayout(label, ingredString, nutriString, healthString, instruction_url, image);
                                }
                            } catch (Exception e) {
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
                    params.put("app_id", "8bacdab9");
                    params.put("app_key", "824756907419ada9db6852f47da00811");
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setLayout(String name, String ingredients, String nutrients, String health, String url, final String img) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View childLayout = inflater.inflate(R.layout.make_yourself_list, null, false);
        TextView nameView = childLayout.findViewById(R.id.recipe_name);
        nameView.setText(name);
        TextView ingredientView = childLayout.findViewById(R.id.recipe_ingredients);
        ingredientView.setText(ingredients);
        TextView nutrientView = childLayout.findViewById(R.id.recipe_nutrients);
        nutrientView.setText(nutrients);
        TextView healthView = childLayout.findViewById(R.id.recipe_health);
        healthView.setText(health);

        TextView urlView = childLayout.findViewById(R.id.recipe);
        urlView.setText(Html.fromHtml("<a href=\""+ url + "\">" + "Recipe" + "</a>"));
        urlView.setClickable(true);
        urlView.setMovementMethod(LinkMovementMethod.getInstance());



        final ImageView imageView = childLayout.findViewById(R.id.recipe_img);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    URL imgURL = new URL(img);
                    Bitmap bmp = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
                    imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        LinearLayout frame = findViewById(R.id.make_yourself_frame);
        frame.addView(childLayout);
    }
}
