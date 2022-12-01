package br.com.caelum.cadastro.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.caelum.cadastro.R;
import br.com.caelum.cadastro.modelo.Aluno;

public class ListaAlunosAdapter extends BaseAdapter {

	private final List<Aluno> alunos;
	private final Activity activity;

	public ListaAlunosAdapter(Activity activity, List<Aluno> alunos) {
		this.activity = activity;
		this.alunos = alunos;
	}
	
	public long getItemId(int posicao) {
		return alunos.get(posicao).getId();
	}

	public Object getItem(int posicao) {
		return alunos.get(posicao);
	}
	
	public int getCount() {
		return alunos.size();
	}
	
	public View getView(int posicao, View convertView, ViewGroup parent) {

		View view = activity.getLayoutInflater().inflate(R.layout.item, null);

		Aluno aluno = alunos.get(posicao);
		
		ViewGroup fundo = (ViewGroup) view.findViewById(R.id.fundo);
		
		if (posicao % 2 == 0) {
			fundo.setBackgroundColor(activity.getResources().getColor(R.color.linha_par));
		} else {
			fundo.setBackgroundColor(activity.getResources().getColor(R.color.linha_impar));
		}
		
		TextView nome = (TextView) view.findViewById(R.id.nome);
		nome.setText(aluno.toString());
		
		Bitmap bm; 
		
		if (aluno.getFoto() != null) {
			bm = BitmapFactory.decodeFile(aluno.getFoto());
		} else {
			bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_no_image);
		}
		
		bm = Bitmap.createScaledBitmap(bm, 100, 100, true);
		
		ImageView foto = (ImageView) view.findViewById(R.id.foto);
		foto.setImageBitmap(bm);
		
		TextView telefone = (TextView) view.findViewById(R.id.telefone);
		if (telefone != null) {
			telefone.setText(aluno.getTelefone());
		}
		
		TextView site = (TextView) view.findViewById(R.id.site);
		if (site != null) {
			site.setText(aluno.getSite());
		}

		return view;
	}
	
}