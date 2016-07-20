package kr.co.tinydew4.errno_list;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class ErrnoListActivity extends Activity {

	static {
		try {
			System.loadLibrary("ndk-errno");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    static private native String NDKErrnoToString(int AErrno);

    private EditText m_Input = null;
	private TextView m_Result = null;
	private ArrayList<String> m_ErrnoList1 = null;
	private ArrayList<String> m_ErrnoList2 = null;
	private ArrayList<String> m_ErrnoList3 = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        if (tabHost != null)
        	tabHost.setup();

        TabHost.TabSpec spec;
        if ((spec = tabHost.newTabSpec("tag1")) != null) {
            spec.setContent(R.id.tab1);
            spec.setIndicator(getResources().getString(R.string.Tab1Title));
            tabHost.addTab(spec);
        }
        if ((spec = tabHost.newTabSpec("tag2")) != null) {
            spec.setContent(R.id.tab2);
            spec.setIndicator(getResources().getString(R.string.Tab2Title));
            tabHost.addTab(spec);
        }
        if ((spec = tabHost.newTabSpec("tag3")) != null) {
            spec.setContent(R.id.tab3);
            spec.setIndicator(getResources().getString(R.string.Tab3Title));
            tabHost.addTab(spec);
        }
        if ((spec = tabHost.newTabSpec("tag4")) != null) {
            spec.setContent(R.id.tab4);
            spec.setIndicator(getResources().getString(R.string.Tab4Title));
            tabHost.addTab(spec);
        }

        tabHost.setCurrentTab(0);
        
        m_Input = (EditText)findViewById(R.id.EdtErrno);
        m_Result = (TextView)findViewById(R.id.LbResult);
        
        Button btnSearch = (Button)findViewById(R.id.BtnErrno);
        if (btnSearch != null)
        	btnSearch.setOnClickListener(BtnErrnoClick);

        ListView lvErrno;
        lvErrno = (ListView)findViewById(R.id.tab2);
        if (lvErrno != null) {
        	lvErrno.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, (m_ErrnoList1 = new ArrayList<String>())));
        } else {
        	Log.e(this.getClass().getName(), "tab2 is null");
        }
        lvErrno = (ListView)findViewById(R.id.tab3);
        if (lvErrno != null) {
        	lvErrno.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, (m_ErrnoList2 = new ArrayList<String>())));
        } else {
        	Log.e(this.getClass().getName(), "tab3 is null");
        }
        lvErrno = (ListView)findViewById(R.id.tab4);
        if (lvErrno != null) {
        	lvErrno.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, (m_ErrnoList3 = new ArrayList<String>())));
        } else {
        	Log.e(this.getClass().getName(), "tab4 is null");
        }

        for (int i = 1; i <= 131; i++)
        	(i <= 50? m_ErrnoList1: (i <= 100? m_ErrnoList2: m_ErrnoList3)).add(String.format(getResources().getString(R.string.fmtListItem), i, NDKErrnoToString(i)));
    }

    View.OnClickListener BtnErrnoClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (m_Input != null && m_Result != null) {
				String sInput = m_Input.getText().toString();
				int iInput = Integer.parseInt(sInput);
				m_Result.setText(NDKErrnoToString(iInput));
			}
		}
	};
}