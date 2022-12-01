package br.notebook.android;

/* Classe reponsável por visualizar o Menu Principal
 * esta classe é chamada logo depois do splash executar */

import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;
import br.notebook.android.menus.PrepararArquivo;

public class MenuPrincipal  extends ListActivity{

	static final String menus[] = {"Apontamentos", "Carregar Dados para Apropriação", "Exportar Dados Apropriados"};
	static final String classes[] = {"Apontamentos", "PegarArquivo", "PrepararArquivo"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(MenuPrincipal.this, android.R.layout.simple_list_item_1, menus));
	}
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			switch (position) {
			case 2:
				montaDialog();
				break;
			default:
				startActivity(new Intent(classes[position]));
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void montaDialog(){
		AlertDialog alertDialog = new AlertDialog.Builder(MenuPrincipal.this).create();
		alertDialog.setTitle("Confirmar...");
		alertDialog.setMessage("Deseja exportar os dados?");
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PrepararArquivo.createArquivoExportacao(MenuPrincipal.this);
			}
		});
		alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		Toast.makeText(this, "Aguarde, o processamento...", Toast.LENGTH_LONG).show();
		alertDialog.show();	
	}
	
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Menu Principal");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	private String montarNome(String nome){

		DateFormat fmt = new SimpleDateFormat("yyyyddMMHHmm");
	    return nome + "_" +fmt.format(Calendar.getInstance().getTime())+".json";
	}

	private File pegarArquivos(String caminho){
		return Environment.getExternalStoragePublicDirectory(caminho);
	}
	
}


