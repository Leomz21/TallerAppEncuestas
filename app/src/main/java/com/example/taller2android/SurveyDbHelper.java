package com.example.taller2android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper de la base de datos. Crea las tablas y, cumpliendo la "regla de oro",
 * inserta las preguntas de la encuesta automáticamente en onCreate(),
 * sin intervención del usuario ni de la UI.
 */
public class SurveyDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "encuestas.db";
    public static final int DATABASE_VERSION = 1;

    public SurveyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SurveyContract.SQL_CREATE_PREGUNTAS);
        db.execSQL(SurveyContract.SQL_CREATE_RESPUESTAS);

        // --- INSERTAR PREGUNTAS DIRECTAMENTE EN LA BASE ---
        String insertar = "INSERT INTO " + SurveyContract.PreguntasEntry.TABLE_NAME +
                " (" + SurveyContract.PreguntasEntry.COLUMN_ID_PREG + ", " +
                SurveyContract.PreguntasEntry.COLUMN_TEXTO + ") VALUES ";

        db.execSQL(insertar + "(1, '¿Cómo califica la atención recibida?')");
        db.execSQL(insertar + "(2, '¿Recomendaría nuestro servicio?')");
        db.execSQL(insertar + "(3, '¿Cómo califica las instalaciones de la institución?')");
        db.execSQL(insertar + "(4, '¿El personal resolvió sus dudas?')");
        db.execSQL(insertar + "(5, '¿Qué sugerencias tiene para mejorar el servicio?')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SurveyContract.SQL_DELETE_RESPUESTAS);
        db.execSQL(SurveyContract.SQL_DELETE_PREGUNTAS);
        onCreate(db);
    }
}
