package br.com.caelum.cadastro;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import br.com.caelum.cadastro.dao.AlunoDAO;
import br.com.caelum.cadastro.modelo.Aluno;

public class FormularioActivity extends Activity {

	private static final int TIRA_FOTO = 123;
	
	private Aluno aluno = new Aluno();
	private FormularioHelper helper;
	private String localArquivoFoto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formulario);
		
		helper = new FormularioHelper(this);
		
		Button botao = (Button) findViewById(R.id.botao);
		
		aluno = (Aluno) getIntent().getSerializableExtra(Extras.ALUNO_SELECIONADO);

		if (aluno == null) {
			aluno = new Aluno();
		} else {
			botao.setText("Alterar");
			helper.colocaNoFormulario(aluno);
		}
		
		botao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aluno = helper.pegaAlunoDoFormulario();
				
				AlunoDAO dao = new AlunoDAO(FormularioActivity.this);
				if(aluno.getId() != null) {
					dao.alterar(aluno);
				} else {
					dao.insere(aluno);	
				}
				dao.close();
				
				finish();
			}
		});
		
		ImageView foto = helper.getFoto();
		foto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent irParaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				localArquivoFoto = Environment.getExternalStorageDirectory() + "/"+ System.currentTimeMillis()+".jpg";
				File arquivo = new File(localArquivoFoto);
				Uri localFoto = Uri.fromFile(arquivo);
				
				irParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, localFoto);
				startActivityForResult(irParaCamera, TIRA_FOTO);
			}
		});		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TIRA_FOTO) {
			if (resultCode == Activity.RESULT_OK) {
				helper.carregaImagem(this.localArquivoFoto);
			} else {
				this.localArquivoFoto = null;
			}
		}
	}

}
