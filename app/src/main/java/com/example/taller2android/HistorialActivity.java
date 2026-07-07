package com.example.taller2android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taller2android.SurveyContract.PreguntasEntry;
import com.example.taller2android.SurveyContract.RespuestasEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de historial: muestra en un RecyclerView todas las encuestas
 * guardadas, con su fecha/hora y el resumen de las respuestas dadas.
 */
public class HistorialActivity extends AppCompatActivity {

    private SurveyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        dbHelper = new SurveyDbHelper(this);

        List<EncuestaResumen> encuestas = obtenerEncuestas();

        RecyclerView rvHistorial = findViewById(R.id.rvHistorial);
        TextView tvVacio = findViewById(R.id.tvVacio);

        if (encuestas.isEmpty()) {
            tvVacio.setVisibility(View.VISIBLE);
            rvHistorial.setVisibility(View.GONE);
        } else {
            rvHistorial.setLayoutManager(new LinearLayoutManager(this));
            rvHistorial.setAdapter(new EncuestaAdapter(encuestas));
        }
    }

    /**
     * Consulta las respuestas junto con el texto de su pregunta y las agrupa
     * por fecha de registro: cada fecha corresponde a una encuesta guardada.
     */
    private List<EncuestaResumen> obtenerEncuestas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT r." + RespuestasEntry.COLUMN_FECHA + ", " +
                "p." + PreguntasEntry.COLUMN_TEXTO + ", " +
                "r." + RespuestasEntry.COLUMN_RESPUESTA + " " +
                "FROM " + RespuestasEntry.TABLE_NAME + " r " +
                "INNER JOIN " + PreguntasEntry.TABLE_NAME + " p " +
                "ON p." + PreguntasEntry.COLUMN_ID_PREG + " = r." + RespuestasEntry.COLUMN_ID_PREG_FK + " " +
                "ORDER BY r." + RespuestasEntry.COLUMN_FECHA + " DESC, " +
                "p." + PreguntasEntry.COLUMN_ID_PREG + " ASC";

        Map<String, StringBuilder> porFecha = new LinkedHashMap<>();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String fecha = cursor.getString(0);
                String pregunta = cursor.getString(1);
                String respuesta = cursor.getString(2);

                StringBuilder resumen = porFecha.get(fecha);
                if (resumen == null) {
                    resumen = new StringBuilder();
                    porFecha.put(fecha, resumen);
                } else {
                    resumen.append("\n");
                }
                resumen.append("• ").append(pregunta).append("  →  ").append(respuesta);
            }
        } finally {
            cursor.close();
        }

        List<EncuestaResumen> encuestas = new ArrayList<>();
        for (Map.Entry<String, StringBuilder> entrada : porFecha.entrySet()) {
            encuestas.add(new EncuestaResumen(entrada.getKey(), entrada.getValue().toString()));
        }
        return encuestas;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
