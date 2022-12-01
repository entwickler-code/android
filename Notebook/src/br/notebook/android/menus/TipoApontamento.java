package br.notebook.android.menus;

/*  Classe responsável por visualizar o tipo do apontamento Equipe ou Individual
 * Sendo buscado uma váriavel TipoApontamento para verificar qual o tipo do Apontamento
 * se é Horas Trabalhadas ou Horas Individuais
 * */

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TipoApontamento extends ListActivity {

	private static String tipo;
	
	static final String menus[] = {"Equipe", "Individual"};//, "Consulta"};
	static final String improdutivo[] = {"ParalizacaoEquipe", "ParalizacaoIndividual", "Consulta"};
	static final String trabalhado[] = {"ApropriacaoEquipe", "ApropriacaoIndividual", "Consulta"};
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		
		tipo = sp.getString("TipoApontamento", "");
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		carregaVars();
		setListAdapter(new ArrayAdapter<String>(TipoApontamento.this, android.R.layout.simple_list_item_1, menus));
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			SharedPreferences sp = getSharedPreferences("DADOS", 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("ApontamentoPor", menus[position]);
			editor.putBoolean("update", false);
			editor.commit();
			if(tipo.equals(Apontamentos.IMPRODUTIVAS)){
				startActivity(new Intent(improdutivo[position]));
			}else if (tipo.equals(Apontamentos.TRABALHADAS)){ // Modificado 11/01/2013 - 23:00
				startActivity(new Intent(trabalhado[position]));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
