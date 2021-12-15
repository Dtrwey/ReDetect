package com.ReDetect.redetect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ReDetect.redetect.R;

public class Tips extends AppCompatActivity {
    /*
    Sets layout.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips);
    }

    /*
    Goes back to MainActivity, which has the camera utility.
    */
    public void onClickGo(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
