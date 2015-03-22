package com.ruitzei.tuentrada.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ruitzei.tuentrada.R;
import com.ruitzei.tuentrada.fragments.FragmentAgenda;
import com.ruitzei.tuentrada.items.ItemAgenda;
import com.ruitzei.tuentrada.model.OnAgendaOverflowItemSelectedListener;

import java.util.List;

public class EventosDestacadosAdapter extends ArrayAdapter<Object>{
    Context contexto;
    private List<ItemAgenda> destacados;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mEventPhotoOptions;
    private FragmentAgenda fragment;


    public EventosDestacadosAdapter(Context contexto, List<ItemAgenda> agenda, ImageLoader imageLoader, DisplayImageOptions imageOptions, FragmentAgenda fragment){
        super(contexto, R.layout.item_destacado);
        this.contexto = contexto;
        this.destacados = agenda;
        this.mImageLoader = imageLoader;
        this.mEventPhotoOptions = imageOptions;
        this.fragment = fragment;
    }


    @Override
    public int getCount(){
        return destacados.size();
    }

    @Override
    public ItemAgenda getItem(int position){
        return destacados.get(position);
    }

    private static class PlaceHolder{
        TextView ciudad;
        TextView nombre;
        ImageView foto;
        View overflowIcon;

        public static PlaceHolder generate (View convertView){

            PlaceHolder placeHolder = new PlaceHolder();
            placeHolder.nombre = (TextView)convertView.findViewById(R.id.destacado_titulo);
            placeHolder.ciudad = (TextView)convertView.findViewById(R.id.destacado_descripcion);
            placeHolder.foto = (ImageView)convertView.findViewById(R.id.destacados_imagen);
            placeHolder.overflowIcon = convertView.findViewById(R.id.destacado_overflow);


            return placeHolder;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        PlaceHolder placeHolder;
        if (convertView == null){
            convertView = View.inflate(contexto,R.layout.item_destacado ,null);
            placeHolder = PlaceHolder.generate(convertView);
            convertView.setTag(placeHolder);
        } else {
            placeHolder = (PlaceHolder)convertView.getTag();
        }

        ItemAgenda item = destacados.get(position);


        //placeHolder.foto.setImageUrl("http://3.bp.blogspot.com/-R_7qdxVpSIg/UTtjWiBNO-I/AAAAAAAABCE/MTX-FUb4LTY/s1600/ballet-el-lago-de-los-cisnes%5B1%5D.jpg",mImageLoader);
        //placeHolder.foto.setImageUrl("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRyiXI4PEL4lr725Bldtawz9VJLVU1b7ayzgqFktV8dfLHlG8uRhh2JMA",mImageLoader);
        mImageLoader.displayImage("drawable://" + R.drawable.ic_launcher, placeHolder.foto);
        placeHolder.ciudad.setText(item.getCiudad());
        placeHolder.nombre.setText(item.getNombre());
        placeHolder.overflowIcon.setOnClickListener(new OnAgendaOverflowItemSelectedListener(contexto, fragment, item));

        String tipo = destacados.get(position).getNombre().toLowerCase();
        setFoto(placeHolder, destacados.get(position).getLogoId(), tipo);

        //Fondo Gris o negro segun corresponda.
        /*if (position % 2 == 0){
            convertView.setBackgroundResource(R.drawable.selector_lista);
        }else{
            convertView.setBackgroundResource(R.drawable.selector_lista_secundario);
        }*/

        return convertView;
    }


    private void setFoto(PlaceHolder placeHolder, String link, String tipo) {
        if (link.length() == 0){
            /*
            Poner una imagen Default dependiendo del Venue.
            */
        }else {
            //mImageLoader.displayImage(link, placeHolder.foto);
            mImageLoader.displayImage(link, placeHolder.foto, mEventPhotoOptions);
        }
    }
}