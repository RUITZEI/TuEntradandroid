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
        ItemDrawer item = items.get(position);
        if (convertView == null){
            //Checkeo si me viene un header o no.
            if (item.isHeader()){     //Inflo el layout del header
                convertView = View.inflate(contexto,R.layout.nav_header_item ,null);
                placeHolder = PlaceHolder.generate(convertView);
                convertView.setTag(placeHolder);
            }else {                  // no es un header, lo trato normalmente
                convertView = View.inflate(contexto,R.layout.drawer_item ,null);
                placeHolder = PlaceHolder.generate(convertView);
                convertView.setTag(placeHolder);
            }

        } else {
            placeHolder = (PlaceHolder)convertView.getTag();
        }

        //Si no es header hacemos tdo como antes.
        if (!item.isHeader()){
            placeHolder.name.setText(items.get(position).getName());
            mImageLoader.displayImage(items.get(position).getImageLink(), placeHolder.image);
        }
        //  todo: Si hacen falta agregar mas headers, habria que ponerle un ID en el layout del header
        //  todo: usando el mismo nombre dado en el PlaceHolder.

        return convertView;
    }
}
