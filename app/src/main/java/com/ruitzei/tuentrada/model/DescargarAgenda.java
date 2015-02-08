package com.ruitzei.tuentrada.model;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ruitzei.tuentrada.items.ItemAgenda;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

/**
 * Created by RUITZEI on 07/01/2015.
 */
public class DescargarAgenda extends AsyncTask<String, Void, String> {
    private static final String TAG = "Descarga de Agenda";
    private Fragment mContext;
    private List<ItemAgenda> agenda;

    public DescargarAgenda(Fragment context) {
        Log.d(TAG, "Comienzo la descarga");
        mContext = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        while (!isCancelled()) {
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
        if (result.equalsIgnoreCase("success")) {
            ((OnDownloadCompleted)mContext).onDownloadSucceed(agenda);
        } else {
            ((OnDownloadCompleted)mContext).onDownloadFailed();
        }
    }


    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, ParseException {
        InputStream stream = null;
        TuEntradaParser parserTuEntrada = new TuEntradaParser();
        try {
            stream = downloadUrl(urlString);
            Log.d("Parse", "Antes del Parse.");
            agenda = parserTuEntrada.parse(stream);
            Log.d("Parse", "Despues del parse");
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
        conn.setReadTimeout(200000 /* milliseconds*/);
        conn.setConnectTimeout(250000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Comienza la descarga.
        conn.connect();
        InputStream stream = conn.getInputStream();
        Log.d("Parse", "Se completo la descarga.");
        return stream;
    }
}