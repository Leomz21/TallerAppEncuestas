package com.example.taller2android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Adaptador del RecyclerView del historial de encuestas. */
public class EncuestaAdapter extends RecyclerView.Adapter<EncuestaAdapter.EncuestaViewHolder> {

    private final List<EncuestaResumen> encuestas;

    public EncuestaAdapter(List<EncuestaResumen> encuestas) {
        this.encuestas = encuestas;
    }

    public static class EncuestaViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFecha;
        final TextView tvResumen;

        public EncuestaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvResumen = itemView.findViewById(R.id.tvResumen);
        }
    }

    @NonNull
    @Override
    public EncuestaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_encuesta, parent, false);
        return new EncuestaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EncuestaViewHolder holder, int position) {
        EncuestaResumen encuesta = encuestas.get(position);
        holder.tvFecha.setText(holder.itemView.getContext()
                .getString(R.string.item_fecha, encuesta.getFecha()));
        holder.tvResumen.setText(encuesta.getResumen());
    }

    @Override
    public int getItemCount() {
        return encuestas.size();
    }
}
