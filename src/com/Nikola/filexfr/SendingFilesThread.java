package com.Nikola.filexfr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.util.Log;

public class SendingFilesThread implements Runnable{
	
	public final String END_TRANSMISSION="END";//
	public final String SUCCESS="OK";
	
	Transfer parent;
	String[] query;
	
	String helperForError;
	
	Socket socket;
	InetSocketAddress sockAdress;
	PrintWriter pwriter;
	BufferedReader breader;
	
	SendingFilesThread(Transfer p, String[] q){
		try{
			parent=p;
			query=q;
			
			sockAdress=new  InetSocketAddress(parent.SERVER_IP_ADDRESS,parent.SENDING_DATA_PORT);
			Log.i("SFT constructor","DONE");
		}catch(Exception e){
			Log.e("SFT constructor error",e.toString());
			System.exit(1);
		}
		
	}

	
	@Override
	public void run() {
		try{
			
			for(String item:query){
				
				/*port init*/
				socket=new Socket();
				socket.connect(sockAdress);
				
				breader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream ostream=socket.getOutputStream();
				pwriter=new PrintWriter(ostream);
				helperForError=item;
				
				/*sending sequence*/
				pwriter.println(item);
				pwriter.flush();
				
				pwriter.println(END_TRANSMISSION);
				pwriter.flush();
				
				
				Log.i("SFTrun","item sended"+" "+item);
				
				if(!(breader.readLine()).equals(SUCCESS)){
					Log.e("SFT","TRANSMISSION file FAIL");
					
				}else{
					/*open file from location to send*/
					InputStream data = new FileInputStream(parent.mPath+"/"+item);
					
					byte[] buf = new byte[8192];
			        int len = 0;
			            
					while ((len = data.read(buf)) != -1) {
			            	
			            	ostream.write(buf, 0, len);
			            	ostream.flush();
			            }
						ostream.close();
						data.close();
					
				}//IFreceivedSUCCESS
				
				socket.close();
				
			}//forEACHfileINquery
			
		}
		catch(FileNotFoundException exc){
			Log.e("SFT","Cannot open requested file on location:\n"+parent.mPath+"/"+helperForError);
		}
		catch(Exception e){
			Log.e("SFT connection error, cannot send files to server",e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		
	}//run


}
