package br.notebook.android.menus;

/* Classe responsável por preparar os dados para serem exportados para um arquivo
 * */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.Toast;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;


public class PrepararArquivo{
	
//	private static Context contexto = null;
	
	private static Object obra;
	
	public static final String path = Environment.DIRECTORY_DCIM+"/Constran/exportados/";
	
	public static JSONArray arquivo = null;
	
	public static List<Object> montaDados(Context contexto, Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(contexto, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
	public static void createArquivoExportacao(Context context){
		try{
//			contexto = context;
			List<Object> obj = montaDados(context, Tabelas.OBRA, new String[]{"id", "obra", "usuario", "data", "tipo"}, null, null);
			obra = obj.get(0);
			if (PrepararDados.preparaExportacao(context)){
				String nome = montarNome(obra);
				File arq = pegarArquivos(PrepararArquivo.path+nome);
				if(arq.exists()){
					arq.delete();
				}
				PrintWriter saida = new PrintWriter(new BufferedWriter(new FileWriter(arq, false)));
				saida.println("["+PrepararDados.exportarArquivo(context, obra)+"]");
				saida.close();
				Toast.makeText(context, "Arquivo "+nome+" pronto...", Toast.LENGTH_LONG).show();
			}
		} catch (IOException e) {
			montaAlerta(context, "Problema ao preparar arquivo");			
		}catch(Exception e){
			montaAlerta(context, "Nenhum dado encontrado.");
		}
	}
	
	private static void montaAlerta(Context context, String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("Menu Principal");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
	public static String montarNome(Object obj){
		String usuario = BancoDeDados.pegarValor(obj, "usuario");
		String obra = BancoDeDados.pegarValor(obj, "obra");
//		String[] data = BancoDeDados.pegarValor(obj, "data").split("-");
		String data = BancoDeDados.pegaDataAtual("yyyyMMdd");
//		StringBuilder novaData = new StringBuilder(""); 
//		for(int i = 0; i < data.length;i++){
//			novaData.append(data[i]);
//		}
//		return "DadosApropriados_"+usuario+"_"+obra+"_"+novaData.toString()+".json";
		return "DadosApropriados_"+usuario+"_"+obra+"_"+data+".json";
	}
	
	public static File pegarArquivos(String caminho){
		return Environment.getExternalStoragePublicDirectory(caminho);
	}
	
//	private static JSONArray montaEstruturaSaida(String nome, JSONArray array){
//		JSONObject json = new JSONObject();
//		try {
//			json.put(nome, array);
//			arquivo.put(json);
//		} catch (JSONException e) {
//		}
//		return new JSONArray();
//	}
//	public static boolean preparaArquivos(Context contexto){
//		arquivo = new JSONArray(); 
//		JSONArray array = new JSONArray();
//		
//		
//		List<Object> trabalhoIndividual = montaDados(contexto, Tabelas.TRABALHO_INDIVIDUAL, new String[]{"id","tipo",
//				"idTipo","idServico","idAtividade", "idAtivServ"}, "id", null);
//		for(Object obj : trabalhoIndividual){
//			Map<String, String> dados = (Map<String, String>)obj;
//			List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade"}, "contador",
//					"contador = " + dados.get("idAtividade"));
//			List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico"},
//					"contador", "contador = " + dados.get("idAtivServ"));
//			List<Object> horas = montaDados(contexto, Tabelas.HORAS_INDIVIDUAIS, new String[]{"tipo", "idServico", "horaInicio", "horaTermino", "descricao", "data"},
//					"id", "idIndividual = " + dados.get("id"));
//			try {
////				arquivo.put(montarIndividual(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas, "trabalhoIndividual", true));
//				array.put(montarIndividual(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas, "trabalhoIndividual", true));
//			} catch (JSONException e) {
//				return false;
//			}
//		}
//		
//		array = montaEstruturaSaida("trabalhoIndividual", array);
//		
//		List<Object> trabalhoEquipes = montaDados(contexto, Tabelas.TRABALHO_EQUIPES, new String[]{"id","idEquipe",
//				"prod","origem","destino", "idServico", "idAtividade", "idAtivServ"}, "id", null);
//		for(Object obj : trabalhoEquipes){
//			Map<String, String> dados = (Map<String, String>)obj;
//			List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade"}, "contador",
//					"contador = " + dados.get("idAtividade"));
//			List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico"},
//					"contador", "contador = " + dados.get("idAtivServ"));
//			List<Object> horas = montaDados(contexto, Tabelas.HORAS_EQUIPES, new String[]{"idHorario", "horarioTrabalho", "horaInicio", "horaTermino", 
//					"descricao", "idServico", "idFuncao", "data", "diaSemana"},"id", "idTrabalhoEquipe = " + dados.get("id"));
//			try {
////				arquivo.put(montarEquipe(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas, "trabalhoEquipe", true));
//				array.put(montarEquipe(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas, "trabalhoEquipe", true));				
//			} catch (JSONException e) {
//				return false;
//			}
//		}
//		
//		array = montaEstruturaSaida("trabalhoEquipe", array);
//		
//		List<Object> paralizacaoEquipe = montaDados(contexto, Tabelas.PARALIZACAO_EQUIPES, new String[]{"id","idEquipe",
//				"idServico", "idAtividade", "idAtivServ"}, "id", null);
//		for(Object obj : paralizacaoEquipe){
//			Map<String, String> dados = (Map<String, String>)obj;
//			List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade"}, "contador",
//					"contador = " + dados.get("idAtividade"));
//			List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico"},
//					"contador", "contador = " + dados.get("idAtivServ"));
//			List<Object> horas = montaDados(contexto, Tabelas.HORAS_PARALIZACOES_EQUIPES, new String[]{"idParalizacao", "horaInicio", "horaTermino",
//					"descricao", "justificativa", "idComponente", "data"},"id", "idParalizacaoEquipe = " + dados.get("id"));
//			try {
////				arquivo.put(montarEquipe(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas,
////						"paralizacaoEquipe", false));
//				array.put(montarEquipe(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas,
//						"paralizacaoEquipe", false));
//			} catch (JSONException e) {
//				return false;
//			}
//		}
//		
//		array = montaEstruturaSaida("paralizacaoEquipe", array);
//		
//		List<Object> paralizacaoIndividual = montaDados(contexto, Tabelas.PARALIZACAO_INDIVIDUAL, new String[]{"id","tipo",
//				"idTipo","idServico","idAtividade", "idAtivServ"}, "id", null);
//		for(Object obj : paralizacaoIndividual){
//			Map<String, String> dados = (Map<String, String>)obj;
//			List<Object> atividade = montaDados(contexto, Tabelas.ATIVIDADES, new String[]{"id", "idAtividade"}, "contador",
//					"contador = " + dados.get("idAtividade"));
//			List<Object> ativServ = montaDados(contexto, Tabelas.ATIVIDADES_SERVICOS, new String[]{"servico"},
//					"contador", "contador = " + dados.get("idAtivServ"));
//			List<Object> horas = montaDados(contexto, Tabelas.HORAS_PARALIZACOES_INDIVIDUAL, new String[]{"idParalizacao", "horaInicio", "horaTermino", 
//					"justificativa", "idComponente", "descricao", "categoria", "data"}, "id", "idIndividual = " + dados.get("id"));
//			try {
////				arquivo.put(montarIndividual(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas, "paralizacaoIndividual", false));
//				array.put(montarIndividual(dados, (Map<String, String>)(atividade).get(0), (Map<String, String>)(ativServ).get(0), horas, "paralizacaoIndividual", false));
//			} catch (JSONException e) {
//				return false;
//			}
//		}
//		array = montaEstruturaSaida("paralizacaoIndividual", array);
//		
//		return true;
//	}
//	
//	private static JSONObject montarIndividual(Map<String, String> dados, Map<String, String> atividade, 
//			Map<String, String> ativServ, List<Object> horas, String estilo, boolean tipo) throws JSONException{
//		JSONObject json = new JSONObject();
//		JSONArray array = new JSONArray();
//		try {
////			json.put("tabela", estilo);
//			json.put("tipo", dados.get("tipo"));
//			json.put("dataArquivo", pegaValorObra("data"));
//			json.put("tipoArquivo", pegaValorObra("tipo"));
//			json.put("dataExportacao", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
//			json.put("usuario", pegaValorObra("usuario"));
//			json.put("obra", pegaValorObra("obra"));
//			json.put("idTipo", dados.get("idTipo"));
//			json.put("idServico", dados.get("idServico"));
//			json.put("idAtividade", atividade.get("idAtividade"));
//			json.put("id", atividade.get("id"));
//			json.put("idAtivServ", ativServ != null ? ativServ.get("servico") : "0");
//			JSONObject horario = null;
//			for(Object hora : horas){
//				Map<String, String> dado = (Map<String, String>)hora;
//				horario = new JSONObject();
//				if(tipo){
//					horario.put("tipo", dado.get("tipo"));
//					horario.put("idServico", dado.get("idServico"));
//				}else{
//					horario.put("idComponente", dado.get("idComponente"));
//					horario.put("justificativa", dado.get("justificativa"));
//					horario.put("idParalizacao", dado.get("idParalizacao"));
//					horario.put("categoria", dado.get("categoria"));
//				}
//				horario.put("horaInicio", dado.get("horaInicio"));
//				horario.put("data", dado.get("data"));
//				horario.put("horaTermino", dado.get("horaTermino"));
//				horario.put("descricao", dado.get("descricao"));
//				array.put(horario);
//			}
//			json.put("horario", array);
//			return json;
//		} catch (JSONException e) {
//			throw new JSONException("Problemas ao montar o arquivo");
//		} catch (Exception e) {
//			throw new JSONException("Problemas ao montar o arquivo");
//		}
//	}
//	
//	private static String pegaValorObra(String campo){
////		BancoDeDados banco = new BancoDeDados(contexto, Tabelas.OBRA, Tabelas.version);
////		Object obj = (banco.consultaDados(new String[]{"id", "obra", "usuario", "data", "tipo"}, null, null)).get(0);
//		return BancoDeDados.pegarValor(obra, campo);
//		
//	}
//	
//	private static JSONObject montarEquipe(Map<String, String> dados, Map<String, String> atividade, 
//			Map<String, String> ativServ, List<Object> horas, String estilo, boolean tipo) throws JSONException{
//		JSONObject json = new JSONObject();
//		JSONArray array = new JSONArray();
//		try {
////			json.put("tabela", estilo);
//			json.put("dataArquivo", pegaValorObra("data"));
//			json.put("dataExportacao", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
//			json.put("tipoArquivo", pegaValorObra("tipo"));
//			json.put("usuario", pegaValorObra("usuario"));
//			json.put("obra", pegaValorObra("obra"));
//			json.put("idEquipe", dados.get("idEquipe"));
//			if(tipo){
//				json.put("prod", dados.get("prod"));
//				json.put("origem", dados.get("origem"));
//				json.put("destino", dados.get("destino"));
//			}
//			json.put("idServico", dados.get("idServico"));
//			json.put("idAtividade", atividade.get("idAtividade"));
//			json.put("id", atividade.get("id"));
//			json.put("idAtivServ", ativServ != null ? ativServ.get("servico") : "0");
//			JSONObject horario = null;
//			for(Object hora : horas){
//				Map<String, String> dado = (Map<String, String>)hora;
//				horario = new JSONObject();
//				if(tipo){
//					horario.put("horarioTrabalho", dado.get("horarioTrabalho"));
//					horario.put("idHorario", dado.get("idHorario"));	
//					horario.put("idServico", dado.get("idServico"));
//					horario.put("idFuncao", dado.get("idFuncao"));
//					horario.put("codigoParalizacao", dado.get("codigoParalizacao"));
//					horario.put("diaSemana", dado.get("diaSemana"));
//				}else{
//					horario.put("idParalizacao", dado.get("idParalizacao"));
//					horario.put("justificativa", dado.get("justificativa"));
//					horario.put("idComponente", dado.get("idComponente"));
//				}
//				horario.put("horaInicio", dado.get("horaInicio"));
//				horario.put("horaTermino", dado.get("horaTermino"));
//				horario.put("descricao", dado.get("descricao"));
//				horario.put("data", dado.get("data"));
//				array.put(horario);
//			}
//			json.put("horario", array);
//			return json;
//		} catch (JSONException e) {
//			throw new JSONException("Problemas ao montar o arquivo");
//		} catch (Exception e) {
//			throw new JSONException("Problemas ao montar o arquivo");
//		}
//	}

}
