package com.Nikola.filexfr;

import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SFAdapter extends RFAdapter{
	
	boolean[] item_sent_status;
	boolean[] item_is_folder;//true-yes,false-no
	Transfer parent;
	public SFAdapter(Transfer parent,Context context, int resource, FileList flist) {
		
		super(context, resource, flist);
		this.parent=parent;
		item_sent_status=new boolean[get_number_of_files()];
		item_is_folder=new boolean[get_number_of_files()];
	}
	
	/*set status of item(is it folder or file) so ClickListener could handle it easier*/
	public void set_all_item_folder_status(boolean[] if_status){
		
		if(if_status==null){item_is_folder=new boolean[get_number_of_files()];}
		if(if_status.length!=get_number_of_files()){item_is_folder=new boolean[get_number_of_files()];}
		item_is_folder=if_status;
	}
	public void set_item_folder_status_FOLDER(int index){
		
		if(index<0 || index>=item_is_folder.length){Log.i("SFA","set_item_folder_status_FILE BAD INDEX"); return;}
		item_is_folder[index]=true;
	}
	public String[] get_selected_files(){
		return this.getFileList().get_selection();
	}
	public boolean get_item_folder_status(int index){
		
		//if(index<0 || index>=item_is_folder.length){Log.i("SFA","Error in indexing IF status check");return;}
		try{
		return item_is_folder[index];
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}


	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		  
		  try{
	   
			  LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			  
			  if(item_sent_status[position]){
				  View rowView = inflater.inflate(R.layout.s_lvitem_sent, parent, false);
				  TextView textView = (TextView) rowView.findViewById(R.id.s_item_sent_tv2);
				  textView.setText(this.getItem(position));
				  return rowView;
			  }
			  
			  View rowView = inflater.inflate(R.layout.s_lvitem, parent, false);
			  
			  TextView textView = (TextView) rowView.findViewById(R.id.s_item_tv);
			  ImageView file_icon= (ImageView) rowView.findViewById(R.id.s_item_icon);
			  ImageView status_icon= (ImageView) rowView.findViewById(R.id.s_item_status);
			  
			  Log.i("getView","Info"+check_if_selected(position));
			  
			  if(item_sent_status[position]){
				  
			  }
			  if(!item_is_folder[position]){
			  switch(check_if_selected(position)){
			  	case 0: {status_icon.setVisibility(View.INVISIBLE);break;}
			  	case 1: {status_icon.setVisibility(View.VISIBLE);break;}
			  	}
			  }
			  String helper=get_file_at(position);
			  textView.setText(helper);
			  
			  RelativeLayout rl=(RelativeLayout)rowView.findViewById(R.id.s_content_item_main);
			    rl.setOnTouchListener(new TouchListener());
			    rl.setId(position);
			    
			  String extension = "";

			  /*Set icon regarding file extension*/
			  int i = helper.lastIndexOf('.');
			  if (i >= 0) {
			      extension = helper.substring(i+1);
			  }
			  if(extension.equals("jpg")||extension.equals("jpeg")||extension.equals("png")
					  ||extension.equals("icon")||extension.equals("JPG")){
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
	
	  private void slide_out_right(View v){
		  v.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_out_right));
		  v.setBackgroundColor(Color.GRAY);
		  this.item_sent_status[v.getId()]=true;
		  Executors.newCachedThreadPool().execute(
				  new SendingFilesThread(parent,new String[]{this.get_file_at(v.getId())}));
		  Log.i("SLIDED POSITION",""+v.getId()+get_file_at(v.getId()));
		  this.notifyDataSetChanged();
		 
	  }
	  
	  
	  private void slide_out_left(View v){
		  v.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_out_left));
		  v.setBackgroundColor(Color.GRAY);
		  this.item_sent_status[v.getId()]=true;
		  Executors.newCachedThreadPool().execute(
				  new SendingFilesThread(parent,new String[]{this.get_file_at(v.getId())}));
		  Log.i("SLIDED POSITION",""+v.getId()+get_file_at(v.getId()));
		  this.notifyDataSetChanged();
		  
	  }
	
	class TouchListener implements View.OnTouchListener{

		 float init_position=0f;
		 boolean progress=false;
		 
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			/*if touched item is folder go to OnclickListener*/
			Log.i("OTL","index: "+v.getId()+", ifs: "+item_is_folder[v.getId()]);
			if(item_is_folder[v.getId()]){
				v.performClick();//Added to avoid Warning
				return false;
			}
			
			float curent_pos=event.getX();
			
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				
				if(init_position==0f){init_position=curent_pos;return true;}
				
			//}else if(event.getAction()==MotionEvent.ACTION_OUTSIDE || event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_SCROLL){
			}else if(true){
				Log.i("OTL","UP");
				if(init_position!=0f){
					float delta=Math.abs(curent_pos)-Math.abs(init_position);
					if(delta>10){
						
						slide_out_right(v);
						init_position=0f;
						progress=false;
						return false;
					}
						
					if(delta<-10){
						slide_out_left(v);
						init_position=0f;
						progress=false;
						return false;}
					
				}//init!=0
				
			}//action up
			return false;
		}
	  }//OTL
}//SFAdapter
