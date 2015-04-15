package com.Nikola.filexfr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class Transfer extends Activity{  
	
	public final String SERVER_IP_ADDRESS="192.168.0.11";
	public final int RECEIVING_DATA_PORT=1030;
	public final int SENDING_DATA_PORT=1031;
	
	public boolean CHOOSE_FRAGMENT_ACTIVE=false;
	
	TextView tw;
	SimpleCursorAdapter mAdapter;
	OnCheckedChangeListener listener;
	FragmentManager fragmentManager;
	SendingFileFragment fragment;
	
	String mPath;
	String rootPath=Environment.getExternalStorageDirectory().toString();
	
	private int[] selection; //
	public String[] listOfFiles;
	
	ExecutorService exec;//for remote connection threads
	
	
	public void set_text(String s){
		
		Log.i("TRANSFER","entered into set_text "+s);
		tw=(TextView)findViewById(R.id.tw);
		tw.setText(s);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_transfer);
		
		tw=(TextView)findViewById(R.id.tw);
		
		mPath=rootPath;
		/*Checkboxes listener*/
		listener=new CompoundButton.OnCheckedChangeListener() {

		       @Override
		       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

		    	   if(isChecked)
		    		   selection[buttonView.getId()]=1;
		    	   else selection[buttonView.getId()]=0;
		       }
		   };     	
		 
		 /*FRAGMENT FOR SENDING FILES*/
		 try{
			 	fragmentManager = getFragmentManager();
			 	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			 	Choose_xfr_type fragment = new Choose_xfr_type(this);
			 	fragmentTransaction.add(R.id.mainLayout, fragment);
			 	CHOOSE_FRAGMENT_ACTIVE=true;
				 fragmentTransaction.commit();
				 
		 }catch(Exception e){
			 Log.e("TRANSFER ERROR",e.toString());
			 System.exit(1);
		 }
		   
	}//onCrt
	
	/*creates new fragment to show folder content*/
	public void go_into_folder(String path){
		
		try{
			mPath=path;
			FragmentTransaction ft=fragmentManager.beginTransaction();
			fragment=new SendingFileFragment(path,this);
			ft.replace(R.id.mainLayout, fragment);
			ft.commit();
		}catch(Exception e){
			Log.e("Fragment REPLACEMENT",e.toString());
			System.exit(1);
		}
	}
	
	/* functions for buttons clicks handling*/
	
	public void exit(View v){
		
		System.exit(0);
	}
	
	public void refresh(View v){
		this.set_text("Select file to download");
		new Link(this).execute("0");
	}
	
	public void download(View v){
		
		Log.i("TRANSFER","Entered into download button");
		for(int i=0;i<selection.length;i++){
	
			
			if(selection[i]!=0){
				Link l=new Link(this);
				l.execute(""+(i+1));// +1 is added because 0 is used for content request, decrement is done on server side 
				
				Log.i("Download request",""+listOfFiles[i]);
				l=null;
			}
			Log.i("Sum",listOfFiles[i]+" " +selection[i]);
		}
}
	
	public void select_all(View view){
		for(int i=0;i<selection.length;i++){
			CheckBox cb=(CheckBox)findViewById(i);
			cb.setChecked(true);
		}
	}

	
	public void send_files(View v){
		
		try{
			
		String[] query=fragment.get_adapter().get_selected_files();
		String text="";
		if(query==null){
			if(fragment.get_adapter().get_number_of_selected_files()==0){
				text="There is no selected file!";
			}else text="There was error during listing files";
		}else{//query not null
					
				/*create thread to send selected files*/
				exec = Executors.newCachedThreadPool();
				exec.execute(new SendingFilesThread(this,query));
				
				Log.i("TRANSFER-send_but","SFT created");
				for(String s:query){ if(s!=null)text=text+"\n"+s; }
					
				}
		
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
			}
		
	}
	
	/*check if we are not in root folder, if not go one step up*/
	public void go_back(View v){
		 if(mPath.equals(rootPath)){return;}
		 Log.i(mPath,rootPath);
		 int end=mPath.lastIndexOf("/");
		 go_into_folder(mPath.substring(0, end));
		 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

public void set_sending_fragment(){
	try{
	 	
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mWifi.isConnected()) {
		    Toast.makeText(this, "Please start WiFi and try again", Toast.LENGTH_LONG).show();
		    return;
		}
		
	 	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		 
	 	 //ReceivingFileFragment fragment = new ReceivingFileFragment(this);
	 	SendingFileFragment fragment = new SendingFileFragment(mPath,this);
	 	fragmentTransaction.replace(R.id.mainLayout, fragment);
	 	CHOOSE_FRAGMENT_ACTIVE=false;
		 fragmentTransaction.commit();
		 
 }catch(Exception e){
	 Log.e("TRANSFER ERROR",e.toString());
	 System.exit(1);
 }
}

public void set_receiving_fragment(){
	
	try{
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mWifi.isConnected()) {
		    Toast.makeText(this, "Please start WiFi and try again", Toast.LENGTH_LONG).show();
		    return;
		}
	 	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		 
	 	 ReceivingFileFragment fragment = new ReceivingFileFragment(this);
	 	fragmentTransaction.replace(R.id.mainLayout, fragment);
	 	CHOOSE_FRAGMENT_ACTIVE=false;
		 fragmentTransaction.commit();
		 
 }catch(Exception e){
	 Log.e("TRANSFER ERROR",e.toString());
	 System.exit(1);
 }

}


