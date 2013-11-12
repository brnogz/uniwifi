/*
 * This file is part of UNIwifi.
 *
 *  UNIwifi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  UNIwifi is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with UNIwifi.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.w3blog.uniwifi3.activity;

import java.util.List;

import net.w3blog.uniwifi3.R;
import net.w3blog.uniwifi3.networks.BAUStaffNetwork;
import net.w3blog.uniwifi3.networks.BAUStudentNetwork;
import net.w3blog.uniwifi3.networks.EduroamNetwork;
import net.w3blog.uniwifi3.networks.YTUStudentNetwork;
import net.w3blog.uniwifi3.util.NetworkConfig;
import net.w3blog.uniwifi3.util.Preferences;
import net.w3blog.uniwifi3.util.SchoolNetwork;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
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
	private Button BTNhelp;
	private Button BTNabout;
	private Button BTNfeedback;

	private final String[] networks = { "\"10\"", "\"20\"", "\"yildiz-net\"",
			"\"Eduroam\"" };
	private final int BAU_STAFF = 0;
	private final int BAU_STUDENT = 1;
	private final int YTU_STUDENT = 2;
	private final int EDUROAM = 3;

	private WifiManager wifiManager;
	private Preferences prefs;

	private String username;
	private String password;
	private int network;
	private boolean sendData;

	private int SSID;

	private boolean isFirstRunEver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.prefs = new Preferences(getApplicationContext());
		isFirstRunEver = prefs.isFirstRunEver();

		this.setContentView(R.layout.main);
		this.initializeViews();
		this.initializeButtons();

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled()) {
			this.wifiNotEnabledDiaglod(); // comment this line to emulator
			// layout testing
		} else if (NetworkConfig.isEnterpriseReachable(this)) {
			this.enterpriseNotReachableDialog();
		}

		if (isFirstRunEver) {
			prefs.setFirstRunEver();
			prefs.setFirstRunForVersion();
		}

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
		BTNhelp = (Button) this.findViewById(R.id.btnHelp);
		BTNabout = (Button) this.findViewById(R.id.btnAbout);
		BTNfeedback = (Button) this.findViewById(R.id.btnFeedback);
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

		BTNhelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UNIwifiActivity.this,
						HelpActivity.class);
				startActivity(intent);
			}
		});

		BTNabout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UNIwifiActivity.this,
						AboutActivity.class);
				startActivity(intent);
			}
		});

		BTNfeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final boolean connected = haveNetworkConnection();
				if (connected) {
					Intent intent = new Intent(UNIwifiActivity.this,
							ContactActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(
							UNIwifiActivity.this,
							UNIwifiActivity.this
									.getString(R.string.warning_no_network),
							Toast.LENGTH_SHORT).show();
				}

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

		SSID = network == Spinner.INVALID_POSITION ? 0 : network;
		// wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * tries to create a network. if a network with given SSID is exist, then
	 * removes it and tries to add it.
	 */
	private void createOrReplaceNetwork() {
		this.initializeVariables();
		if (username.equals("") || password.equals("")) {
			Toast.makeText(this, R.string.warning_blank, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		prefs.setUsername(username);
		prefs.setPassword(password);
		prefs.setNetwork(network);
		prefs.setSendData(sendData);

		List<WifiConfiguration> wirelessList = wifiManager
				.getConfiguredNetworks();

		for (WifiConfiguration wifiConfig : wirelessList) {
			if (wifiConfig.SSID.equals(networks[SSID])) {
				wifiManager.removeNetwork(wifiConfig.networkId);
				Log.d("buwifi", "removed exist network");
			}
		}

		WifiConfiguration finalConfig = createConfiguration(SSID, username,
				password);

		finalConfig.networkId = wifiManager.addNetwork(finalConfig);

		if (finalConfig.networkId != -1)
			Toast.makeText(this, R.string.successfuly_added, Toast.LENGTH_SHORT)
					.show();
		else
			Log.d("buwifi", "network cannot be added");

		boolean successfuladdition = wifiManager.enableNetwork(
				finalConfig.networkId, false);

		boolean successfulsaving = wifiManager.saveConfiguration();
		Log.d("buwifi", "network is enabled : " + successfuladdition
				+ " configuration is saved : " + successfulsaving);
		if (successfulsaving)
			Toast.makeText(this, R.string.successfuly_saved, Toast.LENGTH_SHORT)
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
	private WifiConfiguration createConfiguration(int SSID, String username,
			String password) {
		SchoolNetwork netConfig = null;

		switch (SSID) {
		case BAU_STAFF:
			netConfig = new BAUStaffNetwork();
			break;
		case BAU_STUDENT:
			netConfig = new BAUStudentNetwork();
			break;
		case YTU_STUDENT:
			netConfig = new YTUStudentNetwork();
			break;
		case EDUROAM:
			netConfig = new EduroamNetwork();
			break;
		}

		netConfig.init(username, password);

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

}