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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;
    private SearchView searchView;
    boolean[] checkedItems;
    private String[] radioList;
    private String[] hazardValueList;
    private String hazardLevelInput;
    boolean selected;
    private List<Restaurant> searchResults;
    private List<Restaurant> currentFilterResults1;
    private List<Restaurant> currentFilterResults2;
    private List<Restaurant> currentFilterResults3;

    private String sent_string = "query_main";
    private String received_string = "query_map";
    public String QUERY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurantManager = RestaurantManager.getInstance();
        myRestaurants = restaurantManager.getRestaurants();
        hashMap = new HashMap<>();
        searchResults = myRestaurants;
        currentFilterResults3 = restaurantManager.getRestaurants();
        hazardValueList = new String[]{"Low", "Moderate", "High"};
        radioList = new String[] {getString(R.string.filter_hazard_low), getString(R.string.filter_hazard_moderate),
                getString(R.string.filter_hazard_high)};
        checkedItems = new boolean[] {false};
        selected = false;
        setUpBottomNavigation();
        setupToolBar();
        populateListView(myRestaurants);
        populateHashMap();

        Intent intent = getIntent();
        QUERY = intent.getStringExtra(received_string);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                onQueryTextChange(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchResults = new ArrayList<>();
                for(Restaurant x: currentFilterResults3){
                    if(x.getName().toLowerCase().contains(s.toLowerCase())){
                        searchResults.add(x);
                    }
                }
                populateListView(searchResults);
                QUERY = s;
                return false;
            }
        });
        if(QUERY != null && !QUERY.isEmpty()){
            searchView.setQuery(QUERY, true);
            searchView.setIconified(false);
            searchView.clearFocus();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.options){

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setTitle(getString(R.string.option_title));
            mBuilder.setCancelable(false);

            final EditText lessThanNCriticalInput = new EditText(MainActivity.this);
            lessThanNCriticalInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            lessThanNCriticalInput.setHint(getString(R.string.hint));

            final CheckBox favouriteInput = new CheckBox(MainActivity.this);
            favouriteInput.setText(getString(R.string.filter_favorites));

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(lessThanNCriticalInput);
            layout.addView(favouriteInput);
            layout.setPadding(80, 0, 0, 0);
            mBuilder.setView(layout);

            mBuilder.setSingleChoiceItems(radioList, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    hazardLevelInput = hazardValueList[i];
                    selected = true;
                }
            });

            mBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    currentFilterResults1 = new ArrayList<>();
                    if(favouriteInput.isChecked()) {
                        for (Restaurant x : myRestaurants) {
                            if (isFavourite(x)) {
                                currentFilterResults1.add(x);
                            }
                        }
                    } else{
                        currentFilterResults1 = myRestaurants;
                    }
                    currentFilterResults2 = new ArrayList<>();
                    for(Restaurant x : currentFilterResults1) {
                        if(isLessThanNCritical(x, lessThanNCriticalInput.getText().toString())) {
                            currentFilterResults2.add(x);
                        }
                    }
                    currentFilterResults3 = new ArrayList<>();
                    if(selected) {
                        for (Restaurant x : currentFilterResults2) {
                            if (x.hasInspections()
                                    && x.getInspections().get(0).getHazardRating().equals(hazardLevelInput)) {
                                currentFilterResults3.add(x);
                            }
                        }
                    } else {
                        currentFilterResults3 = currentFilterResults2;
                    }
                    populateListView(currentFilterResults3);
                    CharSequence temp = searchView.getQuery();
                    searchView.setQuery("", true);
                    searchView.setQuery(temp, true);
                    hazardLevelInput = "";
                    selected = false;
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
                    checkedItems = new boolean[] {false};
                    currentFilterResults3 = myRestaurants;
                    searchResults = myRestaurants;
                    searchView.setQuery("", true);
                    populateListView(myRestaurants);
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
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
                        intent.putExtra(sent_string,QUERY);
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
