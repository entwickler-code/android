package br.notebook.android.telas;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ApropriacaoEquipe extends Activity {
	
	private static final String menus[] = {"Menu Principal"};
	
	private List<Object> equipes;
	
	private Object equipe;
	
	private boolean update;
	
	private EditText destino;
	private EditText prod;
	private EditText origem;	
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Apropriação Equipes");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//	}
	
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		update = sp.getBoolean("update", false);
		if(update){
			prod = (EditText)findViewById(R.id.eProdutividade);
			prod.setText(sp.getString("prod", ""));
			
			origem = (EditText)findViewById(R.id.eOrigem);
			origem.setText(sp.getString("origem", ""));

			destino = (EditText)findViewById(R.id.eDestino);
			destino.setText(sp.getString("destino", ""));
		}	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apropriacao_equipe);
		carregaVars();
		BancoDeDados banco = new BancoDeDados(ApropriacaoEquipe.this, Tabelas.EQUIPES, Tabelas.version);
		equipes = banco.consultaDados(new String[]{"id", "nomeEquipe", "horarioTrabalho"}, null, "nomeEquipe");
		
		final Spinner dados = (Spinner)findViewById(R.id.spEquipes);
		
		dados.setAdapter(BancoDeDados.montaAdaptador(this, equipes, "nomeEquipe"));
		if(update){
			dados.setSelection(BancoDeDados.posicaoSpinner(BancoDeDados.EQUIPES, 
					equipes, "id", update, getSharedPreferences("DADOS", 0)));	
		}
		dados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				equipe = BancoDeDados.pegarValor(equipes, "nomeEquipe", (String)arg0.getItemAtPosition(arg2));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		
		Button btn = (Button)findViewById(R.id.btContinuar);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(View arg0) {
				if(equipe != null){
					
					SharedPreferences sp = getSharedPreferences("DADOS", 0);
					SharedPreferences.Editor editor = sp.edit();
					for(Map.Entry<String, String> dado : ((Map<String, String>)equipe).entrySet()){
							editor.putString(dado.getKey(), dado.getValue());
					}
//					try {
						editor.putString("prod", verificaDados((EditText)findViewById(R.id.eProdutividade)));
						editor.putString("origem", verificaDados((EditText)findViewById(R.id.eOrigem)));
						editor.putString("destino", verificaDados((EditText)findViewById(R.id.eDestino)));
						editor.commit();
						startActivity(new Intent("ApropriacaoEquipe2"));
//					} catch (Exception e) {
//						montaAlerta("Campos obrigatórios.");
//					}
				}else{
					montaAlerta("Selecione a equipe.");
				}
			}
			
			public String verificaDados(EditText edit){// throws Exception{
				String texto = edit.getText().toString();
				if ((!texto.equals("")) && (texto != null)){
					return texto;
				}
//				throw new Exception("Campo Obrigatório");
				return "";
			}
		});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apropriacao_equipe, menu);
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
