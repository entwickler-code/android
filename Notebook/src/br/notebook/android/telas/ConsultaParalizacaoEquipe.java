package br.notebook.android.telas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ConsultaParalizacaoEquipe extends ListActivity {

private List<Object> horarios;
	
	private Long id;
	
	private BaseAdapter adapter;
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		setTitle(sp.getString("nomeEquipe", ""));
		id = sp.getLong("idParalizacaoEquipe", 0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		carregaVars();
		
		horarios = montaDados(Tabelas.HORAS_PARALIZACOES_EQUIPES, new String[]{"id", "horaInicio", "horaTermino", "descricao"}, 
				"descricao", String.format(Tabelas.HORAS_PARALIZACOES_EQUIPES.whereSQL(), id));
		adapter = new ArrayAdapter<String>(ConsultaParalizacaoEquipe.this, 
				android.R.layout.simple_list_item_1, montaLista());
		
		setListAdapter(adapter);
	}
	
	@SuppressWarnings("unchecked")
	public String[] montaLista(){
		List<String> listagem = new ArrayList<String>();
		Map<String, String> linha = null;
		for (Object obj : horarios) {
			linha = (Map<String, String>)obj;
			listagem.add(linha.get("horaInicio")+" - "+linha.get("horaTermino")+" - "+linha.get("descricao"));
		}
		String[] retorno = new String[listagem.size()];
		listagem.toArray(retorno);
		return retorno;
	}
	
	private String[] consertaDados(String[] dados){
		List<String> valores = new ArrayList<String>();
		for (String dado : dados) {
			if(!dado.equals("-")){
				valores.add(dado);
			}
		}
		String[] retorno = new String[valores.size()];
		valores.toArray(retorno);
		return retorno;
	}
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Consulta Paralisação");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final String selecionado = (String)l.getItemAtPosition(position);
		AlertDialog alertDialog = new AlertDialog.Builder(ConsultaParalizacaoEquipe.this).create();
		alertDialog.setTitle("Exclusão...");
		alertDialog.setMessage("Deseja excluir o horário?");
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String[] dados = consertaDados(selecionado.split(" - "));
				Object horario = pegarValor(horarios, new String[]{"horaInicio", "horaTermino","descricao"}, dados);
				if(horario != null){
					BancoDeDados banco = new BancoDeDados(ConsultaParalizacaoEquipe.this, Tabelas.HORAS_PARALIZACOES_EQUIPES, Tabelas.version);
					banco.excluirDados(BancoDeDados.pegarValor(horario, "id"));
					horarios.remove(horario);
					adapter.notifyDataSetChanged();
					startActivity(new Intent("ConsultaParalizacaoEquipe"));
				}else{
					montaAlerta("Problemas ao excluir horário.");
				}
			}
		});
		alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();		
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
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
