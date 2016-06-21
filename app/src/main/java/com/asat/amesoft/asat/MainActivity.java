package com.asat.amesoft.asat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.asat.amesoft.asat.Tools.Tools;
import com.asat.amesoft.asat.Tools.VolleySingleton;
import com.asat.amesoft.asat.fragments.AboutFragment;
import com.asat.amesoft.asat.fragments.AdvicesFragment;
import com.asat.amesoft.asat.fragments.HospitalFragment;
import com.asat.amesoft.asat.fragments.HospitalRulesFragment;
import com.asat.amesoft.asat.fragments.MenuFragment;
import com.asat.amesoft.asat.fragments.NotificationsFragment;
import com.asat.amesoft.asat.fragments.RecordFragment;
import com.asat.amesoft.asat.fragments.SettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity{


    private static final int MY_PERMISSIONS_EXTERNAL_STORAGE = 1 ;
    Toolbar toolbar;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.menu_title);
        setSupportActionBar(toolbar);


        sharedPref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String language = sharedPref.getString("language", "en");
        Tools.changeLanguage(language, this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

//             Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            configFilesDirectory();
        }



        if(sharedPref.contains("token")){
            Tools.setToken(sharedPref.getString("token","null"));
            Tools.setName(sharedPref.getString("name","null"));
            Tools.setLastName(sharedPref.getString("lastName","null"));
            keepSession(Tools.getToken(), Tools.keep);
        }
        else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }



        if(savedInstanceState==null) {
            change_content(new MenuFragment(), false);
        }

    }

    private void configFilesDirectory() {
        File asatRoot = Environment.getExternalStorageDirectory();
        asatRoot = new File(asatRoot.getPath() + "/ASAT");
        File advi = new File(asatRoot.getPath() + "/ADVICES");
        File rec = new File(asatRoot.getPath() + "/RECORD");
        File img = new File(asatRoot.getPath() + "/HOSPITAL");

            if (!asatRoot.exists()) {
                asatRoot.mkdirs();
                if (!advi.exists())
                    advi.mkdirs();
                if (!rec.exists())
                    rec.mkdirs();
            }

            Tools.setAsatRoot(asatRoot.getPath());

            File hos_logo = new File(Tools.getAsatRoot() + "hos_logo");
            if (!hos_logo.exists()) {
                getHospitalLogo(Tools.getToken(), Tools.hospital);
            }

    }

    public void goHospital(View view){
       // Log.v("STATIC TOKEN",token);

        Fragment f = new HospitalFragment();
        change_content(f,true);
        toolbar.setTitle(R.string.hospital_title);
        keepSession(Tools.getToken(),Tools.keep);
    }

    public void goRules(View view){
        Fragment f = new HospitalRulesFragment();
        change_content(f,true);

    }

    public void goRecord(View view){
        Fragment f = new RecordFragment();
        change_content(f,true);
        toolbar.setTitle(R.string.record_title);
        keepSession(Tools.getToken(),Tools.keep);
    }

    public void goAdvices(View view){
        Fragment f = new AdvicesFragment();
        change_content(f,true);
        toolbar.setTitle(R.string.advices_title);
        keepSession(Tools.getToken(),Tools.keep);
    }

    public void goSettings(View view){
        Fragment f = new SettingsFragment();
        change_content(f,true);
        toolbar.setTitle(R.string.settings_title);
        keepSession(Tools.getToken(),Tools.keep);
    }

    public void goAbout(View view){
        Fragment f = new AboutFragment();
        change_content(f,true);
        toolbar.setTitle(R.string.about_title);
        keepSession(Tools.getToken(),Tools.keep);
    }

    public void goNotifications(View view){
        Fragment f = new NotificationsFragment();
        change_content(f,true);
        toolbar.setTitle(R.string.notifications_title);
        keepSession(Tools.getToken(),Tools.keep);
    }


    private void checkSession(String response) {

        JSONObject jsonObject;
        String result;

        try {
            jsonObject = new JSONObject(response);
            result = jsonObject.getJSONObject("response").get("result").toString();
            //Si el resultado de la consulta esta bien
            if(result.equals("ERROR")){
                Intent intent = new Intent(this, LoginActivity.class);
                //intent.putExtra("token",token);
                startActivity(intent);
            }
            else{

            }
        } catch (JSONException e) {

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case android.R.id.home:
                change_content(new MenuFragment(),true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void change_content(Fragment f, boolean back){
        FragmentTransaction ft;

        if(f.getClass().equals(MenuFragment.class)){
            toolbar.setTitle(R.string.menu_title);
           // toolbar.setNavigationIcon(null);
        }
        else{
            toolbar.setNavigationIcon(+R.drawable.ic_menu_black_24dp);
        }

        ft=getSupportFragmentManager().beginTransaction().replace(R.id.content_main,f);
        if(back){
            ft.addToBackStack(null).commit();
        }
        else{
         ft.commit(); //
        }
    }


    private void keepSession(final String token_id, String uri){
        //Volley connection
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Log.v("Test session",response);
                        checkSession(response);
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("response","Errors  happens");
                    }
                }
        )
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("token_id",token_id);
                return params;
            }
        };
        queue.add(stringRequest);

    }


    private void getHospitalLogo(final String token_id,String uri){
        //Volley connection
        //Log.v("GET HOSPITAL LOGO0","Deberia estar durmiendo0");
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                         Log.v("GET HOSPITAL LOGO",response);
                        //Log.v("HosRes",response.substring(response.length()/2+2200,response.length()));
                        processHospitalLogo(response);

                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("response","Errors  happens");
                    }
                }
        )
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("token_id",token_id);
                return params;
            }
        };
        queue.add(stringRequest);

    }

    private void processHospitalLogo(String response) {
        Log.v("GET HOSPITAL LOGO2","Deberia estar durmiendo2");
        JSONObject jsonObject;
        String result;


        String center_logo;
        try {
            jsonObject = new JSONObject(response);
            result = jsonObject.getJSONObject("response").get("result").toString();
            //Si el resultado de la consulta esta bien
            Log.v("hos response",jsonObject.names().toString());
            if(result.equals("OK")){
                center_logo=jsonObject.getString("center_logo");
                    Tools.saveFile(center_logo,"hos_logo.png",Tools.getAsatRoot());


            }
            else{
                //process error
            }
        } catch (JSONException e) {

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSIONS_EXTERNAL_STORAGE:
                Tools.permission=true;
                configFilesDirectory();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


}
