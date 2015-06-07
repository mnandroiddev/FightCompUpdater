package com.fight.comp.updater.ws;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class Servant extends Service {
	static int NOTIFY_ID = 199191;
	String[] jpgs;
	String[] links;
	String[] titles;
	String sizes = "";
	static final String url = "http://www.worldstarhiphop.com/videos/search.php?s=FIGHT+COMP";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		boolean visible = intent.getExtras().getBoolean("visible");
		new TheTask(getApplicationContext(), visible);
		return START_NOT_STICKY;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	@Override
	public IBinder onBind(Intent intent) {
		// BOOHOO
		return null;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////

	class TheTask extends AsyncTask<String, String, String> {
		boolean error = false;
		String LINK;
		String TITLE;
		Context context;
		boolean visible;

		public TheTask(Context ctxt, boolean visible) {
			super();
			context = ctxt;
			this.visible = visible;

			this.execute("");
		}

		// //////////////////////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////////////////////
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		// //////////////////////////////////////////////////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////////////////////

		@Override
		protected String doInBackground(String... params) {
			error = !isConnected();
			if (!error) {
				try {

					Document doc = Jsoup.connect(url).get();

					Elements linkElements = doc.getElementsByClass("video-box");
					Elements jpegElements = linkElements.select("img[src]");
					Elements titleElements = doc.getElementsByClass("title");

					int size = 1;// linkElements.size()
					links = new String[size];
					jpgs = new String[size];
					titles = new String[size];

					// for (int i = 0; i < linkElements.size(); i++) {
					int i = 0;
					links[i] = linkElements.get(i).attr("href");
					jpgs[i] = jpegElements.get(i).attr("src");
					titles[i] = titleElements.get(i).text();
					// }

				} catch (Exception e) {
					error = true;
				}
			}
			return "";

		}

		// //////////////////////////////////////////////////////////////////////////////////////////////////

		private boolean isConnected() {

			ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo n = manager.getActiveNetworkInfo();

			if (n != null) {
				if (n.isConnectedOrConnecting()) {
					return true;
				}
			}
			return false;
		}

		// //////////////////////////////////////////////////////////////////////////////////////////////////

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);

			if ((error || links.length == 0 || links == null)) {
			
				Intent intent = new Intent("allDoneChangeText");
				intent.putExtra("text", "Trouble connecting...");
				context.sendBroadcast(intent);
			}else{

				LINK = links[0].substring(20);// 7
				// TITLE = titles[0];

				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				String toCompare = sharedPreferences.getString("link", "");

				// if new video or no entry in shared preferences
				if ((!toCompare.equalsIgnoreCase(LINK)) || (toCompare == "")) {

					// String realLink = getRealLink(LINK);

					Editor ed = sharedPreferences.edit();
					ed.putString("link", LINK);
					// ed.putString("title", TITLE);
					ed.commit();

					// if called directly by user
					if (this.visible) { // negate for immediate launch of
										// notification
						MediaPlayer mp = MediaPlayer.create(context, R.raw.ws);
						mp.start();
						Intent launchNow = new Intent(Intent.ACTION_VIEW).setData(Uri
								.parse("http://www.worldstarhiphop.com/videos/e/16711680/" + LINK));
						// .parse("http://m.worldstarhiphop.com/android" +
						// LINK));
						// .parse("http://www.worldstarhiphop.com" + LINK));
						launchNow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(launchNow);

						// if called by the alarm manager
					} else {

						NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
						Intent toLaunch = new Intent(Intent.ACTION_VIEW).setData(Uri
								.parse("http://www.worldstarhiphop.com/videos/e/16711680/" + LINK));
						// .parse("http://m.worldstarhiphop.com/android" +
						// LINK));
						// .parse("http://www.worldstarhiphop.com" + LINK));
						PendingIntent pending = PendingIntent.getActivity(context, 0, toLaunch, 0);
						builder.setAutoCancel(true).setContentIntent(pending)
								.setContentTitle("New WorldStar").setContentText(TITLE)
								.setSmallIcon(R.drawable.ic_stat_icon).setTicker(TITLE)
								.setWhen(System.currentTimeMillis());
						NotificationManager mgr = ((NotificationManager) context
								.getSystemService(Context.NOTIFICATION_SERVICE));

						mgr.notify(NOTIFY_ID, builder.build());

					}

					// no new video
				} else {
					Intent intent = new Intent("allDoneChangeText");
					intent.putExtra("text", "No new Videos...");
					context.sendBroadcast(intent);

				}
			

			}
			stopSelf();
			// //////////////////////////////////////////////////////////////////////////////////////////////////
			// //////////////////////////////////////////////////////////////////////////////////////////////////

		}

	}
}
