package com.proyectos.javi.json_jm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Botones del menú
    Button btnUnoMenu;
    Button btnDosMenu;
    Button btnTresMenu;
    Button btnCuatroMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ejercicio 1
        btnUnoMenu = (Button)findViewById(R.id.ej1);
        btnUnoMenu.setOnClickListener(this);
        //Ejercicio 2
        btnDosMenu = (Button)findViewById(R.id.ej2);
        btnDosMenu.setOnClickListener(this);
        //Ejercicio 3
        btnTresMenu = (Button)findViewById(R.id.ej3);
        btnTresMenu.setOnClickListener(this);
        //Ejercicio 4
        btnCuatroMenu = (Button)findViewById(R.id.ej4);
        btnCuatroMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent in = null;

        //Ejercicio 1
        if (v == btnUnoMenu) {
            in = new Intent(this, Actividad_Uno.class);
        }
        //Ejercicio 2
        if (v == btnDosMenu) {
            in = new Intent(this, Actividad_Dos.class);
        }
        //Ejercicio 3
        if (v == btnTresMenu) {
            in = new Intent(this, Actividad_Tres.class);
        }
        //Ejercicio 4
        if (v == btnCuatroMenu) {
            in = new Intent(this, Actividad_Cuatro.class);
        }

        startActivity(in);
    }
}
