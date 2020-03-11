package me.cmpt276.restaurantinspector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;

public class SingleRestaurantInspection extends AppCompatActivity {

    private static Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_restaurant_inspection);

        setupToolbar();
        setupTextView();
        populateListView();
        registerClickCallBack();
    }

    private void setupToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle(restaurant.getName());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTextView() {
        TextView view = findViewById(R.id.screen2_restaurant_name);
        view.setText(restaurant.getName());
        view = findViewById(R.id.screen2_restaurant_address);
        view.setText(getString(R.string.address_city, restaurant.getAddress(), restaurant.getCity()));
        view = findViewById(R.id.screen2_gps_coords);
        view.setText(getString(R.string.latitude_longitude, restaurant.getLatitude(), restaurant.getLongitude()));
    }


    public static Intent makeIntent(Context c, Restaurant restaurant) {
        SingleRestaurantInspection.restaurant = restaurant;
        return new Intent(c, SingleRestaurantInspection.class);
    }

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.screen2_list_view);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {
        public MyListAdapter() {
            super(SingleRestaurantInspection.this, R.layout.inspection_view, restaurant.getInspections());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_view, parent, false);
            }

            ImageView imageView = itemView.findViewById(R.id.screen2_hazard_sign);
            int id = chooseHazardSignColor(position);
            imageView.setImageDrawable(getResources().getDrawable(id));

            TextView view = itemView.findViewById(R.id.screen2_critical_issue);
            view.setText(getString(R.string.number_critical_issues,
                    restaurant.getInspections().get(position).getNumCritical()));
            view = itemView.findViewById(R.id.screen2_non_critical_issue);
            view.setText(getString(R.string.number_non_critical_issues,
                    restaurant.getInspections().get(position).getNumNonCritical()));
            view = itemView.findViewById(R.id.screen2_inspection_date);
            view.setText(restaurant.getInspections().get(position).getHowLongAgo());

            parent.setBackgroundColor(getResources().getColor(R.color.beige));
            return itemView;
        }
    }

    private int chooseHazardSignColor(int position) {
        String hazard = restaurant.getInspections().get(position).getHazardRating();
        int id;
        switch (hazard) {
            case "Low":
                id = R.drawable.green_acceptance_sign_icon;
                break;
            case "Moderate":
                id = R.drawable.orange_exlamation_mark_sign_icon;
                break;
            case "High":
                id = R.drawable.red_cross_sign_icon;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + hazard);
        }
        return id;
    }

    private void registerClickCallBack() {
        ListView list = findViewById(R.id.screen2_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(restaurant.getInspections().get(position).getNumCritical() +
                        restaurant.getInspections().get(position).getNumNonCritical() == 0){
                    FragmentManager manager = getSupportFragmentManager();
                    SecondScreenPopUpFragment dialog = new SecondScreenPopUpFragment();
                    dialog.show(manager, "Inspection without violations");
                }else {
                    Intent intent = InspectionActivity.makeIntent(SingleRestaurantInspection.this, restaurant.getInspections().get(position));
                    startActivity(intent);
                }
            }
        });
    }
}

