package com.ruitzei.tuentrada.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ruitzei.tuentrada.items.ItemAgenda;
import com.ruitzei.tuentrada.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUITZEI on 18/12/2014.
 */
public class CustomListAdapter extends ArrayAdapter<Object> implements Filterable {
    Context contexto;
    private List<ItemAgenda> agenda;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mEventPhotoOptions;
    private List<ItemAgenda> agendaFiltrada;


    public CustomListAdapter(Context contexto, List<ItemAgenda> agenda, ImageLoader imageLoader, DisplayImageOptions imageOptions){
        super(contexto, R.layout.list_item_agenda);
        this.contexto = contexto;
        this.agenda  = agenda;
        this.agendaFiltrada = this.agenda;
        this.mImageLoader = imageLoader;
        this.mEventPhotoOptions = imageOptions;
    }


    @Override
    public int getCount(){
        return agendaFiltrada.size();
    }

    @Override
    public ItemAgenda getItem(int position){
        return agendaFiltrada.get(position);
    }

    private static class PlaceHolder{
        TextView ciudad;
        TextView nombre;
        TextView fecha;
        ImageView foto;


        public static PlaceHolder generate (View convertView){

            PlaceHolder placeHolder = new PlaceHolder();
            placeHolder.nombre = (TextView)convertView.findViewById(R.id.nombre);
            placeHolder.ciudad = (TextView)convertView.findViewById(R.id.ciudad);
            placeHolder.fecha = (TextView)convertView.findViewById(R.id.fecha);
            placeHolder.foto = (ImageView)convertView.findViewById(R.id.foto);

            return placeHolder;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        PlaceHolder placeHolder;
        if (convertView == null){
            convertView = View.inflate(contexto,R.layout.list_item_agenda ,null);
            placeHolder = PlaceHolder.generate(convertView);
            convertView.setTag(placeHolder);
        } else {
            placeHolder = (PlaceHolder)convertView.getTag();
        }

        //Obteniendo instancia del volley.


        //placeHolder.foto.setImageUrl("http://3.bp.blogspot.com/-R_7qdxVpSIg/UTtjWiBNO-I/AAAAAAAABCE/MTX-FUb4LTY/s1600/ballet-el-lago-de-los-cisnes%5B1%5D.jpg",mImageLoader);
        //placeHolder.foto.setImageUrl("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRyiXI4PEL4lr725Bldtawz9VJLVU1b7ayzgqFktV8dfLHlG8uRhh2JMA",mImageLoader);
        mImageLoader.displayImage("drawable://" + R.drawable.ic_launcher, placeHolder.foto);
        placeHolder.ciudad.setText(agendaFiltrada.get(position).getCiudad());
        placeHolder.nombre.setText(agendaFiltrada.get(position).getNombre());
        placeHolder.fecha.setText(agendaFiltrada.get(position).getFechaConvertida());

        String tipo = agendaFiltrada.get(position).getNombre().toLowerCase();
        setFoto(placeHolder, agendaFiltrada.get(position).getLogoId(), tipo);

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


    /**
     * Cada vez que se le pasa un filtro, se recorre toda la lista de noticias
     * buscando aquellas que cumplan con la condicion de filtro (contienen tal string)
     * Las que cumplen con la condicion son ingresadas en el nuevo array de noticias
     * que despues es el invocado por el getView del Adapter.
     * En caso de no ser llamado no pasa nada porque en el constructor del Adapter,
     * el valor inicial de las NoticiasFiltradas equivale al de las Noticias.
     */

    @Override
    public Filter getFilter(){
        Filter myFilter = new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null){
                    List<ItemAgenda> todasLasNoticias = agenda;
                    List<ItemAgenda> filtradas = new ArrayList<ItemAgenda>();
                    String palabraFiltro = constraint.toString().toLowerCase();
                    String nombreActual;
                    String tipoActual;
                    for (int i = 0 ;i < todasLasNoticias.size(); i++){
                        nombreActual = todasLasNoticias.get(i).getNombre().toLowerCase();
                        tipoActual = todasLasNoticias.get(i).getCiudad().toLowerCase();
                        if( nombreActual.contains(palabraFiltro) || tipoActual.contains(palabraFiltro)){
                            filtradas.add(todasLasNoticias.get(i));
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filtradas;
                    results.count = filtradas.size();
                    return results;
                }else{
                    return new FilterResults();
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results.count > 0){
                    agendaFiltrada = (ArrayList<ItemAgenda>)results.values;
                    notifyDataSetChanged();
                }else{
                    agendaFiltrada = new ArrayList<ItemAgenda>();
                    notifyDataSetInvalidated();
                }

            }
        };
        return myFilter;
    }
}
