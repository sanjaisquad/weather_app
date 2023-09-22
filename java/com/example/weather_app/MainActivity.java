package com.example.weather_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    //    Button btn_getId,btn_Location,btn_Weather;
//    EditText Location_weatherr;
//    ListView lv_weatherReport;
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIv, iconIv, searchIv;
    private RecyclerView weatherRv;
    private ArrayList<weatherRVmodel> weatherRVmodelArrayList;
    private WeatherRVadapter weatherRVadapter;
    private int PERMISSION_CODE = 404;
    private LocationManager locationManager;
    private String cityname;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("start..");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//variable declaration for ui......................................
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.textView);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVContant);
        cityEdt = findViewById(R.id.idEdtCity);
        backIv = findViewById(R.id.idIVBank);
        iconIv = findViewById(R.id.idIVIcon);
        searchIv = findViewById(R.id.idIVSearch);
        weatherRv = findViewById(R.id.idRvWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        weatherRVmodelArrayList = new ArrayList<>();
        weatherRVadapter = new WeatherRVadapter(this, weatherRVmodelArrayList);
        weatherRv.setAdapter(weatherRVadapter);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},200);
//
//        }
//
//        //getting the name...
//        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//        cityname=getCityName(location.getLongitude(),location.getLatitude());
//        cityname="chennai";
//        getWeatherInfo(cityname);

        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city= cityEdt.getText().toString();

                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter city name",Toast.LENGTH_SHORT).show();
                }
                else {
                    getWeatherInfo(city);
                }
            }
        });



    }

    public void onRequestPermissionResult(int requestCode, @NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"please provide the permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //method to find city name..............................................(latitude,longitude)

    private String getCityName(double longitude,double latitude){
        String cityname="not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> address =gcd.getFromLocation(latitude,longitude,10);
            for(Address adr : address){
                if(adr!=null){
                    String city=adr.getLocality();
                    if(city!=null && city.equals("")){
                        cityname= city;
                    }
                    else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"user city not Found...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return cityname;
    }

    private void getWeatherInfo(String cityName){
        //the key should be stored in .env file.....(https://api.weatherapi.com/v1/current.json?key=a81a957372894471af1163757231009&q=London&aqi=yes)
        String url2="https://api.weatherapi.com/v1/current.json?key=a81a957372894471af1163757231009&q="+cityName+"&aqi=yes";
        String url="https://api.weatherapi.com/v1/forecast.json?key=a81a957372894471af1163757231009&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                loadingPB.setVisibility(View.GONE);
//                homeRL.setVisibility(View.VISIBLE);
                weatherRVmodelArrayList.clear();
                //getting temperature ......
                try {
                    String temperature =  response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature);

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition= response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon= response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIv);
                    conditionTV.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://e1.pxfuel.com/desktop-wallpaper/764/593/desktop-wallpaper-minimal-minimalist-nature-sunrise-river-minimal-mobile.jpg").into(backIv);
                    }
                    else {Picasso.get().load("https://e0.pxfuel.com/wallpapers/5/483/desktop-wallpaper-mountains-night-clouds-vertex-dark-tops.jpg").into(backIv);}

                    JSONObject forecastobject = response.getJSONObject("forecast");
                    JSONObject forcastO = forecastobject.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray horArray = forcastO.getJSONArray("hour");

                    for(int i=0;i<horArray.length();i++){
                        JSONObject hourObj = horArray.getJSONObject(i);
                        String time   = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img    = hourObj.getJSONObject("condition").getString("icon");
                        String wind   = hourObj.getString("wind_kph");
                        weatherRVmodelArrayList.add(new weatherRVmodel(time,temper,img,wind));
                    }
                  weatherRVadapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"please enter valid city  name!!!",Toast.LENGTH_SHORT).show();
            }

        }
        );

        requestQueue.add(jsonObjectRequest);

    }
}