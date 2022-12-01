package br.notebook.android.menus;

/* Classe responsável por listar os arquivos .json */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import org.json.JSONException;

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
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.StatusSDCard;
import br.notebook.android.core.Tabelas;

public class SDCard extends ListActivity {
	
	private static final String path = Environment.DIRECTORY_DCIM+"/Constran/externos/";
	
	private File pegarArquivos(String caminho){
		return Environment.getExternalStoragePublicDirectory(caminho);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String menus[] = null;
		if(StatusSDCard.verificaStatusSDCard(Environment.getExternalStorageState(), StatusSDCard.LEITURA)){
			File dir = pegarArquivos(path);
			menus = dir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".json");
				}
			});
		}
		if(menus.length > 0){
			setListAdapter(new ArrayAdapter<String>(SDCard.this, android.R.layout.simple_list_item_1, menus));			
		}else{
			montaAlerta("Nenhum arquivo encontrado.");
		}
	}
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(SDCard.this).create();
		alertDialog.setTitle("SDCard");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent("MenuPrincipal"));
			}
		});
		alertDialog.show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "Aguarde, o processamento pode ser demorado...", Toast.LENGTH_LONG).show();
		final String caminho = path+(String)l.getItemAtPosition(position);
		AlertDialog alertDialog = new AlertDialog.Builder(SDCard.this).create();
		alertDialog.setTitle("Confirmar...");
		alertDialog.setMessage("Deseja processar arquivo?");
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					BufferedReader arq = new BufferedReader(new FileReader(pegarArquivos(caminho)));
					StringBuilder dados = new StringBuilder();
					while(arq.ready()){
						dados.append(arq.readLine());
					}
					BancoDeDados banco = null;
					for(Tabelas tabela : Tabelas.values()){
						banco = new BancoDeDados(SDCard.this, tabela, Tabelas.version);
						banco.apagarDados(); //Chamar Dialog
						banco.popularBanco(dados.toString()); 
					}
					banco.atualizaBanco();
					montaAlerta("Arquivo processado.");
				}catch(JSONException e){
					montaAlerta("Não é um formato JSON válido.");
				}catch(Exception e){
					montaAlerta("Erro ao processar arquivo.");
				}					
			}
		});
		alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
}
