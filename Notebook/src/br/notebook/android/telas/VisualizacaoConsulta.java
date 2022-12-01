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

public class VisualizacaoConsulta extends ListActivity {
	private Object selecionado;
	private String idSelecionado;
	private int filtro;
	private int contadorHorarios = 0;
	
	private List<String> listaMontada;
	
	private List<Object> servicos;
	private List<Object> horarios;
	private List<Object> atividades;
	private List<Object> atividadeServicos;
	
	private List<Object> listaEquipes;
	private List<Object> equipesTrabalho;
	private List<Object> equipesParalisacao;
	
	private List<Object> listaIndividual;
	private List<Object> individualTrabalho;
	private List<Object> individualParalisacao;
	private int totalHorasIndividuais = 0;
	private int tipoHorario = 0;
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		idSelecionado = sp.getString("id", "0");
		filtro = sp.getInt("tipoConsulta", 0);
		
		servicos = montaDados(Tabelas.SERVICOS, new String[]{"id", "descricao"},"descricao", null);
		atividadeServicos = montaDados(Tabelas.ATIVIDADES_SERVICOS, new String[]{"contador", "servico", "descricao", "id", "frenteObra", "idAtividade"}, 
				"descricao", null);
		atividades = montaDados(Tabelas.ATIVIDADES, new String[]{"contador", "id", "frenteObra", "idAtividade", "descricao"},"descricao", null);
		if(filtro == 1){ // equipe
			setTitle(sp.getString("nomeEquipe", ""));
			equipesParalisacao = preparaConsulta(Tabelas.PARALIZACAO_EQUIPES, 
					new String[]{"id", "idEquipe", "idServico", "idAtividade", "idAtivServ"}, "idEquipe", "idEquipe = "+idSelecionado);
			
			equipesTrabalho = preparaConsulta(Tabelas.TRABALHO_EQUIPES, 
					new String[]{"id", "idEquipe", "idServico", "idAtividade", "idAtivServ", "prod", "origem", "destino"}, "idEquipe", "idEquipe = "+idSelecionado);
			
			for(int i = 0; i < equipesTrabalho.size(); i++){
				Object trabalho  = equipesTrabalho.get(i); 
				Map<String, String> dados = (Map<String, String>)trabalho;
				for(Object obj : servicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idServico").equals(mapa.get("id"))){
						dados.put("descricaoServico", mapa.get("descricao"));
						equipesTrabalho.set(i, dados);		
						break;
					}
				}
				for(Object obj : atividades){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtividade").equals(mapa.get("contador"))){
//						if(dados.get("idServico").equals(mapa.get("frenteObra"))){
							dados.put("descricaoAtividade", mapa.get("descricao"));
							equipesTrabalho.set(i, dados);		
							break;
//						}
					}
				}
				for(Object obj : atividadeServicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtivServ").equals(mapa.get("contador"))){
						dados.put("descricaoAtivServico", mapa.get("descricao"));
						equipesTrabalho.set(i, dados);		
						break;
					}else if(dados.get("idAtivServ").equals("")){
						break;
					}
				}
			}
			
			for(int i = 0; i < equipesParalisacao.size(); i++){
				Object trabalho  = equipesParalisacao.get(i); 
				Map<String, String> dados = (Map<String, String>)trabalho;
				for(Object obj : servicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idServico").equals(mapa.get("id"))){
						dados.put("descricaoServico", mapa.get("descricao"));
						equipesParalisacao.set(i, dados);		
						break;
					}
				}
				for(Object obj : atividades){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtividade").equals(mapa.get("contador"))){
//						if(dados.get("idServico").equals(mapa.get("frenteObra"))){
							dados.put("descricaoAtividade", mapa.get("descricao"));
							equipesParalisacao.set(i, dados);		
							break;
//						}
					}
				}
				for(Object obj : atividadeServicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtivServ").equals(mapa.get("contador"))){
						dados.put("descricaoAtivServico", mapa.get("descricao"));
						equipesParalisacao.set(i, dados);		
						break;
					}else if(dados.get("idAtivServ").equals("")){
						break;
					}
				}
			}
			
		}else{ // indiviual
			setTitle(sp.getString("nome", ""));
			String where = "";
			String tipo = sp.getString("tipo", "");
			if(sp.getString("tipo", "").equals("matricula")){
				where = "idTipo = "+idSelecionado+" and tipo = "+1;
			}else if(sp.getString("tipo", "").equals("prefixo")){
				where = "idTipo = "+idSelecionado+" and tipo = "+2;
			}
			individualParalisacao = preparaConsulta(Tabelas.PARALIZACAO_INDIVIDUAL, 
					new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo", where);
			
			individualTrabalho = preparaConsulta(Tabelas.TRABALHO_INDIVIDUAL, 
					new String[]{"id", "tipo", "idTipo", "idServico", "idAtividade", "idAtivServ"}, "idTipo", where);
			
			for(int i = 0; i < individualParalisacao.size(); i++){
				Object paralisacao  = individualParalisacao.get(i); 
				Map<String, String> dados = (Map<String, String>)paralisacao;
				for(Object obj : servicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idServico").equals(mapa.get("id"))){
						dados.put("descricaoServico", mapa.get("descricao"));
						individualParalisacao.set(i, dados);		
						break;
					}
				}
				for(Object obj : atividades){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtividade").equals(mapa.get("contador"))){
//						if(dados.get("idServico").equals(mapa.get("frenteObra"))){
							dados.put("descricaoAtividade", mapa.get("descricao"));
							individualParalisacao.set(i, dados);		
							break;
//						}
					}
				}
				for(Object obj : atividadeServicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtivServ").equals(mapa.get("contador"))){
						dados.put("descricaoAtivServico", mapa.get("descricao"));
						individualParalisacao.set(i, dados);		
						break;
					}else if(dados.get("idAtivServ").equals("")){
						break;
					}
				}
			}
			
			for(int i = 0; i < individualTrabalho.size(); i++){
				Object trabalho  = individualTrabalho.get(i); 
				Map<String, String> dados = (Map<String, String>)trabalho;
				for(Object obj : servicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idServico").equals(mapa.get("id"))){
						dados.put("descricaoServico", mapa.get("descricao"));
						individualTrabalho.set(i, dados);		
						break;
					}
				}
				for(Object obj : atividades){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtividade").equals(mapa.get("contador"))){
//						if(dados.get("idServico").equals(mapa.get("frenteObra"))){
							dados.put("descricaoAtividade", mapa.get("descricao"));
							individualTrabalho.set(i, dados);		
							break;
//						}
					}
				}
				for(Object obj : atividadeServicos){
					Map<String, String> mapa = (Map<String, String>)obj;
					if(dados.get("idAtivServ").equals(mapa.get("contador"))){
						dados.put("descricaoAtivServico", mapa.get("descricao"));
						individualTrabalho.set(i, dados);		
						break;
					}else if(dados.get("idAtivServ").equals("")){
						break;
					}
				}
			}
		}
  	}
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(VisualizacaoConsulta.this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
	private String preparaHorario(List<Object> listagem, String[] lista){
		for(Object obj : listagem){
			Map<String, String> dados = (Map<String, String>)obj;
			if(dados.get("descricaoServico").equals(lista[0].trim())){
				if(dados.get("descricaoAtividade").equals(lista[1].trim())){
					if((lista[2].trim().equals("sem Escolha")) || (dados.get("descricaoAtivServico").equals(lista[2].trim()))){
						return dados.get("id");
					}
				}
			}
		}
		return "";
	}
	
	private void pegaSelecionado(String chave, String idHorario, String[] finalLista){
		for(Object obj : horarios){
			Map<String, String> dados = (Map<String, String>)obj; 
			try{
				if(dados.get(chave).equals(idHorario)){
					if((finalLista[0].trim().equals(dados.get("horaInicio"))) && (finalLista[1].trim().equals(dados.get("horaTermino")))){
						if(finalLista[2].trim().equals(dados.get("descricao"))){
							selecionado = obj;
						}
					}
				}
			}catch(Exception e){
			}
		}
	}
	
	private boolean excluirApontamentoSemHorario(int tipoHorario, int filtro, String id, boolean temHorarios){
		if(!temHorarios){
			Tabelas tabela = null;
			if(filtro == 1){
				if(tipoHorario == 1){
					tabela = Tabelas.TRABALHO_EQUIPES;
				}else{
					tabela = Tabelas.PARALIZACAO_EQUIPES;
				}
			}else{
				if(tipoHorario == 1){
					tabela = Tabelas.TRABALHO_INDIVIDUAL;
				}else{
					tabela = Tabelas.PARALIZACAO_INDIVIDUAL;
				}
			}
			BancoDeDados banco = new BancoDeDados(VisualizacaoConsulta.this, tabela, Tabelas.version);
			boolean retorno = banco.excluirDados(idSelecionado, id);
			return retorno;
		}
		return false;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String[] lista = null;
		String[] finalLista = null;
		for(String selecionado : listaMontada){
			if(selecionado.equals((String)l.getItemAtPosition(position))){
				lista = selecionado.split("/");
				finalLista = lista[lista.length-1].split("-");
				break;
			}
		}
		String idHorario = null;
		if(filtro == 1){// equipe
			tipoHorario = position >= (equipesTrabalho.size() * contadorHorarios) ? 2 : 1; // 1 = apropriacao //2 paralisacao
			if(tipoHorario == 1){ 
				idHorario = preparaHorario(equipesTrabalho, lista);
				pegaSelecionado("idTrabalhoEquipe", idHorario, finalLista);
			}else{ 
				idHorario = preparaHorario(equipesParalisacao, lista);
				pegaSelecionado("idParalizacaoEquipe", idHorario, finalLista);
			}
		}else{// individual
			tipoHorario = position >= (individualTrabalho.size() * contadorHorarios) ? 2 : 1; // 1 = apropriacao //2 paralisacao
			if(tipoHorario == 1){ 
				idHorario = preparaHorario(individualTrabalho, lista);
				pegaSelecionado("idIndividual", idHorario, finalLista);
			}else{ 
				idHorario = preparaHorario(individualParalisacao, lista);
				pegaSelecionado("idIndividual", idHorario, finalLista);
			}
		}
		if (totalHorasIndividuais != position){
			AlertDialog alertDialog = new AlertDialog.Builder(VisualizacaoConsulta.this).create();
			alertDialog.setTitle("Exclusão...");
			alertDialog.setMessage("Deseja excluir o horário?");
			alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Tabelas tabela = null;
					boolean excluir = false;
					String where = null;
					if(filtro == 1){ // equipe
						where = "idEquipe = "+idSelecionado;
						if(tipoHorario == 1){ // apropriação
							tabela = Tabelas.HORAS_EQUIPES;
						}else{
							tabela = Tabelas.HORAS_PARALIZACOES_EQUIPES;
						}
					}else{
						if(tipoHorario == 1){ // apropriação
							where = "idTipo = "+idSelecionado+" and tipo = 1" ;
							tabela = Tabelas.HORAS_INDIVIDUAIS;
						}else{
							tabela = Tabelas.HORAS_PARALIZACOES_INDIVIDUAL;
							where = "idTipo = "+idSelecionado+" and tipo = 2" ;
						}
					}
					BancoDeDados banco = new BancoDeDados(VisualizacaoConsulta.this, tabela, Tabelas.version);
					banco.excluirDados(BancoDeDados.pegarValor(selecionado, "id"));
					boolean temHorarios = horarios.size() > 1;
					if(excluirApontamentoSemHorario(tipoHorario, filtro, where, temHorarios)){
						startActivity(new Intent("NovaConsulta"));
					}else{
						startActivity(new Intent("VisualizacaoConsulta"));
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
	}
	
//	private void montaAlerta(String msg){
//		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setTitle("Consulta");
//		alertDialog.setMessage(msg);
//		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//			}
//		});
//		alertDialog.show();
//	}
	
	private void montaTela(List<Object> lista, String chave, boolean contador){
		if(!contador && lista.size() > 0){
			listaMontada.add("*************  Paralisação  *************");
		}
		for(Object obj : lista){
			StringBuilder campo = new StringBuilder("");
			Map<String, String> dados = (Map<String, String>)obj;
			campo.append(dados.get("descricaoServico")+" / ");
			campo.append(dados.get("descricaoAtividade")+" /"+System.getProperty("line.separator"));
			campo.append(dados.get("descricaoAtivServico") == null ? "sem Escolha / "+System.getProperty("line.separator") 
					: dados.get("descricaoAtivServico")+" /"+System.getProperty("line.separator"));
			for(Object horario : horarios){
				if(horario != null){
					StringBuilder campo2 = new StringBuilder("");
					Map<String, String> hora = (Map<String, String>)horario;
					if(dados.get("id").equals(hora.get(chave))){
						campo2.append(hora.get("horaInicio")+" - ");
						campo2.append(hora.get("horaTermino")+" - ");
						campo2.append(hora.get("descricao"));
						listaMontada.add(campo.toString()+campo2.toString());
						if(contador){
							contadorHorarios++;
						}
					}
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.visualizacao_consulta);
		carregaVars();
		pegaHorarios();
		listaMontada = new ArrayList<String>();
		contadorHorarios = 0;
		if(filtro == 1){
			montaTela(equipesTrabalho, "idTrabalhoEquipe", true);
			montaTela(equipesParalisacao, "idParalizacaoEquipe", false);
		}else{
			montaTela(individualTrabalho, "idIndividual", true);
			montaTela(individualParalisacao, "idIndividual", false);
		}
		setListAdapter(new ArrayAdapter<String>(VisualizacaoConsulta.this, android.R.layout.simple_list_item_1, listaMontada));
	}
	
	private void montaHorarios(List<Object> lista, Tabelas tabela, String[] campos, String where){
		List<Object> retorno = null;
		for(Object obj : lista){
			Map<String, String> dados = (Map<String, String>)obj;
			retorno = montaDados(tabela, campos,"id", where+dados.get("id"));
			if(retorno != null){
				horarios.addAll(retorno);
			}
		}
	}
	
	private void pegaHorarios(){
		horarios = new ArrayList<Object>();
		if(filtro == 1){ // equipe
			montaHorarios(equipesTrabalho, Tabelas.HORAS_EQUIPES, new String[]{"id", "idTrabalhoEquipe","horaInicio", "horaTermino","descricao",
					"idHorario", "horarioTrabalho", "idServico" , "codigoParalizacao", "idFuncao","data", "diaSemana"},"idTrabalhoEquipe = ");
			totalHorasIndividuais  = horarios.size();
			montaHorarios(equipesParalisacao, Tabelas.HORAS_PARALIZACOES_EQUIPES, new String[]{"id", "idParalizacaoEquipe","horaInicio", "horaTermino",
					"descricao", "idParalizacao"}, "idParalizacaoEquipe = ");
		}else if(filtro == 2){
//			if(tipo == "matricula"){ //pessoa
				montaHorarios(individualTrabalho, Tabelas.HORAS_INDIVIDUAIS, new String[]{"id", "idIndividual", "horaInicio",
						"horaTermino" , "descricao", "idServico", "tipo", "data"},"idIndividual = ");
				totalHorasIndividuais  = horarios.size();
				montaHorarios(individualParalisacao, Tabelas.HORAS_PARALIZACOES_INDIVIDUAL, new String[]{"id", "idIndividual","horaInicio", 
						"horaTermino", "descricao", "justificativa", "idComponente", "idParalizacao","categoria", "data"},"idIndividual = ");
//			}else{
//			}
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.visualizacao_consulta, menu);
//		return true;
//	}
	
	private List<Object> preparaConsulta(Tabelas tabela, String[] select, String ordem, String condicao){
		BancoDeDados banco = new BancoDeDados(VisualizacaoConsulta.this, tabela, Tabelas.version);
		return banco.consultaDados(select, condicao, ordem);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
