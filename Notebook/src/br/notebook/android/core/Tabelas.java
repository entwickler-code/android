package br.notebook.android.core;

/* Enum responsável por armazenar as informações do banco SQLite*/


public enum Tabelas {
	SERVICOS("servicos"), ATIVIDADES("atividades"), ATIVIDADES_SERVICOS("atividadesservicos"), EQUIPAMENTOS("equipamentos"), 
	SERVICO_EQUIPAMENTOS("servicosequipamentos"), HORAS_INDIVIDUAIS("horasindividuais"),TRABALHO_INDIVIDUAL("trabalhoindividual"), 
	TRABALHO_EQUIPES("trabalhoequipe"), HORAS_EQUIPES("horasequipes"), PARALIZACAO_EQUIPES("paralizacaoequipes"), OBRA("obras"), 
	HORAS_PARALIZACOES_EQUIPES("horasparalizacoesequipes"), PARALIZACAO_INDIVIDUAL("paralizacaoindividual"), HORAS_PARALIZACOES_INDIVIDUAL("horasparalizacoesindividuais"),
	MANUTENCAO("manutencao"), PESSOAS("pessoas"), FUNCOES("funcoes"),EQUIPES("equipes"), HORARIOS("horarios"), PARALIZACOES("paralizacoes");

	
	public static final int version = 1;
	private String nomeTabela;
	
	private Tabelas(String tabela) {
		this.nomeTabela = tabela;
	}
	
	public String getNomeTabela(){
		return this.nomeTabela;
	}
	
	
	public String whereSQL(){
		switch (this) {
		case ATIVIDADES:
			return " frenteObra = %s ";
		case ATIVIDADES_SERVICOS:
			return " frenteObra = %s and id = %s and idAtividade = %s ";
		case FUNCOES:
			return " idFuncao = %s";
		case HORARIOS:
			return " id = %s and horarioTrabalho = %s";
		case HORAS_EQUIPES:
			return " idTrabalhoEquipe = %s";
		case HORAS_INDIVIDUAIS:
			return " idIndividual = %s";
		case SERVICO_EQUIPAMENTOS:
			return " id = %s";
		case HORAS_PARALIZACOES_EQUIPES:
			return " idParalizacaoEquipe = %s";
		case HORAS_PARALIZACOES_INDIVIDUAL:
			return " idIndividual = %s ";
		case MANUTENCAO:
			return " categoria = %s ";
		default:
			return "";
		}
	}
	
	public String deleteSQL(){
		switch (this) {
		case ATIVIDADES:
			return "DELETE FROM atividades; ";
		case SERVICOS:
			return "DELETE FROM  servicos; ";		
		case ATIVIDADES_SERVICOS:
			return "DELETE FROM atividadesservicos; ";
		case PARALIZACOES:
			return "DELETE FROM  paralizacoes; ";
		case EQUIPAMENTOS:
			return "DELETE FROM  equipamentos; ";
		case MANUTENCAO:
			return "DELETE FROM  manutencao; ";
		case PESSOAS:
			return "DELETE FROM  pessoas; ";
		case FUNCOES:
			return "DELETE FROM  funcoes; ";
		case EQUIPES:
			return "DELETE FROM  equipes; ";
		case HORARIOS:
			return "DELETE FROM horarios; ";
		case HORAS_INDIVIDUAIS:
			return "DELETE FROM horasindividuais; ";	
		case TRABALHO_INDIVIDUAL:
			return "DELETE FROM trabalhoindividual; ";
		case HORAS_EQUIPES:
			return "DELETE FROM horasequipes; ";
		case TRABALHO_EQUIPES:
			return "DELETE FROM trabalhoequipe; ";
		case SERVICO_EQUIPAMENTOS:
			return "DELETE FROM servicosequipamentos; ";
		case HORAS_PARALIZACOES_EQUIPES:
			return "DELETE FROM horasparalizacoesequipes; ";
		case PARALIZACAO_EQUIPES:
			return "DELETE FROM paralizacaoequipes;";
		case PARALIZACAO_INDIVIDUAL:
			return "DELETE FROM paralizacaoindividual; ";
		case HORAS_PARALIZACOES_INDIVIDUAL:
			return "DELETE FROM horasparalizacoesindividuais; ";
		case OBRA:
			return "DELETE FROM obras; ";
		default:
			return "";
		}
	}
	
