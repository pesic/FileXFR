package com.Nikola.filexfr;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ReceivingFileFragment extends Fragment{

	Transfer parents;
	View view;
	ListView listView;
	FileList flist;
	boolean select_all_active=false;
	LayoutInflater minflater;
	ViewGroup	mcontainer;
	RFAdapter adapter;
	Link link;
	
	ReceivingFileFragment(Transfer p){
		super();
		try{
			parents =p;
			link=new Link(parents);
		
			String list_of_files= link.execute("0").get();
			
			if(list_of_files.equals("SERVER ERROR")){
				Toast.makeText(parents, "No response from server.\n Please check if server is started",Toast.LENGTH_LONG).show();
			}else if(list_of_files!=null){
				flist=new FileList(list_of_files.split("\\n"));
			}else{
				flist=null;
				Log.i("RFF","There is no data to transfer from comp!");
			}
		}catch(Exception e){
			Log.e("RFF","Cannot initialize RFT");
			System.exit(1);
		}
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
			// Inflate the layout for this fragment
			Log.i("FRAGMETN ONCREATE","ENTERED");
			minflater=inflater;
			mcontainer=container;
			
			view=inflater.inflate(R.layout.f_receivingfile, container, false);
			
			final ListView listview = (ListView) view.findViewById(R.id.rlistview);
			Button button=(Button)view.findViewById(R.id.rff_select_all);
			button.setOnClickListener(new ButtonListener());
			button=(Button)view.findViewById(R.id.rff_download);
			button.setOnClickListener(new ButtonListener());
			button=(Button)view.findViewById(R.id.rff_refresh);
			button.setOnClickListener(new ButtonListener());
			if(flist==null)return view;
			
			for(String file:flist.get_files()){
				try{
					Log.i("Lines",file);

					/*separate extension from file name*/
					
					}catch(Exception e){
						Log.e("while", e.toString());
						e.printStackTrace();
						System.exit(1);
					}
				
			}
	
			try{
				adapter=new RFAdapter(parents,android.R.layout.simple_list_item_2, flist);
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(adapter.getListener());
				
			}catch(Exception e){
				Log.e("RFF onCV",e.toString());
				e.printStackTrace();
				System.exit(1);
			}
				return view;

	}
	
	class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			Log.i("RFF button pressed",""+v.getId()+" "+R.id.rff_refresh);
			
			switch(v.getId()){
			case R.id.rff_refresh: {refresh_pressed(); Log.i("RFF button","refresh pressed");break;}
			case R.id.rff_select_all:{ select_all_pressed(); Log.i("RFF button","Select all pressed");break;}
			case R.id.rff_download: {download_presssed(); Log.i("RFF button","download pressed");break;}
			}//sw
			
		}
		
		
	}
	
	private void download_presssed(){
		
		Log.i("TRANSFER","Entered into download button");
		for(int i=0;i<adapter.get_number_of_files();i++){
			
			if(adapter.check_if_selected(i)==1){
				Link l=new Link(parents);
				l.set_filename(adapter.get_file_at(i));
				l.execute(""+(i+1));// +1 is added because 0 is used for content request, decrement is done on server side 	
				Log.i("Download request",""+adapter.get_file_at(i));
				l=null;
			}
			}
		
	}
	
	
	private void refresh_pressed(){
		try{
			String list_of_files= new Link(parents).execute("0").get();
			if(list_of_files!=null){
				adapter.update_content(new FileList(list_of_files.split("\\n")));
			}else{
				Log.i("RFF refresh","There is no filesin folder");
				adapter.update_content(new FileList(new String[]{"FOLDER IS EMPTY"}));
			}
		}catch(Exception e){
			Log.i("RFF refresh","Cannot execute refresh function");
			e.printStackTrace();
		}
	}
	
	public boolean set_content_view(LayoutInflater inflater,ViewGroup container, boolean refresh_pressed){
		
		view=inflater.inflate(R.layout.f_receivingfile, container, false);
		final ListView listview = (ListView) view.findViewById(R.id.listview);
		listview.removeAllViews();
		
		
		
		return true;
	}
	
	private void select_all_pressed(){	
		adapter.select_all_pressed();
	}
	
	
}//RFF
