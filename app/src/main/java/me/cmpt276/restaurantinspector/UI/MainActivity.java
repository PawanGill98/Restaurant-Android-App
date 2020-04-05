package me.cmpt276.restaurantinspector.UI;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.cmpt276.restaurantinspector.GoogleMaps.GoogleMapActivity;
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
            parent.setBackgroundColor(getResources().getColor(R.color.beige));

            //Set Restaurant Icon
            ImageView restaurantIcon = itemView.findViewById(R.id.item_restaurantIcon);
            String restaurantNameKey = currentRestaurant.getName().toLowerCase();
            restaurantIcon.setImageResource(getIconValue(restaurantNameKey));

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
                    default:
                        hazardIcon.setImageResource(R.drawable.question_mark_icon);
                        break;
                }

                int totalIssues = currentRestaurant.getInspections().get(0).getNumNonCritical()
                        + currentRestaurant.getInspections().get(0).getNumCritical();
                TextView issues = itemView.findViewById(R.id.item_issues);
                issues.setText(getString(R.string.total_issues, totalIssues));

                TextView date = itemView.findViewById(R.id.item_date);
                if(currentRestaurant.getInspections().get(0).getHowLongAgo().contains("days ago")){
                    int i = 0;
                    String temp = "";
                    while(currentRestaurant.getInspections().get(0).getHowLongAgo().charAt(i) != ' '){
                        temp += currentRestaurant.getInspections().get(0).getHowLongAgo().charAt(i);
                        i++;
                    }
                    temp += " ";
                    temp += getString(R.string.days_ago_for_date);
                    date.setText(getString(R.string.current_restaurant_date,
                            temp));
                }else {
                    date.setText(getString(R.string.current_restaurant_date,
                            currentRestaurant.getInspections().get(0).getHowLongAgo()));
                }


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


                    Intent intent = SingleRestaurantInspection.makeIntent(MainActivity.this,
                            restaurantManager.getRestaurantByIndex(position));
                    intent.putExtra("calling_activity", MAIN_ACTIVITY_CALL_NUMBER);
                    startActivity(intent);

            }
        });
    }
}
