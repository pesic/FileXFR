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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OpenFile extends Fragment{
	
public static final int MAX_NUMBER_OF_FILES=100;
	
	Transfer parents;
	View view;
	ArrayList<String> list;
	String[] values;
	
	FileList fileList;
	String mPath;//current directory
	
	OpenFile(String path,Transfer p){
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
	    	
	        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, list);
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

	    		int status=fileList.check_if_select(position);
	    		if(status==0){
	    			fileList.select_file(position);
	    		}else if(status==1){
	    			fileList.unselect_file(position);
	    		}else if(status==5){
	    			Log.e("Listener","there was some error in indexing file");
	    		}
	    		
	    	
	    	}    

	
}
	
}//OPenFileFragment






/*
 * FileList is class which contain list of files and array( 0/1) which represents which files are selected(1)
 * */
class FileList{
	String[] files;
	int[] status;
	
	/*constructor for unselected files*/
	FileList(String[] filelist){
		files=filelist;
		status=new int[filelist.length];
	}
	/*constructor for files and selection*/
	FileList(String[] filelist,int[] selection){
		if(filelist.length!=selection.length){
			Log.e("OpenFile","Number of selected files didn't matched with selection number!");
			return;
		}
		files=filelist;
		status=selection;
	}
	/*for given files make selection*/
	boolean set_selection(int[] selection){
		if(selection.length!=files.length){
			Log.e("OpenFiles","number in selection array is different from number of files");
			return false;}
		else status=selection;
		return true;
		}
	/*return file name at index*/
	String get_file_at(int index){
		if(index<0 || index>files.length){
			Log.e("ListFiles","Index exceed FileList length");
			return null;
		}else if(files==null){
			Log.e("ListFiles","There is no files in the list");
			return null;
		}else {
			return files[index];
		}
		
	}
	
	
	/*returns array of selected files*/
	String[] get_selection(){
		
		int temp=0;
		for(int i=0;i<status.length;i++)if(status[i]!=0)temp++;//calculates number of selected files
		if(temp==0)return null;
		
		String[] result=new String[temp];
		temp=0;
		
		/*put selected files in string array*/
		for(int i=0;i<status.length;i++){
			if(status[i]!=0)result[temp++]=files[i];
		}
		
		return result;
	}
	/*returns number of selected files and -1 for error*/
	int get_number_of_selected_files(){
		if(status==null)return -1;
		int temp=0;
		for(int i=1;i<status.length;i++){
			if(status[i]!=0)temp++;
		}
		return temp;
	}
	
	
	
	boolean select_file(int index){
		if(index<0 || index>status.length){
			Log.e("ListFiles","Index exceed FileList length");
			return false;
		}else if(status==null){
			Log.e("ListFiles","There is no files in the list");
			return false;
		}else {
			status[index]=1;
			return true;
		}
		
	}
	
	boolean unselect_file(int index){
		if(index<0 || index>status.length){
			Log.e("ListFiles","Index exceed FileList length");
			return false;
		}else if(status==null){
			Log.e("ListFiles","There is no files in the list");
			return false;
		}else {
			status[index]=0;
			return true;
		}
		
	}
	
	/*return 0 for unselect, 1 for selected, or -1 for error*/
	int check_if_select(int index){
		if(index<0 || index>status.length){
			Log.e("ListFiles","Index exceed FileList length");
			return -1;
		}else if(status==null){
			Log.e("ListFiles","There is no files in the list");
			return 5;
		}else {
			if(status[index]==0)return 0;
			else return 1;
		}
	}
	
	
	}//FileList
	