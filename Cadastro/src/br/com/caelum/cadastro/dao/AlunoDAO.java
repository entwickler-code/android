package br.com.caelum.cadastro.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.com.caelum.cadastro.modelo.Aluno;

public class AlunoDAO extends SQLiteOpenHelper {

	// versao da tabela para marcar que alteramos algum detalhe no modelo
	private static final int VERSAO = 2;
	private static final String TABELA = "CadastroCaelum";
	private static final String DATABASE = "FJ57";
	
	private static final String[] COLUNAS = {"id", "nome", "telefone", "endereco", "site", "nota", "foto"};

	public AlunoDAO(Context context) {
		super(context, DATABASE, null, VERSAO);
	}
	
	public void onCreate(SQLiteDatabase database) {
		String ddl = "CREATE TABLE " + TABELA + " (" +
						" id INTEGER PRIMARY KEY, " +
						" nome TEXT UNIQUE NOT NULL, " +
						" telefone TEXT, " +
						" endereco TEXT, " +
						" site TEXT, " +
						" nota REAL, " +
						" foto TEXT" +
					 " );";
		database.execSQL(ddl);
	}

	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABELA;
		database.execSQL(sql);
		onCreate(database);
	}
	
	public List<Aluno> getLista() {
		List<Aluno> alunos = new ArrayList<Aluno>();
		
		Cursor c = getWritableDatabase().query(TABELA, COLUNAS, null, null, null, null, null);

		while (c.moveToNext()) {
			Aluno aluno = new Aluno();
			
			aluno.setId(c.getLong(0));
			aluno.setNome(c.getString(1));
			aluno.setTelefone(c.getString(2));
			aluno.setEndereco(c.getString(3));
			aluno.setSite(c.getString(4));
			aluno.setNota(c.getDouble(5));
			aluno.setFoto(c.getString(6));

			alunos.add(aluno);
		}
		c.close();

		return alunos;
	}	

	public void insere(Aluno aluno) {
		ContentValues values = toContentValues(aluno);
		long id = getWritableDatabase().insert(TABELA, null, values);
		aluno.setId(id);
	}
	
	public void alterar(Aluno aluno) {
		ContentValues values = toContentValues(aluno);
		String[] args = { aluno.getId().toString() };				
		getWritableDatabase().update(TABELA, values, "id=?", args);
	}	
	
	public void deletar(Aluno aluno) {
		String[] args = {aluno.getId().toString()};
		getWritableDatabase().delete(TABELA, "id=?", args);
	}
	
	public boolean isAluno(String telefone) {
		Cursor rawQuery = getReadableDatabase().rawQuery("SELECT telefone from " + TABELA + " WHERE telefone = ?", new String[]{ telefone });
		int total = rawQuery.getCount();
		rawQuery.close();
		return total > 0;
	}	
	
	private ContentValues toContentValues(Aluno aluno) {
		ContentValues values = new ContentValues();
		values.put(COLUNAS[1], aluno.getNome());
		values.put(COLUNAS[2], aluno.getTelefone());
		values.put(COLUNAS[3], aluno.getEndereco());
		values.put(COLUNAS[4], aluno.getSite());
		values.put(COLUNAS[5], aluno.getNota());
		values.put(COLUNAS[6], aluno.getFoto());
		return values;
	}

}