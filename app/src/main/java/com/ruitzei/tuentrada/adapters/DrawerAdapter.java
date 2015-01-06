package com.ruitzei.tuentrada.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ruitzei.tuentrada.items.ItemDrawer;
import com.ruitzei.tuentrada.R;

import java.util.List;

/**
 * Created by RUITZEI on 13/12/2014.
 */
public class DrawerAdapter extends ArrayAdapter<Object> {
    Context contexto;
    List<ItemDrawer> items;
    ImageLoader mImageLoader;

    public DrawerAdapter (Context contexto, List<ItemDrawer> items, ImageLoader imageLoader){
        super(contexto, R.layout.drawer_item);
        this.contexto = contexto;
        this.items = items;
        this.mImageLoader = imageLoader;
    }



    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public ItemDrawer getItem(int position){
        return items.get(position);
    }

    private static class PlaceHolder{
        TextView name;
        ImageView image;



        public static PlaceHolder generate (View convertView){
            PlaceHolder placeHolder = new PlaceHolder();
            placeHolder.name = (TextView)convertView.findViewById(R.id.drawer_name);
            placeHolder.image = (ImageView)convertView.findViewById(R.id.drawer_image);

            return placeHolder;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        PlaceHolder placeHolder;
        if (convertView == null){
            convertView = View.inflate(contexto,R.layout.drawer_item ,null);
            placeHolder = PlaceHolder.generate(convertView);
            convertView.setTag(placeHolder);
        } else {
            placeHolder = (PlaceHolder)convertView.getTag();
        }

        placeHolder.name.setText(items.get(position).getName());
        mImageLoader.displayImage(items.get(position).getImageLink(), placeHolder.image);

        //if (position == 1) placeHolder.name.setTextColor(contexto.getResources().getColor(R.color.red));

        return convertView;
    }
}
