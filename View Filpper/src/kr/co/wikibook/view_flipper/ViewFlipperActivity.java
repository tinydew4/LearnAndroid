package kr.co.wikibook.view_flipper;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewFlipperActivity extends Activity {
	
	ViewFlipper vf = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        vf = (ViewFlipper)findViewById(R.id.flipper);
        vf.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) {
        			vf.setInAnimation(ViewFlipperActivity.this, android.R.anim.slide_in_left);
        			vf.setOutAnimation(ViewFlipperActivity.this, android.R.anim.slide_out_right);
        			vf.showNext();
        		}
        	}
   		);
        
        registerForContextMenu(vf);
        
        ListView listview = (ListView)findViewById(R.id.listview);
        
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.add("소녀시대");
        arraylist.add("원더걸스");
        arraylist.add("카라");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.name, arraylist);
        
        listview.setAdapter(adapter);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.setHeaderTitle("음식 고르기");
    	menu.add(0, v.getId(), 0, "김밥");
    	menu.add(0, v.getId(), 0, "라면");
    	menu.add(0, v.getId(), 0, "짬뽕");
    	menu.add(0, v.getId(), 0, "우동");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	String message = "";
    	
    	if (item.getTitle().equals("김밥"))
    		message = "김밥을 선택하셨습니다.";
    	else if (item.getTitle().equals("라면"))
    		message = "라면을 선택하셨습니다.";
    	else if (item.getTitle().equals("짬뽕"))
    		message = "짬뽕을 선택하셨습니다.";
    	else if (item.getTitle().equals("우동"))
    		message = "우동을 선택하셨습니다.";
    	else
    		return false;
    	
    	message += "\n아이템 아이디는 " + item.getItemId() + " 입니다.";
    	
    	Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
    	toast.show();
    	
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "새 메시지 작성하기");
    	menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "내용 보기");
    	    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return (itemCallback(item) || super.onOptionsItemSelected(item));
    }
    
    public boolean itemCallback(MenuItem item) {
    	switch (item.getItemId()) {
    		case Menu.FIRST + 1:
    			return true;
    	}
    	
    	return false;
    }
}