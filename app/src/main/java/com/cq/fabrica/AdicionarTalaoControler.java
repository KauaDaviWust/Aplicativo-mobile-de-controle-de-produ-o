package com.cq.fabrica;

import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AdicionarTalaoControler extends Pagina {
    private final View root;
    private final AutoCompleteTextView tipo;
    private final TextInputEditText modelo, data, quantidade;
    private final TextView notifi;
    private final Button adicionar;
    private final Dados db;
    private int idSelecionado = -1;

    private Ferramentas ferramentas = new Ferramentas();

    public AdicionarTalaoControler(View root, Dados db) {
        this.root = root;
        this.db = db;
        tipo = root.findViewById(R.id.spinner);
        modelo = root.findViewById(R.id.mod);
        data = root.findViewById(R.id.editTextDate);
        quantidade = root.findViewById(R.id.quan);
        notifi = root.findViewById(R.id.notifi);
        adicionar = root.findViewById(R.id.editar);


    }


    public void adicionarTalao() {
        adicionar.setOnClickListener(v -> {
            String dataStr = data.getText() != null ? data.getText().toString().trim() : "";
            String modeloStr = modelo.getText() != null ? modelo.getText().toString().trim() : "";
            String quantidadeStr = quantidade.getText() != null ? quantidade.getText().toString().trim() : "";

            if (dataStr.isEmpty() || modeloStr.isEmpty() || quantidadeStr.isEmpty() || idSelecionado == -1) {
                notifi.setText("Preencha todos os campos corretamente!");
                return;
            }

            try {
                int quantidadeInt = Integer.parseInt(quantidadeStr);
                
                // Conversão para o formato do banco (yyyy-MM-dd)
                String dataParaBanco = ferramentas.convertaData(dataStr);

                db.addTalao(dataParaBanco, modeloStr, idSelecionado, quantidadeInt);
                
                notifi.setText("Salvo com sucesso!");
                
                // Reseta os campos para uma melhor experiência (Adicionado reset de tipo e modelo)
                quantidade.setText("");



            } catch (NumberFormatException e) {
                notifi.setText("Erro: Quantidade deve ser um número!");
            } catch (Exception e) {
                notifi.setText("Erro ao salvar no banco.");
            }
        });
    }

    public AutoCompleteTextView getTipo() {
        return tipo;
    }

    @Override
    public void carregarPagina() {
        // Define a data de hoje no formato brasileiro ao abrir a tela
        String dataHoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        data.setText(dataHoje);

        notifi.setText("");

        // Captura o ID quando o usuário seleciona um tipo no Spinner
        tipo.setOnItemClickListener((parent, view, position, id) -> {
            this.idSelecionado = ferramentas.getTiposId().get(position);
        });

        ferramentas.configurarCalendario(data, root);
        ferramentas.CarregarTipos(tipo, root, db);
        adicionarTalao();
    }
}
