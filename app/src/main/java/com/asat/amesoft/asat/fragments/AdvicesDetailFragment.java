package com.asat.amesoft.asat.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.asat.amesoft.asat.MyApplication;
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
public class AdvicesDetailFragment extends Fragment {

    private String id,sTitle;
    private TextView title;
    private TextView text;
    private TextView url;
    public AdvicesDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id=getArguments().getString("id_advice");
        sTitle=getArguments().getString("title");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_advices_detail, container, false);
        title = (TextView) view.findViewById(R.id.advices_detail_title);
        text = (TextView) view.findViewById(R.id.advices_detail_text);
        url = (TextView) view.findViewById(R.id.advices_detail_url);

        title.setText(sTitle);

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_text = url.getText().toString();
                Log.v("URL text", url_text);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url_text));

                startActivity(i);
            }
        });

        connect(MyApplication.getToken(), Tools.advicesDetail);
        return view;
    }

    private void connect(final String token_id,String uri){
        //Volley connection
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                        Log.v("AdvicesDetail Res",response);
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
                params.put("advice_id",id);
                return params;
            }
        };
        queue.add(stringRequest);

    }

    private void processResponse(String response) {

        JSONObject jsonObject;
        String result;

        try {
            jsonObject = new JSONObject(response);
            result = jsonObject.getJSONObject("response").get("result").toString();
            //Si el resultado de la consulta esta bien
            if(result.equals("OK")){

                text.setText(Html.fromHtml(jsonObject.getString("advice_text")));
                if(!jsonObject.getString("advice_url").equals("null")) {
                    url.setText(jsonObject.getString("advice_url"));
                    url.setVisibility(View.VISIBLE);
                }
                else{
                    url.setVisibility(View.GONE);
                }
            }
            else{

            }
        } catch (JSONException e) {

        }
    }


}
