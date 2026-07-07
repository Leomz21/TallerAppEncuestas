package com.example.taller2android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taller2android.SurveyContract.PreguntasEntry;
import com.example.taller2android.SurveyContract.RespuestasEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pantalla de la encuesta. Las preguntas NO están quemadas en el XML:
 * se consultan de la tabla "preguntas" en SQLite y los componentes
 * visuales se generan dinámicamente según la cantidad de preguntas.
 */
public class MainActivity extends AppCompatActivity {

    private SurveyDbHelper dbHelper;
    private LinearLayout contenedorPreguntas;

    // Vista de respuesta (RadioGroup o EditText) asociada al id de cada pregunta
    private final Map<Integer, View> vistasRespuesta = new LinkedHashMap<>();

    // Opciones de respuesta cerrada por id de pregunta. Es solo configuración de
    // presentación: las preguntas en sí siempre se leen de la base de datos.
    // Las preguntas sin opciones se responden con un EditText (respuesta abierta).
    private final Map<Integer, String[]> opcionesPorPregunta = new HashMap<>();

    /** Una pregunta leída de la base de datos. */
    private static class Pregunta {
        final int id;
        final String texto;

        Pregunta(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        opcionesPorPregunta.put(1, new String[]{"Excelente", "Buena", "Regular", "Mala"});
        opcionesPorPregunta.put(2, new String[]{"Sí", "No"});
        opcionesPorPregunta.put(3, new String[]{"Excelente", "Buena", "Regular", "Mala"});
        opcionesPorPregunta.put(4, new String[]{"Sí", "No", "Parcialmente"});

        dbHelper = new SurveyDbHelper(this);
        contenedorPreguntas = findViewById(R.id.contenedorPreguntas);

        construirFormulario();

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardarEncuesta());
    }

    /** Consulta la tabla de preguntas y devuelve la lista de preguntas. */
    private List<Pregunta> obtenerPreguntas() {
        List<Pregunta> preguntas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                PreguntasEntry.TABLE_NAME,
                new String[]{PreguntasEntry.COLUMN_ID_PREG, PreguntasEntry.COLUMN_TEXTO},
                null, null, null, null,
                PreguntasEntry.COLUMN_ID_PREG + " ASC");
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(PreguntasEntry.COLUMN_ID_PREG));
                String texto = cursor.getString(cursor.getColumnIndexOrThrow(PreguntasEntry.COLUMN_TEXTO));
                preguntas.add(new Pregunta(id, texto));
            }
        } finally {
            cursor.close();
        }
        return preguntas;
    }

    /** Genera dinámicamente un TextView por pregunta y su vista de respuesta. */
    private void construirFormulario() {
        contenedorPreguntas.removeAllViews();
        vistasRespuesta.clear();

        for (Pregunta pregunta : obtenerPreguntas()) {
            TextView tvPregunta = new TextView(this);
            tvPregunta.setText(pregunta.texto);
            tvPregunta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            tvPregunta.setTypeface(tvPregunta.getTypeface(), Typeface.BOLD);
            tvPregunta.setPadding(0, dp(20), 0, dp(4));
            contenedorPreguntas.addView(tvPregunta);

            String[] opciones = opcionesPorPregunta.get(pregunta.id);
            View vistaRespuesta;
            if (opciones != null) {
                RadioGroup grupo = new RadioGroup(this);
                grupo.setOrientation(RadioGroup.VERTICAL);
                for (String opcion : opciones) {
                    RadioButton radio = new RadioButton(this);
                    radio.setText(opcion);
                    grupo.addView(radio);
                }
                vistaRespuesta = grupo;
            } else {
                EditText campo = new EditText(this);
                campo.setHint(getString(R.string.hint_respuesta));
                campo.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                campo.setMinLines(2);
                vistaRespuesta = campo;
            }
            contenedorPreguntas.addView(vistaRespuesta, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            vistasRespuesta.put(pregunta.id, vistaRespuesta);
        }
    }

    /** Valida y guarda todas las respuestas en la tabla "respuestas". */
    private void guardarEncuesta() {
        Map<Integer, String> respuestas = new LinkedHashMap<>();
        for (Map.Entry<Integer, View> entrada : vistasRespuesta.entrySet()) {
            View vista = entrada.getValue();
            String respuesta = "";
            if (vista instanceof RadioGroup) {
                RadioGroup grupo = (RadioGroup) vista;
                int seleccionado = grupo.getCheckedRadioButtonId();
                if (seleccionado != -1) {
                    RadioButton radio = grupo.findViewById(seleccionado);
                    respuesta = radio.getText().toString();
                }
            } else if (vista instanceof EditText) {
                respuesta = ((EditText) vista).getText().toString().trim();
            }
            if (respuesta.isEmpty()) {
                Toast.makeText(this, R.string.msg_responda_todo, Toast.LENGTH_SHORT).show();
                return;
            }
            respuestas.put(entrada.getKey(), respuesta);
        }

        // Todas las respuestas de una misma encuesta comparten la fecha de registro
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, String> entrada : respuestas.entrySet()) {
                ContentValues valores = new ContentValues();
                valores.put(RespuestasEntry.COLUMN_ID_PREG_FK, entrada.getKey());
                valores.put(RespuestasEntry.COLUMN_RESPUESTA, entrada.getValue());
                valores.put(RespuestasEntry.COLUMN_FECHA, fecha);
                db.insert(RespuestasEntry.TABLE_NAME, null, valores);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // Al guardar con éxito se muestra la pantalla de confirmación
        startActivity(new Intent(this, ExitoActivity.class));
        finish();
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
