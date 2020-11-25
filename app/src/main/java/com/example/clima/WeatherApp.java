package com.example.clima;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WeatherApp extends AppCompatActivity {

    static final int REQUEST_PERMISSION = 1;
    LocationManager locationManager;
    RequestQueue requestQueue;

    TextView _city, _description, _country, _temp, _windspeed, _sunrisetxt, _sunsettxt, _name, _greeting, _logouttxt;
    EditText _searchTxt;
    String _lon, _lat;
    ImageView _icon, _night, _day, _logout, _searchBtn;
    View _mainBg;
    LinearLayout _wind, _sunrise, _sunset;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_app);

        _city = (TextView) findViewById(R.id.cityTxt);
        _country = (TextView) findViewById(R.id.countryTxt);
        _description = (TextView) findViewById(R.id.descriptionTxt);
        _windspeed = (TextView) findViewById(R.id.windspeed);
        _sunsettxt = (TextView) findViewById(R.id.sunsettxt);
        _sunrisetxt = (TextView) findViewById(R.id.sunrisetxt);
        _temp = (TextView) findViewById(R.id.tempTxt);
        _logouttxt = (TextView) findViewById(R.id.logouttxt);
        _icon = (ImageView) findViewById(R.id.iconImg);
        _night = (ImageView) findViewById(R.id.nightbg);
        _day = (ImageView) findViewById(R.id.daybg);
        _mainBg = (View) findViewById(R.id.mainbg);
        _wind = (LinearLayout) findViewById(R.id.wind);
        _sunrise = (LinearLayout) findViewById(R.id.sunrise);
        _sunset = (LinearLayout) findViewById(R.id.sunset);
        _logout = (ImageView) findViewById(R.id.logout);
        _name = findViewById(R.id.Name);
        _greeting = findViewById(R.id.Greeting);
        _searchTxt = (EditText) findViewById(R.id.searchTxt);
        _searchBtn = (ImageView) findViewById(R.id.searchBtn);


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            _name.setText(signInAccount.getDisplayName());
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        _searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = _searchTxt.getText().toString();
                Log.d("BUTTON CLICKED---------------------", String.valueOf(txt));
                GetWeather("null", "null", txt);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });


        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.logout:
                        signOutGoogle();
                        break;
                }
            }
        });
_logouttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.logout:
                        signOutGoogle();
                        break;
                }
            }
        });

        Window window = WeatherApp.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor( WeatherApp.this,R.color.statusbar));



        requestQueue = Volley.newRequestQueue(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        animatefadeIntop(_wind,500);
        animatefadeIntop(_sunrise,1000);
        animatefadeIntop(_sunset,1500);

        changebg();
        _getLocation();


    }

    private void signOutGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("---------------------------------signOut", "onComplete: ");
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    public void _getLocation() {
        Log.d("LOCATION", "_getLocation: CALLED");
        if (ActivityCompat.checkSelfPermission
                (WeatherApp.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (WeatherApp.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MAIN", "_getLocation: SUCESSSSSSSSSSSS");
            ActivityCompat.requestPermissions(WeatherApp.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION);
            Log.d("MAIN", "---------------------------");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location!=null)
        {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            _lon = String.valueOf(longitude);
            _lat = String.valueOf(latitude);
            Log.d("Method", String.valueOf(_lon));
            Log.d("Method", String.valueOf(_lat));

        }
        else {
            Toast.makeText(WeatherApp.this, "No Location. Enable GPS!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case (REQUEST_PERMISSION):{
                _getLocation();
                break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Resume", "In Resume ");
        try {

//        String lati = _lat.toString();
//        String longi = _lon.toString();

            GetWeather(_lat,_lon,"null");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Waiting for GPS Data. Try Search City", Toast.LENGTH_SHORT).show();
        }




    }

    public void animatefadeIntop(LinearLayout card, Integer value)
    {
        Animation animFadeInTop = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_top);
        animFadeInTop.setStartOffset(value);
        card.startAnimation(animFadeInTop);
    }

    public void animatefadeInTxt(TextView txt)
    {
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        txt.startAnimation(animFadeIn);
    }
    public void GetWeather(String lati, String longi, String txt)
    {
        Log.d("GET WEATHER TXT RECEVIED-----------------", String.valueOf(txt));
        String URL = null;
        if(lati.equals("null") && longi.equals("null") && txt.equals("null"))
        {
            Toast.makeText(this, "Waiting for GPS Data. Try Search City", Toast.LENGTH_SHORT).show();
        }
        else if(lati.equals("null") && longi.equals("null"))
        {
            URL = "https://api.openweathermap.org/data/2.5/weather?q="+txt+"&units=metric&appid=0a43a3ebfefe8ef672268c156b88cf64";
        }
        else{
            URL = "https://api.openweathermap.org/data/2.5/weather?lat="+lati+"&lon="+longi+"&units=metric&appid=0a43a3ebfefe8ef672268c156b88cf64";
        }
        Log.d("GET WEATHER", "GetWeather: Called");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String city;
                String citynew;
                JSONObject sys;
                JSONObject wind;
                JSONObject main;
                String icon;
                String description;

                try {
                    city = response.getString("name");
                    sys = response.getJSONObject("sys");
                    wind = response.getJSONObject("wind");
                    main = response.getJSONObject("main");

                    String country = sys.getString("country");
                    Long sunrise = sys.getLong("sunrise");
                    Long sunset = sys.getLong("sunset");
                    String windspeed = wind.getString("speed");
                    Integer temp = main.getInt("temp");

                    if (city.equals("Tammannekulama")) {
                        citynew = "Wijayapura";
                        _city.setText(citynew);
                        animatefadeInTxt(_city);
                    }else {
                        animatefadeInTxt(_city);
                        _city.setText(city);}

                    if (country.equals("LK")) {
                        country = "Sri Lanka";
                    }

                    _sunrisetxt.setText(getDateCurrentTimeZone(sunrise));
                    _sunsettxt.setText(getDateCurrentTimeZone(sunset));
                    _windspeed.setText(windspeed+" m/s");
                    _country.setText(country);
                    animatefadeInTxt(_country);
                    _temp.setText(temp.toString()+"°C");
                    animatefadeInTxt(_temp);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONArray jsonArray = response.getJSONArray("weather");

                    for (int i =0; i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        icon = jsonObject.getString("icon");
                        description = jsonObject.getString("description");

                        changeIcon(icon);

                        description = String.valueOf(description.charAt(0)).toUpperCase() + description.subSequence(1, description.length());
                        _description.setText(description);
                        animatefadeInTxt(_description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                Log.d("RESPONSE", String.valueOf(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("RESPONSE", String.valueOf(error));
                Toast.makeText(WeatherApp.this, "Enter A Valid City Name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("GMT");
            Log.d("TIEM ZONE", String.valueOf(tz));
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("K:mm a");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public void changebg(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 0);

        long day_start = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 00);

        long day_end = cal.getTimeInMillis();
        long now = System.currentTimeMillis();

        if(now > day_start && now < day_end)
        {
            //set your background here
//            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
//            Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
//            _mainBg.startAnimation(animFadeOut);
//            _mainBg.setBackground(getDrawable(R.drawable.day));
//            _mainBg.startAnimation(animFadeIn);
            _mainBg.setBackground(getDrawable(R.drawable.night));
            _day.setVisibility(View.VISIBLE);
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_real);
            _day.startAnimation(animFadeIn);
            _greeting.setText("Good Day,");



        }
        else
        {
            //set your background here
           _mainBg.setBackground(getDrawable(R.drawable.day));
            _night.setVisibility(View.VISIBLE);
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_real);
            _night.startAnimation(animFadeIn);
            _greeting.setText("Good Night,");

        }
    }

    public void animatefadeIn(ImageView icon)
    {
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        icon.startAnimation(animFadeIn);
    }



    public void changeIcon(String icon)
    {
        switch (icon){
            case "01d":{
                //clear sky
                _icon.setImageResource(R.drawable.oned);
                animatefadeIn(_icon);
                break;
            }
            case "02d":
            case "02n":{
                //few clouds
                _icon.setImageResource(R.drawable.twod);
                animatefadeIn(_icon);
                break;
            }
            case "03d":
            case "03n":{
                //scattered clouds
                _icon.setImageResource(R.drawable.three);
                animatefadeIn(_icon);
                break;
            }case "04d":
            case "04n":{
                //broken clouds
                _icon.setImageResource(R.drawable.fourd);
                animatefadeIn(_icon);
                break;
            }case "09d":
            case "09n":{
                //	shower rain
                _icon.setImageResource(R.drawable.nined);
                animatefadeIn(_icon);
                break;
            }case "10d":{
                //	rain
                _icon.setImageResource(R.drawable.tend);
                animatefadeIn(_icon);
                break;
            }case "11d":{
                //thunderstorm
                _icon.setImageResource(R.drawable.elevend);
                animatefadeIn(_icon);
                break;
            }case "13d":{
                //	snow
                _icon.setImageResource(R.drawable.thirteend);
                animatefadeIn(_icon);
                break;
            }case "50d":{
                //	mist
                _icon.setImageResource(R.drawable.fiftyd);
                animatefadeIn(_icon);
                break;
            }case "01n":{
                //clear sky night
                _icon.setImageResource(R.drawable.onen);
                animatefadeIn(_icon);
                break;
            }
            case "10n":{
                //	rain night
                _icon.setImageResource(R.drawable.tenn);
                animatefadeIn(_icon);
                break;
            }case "11n":{
                //thunderstorm night
                _icon.setImageResource(R.drawable.eleven);
                animatefadeIn(_icon);
                break;
            }case "13n":{
                //	snow night
                _icon.setImageResource(R.drawable.thirteenn);
                animatefadeIn(_icon);
                break;
        }

    }
    }
}