package com.fight.comp.updater.ws;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fight.comp.updater.ws.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class Servant extends Service {
static int	NOTIFY_ID = 199191;

	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		
		 boolean visible =intent.getExtras().getBoolean("visible");
	     new TheTask(getApplicationContext(),visible);	    
	     return START_NOT_STICKY;
	    }
	 
//////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	 @Override
		public IBinder onBind(Intent intent) {
			// BOOHOO
			return null;
		}	
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
	class TheTask extends AsyncTask<String,String, String> {
	
		String LINK;
		String TITLE;
		Context context;
		boolean visible;
			public TheTask(Context ctxt, boolean visible){
				super();
				context=ctxt;
				this.visible=visible;
				
				this.execute("");
			}
			
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

		@Override
		protected String doInBackground(String... params) {
		     try {
		            HttpClient httpclient = new DefaultHttpClient();
		            HttpPost method = new HttpPost("http://www.worldstarhiphop.com/videos/search.php");
		            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		            nameValuePairs.add(new BasicNameValuePair("SearchVideo", "fight comp"));
		            method.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		            HttpResponse response = httpclient.execute(method);
		            HttpEntity httpEntity = response.getEntity();
		             
		            if(httpEntity != null){
		            	
		            	String body=EntityUtils.toString(httpEntity);		            		                
		               
		            	String link=getCurrentVideoString(body);            	
		            	if(link==""){

		            		return "HTML Parsing Failure";
		            	}
		                return link;
		            }
		            else{
		                return "Null HTTP Entity";
		            }
		         }
		         catch(Exception e){
		             return e.toString();
		         }

		}
////////////////////////////////////////////////////////////////////////////////////////////////////		
////////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		protected void onPostExecute(String result) {
		
		   super.onPostExecute(result);
		   String[] data= result.split("a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a");
		
		  
		   if(data.length>=2){    //otherwise goto stopSelf because shit fucked up
			   LINK=data[0];
			   TITLE=data[1];
			 
		   
			
				
			
			SharedPreferences sharedPreferences = PreferenceManager
	                .getDefaultSharedPreferences(context);
			String toCompare=sharedPreferences.getString("link", "");
			
			//if new video or no entry in shared preferences
				if(     (!toCompare.equalsIgnoreCase(LINK))  ||    (toCompare=="")  )       {
					
					Editor ed =sharedPreferences.edit();
					ed.putString("link", LINK);
					ed.putString("title", TITLE);
					ed.commit();
					
					//if called directly by user
					if(this.visible){   //negate for immediate launch of notification
						MediaPlayer mp = MediaPlayer.create(context, R.raw.ws);
						mp.start();
						Intent launchNow = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.worldstarhiphop.com/videos/"+LINK));
						launchNow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(launchNow);
						
					//if called by the alarm manager
					}else{
					
					NotificationCompat.Builder builder = new NotificationCompat.Builder(
							context);													//"http://m.worldstarhiphop.com/android/videos/"+LINK
					Intent toLaunch = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.worldstarhiphop.com/videos/"+LINK));
					PendingIntent pending = PendingIntent.getActivity(context, 0, toLaunch, 0);
					builder.setAutoCancel(true).setContentIntent(pending)
							.setContentTitle("New WorldStar")
							.setContentText(TITLE)
							.setSmallIcon(R.drawable.ic_stat_icon)
							.setTicker(TITLE)
							.setWhen(System.currentTimeMillis());
					NotificationManager mgr = ((NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE));

					mgr.notify(NOTIFY_ID, builder.build());
					
					
					}
					
					//no new video
				}else{
					Intent intent = new Intent("allDoneChangeText");
					context.sendBroadcast(intent);
					
				}
		   }
		   
		   
		   Intent intent = new Intent("allDoneChangeText");
			context.sendBroadcast(intent);
			stopSelf();
		   
		}
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
		private String getCurrentVideoString(String body) {
			//returns either a link of form "video.php?v=wshhRKzaJ7Jfcz1m1u3r" or ""
			//get the wshh link from the page's html
		   //************************VERY FRAGILE*******************************//
			String toReturn="";
			String pattern = "<td valign=\"top\"><div align=\"center\"><a href=";
			int start=body.indexOf(pattern);
			if(start!=-1){
				boolean found = true;
				int end=2;
				String forCodepoint="\"";
				
				start+=pattern.length();
				body=body.substring(start);		
				
				for(int i=2;found;i++){
					end=i;
					found=!(body.codePointAt(i)==forCodepoint.codePointAt(0)); 
				}
				
				toReturn=body.substring(1, end);
				int newStart=body.indexOf("<strong>")+8;
				String titleBody=body.substring(newStart);
				String codePoint = "<";
				int newEnd =2;
				while(!(titleBody.codePointAt(newEnd)==codePoint.codePointAt(0))){
					newEnd++;
				}
				String title = titleBody.substring(0, newEnd);
				
				
				return toReturn + "a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a8a" + title ;		
			}else{
				return "";
			}	
			}

		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}
