package br.com.caelum.cadastro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import br.com.caelum.cadastro.dao.AlunoDAO;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];

		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

		AlunoDAO dao = new AlunoDAO(context);
		if (dao.isAluno(smsMessage[0].getDisplayOriginatingAddress())) {
			Toast.makeText(context, "SMS de Aluno: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG).show();
			MediaPlayer mp = MediaPlayer.create(context, R.raw.msg);
			mp.start();
		}
		dao.close();

	}

}
