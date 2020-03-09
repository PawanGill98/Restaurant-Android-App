package me.cmpt276.restaurantinspector;

import androidx.appcompat.app.AppCompatActivity;

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

        //Action Bar
        getSupportActionBar().setTitle("List of Restaurant's");
        restaurantManager = restaurantManager.getInstance();

        CSVReader.readRestaurantData(getResources().openRawResource(R.raw.restaurants_itr1));
        CSVReader.readInspectionReportData(getResources().openRawResource(R.raw.inspectionreports_itr1), getResources().openRawResource(R.raw.brief_descriptions));

        myRestaurants = restaurantManager.getRestaurants();
        populateListView();
        /*
        // Iterate through all restaurants
        for (Restaurant restaurant : restaurantManager.getRestaurants()) {
            Log.d("Restaurants list: ", restaurant + "");
        }

        // Get restaurant by tracking number. Can also get by index using getRestaurantByIndex(int);
        Restaurant restaurant = restaurantManager.getRestaurantByTrackingNumber("SDFO-8HKP7E");
        Log.d("Individual restaurant: ", restaurant + "");

        // List of inspections from restaurant above
        List<Inspection> inspections = restaurant.getInspections();
        for (Inspection inspection : inspections) {
            Log.d("Inspections list: ", inspection + "");

            // List of violations from inspection above
            List<Violation> violations = inspection.getViolations();
            for (Violation violation : violations) {
                Log.d("Violations list: ", violation + "");
            }
        }
        */
    }

    private void populateListView() {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView listView = (ListView) findViewById(R.id.restaurantListView);
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

            //Restaurant Name
            TextView restaurantName = itemView.findViewById(R.id.item_restaurantName);
            restaurantName.setText(currentRestaurant.getName());
            //Restaurant Icon
            //ImageView restaurantIcon = (ImageView) itemView.findViewById(R.id.item_restaurantIcon);
            //restaurantIcon.setImageResource(currentRestaurant.getRestaurantIconID());

            if(currentRestaurant.hasInspections()){
                //Hazard Icon Colour
                Resources res = getContext().getResources();
                ImageView hazardIcon = (ImageView) itemView.findViewById(R.id.item_hazardIcon);
                if(currentRestaurant.getInspections().get(0).getHazardRating().equals("Low")) {
                    int newColor = res.getColor(R.color.new_color);
                    hazardIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                }
                else if(currentRestaurant.getInspections().get(0).getHazardRating().equals("Moderate")) {
                    int newColor = res.getColor(R.color.colorPrimary);
                    hazardIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                }
                else if(currentRestaurant.getInspections().get(0).getHazardRating().equals("High")) {
                    int newColor = res.getColor(R.color.colorAccent);
                    hazardIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                }

                //Issues
                int totalIssues = currentRestaurant.getInspections().get(0).getNumNonCritical()
                        + currentRestaurant.getInspections().get(0).getNumCritical();
                TextView issues = (TextView) itemView.findViewById(R.id.item_issues);
                issues.setText("Issues: " + totalIssues);
                //Date
                TextView date = (TextView) itemView.findViewById(R.id.item_date);
                date.setText("" + currentRestaurant.getInspections().get(0).getHowLongAgo());
            }
            else {
                //Hazard Icon Colour
                Resources res = getContext().getResources();
                ImageView hazardIcon = (ImageView) itemView.findViewById(R.id.item_hazardIcon);
                int newColor = res.getColor(R.color.colorPrimary);
                hazardIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                //Issues;
                TextView issues = (TextView) itemView.findViewById(R.id.item_issues);
                issues.setText("No Inspections at this Time");
                //Date
                TextView date = (TextView) itemView.findViewById(R.id.item_date);
                date.setText("");
            }
            return itemView;
        }
    }

    private void registerClickCallBack() {
        ListView listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Restaurant clickedRestaurant = myRestaurants.get(i);
                Toast.makeText(MainActivity.this, "!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
