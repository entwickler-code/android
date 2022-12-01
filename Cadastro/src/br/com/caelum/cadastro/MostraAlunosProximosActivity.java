package br.com.caelum.cadastro;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import br.com.caelum.cadastro.fragment.MapaFragment;

public class MostraAlunosProximosActivity extends FragmentActivity {

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map_layout);
		MapaFragment mapaFragment = new MapaFragment();
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.mapa, mapaFragment);
		tx.commit();
	}

}
