package com.asat.amesoft.asat.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.asat.amesoft.asat.Models.Adapters.Notifications_IA;
import com.asat.amesoft.asat.Models.Notifications_Item;
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
public class NotificationsFragment extends Fragment {

    private ListView listView;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        connect(Tools.getToken(), Tools.notifications);
        listView = (ListView) view.findViewById(R.id.notifications_list);
        return view;
    }

    private void connect(final String token_id,String uri){
        //Volley connection
        RequestQueue queue = VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                        Log.v("Notifications Res",response);
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

        try {
            jsonObject = new JSONObject(response);
            result = jsonObject.getJSONObject("response").get("result").toString();
            //Si el resultado de la consulta esta bien
            if(result.equals("OK")){
                JSONArray notifications = jsonObject.getJSONArray("lst_items");


                final ArrayList<Notifications_Item> lista= new ArrayList<>();

                Log.v("Lista",notifications.toString());
                for(int i=0; i<notifications.length();i++){
                    JSONObject item = notifications.getJSONObject(i).getJSONObject("item");
                    Log.v("ITEM ",item.names().toString());
                    lista.add(
                            new Notifications_Item(
                                    item.getString("item_id"),
                                    item.getString("item_date"),
                                    item.getString("item_title"),
                                    item.getString("item_text"),
                                    item.getString("item_url"),
                                    decodeImage(item.getString("item_img"))
                            )
                    );

                }
                ArrayAdapter<Notifications_Item> adapter;
                if(getActivity()!=null) {
                    adapter = new Notifications_IA(getActivity(), R.layout.row_advices, lista);
                    listView.setAdapter(adapter);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url_text = lista.get(position).getUrl();
                        if(!url_text.isEmpty() && !url_text.equals("null")){
                            Log.v("URL text", url_text);
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url_text));
                            startActivity(i);
                        }
                    }
                });

            }
            else{
                Snackbar.make(listView, jsonObject.getJSONObject("response").get("msg").toString(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Log.v("Expired token",Tools.getToken());
            }
        } catch (JSONException e) {

        }
    }

    private Bitmap decodeImage(String encoded){
        byte[] decodedImage = Base64.decode(encoded, Base64.CRLF);
        return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
    }

}
