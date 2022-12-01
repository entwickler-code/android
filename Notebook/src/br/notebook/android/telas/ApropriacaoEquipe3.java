package br.notebook.android.telas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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

public class ApropriacaoEquipe3 extends ListActivity {
	
	private static final String menus[] = {"Menu Principal", "Novo Apontamento", "Excluir Apontamento", "Adicionar Horários"};
	private static boolean finalizar = true;
	
	private List<Object> horarios;
	
	private String idTrabalhoEquipe;
	
	public List<Object> montaDados(Tabelas tabela, String[] campos, String ordem, String condicao){
		return new BancoDeDados(this, tabela, Tabelas.version).consultaDados(campos, condicao, ordem);
	}
	
	private void carregaVars(){
		SharedPreferences sp = getSharedPreferences("DADOS", 0);
		setTitle(sp.getString("nomeEquipe", ""));
		idTrabalhoEquipe = sp.getString("id", "");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.apropriacao_equipe3);
		carregaVars();
		horarios = montaDados(Tabelas.HORAS_EQUIPES, new String[]{"id", "horaInicio", "horaTermino", "descricao"}, "descricao", String.format(Tabelas.HORAS_EQUIPES.whereSQL(), idTrabalhoEquipe));
		
		setListAdapter(new ArrayAdapter<String>(ApropriacaoEquipe3.this, android.R.layout.simple_list_item_1, montaLista()));
		
	}
	
	@SuppressWarnings("unchecked")
	public String[] montaLista(){
		List<String> listagem = new ArrayList<String>();
		Map<String, String> linha = null;
		for (Object obj : horarios) {
			linha = (Map<String, String>)obj;
			listagem.add(linha.get("horaInicio")+" - "+linha.get("horaTermino")+" - "+linha.get("descricao"));
		}
		String[] retorno = new String[listagem.size()];
		listagem.toArray(retorno);
		return retorno;
	}
	
	private void montaAlerta(String msg){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Apropriação Equipe");
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
		final String selecionado = (String)l.getItemAtPosition(position);
		AlertDialog alertDialog = new AlertDialog.Builder(ApropriacaoEquipe3.this).create();
		alertDialog.setTitle("Exclusão...");
		alertDialog.setMessage("Deseja excluir o horário?");
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String[] dados = consertaDados(selecionado.split(" - "));
				Object horario = BancoDeDados.pegarValor(horarios, new String[]{"horaInicio", "horaTermino","descricao"}, dados);
				if(horario != null){
					BancoDeDados banco = new BancoDeDados(ApropriacaoEquipe3.this, Tabelas.HORAS_EQUIPES, Tabelas.version);
					banco.excluirDados(BancoDeDados.pegarValor(horario, "id"));
					startActivity(new Intent("ApropriacaoEquipe3"));
				}else{
					montaAlerta("Problemas ao excluir horário.");
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
	
	private String[] consertaDados(String[] dados){
		List<String> valores = new ArrayList<String>();
		for (String dado : dados) {
			if(!dado.equals("-")){
				valores.add(dado);
			}
		}
		String[] retorno = new String[valores.size()];
		valores.toArray(retorno);
		return retorno;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.apropriacao_equipe3, menu);
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
						startActivity(new Intent("ApropriacaoEquipe"));
						break;
					case 2:
						finalizar = true;
						AlertDialog alertDialog = new AlertDialog.Builder(ApropriacaoEquipe3.this).create();
						alertDialog.setTitle("Exclusão...");
						alertDialog.setMessage("Deseja excluir o Apontamento?");
						alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								BancoDeDados banco = new BancoDeDados(ApropriacaoEquipe3.this, Tabelas.HORAS_EQUIPES, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.HORAS_EQUIPES.getNomeTabela()+" WHERE idTrabalhoEquipe = "+idTrabalhoEquipe+";"});
								
								banco = new BancoDeDados(ApropriacaoEquipe3.this, Tabelas.TRABALHO_EQUIPES, Tabelas.version);
								banco.excluirDados(new String[]{"DELETE FROM "+Tabelas.TRABALHO_EQUIPES.getNomeTabela()+" WHERE id = "+idTrabalhoEquipe+";"});
								startActivity(new Intent("ApropriacaoEquipe"));
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
						startActivity(new Intent("CadastroApropriacaoEquipe"));
						break;

					default:
						break;
					} 
					return false;
				}
			});
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		horarios = montaDados(Tabelas.HORAS_EQUIPES, new String[]{"id", "horaInicio", "horaTermino", "descricao"}, 
				"descricao", String.format(Tabelas.HORAS_EQUIPES.whereSQL(), idTrabalhoEquipe));
		
		setListAdapter(new ArrayAdapter<String>(ApropriacaoEquipe3.this, android.R.layout.simple_list_item_1, 
				montaLista()));
	}
	
    @Override
    protected void onPause() { // Chama o metodo OnPause para Finalizar a Activity 
    	super.onPause();
    	if(finalizar){
    		finish();
    	}
    }
}