	public String createSQL(){
		switch (this) {
		case ATIVIDADES:
			return "CREATE TABLE IF NOT EXISTS atividades (contador integer not null primary key autoincrement,frenteObra integer NOT NULL ," +
					"id integer NOT NULL ,idAtividade integer NOT NULL ,descricao text);";
		case SERVICOS:
			return "CREATE TABLE IF NOT EXISTS servicos (obra text NOT NULL,id integer NOT NULL primary key,descricao text NOT NULL);";		
		case ATIVIDADES_SERVICOS: 
			return "CREATE TABLE IF NOT EXISTS atividadesservicos (contador integer primary key not null, " +
					"frenteObra integer NOT NULL,id integer NOT NULL, idAtividade integer NOT NULL,servico integer NOT NULL,descricao text);";
		case PARALIZACOES:
//			return "CREATE TABLE IF NOT EXISTS paralizacoes (id INTEGER PRIMARY KEY NOT NULL,descricao TEXT NOT NULL);";
			return "CREATE TABLE IF NOT EXISTS paralizacoes (id TEXT PRIMARY KEY NOT NULL,descricao TEXT NOT NULL);";
		case EQUIPAMENTOS:
			return "CREATE TABLE IF NOT EXISTS equipamentos (id integer NOT NULL primary key, prefixo text NOT NULL," +
					"nome text NOT NULL,servicoPadrao integer,descricao text,categoria integer);";
		case MANUTENCAO:
			return "CREATE TABLE IF NOT EXISTS manutencao (contador integer not null primary key autoincrement,id integer NOT NULL ," +
					"descricao text NOT NULL,categoria integer NOT NULL);";
		case PESSOAS:
			return "CREATE TABLE IF NOT EXISTS pessoas (id integer NOT NULL  primary key,matricula text NOT NULL,nome text NOT NULL," +
					"funcao integer NOT NULL,servicoPadrao integer NOT NULL,descricao text NOT NULL);";
		case FUNCOES:
			return "CREATE TABLE IF NOT EXISTS funcoes (contador integer not null primary key,id integer NOT NULL,descricao text NOT NULL," +
					"idFuncao integer NOT NULL,descricaoFuncao text NOT NULL);";
		case EQUIPES:
			return "CREATE TABLE IF NOT EXISTS equipes ( id integer NOT NULL primary key,nomeEquipe text NOT NULL," +
					"horarioTrabalho integer NOT NULL);";
		case HORARIOS:
			return "CREATE TABLE IF NOT EXISTS horarios (contador integer primary key autoincrement,id integer NOT NULL," +
					"horarioTrabalho integer NOT NULL,horaInicio text NOT NULL,nomeEquipe text not  null,horaTermino text NOT NULL," +
					"descricao text NOT NULL,codigoParalizacao integer, diaSemana integer);";
		case HORAS_INDIVIDUAIS:
			return "CREATE TABLE IF NOT EXISTS horasindividuais (id integer primary key autoincrement, idIndividual integer NOT NULL," +
					"horaInicio text NOT NULL, horaTermino text NOT NULL,descricao text NOT NULL, idServico integer not null, tipo text not null, data text);";
		case TRABALHO_INDIVIDUAL:
			return "CREATE TABLE IF NOT EXISTS trabalhoindividual (id integer primary key autoincrement, tipo integer NOT NULL, " +
					"idTipo integer not null, idServico integer not null, idAtividade integer NOT NULL, idAtivServ integer NOT NULL, " +//);";
					"horoInicial text, horoFinal text);"; //Adicionado 04/02 - Horimetro Inicial e Final
		case HORAS_EQUIPES:
			return "CREATE TABLE IF NOT EXISTS horasequipes (id integer primary key autoincrement, idTrabalhoEquipe integer NOT NULL," +
					"horaInicio text NOT NULL, horaTermino text NOT NULL,descricao text NOT NULL, idHorario integer," +
					" horarioTrabalho integer, idServico integer, codigoParalizacao integer, idFuncao integer, data text, diaSemana integer);";
		case TRABALHO_EQUIPES:
			return "CREATE TABLE IF NOT EXISTS trabalhoequipe (id integer primary key autoincrement, idEquipe integer NOT NULL, " +
					"prod text NOT NULL, origem text not null, destino text not null, idServico integer not null, idAtividade integer NOT NULL, " +
					"idAtivServ integer NOT NULL);";
		case SERVICO_EQUIPAMENTOS:
			return "CREATE TABLE IF NOT EXISTS servicosequipamentos (id integer primary key not null, descricao text not null); ";
		case PARALIZACAO_EQUIPES:
			return "CREATE TABLE IF NOT EXISTS paralizacaoequipes (id integer primary key autoincrement, idEquipe integer NOT NULL, " +
					"idServico integer not null, idAtividade integer NOT NULL, idAtivServ integer NOT NULL);";			
		case HORAS_PARALIZACOES_EQUIPES:
			return "CREATE TABLE IF NOT EXISTS horasparalizacoesequipes (id integer primary key autoincrement, " +
					"idParalizacaoEquipe integer NOT NULL,horaInicio text NOT NULL, horaTermino text NOT NULL,descricao text NOT NULL, " +
					"justificativa text NOT NULL, idComponente integer, idParalizacao integer, data text);";
		case PARALIZACAO_INDIVIDUAL:
			return "CREATE TABLE IF NOT EXISTS paralizacaoindividual (id integer primary key autoincrement, tipo integer NOT NULL, " +
					"idTipo integer not null, idServico integer not null, idAtividade integer NOT NULL, idAtivServ integer);";
		case HORAS_PARALIZACOES_INDIVIDUAL:
			return "CREATE TABLE IF NOT EXISTS horasparalizacoesindividuais (id integer primary key autoincrement, idIndividual integer NOT NULL," +
					"horaInicio text NOT NULL, horaTermino text NOT NULL,descricao text NOT NULL, justificativa text NOT NULL, " +
					"idComponente integer, idParalizacao integer not null, categoria integer, data text );";
		case OBRA:
			return "CREATE TABLE IF NOT EXISTS obras (id integer primary key autoincrement, obra TEXT not null, status integer not null, usuario integer, data text, tipo text); ";
		default:
			return "";
		}
	}
	
