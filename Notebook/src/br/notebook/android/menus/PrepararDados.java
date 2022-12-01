package br.notebook.android.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

/* Classe responsável por preparar os dados para serem exportados para um arquivo
 * */



public class PrepararDados{
	
	private static JSONObject arquivo;
	private  static JSONArray pessoas;
	private static JSONArray equipes;
	private static JSONArray equipamentos;
	
	public static List<Object> montaDados(Context contexto, Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(contexto, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
	public static String exportarArquivo(Context context, Object obra) throws JSONException{
		arquivo = new JSONObject();
		arquivo.put("tipo", pegaValorObra(obra, "tipo"));
		arquivo.put("dataArquivo", pegaValorObra(obra, "data"));
		arquivo.put("dataExportacao", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
		arquivo.put("usuario", pegaValorObra(obra, "usuario"));
		arquivo.put("obra", pegaValorObra(obra, "obra"));
		arquivo.put("equipesTrabalho", equipes);
		arquivo.put("equipamento", equipamentos);
		arquivo.put("pessoal", pessoas);
		return arquivo.toString();
	}
	
	private static String pegaValorObra(Object obra, String campo){
//		BancoDeDados banco = new BancoDeDados(contexto, Tabelas.OBRA, Tabelas.version);
//		Object obj = (banco.consultaDados(new String[]{"id", "obra", "usuario", "data", "tipo"}, null, null)).get(0);
		return BancoDeDados.pegarValor(obra, campo);
	}
	
	private static void montaIndividual(Context contexto, Map<String, String> dados) throws JSONException{
		List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade", "descricao"}, "contador",
				"contador = " + dados.get("idAtividade"));
		List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico", "descricao"},
				"contador", "contador = " + dados.get("idAtivServ"));
		List<Object> horasParalizacao = montaDados(contexto, Tabelas.HORAS_PARALIZACOES_INDIVIDUAL, new String[]{"idParalizacao", "horaInicio", "horaTermino", 
				"justificativa", "idComponente", "descricao", "categoria", "data"}, "id", "idIndividual = " + dados.get("id"));
		
		List<Object> servico = montaDados(contexto, Tabelas.SERVICOS, new String[]{"id", "descricao"}, "id",
				"id = " + dados.get("idServico"));
		
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		json.put("idTipo", dados.get("idTipo"));
		json.put("nome", dados.get("nome"));
		json.put("idServico", dados.get("idServico"));
		json.put("descricaoServico", ((Map<String, String>)(servico).get(0)).get("descricao"));
		json.put("idAtividade", ((Map<String, String>)(atividade).get(0)).get("idAtividade"));
		json.put("descricaoAtividade", ((Map<String, String>)(atividade).get(0)).get("descricao"));
		json.put("id", ((Map<String, String>)(atividade).get(0)).get("id"));
		json.put("idAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("servico") : "");
		json.put("descricaoAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("descricao") : "");
		JSONObject horario = null;
		for(Object hora : horasParalizacao){
			Map<String, String> dado = (Map<String, String>)hora;
			horario = new JSONObject();
//			horario.put("idComponente", dado.get("idComponente"));
			horario.put("idComponente", dado.get("idComponente").equals("0") ? "null" : dado.get("idComponente"));
			horario.put("justificativa", dado.get("justificativa"));
			horario.put("idParalizacao", dado.get("idParalizacao"));
			horario.put("categoria", dado.get("categoria"));
			
			horario.put("horaInicio", dado.get("horaInicio"));
			horario.put("data", dado.get("data"));
			horario.put("horaTermino", dado.get("horaTermino"));
			horario.put("descricao", dado.get("descricao"));
			array.put(horario);
		}
		json.put("horario", new JSONArray());
		json.put("paralizacoes", array);
		if(dados.get("tipo").equals("1")){
			pessoas.put(json);
		}else if(dados.get("tipo").equals("2")){
			equipamentos.put(json);
		}
	}
	
	private static void montaIndividual(Context contexto, Map<String, String> dados, List<Object> lista) throws JSONException{
		
		List<Object> servico = montaDados(contexto, Tabelas.SERVICOS, new String[]{"id", "descricao"}, "id",
				"id = " + dados.get("idServico"));
		
		List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade", "descricao"}, "contador",
				"contador = " + dados.get("idAtividade"));
		List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico",  "descricao"},
				"contador", "contador = " + dados.get("idAtivServ"));
		List<Object> horas = montaDados(contexto, Tabelas.HORAS_INDIVIDUAIS, new String[]{"tipo", "idServico", "horaInicio", "horaTermino", "descricao", "data"},
				"id", "idIndividual = " + dados.get("id"));
		
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		json.put("idTipo", dados.get("idTipo"));
		json.put("nome", dados.get("nome"));
		json.put("horiInicial", dados.get("horoInicial"));
		json.put("horiFinal", dados.get("horoFinal"));
		json.put("idServico", dados.get("idServico"));
		json.put("descricaoServico", ((Map<String, String>)(servico).get(0)).get("descricao"));
		json.put("idAtividade", ((Map<String, String>)(atividade).get(0)).get("idAtividade"));
		json.put("id", ((Map<String, String>)(atividade).get(0)).get("id"));
		json.put("idAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("servico") : "");
		json.put("descricaoAtividade", ((Map<String, String>)(atividade).get(0)).get("descricao"));
		json.put("descricaoAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("descricao") : "");
		JSONObject horario = null;
		for(Object hora : horas){
			Map<String, String> dado = (Map<String, String>)hora;
			horario = new JSONObject();
			horario.put("idServico", dado.get("idServico"));
			horario.put("horaInicio", dado.get("horaInicio"));
			horario.put("data", dado.get("data"));
			horario.put("horaTermino", dado.get("horaTermino"));
			horario.put("descricao", dado.get("descricao"));
			array.put(horario);
		}
		json.put("horario", array);
		array = new JSONArray();
		horario = null;
		if(!lista.isEmpty()){
			for(Object paralizacao : lista){
				Map<String, String> dadoParalizacao = (Map<String, String>)paralizacao;
				List<Object> horasParalizacao = montaDados(contexto, Tabelas.HORAS_PARALIZACOES_INDIVIDUAL, new String[]{"idParalizacao", "horaInicio", "horaTermino", 
						"justificativa", "idComponente", "descricao", "categoria", "data"}, "id", "idIndividual = " + dadoParalizacao.get("id"));
				for(Object hora : horasParalizacao){
					Map<String, String> dado = (Map<String, String>)hora;
					horario = new JSONObject();
//					horario.put("idComponente", dado.get("idComponente"));
					horario.put("idComponente", dado.get("idComponente").equals("0") ? "null" : dado.get("idComponente"));
					horario.put("justificativa", dado.get("justificativa"));
					horario.put("idParalizacao", dado.get("idParalizacao"));
					horario.put("categoria", dado.get("categoria"));
					
					horario.put("horaInicio", dado.get("horaInicio"));
					horario.put("data", dado.get("data"));
					horario.put("horaTermino", dado.get("horaTermino"));
					horario.put("descricao", dado.get("descricao"));
					array.put(horario);
				}
			}
			json.put("paralizacoes", array);
		}else{
			json.put("paralizacoes", new JSONArray());
		}
		if(dados.get("tipo").equals("1")){
			pessoas.put(json);
		}else if(dados.get("tipo").equals("2")){
			equipamentos.put(json);
		}
	}
	
	public static boolean preparaIndividuais(Context contexto){
		List<Object> trabalhoIndividual = montaDados(contexto, Tabelas.TRABALHO_INDIVIDUAL, new String[]{"id","tipo",
				"idTipo","idServico","idAtividade", "idAtivServ", "horoInicial", "horoFinal"}, "id", null);
		
		List<Object> paralizacaoIndividual = montaDados(contexto, Tabelas.PARALIZACAO_INDIVIDUAL, new String[]{"id","tipo",
				"idTipo","idServico","idAtividade", "idAtivServ"}, "id", null);
		
		Map<String, String> dados = null;
		Map<String, String> dadosParalizacao = null;
		List<Object> lista = new ArrayList<Object>();
		
		for(Object obj : trabalhoIndividual){
			dados = (Map<String, String>)obj;
			List<Object> listagemNomes = null;
			if(dados.get("tipo").equals("1")){
				listagemNomes = montaDados(contexto, Tabelas.PESSOAS, new String[]{"id","nome"}, "id", "id ="+dados.get("idTipo"));
			}else if(dados.get("tipo").equals("2")){
				listagemNomes = montaDados(contexto, Tabelas.EQUIPAMENTOS, new String[]{"id","nome"}, "id", "id ="+dados.get("idTipo"));
			}
			dados.put("nome", ((Map<String, String>)listagemNomes.get(0)).get("nome"));
			for(int i = 0; i < paralizacaoIndividual.size();i++){
				dadosParalizacao = (Map<String, String>)paralizacaoIndividual.get(i);
				if((dados.get("tipo").equals(dadosParalizacao.get("tipo"))) && (dados.get("idTipo").equals(dadosParalizacao.get("idTipo")))){
					if((dados.get("idServico").equals(dadosParalizacao.get("idServico"))) && (dados.get("idAtividade").equals(dadosParalizacao.get("idAtividade"))) 
							&& (dados.get("idAtivServ").equals(dadosParalizacao.get("idAtivServ")))){
						lista.add(paralizacaoIndividual.remove(i));
					}
				}
			}
			try {
				montaIndividual(contexto, dados, lista);
				lista.clear();
			} catch (JSONException e) {
				return false;
			}
		}	
		for(Object obj : paralizacaoIndividual){
			dados = (Map<String, String>)obj;
			List<Object> listagemNomes = null;
			if(dados.get("tipo").equals("1")){
				listagemNomes = montaDados(contexto, Tabelas.PESSOAS, new String[]{"id","nome"}, "id", "id ="+dados.get("idTipo"));
			}else if(dados.get("tipo").equals("2")){
				listagemNomes = montaDados(contexto, Tabelas.EQUIPAMENTOS, new String[]{"id","nome"}, "id", "id ="+dados.get("idTipo"));
			}
			dados.put("nome", ((Map<String, String>)listagemNomes.get(0)).get("nome"));
			try {
				montaIndividual(contexto, dados);
			} catch (JSONException e) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean preparaEquipes(Context contexto){
		List<Object> trabalhoEquipes = montaDados(contexto, Tabelas.TRABALHO_EQUIPES, new String[]{"id","idEquipe",
				"prod","origem","destino", "idServico", "idAtividade", "idAtivServ"}, "id", null);
		
		List<Object> paralizacaoEquipe = montaDados(contexto, Tabelas.PARALIZACAO_EQUIPES, new String[]{"id","idEquipe",
				"idServico", "idAtividade", "idAtivServ"}, "id", null);
		
		Map<String, String> dados = null;
		Map<String, String> dadosParalizacao = null;
		List<Object> lista = new ArrayList<Object>();
		
//		JSONArray array = new JSONArray();
		for(Object obj : trabalhoEquipes){
			dados = (Map<String, String>)obj;
			List<Object> equipe = montaDados(contexto, Tabelas.EQUIPES, new String[]{"id", "nomeEquipe"}, "nomeEquipe",
					"id = " + dados.get("idEquipe"));
			dados.put("nome", ((Map<String, String>)equipe.get(0)).get("nomeEquipe"));
			for(int i = 0; i < paralizacaoEquipe.size();i++){
				dadosParalizacao = (Map<String, String>)paralizacaoEquipe.get(i);
				if(dados.get("idEquipe").equals(dadosParalizacao.get("idEquipe"))){
					if((dados.get("idServico").equals(dadosParalizacao.get("idServico"))) && (dados.get("idAtividade").equals(dadosParalizacao.get("idAtividade"))) 
							&& (dados.get("idAtivServ").equals(dadosParalizacao.get("idAtivServ")))){
						lista.add(paralizacaoEquipe.remove(i));
					}
				}
			}
			try {
				montaEquipe(contexto, dados, lista);
				lista.clear();
			} catch (JSONException e) {
				return false;
			}
		}	
		for(Object obj : paralizacaoEquipe){
			dados = (Map<String, String>)obj;
			List<Object> equipe = montaDados(contexto, Tabelas.EQUIPES, new String[]{"id", "nomeEquipe"}, "nomeEquipe",
					"id = " + dados.get("idEquipe"));
			dados.put("nome", ((Map<String, String>)equipe.get(0)).get("nomeEquipe"));
			try {
				montaEquipe(contexto, dados);
			} catch (JSONException e) {
				return false;
			}
		}
		return true;
	}
	
	private static void montaEquipe(Context contexto, Map<String, String> dados) throws JSONException{
		List<Object> servico = montaDados(contexto, Tabelas.SERVICOS, new String[]{"id", "descricao"}, "id",
				"id = " + dados.get("idServico"));
		List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade", "descricao"}, "contador",
				"contador = " + dados.get("idAtividade"));
		List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico", "descricao"},
			"contador", "contador = " + dados.get("idAtivServ"));
		List<Object> horasParalizacao = montaDados(contexto, Tabelas.HORAS_PARALIZACOES_EQUIPES, new String[]{"idParalizacao", "horaInicio", "horaTermino",
				"descricao", "justificativa", "idComponente", "data"},"id", "idParalizacaoEquipe = " + dados.get("id"));
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		json.put("idEquipe", dados.get("idEquipe"));
		json.put("nome", dados.get("nome"));
		json.put("idServico", dados.get("idServico"));
		json.put("descricaoServico", ((Map<String, String>)(servico).get(0)).get("descricao"));
		json.put("idAtividade", ((Map<String, String>)(atividade).get(0)).get("idAtividade"));
		json.put("id", ((Map<String, String>)(atividade).get(0)).get("id"));
		json.put("idAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("servico") : "");
		json.put("descricaoAtividade", ((Map<String, String>)(atividade).get(0)).get("descricao"));
		json.put("descricaoAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("descricao") : "");
		json.put("prod", "");
		json.put("origem", "null");
		json.put("destino", "null");
		JSONObject horario = null;
		for(Object hora : horasParalizacao){
			Map<String, String> dado = (Map<String, String>)hora;
			horario = new JSONObject();
			horario.put("idParalizacao", dado.get("idParalizacao"));
			horario.put("justificativa", dado.get("justificativa"));
			horario.put("idComponente", dado.get("idComponente").equals("0") ? "null" : dado.get("idComponente"));
			horario.put("horaInicio", dado.get("horaInicio"));
			horario.put("horaTermino", dado.get("horaTermino"));
			horario.put("descricao", dado.get("descricao"));
			horario.put("data", dado.get("data"));
			array.put(horario);
		}
		json.put("horario", new JSONArray());
		json.put("paralizacoes", array);
		equipes.put(json);
	}
	
	private static void montaEquipe(Context contexto, Map<String, String> dados, List<Object> lista) throws JSONException{
		List<Object> servico = montaDados(contexto, Tabelas.SERVICOS, new String[]{"id", "descricao"}, "id",
				"id = " + dados.get("idServico"));
		
		List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade", "descricao"}, "contador",
				"contador = " + dados.get("idAtividade"));
		List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico", "descricao"},
			"contador", "contador = " + dados.get("idAtivServ"));
		List<Object> horas = montaDados(contexto, Tabelas.HORAS_EQUIPES, new String[]{"idHorario", "horarioTrabalho", "horaInicio", "horaTermino", 
				"descricao", "idServico", "idFuncao", "data", "diaSemana"},"id", "idTrabalhoEquipe = " + dados.get("id"));
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		json.put("idEquipe", dados.get("idEquipe"));
		json.put("nome", dados.get("nome"));
		json.put("idServico", dados.get("idServico"));
		json.put("descricaoServico", ((Map<String, String>)(servico).get(0)).get("descricao"));
		json.put("idAtividade", ((Map<String, String>)(atividade).get(0)).get("idAtividade"));
		json.put("id", ((Map<String, String>)(atividade).get(0)).get("id"));
		json.put("idAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("servico") : "");
		json.put("descricaoAtividade", ((Map<String, String>)(atividade).get(0)).get("descricao"));
		json.put("descricaoAtivServ", (Map<String, String>)(ativServ).get(0) != null ? ((Map<String, String>)(ativServ).get(0)).get("descricao") : "");
		json.put("prod", dados.get("prod"));
		json.put("origem", dados.get("origem").equals("") ? "null" : dados.get("origem"));
		json.put("destino", dados.get("destino").equals("") ? "null" : dados.get("destino"));
		JSONObject horario = null;
		for(Object hora : horas){
			Map<String, String> dado = (Map<String, String>)hora;
			horario = new JSONObject();
			horario.put("horarioTrabalho", dado.get("horarioTrabalho"));
			horario.put("idHorario", dado.get("idHorario"));	
			horario.put("idServico", dado.get("idServico"));
//			horario.put("idFuncao", dado.get("idFuncao"));
//			horario.put("codigoParalizacao", dado.get("codigoParalizacao"));
			horario.put("diaSemana", dado.get("diaSemana"));
			horario.put("horaInicio", dado.get("horaInicio"));
			horario.put("horaTermino", dado.get("horaTermino"));
			horario.put("descricao", dado.get("descricao"));
			horario.put("data", dado.get("data"));
			array.put(horario);
		}
		json.put("horario", array);
		array = new JSONArray();
		horario = null;
		if(!lista.isEmpty()){
			for(Object paralizacao : lista){
				Map<String, String> dadoParalizacao = (Map<String, String>)paralizacao;
				List<Object> horasParalizacao = montaDados(contexto, Tabelas.HORAS_PARALIZACOES_EQUIPES, new String[]{"idParalizacao", "horaInicio", "horaTermino",
						"descricao", "justificativa", "idComponente", "data"},"id", "idParalizacaoEquipe = " + dados.get("id"));				
				for(Object hora : horasParalizacao){
					Map<String, String> dado = (Map<String, String>)hora;
					horario = new JSONObject();
					horario.put("idParalizacao", dado.get("idParalizacao"));
					horario.put("justificativa", dado.get("justificativa"));
					horario.put("idComponente", dado.get("idComponente").equals("0") ? "null" : dado.get("idComponente"));
					horario.put("horaInicio", dado.get("horaInicio"));
					horario.put("horaTermino", dado.get("horaTermino"));
					horario.put("descricao", dado.get("descricao"));
					horario.put("data", dado.get("data"));
					array.put(horario);
				}
			}
			json.put("paralizacoes", array);
		}else{
			json.put("paralizacoes", new JSONArray());
		}
		equipes.put(json);
	}
	
	public static boolean preparaExportacao(Context contexto){
		pessoas = new JSONArray();
		equipes = new JSONArray();
		equipamentos = new JSONArray();

		if(!preparaIndividuais(contexto)){
			return false;
		}
		if(!preparaEquipes(contexto)){
			return false;
		}
		return true;
	}
	
}
