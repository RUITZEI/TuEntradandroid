package com.ruitzei.tuentrada.items;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by RUITZEI on 18/12/2014.
 */
public class ItemAgenda {

    private String nombre;
    private String ciudad;
    private String fecha;
    private String link;
    private String logoId;
    private String fechaDeVenta;
    private String nombreVenue;
    private String asientosDesde;
    private String seriesName;
    private char disponibilidad;
    private String category;

    public ItemAgenda(String nombre, String ciudad,  String fecha, String link,
                      String logoId, String fechaDeVenta, char disponibilidad, String nombreVenue, String asientosDesde, String seriesName, String category){
        setNombre(nombre);
        setCiudad(ciudad);
        setFecha(fecha);
        setLink(link);
        setLogoId(logoId);
        setFechaDeVenta(fechaDeVenta);
        setDisponibilidad(disponibilidad);
        setNombreVenue(nombreVenue);
        setAsientosDesde(asientosDesde);
        setSeriesName(seriesName);
        setCategory(category);
    }

    public void setFechaDeVenta(String fechaDeVenta) {
        this.fechaDeVenta = fechaDeVenta;
    }

    public void setDisponibilidad(char disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    private void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    private void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setFecha(String fecha){
        this.fecha = fecha;
    }

    public void setLink(String link){
        this.link = link;
    }

    public void setNombreVenue(String nombreVenue){
        this.nombreVenue = nombreVenue;
    }

    public void setAsientosDesde (String asientosDesde){
        this.asientosDesde = asientosDesde;
    }

    public void setSeriesName (String seriesName){
        this.seriesName = seriesName;
    }

    public void setCategory (String category){
        this.category = category;
    }


    public String getCiudad(){
        return this.ciudad;
    }

    public String getNombre(){
        return this.nombre;
    }

    public String getFecha(){
        return this.fecha;
    }

    public String getLink(){
        return this.link;
    }

    public String getLogoId(){
        return this.logoId;
    }

    public char getDisponibilidad(){
        return this.disponibilidad;
    }

    public String getFechaDeVenta(){
        return this.fechaDeVenta;
    }

    public String getNombreVenue(){
        return this.nombreVenue;
    }

    public String getAsientosDesde(){
        return this.asientosDesde;
    }

    public String getCategory(){
        return this.category;
    }

    public String getSeriesName(){
        return this.seriesName;
    }

    public String getFechaDeVentaConvertida(){
        String fecha = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ", new Locale("es", "ES"));
        Date fechaConvertida;

        try{
            fechaConvertida = dateFormat.parse(this.fechaDeVenta);
            SimpleDateFormat postFormat = new SimpleDateFormat("dd'/'M 'a las' HH:mm'hs'", new Locale("es", "ES"));
            fecha =  postFormat.format(fechaConvertida);

        } catch (ParseException e){
            e.printStackTrace();
        }
        return fecha;
    }

    public String getFechaConvertida(){
        String fecha = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ", new Locale("es", "ES"));
        Date fechaConvertida;

        try{
            fechaConvertida = dateFormat.parse(this.fecha);
            SimpleDateFormat postFormat = new SimpleDateFormat("dd'/'M'/'yy ", new Locale("es", "ES"));
            fecha =  postFormat.format(fechaConvertida);

        } catch (ParseException e){
            e.printStackTrace();
        }
        return fecha;
    }

    public int getDiaEvento(){
        String fecha = getFechaConvertida();
        String dia = fecha.substring(0,2);
        Log.d("Dia", dia);
        return Integer.parseInt(dia);
    }

    public int getMesEvento(){
        String fecha = getFechaConvertida();
        String mes = fecha.substring(fecha.indexOf("/")+1, fecha.lastIndexOf("/"));
        Log.d("Mes", mes);

        return Integer.parseInt (mes);
    }

    public int getAnioEvento(){
        String fecha = getFechaConvertida();
        String anio = fecha.substring(fecha.lastIndexOf("/")+1, fecha.length()-1);
        Log.d("Anio", anio);

        return 2000 + Integer.parseInt(anio);
    }
}
