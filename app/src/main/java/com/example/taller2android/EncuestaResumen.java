package com.example.taller2android;

/** Una encuesta guardada: su fecha/hora y el resumen de las respuestas. */
public class EncuestaResumen {

    private final String fecha;
    private final String resumen;

    public EncuestaResumen(String fecha, String resumen) {
        this.fecha = fecha;
        this.resumen = resumen;
    }

    public String getFecha() {
        return fecha;
    }

    public String getResumen() {
        return resumen;
    }
}
