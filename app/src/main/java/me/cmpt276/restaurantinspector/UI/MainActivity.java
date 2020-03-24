package me.cmpt276.restaurantinspector.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.cmpt276.restaurantinspector.GoogleMapActivity;
import me.cmpt276.restaurantinspector.Model.CSVReader;
import me.cmpt276.restaurantinspector.Model.FileHandler;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;
import me.cmpt276.restaurantinspector.Model.Time;
import me.cmpt276.restaurantinspector.R;

/**
 *  Displays list of restaurants on first screen
 */

public class MainActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;
    private List<Restaurant> myRestaurants;

    public static final String TRUE = "TRUE";
    private static final String RESTAURANT_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";

    private static final String RESTAURANT_FILE1 = "restaurants1.csv";
    private static final String INSPECTIONS_FILE1 = "inspections1.csv";

    private static final String VERSION_FILE = "version.txt";

    private static final String LAST_MODIFIED_FILE = "last_modified.txt";
    private static final String LOCAL_BUILD_DATE = "build_date.txt";

    private static final String ASK_FOR_UPDATE = "ask_update.txt";

    private static final String RESTAURANT_URL = "https://data.surrey.ca/dataset/3c8cb648-0e80-4659-9078-ef4917b90ffb/resource/0e5d04a2-be9b-40fe-8de2-e88362ea916b/download/restaurants.csv";
    private static final String INSPECTIONS_URL = "https://data.surrey.ca/dataset/948e994d-74f5-41a2-b3cb-33fa6a98aa96/resource/30b38b66-649f-4507-a632-d5f6f5fe87f1/download/fraserhealthrestaurantinspectionreports.csv";
    public static final String FALSE = "FALSE";

    Dialog dialog;
    CSVUpdater csvUpdater;

    public boolean checkFileExists(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolBar();


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        setupDialog();
        if (!checkFileExists(RESTAURANT_FILE) && !checkFileExists(INSPECTIONS_FILE) && !checkFileExists(VERSION_FILE)) {
            FileOutputStream askUpdateStream ;
            try {
                askUpdateStream = openFileOutput(ASK_FOR_UPDATE, MODE_PRIVATE);
                BufferedWriter askUpdateWriter = new BufferedWriter( new OutputStreamWriter(askUpdateStream, "UTF-8"));
                FileHandler.writeToInternalMemory(askUpdateWriter, TRUE);
                askUpdateWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream restaurantsOutputStream;
            FileOutputStream inspectionsOutputStream;
            try {
                restaurantsOutputStream = openFileOutput(RESTAURANT_FILE, MODE_PRIVATE);
                InputStream restaurantsInputStream = getResources().openRawResource(R.raw.restaurants_itr1);

                inspectionsOutputStream = openFileOutput(INSPECTIONS_FILE, MODE_PRIVATE);
                InputStream inspectionsInputStream = getResources().openRawResource(R.raw.inspectionreports_itr1);

                FileHandler.loadIteration1Data(restaurantsInputStream, inspectionsInputStream, restaurantsOutputStream, inspectionsOutputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FileInputStream restaurantInputStream;
            try {
                restaurantInputStream = openFileInput(RESTAURANT_FILE);
                CSVReader.readRestaurantData(restaurantInputStream);
                CSVReader.readInspectionReportData(getResources().openRawResource(R.raw.inspectionreports_itr1), getResources().openRawResource(R.raw.brief_descriptions));

                FileOutputStream lastModifiedStream = openFileOutput(LAST_MODIFIED_FILE, MODE_PRIVATE);
                BufferedWriter lastModifiedWriter = new BufferedWriter( new OutputStreamWriter(lastModifiedStream, "UTF-8"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                Date date = new Date();
                FileHandler.writeToInternalMemory(lastModifiedWriter, dateFormat.format(date));
                lastModifiedWriter.close();
                FileOutputStream localBuildStream = openFileOutput(LOCAL_BUILD_DATE, MODE_PRIVATE);
                BufferedWriter localBuildWriter = new BufferedWriter( new OutputStreamWriter(localBuildStream, "UTF-8"));
                Date date2 = new Date();
                FileHandler.writeToInternalMemory(localBuildWriter, dateFormat.format(date2));
                localBuildWriter.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.show();
        } else {
            if (checkFileExists(VERSION_FILE)) {
                FileInputStream restaurantFIS;
                try {
                    restaurantFIS = openFileInput(RESTAURANT_FILE);
                    InputStream inspectionsFIS = openFileInput(INSPECTIONS_FILE);
                    CSVReader.readRestaurantData(restaurantFIS);
                    CSVReader.readUpdatedInspectionReportData(inspectionsFIS, getResources().openRawResource(R.raw.brief_descriptions), getResources().openRawResource(R.raw.allviolations));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (!checkFileExists(VERSION_FILE)) {
                FileInputStream restaurantInputStream;
                try {
                    restaurantInputStream = openFileInput(RESTAURANT_FILE);
                    CSVReader.readRestaurantData(restaurantInputStream);
                    CSVReader.readInspectionReportData(getResources().openRawResource(R.raw.inspectionreports_itr1), getResources().openRawResource(R.raw.brief_descriptions));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            try {
                String askUpdate;
                InputStream askUpdateStream = openFileInput(ASK_FOR_UPDATE);
                BufferedReader askUpdateReader = new BufferedReader(new InputStreamReader(askUpdateStream, Charset.forName("UTF-8")));
                askUpdate = askUpdateReader.readLine();
                askUpdateReader.close();

                String localLastModified;
                InputStream lastModifiedStream = openFileInput(LAST_MODIFIED_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(lastModifiedStream, Charset.forName("UTF-8")));
                localLastModified = reader.readLine();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                reader.close();

                String localBuildDate;
                InputStream localBuildStream = openFileInput(LOCAL_BUILD_DATE);
                BufferedReader localBuildReader = new BufferedReader(new InputStreamReader(localBuildStream, Charset.forName("UTF-8")));
                localBuildDate = localBuildReader.readLine();
                localBuildReader.close();

                Date date = new Date();
                if (askUpdate.equals("TRUE")) {
                    dialog.show();
                } else {
                    if (Time.calculateHourDifference(formatter.format(date), localLastModified) > 20) {
                        String serverLastModified = FileHandler.readInspectionsDateModified().replace("T", " ").replace("-", "").substring(0, 17);
                        if (!localBuildDate.equals(serverLastModified)) {
                            dialog.show();
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        restaurantManager = RestaurantManager.getInstance();

        myRestaurants = restaurantManager.getRestaurants();
        populateListView();

        setUpBottomNavigation();
    }

    private void setupDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.message_layout);
        dialog.setCancelable(false);


        final Button button_cancel = dialog.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button_update = dialog.findViewById(R.id.button_update);
                if (button_update.getText().equals("")) {
                    csvUpdater.cancel(true);
                    File dir = getFilesDir();
                    if (checkFileExists(RESTAURANT_FILE1)) {
                        File createdRestaurantsFile = new File(dir, RESTAURANT_FILE1);
                        createdRestaurantsFile.delete();
                    }
                    if (checkFileExists(INSPECTIONS_FILE1)) {
                        File createdInspectionsFile = new File(dir, INSPECTIONS_FILE1);
                        createdInspectionsFile.delete();
                    }
                }


                dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });


        final Button button_update = dialog.findViewById(R.id.button_update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_update.getText().equals("Update")) {
                    csvUpdater = new CSVUpdater();
                    csvUpdater.execute();
                    button_update.setText("");
                    dialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                } else if (button_update.getText().equals("Done")) {
                    dialog.dismiss();

                }
            }
        });
    }

    private void onBackgroundTaskDataObtained(String results, String results2) {
        if (csvUpdater.isCancelled()) {
            return;
        }
        Button button = dialog.findViewById(R.id.button_update);
        button.setText("Done");
        dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        final Button button2 = dialog.findViewById(R.id.button_cancel);
        button2.setVisibility(View.INVISIBLE);

        File dir = getFilesDir();
        if (checkFileExists(RESTAURANT_FILE)) {
            File oldRestaurantsFile = new File(dir, RESTAURANT_FILE);
            oldRestaurantsFile.delete();
        }
        if (checkFileExists(INSPECTIONS_FILE)){
            File oldInspectionsFile = new File(dir, INSPECTIONS_FILE);
            oldInspectionsFile.delete();
        }
        File newRestaurantsFile = new File(dir, RESTAURANT_FILE1);
        File newInspectionsFile = new File(dir, INSPECTIONS_FILE1);

        newRestaurantsFile.renameTo(new File(dir,RESTAURANT_FILE));
        newInspectionsFile.renameTo(new File(dir,INSPECTIONS_FILE));

        try {
            FileInputStream restaurantFIS = openFileInput(RESTAURANT_FILE);
            InputStream inspectionsFIS = openFileInput(INSPECTIONS_FILE);
            CSVReader.readRestaurantData(restaurantFIS);
            CSVReader.readUpdatedInspectionReportData(inspectionsFIS, getResources().openRawResource(R.raw.brief_descriptions), getResources().openRawResource(R.raw.allviolations));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream  versionFile = openFileOutput(VERSION_FILE, MODE_PRIVATE);
            BufferedWriter versionFileWriter = new BufferedWriter( new OutputStreamWriter(versionFile, "UTF-8"));
            FileHandler.writeToInternalMemory(versionFileWriter, "on_updated_version");
            versionFileWriter.close();

            FileOutputStream lastModifiedStream = openFileOutput(LAST_MODIFIED_FILE, MODE_PRIVATE);
            BufferedWriter lastModifiedWriter = new BufferedWriter( new OutputStreamWriter(lastModifiedStream, "UTF-8"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            Date date = new Date();
            FileHandler.writeToInternalMemory(lastModifiedWriter, dateFormat.format(date));
            lastModifiedWriter.close();

            FileOutputStream localBuildStream = openFileOutput(LOCAL_BUILD_DATE, MODE_PRIVATE);
            BufferedWriter localBuildWriter = new BufferedWriter( new OutputStreamWriter(localBuildStream, "UTF-8"));
            String serverLastModified = FileHandler.readInspectionsDateModified().replace("T", " ").replace("-", "").substring(0, 17);
            FileHandler.writeToInternalMemory(localBuildWriter, serverLastModified);
            localBuildWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(ASK_FOR_UPDATE, MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(fos, "UTF-8"));
            FileHandler.writeToInternalMemory(writer, FALSE);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Restaurant restaurant : restaurantManager.getRestaurants()) {
            if (restaurant.hasInspections()) {
                restaurant.sortInspectionDates();
            }
        }
        populateListView();
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
                        Intent intent = GoogleMapActivity.makeIntent(MainActivity.this, myRestaurants);
                        startActivity(intent);
                        overridePendingTransition(0,0);
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

    private void clearListView() {

        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView listView = findViewById(R.id.restaurantListView);
        adapter.clear();
        adapter.notifyDataSetChanged();
        listView.setAdapter(null);
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!myRestaurants.get(position).hasInspections()){
                    FragmentManager manager = getSupportFragmentManager();
                    FirstScreenPopUpFragment dialog = new FirstScreenPopUpFragment();
                    dialog.show(manager, "Inspection without violations");
                }
                else {
                    Intent intent = SingleRestaurantInspection.makeIntent(MainActivity.this,
                            restaurantManager.getRestaurantByIndex(position));
                    startActivity(intent);
                }
            }
        });
    }

    public class CSVUpdater extends AsyncTask<Void, Void, Void> {
        String restaurantsData = "";
        String inspectionsData = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL restaurantURL = new URL(RESTAURANT_URL);
                URL inspectionsURL = new URL(INSPECTIONS_URL);


                HttpURLConnection restaurantsURLConnnection = (HttpURLConnection) restaurantURL.openConnection();
                InputStream restaurantsInputStream = restaurantsURLConnnection.getInputStream();
                BufferedReader restaurantsReader = new BufferedReader(new InputStreamReader(restaurantsInputStream));
                String restaurantLine;
                FileOutputStream restaurantsOutputStream = openFileOutput(RESTAURANT_FILE1, MODE_PRIVATE);
                BufferedWriter restaurantsBufferedWriter = new BufferedWriter( new OutputStreamWriter(restaurantsOutputStream, "UTF-8"));
                while ((restaurantLine = restaurantsReader.readLine()) != null) {
                    Log.d("Resturants: ", restaurantLine);  // Do not delete this
                    FileHandler.writeToInternalMemory(restaurantsBufferedWriter, restaurantLine);
                }
                restaurantsBufferedWriter.close();


                HttpURLConnection inspectionsURLConnection = (HttpURLConnection) inspectionsURL.openConnection();
                InputStream inspectionsInputStream = inspectionsURLConnection.getInputStream();
                BufferedReader inspectionsReader = new BufferedReader(new InputStreamReader(inspectionsInputStream));
                String inspectionsLine;
                FileOutputStream inspectionsOutputStream = openFileOutput(INSPECTIONS_FILE1, MODE_PRIVATE);
                BufferedWriter inspectionsBufferedWriter = new BufferedWriter( new OutputStreamWriter(inspectionsOutputStream, "UTF-8"));


                while ((inspectionsLine = inspectionsReader.readLine()) != null) {
                    Log.d("Inspections: ", inspectionsLine);    // Do not delete this
                    FileHandler.writeToInternalMemory(inspectionsBufferedWriter, inspectionsLine);
                }
                inspectionsBufferedWriter.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void setupDialog() {
            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.message_layout);
            dialog.setCancelable(false);


            final Button button_cancel = dialog.findViewById(R.id.button_cancel);
            button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button_update = dialog.findViewById(R.id.button_update);
                    if (button_update.getText().equals("")) {
                        csvUpdater.cancel(true);
                        File dir = getFilesDir();
                        if (checkFileExists(RESTAURANT_FILE1)) {
                            File createdRestaurantsFile = new File(dir, RESTAURANT_FILE1);
                            createdRestaurantsFile.delete();
                        }
                        if (checkFileExists(INSPECTIONS_FILE1)) {
                            File createdInspectionsFile = new File(dir, INSPECTIONS_FILE1);
                            createdInspectionsFile.delete();
                        }
                    }


                    dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }
            });


            final Button button_update = dialog.findViewById(R.id.button_update);
            button_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button_update.getText().equals("Update")) {
                        csvUpdater = new CSVUpdater();
                        csvUpdater.execute();
                        button_update.setText("");
                        dialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    } else if (button_update.getText().equals("Done")) {
                        dialog.dismiss();

                    }
                }
            });
        }

        private void onBackgroundTaskDataObtained(String results, String results2) {
            if (csvUpdater.isCancelled()) {
                return;
            }
            Button button = dialog.findViewById(R.id.button_update);
            button.setText("Done");
            dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            final Button button2 = dialog.findViewById(R.id.button_cancel);
            button2.setVisibility(View.INVISIBLE);

            File dir = getFilesDir();
            if (checkFileExists(RESTAURANT_FILE)) {
                File oldRestaurantsFile = new File(dir, RESTAURANT_FILE);
                oldRestaurantsFile.delete();
            }
            if (checkFileExists(INSPECTIONS_FILE)){
                File oldInspectionsFile = new File(dir, INSPECTIONS_FILE);
                oldInspectionsFile.delete();
            }
            File newRestaurantsFile = new File(dir, RESTAURANT_FILE1);
            File newInspectionsFile = new File(dir, INSPECTIONS_FILE1);

            newRestaurantsFile.renameTo(new File(dir,RESTAURANT_FILE));
            newInspectionsFile.renameTo(new File(dir,INSPECTIONS_FILE));

            try {
                FileInputStream restaurantFIS = openFileInput(RESTAURANT_FILE);
                InputStream inspectionsFIS = openFileInput(INSPECTIONS_FILE);
                CSVReader.readRestaurantData(restaurantFIS);
                CSVReader.readUpdatedInspectionReportData(inspectionsFIS, getResources().openRawResource(R.raw.brief_descriptions), getResources().openRawResource(R.raw.allviolations));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream  versionFile = openFileOutput(VERSION_FILE, MODE_PRIVATE);
                BufferedWriter versionFileWriter = new BufferedWriter( new OutputStreamWriter(versionFile, "UTF-8"));
                FileHandler.writeToInternalMemory(versionFileWriter, "on_updated_version");
                versionFileWriter.close();

                FileOutputStream lastModifiedStream = openFileOutput(LAST_MODIFIED_FILE, MODE_PRIVATE);
                BufferedWriter lastModifiedWriter = new BufferedWriter( new OutputStreamWriter(lastModifiedStream, "UTF-8"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                Date date = new Date();
                FileHandler.writeToInternalMemory(lastModifiedWriter, dateFormat.format(date));
                lastModifiedWriter.close();

                FileOutputStream localBuildStream = openFileOutput(LOCAL_BUILD_DATE, MODE_PRIVATE);
                BufferedWriter localBuildWriter = new BufferedWriter( new OutputStreamWriter(localBuildStream, "UTF-8"));
                String serverLastModified = FileHandler.readInspectionsDateModified().replace("T", " ").replace("-", "").substring(0, 17);
                FileHandler.writeToInternalMemory(localBuildWriter, serverLastModified);
                localBuildWriter.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = null;
            try {
                fos = openFileOutput(ASK_FOR_UPDATE, MODE_PRIVATE);
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(fos, "UTF-8"));
                FileHandler.writeToInternalMemory(writer, FALSE);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Restaurant restaurant : restaurantManager.getRestaurants()) {
                if (restaurant.hasInspections()) {
                    restaurant.sortInspectionDates();
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity.this.onBackgroundTaskDataObtained(restaurantsData, inspectionsData);
        }
    }
}
