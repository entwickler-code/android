package br.notebook.android.telas;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class CadastroApropriacao extends Activity {
	
	private List<Object> lista;
	
	private static boolean finalizar = false;

	private static final String menus[] = {"Menu Principal", "Novo Apontamento", "Excluir Apontamento",  "Consulta Horários"};
	
	private Object funcao;
	
	private long id;
	private String tipo;
	
//	private void montaAlerta(String msg){
//		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setTitle("Apropriação Individual");
//		alertDialog.setMessage(msg);
//		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//			}
//		});
//		alertDialog.show();
//	}
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
//		setTitle(sp.getString("id", "") + " - " + sp.getString("nome", ""));
		setTitle(sp.getString("nomeJanela", "") + " - " + sp.getString("nome", ""));
		BancoDeDados banco;
		id = sp.getLong("trabalhoIndividual", 0);
		tipo = sp.getString("tipo", "");
		if(tipo.equals(ApropriacaoIndividual.PREFIXO)){
			banco = new BancoDeDados(CadastroApropriacao.this, Tabelas.SERVICO_EQUIPAMENTOS, Tabelas.version);
			String where;
			if(!sp.getString("servicoPadrao", "").equals("")){
				where =	String.format(Tabelas.SERVICO_EQUIPAMENTOS.whereSQL(), sp.getString("servicoPadrao", ""));
				lista = banco.consultaDados(new String[]{"id", "descricao"}, where, "descricao");
				if(lista.isEmpty()){
					lista = banco.consultaDados(new String[]{"id", "descricao"}, null, "descricao");
				}
			}else{
				where =	String.format(Tabelas.SERVICO_EQUIPAMENTOS.whereSQL(), sp.getString("servicoPadrao", ""));
				lista = banco.consultaDados(new String[]{"id", "descricao"}, where, "descricao");
			}
		}else if(tipo.equals(ApropriacaoIndividual.MATRICULA)){
			banco = new BancoDeDados(CadastroApropriacao.this, Tabelas.FUNCOES, Tabelas.version);
			lista = banco.consultaDados(new String[]{"id", "descricao"}, 
					String.format(Tabelas.FUNCOES.whereSQL(), sp.getString("funcao", "")), "descricao");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		carregaVars();
		setContentView(R.layout.cadastro_apropriacao);
		
		final AutoCompleteTextView  texto = (AutoCompleteTextView)findViewById(R.id.listaServicos);		
		
		texto.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String nome = (String)arg0.getItemAtPosition(arg2);
				String id = nome.split(" - ")[0];
				funcao = BancoDeDados.pegarValor(lista, "descricao", id);
			}
			
		});

		texto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//					ArrayAdapter<String> listagem = null;
				texto.setText(null);
				texto.clearListSelection();
				texto.setHint("Serviço/Função");
//					listagem = new ArrayAdapter<String>(ParalizacaoIndividual.this,
//							android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, tipo));
				texto.setAdapter(BancoDeDados.montaAdaptador(CadastroApropriacao.this, lista, "descricao"));
				texto.showDropDown();
			}
		});
		
