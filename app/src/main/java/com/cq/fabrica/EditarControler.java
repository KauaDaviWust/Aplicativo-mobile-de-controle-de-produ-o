package com.cq.fabrica;

import android.database.Cursor;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditarControler extends Pagina{
    private View root;
    private Dados db;
    private AutoCompleteTextView tipo;
    private TextInputEditText modelo, data, quantidade;
    private TextView notifi;
    private Button editar;
    private Button excluir;

    int idSelecionado = -1;

    int idTalao;
    private Ferramentas ferramentas = new Ferramentas();

    public EditarControler(View root, Dados db) {
        this.root = root;
        this.db = db;
        this.tipo = root.findViewById(R.id.spinner);
        this.modelo = root.findViewById(R.id.mod);
        this.data = root.findViewById(R.id.editTextDate);
        this.quantidade = root.findViewById(R.id.quan);
        this.notifi = root.findViewById(R.id.notifi);
        this.editar = root.findViewById(R.id.editar);
        this.excluir = root.findViewById(R.id.excluir);
    }

    public void carregarDados(int id){
        Cursor cursor = db.procurarPorTalão(id);
        if (cursor.moveToFirst()) {
            this.idTalao = cursor.getInt(0); // Aqui o idTalao é atualizado
            tipo.setText(cursor.getString(6), false);
            idSelecionado = cursor.getInt(3);
            modelo.setText(cursor.getString(2));
            
            String dataBanco = cursor.getString(1);
            if (dataBanco != null && !dataBanco.isEmpty()) {
                try {
                    SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    java.util.Date parsedDate = dbFormat.parse(dataBanco);
                    if (parsedDate != null) {
                        data.setText(brFormat.format(parsedDate));
                    } else {
                        data.setText(dataBanco);
                    }
                } catch (Exception e) {
                    data.setText(dataBanco);
                }
            } else {
                data.setText("");
            }

            quantidade.setText(cursor.getString(4));
        }
        cursor.close();
    }

    public void editarTalao(){
        editar.setOnClickListener(v -> {
            String dataStr = data.getText() != null ? data.getText().toString().trim() : "";
            String modeloStr = modelo.getText() != null ? modelo.getText().toString().trim() : "";
            String quantidadeStr = quantidade.getText() != null ? quantidade.getText().toString().trim() : "";


            if (dataStr.isEmpty() || modeloStr.isEmpty() || quantidadeStr.isEmpty() || idSelecionado == -1) {
                notifi.setText("Preencha todos os campos corretamente!");
                return;
            }
            try{
                int quantidadeInt = Integer.parseInt(quantidadeStr);
                String dataParaBanco = ferramentas.convertaData(dataStr);
                db.editarTalao(idTalao, dataParaBanco, modeloStr, idSelecionado, quantidadeInt);
                notifi.setText("Salvo com sucesso!");
                if (root.getContext() instanceof MainActivity) {
                    ((MainActivity) root.getContext()).taloesVisivel();
                }
            }
            catch (NumberFormatException e) {
                notifi.setText("Erro: Quantidade deve ser um número!");
            } catch (Exception e) {
                notifi.setText("Erro ao salvar no banco.");
            }

        });
    }

    private void deletarTalão() {
        excluir.setOnClickListener(v -> {
            if (idTalao > 0) {
                db.deletarTalao(idTalao);
                if (root.getContext() instanceof MainActivity) {
                    ((MainActivity) root.getContext()).taloesVisivel();
                }
            }
        });
    }

    @Override
    public void carregarPagina() {


        tipo.setOnItemClickListener((parent, view, position, id) -> {
            this.idSelecionado = ferramentas.getTiposId().get(position);
        });
        notifi.setText("");
        ferramentas.configurarCalendario(data, root);
        ferramentas.CarregarTipos(tipo, root, db);
        editarTalao();
        deletarTalão();
    }
}
