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
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ParalizacaoEquipe4 extends Activity {
	
	private long idParalizacaoEquipe;
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
		setTitle(sp.getString("nomeEquipe", ""));
		idParalizacaoEquipe = sp.getLong("idParalizacaoEquipe", 0);
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
		setContentView(R.layout.paralizacao_equipe4);
		carregaVars();
		
		final Spinner manutencao = (Spinner)findViewById(R.id.spManutencao);
		manutencao.setVisibility(Spinner.INVISIBLE);
//		if(tipo == 1){ // Funcionarios - não mostra spinner
//			manutencao.setVisibility(Spinner.INVISIBLE);
//		}else{
//			manutencoes = montaDados(Tabelas.MANUTENCAO, new String[]{"contador", "id", "descricao", "categoria"}, "descricao", 
//					String.format(Tabelas.MANUTENCAO.whereSQL(), categoria));
//			manutencao.setAdapter(BancoDeDados.montaAdaptador(ParalizacaoEquipe4.this, manutencoes, "descricao"));
//			
//		}
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
				AlertDialog alertDialog = new AlertDialog.Builder(ParalizacaoEquipe4.this).create();
				alertDialog.setTitle("Confirmar...");
				alertDialog.setMessage("Deseja salvar os dados?");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ContentValues values = new ContentValues();
						values.put("idParalizacaoEquipe", idParalizacaoEquipe);
						values.put("horaInicio", horaInicio);
						values.put("horaTermino", horaTermino);
						values.put("descricao", descricao);
						values.put("idParalizacao", idParalizacao);
						values.put("data", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
						values.put("justificativa", verificaDados((EditText)findViewById(R.id.eJustificativa)));
						values.put("idComponente", 0);
//						if(tipo == 1){
//							values.put("idComponente", 0);
//						}else{
//							Object selecionado = BancoDeDados.pegarValor(manutencoes, "descricao", (String)manutencao.getSelectedItem());
//							values.put("idComponente", (String)BancoDeDados.pegarValor(selecionado, "id"));
//						}
						BancoDeDados banco = new BancoDeDados(ParalizacaoEquipe4.this, Tabelas.HORAS_PARALIZACOES_EQUIPES, Tabelas.version);
						banco.salvarDados(values, false, null);
						onPause();
//						startActivity(new Intent("CadastroParalizacaoEquipe"));
					}
				});
				alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alertDialog.show();
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
