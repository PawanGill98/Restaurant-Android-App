package me.cmpt276.restaurantinspector.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
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
    private Map<String, Integer> hashMap;
    List<Restaurant> currentFilterResults;
    String[] hazardList;
    boolean[] checkedItems;
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurantManager = RestaurantManager.getInstance();
        myRestaurants = restaurantManager.getRestaurants();
        hashMap = new HashMap<>();
        hazardList = new String[]{getString(R.string.filter_hazard_low), getString(R.string.filter_hazard_moderate)
                ,getString(R.string.filter_hazard_high), getString(R.string.filter_favorites)};
        checkedItems = new boolean[] {true, true, true, false};
        setUpBottomNavigation();
        setupToolBar();
        populateListView(myRestaurants);
        populateHashMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Restaurant> restaurantResults = new ArrayList<>();
                for(Restaurant x: myRestaurants){
                    if(x.getName().toLowerCase().contains(s.toLowerCase())){
                        restaurantResults.add(x);
                    }
                }
                populateListView(restaurantResults);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.options){

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setTitle(getString(R.string.option_title));

            final EditText lessThanNCriticalInput = new EditText(MainActivity.this);
            lessThanNCriticalInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            lessThanNCriticalInput.setHint(getString(R.string.hint));
            mBuilder.setView(lessThanNCriticalInput);

            mBuilder.setMultiChoiceItems(hazardList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    checkedItems[position] = isChecked;
                }
            });

            mBuilder.setCancelable(false);

            mBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    currentFilterResults = new ArrayList<>();
                    for(Restaurant x: myRestaurants) {
                        if(checkedItems[3] && isFavourite(x) && isLessThanNCritical(x, lessThanNCriticalInput.getText().toString())) {
                            filterHazardLevel(x);
                            if(!checkedItems[0] && !checkedItems[1] && !checkedItems[2]) {
                                currentFilterResults.add(x);
                            }
                        }
                        else if (!checkedItems[3] && isLessThanNCritical(x, lessThanNCriticalInput.getText().toString())) {
                            filterHazardLevel(x);
                        }
                    }
                    populateListView(currentFilterResults);
                }
            });

            mBuilder.setNegativeButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            mBuilder.setNeutralButton(getString(R.string.clear), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    checkedItems = new boolean[] {true, true, true, false};
                    populateListView(myRestaurants);
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int convertStringToInt(String str){
        int x;
        try{
            x = Integer.parseInt(str);
        }catch(NumberFormatException ex){
            x = 999;
        }
        return x;
    }

    private boolean isLessThanNCritical(Restaurant x, String str) {
        if(x.hasInspections()) {
            if (x.getInspections().get(0).getNumCritical() <= convertStringToInt(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFavourite(Restaurant x) {
        if(checkFileExists(getInternalName(x.getAddress() + x.getName() + ".txt"))) {
            return true;
        }
        return false;
    }

    private void filterHazardLevel(Restaurant x) {
        if (x.hasInspections()) {
            if (checkedItems[0] && x.getInspections().get(0).getHazardRating().equals("Low")) {
                currentFilterResults.add(x);
            }
            else if (checkedItems[1] && x.getInspections().get(0).getHazardRating().equals("Moderate")) {
                currentFilterResults.add(x);
            }
            else if (checkedItems[2] && x.getInspections().get(0).getHazardRating().equals("High")) {
                currentFilterResults.add(x);
            }
        }
    }

    // Reference: https://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview
    @Override
    public void onResume() {
        super.onResume();
        restaurantManager = RestaurantManager.getInstance();
        myRestaurants = restaurantManager.getRestaurants();
        setUpBottomNavigation();
        ListView list = findViewById(R.id.restaurantListView);
        if (mListState != null)
            list.onRestoreInstanceState(mListState);
        mListState = null;
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mListState = state.getParcelable(LIST_STATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        ListView list = findViewById(R.id.restaurantListView);
        mListState = list.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mListState);
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

    private void populateListView(List<Restaurant> restaurants) {
        ArrayAdapter<Restaurant> adapter = new MyListAdapter(restaurants);
        ListView listView = findViewById(R.id.restaurantListView);
        listView.setAdapter(adapter);
        registerClickCallBack(restaurants);
    }

    private void registerClickCallBack(final List<Restaurant> restaurants) {
        ListView listView = findViewById(R.id.restaurantListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = SingleRestaurantInspection.makeIntent(MainActivity.this,
                        restaurants.get(position));
                intent.putExtra("calling_activity", MAIN_ACTIVITY_CALL_NUMBER);
                startActivity(intent);
            }
        });
    }

    public void populateHashMap() {
        hashMap.put("7-eleven", R.drawable.seveneleven);
        hashMap.put("a&w", R.drawable.aw);
        hashMap.put("barcelos", R.drawable.barcelos);
        hashMap.put("burger king", R.drawable.burgerking);
        hashMap.put("domino's", R.drawable.dominos);
        hashMap.put("dairy queen", R.drawable.dq);
        hashMap.put("freshii", R.drawable.freshii);
        hashMap.put("freshslice", R.drawable.freshslice);
        hashMap.put("ihop", R.drawable.ihop);
        hashMap.put("jugo juice", R.drawable.jugojuice);
        hashMap.put("kfc", R.drawable.kfc);
        hashMap.put("little caesars", R.drawable.littlecaesars);
        hashMap.put("mcdonald's", R.drawable.mcdonalds);
        hashMap.put("nandos", R.drawable.nandos);
        hashMap.put("non stop pizza", R.drawable.nonstoppizza);
        hashMap.put("panago", R.drawable.panago);
        hashMap.put("pizza hut", R.drawable.pizzahut);
        hashMap.put("quiznos", R.drawable.quiznos);
        hashMap.put("safeway", R.drawable.safeway);
        hashMap.put("save on foods", R.drawable.saveonfoods);
        hashMap.put("starbucks", R.drawable.starbucks);
        hashMap.put("subway", R.drawable.subway);
        hashMap.put("tim hortons", R.drawable.timhortons);
        hashMap.put("wendy's", R.drawable.wendys);
        hashMap.put("white spot", R.drawable.whitespot);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter(List<Restaurant> restaurants) {
            super(MainActivity.this, R.layout.restaurant_view, restaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_view, parent, false);
            }
            Restaurant currentRestaurant = getItem(position);

            TextView restaurantName = itemView.findViewById(R.id.item_restaurantName);
            restaurantName.setText(currentRestaurant.getName());
            parent.setBackgroundColor(getResources().getColor(R.color.beige));

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
                TextView issues = itemView.findViewById(R.id.item_issues);
                issues.setText(getString(R.string.no_inspections));

                TextView date = itemView.findViewById(R.id.item_date);
                date.setText(getString(R.string.empty_string));

                ImageView imageView = itemView.findViewById(R.id.item_hazardIcon);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.hazard));
            }
            if (checkFileExists(getInternalName(currentRestaurant.getAddress()+currentRestaurant.getName()) + ".txt")) {
                ImageView favoritedImage = itemView.findViewById(R.id.image_showFavorite);
                favoritedImage.setVisibility(View.VISIBLE);
            } else {
                ImageView favoritedImage = itemView.findViewById(R.id.image_showFavorite);
                favoritedImage.setVisibility(View.INVISIBLE);
            }
            return itemView;
        }

    }

    public Integer getIconValue(String restaurantNameKey) {
        Integer RestaurantIcon = R.drawable.restaurant_icon;
        for(String key : hashMap.keySet()) {
            if(restaurantNameKey.contains(key)) {
                RestaurantIcon = hashMap.get(key);
            }
        }
        return RestaurantIcon;
    }

    public boolean checkFileExists(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    public String getInternalName(String string) {
        return string.replaceAll("[\\\\|<|>|\"|?|/|*|\\||:]", "");
    }
}
