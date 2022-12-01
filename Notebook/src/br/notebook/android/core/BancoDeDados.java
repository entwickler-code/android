package br.notebook.android.core;

/* Classe responsável por manipular valores e dados do Banco */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

public class BancoDeDados extends SQLiteOpenHelper{
	

	private static final String DATABASE_NAME = "dados.db";
	
	public static final int SERVICO = 0;
	public static final int ATIVIDADE = 1;
	public static final int ATIVIDADE_SERVICO = 2;
	public static final int EQUIPES = 3;
	
	private Tabelas nomeTabela;
	private Context contexto;
	
	public BancoDeDados(Context context, Tabelas nomeTabela, int version) {
		super(context, DATABASE_NAME, null, version);
		this.nomeTabela = nomeTabela; 
		this.contexto = context;
	}
	
	public static List<Object> listaCampoEmBranco(List<Object> lista){
		List<Object> dados = new ArrayList<Object>();
		Map<String, String> branco = new HashMap<String, String>();
		branco.put("descricao", "Sem escolha");
		branco.put("contador", "");
		dados.add(branco);
		dados.addAll(lista);
		return dados;
	}
	
	@SuppressWarnings("unchecked")
	public static Object pegarValor(List<Object> lista, String[] campos, String[] valores){
		Object retorno = null;
		int certos;
		for(Object obj : lista){
			certos = 0; 
			for(int i = 0; i < campos.length; i++){
				String valor = ((Map<String, String>)obj).get(campos[i]);
				if(valor.equals(valores[i])){
					certos++;
				}
				if(certos == campos.length){
					return obj;
				}
			}
		}
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public static Object pegarValor(List<Object> lista, String campo, String valor){
		Object retorno = null;
		finalizar: for(Object obj : lista){
			for(Map.Entry<String, String> linha : ((Map<String, String>)obj).entrySet()){
				if(linha.getKey().equals(campo)){
					if(linha.getValue().equals(valor)){
						retorno = obj;
						break finalizar;
					}
				}
			}
		}
		return retorno;
	}
	
	
	@SuppressWarnings("unchecked")
	public static String pegarValor(Object obj, String campo){
		if(obj != null){
			for(Map.Entry<String, String> linha : ((Map<String, String>)obj).entrySet()){
				if(linha.getKey().equals(campo)){
					return linha.getValue();
				}
			}
		}
		return null;
	}
	
	public SQLiteDatabase pegaBancoDeDados(){
		return this.getWritableDatabase();
	}
	
	
	@SuppressWarnings("unchecked")
	public static String montaWhere(Tabelas nomeTabela, Object obj, String... chaves){
		List<Integer> campos = new ArrayList<Integer>();
		for(String chave : chaves){
			campos.add(Integer.parseInt(((Map<String, String>)obj).get(chave)));
		}
		return String.format(nomeTabela.whereSQL(), campos.toArray());
	}
	
	public List<Object> consultaDados(String[] campos, String condicao, String ordem){
		try {
			Map<String, String> box = null;
			List<Object> lista = new ArrayList<Object>();
			Cursor linhas = this.getWritableDatabase().query(nomeTabela.getNomeTabela(), campos, condicao, null, null, null, ordem);
			if(linhas.getCount() > 0){
				linhas.moveToFirst();
				while(!linhas.isAfterLast()){
					box = new HashMap<String, String>();
					for (int i = 0; i < campos.length; i++) {
						box.put(campos[i], linhas.getString(i));
					}
					lista.add(box);
					linhas.moveToNext();
				}
			}
			linhas.close();
			return lista; 
		} catch (Exception e) {
			ArrayList retorno = new ArrayList<Object>();
			retorno.add(null);
			return retorno;
		}
	}

	public static ArrayAdapter<String> montaAdaptador(Context context, List<Object> lista, String texto){
		return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, leituraDados(lista, texto)); 
	}
	
	public static String consertaHorario(String horario){
		String[] horarios = horario.split(":");
		int valor = 0;
		for(int i = 0; i < horarios.length; i++){
			try{
				valor = Integer.parseInt(horarios[i]);
				horarios[i] = (valor < 10) ? "0"+horarios[i] : horarios[i];
			}catch(Exception e){
			}
		}
		return horarios[0]+":"+horarios[1];
	}
	
	public static String montaData(int hora, int minuto){
		return consertaHorario(hora+":"+minuto);
	}
	
	@SuppressWarnings("unchecked")
	public static String[] leituraDados(List<Object> lista, String campo, String descricao){
		List<String> listagem = new ArrayList<String>();
		if(lista != null){
			for (Object obj : lista) {
				for(Map.Entry<String, String> linha : ((Map<String, String>)obj).entrySet()){
					if(linha.getKey().equals(campo)){
						Map<String, String> dado = ((Map<String, String>)obj);
						listagem.add(linha.getValue()+" - "+dado.get(descricao));
					}
				}
				
			}
			String[] retorno = new String[listagem.size()];
			listagem.toArray(retorno);
			return retorno;
		}
		return new String[]{};
	}
	
