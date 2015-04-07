package com.Nikola.filexfr;

import android.util.Log;


/*
 * FileList is class which contain list of files and array( 0/1) which represents which files are selected(1)
 * */
class FileList{
	private String[] files;
	private int[] status;//1-item selected,0-not selected
	
	/*constructor for unselected files*/
	FileList(String[] filelist){
		files=filelist;
		status=new int[filelist.length];
	}
	/*constructor for files and selection*/
	FileList(String[] filelist,int[] selection){
		if(filelist.length!=selection.length){
			Log.e("FileList","Number of selected files didn't matched with selection number!");
			return;
		}
		files=filelist;
		status=selection;
	}
	/*for given files make selection*/
	boolean set_selection(int[] selection){
		if(selection.length!=files.length){
			Log.e("FileList","number in selection array is different from number of files");
			return false;}
		else status=selection;
		return true;
		}
	
	
	public String[] get_files(){
		return files;
	}
	
	public int get_number_of_files(){
		return files.length;
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
	/*return false if there is no files in filelist*/
	boolean select_all(){
		if(!(status.length>0))return false;
		for(int i=0;i<status.length;i++){
			status[i]=1;
		}
		return true;
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
	
	/*return false if there is no files in filelist*/
	boolean unselect_all(){
		if(!(status.length>0))return false;
		for(int i=0;i<status.length;i++){
			status[i]=0;
		}
		return true;
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
	