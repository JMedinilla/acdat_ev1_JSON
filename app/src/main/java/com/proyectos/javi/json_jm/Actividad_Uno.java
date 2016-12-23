package com.proyectos.javi.json_jm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Clase Ciudad
 *
 * Almacena un nombre y un país para una ciudad
 */
class Ciudad {
    public String nombre;
    public String pais;

    @Override
    public String toString(){
            return nombre;
        }
}

/**
 * Actividad 1
 */
public class Actividad_Uno extends Activity {

    //Contexto de la actividad
    Context ctxt;
    //Lista para la vista de la actividad
    ListView mListV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__uno);

        ctxt = getApplicationContext();
        mListV = (ListView)findViewById(R.id.listaCiudades);

        //Lista de ciudades que serán cargadas en el menú de ciudades de la actividad
        ArrayList<Ciudad> lstCiudades = new ArrayList<>();
        String[] cius = new String[]{"Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga", "Murcia", "Bilbao", "Medinilla"};
        for (String ciu : cius) {
            Ciudad CDD = new Ciudad();
            CDD.nombre = ciu;
            CDD.pais = "es";
            lstCiudades.add(CDD);
        }

        //Se establece un adaptador simple para la lista de la vista principal
        mListV.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lstCiudades));

        //Se suscribe la lista a un evento a ejecutar en la pulsación de cada uno de sus elementos
        mListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ciudad cd = (Ciudad) mListV.getItemAtPosition(position);
                Intent in = new Intent(ctxt, DescargaEstadoCiudad.class);
                in.putExtra("ciudad", cd.nombre);
                in.putExtra("pais", cd.pais);
                in.putExtra("id", "2de143494c0b295cca9337e1e96b00e0");
                startActivity(in);
            }
        });
    }
}
