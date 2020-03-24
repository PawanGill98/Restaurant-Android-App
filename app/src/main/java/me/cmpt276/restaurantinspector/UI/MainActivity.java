package me.cmpt276.restaurantinspector.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.cmpt276.restaurantinspector.GoogleMapActivity;
import me.cmpt276.restaurantinspector.Model.CSVReader;
import me.cmpt276.restaurantinspector.Model.FileHandler;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;
import me.cmpt276.restaurantinspector.Model.Time;
import me.cmpt276.restaurantinspector.R;

/**
 *  Displays list of restaurants on first screen
 */

public class MainActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;
    private List<Restaurant> myRestaurants;

    public static final String TRUE = "TRUE";
    private static final String RESTAURANT_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";

    private static final String RESTAURANT_FILE1 = "restaurants1.csv";
    private static final String INSPECTIONS_FILE1 = "inspections1.csv";

    private static final String VERSION_FILE = "version.txt";

    private static final String LAST_MODIFIED_FILE = "last_modified.txt";
    private static final String LOCAL_BUILD_DATE = "build_date.txt";

    private static final String ASK_FOR_UPDATE = "ask_update.txt";

    private static final String RESTAURANT_URL = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private static final String INSPECTIONS_URL = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";
    public static final String FALSE = "FALSE";

    Dialog dialog;
    CSVUpdater csvUpdater;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolBar();


        restaurantManager = RestaurantManager.getInstance();
        myRestaurants = restaurantManager.getRestaurants();
        populateListView();
        registerClickCallBack();
        setUpBottomNavigation();
    }




    private void setUpBottomNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant_list);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.restaurant_list:
                        return true;
                    case R.id.map:
                        overridePendingTransition(0,0);
                        finish();
                        Intent intent = GoogleMapActivity.makeIntent(MainActivity.this, myRestaurants);
                        intent.putExtra("fetch_data", "no_fetch");
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }

    private void setupToolBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green2)));
        getSupportActionBar().setTitle(getString(R.string.list_of_restaurants));
    }

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView listView = findViewById(R.id.restaurantListView);
        listView.setAdapter(adapter);
        registerClickCallBack();
    }

    //Icon HashMap
    Map<String, Integer> map = new HashMap<>();
    //Populate HashMap
    public void populateMap() {
        map.put("7-eleven", R.drawable.seveneleven);
        map.put("a&w", R.drawable.aw);
        map.put("barcelos", R.drawable.barcelos);
        map.put("burger king", R.drawable.burgerking);
        map.put("domino's", R.drawable.dominos);
        map.put("dairy queen", R.drawable.dq);
        map.put("freshii", R.drawable.freshii);
        map.put("freshslice", R.drawable.freshslice);
        map.put("ihop", R.drawable.ihop);
        map.put("jugo juice", R.drawable.jugojuice);
        map.put("kfc", R.drawable.kfc);
        map.put("little caesars", R.drawable.littlecaesars);
        map.put("mcdonald's", R.drawable.mcdonalds);
        map.put("nandos", R.drawable.nandos);
        map.put("non stop pizza", R.drawable.nonstoppizza);
        map.put("panago", R.drawable.panago);
        map.put("pizza hut", R.drawable.pizzahut);
        map.put("quiznos", R.drawable.quiznos);
        map.put("safeway", R.drawable.safeway);
        map.put("save on foods", R.drawable.saveonfoods);
        map.put("starbucks", R.drawable.starbucks);
        map.put("subway", R.drawable.subway);
        map.put("tim hortons", R.drawable.timhortons);
        map.put("wendy's", R.drawable.wendys);
        map.put("white spot", R.drawable.whitespot);
    }
    //Get Current Restaurant Icon
    public Integer getIconValue(String restaurantNameKey) {
        populateMap();
        Integer RestaurantIcon = R.drawable.restaurant_icon;
        for(String key : map.keySet()) {
            if(restaurantNameKey.contains(key)) {
                RestaurantIcon = map.get(key);
            }
        }
        return RestaurantIcon;
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.restaurant_view, myRestaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_view, parent, false);
            }
            Restaurant currentRestaurant = myRestaurants.get(position);

            TextView restaurantName = itemView.findViewById(R.id.item_restaurantName);
            restaurantName.setText(currentRestaurant.getName());

            //Set Restaurant Icon
            ImageView restaurantIcon = itemView.findViewById(R.id.item_restaurantIcon);
            String restaurantNameKey = currentRestaurant.getName().toLowerCase();
            restaurantIcon.setImageResource(getIconValue(restaurantNameKey));

            if(currentRestaurant.hasInspections()){
                ImageView hazardIcon = itemView.findViewById(R.id.item_hazardIcon);
                switch (currentRestaurant.getInspections().get(0).getHazardRating()) {
                    case "Low":
                        hazardIcon.setImageDrawable(getResources().getDrawable(R.drawable.green_acceptance_sign_icon));
                        break;
                    case "Moderate":
                        hazardIcon.setImageDrawable(getResources().getDrawable(R.drawable.orange_exlamation_mark_sign_icon));
                        break;
                    case "High":
                        hazardIcon.setImageDrawable(getResources().getDrawable(R.drawable.red_cross_sign_icon));
                        break;
                }

                int totalIssues = currentRestaurant.getInspections().get(0).getNumNonCritical()
                        + currentRestaurant.getInspections().get(0).getNumCritical();
                TextView issues = itemView.findViewById(R.id.item_issues);
                issues.setText(getString(R.string.total_issues, totalIssues));
                TextView date = itemView.findViewById(R.id.item_date);
                date.setText(getString(R.string.current_restaurant_date,
                        currentRestaurant.getInspections().get(0).getHowLongAgo()));
                parent.setBackgroundColor(getResources().getColor(R.color.beige));

            }
            else {
                Resources res = getContext().getResources();
                ImageView hazardIcon = itemView.findViewById(R.id.item_hazardIcon);
                int newColor = res.getColor(R.color.blue);
                hazardIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                TextView issues = itemView.findViewById(R.id.item_issues);
                issues.setText(getString(R.string.no_inspections));

                TextView date = itemView.findViewById(R.id.item_date);
                date.setText(getString(R.string.empty_string));
            }
            return itemView;
        }
    }

    private void registerClickCallBack() {
        ListView listView = findViewById(R.id.restaurantListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!myRestaurants.get(position).hasInspections()){
                    FragmentManager manager = getSupportFragmentManager();
                    FirstScreenPopUpFragment dialog = new FirstScreenPopUpFragment();
                    dialog.show(manager, "Inspection without violations");
                }
                else {
                    Intent intent = SingleRestaurantInspection.makeIntent(MainActivity.this,
                            restaurantManager.getRestaurantByIndex(position));
                    startActivity(intent);
                }
            }
        });
    }

    public class CSVUpdater extends AsyncTask<Void, Void, Void> {
        String restaurantsData = "";
        String inspectionsData = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL restaurantURL = new URL(RESTAURANT_URL);
                URL inspectionsURL = new URL(INSPECTIONS_URL);


                HttpURLConnection restaurantsURLConnnection = (HttpURLConnection) restaurantURL.openConnection();
                InputStream restaurantsInputStream = restaurantsURLConnnection.getInputStream();
                BufferedReader restaurantsReader = new BufferedReader(new InputStreamReader(restaurantsInputStream));
                String restaurantLine;
                FileOutputStream restaurantsOutputStream = openFileOutput(RESTAURANT_FILE1, MODE_PRIVATE);
                BufferedWriter restaurantsBufferedWriter = new BufferedWriter( new OutputStreamWriter(restaurantsOutputStream, "UTF-8"));
                while ((restaurantLine = restaurantsReader.readLine()) != null) {
                    Log.d("Resturants: ", restaurantLine);  // Do not delete this
                    FileHandler.writeToInternalMemory(restaurantsBufferedWriter, restaurantLine);
                }
                restaurantsBufferedWriter.close();


                HttpURLConnection inspectionsURLConnection = (HttpURLConnection) inspectionsURL.openConnection();
                InputStream inspectionsInputStream = inspectionsURLConnection.getInputStream();
                BufferedReader inspectionsReader = new BufferedReader(new InputStreamReader(inspectionsInputStream));
                String inspectionsLine;
                FileOutputStream inspectionsOutputStream = openFileOutput(INSPECTIONS_FILE1, MODE_PRIVATE);
                BufferedWriter inspectionsBufferedWriter = new BufferedWriter( new OutputStreamWriter(inspectionsOutputStream, "UTF-8"));


                while ((inspectionsLine = inspectionsReader.readLine()) != null) {
                    Log.d("Inspections: ", inspectionsLine);    // Do not delete this
                    FileHandler.writeToInternalMemory(inspectionsBufferedWriter, inspectionsLine);
                }
                inspectionsBufferedWriter.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
}
