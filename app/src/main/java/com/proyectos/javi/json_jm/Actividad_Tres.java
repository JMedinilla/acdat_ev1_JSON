package com.proyectos.javi.json_jm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Actividad 3
 */
public class Actividad_Tres extends AppCompatActivity implements View.OnClickListener {

    //URL de la página con el XML de cambios de divisas del Banco Central Europeo
    final String WEB = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    //Nombre del fichero a escribir con el contenido descargado de la página
    final String TMP = "cambio.txt";

    AsyncHttpClient cliente;

    //Controles de la vista de la actividad
    RadioButton deDolarAEuro;
    RadioButton deEuroADolar;
    EditText Euros;
    EditText Dolares;
    Button btnCalcular;
    double cambioDivisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__tres);

        deDolarAEuro = (RadioButton)findViewById(R.id.rbtDolarEuro_ej6);
        deEuroADolar = (RadioButton)findViewById(R.id.rbtEuroDolar_ej6);
        Euros = (EditText)findViewById(R.id.tbxEuro);
        Dolares = (EditText)findViewById(R.id.tbxDolar);
        btnCalcular = (Button)findViewById(R.id.btnCalcular);
        btnCalcular.setOnClickListener(this);
        cambioDivisa = 0;

        cliente = new AsyncHttpClient();

        ObtenerCambio();
    }

    /**
     * Metodo con el hilo que realiza la descarga y lectura de la página en XML para obtener el cambio de divisas
     */
    public void ObtenerCambio() {
        final ProgressDialog progreso = new ProgressDialog(this);
        final File miFichero = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), TMP);
        RequestHandle requestHandle = cliente.get(WEB, new FileAsyncHttpResponseHandler(miFichero) {

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
                    //Se almacena en una variable de tipo 'double' el valor de cambio de euros a dólares
                    cambioDivisa = Analisis.obtenerCambioDolar(file);
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

    @Override
    public void onClick(View v) {
        //Si se ha elegido de Dólar a Euro, se multiplica por el valor y escribe el resultado
        if(deDolarAEuro.isChecked() && (!Dolares.getText().toString().equals("")) && cambioDivisa != 0){
            double dinero = Double.parseDouble(Dolares.getText().toString()) * cambioDivisa;
            Euros.setText("" + dinero);
        }
        //Si se ha elegido de Euro a Dólar, se divide por el valor y escribe el resultado
        else if(deEuroADolar.isChecked() && (!Euros.getText().toString().equals("")) && cambioDivisa != 0){
            double dinero = Double.parseDouble(Euros.getText().toString()) / cambioDivisa;
            Dolares.setText("" + dinero);
        }
        //Si el cambio de divisas es 0, no se puede realizar la operación
        else if (cambioDivisa == 0) {
            Toast.makeText(this, "No se puede realizar la operación, ya que el cambio de divisas es 0", Toast.LENGTH_LONG).show();
        }
        //No se realiza ninguna operación si no se ha introducido ningún número
        else {
            Toast.makeText(this, "El cuadro de texto está vacío", Toast.LENGTH_SHORT).show();
        }
    }
}