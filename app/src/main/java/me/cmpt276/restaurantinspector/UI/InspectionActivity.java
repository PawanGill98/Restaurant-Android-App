package me.cmpt276.restaurantinspector.UI;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.List;

import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Violation;
import me.cmpt276.restaurantinspector.R;

/**
 *  Displays all violations of a single inspection on third screen
 */
public class InspectionActivity extends AppCompatActivity {

    private List<Violation> violations = new ArrayList<>();

    private String sentString = "full_description";
    private String sentInteger = "violation_id";

    private static Inspection inspection;
    private static String restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        setupToolBar();

        populateViolationList();
        populateViolationListView();

        registerClickCallBack();
    }

    private void setupToolBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green2)));
        getSupportActionBar().setTitle(restaurantName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void populateViolationList() {
        violations = inspection.getViolations();

        TextView dateView = findViewById(R.id.inspection_date);
        dateView.setText(getString(R.string.hazard_rating, inspection.getFullInspectionDate()));

        TextView typeView = findViewById(R.id.inspection_type);
        typeView.setText(getString(R.string.inspection_type, inspection.getInspectionType()));

        TextView critView = findViewById(R.id.crit_count);
        critView.setText(getString(R.string.number_of_critical, inspection.getNumCritical()));

        TextView nonCritView = findViewById(R.id.non_crit_count);
        nonCritView.setText(getString(R.string.number_of_non_critical, inspection.getNumNonCritical()));

        TextView hazardView = findViewById(R.id.hazard_rating);
        hazardView.setText(getString(R.string.hazard_rating, inspection.getHazardRating()));

        ImageView hazardLevel = findViewById(R.id.hazard_icon);

        switch (inspection.getHazardRating()) {
            case "Low":
                hazardLevel.setImageResource(R.drawable.green_acceptance_sign_icon);
                break;
            case "Moderate":
                hazardLevel.setImageResource(R.drawable.orange_exlamation_mark_sign_icon);
                break;
            case "High":
                hazardLevel.setImageResource(R.drawable.red_cross_sign_icon);
                break;
            default:
                hazardLevel.setImageResource(R.drawable.question_mark_icon);
                break;
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

            if(currentViolation.getCriticality().equals("Critical")) {
                imageView.setImageResource(R.drawable.critical);
            }
            else{
                imageView.setImageResource(R.drawable.non_critical);
            }

            ImageView type_view = itemView.findViewById(R.id.violation_type_view);
            int id = currentViolation.getId();

            if(id == 304 || id == 305) {
                type_view.setImageResource(R.drawable.pest);
            }
            else if(id >= 301 && id <= 312 || id == 315) {
                type_view.setImageResource(R.drawable.equipment);
            }
            else if(id >= 201 && id <= 212 && id != 207 || id == 501 || id == 502){
                type_view.setImageResource(R.drawable.food);
            }
            else if(id >= 101 && id <= 104){
                type_view.setImageResource(R.drawable.building);
            }
            else if(id == 313 || id == 314 || id >= 401 && id <= 404){
                type_view.setImageResource(R.drawable.sanitization);
            }

            TextView idText = itemView.findViewById(R.id.violation_id_view);
            idText.setText(getString(R.string.violation_id, currentViolation.getId()));

            String violationNumberId = "violation_" + currentViolation.getId();
            TextView briefText = itemView.findViewById(R.id.brief_desc_view);
            String briefViolationText = getStringByIdName(InspectionActivity.this,
                    violationNumberId);
            briefText.setText(briefViolationText);
            return itemView;
        }
    }
    public static String getStringByIdName(Context context, String idName) {
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(idName, "string", context.getPackageName()));
    }

    public static Intent makeIntent(Context context, Inspection inspection, String restaurantName){
        InspectionActivity.inspection = inspection;
        InspectionActivity.restaurantName = restaurantName;
        return new Intent(context, InspectionActivity.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

                Intent intent = new Intent(VioPopUpActivity.makeIntent(InspectionActivity.this));
                intent.putExtra(sentString,clickedViolation.getDescription());
                intent.putExtra(sentInteger, clickedViolation.getId());
                startActivity(intent);
            }
        });
    }

}
