package me.cmpt276.restaurantinspector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import me.cmpt276.restaurantinspector.Model.CSVReader;
import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;

public class MainActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantManager = restaurantManager.getInstance();

        CSVReader.readRestaurantData(getResources().openRawResource(R.raw.restaurants_itr1));
        CSVReader.readInspectionReportData(getResources().openRawResource(R.raw.inspectionreports_itr1));
    }
}
