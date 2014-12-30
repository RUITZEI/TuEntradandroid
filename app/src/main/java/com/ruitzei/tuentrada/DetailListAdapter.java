package com.ruitzei.tuentrada;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RUITZEI on 24/12/2014.
 */
public class DetailListAdapter extends ArrayAdapter<Object> {
    private Context context;
    private List<String> detailValues;
    private String[] detailItems;

    public DetailListAdapter(Context context, List<String> detailValues){
        super(context, R.layout.item_details);
        this.context = context;
        this.detailValues = detailValues;

        /*
            nombre , fecha , venue_name , seats_from , ciudad
        */
        this.detailItems = new String[]{"Nombre", "Fecha", "Lugar:", "$$$", "Ciudad", "Series Name"};
    }

    @Override
    public int getCount(){
        return detailValues.size();
    }

    @Override
    public String getItem(int position){
        return detailValues.get(position);
    }

    private static class PlaceHolder{
        TextView detailValue;
        TextView detailItem;

        public static PlaceHolder generate (View convertView){
            PlaceHolder placeHolder = new PlaceHolder();
            placeHolder.detailValue = (TextView)convertView.findViewById(R.id.detail_value);
            placeHolder.detailItem = (TextView) convertView.findViewById(R.id.detail_item);
            return placeHolder;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        PlaceHolder placeHolder;
        if (convertView == null){
            convertView = View.inflate(context,R.layout.detail_item ,null);
            placeHolder = PlaceHolder.generate(convertView);
            convertView.setTag(placeHolder);
        } else {
            placeHolder = (PlaceHolder)convertView.getTag();
        }

        placeHolder.detailValue.setText(detailValues.get(position));
        placeHolder.detailItem.setText(detailItems[position]);

        return convertView;
    }


}
