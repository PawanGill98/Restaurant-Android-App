package me.cmpt276.restaurantinspector.GoogleMaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.cmpt276.restaurantinspector.Model.CSVReader;
import me.cmpt276.restaurantinspector.Model.FileHandler;
import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;
import me.cmpt276.restaurantinspector.Model.Time;
import me.cmpt276.restaurantinspector.R;
import me.cmpt276.restaurantinspector.UI.MainActivity;
import me.cmpt276.restaurantinspector.UI.SingleRestaurantInspection;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static Intent makeIntent(Context c){
        return new Intent(c, GoogleMapActivity.class);
    }

    private ClusterManager<MyItem> mClusterManager;
    private ArrayList<MyItem> mMarkerArray = new ArrayList<>();

    private RestaurantManager restaurantManager;
    private List<Restaurant> restaurants;
    private static final String TAG = "GoogleMap";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

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
    public static final int GOOGLE_MAPS_ACTIVITY_CALL_NUMBER = 1002;

    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    Dialog dialog;
    CSVUpdater csvUpdater;

    public boolean checkFileExists(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
    private double[] callingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        Bundle extras = getIntent().getExtras();
        setupDialog();
        String fetch = "";
        if (extras != null) {
            fetch = extras.getString("fetch_data");
        }

        if (fetch != null && fetch.equals("no_fetch")) {
            // Don't fetch data
        } else {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

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
                if (isNetworkAvailable()) {
                    dialog.show();
                }

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
            }
        }
        if (fetch != null && fetch.equals("no_fetch")) {
            // Don't fetch data
        } else {
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
                if (askUpdate.equals(TRUE)) {
                    if (isNetworkAvailable()) {
                        dialog.show();
                    }
                } else {
                    if (Time.calculateHourDifference(formatter.format(date), localLastModified) > 20) {
                        String serverLastModified = FileHandler.readInspectionsDateModified().replace("T", " ").replace("-", "").substring(0, 17);
                        if (!localBuildDate.equals(serverLastModified)) {
                            if (isNetworkAvailable()) {
                                dialog.show();
                            }
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

        restaurants = restaurantManager.getRestaurants();
        setUpBottomNavigation();
        setUpMapFragmentSupport();

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

        setUpBottomNavigation();
        setUpMapFragmentSupport();
    }

    private void setupDialog() {
        dialog = new Dialog(GoogleMapActivity.this);
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
                if (button_update.getText().equals("Done")) {
                    dialog.dismiss();
                }
                else {
                    csvUpdater = new CSVUpdater();
                    csvUpdater.execute();
                    button_update.setText("");
                    dialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setUpMapFragmentSupport(){
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpBottomNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.map);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.restaurant_list:
                        overridePendingTransition(0,0);
                        finish();
                        Intent myIntent = new Intent(GoogleMapActivity.this, MainActivity.class);
                        GoogleMapActivity.this.startActivity(myIntent);
                        return true;
                    case R.id.map:
                        return true;
                }
                return false;
            }
        });
    }

    private void setAllRestaurantsLocations(){
        for(int i = 0, j = 1, k = 2, m = 3, n = 4; n < restaurants.size();
            i += 5, j += 5, k += 5, m += 5,n += 5){
            setUpInfoWindow(restaurants.get(i));
            setUpInfoWindow(restaurants.get(j));
            setUpInfoWindow(restaurants.get(k));
            setUpInfoWindow(restaurants.get(m));
            setUpInfoWindow(restaurants.get(n));
        }
        setUpClusters();
    }

    private void setUpInfoWindow(Restaurant restaurant){
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(GoogleMapActivity.this));
        final Location targetLocation = new Location("");
        targetLocation.setLatitude(restaurant.getLatitude());
        targetLocation.setLongitude(restaurant.getLongitude());
        LatLng latLng = new LatLng(targetLocation.getLatitude(), targetLocation.getLongitude());
        float color = BitmapDescriptorFactory.HUE_BLUE;
        if(restaurant.hasInspections()) {
            color = setMarkerColor(color, restaurant.getInspections().get(0));
        }
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.restaurant_name_on_map, restaurant.getName()))
                .snippet(getString(R.string.snippet, setSnippet(restaurant)))
                .icon(BitmapDescriptorFactory.defaultMarker(color));
         mMarkerArray.add(new MyItem(options.getPosition(),options.getTitle(),options.getSnippet(),options.getIcon()));
    }

    private float setMarkerColor(float color, Inspection inspection){
        switch (inspection.getHazardRating()) {
            case "Low":
                color = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "Moderate":
                color = BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case "High":
                color = BitmapDescriptorFactory.HUE_RED;
                break;
        }
        return color;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mLocationPermissionGranted){
            getDeviceLocation();

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            setAllRestaurantsLocations();
//                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                @Override
//                                public boolean onMarkerClick(Marker marker) {
//                                    //setUpInfoWindowClickable();///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                    return false;
//                                }
//                            });
                            callingActivity = getIntent().getDoubleArrayExtra("latitude/longitude");
                            moveCamera(new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()), DEFAULT_ZOOM);
                            if(callingActivity != null) {
                                Log.d(TAG, "from other activity latitude: " + callingActivity[0]
                                        + " longitude: " + callingActivity[1]);
                                moveCam(new LatLng(callingActivity[0], callingActivity[1]), DEFAULT_ZOOM);
                                for(int i = 0; i < restaurants.size(); i++){
                                    if(callingActivity[0] == restaurants.get(i).getLatitude()
                                            && callingActivity[1] == restaurants.get(i).getLongitude()){
                                        Log.d(TAG, "from INSIDE");
                                        setUpInfoWindow(restaurants.get(i));
                                    }
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(GoogleMapActivity.this,
                                    "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

//    private void setUpInfoWindowClickable(){
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                for(int i = 0; i < restaurants.size(); i++){
//                    if(restaurants.get(i).getName().equals(marker.getTitle())){
//                        Intent intent = SingleRestaurantInspection.makeIntent(GoogleMapActivity.this,
//                                restaurants.get(i));
//                        intent.putExtra("calling_activity", GOOGLE_MAPS_ACTIVITY_CALL_NUMBER);
//                        startActivity(intent);
//                    }
//                }
//            }
//        });
//    }

    private String setSnippet(Restaurant restaurant){
        String snippet;
        if(!restaurant.getInspections().isEmpty()) {
            snippet = "Address: " + restaurant.getAddress() + "\n" +
                    "Hazard level: " + restaurant.getInspections().get(0).getHazardRating();
        }else{
            snippet = "Address: " + restaurant.getAddress() + "\n" +
                    "No inspections happened in this restaurant";
        }
        return snippet;
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude
                + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng)
                .title(getString(R.string.my_location_title))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mMap.addMarker(marker);
    }

    private void moveCam(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(GoogleMapActivity.this);
    }

    private void getLocationPermission(){
        String[] permissions =  {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if((ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    // Referenced from: https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                    Log.d("Resturants: ", restaurantLine);  // Do not delete this, thread yield.
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
                    Log.d("Inspections: ", inspectionsLine);    // Do not delete this, thread yield.
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GoogleMapActivity.this.onBackgroundTaskDataObtained(restaurantsData, inspectionsData);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setUpClusters(){
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));
        mClusterManager.setAnimation(false);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        addItems();
    }

    private void addItems(){
        mClusterManager.addItems(mMarkerArray);
        mClusterManager.cluster();

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem item) {
                    for(int i = 0; i < restaurants.size(); i++){
                        if(restaurants.get(i).getName().equals(item.getTitle())){
                            Intent intent = SingleRestaurantInspection.makeIntent(GoogleMapActivity.this,
                                    restaurants.get(i));
                            intent.putExtra("calling_activity", GOOGLE_MAPS_ACTIVITY_CALL_NUMBER);
                            startActivity(intent);
                        }
                    }
            }
        });
    }
}
