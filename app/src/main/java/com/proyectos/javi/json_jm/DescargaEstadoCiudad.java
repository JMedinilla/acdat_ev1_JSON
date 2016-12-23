package com.proyectos.javi.json_jm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Clase EstadoCiudad
 *
 * Almacena información completa del tiempo para una ciudad
 */
class EstadoCiudad {
    String nombre;
    String pais;
    String imagen;
    double latitud;
    double longitud;
    double humedad;
    double temperaturaMaxima;
    double temperaturaMinima;
    String estadoCielo;
    double velocidadViento;
    double presion;
}

/**
 * Clase DescargaEstadoCiudad
 *
 * Implementa el hilo que descargar el contenido de la información del tiempo para una ciudad dada
 * y muestra sus datos en la vista de la actividad
 */
public class DescargaEstadoCiudad extends Activity {
    //Datos para la URL a descargar
    String nombre;
    String pais;
    String appid;
    String URL;

    Context ctxt;
    EstadoCiudad estadoCiudad;
    AsyncHttpClient client;

    //Controles de la vista de la actividad
    ImageView imIcono;
    TextView txNombre;
    TextView txPais;
    TextView txLatitud;
    TextView txLongitud;
    TextView txHumedad;
    TextView txMax;
    TextView txMin;
    TextView txCielo;
    TextView txViento;
    TextView txPresion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descarga_estado_ciudad);

        imIcono = (ImageView)findViewById(R.id.imgTiempo);
        txNombre = (TextView)findViewById(R.id.txtNombre);
        txPais = (TextView)findViewById(R.id.txtPais);
        txLatitud = (TextView)findViewById(R.id.txtLatitud);
        txLongitud = (TextView)findViewById(R.id.txtLongitud);
        txHumedad = (TextView)findViewById(R.id.txtHumedad);
        txMax = (TextView)findViewById(R.id.txtTempMax);
        txMin = (TextView)findViewById(R.id.txtTempMin);
        txCielo = (TextView)findViewById(R.id.txtCielo);
        txViento = (TextView)findViewById(R.id.txtViento);
        txPresion = (TextView)findViewById(R.id.txtPresion);

        //La ciudad, su país y el ID para usar la API vienen desde al actividad anterior
        Bundle extras = getIntent().getExtras();
        nombre = extras.getString("ciudad");
        pais = extras.getString("pais");
        appid = extras.getString("id");
        //URL completa que contiene los datos del tiempo de la ciudad
        URL = "http://api.openweathermap.org/data/2.5/weather?q=" + nombre + "," + pais + "&appid=" + appid;

        ctxt = this;
        estadoCiudad = new EstadoCiudad();
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
                    //Almacena en un objeto de tipo 'EstadoCiudad' toda la información obtenida
                    //en el método de la clase 'Analisis' específico para obtenerla
                    estadoCiudad = Analisis.analizarEjercicioUno(response);
                    Toast toast = Toast.makeText(ctxt, "Descarga completada", Toast.LENGTH_SHORT);
                    toast.show();
                    //Se muestran los datos al usuario
                    mostrarCiudad();
                }
                catch (JSONException e) {
                    Toast toast = Toast.makeText(ctxt, "Ha habido un error al analizar el fichero", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    /**
     * Método que muestra mediante la vista de la actividad al usuario todos los datos
     * leídos sobre el tiempo actual de una ciudad elegida por el usuario
     */
    private void mostrarCiudad() {
        double tmpMax = estadoCiudad.temperaturaMaxima - 273.15;
        double tmpMin = estadoCiudad.temperaturaMinima - 273.15;
        String tmpImg = "http://openweathermap.org/img/w/" + estadoCiudad.imagen + ".png";

        txNombre.setText(String.valueOf(estadoCiudad.nombre));
        txPais.setText(String.valueOf(estadoCiudad.pais) + " (EU)");
        txLatitud.setText(String.format("%.5f", estadoCiudad.latitud));
        txLongitud.setText(String.format("%.5f", estadoCiudad.longitud));
        txHumedad.setText(String.valueOf(estadoCiudad.humedad) + " %");
        txMax.setText(String.format("%.2f", tmpMax) + " ºC");
        txMin.setText(String.format("%.2f", tmpMin) + " ºC");
        txCielo.setText(String.valueOf(estadoCiudad.estadoCielo));
        txViento.setText(String.valueOf(estadoCiudad.velocidadViento) + " m/s");
        txPresion.setText(String.valueOf(estadoCiudad.presion) + " hpa");
        Picasso.with(this).load(tmpImg).error(R.drawable.error).into(imIcono);
    }
}