//		Spinner servicos = (Spinner) findViewById(R.id.listaServicos);
//		servicos.setAdapter(BancoDeDados.montaAdaptador(this, lista, "descricao"));
//		
//		servicos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				funcao = BancoDeDados.pegarValor(lista, "descricao", (String)arg0.getItemAtPosition(arg2));
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//			}
//		});
		
		TimePicker horaInicio = (TimePicker)findViewById(R.id.horaInicio);
		horaInicio.setIs24HourView(true);
		
		TimePicker horaFinal = (TimePicker)findViewById(R.id.horaFinal);
		horaFinal.setIs24HourView(true);
		
		resetaHora(horaInicio, horaFinal);
		
		Button btn = (Button)findViewById(R.id.btSalvarTab);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String idServico = BancoDeDados.pegarValor(funcao, "id");
				if(idServico != null){
					AlertDialog alertDialog = new AlertDialog.Builder(CadastroApropriacao.this).create();
					alertDialog.setTitle("Confirmar...");
					alertDialog.setMessage("Deseja salvar os dados?");
					alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//Pegar retorno boolean
							TimePicker hInicio = (TimePicker)findViewById(R.id.horaInicio);
							String dataInicio = BancoDeDados.montaData(hInicio.getCurrentHour(), hInicio.getCurrentMinute());
							
							TimePicker hFinal = (TimePicker)findViewById(R.id.horaFinal);
							String dataFinal = BancoDeDados.montaData(hFinal.getCurrentHour(), hFinal.getCurrentMinute());
							resetaHora(hInicio, hFinal);
							
							ContentValues values = new ContentValues();
							values.put("idIndividual", id); // Alteração de espaço
							values.put("tipo", tipo);
							values.put("idServico", funcao == null ? "0" : BancoDeDados.pegarValor(funcao, "id"));
							values.put("horaInicio", dataInicio);
							values.put("horaTermino", dataFinal);
							values.put("data", BancoDeDados.pegaDataAtual("yyyy-MM-dd"));
							values.put("descricao", funcao == null ? "Sem escolha" : BancoDeDados.pegarValor(funcao, "descricao"));
							BancoDeDados banco = new BancoDeDados(CadastroApropriacao.this, Tabelas.HORAS_INDIVIDUAIS, Tabelas.version);
							banco.salvarDados(values, false, null);
							Toast.makeText(CadastroApropriacao.this, "Informações salvas", Toast.LENGTH_SHORT).show();
						}
					});
					alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alertDialog.show();
				}else{
					Toast.makeText(CadastroApropriacao.this, "Serviço/Funçao é obrigatório.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void resetaHora(TimePicker hInicio, TimePicker hFinal){
		hInicio.setCurrentHour(Calendar.getInstance().getTime().getHours());
		hInicio.setCurrentMinute(Calendar.getInstance().getTime().getMinutes());
		
		hFinal.setCurrentHour(Calendar.getInstance().getTime().getHours());
		hFinal.setCurrentMinute(Calendar.getInstance().getTime().getMinutes());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cadastro_apropriacao, menu);
		
		MenuItem item;
		for (int i = 0; i < menu.size(); i++){
			item = menu.getItem(i);
			item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
					int tipo = 0;
					for (int j = 0; j < menus.length; j++) {
						if(arg0.getTitle().equals(menus[j])){
							tipo = j;
							break;
						}
						
					}
					switch (tipo) {
					case 0:
						finalizar = true;
						startActivity(new Intent("MenuPrincipal"));
						break;
					case 1:
						finalizar = true;
						startActivity(new Intent("ApropriacaoIndividual"));
						break;
					case 2:
						finalizar = false;
						AlertDialog alertDialog = new AlertDialog.Builder(CadastroApropriacao.this).create();
						alertDialog.setTitle("Exclusão...");
						alertDialog.setMessage("Deseja excluir o Apontamento?");
						alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								BancoDeDados banco = new BancoDeDados(CadastroApropriacao.this, Tabelas.HORAS_INDIVIDUAIS, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.HORAS_INDIVIDUAIS.getNomeTabela()+" WHERE idIndividual = "+id+";"});
								
								banco = new BancoDeDados(CadastroApropriacao.this, Tabelas.TRABALHO_INDIVIDUAL, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.TRABALHO_INDIVIDUAL.getNomeTabela()+" WHERE id = "+id+";"});
								startActivity(new Intent("ApropriacaoIndividual"));
							}
						});
						alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						alertDialog.show();		
						break;	
					case 3:
						finalizar = false;
						startActivity(new Intent("ConsultaApropriacao"));
						break;
					default:
						finalizar = false;
						break;
					} 
					return false;
				}
			});
		}
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(finalizar){
			finish();
		}
	}
}
