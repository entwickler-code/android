package br.notebook.android.telas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;
import br.notebook.android.menus.Apontamentos;

public class Consulta extends Activity {
	
	private static String tipo;
	private static String campo;
	private static String escolha;
	
	private static String id;
	private static String idNome;
	
	public static final String INDIVIDUAL = "Individual";
	public static final String EQUIPE = "Equipe";
	
	public static String tipoConsulta = "";
	
	private List<Object> lista = new ArrayList<Object>();
	private List<Object> listaOriginal = new ArrayList<Object>();
	private ArrayAdapter<String> listagem = null;
	
	private Object selecionado = null; 
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		
		tipo = sp.getString("TipoApontamento", "");
	}
	
	
	private Object preparaSelecionado(){
		for(Object obj : listaOriginal){
			int valor = Integer.parseInt(BancoDeDados.pegarValor(obj, idNome));
			int valorCampo = Integer.parseInt(BancoDeDados.pegarValor(selecionado, id));
			if(valor == valorCampo){
				return obj;
			}
		}
		return null;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consulta);
		carregaVars();
		
		Button btn = (Button)findViewById(R.id.btContinuar);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(selecionado != null){
					SharedPreferences sp = getSharedPreferences("DADOS", 0);
					SharedPreferences.Editor editor = sp.edit();
					editor.putBoolean("update", true);
					Object obj = preparaSelecionado();
					if(obj != null){
						for(Map.Entry<String, String> dado : ((Map<String, String>)obj).entrySet()){
							if(dado.getKey().equals("id")){
								editor.putString("id", dado.getValue());
								editor.putString("idUpdate", dado.getValue());
							}else{
								editor.putString(dado.getKey(), dado.getValue());
							}
						}
						if(!campo.equals("nomeEquipe")){
							String janela = null;
							BancoDeDados banco = null;
							List<Object> lista = null;
							Map<String, String> mapa = (Map<String, String>)obj;
							String chave = mapa.get("idTipo");
							if(campo.equals("matricula") ){
								janela = "matricula";
								banco = new BancoDeDados(Consulta.this, Tabelas.PESSOAS, Tabelas.version);
								lista = banco.consultaDados(new String[]{/*"id",*/ "matricula", "nome", "funcao"}, "id = "+chave, "matricula");
							}else if(campo.equals("prefixo")){
								janela = "prefixo";
								banco = new BancoDeDados(Consulta.this, Tabelas.EQUIPAMENTOS, Tabelas.version);
								lista = banco.consultaDados(new String[]{/* "id", */ "prefixo", "nome", "servicoPadrao"}, "id = "+chave, "prefixo");
							}
							for(Map.Entry<String, String> dado : ((Map<String, String>)lista.get(0)).entrySet()){
								editor.putString(dado.getKey(), dado.getValue());
							}
							Map<String, String> nome = ((Map<String, String>)lista.get(0));
							editor.putString("nomeJanela", nome.get(janela));
						}
						editor.putString("tipo", tipoConsulta);
						editor.commit();
						if(tipo.equals(Apontamentos.IMPRODUTIVAS)){
							if(escolha.equals(EQUIPE)){
								startActivity(new Intent("ParalizacaoEquipe2"));
							}else if(escolha.equals(INDIVIDUAL)){
								startActivity(new Intent("ParalizacaoIndividual2"));
							}
						}else if (tipo.equals(Apontamentos.TRABALHADAS)){
							if(escolha.equals(EQUIPE)){
								startActivity(new Intent("ApropriacaoEquipe"));
							}else if(escolha.equals(INDIVIDUAL)){
								startActivity(new Intent("ApropriacaoIndividual2"));
							}
						}
					}
				}
			}
		});
		
		
		final AutoCompleteTextView  texto = (AutoCompleteTextView)findViewById(R.id.autoCompDados);
		
		texto.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String nome = (String)arg0.getItemAtPosition(arg2);
				String id = nome.split(" - ")[0];
				selecionado = BancoDeDados.pegarValor(lista, campo, id);
			}
			
		});
		
		texto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				ArrayAdapter<String> listagem = null;
				texto.setText(null);
				texto.clearListSelection();
				texto.setHint("Equipe/Individual");
