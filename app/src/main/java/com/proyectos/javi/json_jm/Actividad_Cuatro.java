package com.proyectos.javi.json_jm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Clase Bitcoin
 *
 * Almacena toda la información relevante sobre una Bitcoin obtenida de la API con los datos en JSON
 */
class Bitcoin {
    double cambioEURtoUSD;
    double cambioEURtoJPY;
    String valorEnDolar;
    double valorEnOroOnza;
    double valorEnOroGramo;
    double valorEnPlataOnza;
    double valorEnPlataGramo;
    double precioOroOnza;
    double precioOroGramo;
    double precioPlataOnza;
    double precioPlataGramo;
}

/**
 * Actividad 4
 */
public class Actividad_Cuatro extends AppCompatActivity {

    //Controles de la vista de la actividad
    TextView txtEnDolar;
    TextView txtEnEuro;
    TextView txtEnYen;
    TextView txtOnzaOro;
    TextView txtGramoOro;
    TextView txtOnzaPlata;
    TextView txtGramoPlata;
    TextView txtValorOnzaO;
    TextView txtValorGramoO;
    TextView txtValorOnzaP;
    TextView txtValorGramoP;

    //URL con los cambios de divisas del Banco Central Europeo
    final String WEB = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    //Fichero en el que almacenar el contenido descargado y del que obtener los valores de cambio
    final String TMP = "cambio.txt";

    AsyncHttpClient cliente;
    AsyncHttpClient cliente2;
    AsyncHttpClient client;

    Context ctxt;
    Bitcoin infoCoin;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__cuatro);

        txtEnDolar = (TextView)findViewById(R.id.txtEnDolar);
        txtEnEuro = (TextView)findViewById(R.id.txtEnEuro);
        txtEnYen = (TextView)findViewById(R.id.txtEnYen);
        txtOnzaOro = (TextView)findViewById(R.id.txtOnzaOro);
        txtGramoOro = (TextView)findViewById(R.id.txtGramoOro);
        txtOnzaPlata = (TextView)findViewById(R.id.txtOnzaPlata);
        txtGramoPlata = (TextView)findViewById(R.id.txtGramoPlata);
        txtValorOnzaO = (TextView)findViewById(R.id.txtValorOnzaO);
        txtValorGramoO = (TextView)findViewById(R.id.txtValorGramoO);
        txtValorOnzaP = (TextView)findViewById(R.id.txtValorOnzaP);
        txtValorGramoP = (TextView)findViewById(R.id.txtValorGramoP);

        //Dirección de la API pública con toda la información sobre una Bitcoin
        URL = "http://coinabul.com/api.php";
        infoCoin = new Bitcoin();

        ctxt = this;

        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();
        client = new AsyncHttpClient();

        ObtenerInformacion();
    }

    /**
     * Método que obtiene todos los datos relevantes de la web con contenido en JSON que representa
     * información sobre una Bitcoin
     *
     * Realiza la descarga de este contenido y mediante la clase 'Analisis' se obtiene la
     * información en un objeto de tipo Bitcoin
     */
    private void ObtenerInformacion() {
        client = new AsyncHttpClient();

        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast toast = Toast.makeText(ctxt, "No se ha podido descargar", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    infoCoin = Analisis.analizarEjercicioCuatro(response);
                    Toast toast = Toast.makeText(ctxt, "Descarga completada", Toast.LENGTH_SHORT);
                    toast.show();

                    //Cuando termina este hilo, se ejecuta el método que obtiene el valor de cambio
                    //entre euros y dólares
                    ObtenerCambioUSD();
                } catch (JSONException e) {
                    Toast toast = Toast.makeText(ctxt, "Ha habido un error al analizar el fichero", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    /**
     * Método que obtiene el valor de cambio entre euros y dólares de la API del Banco
     * Central Europeo
     *
     * Realiza la descarga de la web en un fichero y mediante la clase 'Analisis' se
     * obtiene el campo deseado en una variable de tipo 'double', dentro del objeto de
     * tipo 'Bitcoin'
     */
    public void ObtenerCambioUSD() {
        final ProgressDialog progreso = new ProgressDialog(this);
        final File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), TMP);
        RequestHandle requestHandle = cliente.get(WEB, new FileAsyncHttpResponseHandler(miFichero) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
                Toast.makeText(ctxt, "Fallo: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();
                Toast.makeText(ctxt, "Valor de cambio obtenido", Toast.LENGTH_LONG).show();
                try {
                    infoCoin.cambioEURtoUSD = Analisis.obtenerCambioDolar(file);
                    //Cuando termina este hilo, se ejecuta el método que obtiene el valor de cambio
                    //entre euros y yenes
                    ObtenerCambioJPY();
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Obteniendo valor de cambio . . .");
                progreso.setCancelable(false);
                progreso.show();
            }
        });
        requestHandle.setTag("");
    }

    /**
     * Método que obtiene el valor de cambio entre euros y yenes de la API del Banco
     * Central Europeo
     *
     * Realiza la descarga de la web en un fichero y mediante la clase 'Analisis' se
     * obtiene el campo deseado en una variable de tipo 'double', dentro del objeto de
     * tipo 'Bitcoin'
     */
    public void ObtenerCambioJPY() {
        final ProgressDialog progreso = new ProgressDialog(this);
        final File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), TMP);
        RequestHandle requestHandle = cliente2.get(WEB, new FileAsyncHttpResponseHandler(miFichero) {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progreso.dismiss();
                Toast.makeText(getApplicationContext(), "Fallo: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                progreso.dismiss();
                Toast.makeText(getApplicationContext(), "Valor de cambio obtenido", Toast.LENGTH_LONG).show();
                try {
                    infoCoin.cambioEURtoJPY = Analisis.obtenerCambioYen(file);
                    //Cuando termina este hilo, se ejecuta el método que obtiene muestra en pantalla
                    //toda la información acerca de una Bitcoin
                    MostrarInformacion();
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Obteniendo valor de cambio . . .");
                progreso.setCancelable(false);
                progreso.show();
            }
        });
        requestHandle.setTag("");
    }

    /**
     * Método que muestra al usuario en pantalla todos los datos relevantes obtenidos sobre
     * una Bitcoin a través de los controles de la vista de la actividad
     */
    private void MostrarInformacion() {

        double valorEnEUR = ((Double.parseDouble(infoCoin.valorEnDolar)) / infoCoin.cambioEURtoUSD);
        double valorEnJPY = (valorEnEUR * infoCoin.cambioEURtoJPY);

        txtEnDolar.setText(infoCoin.valorEnDolar.substring(0, 8));
        txtEnEuro.setText(String.format("%.4f", valorEnEUR));
        txtEnYen.setText(String.format("%.4f", valorEnJPY));
        txtOnzaOro.setText(String.format("%.4f", infoCoin.valorEnOroOnza));
        txtGramoOro.setText(String.format("%.4f", infoCoin.valorEnOroGramo));
        txtOnzaPlata.setText(String.format("%.4f", infoCoin.valorEnPlataOnza));
        txtGramoPlata.setText(String.format("%.4f", infoCoin.valorEnPlataGramo));
        txtValorOnzaO.setText(String.format("%.4f", infoCoin.precioOroOnza));
        txtValorGramoO.setText(String.format("%.4f", infoCoin.precioOroGramo));
        txtValorOnzaP.setText(String.format("%.4f", infoCoin.precioPlataOnza));
        txtValorGramoP.setText(String.format("%.4f", infoCoin.precioPlataGramo));
    }
}