/*start sending/receiving fragment on click*/
public void onClickHandler(View v){
	
	switch(v.getId()){
	case R.id.l1:set_sending_fragment();break;
	case R.id.l2:set_sending_fragment();break;
	case R.id.l3:set_sending_fragment();break;
	case R.id.l4:set_sending_fragment();break;
	case R.id.l5:set_sending_fragment();break;
	case R.id.l6:set_sending_fragment();break;
	case R.id.l7:set_sending_fragment();break;
	case R.id.l8:set_sending_fragment();break;
	case R.id.l9:set_sending_fragment();break;
	case R.id.r1:set_receiving_fragment();break;
	case R.id.r2:set_receiving_fragment();break;
	case R.id.r3:set_receiving_fragment();break;
	case R.id.r4:set_receiving_fragment();break;
	case R.id.r5:set_receiving_fragment();break;
	case R.id.r6:set_receiving_fragment();break;
	case R.id.r7:set_receiving_fragment();break;
	case R.id.r8:set_receiving_fragment();break;
	case R.id.r9:set_receiving_fragment();break;
	
	}
}

public void onBackPressed() {
	if(CHOOSE_FRAGMENT_ACTIVE){System.exit(0);}
	 try{
		 	fragmentManager = getFragmentManager();
		 	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		 	Choose_xfr_type fragment = new Choose_xfr_type(this);
		 	CHOOSE_FRAGMENT_ACTIVE=true;
		 	fragmentTransaction.replace(R.id.mainLayout, fragment);
			 fragmentTransaction.commit();
			 
	 }catch(Exception e){
		 Log.e("TRANSFER ERROR",e.toString());
		 System.exit(1);
	 }


}

}//Transfer

class Choose_xfr_type extends Fragment{
	
	
	Transfer parent;
	Animation animationSlideInLeft;
	Animation animationSlideInRight;
	
	Choose_xfr_type(Transfer p){
		parent=p;
		
		animationSlideInLeft = AnimationUtils.loadAnimation(parent,android.R.anim.slide_in_left);
   	 	animationSlideInRight = AnimationUtils.loadAnimation(parent,R.anim.slide_in_right);
   	    animationSlideInLeft.setDuration(1000);
   	    animationSlideInRight.setDuration(1000);
   	       
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	Log.i("FRAGMETN ONCREATE","ENTERED");
		
		
    	View view=inflater.inflate(R.layout.f_choose_xfr_type, container, false);
    	int i=800;
    	int step=0;
   
    	TextView tw1=(TextView)view.findViewById(R.id.twbutton1);
    	tw1.startAnimation(animationSlideInLeft);
    	TextView tw2=(TextView)view.findViewById(R.id.twbutton2);
    	tw2.setAnimation(animationSlideInRight);
    	ImageView iwl1=(ImageView)view.findViewById(R.id.l1);
    	iwl1.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl2=(ImageView)view.findViewById(R.id.l2);
    	iwl2.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl3=(ImageView)view.findViewById(R.id.l3);
    	iwl3.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl4=(ImageView)view.findViewById(R.id.l4);
    	iwl4.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl5=(ImageView)view.findViewById(R.id.l5);
    	iwl5.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl6=(ImageView)view.findViewById(R.id.l6);
    	iwl6.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl7=(ImageView)view.findViewById(R.id.l7);
    	iwl7.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl8=(ImageView)view.findViewById(R.id.l8);
    	iwl8.startAnimation(animationSlideInLeft);
    	animationSlideInLeft.setDuration(i+=step);
    	ImageView iwl9=(ImageView)view.findViewById(R.id.l9);
    	iwl9.startAnimation(animationSlideInLeft);
    	ImageView iwr1=(ImageView)view.findViewById(R.id.r1);
    	iwr1.setAnimation(animationSlideInRight);
    	ImageView iwr2=(ImageView)view.findViewById(R.id.r2);
    	iwr2.setAnimation(animationSlideInRight);
    	ImageView iwr3=(ImageView)view.findViewById(R.id.r3);
    	iwr3.setAnimation(animationSlideInRight);
    	ImageView iwr4=(ImageView)view.findViewById(R.id.r4);
    	iwr4.setAnimation(animationSlideInRight);
    	ImageView iwr5=(ImageView)view.findViewById(R.id.r5);
    	iwr5.setAnimation(animationSlideInRight);
    	ImageView iwr6=(ImageView)view.findViewById(R.id.r6);
    	iwr6.setAnimation(animationSlideInRight);
    	ImageView iwr7=(ImageView)view.findViewById(R.id.r7);
    	iwr7.setAnimation(animationSlideInRight);
    	ImageView iwr8=(ImageView)view.findViewById(R.id.r8);
    	iwr8.setAnimation(animationSlideInRight);
    	ImageView iwr9=(ImageView)view.findViewById(R.id.r9);
    	iwr9.setAnimation(animationSlideInRight);

    	return view;
    }
	
	
}//Choose_xfr_type
