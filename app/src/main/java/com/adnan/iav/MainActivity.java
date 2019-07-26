package com.adnan.iav;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.adnan.kav.R;
import com.adnan.kavlibrary.InputAccessoryViewHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new InputAccessoryViewHandler(this)
                .withActivity(this)
                .accessoryView(findViewById(R.id.input_accessory_view))
                .trigger(findViewById(R.id.btnSearch))
                .focus((EditText) findViewById(R.id.searchEditText))
                .handle();

    }
}
