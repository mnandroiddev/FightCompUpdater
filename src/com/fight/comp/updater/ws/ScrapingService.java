package com.fight.comp.updater.ws;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class ScrapingService extends Service {
	static int NOTIFY_ID = 199191;
	String[] jpgs;
	String[] links;
	String[] titles;
	String sizes = "";
	static final String url = "http://www.worldstarhiphop.com/videos/search.php?s=FIGHT+COMP";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		boolean visible = intent.getExtras().getBoolean("visible");
		new ScrapingTask(getApplicationContext(), visible);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// BOOHOO
		return null;
	}

	class ScrapingTask extends AsyncTask<String, String, String> {
		boolean error = false;
		String LINK;
		String TITLE;
		Context context;
		boolean visible;

		public ScrapingTask(Context ctxt, boolean visible) {
			super();
			context = ctxt;
			this.visible = visible;

			this.execute("");
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			error = !isConnected();
			if (!error) {
				try {
					Document doc = Jsoup.connect(url).get();
					Elements linkElements = doc.getElementsByClass("video-box");
					Elements jpegElements = linkElements.select("img[src]");
					Elements titleElements = doc.getElementsByClass("title");

					int size = linkElements.size();
					links = new String[size];
					jpgs = new String[size];
					titles = new String[size];

					for (int i = 0; i < size; i++) {
						links[i] = linkElements.get(i).attr("href");
						jpgs[i] = jpegElements.get(i).attr("src");
						titles[i] = titleElements.get(i).text();
					}

				} catch (Exception e) {
					error = true;

				}
			}
			return "";

		}

		private String[] relativeLinksToEmbeddedLinks(String[] regularLinks) throws IOException {

			String[] embeddedLinks = new String[regularLinks.length];
			for (int i = 0; i < regularLinks.length; i++) {
				String link = "http://www.worldstarhiphop.com" + regularLinks[i];
				Document doc = Jsoup.connect(link).get();
				Element embedElement = doc.getElementsByClass("copy-embed").first();
				String html = embedElement.attr("data-text");
				int index = html.indexOf("embed/") + 6;
				String shortHtml = html.substring(index);
				embeddedLinks[i] = shortHtml.substring(0, shortHtml.indexOf("\""));

			}
			return embeddedLinks;

		}

		private boolean isConnected() {

			ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkManager = manager.getActiveNetworkInfo();

			if (networkManager != null) {
				if (networkManager.isConnectedOrConnecting()) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if ((error || links.length == 0 || links == null)) {
				sendClosingIntent("Trouble connecting...");
				stopSelf();
				return;
			}

			LINK = links[0];
			TITLE = titles[0];
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			String toCompare = sharedPreferences.getString("title", "");

			// if new video or no entry in shared preferences
			if ((!toCompare.equalsIgnoreCase(TITLE)) || (toCompare == "")) {
				updatePreferences(sharedPreferences, TITLE, LINK);// update stored link
				if (this.visible) {// if called directly by user
					launchActivity();
				} else {// if called by the alarm manager
					launchNotification();
				}
			} else {// no new video
				sendClosingIntent("No new Videos...");
			}
			stopSelf();
		}

		private void updatePreferences(SharedPreferences sharedPreferences, String title, String link) {
			Editor preferenceEditor = sharedPreferences.edit();
			preferenceEditor.putString("title", title);
			preferenceEditor.putString("link", link);
			preferenceEditor.commit();

		}

		private void launchActivity() {
			Intent launchNow = new Intent(Intent.ACTION_VIEW).setData(Uri
					.parse("http://www.worldstarhiphop.com" + LINK));
			launchNow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(launchNow);

		}

		private void sendClosingIntent(String text) {
			Intent intent = new Intent("allDoneChangeText");
			intent.putExtra("text", text);
			context.sendBroadcast(intent);
		}

		private void launchNotification() {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			Intent toLaunch = new Intent(Intent.ACTION_VIEW).setData(Uri
					.parse("http://www.worldstarhiphop.com/embed/" + LINK));
			PendingIntent pending = PendingIntent.getActivity(context, 0, toLaunch, 0);
			builder.setAutoCancel(true).setContentIntent(pending).setContentTitle("New WorldStar")
					.setContentText(TITLE).setSmallIcon(R.drawable.ic_stat_icon).setTicker(TITLE)
					.setWhen(System.currentTimeMillis());
			NotificationManager manager = ((NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE));

			manager.notify(NOTIFY_ID, builder.build());

		}

	}

}
