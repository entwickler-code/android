package br.notebook.android.telas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ApropriacaoEquipe2 extends Activity {
	
	private List<Object> servicos;
	private List<Object> atividades;
	private List<Object> obras;
	
	private Object servicoSelecionado;
	private Object atividadeSelecionado;
	
	private String idEquipe;
	private String prod;
	private String origem;
	private String destino;
	private String horarioTrabalho;
	
	private boolean update;
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		setTitle(sp.getString("nomeEquipe", ""));
		idEquipe = sp.getString("id", "");
		horarioTrabalho = sp.getString("horarioTrabalho", "");
		prod = sp.getString("prod", "");
		origem = sp.getString("origem", "");
		destino = sp.getString("destino", "");
		update = sp.getBoolean("update", false);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
try{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apropriacao_equipe2);
		
		carregaVars();
		final Spinner obra = (Spinner)findViewById(R.id.frenteObra);
		final Spinner atividade = (Spinner)findViewById(R.id.ativFrenteObra);
		final Spinner servico = (Spinner)findViewById(R.id.servicoFrenteObra);
		
		obras = montaDados(Tabelas.SERVICOS, new String[]{"id", "descricao"}, "descricao", null);
		obra.setAdapter(BancoDeDados.montaAdaptador(ApropriacaoEquipe2.this, obras, "descricao"));
		obra.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.SERVICO, obras, "id", update, getSharedPreferences("DADOS", 0)));
		obra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				servicoSelecionado = BancoDeDados.pegarValor(obras, "descricao", (String)arg0.getItemAtPosition(arg2));
				atividades = montaDados(Tabelas.ATIVIDADES, new String[]{"contador", "id", "frenteObra", "idAtividade", "descricao"}, 
						"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES, servicoSelecionado, new String[]{"id"}));
				
				atividade.setAdapter(BancoDeDados.montaAdaptador(ApropriacaoEquipe2.this, atividades, "descricao"));
				atividade.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE, atividades, "idAtividade", update, getSharedPreferences("DADOS", 0)));
				if(update){
					atividadeSelecionado = BancoDeDados.pegarValor(atividades, "descricao", (String)atividade.getItemAtPosition(atividade.getSelectedItemPosition()));
					servicos = montaDados(Tabelas.ATIVIDADES_SERVICOS, new String[]{"contador", "servico", "descricao"}, 
							"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES_SERVICOS, atividadeSelecionado, new String[]{"id", "frenteObra", "idAtividade"}));
					servico.setAdapter(BancoDeDados.montaAdaptador(ApropriacaoEquipe2.this, BancoDeDados.listaCampoEmBranco(servicos), "descricao"));
					servico.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE_SERVICO, BancoDeDados.listaCampoEmBranco(servicos), "contador", update, getSharedPreferences("DADOS", 0)));
				}else{
					servico.setAdapter(new ArrayAdapter<String>(ApropriacaoEquipe2.this, android.R.layout.simple_dropdown_item_1line, new String[]{}));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		atividades = montaDados(Tabelas.ATIVIDADES, new String[]{"id", "frenteObra", "idAtividade", "descricao"}, 
			"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES, obras.get(0), new String[]{"id"}));
		atividade.setAdapter(BancoDeDados.montaAdaptador(ApropriacaoEquipe2.this, atividades, "descricao"));
		atividade.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE, atividades, "idAtividade", update, getSharedPreferences("DADOS", 0)));
		
		atividade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				atividadeSelecionado = BancoDeDados.pegarValor(atividades, "descricao", (String)arg0.getItemAtPosition(arg2));
				servicos = montaDados(Tabelas.ATIVIDADES_SERVICOS, new String[]{"contador", "servico", "descricao"}, 
						"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES_SERVICOS, atividadeSelecionado, new String[]{"id", "frenteObra", "idAtividade"}));
				servico.setAdapter(BancoDeDados.montaAdaptador(ApropriacaoEquipe2.this, BancoDeDados.listaCampoEmBranco(servicos), "descricao"));
				servico.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE_SERVICO, BancoDeDados.listaCampoEmBranco(servicos), "contador", update, getSharedPreferences("DADOS", 0)));
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});

		Button btn = (Button)findViewById(R.id.btContinuar2);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Spinner frenteObra = (Spinner)findViewById(R.id.frenteObra);
				servicoSelecionado = BancoDeDados.pegarValor(obras, "descricao", (String)frenteObra.getSelectedItem());
				
				Spinner ativ = (Spinner)findViewById(R.id.ativFrenteObra);
				atividadeSelecionado = BancoDeDados.pegarValor(atividades, "descricao", (String)ativ.getSelectedItem());
				
				Spinner servicoFente = (Spinner)findViewById(R.id.servicoFrenteObra);
				Object ativServSelecionado = BancoDeDados.pegarValor(servicos, "descricao", (String)servicoFente.getSelectedItem());
				
				if((servicoSelecionado != null) && (atividadeSelecionado !=null)){
					ContentValues values = new ContentValues();
					values.put("idEquipe", idEquipe);
					values.put("prod", prod);
					values.put("origem", origem);
					values.put("destino", destino);
					values.put("idServico", (String)BancoDeDados.pegarValor(servicoSelecionado, "id"));
					values.put("idAtividade", (String)BancoDeDados.pegarValor(atividadeSelecionado, "contador"));
					values.put("idAtivServ", ativServSelecionado != null ? (String)BancoDeDados.pegarValor(ativServSelecionado, "contador") : " ");
					BancoDeDados banco = new BancoDeDados(ApropriacaoEquipe2.this, Tabelas.TRABALHO_EQUIPES, Tabelas.version);
					long id = banco.salvarDados(values, update, getSharedPreferences("DADOS", 0));
					if(id > 0){
						Map<String, String> dados = new HashMap<String, String>();
						dados.put("id", idEquipe);
						dados.put("horarioTrabalho", horarioTrabalho);
						if(!update){
							List<Object> horarios = montaDados(Tabelas.HORARIOS, new String[]{"id", "horarioTrabalho", "horaInicio", "horaTermino", "descricao", "codigoParalizacao", "diaSemana"}, 
									"horaInicio", BancoDeDados.montaWhere(Tabelas.HORARIOS, dados, new String[]{"id", "horarioTrabalho"}));
							for(Object horario : horarios){
								values = new ContentValues();
								values.put("idTrabalhoEquipe", id);
								values.put("idServico", 0);
								values.put("idFuncao", 0);
								values.put("idHorario", (String)BancoDeDados.pegarValor(horario, "id"));
								values.put("diaSemana", (String)BancoDeDados.pegarValor(horario, "diaSemana"));
								values.put("codigoParalizacao", (String)BancoDeDados.pegarValor(horario, "codigoParalizacao"));
								values.put("horarioTrabalho", (String)BancoDeDados.pegarValor(horario, "horarioTrabalho"));
								values.put("horaInicio", (String)BancoDeDados.pegarValor(horario, "horaInicio"));
								values.put("horaTermino", (String)BancoDeDados.pegarValor(horario, "horaTermino"));
								values.put("data", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
								values.put("descricao", (String)BancoDeDados.pegarValor(horario, "descricao"));
								banco = new BancoDeDados(ApropriacaoEquipe2.this, Tabelas.HORAS_EQUIPES, Tabelas.version);
								banco.salvarDados(values, false, null);
							}
						}
						SharedPreferences sp = getSharedPreferences("DADOS", 0);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("id", id+"");
						editor.putLong("idTrabalhoEquipe", id);
						editor.commit();
						startActivity(new Intent("ApropriacaoEquipe3"));
					}else{
						montaAlerta("Problema ao salvar dados.");
					}
				}else{
					montaAlerta("Frente de Obra e Atividades são obrigatórios.");
				}
			}
		});
}catch(Exception e){
	e.printStackTrace();
}
	}
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Apropriação Equipe");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(ApropriacaoEquipe2.this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
    @Override
    protected void onPause() { // Chama o metodo OnPause para Finalizar a Activity
    	super.onPause();
    	finish();
    }



}
