package com.proyectos.javi.json_jm;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Clase Prediccion
 *
 * Almacena información sobre la predicción de una ciudad
 */
class Prediccion {
    String fecha;
    double temperaturaMax;
    double temperaturaMin;
    double presion;
}

/**
 * Clase CiudadPrediccion
 *
 * Almacena información sobre el nombre y el país de una ciudad
 */
class CiudadPrediccion {
    String nombre;
    String pais;
}

/**
 * Actividad 2
 */
public class Actividad_Dos extends AppCompatActivity implements View.OnClickListener {

    //Controles de la vista de la actividad
    Button btnDescargar;
    EditText tbxCiudad;

    Context ctxt;
    AsyncHttpClient cliente;
    ArrayList<Prediccion> listaPredicciones;
    CiudadPrediccion ciPred;

    //Datos sobre la URL de la que obtener los datos y nombres de lso ficheros a escribir con la información
    String ciudad = "";
    String URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
    String URL2 = "&mode=json&units=metric&cnt=7&appid=2de143494c0b295cca9337e1e96b00e0";
    String Fichero_JSON = "prediccion.json";
    String Fichero_XML = "prediccion.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__dos);

        ctxt = this;
        cliente = new AsyncHttpClient();

        btnDescargar = (Button)findViewById(R.id.btnDescargar_Ej2);
        btnDescargar.setOnClickListener(this);
        tbxCiudad = (EditText)findViewById(R.id.tbxCiudad_Ej2);
    }

    @Override
    public void onClick(View v) {
        if(tbxCiudad.getText().toString().length() == 0)
            Toast.makeText(this, "No se ha escrito nada", Toast.LENGTH_SHORT).show();
        else{
            //Si se ha escrito algo, se construye la URL y descarga la información si es una ciudad válida
            ciudad = tbxCiudad.getText().toString();
            URL = URL + ciudad + URL2;
            descarga(URL);
        }
    }

    /**
     * Método con el hilo que realiza la descarga de la información de las predicciones
     * @param url URL con el contenido que hay que descargar en formato JSON
     */
    private void descarga(String url) {
        cliente.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //Almacena en la lista los datos sobre las predicciones
                    listaPredicciones = Analisis.analizarPredicciones(response);
                    //Almacena en otro objeto el nombre y el país de la ciudad elegida
                    ciPred = Analisis.analizarCiudadPredicciones(response);

                    //Se escriben los contenidos en memoria externa, tanto en formato JSON como XML
                    Analisis.almacenarJSON(listaPredicciones, Fichero_JSON);
                    Analisis.almacenarXML(listaPredicciones, Fichero_XML, ciPred);

                    Toast.makeText(ctxt, "Descarga realizada con éxito", Toast.LENGTH_SHORT).show();

                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
