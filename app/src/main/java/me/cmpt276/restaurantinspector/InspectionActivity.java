package me.cmpt276.restaurantinspector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.List;

import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.Violation;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;

public class InspectionActivity extends AppCompatActivity {

    private List<Violation> violations = new ArrayList<Violation>();

    private String sentString = "full_description";
    private String sentInteger = "violation_id";

    private String receivedRestaurantID = "";
    private String restaurantID = "SWOD-AHZUMF";

    private String receivedInspectionIndex = "";
    private int inspectionIndex = 7;

    private RestaurantManager restaurantManager = RestaurantManager.getInstance();
    private Restaurant restaurant;

    List<Inspection> inspections = new ArrayList<Inspection>();

    static Inspection inspect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        setTitle("RESTAURANT_NAME");

        //Back Button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fill_information();

        populateViolationList();
        populateViolationListView();

        registerClickCallBack();
    }

//    private void fill_information(){
//        Intent intent = getIntent();
//        //restaurantID = intent.getStringExtra(receivedRestaurantID);
//        restaurant = restaurantManager.getRestaurantByTrackingNumber(restaurantID);
//
//        //inspectionIndex = intent.getIntExtra(receivedInspectionIndex,0);
//        inspections = restaurant.getInspections();
//
//    }

    private void populateViolationList() {
        //Inspection inspection = inspections.get(inspectionIndex);

        violations = inspect.getViolations();

        TextView dateView = (TextView) findViewById(R.id.inspection_date);
        dateView.setText(inspect.getFullInspectionDate());

        TextView typeView = (TextView) findViewById(R.id.inspection_type);
        typeView.setText(inspect.getInspectionType());

        TextView critView = (TextView) findViewById(R.id.crit_count);
        critView.setText("" + inspect.getNumCritical());

        TextView nonCritView = (TextView) findViewById(R.id.non_crit_count);
        nonCritView.setText("" + inspect.getNumNonCritical());

        TextView hazardView = (TextView) findViewById(R.id.hazard_rating);
        hazardView.setText(inspect.getHazardRating());

        ImageView hazardLevel = (ImageView) findViewById(R.id.hazard_icon);

        Resources res = getApplicationContext().getResources();
        hazardLevel.setImageResource(R.drawable.hazard);

        if(inspect.getHazardRating().equals("Low")) {
            int color = res.getColor(R.color.GREEN);
            hazardLevel.setColorFilter(color);
        }
        else if(inspect.getHazardRating().equals("Moderate")) {
            int color = res.getColor(R.color.ORANGE);
            hazardLevel.setColorFilter(color);
        }
        else if(inspect.getHazardRating().equals("High")) {
            int color = res.getColor(R.color.RED);
            hazardLevel.setColorFilter(color);
        }
    }

    private void populateViolationListView() {
        ArrayAdapter<Violation> adapter = new customAdapter();
        ListView list = (ListView) findViewById(R.id.violation_list);
        list.setAdapter(adapter);
    }

    private class customAdapter extends ArrayAdapter<Violation>{
        public customAdapter(){
            super(InspectionActivity.this, R.layout.violation_view, violations);
        }

        @Override
        public View getView(int position, View conterView, ViewGroup parent){
            View itemView = conterView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violation_view, parent, false);
            }

            Violation currentViolation = violations.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.hazard_view);

            Resources res = getApplicationContext().getResources();

            if(currentViolation.getCriticality().equals("Critical")) {
                imageView.setImageResource(R.drawable.critical);
                int color = res.getColor(R.color.RED);
                imageView.setColorFilter(color);
            }
            else{
                imageView.setImageResource(R.drawable.non_critical);
                int color = res.getColor(R.color.ORANGE);
                imageView.setColorFilter(color);
            }

            TextView idText = (TextView) itemView.findViewById(R.id.violation_id_view);
            idText.setText("" + currentViolation.getId());

            TextView briefText = (TextView) itemView.findViewById(R.id.brief_desc_view);
            briefText.setText(currentViolation.getBriefDescription());

            return itemView;
        }
    }

    public static Intent makeIntent(Context context, Inspection inspection){
        inspect = inspection;
        return new Intent(context, InspectionActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerClickCallBack() {
        ListView list = (ListView) findViewById(R.id.violation_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation clickedViolation = violations.get(position);
                String message = "You clicked violation " + clickedViolation.getId();
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(VioPopUpActivity.makeIntent(InspectionActivity.this));
                intent.putExtra(sentString,clickedViolation.getDescription());
                intent.putExtra(sentInteger, clickedViolation.getId());
                startActivity(intent);
            }
        });
    }

}
