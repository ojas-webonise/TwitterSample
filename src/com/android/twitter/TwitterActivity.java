package com.android.twitter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.twitter.android.TwitterApp;
import com.twitter.android.TwitterApp.TwDialogListener;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class TwitterActivity extends Activity implements OnClickListener {
	
	private TwitterApp mTwitter;
	private Button mBtnTwitter;
	
	//	private static final String CONSUMER_KEY = "EevD9tRO1GB3eryMq183g";
	//	private static final String CONSUMER_SECRET = "gI8R1gt27ryMJZLSHl5CLi2SKncJyxkqlKCSzPyw8";
	
	//	private static final String CONSUMER_KEY = "hzTDzBafeCsWj2BFIM0doQ";
	//	private static final String CONSUMER_SECRET = "2WDmWaAFcyeZwlFo1HXkV8V8fvU6jsaSz6JDhy5cYQw";

	// For Sample_Demo_Application
	 private static final String CONSUMER_KEY = "1ozjDiHOqJq4UkowfvZIA";
	 private static final String CONSUMER_SECRET = "FPToH6HrOZa5TttS0dSGjgPsNIHYCgCJA5uki8Bi00";

	private enum FROM {
		TWITTER_POST, TWITTER_LOGIN
	};

	private enum MESSAGE {
		SUCCESS, DUPLICATE, FAILED, CANCELLED
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mTwitter = new TwitterApp(this, CONSUMER_KEY, CONSUMER_SECRET);
		mBtnTwitter = (Button) findViewById(R.id.mBtnTwitter);
		mBtnTwitter.setOnClickListener(this);
	}

	public void onClick(View v) {
		mTwitter.setListener(mTwLoginDialogListener);
		mTwitter.resetAccessToken();
		if (mTwitter.hasAccessToken() == true) {
			try {
//				mTwitter.updateStatus(String.valueOf(Html.fromHtml(TwitterApp.MESSAGE)));

				// File f = new File("/mnt/sdcard/android.jpg");
				// mTwitter.uploadPic(f, String.valueOf(Html
				// .fromHtml(TwitterApp.MESSAGE)));
				
				postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
				}
				e.printStackTrace();
			}
			mTwitter.resetAccessToken();
		} else {
			mTwitter.authorize();
		}
	}

	private void postAsToast(FROM twitterPost, MESSAGE success) {
		switch (twitterPost) {
		case TWITTER_LOGIN:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG)
						.show();
				break;
			case FAILED:
				Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG)
						.show();
				break;
			case FAILED:
				Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG)
						.show();
				break;
			case DUPLICATE:
				Toast.makeText(this,
						"Posting Failed because of duplicate message...",
						Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		}
	}

	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onError(String value) {
			postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);
			Log.e("TWITTER", value);
			mTwitter.resetAccessToken();
		}

		public void onComplete(String value) {
			try {
				mTwitter.updateStatus(TwitterApp.MESSAGE);
				postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
				}
				e.printStackTrace();
			}
			mTwitter.resetAccessToken();
		}
	};
}