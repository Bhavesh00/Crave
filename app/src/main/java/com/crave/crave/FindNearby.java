package com.crave.crave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FindNearby extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        String searchVal = getIntent().getStringExtra("SearchValue");
        TextView searchValue = findViewById(R.id.search_val);
        searchValue.setText(searchVal);
    }
}
