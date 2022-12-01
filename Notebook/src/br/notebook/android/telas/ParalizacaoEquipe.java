package br.notebook.android.telas;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.notebook.android.R;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class ParalizacaoEquipe extends ListActivity {
	
	private static final String menus[] = {"Menu Principal"};
	
	private List<Object> equipes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//			setContentView(R.layout.paralizacao_equipe);
		
		BancoDeDados banco = new BancoDeDados(ParalizacaoEquipe.this, Tabelas.EQUIPES, Tabelas.version);
		equipes = banco.consultaDados(new String[]{"id", "nomeEquipe", "horarioTrabalho"}, null, "nomeEquipe");
		
		setListAdapter(new ArrayAdapter<String>(ParalizacaoEquipe.this, 
				android.R.layout.simple_list_item_1, BancoDeDados.leituraDados(equipes, "nomeEquipe")));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			SharedPreferences sp = getSharedPreferences("DADOS", 0);
			SharedPreferences.Editor editor = sp.edit();
			Object equipe = BancoDeDados.pegarValor(equipes, "nomeEquipe", (String)l.getItemAtPosition(position));
			for(Map.Entry<String, String> dado : ((Map<String, String>)equipe).entrySet()){
				editor.putString(dado.getKey(), dado.getValue());
			}
			editor.commit();
			startActivity(new Intent("ParalizacaoEquipe2"));
		}catch(Exception e){
			e.printStackTrace();
		}
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
