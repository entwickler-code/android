package br.notebook.android.menus;

/* Classe responsável por mostrar as opções para pegar o arquivo
 * inicialmente somente via SDCard, mas pode-se adicionar outros formas
 *  */

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.notebook.android.core.BancoDeDados;
import br.notebook.android.core.Tabelas;

public class PegarArquivo extends ListActivity {

	static final String menus[] = {"SD Card"};//, "WebService"};
	static final String classes[] = {"SDCard"};//, "WebService"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(PegarArquivo.this, android.R.layout.simple_list_item_1, menus));
		montaDialogBackup();
	}
	
	private void montaDialogBackup(){
		if(verificaDadosGravados()){
			AlertDialog alertDialog = new AlertDialog.Builder(PegarArquivo.this).create();
			alertDialog.setTitle("Exportação...");
			alertDialog.setMessage("Existem apontamentos gravados, deseja exportá-los?");
			alertDialog.setButton("Sim", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PrepararArquivo.createArquivoExportacao(PegarArquivo.this);
				}
			});
			alertDialog.setButton2("Não", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					montaAlerta("Seus dados serão apagados.");
				}
			});
			alertDialog.show();	
		}
	}
	
	private boolean verificaDadosGravados(){
		List<Object> lista = new BancoDeDados(this, Tabelas.OBRA, Tabelas.version).consultaDados(new String[]{"status"}, "", null);
		
		if(!lista.isEmpty()){
			Object obj = lista.get(0);
			if(obj != null){
				String dados = BancoDeDados.pegarValor(obj, "status");
				return Integer.parseInt(dados) == 1 ? true : false;
			}
		}
		return false;
	}
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Último Aviso");
		alertDialog.setMessage(msg);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			startActivity(new Intent(classes[position]));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

