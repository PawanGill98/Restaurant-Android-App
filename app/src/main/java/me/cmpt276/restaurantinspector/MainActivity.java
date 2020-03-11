package me.cmpt276.restaurantinspector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.cmpt276.restaurantinspector.Model.CSVReader;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;

public class MainActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;
    private List<Restaurant> myRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("List of Restaurants");
        restaurantManager = RestaurantManager.getInstance();

        CSVReader.readRestaurantData(getResources().openRawResource(R.raw.restaurants_itr1));
        CSVReader.readInspectionReportData(getResources().openRawResource(R.raw.inspectionreports_itr1),
                getResources().openRawResource(R.raw.brief_descriptions));

        myRestaurants = restaurantManager.getRestaurants();
        populateListView();
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = SingleRestaurantInspection.makeIntent(MainActivity.this, restaurantManager.getRestaurantByIndex(i));
                startActivity(intent);
            }
        });
    }

}
