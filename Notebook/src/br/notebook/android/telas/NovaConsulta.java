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
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class NovaConsulta extends ListActivity {
	
	private static final String menus[] = {"Menu Principal"};
	private static boolean finalizar = false;
	
	private List<Object> listaEquipes;
	private List<Object> equipesTrabalho;
	private List<Object> equipesParalisacao;
	
	private List<Object> listaIndividual;
	private List<Object> individualTrabalho;
	private List<Object> individualParalisacao;
	
	private int totalEquipes = 0;
	private int totalPessoas = 0;
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Consulta");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent("Apontamentos"));
			}
		});
		alertDialog.show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> listaFinal = new ArrayList<String> ();
		finalizar=false;
//		setContentView(R.layout.nova_consulta);
//		BancoDeDados banco = new BancoDeDados(NovaConsulta.this, Tabelas.EQUIPES, Tabelas.version);
//		equipes = banco.consultaDados(new String[]{"id", "nomeEquipe", "horarioTrabalho"}, null, "nomeEquipe");
//		
//		banco = new BancoDeDados(NovaConsulta.this, Tabelas.EQUIPAMENTOS, Tabelas.version);
//		equipamentos = banco.consultaDados(new String[]{"id", "prefixo", "categoria", "nome"}, null, "prefixo");
//	
//		banco = new BancoDeDados(NovaConsulta.this, Tabelas.PESSOAS, Tabelas.version);
//		pessoas = banco.consultaDados(new String[]{"id", "matricula", "nome"}, null, "matricula");
//		
//		listagemGeral = new ArrayList<Object>();
//		String[] v = BancoDeDados.leituraDados(equipes, "nomeEquipe");
//		BancoDeDados.leituraDados(equipamentos, "nome");
//		BancoDeDados.leituraDados(pessoas, "nome");
		
		
		equipesParalisacao = preparaConsulta(Tabelas.PARALIZACAO_EQUIPES,
					new String[]{"id", "idEquipe", "idServico", "idAtividade", "idAtivServ"}, "idEquipe");
		
		equipesTrabalho = preparaConsulta(Tabelas.TRABALHO_EQUIPES, 
				new String[]{"id", "idEquipe", "idServico", "idAtividade", "idAtivServ", "prod", "origem", "destino"}, "idEquipe");
		listaEquipes = filtraConsulta(comparaDados(equipesParalisacao, equipesTrabalho, "idEquipe"), 
				Tabelas.EQUIPES, new String[]{"id", "nomeEquipe"}, "idEquipe");
		
		individualParalisacao = preparaConsulta(Tabelas.PARALIZACAO_INDIVIDUAL, 
				new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo");
		
		individualTrabalho = preparaConsulta(Tabelas.TRABALHO_INDIVIDUAL, 
				new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo");
		
		
		listaIndividual = filtraConsulta(comparaDados(individualParalisacao, individualTrabalho, new String[]{"tipo","idTipo"}),
				Tabelas.PESSOAS, new String[]{"id", "nome", "matricula"}, "idTipo");
		if(listaIndividual != null){
			totalPessoas = listaIndividual.size();
		}
		listaIndividual.addAll(filtraConsulta(comparaDados(individualParalisacao, individualTrabalho, new String[]{"tipo","idTipo"}),
				Tabelas.EQUIPAMENTOS, new String[]{"id", "nome", "prefixo"}, "idTipo"));
		
		String[] equipes = BancoDeDados.leituraDados(listaEquipes, "nomeEquipe");
		String[] individuais = BancoDeDados.leituraDados(listaIndividual, "nome");
		
		if((equipes.length+individuais.length) <= 0){
			finalizar=true;
			montaAlerta("Não existe apontamentos.");
		}
		if(equipes != null){
			totalEquipes = equipes.length;
		}
		if(equipes.length > 0){
			listaFinal.add("***************   Equipes   **************"); // position 0 
		}
		for(String equipe : equipes){
			listaFinal.add(equipe);
		}
		if(totalPessoas > 0){
			listaFinal.add("***************   Pessoas   **************");
		}
		for(int i = 0; i< individuais.length; i++){
			if(i == totalPessoas){
				listaFinal.add("***********  Equipamentos  *************");
			}
			listaFinal.add(individuais[i]);
			
		}
		
		setListAdapter(new ArrayAdapter<String>(NovaConsulta.this, 
				android.R.layout.simple_list_item_1, listaFinal));
	}
	
	private List<Object> comparaDados(List<Object> lista1,
			List<Object> lista2, String[] campos) {
		List<Object> retorno = new ArrayList<Object>();
		retorno.addAll(lista2);
		boolean encontrou = false;
		int qtde = 0;
		for(Object obj : lista1){
			Map<String, String> dado = (Map<String, String>)obj;
			qtde=0;
			saida : for(Object obj2 : lista2){
				Map<String, String> dado2 = (Map<String, String>)obj2;
				encontrou = false;
				qtde=0;
				for(String campo : campos){
					if(dado.get(campo).equals(dado2.get(campo))){
						qtde++;
						if(qtde == campos.length){
							encontrou = true;
							break saida;
						}
					}
				}
			}
			if(!encontrou){
				retorno.add(obj);
			}
		}
		return retorno;
	}

	private List<Object> comparaDados(List<Object> lista1,
			List<Object> lista2, String campo) {
		List<Object> retorno = new ArrayList<Object>();
		retorno.addAll(lista2);
		boolean encontrou = false;
		for(Object obj : lista1){
			Map<String, String> dado = (Map<String, String>)obj;
			encontrou = false;
			for(Object obj2 : lista2){
				Map<String, String> dado2 = (Map<String, String>)obj2;
				if(dado.get(campo).equals(dado2.get(campo))){
					encontrou = true;
					break;
				}
			}
			if(!encontrou){
				retorno.add(obj);
			}
		}
		return retorno;
	}

	private List<Object> preparaConsulta(Tabelas tabela, String[] select, String ordem){
		BancoDeDados banco = new BancoDeDados(NovaConsulta.this, tabela, Tabelas.version);
		return banco.consultaDados(select, null, ordem);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.nova_consulta, menu);
//		return true;
//	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			SharedPreferences sp = getSharedPreferences("DADOS", 0);
			SharedPreferences.Editor editor = sp.edit();
			Object obj = BancoDeDados.pegarValor(listaEquipes, "nomeEquipe", (String)l.getItemAtPosition(position));
			if(obj == null){
				obj = BancoDeDados.pegarValor(listaIndividual, "nome", (String)l.getItemAtPosition(position));
				editor.putInt("tipoConsulta", 2);
				String tipo = position >= (totalEquipes+totalPessoas) ? "prefixo" : "matricula";
				editor.putString("tipo", tipo);
			}else{
				editor.putInt("tipoConsulta", 1);
			}
			for(Map.Entry<String, String> dado : ((Map<String, String>)obj).entrySet()){
				editor.putString(dado.getKey(), dado.getValue());
			}
			editor.commit();
			startActivity(new Intent("VisualizacaoConsulta"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private List<Object> filtraConsulta(List<Object> dados, Tabelas tabela, String[] select, String campo){
		Object obj = null;
		StringBuilder whereIN = new StringBuilder("");
		for(int i = 0; i < dados.size(); i++){
			obj = dados.get(i);
			Map<String, String> dado = (Map<String, String>)obj;
			if((i+1) >= dados.size()){
				whereIN.append(dado.get(campo));
			}else{
				whereIN.append(dado.get(campo)+",");
			}	
		}
		if(whereIN.length() > 0){
			BancoDeDados banco = new BancoDeDados(NovaConsulta.this, tabela, Tabelas.version);
			return banco.consultaDados(select, " id in ("+whereIN.toString()+")", "id");
		}
		return new ArrayList<Object>();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(finalizar){
			finish();
		}
	}

}
