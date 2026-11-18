package com.cq.fabrica;

import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TaloesControler extends Pagina {
    private TableLayout tabela;
    private TextView valorTotal;
    private TextView quantidadeTotal;
    private Button fechar;
    private RadioGroup radioGrup;
    private View root;
    private Dados db;

    public TaloesControler(View view, Dados db) {
        this.root = view;
        this.tabela = view.findViewById(R.id.tabela);
        this.valorTotal = view.findViewById(R.id.valorTotal);
        this.quantidadeTotal = view.findViewById(R.id.quantidadeTotal);
        this.fechar = view.findViewById(R.id.fechar);
        this.radioGrup = view.findViewById(R.id.radioGrup);
        this.db = db;
    }

    public void AtualizarTabela() {
        double vTotal = 0.0;
        int qTotal = 0;
        Cursor cursor = null;
        
        if (tabela.getChildCount() > 1) {
            tabela.removeViews(1, tabela.getChildCount() - 1);
        }
        
        int idRadio = radioGrup.getCheckedRadioButtonId();
        if (idRadio == R.id.radioQ) {
            vTotal = db.valorT();
            qTotal = db.quantidadeT();
            cursor = db.mostrarTTalao();
        } else if (idRadio == R.id.radioS) {
            LocalDate hoje = LocalDate.now();
            LocalDate segundaFeira = hoje.with(DayOfWeek.MONDAY);
            vTotal = db.valorPorTempo(segundaFeira, hoje);
            qTotal = db.quantidadePorTempo(segundaFeira, hoje);
            cursor = db.mostrarTalaoPorTempo(segundaFeira, hoje);
        } else if (idRadio == R.id.radioD) {
            LocalDate hoje = LocalDate.now();
            vTotal = db.valorPorTempo(hoje, hoje);
            qTotal = db.quantidadePorTempo(hoje, hoje);
            cursor = db.mostrarTalaoPorTempo(hoje, hoje);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(2);
        this.valorTotal.setText(nf.format(vTotal));
        this.quantidadeTotal.setText(String.valueOf(qTotal));

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                DateTimeFormatter formatterBR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                do {
                    int id = cursor.getInt(0);
                    String dataOriginal = cursor.getString(1);
                    String modelo = cursor.getString(2);
                    String tipo = cursor.getString(6);
                    String quantidade = cursor.getString(4);

                    // Formatação da data para BR
                    String dataFormatada = dataOriginal;
                    try {
                        LocalDate localDate = LocalDate.parse(dataOriginal);
                        dataFormatada = localDate.format(formatterBR);
                    } catch (Exception e) {}

                    TableRow row = new TableRow(root.getContext());
                    row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                    row.setMinimumHeight(100);

                    TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
                    TableRow.LayoutParams params08 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.8f);
                    TableRow.LayoutParams params04 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.4f);

                    TextView etDataRow = new TextView(root.getContext());
                    etDataRow.setText(dataFormatada);
                    etDataRow.setGravity(android.view.Gravity.CENTER);
                    etDataRow.setBackgroundResource(R.drawable.borda_linha);
                    etDataRow.setLayoutParams(params1);

                    TextView etModeloRow = new TextView(root.getContext());
                    etModeloRow.setText(modelo);
                    etModeloRow.setGravity(android.view.Gravity.CENTER);
                    etModeloRow.setBackgroundResource(R.drawable.borda_linha);
                    etModeloRow.setLayoutParams(params1);

                    TextView etTipoRow = new TextView(root.getContext());
                    etTipoRow.setText(tipo);
                    etTipoRow.setGravity(android.view.Gravity.CENTER);
                    etTipoRow.setBackgroundResource(R.drawable.borda_linha);
                    etTipoRow.setLayoutParams(params1);

                    TextView etQuantRow = new TextView(root.getContext());
                    etQuantRow.setText(quantidade);
                    etQuantRow.setGravity(android.view.Gravity.CENTER);
                    etQuantRow.setBackgroundResource(R.drawable.borda_linha);
                    etQuantRow.setLayoutParams(params08);

                    Button btnEditar = new Button(root.getContext());
                    btnEditar.setBackgroundResource(R.drawable.lapis);
                    btnEditar.setLayoutParams(params04);
                    btnEditar.setOnClickListener(v -> {
                        if (root.getContext() instanceof MainActivity) {
                            ((MainActivity) root.getContext()).EditarVesivel(id);
                        }
                    });

                    row.addView(etDataRow);
                    row.addView(etModeloRow);
                    row.addView(etTipoRow);
                    row.addView(etQuantRow);
                    row.addView(btnEditar);
                    tabela.addView(row);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @Override
    public void carregarPagina() {
        this.radioGrup.setOnCheckedChangeListener((group, checkedId) -> AtualizarTabela());
        this.radioGrup.check(R.id.radioQ);
        
        fechar.setOnClickListener(v -> {
            if (root.getContext() instanceof MainActivity) {
                ((MainActivity) root.getContext()).finalizarVisivel();
            }
        });
    }
}
