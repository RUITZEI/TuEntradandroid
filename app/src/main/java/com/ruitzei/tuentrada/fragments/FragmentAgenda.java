package com.ruitzei.tuentrada.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ruitzei.tuentrada.MainActivity;
import com.ruitzei.tuentrada.R;
import com.ruitzei.tuentrada.items.ItemAgenda;
import com.ruitzei.tuentrada.model.Categorias;
import com.ruitzei.tuentrada.model.DescargarAgenda;
import com.ruitzei.tuentrada.model.OnDownloadCompleted;
import com.ruitzei.tuentrada.adapters.CustomListAdapter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by RUITZEI on 18/12/2014.
 */

public class FragmentAgenda extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnDownloadCompleted {

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
    private HashMap<String, List<ItemAgenda>> map;
    private SwipeRefreshLayout mRefreshLayout;

    private static final String RSS_TUENTRADA = "https://www.tuentrada.com/online/feedxml.asp";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        this.actividadPrincipal = ((MainActivity)getActivity());
        this.lista = (ListView)view.findViewById(android.R.id.list);
        this.mSpinner = view.findViewById(R.id.loadingPanel);
        this.mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        this.mRefreshLayout.setOnRefreshListener(this);
        this.mRefreshLayout.setColorSchemeColors(R.color.material_blue, R.color.TuEntradaMain, R.color.darkerRed, R.color.material_deep_teal_200);

        setHasOptionsMenu(true);

        if (actividadPrincipal.existeAgenda()){
            mostrarLista();
        }else{
            mSpinner.setVisibility(View.VISIBLE);
            new DescargarAgenda(this).execute(RSS_TUENTRADA);
        }

        agregarListenerLista();

        return view;
    }


    public void mostrarLista(){
        adapterNoticias = new CustomListAdapter(actividadPrincipal.getmToolBar().getContext(),actividadPrincipal.getAgenda(), actividadPrincipal.getImageLoader(), actividadPrincipal.getEventPhotoOptions(),this);
        lista.setAdapter(adapterNoticias);

        agregarListenerLista();
    }


    /**
     * Paso como extra la ruta COMPLETA al link donde quiero acceder.
     */
    public void agregarListenerLista() {
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                goToBuy(position);
            }
        });
    }

    public void mostrarSpinner(){
        this.mSpinner.setVisibility(View.VISIBLE);
    }

    public void ocultarSpinner(){
        this.mSpinner.setVisibility(View.GONE);
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
    public void onRefresh(){
        Log.d(TAG, "Refreshing...");
        if (!actividadPrincipal.existeAgenda()){
            new DescargarAgenda(this).execute(RSS_TUENTRADA);
        }else{
            Log.d(TAG, "Ya exisita la agenda...");
            mRefreshLayout.setRefreshing(false);
        }
    }

    /*
    Metodos de la descarga del XML de TuEntrada
     */
    @Override
    public void onDownloadSucceed(HashMap<String, List<ItemAgenda>> agenda){
        this.map = agenda;
        actividadPrincipal.setAgenda(agenda.get(Categorias.PRINCIPAL));
        mostrarLista();
        mSpinner.setVisibility(View.GONE);
        if (mRefreshLayout.isRefreshing()) mRefreshLayout.setRefreshing(false);
    }

    public void cambiarAgenda(String claveDiccionario){
        actividadPrincipal.setAgenda(map.get(claveDiccionario));
    }

    @Override
    public void onDownloadFailed(){
        mSpinner.setVisibility(View.GONE);
        if (mRefreshLayout.isRefreshing()) mRefreshLayout.setRefreshing(false);

        //TODO Should probably add a view to let the user know smth happened.
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

    public void goToDetails(int position){
        boolean tieneAsientosDisponibles = adapterNoticias.getItem(position).getDisponibilidad() != 'S';
        //No se puede comprar si los tickets toadvia no salieron a la venta
        boolean puedenComprar = (adapterNoticias.getItem(position).getFechaDeVenta().length() == 0);

        if (tieneAsientosDisponibles && puedenComprar){
            adapterNoticias.getItem(position).getDiaEvento();
            adapterNoticias.getItem(position).getMesEvento();
            adapterNoticias.getItem(position).getAnioEvento();

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

            Fragment fragment = new FragmentDetails();
            fragment.setArguments(args);
            FragmentManager fm = actividadPrincipal.getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, fragment)
                    .addToBackStack("Fragback")
                    .commit();
        }else if (!tieneAsientosDisponibles){
            //Toast.makeText(getActivity(), R.string.msg_no_seats_available, Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "No Asientos Disponibles", Toast.LENGTH_LONG).show();
        }else if (!puedenComprar){
            //String msgNoSeatsYet = getString(R.string.msg_no_seats_on_sale);
            //Toast.makeText(getActivity(), msgNoSeatsYet + " " + adapterNoticias.getItem(position).getFechaDeVentaConvertida(), Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "no salieron a la venta" + " " + adapterNoticias.getItem(position).getFechaDeVentaConvertida(), Toast.LENGTH_LONG).show();
        }

    }

    public void goToBuy(int position){
        Log.d("FECHA COMUN>" , adapterNoticias.getItem(position).getFecha());
        Bundle args = new Bundle();

        String link = adapterNoticias.getItem(position).getLink();
        String linkMobile = link.substring(0, link.indexOf("seat")) + "mobile/" + link.substring(link.indexOf("seat"), link.length());

        //Necesito ambos links porque si comparto por facebook voy a necesitar el link a la version desktop.
        args.putString("link", link);
        args.putString("linkMobile", linkMobile);

        Fragment fragment = new FragmentWebView();
        fragment.setArguments(args);
        FragmentManager fm = actividadPrincipal.getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.container, fragment)
                .addToBackStack("Fragback")
                .commit();
    }

    public void addToCalendar(int position){
            long startMillis = 0;
        long endMillis = 0;

        ItemAgenda item = adapterNoticias.getItem(position);

        //Por alguna razon le suma 1 al numero de mes...
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(item.getAnioEvento(),
                     item.getMesEvento()-1,
                     item.getDiaEvento());
        startMillis = beginCal.getTimeInMillis();


        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, item.getNombre());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, item.getSeriesName());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, item.getNombreVenue());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginCal.getTimeInMillis());
//        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        intent.putExtra(CalendarContract.Events.STATUS, 1);
        intent.putExtra(CalendarContract.Events.VISIBLE, 0);
        intent.putExtra(CalendarContract.Events.HAS_ALARM, false);
        startActivity(intent);
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

