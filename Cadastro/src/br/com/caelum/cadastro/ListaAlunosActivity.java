package br.com.caelum.cadastro;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import br.com.caelum.cadastro.adapter.ListaAlunosAdapter;
import br.com.caelum.cadastro.dao.AlunoDAO;
import br.com.caelum.cadastro.modelo.Aluno;
import br.com.caelum.cadastro.task.EnviaContatosTask;

public class ListaAlunosActivity extends Activity {

	private ListView listaAlunos;
	private List<Aluno> alunos;
	private Aluno alunoSelecionado;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_alunos);
        
        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        
        registerForContextMenu(listaAlunos);
        
        listaAlunos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int posicao, long id) {
				Aluno alunoSelecionado = (Aluno) listaAlunos.getItemAtPosition(posicao);
				Intent edicao = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
				edicao.putExtra(Extras.ALUNO_SELECIONADO, alunoSelecionado);
				startActivity(edicao);
			}
		});

        listaAlunos.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int posicao, long id) {
				alunoSelecionado = (Aluno) adapter.getItemAtPosition(posicao);
				return false;
			}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	carregaLista();
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_principal, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_novo:
			Intent intent = new Intent(this, FormularioActivity.class);
			startActivity(intent);
			return false;
		case R.id.menu_receber_provas:
			Intent provas = new Intent(this, ProvasActivity.class);
			startActivity(provas);
			return false;			
		case R.id.menu_enviar_alunos:
			new EnviaContatosTask(this).execute();
			return false;
		case R.id.menu_mapa:
			Intent mapa = new Intent(this, MostraAlunosProximosActivity.class);
			startActivity(mapa);
			return false;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuItem ligar = menu.add("Ligar");
		Intent intentLigar = new Intent(Intent.ACTION_CALL);
        intentLigar.setData(Uri.parse("tel:" + alunoSelecionado.getTelefone()));
        ligar.setIntent(intentLigar);
		
		MenuItem sms = menu.add("Enviar SMS");
        Intent intenteSMS = new Intent(Intent.ACTION_VIEW);
        intenteSMS.setData(Uri.parse("sms:" + alunoSelecionado.getTelefone()));
        intenteSMS.putExtra("sms_body", "Mensagem");
        sms.setIntent(intenteSMS);

		MenuItem mapa = menu.add("Achar no Mapa");
		Intent intenteMapa = new Intent(Intent.ACTION_VIEW);
		intenteMapa.setData(Uri.parse("geo:0,0?z=14&q=" + alunoSelecionado.getEndereco()));
        mapa.setIntent(intenteMapa);
		
		MenuItem site = menu.add("Navegar no site");
		Intent intenteSite = new Intent(Intent.ACTION_VIEW);
		intenteSite.setData(Uri.parse(alunoSelecionado.getSite()));
		site.setIntent(intenteSite);
		
		MenuItem deletar = menu.add("Deletar");
		deletar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new AlertDialog.Builder(ListaAlunosActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Deletar")
				.setMessage("Deseja mesmo deletar?")
				.setPositiveButton("Quero",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
								dao.deletar(alunoSelecionado);
								dao.close();
								carregaLista();
							}
						}).setNegativeButton("Não", null).show();
				return false ;            
			}
		});
		
	}
    
    private void carregaLista() {
    	AlunoDAO dao = new AlunoDAO(this);
        alunos = dao.getLista();
        dao.close();
        ListaAlunosAdapter adapter = new ListaAlunosAdapter(this, alunos);
        
        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        listaAlunos.setAdapter(adapter);		
	}
    
}
