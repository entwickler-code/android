package br.com.caelum.cadastro.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.com.caelum.cadastro.R;
import br.com.caelum.cadastro.modelo.Prova;

public class DetalhesProvaFragment extends Fragment {
	
	private TextView materia;
	private TextView data;
	private ListView topicos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.provas_detalhe, container, false);

		Prova prova = null;
		
		if (getArguments() != null) {
			prova = (Prova) getArguments().getSerializable("prova");
		}

		buscaComponentes(layout);
		populaCamposComDadosDaProva(prova);

		return layout;
	}

	public void populaCamposComDadosDaProva(Prova prova) {
		if (prova != null) {
			this.materia.setText(prova.getMateria());
			this.data.setText(prova.getData());

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,prova.getTopicos());
			this.topicos.setAdapter(adapter);
		}
	}

	private void buscaComponentes(View layout) {
		this.materia = (TextView) layout.findViewById(R.id.detalhe_prova_materia);
		this.data    = (TextView) layout.findViewById(R.id.detalhe_prova_data);
		this.topicos = (ListView) layout.findViewById(R.id.detalhe_prova_topicos);
	}
	
}