package me.cmpt276.restaurantinspector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;

public class SingleRestaurantInspection extends AppCompatActivity {

    private static Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_restaurant_inspection);

        setupTextView();
        populateListView();
    }

    private void setupTextView() {
        TextView view = findViewById(R.id.screen2_restaurant_name);
        view.setText(restaurant.getName());
        TextView view2 = findViewById(R.id.screen2_restaurant_address);
        view2.setText(restaurant.getAddress() + ", " + restaurant.getCity());
        TextView view3 = findViewById(R.id.screen2_gps_coords);
        view3.setText(restaurant.getLatitude() + ", " + restaurant.getLongitude());
    }


    public static Intent makeIntent(Context c, Restaurant restaurant){
        SingleRestaurantInspection.restaurant = restaurant;
        return new Intent(c, SingleRestaurantInspection.class);
    }

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.screen2_list_view);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Inspection>{
        public MyListAdapter(){
            super(SingleRestaurantInspection.this, R.layout.item_view, restaurant.getInspections());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

//            Inspection currentInspection = restaurant.getInspections().get(position);
            ImageView imageView = itemView.findViewById(R.id.screen2_hazard_sign);
            int id = chooseHazardSignColor(position);
            imageView.setImageDrawable(getResources().getDrawable(id));

//            TextView view = findViewById(R.id.screen2_critical_issue);
//            view.setText("hello");

            return imageView;
//            return super.getView(position, convertView, parent);
        }
    }

    private int chooseHazardSignColor(int position) {
        String hazard = restaurant.getInspections().get(position).getHazardRating();
        int id;
        switch (hazard){
            case "Low":
               id = R.drawable.warning_green;
               break;
            case "Moderate":
                id = R.drawable.warning_yellow;
                break;
            case "High":
                id = R.drawable.warning_red;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + hazard);
        }
        return id;
    }
}
