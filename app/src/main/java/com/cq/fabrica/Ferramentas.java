package com.cq.fabrica;

import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Ferramentas {
    private static final List<Integer> tiposId = new ArrayList<>();
    public static void configurarCalendario(TextInputEditText campoDeTexto, View root){
        campoDeTexto.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione a data")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                // CORREÇÃO: O MaterialDatePicker retorna milissegundos em UTC.
                // Para não haver erro de fuso horário, forçamos o SimpleDateFormat a usar UTC.
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                campoDeTexto.setText(format.format(new java.util.Date(selection)));
            });

            if (root.getContext() instanceof FragmentActivity) {
                datePicker.show(((FragmentActivity) root.getContext()).getSupportFragmentManager(), "DATE_PICKER");
            }
        });
    }
    public static void CarregarTipos( AutoCompleteTextView tipo, View root, Dados db){
        tiposId.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                root.getContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );

        try (Cursor cursor = db.mostrarTTipo()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    adapter.add(cursor.getString(1));
                    tiposId.add(cursor.getInt(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tipo.setAdapter(adapter);
    }

    public static String convertaData(String dataBr) {
        try {
            SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dbFormat.format(brFormat.parse(dataBr));
        } catch (Exception e) {
            return dataBr;
        }
    }

    public List<Integer> getTiposId(){
        return tiposId;
    }
}