//				listagem = new ArrayAdapter<String>(ApropriacaoIndividual.this, 
//						android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, tipo));
				texto.setAdapter(listagem);
				texto.showDropDown();
			}
		});
		
		RadioGroup group = (RadioGroup)findViewById(R.id.rgTipo);
		final RadioGroup groupIndividual = (RadioGroup)findViewById(R.id.rgTipoIndividual);
		groupIndividual.setVisibility(RadioGroup.INVISIBLE);
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				texto.setText(null);
				texto.clearListSelection();
				switch (checkedId) {
				case R.id.rBEquipe:
					escolha = EQUIPE;
					groupIndividual.setVisibility(RadioGroup.INVISIBLE);
					if(tipo.equals(Apontamentos.IMPRODUTIVAS)){
						listaOriginal = preparaConsulta(Tabelas.PARALIZACAO_EQUIPES, 
								new String[]{"id", "idEquipe", "idServico", "idAtividade", "idAtivServ"}, "idEquipe");
						lista = filtraConsulta(listaOriginal, Tabelas.EQUIPES, new String[]{"id", "nomeEquipe"}, "idEquipe");
						listagem = new ArrayAdapter<String>(Consulta.this, 
								android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, "nomeEquipe"));
					}else if (tipo.equals(Apontamentos.TRABALHADAS)){
						listaOriginal = preparaConsulta(Tabelas.TRABALHO_EQUIPES, 
								new String[]{"id", "idEquipe", "idServico", "idAtividade", "idAtivServ", "prod", "origem", "destino"}, "idEquipe");
						lista = filtraConsulta(listaOriginal, Tabelas.EQUIPES, new String[]{"id", "nomeEquipe"}, "idEquipe");
						listagem = new ArrayAdapter<String>(Consulta.this, 
								android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, "nomeEquipe"));
					}
					texto.setHint("Equipe");
					campo = "nomeEquipe";
					id = "id";
					idNome = "idEquipe";
					break;
				case R.id.rBIndividual:
					escolha = INDIVIDUAL;
					id = "id";
					idNome = "idTipo";
					groupIndividual.setVisibility(RadioGroup.VISIBLE);
					texto.setHint("Individual");
//					campo = "Individual";
					break;					

				default:
					break;
				}
				texto.setAdapter(listagem);
				texto.showDropDown();
			}
		});
		
		
		groupIndividual.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				texto.setText(null);
				texto.clearListSelection();
				switch (checkedId) {
				case R.id.rBFuncionario:
					campo = "matricula";
					tipoConsulta = "matricula";
					if(tipo.equals(Apontamentos.IMPRODUTIVAS)){
						listaOriginal = preparaConsulta(Tabelas.PARALIZACAO_INDIVIDUAL, 
								new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo");
						lista = filtraConsulta(listaOriginal, Tabelas.PESSOAS, new String[]{"id", "nome", "matricula"}, "idTipo");
						listagem = new ArrayAdapter<String>(Consulta.this, 
								android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, "matricula", "nome"));
					}else if (tipo.equals(Apontamentos.TRABALHADAS)){
						listaOriginal = preparaConsulta(Tabelas.TRABALHO_INDIVIDUAL,
								new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo");
						lista = filtraConsulta(listaOriginal, Tabelas.PESSOAS, new String[]{"id", "nome", "matricula"}, "idTipo");
						listagem = new ArrayAdapter<String>(Consulta.this, 
								android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, "matricula", "nome"));
					}
					break;
				case R.id.rBEquipamento:
					campo = "prefixo";
					tipoConsulta = "prefixo";
					if(tipo.equals(Apontamentos.IMPRODUTIVAS)){
						listaOriginal = preparaConsulta(Tabelas.PARALIZACAO_INDIVIDUAL, 
								new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo");
						lista = filtraConsulta(listaOriginal, Tabelas.EQUIPAMENTOS, new String[]{"id", "prefixo", "nome", "servicoPadrao"}, "idTipo");
						listagem = new ArrayAdapter<String>(Consulta.this, 
								android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, "prefixo", "nome"));
					}else if (tipo.equals(Apontamentos.TRABALHADAS)){
						listaOriginal = preparaConsulta(Tabelas.TRABALHO_INDIVIDUAL,
								new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo");
						lista = filtraConsulta(listaOriginal, Tabelas.EQUIPAMENTOS, new String[]{"id", "prefixo", "nome", "servicoPadrao"}, "idTipo");
						listagem = new ArrayAdapter<String>(Consulta.this, 
								android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, "prefixo", "nome"));
					}
					break;					

				default:
					break;
				}
				texto.setAdapter(listagem);
				texto.showDropDown();
			}
		});
		
		
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
			BancoDeDados banco = new BancoDeDados(Consulta.this, tabela, Tabelas.version);
			return banco.consultaDados(select, " id in ("+whereIN.toString()+")", "id");
		}
		return null;
	}
	
	private List<Object> preparaConsulta(Tabelas tabela, String[] select, String ordem){
		BancoDeDados banco = new BancoDeDados(Consulta.this, tabela, Tabelas.version);
		return banco.consultaDados(select, null, ordem);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
}
