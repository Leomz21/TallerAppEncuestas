package com.example.taller2android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla de confirmación: informa que la encuesta se completó con éxito
 * y ofrece revisar el historial o realizar otra encuesta.
 */
public class ExitoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exito);

        Button btnOtraEncuesta = findViewById(R.id.btnOtraEncuesta);
        btnOtraEncuesta.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        Button btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnVerHistorial.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialActivity.class)));
    }
}
