package net.w3blog.uniwifi.activity;

import java.util.List;

import net.w3blog.uniwifi.R;
import net.w3blog.uniwifi.networks.BAUStaffNetwork;
import net.w3blog.uniwifi.networks.BAUStudentNetwork;
import net.w3blog.uniwifi.networks.YTUStudentNetwork;
import net.w3blog.uniwifi.util.NetworkConfig;
import net.w3blog.uniwifi.util.Preferences;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UNIwifiActivity extends Activity {
	private EditText ETusername;
	private EditText ETpassword;
	private Spinner Snetwork;
	private Button BTNdone;

	private final String[] networks = { "\"10\"", "\"20\"", "\"yildiz-net\"" };
	private final int BAU_STAFF = 0;
	private final int BAU_STUDENT = 1;
	private final int YTU_STUDENT = 2;

	private WifiManager wifiManager;
	private Preferences prefs;

	private String username;
	private String password;
	private int network;
	
	private int SSID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.prefs = new Preferences(getApplicationContext());

		this.setContentView(R.layout.main);
		this.initializeViews();
		this.initializeButtons();

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled()) {
			this.wifiNotEnabledDiaglod();
		} else if (NetworkConfig.isEnterpriseReachable()) {
			this.enterpriseNotReachableDialog();
		}

		if (prefs.isFirstRunEver()) {
			this.welcomeDialog();
			prefs.setFirstRunEver();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean connected = haveNetworkConnection();
		Intent intent;

		switch (item.getItemId()) {
		case R.id.mnuHelp:
			intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			break;
		case R.id.mnuFeedBack:
			if (connected) {
				Uri uri = Uri
						.parse("http://code.google.com/p/buwifi/issues/entry");
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} else {
				Toast.makeText(this,
						this.getString(R.string.warning_no_network),
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.mnuAbout:
			this.AboutDialog();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * initializes views on the activity
	 */
	private void initializeViews() {
		ETusername = (EditText) this.findViewById(R.id.ETUsername);
		ETusername.setText(prefs.getUsername());

		ETpassword = (EditText) this.findViewById(R.id.ETPassword);
		ETpassword.setText(prefs.getPassword());

		Snetwork = (Spinner) this.findViewById(R.id.SNetwork);
		Snetwork.setSelection(prefs.getNetwork());
		
		BTNdone = (Button) this.findViewById(R.id.BTNDone);
	}

	/**
	 * initializes button events on the activity
	 */
	public void initializeButtons() {
		BTNdone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createOrReplaceNetwork();
			}
		});
	}

	/**
	 * initialize global variables
	 */
	private void initializeVariables() {
		username = ETusername.getText().toString().trim();
		password = ETpassword.getText().toString().trim();
		network = Snetwork.getSelectedItemPosition();
		
		SSID = network == Spinner.INVALID_POSITION ? 0
				: network;
		// wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * tries to create a network. if a network with given SSID is exist, then
	 * removes it and tries to add it.
	 */
	private void createOrReplaceNetwork() {
		this.initializeVariables();
		if (username.equals("") || password.equals("")) {
			Toast.makeText(this, R.string.warning_blank, Toast.LENGTH_LONG)
					.show();
			return;
		}

		prefs.setUsername(username);
		prefs.setPassword(password);
		prefs.setNetwork(network);

		List<WifiConfiguration> wirelessList = wifiManager
				.getConfiguredNetworks();

		for (WifiConfiguration wifiConfig : wirelessList) {
			if (wifiConfig.SSID.equals(networks[SSID])) {
				wifiManager.removeNetwork(wifiConfig.networkId);
				Log.d("buwifi", "removed exist network");
			}
		}

		WifiConfiguration finalConfig = CreateConfiguration(SSID, username,
				password);

		finalConfig.networkId = wifiManager.addNetwork(finalConfig);

		if (finalConfig.networkId != -1)
			Toast.makeText(this, R.string.successfuly_added, Toast.LENGTH_LONG)
					.show();
		else
			Log.d("buwifi", "network cannot be added");

		boolean successfuladdition = wifiManager.enableNetwork(
				finalConfig.networkId, false);

		boolean successfulsaving = wifiManager.saveConfiguration();
		Log.d("buwifi", "network is enabled : " + successfuladdition
				+ " configuration is saved : " + successfulsaving);
		if (successfulsaving)
			Toast.makeText(this, R.string.successfuly_saved, Toast.LENGTH_LONG)
					.show();

	}

	/**
	 * creates a network configuration with given variables and constant
	 * settings
	 * 
	 * @param String
	 *            SSID
	 * @param String
	 *            username
	 * @param String
	 *            password
	 * @return WifiConfiguration
	 */
	private WifiConfiguration CreateConfiguration(int SSID, String username,
			String password) {
		NetworkConfig netConfig = null;

		switch (SSID) {
		case BAU_STAFF:
			netConfig = new BAUStaffNetwork(new WifiConfiguration(), username,
					password);
			break;
		case BAU_STUDENT:
			netConfig = new BAUStudentNetwork(new WifiConfiguration(),
					username, password);
			break;
		case YTU_STUDENT:
			netConfig = new YTUStudentNetwork(new WifiConfiguration(),
					username, password);
			break;
		}

		return netConfig.getConfiguration();
	}

	/**
	 * @see http
	 *      ://stackoverflow.com/questions/4238921/android-detect-whether-there
	 *      -is-an-internet-connection-available/4239410#4239410
	 * @return boolean if have a connection returns true, otherwise returns
	 *         false
	 */
	private boolean haveNetworkConnection() {
		boolean HaveConnectedWifi = false;
		boolean HaveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					HaveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					HaveConnectedMobile = true;
		}
		return HaveConnectedWifi || HaveConnectedMobile;
	}

	private void wifiNotEnabledDiaglod() {
		final Activity ac = this;
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(getText(R.string.warning_not_enabled_wireless));
		ab.setPositiveButton(getText(R.string.enable),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						((UNIwifiActivity) ac).wifiManager.setWifiEnabled(true);
					}
				});
		ab.setNegativeButton(getText(R.string.close),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		ab.setCancelable(false);
		AlertDialog a = ab.create();
		a.show();
	}

	private void enterpriseNotReachableDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(getText(R.string.enterprise_msg));
		ab.setNegativeButton(getText(R.string.close),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		ab.setCancelable(false);
		AlertDialog a = ab.create();
		a.show();
	}

	private void welcomeDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(R.string.welcome_msg);
		ab.setPositiveButton(R.string.Okay, null);
		ab.setCancelable(true);
		AlertDialog a = ab.create();
		a.show();
	}

	private void AboutDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(R.string.about_msg);
		ab.setPositiveButton(R.string.Okay, null);
		AlertDialog a = ab.create();
		a.show();
	}
}