package kr.co.softbridge.NDKEcho;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class NDKEchoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button btnConnect = (Button)findViewById(R.id.connect);
        if (btnConnect != null)
        	btnConnect.setOnClickListener(onBtnConnectClick);
    }

    private View.OnClickListener onBtnConnectClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditText edtHost = (EditText)findViewById(R.id.host);
			EditText edtPort = (EditText)findViewById(R.id.port);
			RadioButton rbUDP = (RadioButton)findViewById(R.id.rbUDP);
			if (edtHost == null || edtPort == null || rbUDP == null)
				return;

			String sHost = edtHost.getText().toString();
			int iPort = Integer.parseInt(edtPort.getText().toString());
			int iType = (rbUDP.isChecked() == false? 0: 1);
 
			// open Chat Intent
			Intent intent = new Intent(NDKEchoActivity.this, NDKEchoShell.class);
			intent.putExtra("Host", sHost);
			intent.putExtra("Port", iPort);
			intent.putExtra("Type", iType);
			startActivityForResult(intent, 1);
		}
	};
}