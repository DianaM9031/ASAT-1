package com.asat.amesoft.asat.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.asat.amesoft.asat.Models.Adapters.Hospital_IA;
import com.asat.amesoft.asat.Models.Hospital_Item;
import com.asat.amesoft.asat.R;
import com.asat.amesoft.asat.Tools.Tools;
import com.asat.amesoft.asat.Tools.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HospitalFragment extends Fragment {

    String center_title="";
    String center_id="";
    TextView title;
    TextView description;
    Button rules, images;

    ImageView icon;
    ListView listView;

    public HospitalFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hospital, container, false);
        title = (TextView) view.findViewById(R.id.hospital_title);
        description = (TextView) view.findViewById(R.id.hospital_description);
        rules = (Button) view.findViewById(R.id.hospital_rules);
        images = (Button) view.findViewById(R.id.hospital_images);

        icon = (ImageView) view.findViewById(R.id.hospital_icon);
        listView = (ListView) view.findViewById(R.id.hospital_listView);
        connect(Tools.getToken(),Tools.hospital);

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new HospitalImagesFragment();

                Bundle args = new Bundle();
                args.putString("title",center_title);
                args.putString("hospital",center_id);

                f.setArguments(args);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main,f).addToBackStack(null)
                        .commit();
            }
        });
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new HospitalRulesFragment();

                Bundle args = new Bundle();
                args.putString("title",center_title);
                args.putString("hospital",center_id);

                f.setArguments(args);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main,f).addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void connect(final String token_id,String uri){
        //Volley connection
        RequestQueue queue = VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                        // Log.v("HosRes",response.substring(0,response.length()/2));
                        //Log.v("HosRes",response.substring(response.length()/2+2200,response.length()));
                        processResponse(response);

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


    private void processResponse(String response) {

        JSONObject jsonObject;
        String result;


        String center_logo;
        try {
            jsonObject = new JSONObject(response);
            result = jsonObject.getJSONObject("response").get("result").toString();
            //Si el resultado de la consulta esta bien
            if(result.equals("OK")){
                center_id=jsonObject.getString("center_id");
                center_logo=jsonObject.getString("center_logo");
                center_title=jsonObject.getString("center_title");

                title.setText(center_title);

                description.setText(Html.fromHtml(jsonObject.getString("center_text")));
                if(jsonObject.getBoolean("center_hasRules")){
                    rules.setVisibility(View.VISIBLE);
                }
                else{
                    rules.setVisibility(View.GONE);
                }
                if(jsonObject.getBoolean("center_hasImages")){
                    images.setVisibility(View.VISIBLE);
                }
                else{
                    images.setVisibility(View.GONE);
                }
                //se cambia el logo del hospital luego de decodificar la imagen
                icon.setImageBitmap(decodeImage(center_logo));

                ArrayList<Hospital_Item> lista = new ArrayList<>();
                JSONArray lst_contact = jsonObject.getJSONArray("lst_contact");
                for(int i=0; i<lst_contact.length();i++){
                    JSONObject item = lst_contact.getJSONObject(i).getJSONObject("item");
                    lista.add(new Hospital_Item(
                            item.getString("item_text"),item.getString("item_icon")
                    ));
                }
                ArrayAdapter<Hospital_Item> adapter;
                if(getActivity()!=null) {
                    adapter = new Hospital_IA(getActivity(), lista);
                    listView.setAdapter(adapter);
                }
            }
            else{
                Snackbar.make(listView, jsonObject.getJSONObject("response").get("msg").toString(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        } catch (JSONException e) {

        }
    }


    private Bitmap decodeImage(String encoded){
        Log.v("Image64",encoded);
            byte[] decodedImage = Base64.decode(encoded, Base64.CRLF);
            return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }
}
