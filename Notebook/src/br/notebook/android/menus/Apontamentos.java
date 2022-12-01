package br.notebook.android.menus;

/* Classe responsável por mostrar a listagem definida na váriavel menus */

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Apontamentos extends ListActivity {
	
	public static final String TRABALHADAS = "Trabalhadas";
	public static final String IMPRODUTIVAS = "Improdutivas";
	
	static final String menus[] = {"Horas Trabalhadas", "Horas Improdutivas", "Consulta Apropriações"};
	static final String classes[] = {"Trabalhadas", "Improdutivas", "NovaConsulta"};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(Apontamentos.this, android.R.layout.simple_list_item_1, menus));
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			SharedPreferences sp = getSharedPreferences("DADOS", 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("TipoApontamento", classes[position]);
			editor.commit();
			if(position > 1){
				startActivity(new Intent("NovaConsulta"));
			}else{
				startActivity(new Intent("TipoApontamento"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
