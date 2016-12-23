package com.proyectos.javi.json_jm;

import android.os.Environment;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clae auxiliar para todos los ejercicios, engargada de manejar los contenidos en JSON y XML
 */
public class Analisis {

    /**
     * Método que analiza un documento descargado en JSON para obtener la información
     * necesaria sobre la predicción del estado de una ciudad en tiempo real
     *
     * Creado para: Ejercicio 1
     *
     * @param jsonObj Objeto de tipo JSON que contiene el contenido del fichero web descargado
     * @return Devuelve un objeto de tipo 'EstadoCiudad' con toda la información leída del fichero JSON
     * @throws JSONException
     */
    public static EstadoCiudad analizarEjercicioUno(JSONObject jsonObj) throws JSONException {
        EstadoCiudad estado = new EstadoCiudad();

        //Nombre
        estado.nombre = jsonObj.getString("name");

        //Coordenadas
        JSONObject cTmp = jsonObj.getJSONObject("coord");
        estado.longitud = cTmp.getDouble("lon");
        estado.latitud = cTmp.getDouble("lat");

        //Estado del cielo
        JSONArray jsonWeather = jsonObj.getJSONArray("weather");
        JSONObject wTmp = jsonWeather.getJSONObject(0);
        estado.estadoCielo = wTmp.getString("description");
        estado.imagen = wTmp.getString("icon");

        //Info
        JSONObject mTmp = jsonObj.getJSONObject("main");
        estado.temperaturaMaxima = mTmp.getDouble("temp_max");
        estado.temperaturaMinima = mTmp.getDouble("temp_min");
        estado.humedad = mTmp.getDouble("humidity");
        estado.presion = mTmp.getDouble("pressure");

        //Viento
        JSONObject vTmp = jsonObj.getJSONObject("wind");
        estado.velocidadViento = vTmp.getInt("speed");

        //Sistema
        JSONObject sTmp = jsonObj.getJSONObject("sys");
        estado.pais = sTmp.getString("country");

        return estado;
    }

