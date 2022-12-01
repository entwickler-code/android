package br.notebook.android.telas;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import br.fourlinux.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ParalizacaoIndividual4 extends Activity {
	
	private long idIndividual;
	private String horaInicio;
	private String horaTermino;
	private String descricao;
	private String categoria;
	private String idParalizacao;
//	private String idComponente;
	private int tipo;
	
	private List<Object> manutencoes;
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
//		setTitle(sp.getString("id", "") + " - " + sp.getString("nome", ""));
		setTitle(sp.getString("nomeJanela", "") + " - " + sp.getString("nome", ""));
		idIndividual = sp.getLong("idIndividual", 0);
		horaInicio = sp.getString("horaInicio", "");
		horaTermino = sp.getString("horaTermino ", "");
		descricao = sp.getString("descricao", "");
		idParalizacao = sp.getString("idParalizacao", "");
		categoria = sp.getString("categoria", "");
		tipo = (sp.getString("tipo", "")).equals(ParalizacaoIndividual.MATRICULA) ? 1 : 2;
	}
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paralizacao_individual4);
		carregaVars();
		
		final Spinner manutencao = (Spinner)findViewById(R.id.spManutencao);
		if(tipo == 1){ // Funcionarios - não mostra spinner
			manutencao.setVisibility(Spinner.INVISIBLE);
		}else{
			manutencoes = montaDados(Tabelas.MANUTENCAO, new String[]{"contador", "id", "descricao", "categoria"}, "descricao", 
					String.format(Tabelas.MANUTENCAO.whereSQL(), categoria));
			manutencao.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoIndividual4.this, BancoDeDados.listaCampoEmBranco(manutencoes), "descricao"));
			
		}
		Button btn = (Button)findViewById(R.id.btSalvar);
		btn.setOnClickListener(new View.OnClickListener() {
			
			public String verificaDados(EditText edit){
				String texto = edit.getText().toString();
				if ((!texto.equals("")) && (texto != null)){
					return texto;
				}
				return "";
			}
			
			@Override
			public void onClick(View arg0) {
				AlertDialog alertDialog = new AlertDialog.Builder(ParalizacaoIndividual4.this).create();
				alertDialog.setTitle("Confirmar...");
				alertDialog.setMessage("Deseja salvar os dados?");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContentValues values = new ContentValues();
						values.put("idIndividual", idIndividual);
						values.put("horaInicio", horaInicio);
						values.put("horaTermino", horaTermino);
						values.put("descricao", descricao);
						values.put("idParalizacao", idParalizacao);
						values.put("data", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
						values.put("justificativa", verificaDados((EditText)findViewById(R.id.eJustificativa)));
						if(tipo == 1){
							values.put("idComponente", 0);
						}else{
							Object selecionado = BancoDeDados.pegarValor(manutencoes, "descricao", (String)manutencao.getSelectedItem());
							String retorno = (String)BancoDeDados.pegarValor(selecionado, "id");
							values.put("idComponente", retorno != null ? retorno : 0+"");
							
							String categoria = (String)BancoDeDados.pegarValor(selecionado, "categoria");
							values.put("categoria", categoria != null ? categoria : 0+"");
						}
						BancoDeDados banco = new BancoDeDados(ParalizacaoIndividual4.this, Tabelas.HORAS_PARALIZACOES_INDIVIDUAL, Tabelas.version);
						banco.salvarDados(values, false, null);
						onPause();
//						startActivity(new Intent("CadastroParalizacao"));
					}
				});
				alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alertDialog.show();
				finishActivity(Activity.RESULT_OK);
//				startActivity(new Intent("ParalizacaoIndividual4"));
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
