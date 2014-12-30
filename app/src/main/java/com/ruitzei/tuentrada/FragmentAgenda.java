package com.ruitzei.tuentrada;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

/**
 * Created by RUITZEI on 18/12/2014.
 */

public class FragmentAgenda extends Fragment{

    private static final String TAG = "Fragment Agenda";
    private CustomListAdapter adapterNoticias;
    private MainActivity actividadPrincipal;
    private ListView lista;
    private SearchView searchView;
    private MenuItem searchItem;
    private int ultimoItemClickeado = 0;
    private String[] itemsSpinner;
    private SearchView mSearchView;
    private View mSpinner;

    private static final String RSS_TUENTRADA = "https://www.tuentrada.com/online/feedxml.asp";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        this.actividadPrincipal = ((MainActivity)getActivity());
        this.lista = (ListView)view.findViewById(android.R.id.list);
        this.mSpinner = view.findViewById(R.id.loadingPanel);

        setHasOptionsMenu(true);

        if (actividadPrincipal.existeAgenda()){
            mostrarLista();
        }else{
            new DescargarYMostrar().execute(RSS_TUENTRADA);
        }

        agregarListenerLista();

        return view;
    }


    public void mostrarLista(){
        adapterNoticias = new CustomListAdapter(getActivity().getApplicationContext(),actividadPrincipal.getAgenda(), actividadPrincipal.getImageLoader(), actividadPrincipal.getEventPhotoOptions());
        lista.setAdapter(adapterNoticias);

        agregarListenerLista();
    }


    // Descarga del archivo en un hilo separado para no tildar la interfaz de usuario.
    private class DescargarYMostrar extends AsyncTask<String, Void, String> {
        private ProgressDialog asycdialog = new ProgressDialog(getActivity());

        public DescargarYMostrar(){

        }

        @Override
        protected void onPreExecute(){
            //Let the Spinner start rolling.
            mSpinner.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... urls) {
            while (!isCancelled()){
                try {
                    return loadXmlFromNetwork(urls[0]);
                } catch (IOException e) {
                    return "Error de conexion";
                } catch (XmlPullParserException e) {
                    return "Error del link!";
                } catch (ParseException e) {
                    return "Error del parse Date";
                }
            }
            return "asd";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("success")){
                //Toast.makeText(getActivity(), "Descarga correcta", Toast.LENGTH_LONG).show();
                mostrarLista();
            } else {
                //Toast.makeText(getActivity(), R.string.msg_nointernet_agenda, Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "No internet", Toast.LENGTH_LONG).show();
            }
            mSpinner.setVisibility(View.GONE);
        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, ParseException {
        InputStream stream = null;
        TuEntradaParser oleParser = new TuEntradaParser();
        try {
            stream = downloadUrl(urlString);
            Log.d("Parse", "Antes del Parse.");
            //Le paso al PARSER el archivo XML para que haga lo suyo.
            actividadPrincipal.setAgenda(oleParser.parse(stream));
            Log.d("Parse","Despues del parse");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return "Success";
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(200000 /* milliseconds*/ );
        conn.setConnectTimeout(250000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Comienza la descarga.
        conn.connect();
        InputStream stream = conn.getInputStream();
        Log.d("Parse","Se completo la descarga.");
        return stream;
    }


    /**
     * Paso como extra la ruta COMPLETA al link donde quiero acceder.
     */
    public void agregarListenerLista() {
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                boolean tieneAsientosDisponibles = adapterNoticias.getItem(position).getDisponibilidad() != 'S';
                //No se puede comprar si los tickets toadvia no salieron a la venta
                boolean puedenComprar = (adapterNoticias.getItem(position).getFechaDeVenta().length() == 0);

                if (tieneAsientosDisponibles && puedenComprar){
                    Log.d("Item Clicked", adapterNoticias.getItem(position).getLink());

                    /*
                    Preparando el Bundle para mandarle al siguiente Fragment.
                     */

                    String link = adapterNoticias.getItem(position).getLink();
                    String linkMobile = link.substring(0, link.indexOf("seat")) + "mobile/" + link.substring(link.indexOf("seat"), link.length());
                    Bundle args = new Bundle();
                    args.putString("link", linkMobile);
                    args.putString("image", adapterNoticias.getItem(position).getLogoId());
                    args.putString("nombre", adapterNoticias.getItem(position).getNombre());
                    args.putString("fecha", adapterNoticias.getItem(position).getFechaConvertida());
                    args.putString("venue_name", adapterNoticias.getItem(position).getNombreVenue());
                    args.putString("seats_from", adapterNoticias.getItem(position).getAsientosDesde());
                    args.putString("ciudad", adapterNoticias.getItem(position).getCiudad());
                    args.putString("series_name", adapterNoticias.getItem(position).getSeriesName());


                    Fragment fragment = new FragmentWeb();
                    fragment.setArguments(args);
                    FragmentManager fm = actividadPrincipal.getSupportFragmentManager();
                    fm.beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack("Fragback")
                            .commit();
                    /*Bundle args = new Bundle();
                    args.putString("link", COMPRA_COLON+adapterNoticias.getItem(position).getLink());
                    Fragment fragment = new FragmentWeb();
                    fragment.setArguments(args);
                    MenuItemCompat.collapseActionView(searchItem);

                    Log.d("Agenda","Se clickeo el elemento Nº "+ position );
                    FragmentManager fragmentManager = actividadPrincipal.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack("FragBack")
                            .commit();*/
                }else if (!tieneAsientosDisponibles){
                    //Toast.makeText(getActivity(), R.string.msg_no_seats_available, Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "No Asientos Disponibles", Toast.LENGTH_LONG).show();
                }else if (!puedenComprar){
                    //String msgNoSeatsYet = getString(R.string.msg_no_seats_on_sale);
                    //Toast.makeText(getActivity(), msgNoSeatsYet + " " + adapterNoticias.getItem(position).getFechaDeVentaConvertida(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "no salieron a la venta" + " " + adapterNoticias.getItem(position).getFechaDeVentaConvertida(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);

         // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG, "Action Search pressed");
                mSearchView.setIconified(true);
                mSearchView.setOnQueryTextListener(queryTextListener);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        agregarListenerLista();
        actividadPrincipal.mDrawerToggle.setDrawerIndicatorEnabled(true);
        actividadPrincipal.getSupportActionBar().setTitle("  TuEntrada");
        actividadPrincipal.getSupportActionBar().setDisplayShowHomeEnabled(true);
        actividadPrincipal.getmToolBar().setBackgroundColor(getResources().getColor(R.color.TuEntradaMain));
    }

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String arg0) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String arg0) {
            adapterNoticias.getFilter().filter(arg0);
            return false;
        }
    };
}

