package com.cq.fabrica;

import android.view.View;
import android.widget.Button;

import java.io.File;

public class FinalizarControler extends Pagina {
    private View root;
    private Dados db;
    private Button visualizar;
    private Button excluir;
    private Button voltar;

    public FinalizarControler(View root, Dados db) {
        this.root = root;
        this.db = db;
        this.visualizar = root.findViewById(R.id.btn_visualizar_pdf);
        this.excluir = root.findViewById(R.id.btn_excluir_quinzena);
        this.voltar = root.findViewById(R.id.btn_voltar); 
    }

    @Override
    public void carregarPagina() {
        visualizar.setOnClickListener(v -> {
            CriarPdf criador = new CriarPdf();
            // CORREÇÃO: Usar root.getContext() em vez de 'this'
            File arquivo = criador.gerarPdf(root.getContext(), db);
            if (arquivo != null) {
                criador.visualizarPdf(root.getContext(), arquivo);
            }
        });
        excluir.setOnClickListener(v -> {
            db.deletarQuinzena();
            if (root.getContext() instanceof MainActivity) {
                ((MainActivity) root.getContext()).taloesVisivel();
            }
        });
        voltar.setOnClickListener(v -> {
            if (root.getContext() instanceof MainActivity) {
                ((MainActivity) root.getContext()).taloesVisivel();
            }
        });
    }
}