    /**
     * Método que analiza un documento descargado en JSON para obtener la información
     * necesaria sobre la predicción del tiempo para los próximos siete días de una ciudad
     * cualquiera, introducida por el usuario
     *
     * Creado para: Ejercicio 2
     *
     * @param response Objeto de tipo JSON que contiene el contenido del fichero web descargado
     * @return Devuelve una lista de objetos tipo 'Prediccion' con toda la información leída del fichero JSON
     * @throws JSONException
     */
    public static ArrayList<Prediccion> analizarPredicciones(JSONObject response) throws JSONException {
        ArrayList<Prediccion> preds = new ArrayList<>();


        JSONArray arrayDatos = response.getJSONArray("list");

        for (int i = 0; i< arrayDatos.length(); i++){
            Prediccion prd = new Prediccion();

            JSONObject datos = arrayDatos.getJSONObject(i);
            JSONObject temperatura = datos.getJSONObject("temp");

            prd.temperaturaMax = temperatura.getDouble("max");
            prd.temperaturaMax = temperatura.getDouble("min");
            prd.presion = datos.getDouble("pressure");
            prd.fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date(datos.getLong("dt") * 1000));

            preds.add(prd);
        }
        return preds;
    }

    /**
     * Método que analiza un documento descargado en JSON para obtener el nombre y el país de una
     * ciudad cualquiera, introducida por el usuario, para la cual se van a obtener predicciones de tiempo
     *
     * Creado para: Ejercicio 2
     *
     * @param response Objeto de tipo JSON que contiene el contenido del fichero web descargado
     * @return Devuelve un objeto de tipo 'CiudadPrediccion' con el nombre de la ciudad y su país
     * @throws JSONException
     */
    public static CiudadPrediccion analizarCiudadPredicciones(JSONObject response) throws JSONException {
        CiudadPrediccion cp = new CiudadPrediccion();
        JSONObject ciudad = response.getJSONObject("city");
        cp.nombre = ciudad.getString("name");
        cp.pais = ciudad.getString("country");

        return cp;
    }

    /**
     * Método que recoge toda la información de una lista de predicciones, las convierte en una cadena
     * y escribe el contenido en un fichero de tipo JSON para su lectura
     *
     * Creado para: Ejercicio 2
     *
     * @param listaPredicciones Lista de objetos tipo 'Prediccion' que contiene todos los datos necesarios
     * @param fichero_json Nombre del fichero a escribir en memoria externa
     * @throws IOException
     */
    public static void almacenarJSON(ArrayList<Prediccion> listaPredicciones, String fichero_json) throws IOException {
        OutputStreamWriter out;
        File miFichero;
        File tarjeta;
        String contenido;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero_json);
        out = new FileWriter(miFichero);

        contenido = gson.toJson(listaPredicciones);
        out.write(contenido);

        out.close();
    }

    /**
     * Método que recoge toda la información de una lista de predicciones de una ciudad, las convierte en una cadena
     * y escribe el contenido en un fichero de tipo XML para su lectura
     *
     * Creado para: Ejercicio 2
     *
     * @param listaPredicciones Lista de objetos tipo 'Prediccion' que contiene todos los datos necesarios
     * @param fichero_xml Nombre del fichero a escribir en memoria externa
     * @param cpred Objeto de tipo 'CiudadPrediccion' que contiene el nombre y el país de la ciudad de la cual
     *              se han realizado las predicciones
     * @throws IOException
     */
    public static void almacenarXML(ArrayList<Prediccion> listaPredicciones, String fichero_xml, CiudadPrediccion cpred) throws IOException {
        File miFichero;
        File tarjeta;
        tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero_xml);

        FileOutputStream escritorXML = new FileOutputStream(miFichero);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(escritorXML,"UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        serializer.startTag(null, "predicciones");
        serializer.startTag(null, "ciudad");
        serializer.attribute(null, "pais", cpred.pais);
        serializer.text(cpred.nombre);
        serializer.endTag(null, "ciudad");
        for (Prediccion prediccion : listaPredicciones) {
            serializer.startTag(null, "prediccion");

            serializer.startTag(null, "fecha");
            serializer.text(prediccion.fecha);
            serializer.endTag(null, "fecha");

            serializer.startTag(null, "tmpMin");
            serializer.attribute(null, "und", "Celsius_ºC");
            serializer.text(String.valueOf(prediccion.temperaturaMin));
            serializer.endTag(null, "tmpMin");

            serializer.startTag(null, "tmpMax");
            serializer.attribute(null, "und", "Celsius_ºC");
            serializer.text(String.valueOf(prediccion.temperaturaMax));
            serializer.endTag(null, "tmpMax");

            serializer.startTag(null, "presion");
            serializer.attribute(null, "und", "Hectopascal_hPa");
            serializer.text(String.valueOf(prediccion.presion));
            serializer.endTag(null, "presion");

            serializer.endTag(null, "prediccion");
        }
        serializer.endTag(null, "predicciones");
        serializer.endDocument();
        serializer.flush();
        escritorXML.close();
    }

    /**
     * Método que lee un fichero web del Banco Central Europeo sobre cambios de divisas para obtener
     * el valor en tiempo real de conversión entre euros y dólares
     *
     * Creado para: Ejercicio 3 y Ejercicio 4
     *
     * @param file Fichero en el que ha sido almacenado el documento XML que hay que leer para obtener el valor de cambio
     * @return Devuelve el valor de cambio de euros a dólares
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static double obtenerCambioDolar(File file) throws IOException, XmlPullParserException {
        double cambio = 0;

        int eventType;
        XmlPullParser xrp = Xml.newPullParser();
        xrp.setInput(new FileReader(file));
        eventType = xrp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser. START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(xrp.getName().equalsIgnoreCase("Cube")) {
                        if (xrp.getAttributeCount() == 2) {
                            if (xrp.getAttributeValue(0).equals("USD")) {
                                cambio = Double.parseDouble(xrp.getAttributeValue(1));
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xrp.next();
        }
        return cambio;
    }

    /**
     * Método que lee un fichero web del Banco Central Europeo sobre cambios de divisas para obtener
     * el valor en tiempo real de conversión entre euros y yenes
     *
     * Creado para: Ejercicio 4
     *
     * @param file Fichero en el que ha sido almacenado el documento XML que hay que leer para obtener el valor de cambio
     * @return Devuelve el valor de cambio de euros a yenes
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static double obtenerCambioYen(File file) throws IOException, XmlPullParserException {
        double cambio = 0;

        int eventType;
        XmlPullParser xrp = Xml.newPullParser();
        xrp.setInput(new FileReader(file));
        eventType = xrp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser. START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(xrp.getName().equalsIgnoreCase("Cube")) {
                        if (xrp.getAttributeCount() == 2) {
                            if (xrp.getAttributeValue(0).equals("JPY")) {
                                cambio = Double.parseDouble(xrp.getAttributeValue(1));
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xrp.next();
        }
        return cambio;
    }

    /**
     * Método que lee de un contenido JSON descargado para obtener información relevante sobre una Bitcoin
     *
     * Creado para: Ejercicio 4
     *
     * @param response Objeto de tipo JSON que contiene el texto que hay que analizar con los datos sobre una Bitcoin
     * @return Devuelve un objeto de tipo 'Bitcoin' con los datos obtenidos del documento para su uso
     * @throws JSONException
     */
    public static Bitcoin analizarEjercicioCuatro(JSONObject response) throws JSONException {
        Bitcoin moneda = new Bitcoin();

        //BTC
        JSONObject tmp = response.getJSONObject("BTC");
        moneda.valorEnDolar = tmp.getString("USD");
        moneda.valorEnOroOnza = tmp.getDouble("Ounces");
        moneda.valorEnOroGramo = tmp.getDouble("Grams");
        moneda.valorEnPlataOnza = tmp.getDouble("SilverOunces");
        moneda.valorEnPlataGramo = tmp.getDouble("SilverGrams");

        //Gold
        JSONObject tmp2 = response.getJSONObject("Gold");
        moneda.precioOroOnza = tmp2.getDouble("Ounces");
        moneda.precioOroGramo = tmp2.getDouble("Grams");

        //Silver
        JSONObject tmp3 = response.getJSONObject("Silver");
        moneda.precioPlataOnza = tmp3.getDouble("Ounces");
        moneda.precioPlataGramo = tmp3.getDouble("Grams");

        return moneda;
    }
}