	public String dropSQL(){
		switch (this) {
		case ATIVIDADES:
			return "DROP TABLE atividades;";
		case SERVICOS:
			return "DROP TABLE servicos;";		
		case ATIVIDADES_SERVICOS:
			return "DROP TABLE atividadesservicos;";
		case PARALIZACOES:
			return "DROP TABLE paralizacoes;";
		case EQUIPAMENTOS:
			return "DROP TABLE equipamentos;";
		case MANUTENCAO:
			return "DROP TABLE manutencao;";
		case PESSOAS:
			return "DROP TABLE pessoas;";
		case FUNCOES:
			return "DROP TABLE funcoes;";
		case EQUIPES:
			return "DROP TABLE equipes;";
		case HORARIOS:
			return "DROP TABLE horarios;";
		case HORAS_INDIVIDUAIS:
			return "DROP TABLE horasindividuais;";
		case TRABALHO_INDIVIDUAL:
			return "DROP TABLE trabalhoindividual;";
		case HORAS_EQUIPES:
			return "DROP TABLE horasequipes; ";
		case TRABALHO_EQUIPES:
			return "DROP TABLE trabalhoequipe; ";
		case SERVICO_EQUIPAMENTOS:
			return "DROP TABLE servicosequipamentos; ";
		case HORAS_PARALIZACOES_EQUIPES:
			return "DROP TABLE horasparalizacoesequipes; ";
		case PARALIZACAO_EQUIPES:			
			return "DROP TABLE paralizacaoequipes; ";
		case PARALIZACAO_INDIVIDUAL:	
			return "DROP TABLE paralizacaoindividual; ";
		case HORAS_PARALIZACOES_INDIVIDUAL:
			return "DROP TABLE horasparalizacoesindividuais; ";
		case OBRA:
			return "DROP TABLE obras; ";
		default:
			return "";
		}
	}
}
