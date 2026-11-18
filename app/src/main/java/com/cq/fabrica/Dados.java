package com.cq.fabrica;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;

public class Dados extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "BD_Fabrica.db";
    private static final int VERSAO = 5;

    public Dados(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE tipo (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nome TEXT," +
                        "valor REAL)"
        );

        db.execSQL(
                "CREATE TABLE talao (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "data TEXT," +
                        "modelo TEXT," +
                        "tipo_id INTEGER," +
                        "quantidade INTEGER," +
                        "FOREIGN KEY(tipo_id) REFERENCES tipo(id) ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS talao");
        db.execSQL("DROP TABLE IF EXISTS tipo");
        onCreate(db);
    }

    public void addTalao(String data, String modelo, int tipo_id, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("data", data);
        values.put("modelo", modelo);
        values.put("tipo_id", tipo_id);
        values.put("quantidade", quantidade);
        db.insert("talao", null, values);
    }

    public void addTipo(String nome, double valor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("valor", valor);
        db.insert("tipo", null, values);
    }

    public Cursor procurarPorTalão(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM talao s JOIN tipo t ON s.tipo_id = t.id WHERE s.id = ?", new String[]{String.valueOf(id)});
    }
    public void editarTalao(int id, String data, String modelo, int tipo_id, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("data", data);
        values.put("modelo", modelo);
        values.put("tipo_id", tipo_id);
        values.put("quantidade", quantidade);
        db.update("talao", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deletarQuinzena(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM talao");
    }

    public void editarTipo(int id, String nome, double valor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("valor", valor);
        db.update("tipo", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deletarTalao(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("talao", "id = ?", new String[]{String.valueOf(id)});
    }

    public void deletarTipo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tipo", "id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor mostrarTTipo() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tipo", null);
    }

    public Cursor mostrarTTalao() {
        SQLiteDatabase db = this.getReadableDatabase();
        // CORREÇÃO: Ajustado alias 's' para vir depois do nome da tabela
        return db.rawQuery("SELECT * FROM talao s JOIN tipo t ON s.tipo_id = t.id", null);
    }

    public Cursor mostrarTalaoPorTempo(LocalDate dataInicio, LocalDate dataFim) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dataInicioString = dataInicio.toString();
        String dataFimString = dataFim.toString();
        return db.rawQuery(
                "SELECT * FROM talao s JOIN tipo t ON s.tipo_id = t.id WHERE s.data BETWEEN ? AND ?",
                new String[]{dataInicioString, dataFimString}
        );
    }

    public Cursor buscarTalãoSeparadoTipo(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM talao s JOIN tipo t ON s.tipo_id = t.id ORDER BY t.nome", null);
    }

    public Cursor mostrarValorPorTipo() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT SUM(t.valor * s.quantidade), t.nome FROM talao s JOIN tipo t ON s.tipo_id = t.id GROUP BY t.nome", null);

    }

    public double valorT() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(t.valor * s.quantidade) FROM talao s JOIN tipo t ON s.tipo_id = t.id", null
        );
        double vTotal = 0.0;
        if (cursor.moveToFirst()) vTotal = cursor.getDouble(0);
        cursor.close();
        return vTotal;
    }

    public int quantidadeT() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(quantidade) FROM talao", null);
        int qTotal = 0;
        if (cursor.moveToFirst()) qTotal = cursor.getInt(0);
        cursor.close();
        return qTotal;
    }

    public int quantidadePorTempo(LocalDate dataInicio, LocalDate dataFim) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dataInicioString = dataInicio.toString();
        String dataFimString = dataFim.toString();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(quantidade) FROM talao WHERE data BETWEEN ? AND ?",
                new String[]{dataInicioString, dataFimString}
        );
        int qTotal = 0;
        if (cursor.moveToFirst()) qTotal = cursor.getInt(0);
        cursor.close();
        return qTotal;
    }

    public double valorPorTempo(LocalDate dataInicio, LocalDate dataFim) {
        SQLiteDatabase db = this.getReadableDatabase();
        String dataInicioString = dataInicio.toString();
        String dataFimString = dataFim.toString();
        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(t.valor * s.quantidade),0) " +
                        "FROM talao s JOIN tipo t ON s.tipo_id = t.id " +
                        "WHERE s.data BETWEEN ? AND ?",
                new String[]{dataInicioString, dataFimString}
        );
        double vTotal = 0.0;
        if (cursor.moveToFirst()) vTotal = cursor.getDouble(0);
        cursor.close();
        return vTotal;
    }
}
