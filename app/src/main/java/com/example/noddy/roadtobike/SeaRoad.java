package com.example.noddy.roadtobike;
/*해안가도로 페이지*/
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class SeaRoad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_sea_road);
    }
}
