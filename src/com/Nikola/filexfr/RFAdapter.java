package com.Nikola.filexfr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RFAdapter extends ArrayAdapter<String>{
	
	private FileList flist;
	private final Context context;
	private Listener listener;
	private boolean select_all_flag=false;
	
	public RFAdapter(Context context, int resource, FileList flist) {
		super(context, resource, flist.get_files());
		
		try{
		
		if(flist==null||flist.get_files()==null){
			Log.e("RFAdapter","There is no files in files");
		}
		this.flist=flist;
		
		listener=new Listener();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		this.context=context;
	}
	
	public Listener getListener(){
		return listener;
	}
	public Context getContext(){
		return context;
	}
	public FileList getFileList(){
		return flist;
	}
	
	public int get_number_of_files(){
		return flist.get_number_of_files();
	}
	
	public int get_number_of_selected_files(){
		return flist.get_number_of_selected_files();
	}
	
	public String[] get_selected_files(){
		return flist.get_selection();
	}
	public String get_file_at(int index){
		return flist.get_file_at(index);
	}
	
	
	public void select_all(){
		  if(!flist.select_all()){Log.i("RFAdapter","There is no files to select");return;}
		  this.notifyDataSetChanged();
	  }
	public void unselect_all(){
		  if(!flist.unselect_all()){Log.i("RFAdapter","There is no files to select");return;}
		  this.notifyDataSetChanged();
	  }
	 public void select_all_pressed(){
		 if(select_all_flag){
			 select_all_flag=false;
			 unselect_all();
		 }else{
			 select_all_flag=true;
			 select_all();
		 }
	 }
	  
	  public void select_item(int index){
		  if(!flist.select_file(index)){Log.i("RFAdapter","Wrong index");return;}
		  this.notifyDataSetChanged();
	  }
	  
	  public void unselect_item(int index){
		  if(!flist.unselect_file(index)){Log.i("RFAdapter","Wrong index");return;}
		  this.notifyDataSetChanged();
	  }
	  
	  public int check_if_selected(int index){
		  return flist.check_if_select(index);
	  }
	  
	  
	  public void update_content(FileList flist){
		  this.flist=flist;
		  notifyDataSetChanged();
	  }
	  
	
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		  
		  try{
	   
			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			  View rowView = inflater.inflate(R.layout.r_lvitem, parent, false);
			  //if(position>=flist.get_number_of_files())return rowView;
			  TextView textView = (TextView) rowView.findViewById(R.id.item_tv);
			  ImageView file_icon= (ImageView) rowView.findViewById(R.id.item_icon);
			  ImageView status_icon= (ImageView) rowView.findViewById(R.id.item_status);
			  
			  
			  switch(flist.check_if_select(position)){
			  	case 0: status_icon.setVisibility(View.INVISIBLE);break;
			  	case 1: status_icon.setVisibility(View.VISIBLE);break;
			  	default: return null;
			  }
			
	    	
			  String helper=flist.get_file_at(position);
			  textView.setText(helper);
			  String extension = "";

			  /*Set icon regarding file extension*/
			  int i = helper.lastIndexOf('.');
			  if (i >= 0) {
			      extension = helper.substring(i+1);
			  }
			  
			  if(extension.equals("jpg")||extension.equals("jpeg")||extension.equals("png")
					  ||extension.equals("icon")){
				  file_icon.setBackgroundResource(R.drawable.picture_icon);
				  }
			  else if(extension.equals("mp3")||extension.equals("flac")||
					  extension.equals("mmf")||extension.equals("aac")){
				  file_icon.setBackgroundResource(R.drawable.sound_icon);
			  }else if(extension.equals("flv")||extension.equals("mkv")||
					  extension.equals("mp4")||extension.equals("avi")
					  ||extension.equals("wmv")||extension.equals("mpg")){
				  file_icon.setBackgroundResource(R.drawable.video_icon);
			  }else if(extension.equals("pdf")){
				  file_icon.setBackgroundResource(R.drawable.pdf_icon);
			  }else{
				  file_icon.setBackgroundResource(R.drawable.file_icon);
			  }
			  
			  return rowView;
	   
		  }catch(Exception e){
			  
			  Log.e("OV",e.toString());
			  e.printStackTrace();
	
			  System.exit(1);
			  
		  }
		  return null;
	  }//getView
	  
	  
	  class Listener implements AdapterView.OnItemClickListener{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				switch(flist.check_if_select(position)){
				case -1:Log.e("RFF Touch Listener","There was error on selecion check");break;
				case 0:
					flist.select_file(position);
					notifyDataSetChanged();
					break;
				
				case 1:
					flist.unselect_file(position);
					notifyDataSetChanged();
					break;
				}

			}
			}//Listener


}//RFAdapter
