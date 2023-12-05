package de.hka.projekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //beim Aufrufen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_map = this.findViewById(R.id.btnMap);

        btn_map.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
        });

        Log.d("MainActivity", "onCreate");
    }

    @Override
    protected void onResume() { //beim weiteren Aufrufen
        super.onResume();

        Log.i("MainActivity", "onResume");
    }
}