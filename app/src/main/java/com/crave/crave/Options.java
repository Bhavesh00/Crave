package com.crave.crave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        final Options thisActivity = this;
        final String searchVal = getIntent().getStringExtra("SearchValue");
        TextView findNearby = findViewById(R.id.options_find_it_nearby);
        TextView makeYourself = findViewById(R.id.options_make_it_yourself);
        findNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toFindNearby = new Intent(thisActivity, FindNearby.class);
                toFindNearby.putExtra("SearchValue", searchVal);
                startActivity(toFindNearby);
            }
        });
        makeYourself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMakeYourself = new Intent(thisActivity, MakeYourself.class);
                toMakeYourself.putExtra("SearchValue", searchVal);
                startActivity(toMakeYourself);
            }
        });
    }
}
