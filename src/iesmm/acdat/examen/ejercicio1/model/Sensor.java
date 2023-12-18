package iesmm.acdat.examen.ejercicio1.model;

import java.io.Serializable;

public class Sensor implements Serializable {

    private int temperatura;
    private float latitud, longitud;

    public Sensor(int temperatura, float latitud, float longitud) {
        this.temperatura = temperatura;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    @Override
    public String toString() {
        return temperatura + " - LAT: " + latitud + " - LONG: " + longitud;
    }
}
