package com.Nikola.filexfr;


/*PART OF RECEIVING FILE TREE*/

import java.io.File;
import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SendingFileFragment extends Fragment{
	
public static final int MAX_NUMBER_OF_FILES=100;
	
	Transfer parents;
	View view;
	ArrayList<String> list;
	String[] values;
	SFAdapter adapter;
	FileList fileList;
	String mPath;//current directory
	
	SendingFileFragment(String path,Transfer p){
		super();
		mPath=path;
		parents =p;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
    	File file =new File(mPath);
	 
    	values=file.list();
	 
	   fileList=new FileList(values);
	}
	
	
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	    	Log.i("FRAGMETN ONCREATE","ENTERED");
	    	
	    	view=inflater.inflate(R.layout.f_sendingfile, container, false);
	    	
	    	
	        final ListView listview = (ListView) view.findViewById(R.id.listview);
	        
	        
	        if(values==null || values.length==0){
	        	TextView tv=(TextView)view.findViewById(R.id.emptyfoldertw);
	        	tv.setVisibility(View.VISIBLE);
	        	tv.setText("There is no files in this folder");
	        	listview.setVisibility(View.GONE);
	        	Log.i("OnCrtV","empty folder list");
	        	
	        }else{
	        	TextView tv=(TextView)view.findViewById(R.id.emptyfoldertw);
	        	tv.setVisibility(View.GONE);
	        	listview.setVisibility(View.VISIBLE);
	        	Log.i("OnCrtV","folder list");
	        }
	       
	        	
	       listview.setOnItemClickListener(new Listener());

	    	
	    	list = new ArrayList<String>();
	    	
	        for (int i = 0; i < values.length; i++) {
	          list.add(values[i]);
	        }
	    	
	        adapter=new SFAdapter(parents,getActivity(),android.R.layout.simple_list_item_1,fileList);
	         
	        
	         for(int i=0;i<values.length;i++){
	        	 File temp=new File(mPath+"/"+adapter.get_file_at(i));
		    		
	        	 if(temp.isDirectory()){
	        		 adapter.set_item_folder_status_FOLDER(i);
	        		 System.out.println(temp.toString()+adapter.get_item_folder_status(i));
		    		}
	         }
	        listview.setAdapter(adapter);
	        
	    	//tw.setText(sb.toString());
	    	return view;
	        
	    }
	    
	    String[] get_selected_files(){
	    	return fileList.get_selection();
	    }
	    
	    int get_number_of_selected_files(){
	    	return fileList.get_number_of_selected_files();
	    }
	    
	    SFAdapter get_adapter(){
	    	return adapter;
	    }
	    /*Handler for the item click event
	     * if item is directory, method passes its path to Transfer class to start new fragment
	     * if item is file, method marks it in FileList class (status) 
	     * */
	   class Listener implements AdapterView.OnItemClickListener{

	    	@Override
	    	public void onItemClick(AdapterView<?> parent, View view, int position,
	    			long id) {
	    		
	    		String name=fileList.get_file_at(position);
	    		if(name==null)return;
	    		Log.i("LISTENER",mPath+"/"+name);
	    		File temp=new File(mPath+"/"+name);
	    		
	    		if(temp.isDirectory()){
	    			parents.go_into_folder(temp.toString());
	    		}
	    		
	    		
	    		int status=adapter.check_if_selected(position);
	    		
	    		if(status==0){
	    			adapter.select_item(position);
	    		}else if(status==1){
	    			adapter.unselect_item(position);
	    		}else if(status==-1){
	    			Log.e("Listener","there was some error in indexing file");
	    		}
	    		
	    	
	    	}    
	   }//ListenerClass
	
}//SendingFileFragmentFragment


