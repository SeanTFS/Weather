package com.schen.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //Declaration des champs
    TextView mDate, mCity, mTemp, mDescription, mCloud, mWind;
    private RequestQueue requestQueue;
    ImageView imgIcon;
    String maVille="Toronto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue= Volley.newRequestQueue(this);

        mDate = findViewById(R.id.mDate);
        mCity = findViewById(R.id.mCity);
        mTemp = findViewById(R.id.mTemp);
        mDescription = findViewById(R.id.mDescription);
        mCloud = findViewById(R.id.mCloud);
        mWind = findViewById(R.id.mWind);
        afficher();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recherche,menu);
        MenuItem menuItem= menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Ecrire le nom de la ville");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                maVille=query;
                afficher();
                //gestion du clavier
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null){
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void afficher(){
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+maVille+"&units=metric&appid=9b78452aaff49e04b6b1c6a22ce6fd92";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject objectCloud = response.getJSONObject("clouds");
                    JSONObject objectWind = response.getJSONObject("wind");
                    //Log.d("Tag","resultat = "+array.toString());
                    //Log.d("Tag","resultat = "+main_object.toString());
                    JSONObject object = array.getJSONObject(0);
                    //temperature
                    int tempC=(int)Math.round(main_object.getDouble("temp"));
                    String temp = String.valueOf(tempC);

                    String description = object.getString("description");
                    String city = response.getString("name");
                    String icon = object.getString("icon");

                    int tempCloud=(int)Math.round(objectCloud.getDouble("all"));
                    int tempWind=(int)Math.round(objectWind.getDouble("speed"));
                    //mettre les valeurs
                    mCloud.setText("Clouds: "+tempCloud);
                    mWind.setText("Wind speed: "+tempWind);
                    mCity.setText(city);
                    mTemp.setText(temp);
                    mDescription.setText(description);
                    //formattage du temps
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM dd");
                    String formatted_date = simpleDateFormat.format(calendar.getTime());

                    mDate.setText(formatted_date);
                    // gestion de l'image
                    String imageUri="http://openweathermap.org/img/w/"+icon+".png";
                    imgIcon = findViewById(R.id.imgIcon);
                    Uri myUri=Uri.parse(imageUri);
                    Picasso.with(MainActivity.this).load(myUri).resize(200,200).into(imgIcon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}