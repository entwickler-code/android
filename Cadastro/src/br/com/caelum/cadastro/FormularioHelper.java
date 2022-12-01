package br.com.caelum.cadastro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import br.com.caelum.cadastro.modelo.Aluno;

public class FormularioHelper {

	private Aluno aluno;
	
	private EditText nome;
    private EditText telefone;
    private EditText site;
    private SeekBar nota;
    private EditText endereco;
    private ImageView foto;
    
    public FormularioHelper(FormularioActivity activity) {
		
		aluno = new Aluno();

		nome = (EditText) activity.findViewById(R.id.nome);
		telefone = (EditText) activity.findViewById(R.id.telefone);
		site = (EditText) activity.findViewById(R.id.site);
		nota = (SeekBar) activity.findViewById(R.id.nota);
		endereco = (EditText) activity.findViewById(R.id.endereco);
		foto = (ImageView) activity.findViewById(R.id.foto);
		
	}

	public Aluno pegaAlunoDoFormulario() {
	
		aluno.setNome(nome.getEditableText().toString());
		aluno.setEndereco(endereco.getEditableText().toString());
		aluno.setSite(site.getEditableText().toString());
		aluno.setTelefone(telefone.getEditableText().toString());
		aluno.setNota(Double.valueOf(nota.getProgress()));

		return aluno;
		
	}
	
	public void colocaNoFormulario(Aluno aluno) {
		nome.setText(aluno.getNome());
		telefone.setText(aluno.getTelefone());
		site.setText(aluno.getSite());
		nota.setProgress(aluno.getNota().intValue());
		endereco.setText(aluno.getEndereco());
		
		this.aluno = aluno;
		
		if (aluno.getFoto() != null) {
			this.carregaImagem(aluno.getFoto());
		}
	}	
	
	public ImageView getFoto() {
		return foto;
	}
	
	public void carregaImagem(String localArquivoFoto) {
		Bitmap imagemFoto = BitmapFactory.decodeFile(localArquivoFoto);
		Bitmap imagemFotoReduzida = Bitmap.createScaledBitmap(imagemFoto, 100, 100, true);
		
		aluno.setFoto(localArquivoFoto);
		foto.setImageBitmap(imagemFotoReduzida);
	}
	
}
