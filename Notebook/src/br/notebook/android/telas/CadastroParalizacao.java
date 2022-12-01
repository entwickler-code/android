package br.notebook.android.telas;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class CadastroParalizacao extends Activity {
	
	private static boolean finalizar = false;
	private static final String menus[] = {"Menu Principal", "Nova Paralisação", "Excluir Paralisação", "Consulta Horários"};
	
	private List<Object> lista;
	
	private Object funcao;
	
	private long idIndividual;
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
//		setTitle(sp.getString("id", "") + " - " + sp.getString("nome", ""));
		setTitle(sp.getString("nomeJanela", "") + " - " + sp.getString("nome", ""));
		BancoDeDados banco = new BancoDeDados(CadastroParalizacao.this, Tabelas.PARALIZACOES, Tabelas.version);
		idIndividual = sp.getLong("idIndividual", 0);
		lista = banco.consultaDados(new String[]{"id", "descricao"}, null, "descricao");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		carregaVars();
		setContentView(R.layout.cadastro_paralizacao);
		
		try{
			Spinner servicos = (Spinner) findViewById(R.id.listaServicos);
			servicos.setAdapter(BancoDeDados.montaAdaptador(this, lista, "descricao"));
			
			servicos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					funcao = BancoDeDados.pegarValor(lista, "descricao", (String)arg0.getItemAtPosition(arg2));
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
		
		TimePicker horaInicio = (TimePicker)findViewById(R.id.horaInicio);
		horaInicio.setIs24HourView(true);
		
		TimePicker horaFinal = (TimePicker)findViewById(R.id.horaFinal);
		horaFinal.setIs24HourView(true);
		
		resetaHora(horaInicio, horaFinal);
		Button btn = (Button)findViewById(R.id.btSalvarTab);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TimePicker hInicio = (TimePicker)findViewById(R.id.horaInicio);
				String dataInicio = BancoDeDados.montaData(hInicio.getCurrentHour(), hInicio.getCurrentMinute());
				
				TimePicker hFinal = (TimePicker)findViewById(R.id.horaFinal);
				String dataFinal = BancoDeDados.montaData(hFinal.getCurrentHour(), hFinal.getCurrentMinute());
				resetaHora(hInicio, hFinal);
				
				SharedPreferences sp = getSharedPreferences("DADOS", 0);
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong("idIndividual", idIndividual);
				editor.putString("horaInicio", dataInicio);
				editor.putString("horaTermino ", dataFinal);
				editor.putString("descricao", BancoDeDados.pegarValor(funcao, "descricao"));
				editor.putString("idParalizacao", BancoDeDados.pegarValor(funcao, "id"));
				editor.commit();
				startActivity(new Intent("ParalizacaoIndividual4"));
			}
			
		});
	}
	private  void resetaHora(TimePicker hInicio, TimePicker hFinal){
		hInicio.setCurrentHour(Calendar.getInstance().getTime().getHours());
		hInicio.setCurrentMinute(Calendar.getInstance().getTime().getMinutes());
		
		hFinal.setCurrentHour(Calendar.getInstance().getTime().getHours());
		hFinal.setCurrentMinute(Calendar.getInstance().getTime().getMinutes());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cadastro_paralizacao, menu);
		
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
						startActivity(new Intent("ParalizacaoIndividual"));
						break;
					case 2:
						finalizar = false;
						AlertDialog alertDialog = new AlertDialog.Builder(CadastroParalizacao.this).create();
						alertDialog.setTitle("Exclusão...");
						alertDialog.setMessage("Deseja excluir o Apontamento?");
						alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								BancoDeDados banco = new BancoDeDados(CadastroParalizacao.this, Tabelas.HORAS_PARALIZACOES_INDIVIDUAL, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.HORAS_PARALIZACOES_INDIVIDUAL.getNomeTabela()+" WHERE idIndividual = "+idIndividual+";"});
								
								banco = new BancoDeDados(CadastroParalizacao.this, Tabelas.PARALIZACAO_INDIVIDUAL, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.PARALIZACAO_INDIVIDUAL.getNomeTabela()+" WHERE id = "+idIndividual+";"});
								startActivity(new Intent("ParalizacaoIndividual"));
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
						startActivity(new Intent("ConsultaParalizacao"));
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
    protected void onPause() { // Chama o metodo OnPause para Finalizar a Activity 
    	super.onPause();
    	if(finalizar){
    		finish();
    	}
    }
}
