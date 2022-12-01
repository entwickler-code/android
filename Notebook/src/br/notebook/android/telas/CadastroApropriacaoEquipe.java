package br.notebook.android.telas;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class CadastroApropriacaoEquipe extends Activity {

	private List<Object> lista;
	
//	private static boolean finalizar = false;
//
//	private static final String menus[] = {"Menu Principal", "Novo Apontamento", "Excluir Apontamento",  "Consulta Horários"};
	
	private Object funcao;
	
	private long id;
//	private String tipo;
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		setTitle(sp.getString("id", "") + " - " + sp.getString("nomeEquipe", ""));
		BancoDeDados banco;
		id = sp.getLong("idTrabalhoEquipe", 0);
		banco = new BancoDeDados(CadastroApropriacaoEquipe.this, Tabelas.FUNCOES, Tabelas.version);
		lista = banco.consultaDados(new String[]{"id", "idFuncao", "descricao"}, null, "descricao");	
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
				texto.setText(null);
				texto.clearListSelection();
				texto.setHint("Serviço/Função");
//					listagem = new ArrayAdapter<String>(ParalizacaoIndividual.this,
//							android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, tipo));
				texto.setAdapter(BancoDeDados.montaAdaptador(CadastroApropriacaoEquipe.this, lista, "descricao"));
				texto.showDropDown();
			}
		});
		
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
					AlertDialog alertDialog = new AlertDialog.Builder(CadastroApropriacaoEquipe.this).create();
					alertDialog.setTitle("Confirmar...");
					alertDialog.setMessage("Deseja salvar os dados?");
					alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							TimePicker hInicio = (TimePicker)findViewById(R.id.horaInicio);
							String dataInicio = BancoDeDados.montaData(hInicio.getCurrentHour(), hInicio.getCurrentMinute());
							
							TimePicker hFinal = (TimePicker)findViewById(R.id.horaFinal);
							String dataFinal = BancoDeDados.montaData(hFinal.getCurrentHour(), hFinal.getCurrentMinute());
							resetaHora(hInicio, hFinal);
							
							ContentValues values = new ContentValues();
							String idServico = BancoDeDados.pegarValor(funcao, "id");
							values.put("idServico", idServico == null ? "0" : idServico);
							values.put("idHorario", 0);
							values.put("horarioTrabalho", 0);
							values.put("idTrabalhoEquipe", id);
							values.put("diaSemana", 0);
							values.put("codigoParalizacao", 0); // Add 13/01/2013 - 17:15
							values.put("idFuncao", idServico == null ? "0" : BancoDeDados.pegarValor(funcao, "idFuncao")); // Add 13/01/2013 - 17:15
							values.put("horaInicio", dataInicio);
							values.put("horaTermino ", dataFinal);
							values.put("data", BancoDeDados.pegaDataAtual("yyyy-MM-dd")); // Add 21/01 - 11:35
							values.put("descricao", idServico == null ? "Sem escolha" : BancoDeDados.pegarValor(funcao, "descricao"));
							BancoDeDados banco = new BancoDeDados(CadastroApropriacaoEquipe.this, Tabelas.HORAS_EQUIPES, Tabelas.version);
							banco.salvarDados(values, false, null);
							Toast.makeText(CadastroApropriacaoEquipe.this, "Informações salvas", Toast.LENGTH_SHORT).show();
						}
					});
					alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					alertDialog.show();
				}else{
					Toast.makeText(CadastroApropriacaoEquipe.this, "Serviço/Função é Obrigatório.", Toast.LENGTH_SHORT).show();
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
	protected void onPause() {
		super.onPause();
		finish();
	}

}
