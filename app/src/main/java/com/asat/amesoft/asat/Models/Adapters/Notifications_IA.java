package com.asat.amesoft.asat.Models.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asat.amesoft.asat.Models.Notifications_Item;
import com.asat.amesoft.asat.R;

import java.util.ArrayList;

/**
 * Created by Jorge on 20/06/2016.
 */
public class Notifications_IA extends ArrayAdapter<Notifications_Item> {


    public Notifications_IA(Context context, int resource, ArrayList<Notifications_Item> objects) {
        super(context, resource, objects);
    }

    class ViewHolder{
        TextView date;
        TextView text;
        ImageView icon;

        public ViewHolder(View view) {
            date = (TextView) view.findViewById(R.id.row_notifiications_date);
            icon = (ImageView) view.findViewById(R.id.row_notifiications_icon);
            text = (TextView) view.findViewById(R.id.row_notifiications_text);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.row_notifications,parent,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        Notifications_Item item = getItem(position);

        holder.date.setText(item.getDate());
        holder.icon.setImageBitmap(item.getIcon());
        holder.text.setText(Html.fromHtml(item.getText()));

        return view;
    }
}
