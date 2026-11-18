package com.cq.fabrica;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CriarPdf {
    private Dados db;
    private String tipoA = "";
    private PdfDocument document;
    private PdfDocument.PageInfo pageInfo;
    private PdfDocument.Page page;
    private Canvas canvas;
    private Paint paint = new Paint();
    private Locale localeBR = new Locale("pt", "BR");

    private int x = 40;
    private int y = 50;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public File gerarPdf(Context context, Dados db) {
        this.db = db;
        this.document = new PdfDocument();
        this.pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        this.page = document.startPage(pageInfo);
        this.canvas = page.getCanvas();
        this.y = 50;
        this.tipoA = "";

        DateTimeFormatter formatadorBR = DateTimeFormatter.ofPattern("dd/MM/yy");
        Cursor cursor = db.buscarTalãoSeparadoTipo();

        double valTipo = 0;
        int quanTipo = 0;
        boolean primeiro = true;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // CORREÇÃO: Coluna correta é "nome"
                String dataRaw = cursor.getString(cursor.getColumnIndexOrThrow("data"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                String modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"));
                int qtd = cursor.getInt(cursor.getColumnIndexOrThrow("quantidade"));
                double valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"));

                if (!tipo.equals(tipoA)) {
                    if (!primeiro) {
                        criarResumo(valTipo, quanTipo);
                        quebraDePagina();

                    }
                    valTipo = valor;
                    tipoA = tipo;

                    quanTipo = 0;
                    primeiro = false;

                    // VOLTANDO O TÍTULO GRANDE COM QUADRADO
                    y += 30;
                    paint.setColor(Color.BLACK);
                    paint.setStrokeWidth(0.5f);
                    canvas.drawLine(x, y + 4, 555, y + 4, paint);
                    canvas.drawLine(x, y - 25, 555, y - 25, paint);
                    canvas.drawLine(x, y + 20, x, y - 25, paint); 
                    canvas.drawLine(555, y + 20, 555, y - 25, paint);
                    
                    paint.setTextSize(25);
                    paint.setFakeBoldText(true);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(tipoA, 595 / 2f, y, paint);
                    paint.setTextAlign(Paint.Align.LEFT);

                    y += 20;
                    gerarCabecalho();
                }

                if (y > 740) {
                    quebraDePagina();
                    gerarCabecalho();
                }


                quanTipo += qtd;

                // VOLTANDO AS LINHAS COM BORDAS LATERAIS
                String dataFormatada = dataRaw;
                try {
                    LocalDate date = LocalDate.parse(dataRaw);
                    dataFormatada = date.format(formatadorBR);
                } catch (Exception e) {}

                paint.setTextSize(12);
                paint.setFakeBoldText(false);
                canvas.drawText(dataFormatada, x + 5, y, paint);
                canvas.drawText(tipo, x + 100, y, paint);
                canvas.drawText(modelo, x + 200, y, paint);
                canvas.drawText(String.valueOf(qtd), x + 465, y, paint);

                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(0.5f);
                canvas.drawLine(x, y + 5, 555, y + 5, paint);
                canvas.drawLine(x, y + 5, x, y - 40, paint); 
                canvas.drawLine(555, y + 5, 555, y - 40, paint); 
                y += 20;

            } while (cursor.moveToNext());
            
            criarResumo(valTipo, quanTipo);
            cursor.close();
        }

        criarResumoFinal();
        document.finishPage(page);

        File filePath = new File(context.getExternalFilesDir(null), "Relatorio_Producao.pdf");
        try {
            document.writeTo(new FileOutputStream(filePath));
            return filePath;
        } catch (IOException e) {
            return null;
        } finally {
            document.close();
        }
    }

    public void visualizarPdf(Context context, File arquivo) {
        if (arquivo == null || !arquivo.exists()) return;

        Uri uri = FileProvider.getUriForFile(context, "com.cq.fabrica.fileprovider", arquivo);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Instale um leitor de PDF", Toast.LENGTH_LONG).show();
        }
    }

    public void quebraDePagina() {
        document.finishPage(page);
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        y = 50;
    }

    public void gerarCabecalho() {
        paint.setTextSize(12);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(x, y - 15, 555, y + 5, paint);

        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        canvas.drawText("Data", x + 5, y, paint);
        canvas.drawText("Tipo", x + 100, y, paint);
        canvas.drawText("Modelo", x + 200, y, paint);
        canvas.drawText("Qtd", x + 465, y, paint);

        y += 25;
        paint.setFakeBoldText(false);
    }

    public void criarResumo(double valTipo, int quanTipo) {
        // VOLTANDO O RESUMO BOXEADO
        paint.setStrokeWidth(0.5f);
        paint.setColor(Color.GRAY);
        paint.setTextSize(9);
        paint.setFakeBoldText(true);
        canvas.drawText("Valor por par:",x + 245, y - 5, paint);
        canvas.drawText("Quantidade total:", x + 355, y - 5, paint);
        canvas.drawText("Valor total:", x + 445, y - 5, paint);
        
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);
        canvas.drawText(String.format(localeBR, "%.2f", valTipo), x + 245, y + 15, paint);
        canvas.drawText(String.valueOf(quanTipo), x + 355, y + 15, paint);
        canvas.drawText(String.format(localeBR, "%.2f", valTipo*quanTipo), x + 445, y + 15, paint);
        
        canvas.drawLine(x, y + 20, 555, y + 20, paint);

        canvas.drawLine(x + 243, y + 20, x + 243, y - 15, paint);
        canvas.drawLine(x + 353, y + 20, x + 353, y - 15, paint);
        canvas.drawLine(x + 443, y + 20, x + 443, y - 15, paint);

        canvas.drawLine(x, y + 20, x, y - 40, paint);
        canvas.drawLine(555, y + 20, 555, y - 40, paint);
        y += 20;
    }

    private void criarResumoFinal() {
        if (y > 600) quebraDePagina();
        y += 100;
        paint.setTextSize(39);
        paint.setFakeBoldText(true);
        canvas.drawText("Resumo Final", x, y, paint);
        y += 30;
        
        Cursor cursor = db.mostrarValorPorTipo();
        paint.setTextSize(15);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double valorTipo = cursor.getDouble(0);
                String tipo = cursor.getString(1);
                canvas.drawText(tipo + ": R$ " + String.format(localeBR, "%.2f", valorTipo), x, y, paint);
                y += 20;
            } while (cursor.moveToNext());
            cursor.close();
        }
        y += 40;
        canvas.drawText("Valor Total Geral: R$ " + String.format(localeBR, "%.2f", db.valorT()), x, y, paint);
    }
}
