package br.notebook.android.telas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ApropriacaoIndividual extends Activity {
	
	private static final String menus[] = {"Menu Principal"};
	
	private List<Object> lista = new ArrayList<Object>();
	private ArrayAdapter<String> listagem = null;
	
	private Object selecionado = null; 
	
	public static final String PREFIXO = "prefixo";
	public static final String MATRICULA = "matricula";
	
	private String tipo = null;
	
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
	
	private void carregaVars(boolean visible){
		EditText horoInicial = (EditText)findViewById(R.id.horoInicial);
		EditText horoFinal = (EditText)findViewById(R.id.horoFinal);
		horoInicial.setVisibility(visible ? EditText.VISIBLE : EditText.INVISIBLE);
		horoFinal.setVisibility(visible ? EditText.VISIBLE : EditText.INVISIBLE);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apropriacao_individual);
		carregaVars(false);
		Button btn = (Button)findViewById(R.id.btContinuar);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(View arg0) {
				if((tipo != null) && (selecionado != null)){
					SharedPreferences sp = getSharedPreferences("DADOS", 0);
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("tipo", tipo);
					for(Map.Entry<String, String> dado : ((Map<String, String>)selecionado).entrySet()){
						editor.putString(dado.getKey(), dado.getValue());
					}
					Map<String, String> nome = ((Map<String, String>)selecionado);
					editor.putString("nomeJanela", nome.get(tipo));
					EditText horoInicial = (EditText)findViewById(R.id.horoInicial);
					EditText horoFinal = (EditText)findViewById(R.id.horoFinal);
					editor.putString("horoInicial", horoInicial.getText().toString());
					editor.putString("horoFinal", horoFinal.getText().toString());
					editor.commit();
					startActivity(new Intent("ApropriacaoIndividual2"));
				}else{
					montaAlerta("Campo obrigatório.");
				}
			}
		});
		
		final AutoCompleteTextView  texto = (AutoCompleteTextView)findViewById(R.id.autoCompDados);
		
		texto.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String nome = (String)arg0.getItemAtPosition(arg2);
				String id = nome.split(" - ")[0];
				selecionado = BancoDeDados.pegarValor(lista, tipo, id);
			}
			
		});
		
		texto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				ArrayAdapter<String> listagem = null;
				texto.setText(null);
				texto.clearListSelection();
				texto.setHint("Matrícula/Prefixo");
//				listagem = new ArrayAdapter<String>(ApropriacaoIndividual.this, 
//						android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, tipo));
				texto.setAdapter(listagem);
				texto.showDropDown();
			}
		});
		
		RadioGroup group = (RadioGroup)findViewById(R.id.rgTipo);
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				BancoDeDados banco = null;
				texto.setText(null);
				texto.clearListSelection();
				switch (checkedId) {
				case R.id.rBEquipamento:
					banco = new BancoDeDados(ApropriacaoIndividual.this, Tabelas.EQUIPAMENTOS, Tabelas.version);
					lista = banco.consultaDados(new String[]{"id", "prefixo", "nome", "servicoPadrao"}, null, "prefixo");
					listagem = new ArrayAdapter<String>(ApropriacaoIndividual.this, 
							android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, PREFIXO, "nome"));
					texto.setHint("Prefixo");
					tipo = PREFIXO;
					carregaVars(true);
					break;
				case R.id.rBFuncionario:
					banco = new BancoDeDados(ApropriacaoIndividual.this, Tabelas.PESSOAS, Tabelas.version);
					lista = banco.consultaDados(new String[]{"id", "matricula", "nome", "funcao"}, null, "matricula");
					listagem = new ArrayAdapter<String>(ApropriacaoIndividual.this, 
							android.R.layout.simple_spinner_dropdown_item, BancoDeDados.leituraDados(lista, MATRICULA, "nome"));
					texto.setHint("Matrícula");
					tipo = MATRICULA;
					carregaVars(false);
					break;					

				default:
					break;
				}
				texto.setAdapter(listagem);
				texto.showDropDown();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apropriacao_individual, menu);
		
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
						startActivity(new Intent("MenuPrincipal"));
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
   		finish();
    }

}
