package me.cmpt276.restaurantinspector.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import me.cmpt276.restaurantinspector.GoogleMaps.GoogleMapActivity;
import me.cmpt276.restaurantinspector.Model.FileHandler;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;
import me.cmpt276.restaurantinspector.R;

/**
 *  Displays list of restaurants on first screen
 *
 */

public class MainActivity extends AppCompatActivity {

    public static final int MAIN_ACTIVITY_CALL_NUMBER = 1001;
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
                        Intent intent = GoogleMapActivity.makeIntent(MainActivity.this);
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
            parent.setBackgroundColor(getResources().getColor(R.color.beige));

            if(currentRestaurant.hasInspections()){
                Log.d("Inside if", currentRestaurant.getName());
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

            } else{
                Log.d("Inside else", currentRestaurant.getName());
                TextView issues = itemView.findViewById(R.id.item_issues);
                issues.setText(getString(R.string.no_inspections));

                TextView date = itemView.findViewById(R.id.item_date);
                date.setText(getString(R.string.empty_string));

                ImageView imageView = itemView.findViewById(R.id.item_hazardIcon);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.hazard));
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
                    intent.putExtra("calling_activity", MAIN_ACTIVITY_CALL_NUMBER);
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
