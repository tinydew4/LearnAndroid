package kr.co.wikibook.socket_messenger;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SocketMessengerActivity extends Activity {
	
	public static final int SERVER_MODE = 0x00;
	public static final int CLIENT_MODE = 0x01;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button connect_to_server = (Button)findViewById(R.id.connect_to_server);
        connect_to_server.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View v) {
        			EditText server_ip = (EditText)findViewById(R.id.server_ip);
        			String ip = server_ip.getText().toString();
        			EditText server_port = (EditText)findViewById(R.id.server_port);
        			String port = server_port.getText().toString();
        			
        			Intent it = new Intent(SocketMessengerActivity.this, Chat.class);
        			it.putExtra("mode", CLIENT_MODE);
        			it.putExtra("ip", ip);
        			it.putExtra("port", port);
       			startActivity(it);
        		}
        	}
        );
        
        Button run_as_server = (Button)findViewById(R.id.run_as_server);
        run_as_server.setOnClickListener(
        		new OnClickListener() {
        			public void onClick(View v) {
        				Intent it = new Intent(SocketMessengerActivity.this, Chat.class);
        				it.putExtra("mode", SERVER_MODE);
        				startActivity(it);
        			}
        		}
        );
    }
}