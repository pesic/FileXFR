package com.Nikola.filexfr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


/*
 * Link paremeters
 * 1) phase of transmission
 * 2) server IP address
 * 3) server listening port  
 */


public class Link extends AsyncTask<String,Void,String> {

	Transfer parent;
	StringBuffer data;
	Socket socket;
	
	BufferedReader in;
	InputStream istream;
	PrintStream pstream;
	String filename;
	final int server_timeout=1500;
	
	public Link(Transfer p){
		super();
		parent=p;//used for accessing Views 
		
	}
	
	public void set_filename(String s){
		filename=s;
	}
	
	private String read_content(){
		
		try{
			
			String line=null;
			
			StringBuilder content=new StringBuilder();
			while((line=in.readLine())!=null){
				Log.i("DATA COMMING",line);
				content.append(line+"\n");
				
			}
			
			return content.toString();
		}catch(Exception e){
			Log.e("read_content",e.toString());
			return null;
		}
		
	}
	
	
	private String read_data(String s){
		
		try{
			
			File fil=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename);
		
			FileOutputStream fi= new FileOutputStream(fil);
		 
			Log.i("LINK RECV FILE",fi.toString());
		
		byte[] buff = new byte[8192];
		int le = 0;
		
		parent.runOnUiThread(new Runnable(){public void run(){parent.set_text("copping...");}});
		
		while (((le = istream.read(buff)) != -1)) {
			
			fi.write(buff, 0, le);
			fi.flush();
			
			
       }
		fi.close();
		
		parent.runOnUiThread(new Runnable(){public void run(){parent.set_text("Transfer complete!");}});
		 
		return "OK";//File received and stored in memory
		
		}catch(Exception e){
			Log.e("receive_data",e.toString());
			e.printStackTrace();
			return "null";
		}
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		
		try{
			
			socket=new Socket(parent.SERVER_IP_ADDRESS,parent.RECEIVING_DATA_PORT);
			socket.setSoTimeout(server_timeout);
			int phase=Integer.parseInt(params[0]);
			
			Log.i("XFR","socket created");
			
			pstream =new PrintStream(socket.getOutputStream(), true);
			istream=socket.getInputStream();
			in =new BufferedReader(new InputStreamReader(istream));
	
			pstream.println(params[0]);
			
			if(phase==0)  return read_content();
				else return read_data(params[0]);
			

		}catch(ConnectException ce){
			Log.e("Transfer","No response from server"+ce.toString());
			ce.printStackTrace();		
			return "SERVER ERROR";
		}
		catch(Exception e){
			Log.e("Transfer","Cannot create socket"+e.toString());
			e.printStackTrace();		
			return null;
		}
		
		
	}
	
	
	

}
