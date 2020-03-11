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
import me.cmpt276.restaurantinspector.Model.Violation;

public class InspectionActivity extends AppCompatActivity {

    private List<Violation> violations = new ArrayList<Violation>();

    private String sentString = "full_description";
    private String sentInteger = "violation_id";

    static Inspection inspection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        setTitle("RESTAURANT_NAME");

        //Back Button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateViolationList();
        populateViolationListView();

        registerClickCallBack();
    }

    private void populateViolationList() {
        violations = inspection.getViolations();

        TextView dateView = findViewById(R.id.inspection_date);
        dateView.setText(inspection.getFullInspectionDate());

        TextView typeView = findViewById(R.id.inspection_type);
        typeView.setText(inspection.getInspectionType());

        TextView critView = findViewById(R.id.crit_count);
        critView.setText("" + inspection.getNumCritical());

        TextView nonCritView = findViewById(R.id.non_crit_count);
        nonCritView.setText("" + inspection.getNumNonCritical());

        TextView hazardView = findViewById(R.id.hazard_rating);
        hazardView.setText(inspection.getHazardRating());

        ImageView hazardLevel = findViewById(R.id.hazard_icon);

        Resources res = getApplicationContext().getResources();
        hazardLevel.setImageResource(R.drawable.hazard);

        if(inspection.getHazardRating().equals("Low")) {
            int color = res.getColor(R.color.green);
            hazardLevel.setColorFilter(color);
        }
        else if(inspection.getHazardRating().equals("Moderate")) {
            int color = res.getColor(R.color.ORANGE);
            hazardLevel.setColorFilter(color);
        }
        else if(inspection.getHazardRating().equals("High")) {
            int color = res.getColor(R.color.red);
            hazardLevel.setColorFilter(color);
        }
    }

    private void populateViolationListView() {
        ArrayAdapter<Violation> adapter = new customAdapter();
        ListView list = findViewById(R.id.violation_list);
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

            ImageView imageView = itemView.findViewById(R.id.hazard_view);
            Resources res = getApplicationContext().getResources();
            int color = 0;
            if(currentViolation.getCriticality().equals("Critical")) {
                imageView.setImageResource(R.drawable.critical);
                color = res.getColor(R.color.red);
                imageView.setColorFilter(color);
            }
            else{
                imageView.setImageResource(R.drawable.non_critical);
                color = res.getColor(R.color.ORANGE);
                imageView.setColorFilter(color);
            }

            ImageView type_view = itemView.findViewById(R.id.violation_type_view);
            int id = currentViolation.getId();
            color = res.getColor(R.color.BLACK);
            if(id == 304 || id == 305) {
                type_view.setImageResource(R.drawable.pest);
                type_view.setColorFilter(color);
            }
            else if(id >= 301 && id <= 311 && id != 304 && id != 305 || id == 315) {
                type_view.setImageResource(R.drawable.equipment);
                type_view.setColorFilter(color);
            }
            else if(id >= 201 && id <= 212 && id != 207){
                type_view.setImageResource(R.drawable.food);
                type_view.setColorFilter(color);
            }
            else if(id >= 101 && id <= 104){
                type_view.setImageResource(R.drawable.building);
                type_view.setColorFilter(color);
            }
            else if(id == 314 || id >= 401 && id <= 403){
                type_view.setImageResource(R.drawable.sanitization);
                type_view.setColorFilter(color);
            }

            TextView idText = itemView.findViewById(R.id.violation_id_view);
            idText.setText("" + currentViolation.getId());

            TextView briefText = itemView.findViewById(R.id.brief_desc_view);
            briefText.setText(currentViolation.getBriefDescription());

            return itemView;
        }
    }

    public static Intent makeIntent(Context context, Inspection inspection){
        InspectionActivity.inspection = inspection;
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
        ListView list = findViewById(R.id.violation_list);
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
