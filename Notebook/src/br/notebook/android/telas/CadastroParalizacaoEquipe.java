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

public class CadastroParalizacaoEquipe extends Activity {
	
	private static boolean finalizar = false;
	private static final String menus[] = {"Menu Principal", "Nova Paralisação", "Excluir Paralisação", "Consulta Horários"};
	

	private List<Object> lista;
	
	private Object funcao;
	
	private long idParalizacaoEquipes;
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		setTitle(sp.getString("nomeEquipe", ""));
		BancoDeDados banco = new BancoDeDados(CadastroParalizacaoEquipe.this, Tabelas.PARALIZACOES, Tabelas.version);
//		idParalizacaoEquipes = sp.getLong("idParalizacaoEquipes", 0);
		idParalizacaoEquipes = sp.getLong("idParalizacaoEquipe", 0);
		lista = banco.consultaDados(new String[]{"id", "descricao"}, null, "descricao");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cadastro_paralizacao_equipe);
		carregaVars();
		
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
		
		TimePicker horaInicio = (TimePicker)findViewById(R.id.horaInicio);
		horaInicio.setIs24HourView(true);
		
		TimePicker horaFinal = (TimePicker)findViewById(R.id.horaFinal);
		horaFinal.setIs24HourView(true);
		
		resetaHora(horaInicio, horaFinal);
		
		Button btn = (Button)findViewById(R.id.btSalvarParaTab);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TimePicker hInicio = (TimePicker)findViewById(R.id.horaInicio);
				String dataInicio = BancoDeDados.montaData(hInicio.getCurrentHour(), hInicio.getCurrentMinute());
				
				TimePicker hFinal = (TimePicker)findViewById(R.id.horaFinal);
				String dataFinal = BancoDeDados.montaData(hFinal.getCurrentHour(), hFinal.getCurrentMinute());
				SharedPreferences sp = getSharedPreferences("DADOS", 0);
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong("idParalizacaoEquipe", idParalizacaoEquipes);
				editor.putString("horaInicio", dataInicio);
				editor.putString("horaTermino ", dataFinal);
				editor.putString("descricao", BancoDeDados.pegarValor(funcao, "descricao"));
				editor.putString("idParalizacao", BancoDeDados.pegarValor(funcao, "id"));
				editor.commit();
				resetaHora(hInicio, hFinal);
				startActivity(new Intent("ParalizacaoEquipe4"));
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
		getMenuInflater().inflate(R.menu.cadastro_paralizacao_equipe, menu);
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
						startActivity(new Intent("ParalizacaoEquipe"));
						break;
					case 2:
						AlertDialog alertDialog = new AlertDialog.Builder(CadastroParalizacaoEquipe.this).create();
						alertDialog.setTitle("Exclusão...");
						alertDialog.setMessage("Deseja excluir o Apontamento?");
						alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								BancoDeDados banco = new BancoDeDados(CadastroParalizacaoEquipe.this, Tabelas.HORAS_PARALIZACOES_EQUIPES, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.HORAS_PARALIZACOES_EQUIPES.getNomeTabela()+" WHERE idParalizacaoEquipe = "+idParalizacaoEquipes+";"});
								
								banco = new BancoDeDados(CadastroParalizacaoEquipe.this, Tabelas.PARALIZACAO_EQUIPES, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.PARALIZACAO_EQUIPES.getNomeTabela()+" WHERE id = "+idParalizacaoEquipes+";"});
								startActivity(new Intent("ParalizacaoEquipe"));
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
						startActivity(new Intent("ConsultaParalizacaoEquipe"));
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