	@SuppressWarnings("unchecked")
	public static String[] leituraDados(List<Object> lista, String campo){
		List<String> listagem = new ArrayList<String>();
		if(lista != null){
			for (Object obj : lista) {
				for(Map.Entry<String, String> linha : ((Map<String, String>)obj).entrySet()){
					if(linha.getKey().equals(campo)){
//					Map<String, String> dado = ((Map<String, String>)obj);
						listagem.add(linha.getValue());
					}
				}
				
			}
			String[] retorno = new String[listagem.size()];
			listagem.toArray(retorno);
			return retorno;
		}
		return new String[]{};
	}
	
	public void apagarDados(){
		try {
			this.getWritableDatabase().execSQL(nomeTabela.deleteSQL());
		} catch (Exception e) {
			Log.i("Erro Dados", e.getMessage());
		}
	}
	
	public boolean popularBanco(String dados) throws JSONException{
		try{
			executaSQL(PrepararDados.prepararTabela(dados, nomeTabela).split(";"));
			Log.i("Tabela Inserida : ", nomeTabela.getNomeTabela());
//			executaSQL(new String[]{"update horarios set descricao = (select descricao from paralizacoes" +
//					" where id = codigoParalizacao) where codigoParalizacao is not null;"});
			return true;
		}catch(JSONException e){
			throw new JSONException("Arquivo não é válido.");
		}
	}
	
	public void atualizaBanco(){
		executaSQL(new String[]{"update horarios set descricao = (select descricao from paralizacoes" +
					" where id = codigoParalizacao) where codigoParalizacao is not null;"});
	}
	
	private void executaSQL(String[] sql){
		for(String s : sql){
			if(s.trim().length() > 0){
				try {
					this.getWritableDatabase().execSQL(s);
				} catch (SQLiteException e) {
					Log.i("Erro Dados", e.getMessage());
				}
			}
		}
	}
	
	public boolean excluirDados(String id, String where){
		return this.getWritableDatabase().delete(nomeTabela.getNomeTabela(), where, null) > 0;
	}

	public boolean excluirDados(String id){
		return this.getWritableDatabase().delete(nomeTabela.getNomeTabela(), "id = " + id, null) > 0;
	}
	
	public boolean excluirDados(String[] comandos){
		try {
			for(String comando : comandos){
				this.getWritableDatabase().execSQL(comando);
			}
			return true; 
		} catch (Exception e) {
			return false;
		}
	}
	
	public static int posicaoSpinner(int tipo, List<Object> lista, String campo, boolean update, SharedPreferences sp){
		if(update == true){
			String id = null;
			switch (tipo) {
			case BancoDeDados.SERVICO:
				id = sp.getString("idServico", "");
				break;
			case BancoDeDados.ATIVIDADE:
				id = sp.getString("idAtividade", "");
				break;
			case BancoDeDados.ATIVIDADE_SERVICO:
				id = sp.getString("idAtivServ", "");	
				if(id == null || id.equals("")){
					return 0;
				}
				break;
			case BancoDeDados.EQUIPES:
				id = sp.getString("idEquipe", "");
				break;				
			default:
				break;
			}
			String valorLista = null;
			if(lista != null){
				for(int i = 0; i < lista.size(); i++){
					Object obj = lista.get(i);
					valorLista = pegarValor(obj, campo);
					try{
						if(Integer.parseInt(id) == Integer.parseInt(valorLista)){
							return i;
						}
					}catch(Exception e){
					}
				}
			}
		}
		return 0;
	}
	
	public long salvarDados(ContentValues values, boolean update, SharedPreferences sp){
		ContentValues valorObra = new ContentValues();
		valorObra.put("status", 1);
		BancoDeDados banco = new BancoDeDados(this.contexto, Tabelas.OBRA, Tabelas.version);
		banco.getWritableDatabase().update(Tabelas.OBRA.getNomeTabela(), valorObra, null, null);
		if(update){
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean("update", false);
			editor.commit();
			String id = sp.getString("idUpdate", "-1");
			try {
				if (this.getWritableDatabase().update(nomeTabela.getNomeTabela(), values, "id = "+id, null) > 0){
					return Integer.parseInt(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;
		}else{
			return this.getWritableDatabase().insert(nomeTabela.getNomeTabela(), null, values);
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			for(Tabelas tabela : Tabelas.values()){
				Log.i("Banco", "Criando tabela "+tabela);
				db.execSQL(tabela.createSQL());
			}
		} catch (SQLiteException e) {
			Log.i("Erro Dados", e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BancoDeDados.class.getName(), "Atualizando tabela "+nomeTabela+" da versão " + oldVersion + " para " + newVersion + ", todos as informações serão destruidas.");
        for(Tabelas tabela : Tabelas.values()){
        	Log.i("Banco", "Drop tabela "+tabela);
			db.execSQL(tabela.dropSQL());
		}
        onCreate(db); 
	}

	public static String pegaDataAtual(String pattern) {
		SimpleDateFormat dataAtual = new SimpleDateFormat(pattern);
		return dataAtual.format(Calendar.getInstance().getTime());
	}
	 
	 
}
