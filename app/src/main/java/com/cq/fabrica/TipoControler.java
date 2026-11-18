package com.cq.fabrica;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import java.text.NumberFormat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.Locale;

public class TipoControler extends Pagina{
    private View root;
    private TextInputEditText editNome;
    private TextInputEditText editValor;
    private TableLayout tabela;

    private Dados db;

    public TipoControler(View root, Dados db) {
        this.root = root;
        this.db = db;
        editNome = root.findViewById(R.id.nomeTipo);
        editValor = root.findViewById(R.id.editTextNumberDecimal);
        tabela = root.findViewById(R.id.tabela);

    }

    public void atualizarTabela() {
            // 1. Limpa a tabela (como o cabeçalho agora é fixo no XML, limpamos tudo)
            if (tabela.getChildCount() > 1) {
                tabela.removeViews(1, tabela.getChildCount() - 1);
            }
            Cursor cursor = db.mostrarTTipo();

            if (cursor.moveToFirst()) {
                do {
                    final int id = cursor.getInt(0);
                    String nome = cursor.getString(1);
                    double valor = cursor.getDouble(2);

                    // 2. Cria a linha da tabela
                    TableRow row = new TableRow(root.getContext());
                    row.setPadding(0, 8, 0, 8);
                    row.setGravity(android.view.Gravity.CENTER_VERTICAL);

                    // 3. Campo de Texto para o Nome COM BORDA
                    TextInputEditText etNomeRow = new TextInputEditText(root.getContext());
                    etNomeRow.setText(nome);
                    etNomeRow.setGravity(android.view.Gravity.CENTER);
                    etNomeRow.setTextSize(14);
                    etNomeRow.setBackgroundResource(R.drawable.edittext_border); // Aplica a borda aqui
                    
                    TableRow.LayoutParams paramsNome = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
                    paramsNome.setMargins(8, 4, 8, 4); // Margens para a borda aparecer bem
                    etNomeRow.setLayoutParams(paramsNome);

                    // 4. Campo de Texto para o Valor COM BORDA
                    TextInputEditText etValorRow = new TextInputEditText(root.getContext());
                    // Multiplicamos por 100 para que a máscara formate corretamente os centavos na exibição inicial
                    etValorRow.setText(String.format(Locale.US, "%.0f", valor * 100));
                    etValorRow.setTextSize(14);
                    etValorRow.setGravity(android.view.Gravity.CENTER);
                    etValorRow.setBackgroundResource(R.drawable.edittext_border);
                    
                    // Aplicamos a máscara aqui (ela já define o InputType correto internamente)
                    configurarMascara(etValorRow);
                    
                    TableRow.LayoutParams paramsValor = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
                    paramsValor.setMargins(8, 4, 8, 4);
                    etValorRow.setLayoutParams(paramsValor);

                    // 5. Botão OK
                    Button btnAcao = new Button(root.getContext());
                    btnAcao.setText("OK");
                    btnAcao.setTextColor(Color.WHITE);
                    btnAcao.setTextSize(12);
                    btnAcao.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                            root.getContext().getResources().getColor(R.color.success)));
                    
                    TableRow.LayoutParams paramsOk = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
                    paramsOk.setMargins(4, 4, 4, 4);
                    btnAcao.setLayoutParams(paramsOk);

                    // 6. Botão X
                    Button btnDeletar = new Button(root.getContext());
                    btnDeletar.setText("X");
                    btnDeletar.setTextColor(Color.WHITE);
                    btnDeletar.setTextSize(12);
                    btnDeletar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                            root.getContext().getResources().getColor(R.color.error)));
                    
                    TableRow.LayoutParams paramsDel = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
                    paramsDel.setMargins(4, 4, 8, 4);
                    btnDeletar.setLayoutParams(paramsDel);

                    // Lógica do botão Editar
                    btnAcao.setOnClickListener(v -> {
                        String novoNome = etNomeRow.getText().toString();
                        String novoValorStr = etValorRow.getText().toString();
                        if (!novoNome.isEmpty() && !novoValorStr.isEmpty()) {
                            try {
                                // Usamos Locale PT-BR para ler a vírgula da máscara corretamente
                                NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
                                double novoValor = nf.parse(novoValorStr).doubleValue();
                                db.editarTipo(id, novoNome, novoValor);
                                Toast.makeText(root.getContext(), "Atualizado!", Toast.LENGTH_SHORT).show();
                                etNomeRow.clearFocus();
                                etValorRow.clearFocus();
                            } catch (Exception e) {
                                Toast.makeText(root.getContext(), "Erro ao salvar", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                    // Lógica do botão Deletar
                    btnDeletar.setOnClickListener(v -> {
                        new AlertDialog.Builder(root.getContext())
                                .setTitle("Excluir")
                                .setMessage("Deseja realmente excluir este tipo?")
                                .setPositiveButton("Sim", (dialog, which) -> {
                                    db.deletarTipo(id);
                                    atualizarTabela();
                                })
                                .setNegativeButton("Não", null)
                                .show();
                    });

                    row.addView(etNomeRow);
                    row.addView(etValorRow);
                    row.addView(btnAcao);
                    row.addView(btnDeletar);
                    tabela.addView(row);

                } while (cursor.moveToNext());
            }
            cursor.close();
    }

    public void salvarTipo(){
        Button button = root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editNome.getText().toString().trim();
                String valorStr = editValor.getText().toString().trim();

                if (!nome.isEmpty() && !valorStr.isEmpty()) {
                    try {
                        // Usamos Locale PT-BR para ler a vírgula da máscara corretamente
                        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
                        double valor = nf.parse(valorStr).doubleValue();
                        
                        Dados db = new Dados(root.getContext());
                        db.addTipo(nome, valor);
                        
                        editNome.setText("");
                        editValor.setText("");
                        Toast.makeText(root.getContext(), "Tipo salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        atualizarTabela();
                        
                    } catch (ParseException e) {
                        Toast.makeText(root.getContext(), "Erro ao processar valor!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(root.getContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configurarMascara(TextInputEditText editText) {
        // Permitir decimais no teclado para que a máscara consiga inserir a vírgula
        editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        TextWatcher watcher = new TextWatcher() {
            private String current = "";

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(current)){
                    editText.removeTextChangedListener(this);
                    
                    String stringLimpa = s.toString().replaceAll("[^0-9]", "");
                    
                    if (!stringLimpa.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(stringLimpa) / 100.0;
                            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
                            nf.setMinimumFractionDigits(2);
                            String formatted = nf.format(parsed);

                            current = formatted;
                            editText.setText(formatted);
                            editText.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                        }
                    } else {
                        current = "";
                        editText.setText("");
                    }
                    
                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        
        editText.addTextChangedListener(watcher);
        
        // Forçar a formatação inicial se já houver texto (necessário para a edição na tabela)
        if (editText.getText() != null && !editText.getText().toString().isEmpty()) {
            watcher.afterTextChanged(editText.getText());
        }
    }

    @Override
    public void carregarPagina() {
        configurarMascara(editValor);
        atualizarTabela();
        // MUITO IMPORTANTE: Chame o método aqui para ativar o clique do botão
        salvarTipo();
    }
}
