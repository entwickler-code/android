package br.notebook.android.telas;

import java.util.List;

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

public class ParalizacaoIndividual2 extends Activity {
	
	private List<Object> servicos;
	private List<Object> atividades;
	private List<Object> obras;
	
	private Object servicoSelecionado;
	private Object atividadeSelecionado;
	
	private boolean update;
	
	private int tipo;
	private String id;
//	private String idUpdate;

	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		
		tipo = (sp.getString("tipo", "")).equals(ParalizacaoIndividual.MATRICULA) ? 1 : 2;
//		idUpdate = sp.getString("idUpdate", "");
		update = sp.getBoolean("update", false);
//		setTitle(sp.getString("id", "") + " - " + sp.getString("nome", ""));
		setTitle(sp.getString("nomeJanela", "") + " - " + sp.getString("nome", ""));
		id = sp.getString("id", "");
	}
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paralizacao_individual2);
		carregaVars();
		
		final Spinner obra = (Spinner)findViewById(R.id.frenteObra);
		final Spinner atividade = (Spinner)findViewById(R.id.ativFrenteObra);
		final Spinner servico = (Spinner)findViewById(R.id.servicoFrenteObra);
		
		obras = montaDados(Tabelas.SERVICOS, new String[]{"id", "descricao"}, "descricao", null);
		obra.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoIndividual2.this, obras, "descricao"));
		obra.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.SERVICO, obras, "id", update, getSharedPreferences("DADOS", 0)));
		obra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				servicoSelecionado = BancoDeDados.pegarValor(obras, "descricao", (String)arg0.getItemAtPosition(arg2));
				atividades = montaDados(Tabelas.ATIVIDADES, new String[]{"contador", "id", "frenteObra", "idAtividade", "descricao"}, 
						"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES, servicoSelecionado, new String[]{"id"}));
				
				atividade.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoIndividual2.this, atividades, "descricao"));
				atividade.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE, atividades, "idAtividade", update, getSharedPreferences("DADOS", 0)));
				if(update){
					atividadeSelecionado = BancoDeDados.pegarValor(atividades, "descricao", (String)atividade.getItemAtPosition(atividade.getSelectedItemPosition()));
					servicos = montaDados(Tabelas.ATIVIDADES_SERVICOS, new String[]{"contador", "servico", "descricao"}, 
							"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES_SERVICOS, atividadeSelecionado, new String[]{"id", "frenteObra", "idAtividade"}));
					servico.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoIndividual2.this, BancoDeDados.listaCampoEmBranco(servicos), "descricao"));
					servico.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE_SERVICO, BancoDeDados.listaCampoEmBranco(servicos), "contador", update, getSharedPreferences("DADOS", 0)));
				}else{
					servico.setAdapter(new ArrayAdapter<String>(ParalizacaoIndividual2.this, android.R.layout.simple_dropdown_item_1line, new String[]{}));
				}				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		atividades = montaDados(Tabelas.ATIVIDADES, new String[]{"contador", "id", "frenteObra", "idAtividade", "descricao"}, 
			"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES, obras.get(0), new String[]{"id"}));
		atividade.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoIndividual2.this, atividades, "descricao"));
		
		atividade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				atividadeSelecionado = BancoDeDados.pegarValor(atividades, "descricao", (String)arg0.getItemAtPosition(arg2));
				servicos = montaDados(Tabelas.ATIVIDADES_SERVICOS, new String[]{"contador", "servico", "descricao"}, 
						"descricao", BancoDeDados.montaWhere(Tabelas.ATIVIDADES_SERVICOS, atividadeSelecionado, new String[]{"id", "frenteObra", "idAtividade"}));
				servico.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoIndividual2.this, BancoDeDados.listaCampoEmBranco(servicos), "descricao"));
				servico.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.ATIVIDADE_SERVICO, BancoDeDados.listaCampoEmBranco(servicos), "contador", update, getSharedPreferences("DADOS", 0)));
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
//		
		//ServicoFrenteObra não é obrigatório
		
		Button btn = (Button)findViewById(R.id.btCont2Para);
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
					if(!update){
						values.put("tipo", tipo);
						values.put("idTipo", id);
					}
					values.put("idServico", (String)BancoDeDados.pegarValor(servicoSelecionado, "id"));
					values.put("idAtividade", (String)BancoDeDados.pegarValor(atividadeSelecionado, "contador"));
					values.put("idAtivServ", ativServSelecionado != null ? (String)BancoDeDados.pegarValor(ativServSelecionado, "contador") : " ");
					BancoDeDados banco = new BancoDeDados(ParalizacaoIndividual2.this, Tabelas.PARALIZACAO_INDIVIDUAL, Tabelas.version);
					SharedPreferences sp = getSharedPreferences("DADOS", 0);
					long idIndividual = banco.salvarDados(values, update, sp);
					SharedPreferences.Editor editor = sp.edit();
					editor.putLong("idIndividual", idIndividual);
					editor.commit();
					startActivity(new Intent("CadastroParalizacao"));
				}else{
					montaAlerta("Frente de Obra e Atividades são obrigatórios.");
				}
			}
		});
		
	}
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Apropriação Individual");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
    @Override
    protected void onPause() { // Chama o metodo OnPause para Finalizar a Activity
    	super.onPause();
    	finish();
    }
    
}

