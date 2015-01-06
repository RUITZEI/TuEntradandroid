package com.ruitzei.tuentrada.model;

import android.util.Xml;

import com.ruitzei.tuentrada.items.ItemAgenda;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RUITZEI on 18/12/2014.
 */
public class TuEntradaParser {

    private String ns = null;
    private static final String ATT_NOMBRE = "performance_description";
    private static final String ATT_CIUDAD = "venue_city";
    private static final String ATT_FECHA = "performance_start_date";
    private static final String ATT_LINK = "link_compra";
    private static final String ATT_LOGOID = "performance_logo1";
    private static final String ATT_DISPONIBILIDAD = "availability_status";
    private static final String ATT_ONSALE = "on_sale_date";
    private static final String ATT_VENUE_NAME = "venue_name";
    private static final String ATT_SEATS_FROM = "min_price";
    private static final String ATT_SERIES_NAME = "series_name";

    public List<ItemAgenda> parse(InputStream in) throws XmlPullParserException, IOException{
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResults(parser);
        } finally {
            in.close();
        }
    }

    private List<ItemAgenda> readResults (XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "results");
        List<ItemAgenda> agenda = new ArrayList<ItemAgenda>();

        while ( parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name =parser.getName();

            if (name.equals("event")){
                //Asumo que si el minimo de las entradas es 0.00, el evento fue cancelado
                ItemAgenda item = readEvent(parser);
                if (!item.getAsientosDesde().equalsIgnoreCase("0.00")){
                    agenda.add(item);
                }

            } else {
                skip(parser);
            }
        }

        return agenda;
    }

    private ItemAgenda readEvent(XmlPullParser parser)throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "event");
        String nombre = null;
        String ciudad = null;
        String fecha = null;
        String link = null;
        String logoId = null;
        String fechaDeVenta = null;
        String nombreVenue = null;
        String asientosDesde = null;
        String seriesName = null;
        char disponibilidad = ' ';

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(ATT_NOMBRE)){
                nombre = leerNombre(parser);
            } else if (name.equals(ATT_CIUDAD)){
                ciudad = leerTipo(parser);
            } else if (name.equals(ATT_FECHA)){
                fecha = leerFecha(parser);
            } else if (name.equals(ATT_LINK)){
                link = leerLink(parser);
            }else if (name.equals(ATT_LOGOID)){
                logoId = leerLogoId(parser);
            }else if (name.equals(ATT_DISPONIBILIDAD)){
                disponibilidad = leerDisponibilidad(parser);
            }else if (name.equals(ATT_ONSALE)){
                fechaDeVenta = leerFechaDeVenta(parser);
            }else if (name.equals(ATT_VENUE_NAME)){
                nombreVenue = leerNombreVenue(parser);
            }else if (name.equals(ATT_SEATS_FROM)){
                asientosDesde = leerAsientosDesde(parser);
            }else if (name.equals(ATT_SERIES_NAME)){
                seriesName = leerSeriesName(parser);
            }else{
                skip(parser);
            }
        }

        return new ItemAgenda(nombre, ciudad, fecha, link, logoId, fechaDeVenta, disponibilidad, nombreVenue, asientosDesde, seriesName);
    }


    private String leerFechaDeVenta(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_ONSALE);
        String fechaDeVenta = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_ONSALE);
        return fechaDeVenta;
    }

    private String leerNombre(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_NOMBRE);
        String nombre = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_NOMBRE);
        return nombre;
    }

    private String leerTipo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_CIUDAD);
        String tipo = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_CIUDAD);

        return tipo;
    }

    private String leerFecha(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_FECHA);
        String fecha = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_FECHA);
        return fecha;
    }

    private String leerLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_LINK);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_LINK);
        return link;
    }

    private String leerLogoId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_LOGOID);
        String logoId = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_LOGOID);
        return logoId;
    }

    private char leerDisponibilidad(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_DISPONIBILIDAD);
        char asientosLibres = readText(parser).charAt(0);
        parser.require(XmlPullParser.END_TAG, ns, ATT_DISPONIBILIDAD);
        return asientosLibres;
    }

    private String leerNombreVenue(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_VENUE_NAME);
        String nombreVenue = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_VENUE_NAME);
        return nombreVenue;
    }

    private String leerAsientosDesde(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_SEATS_FROM);
        String asientosDesde = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_SEATS_FROM);
        return asientosDesde;
    }

    private String leerSeriesName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ATT_SERIES_NAME);
        String seriasName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ATT_SERIES_NAME);
        return seriasName;
    }

    //Aca estoy levantando texto del atributo pedido.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultado = "";
        if (parser.next() == XmlPullParser.TEXT){
            resultado = parser.getText();
            parser.nextTag();
        }
        return resultado;
    }


    //Si no me interesa porque no tiene el tag que le pedi, lo mando al carajo.
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
