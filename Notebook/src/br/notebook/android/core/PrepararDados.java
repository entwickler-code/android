package br.notebook.android.core;

/* Classe responsável por manipular os dados do Banco, gerar Insert */

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrepararDados {
	
	public static String prepararTabela(String dados, Tabelas tabela) throws JSONException{
		try {
			JSONObject json = new JSONObject(dados);
			switch (tabela) {
			case ATIVIDADES:
				return montaInsert(Tabelas.ATIVIDADES.getNomeTabela(), (JSONArray)json.get("FrentesObraAtividades"));	

			case SERVICOS:
				return montaInsert(Tabelas.SERVICOS.getNomeTabela(), addObra((JSONArray)json.get("FrentesObraServicos"), json.getString("Obra")));	
			
			case ATIVIDADES_SERVICOS:
				return montaInsert(Tabelas.ATIVIDADES_SERVICOS.getNomeTabela(), (JSONArray)json.get("FrentesObraAtividadesServicos"));	

			case PARALIZACOES:
				return montaInsert(Tabelas.PARALIZACOES.getNomeTabela(), (JSONArray)json.get("Paralizacoes"));				

			case EQUIPAMENTOS:
				return montaInsert(Tabelas.EQUIPAMENTOS.getNomeTabela(), (JSONArray)json.get("EquipamentosAlocadosContrato"));	

			case MANUTENCAO:
				return montaInsert(Tabelas.MANUTENCAO.getNomeTabela(), (JSONArray)json.get("ComponentesManutencao"));	

			case PESSOAS:
				return montaInsert(Tabelas.PESSOAS.getNomeTabela(), (JSONArray)json.get("PessoasAlocadasContratos"));	

			case FUNCOES:
				return montaInsert(Tabelas.FUNCOES.getNomeTabela(), (JSONArray)json.get("ServicosFuncoes"));	

			case EQUIPES:
				return montaInsert(Tabelas.EQUIPES.getNomeTabela(), (JSONArray)json.get("EquipesTrabalho"));				
			
			case HORARIOS:
				return montaInsert(Tabelas.HORARIOS.getNomeTabela(), (JSONArray)json.get("HorariosTrabalhoEquipes"));
			
			case SERVICO_EQUIPAMENTOS:
				return montaInsert(Tabelas.SERVICO_EQUIPAMENTOS.getNomeTabela(), (JSONArray)json.get("ServicosEquipamentos")); 
			
			case OBRA:
				return montaInsert(Tabelas.OBRA.getNomeTabela(), (String)json.get("Obra"), (Integer)json.get("Usuario"), "\""+(String)json.get("DataArquivo")+"\"", "\"I\"");
				
			default:
				return ""; 
			}
		} catch (JSONException e) {
			throw new JSONException("Arquivo não é válido.");
		}
	}
	
	private static JSONArray addObra(JSONArray array, String obra){
		JSONObject dado = null;
		JSONArray retorno = new JSONArray();
		for (int i = 0; i < array.length(); i++) {
			try {
				dado = new JSONObject(array.getString(i));
				dado.put("obra", obra);
				retorno.put(dado);
			}catch (Exception e) {
			}	
		}
		return retorno;
	}

	private static String montaInsert(String nomeTabela, String obj, int usuario, String data, String tipo){
		return "INSERT INTO " + nomeTabela + " ( obra, status, usuario, data, tipo ) VALUES ( "+ obj +", 0, "+usuario+", "+data+", "+tipo+")";
		
	}
	
	private static String montaInsert(String nomeTabela, JSONArray array){
		String sql = "INSERT INTO " + nomeTabela + " ( ";
		StringBuilder retorno = new StringBuilder();
		StringBuilder chaves = null;
		StringBuilder valores = null;
		JSONObject dado = null;
		for (int i = 0; i < array.length(); i++) {
			try {
				valores = new StringBuilder();
				chaves = new StringBuilder();
				dado = new JSONObject(array.getString(i));
				Iterator it = dado.keys();
				StringBuilder campo  = null;
				while(it.hasNext()){
					campo = new StringBuilder((String)it.next());
					try{
						int valor = dado.getInt(campo.toString());
						valores.append(valor);
					}catch(JSONException e){
						String valor = dado.getString(campo.toString());
						if(campo.toString().equals("codigoParalizacao")){
							valores.append("null");
						}else{
							valores.append('\"'+valor+'\"');
						}
					}
					if(campo.toString().equals("nomeEquipamento")){
						chaves.append("nome");
					}else{
						chaves.append(campo);
					}
					if(it.hasNext()){
						chaves.append(", ");
						valores.append(",");
					}
				}
				chaves.append(") VALUES (");
				valores.append("); ");
				retorno.append(sql + chaves.toString() + valores.toString());
			} catch (JSONException e) {
			}
		}
		return retorno.toString();
	}

}
