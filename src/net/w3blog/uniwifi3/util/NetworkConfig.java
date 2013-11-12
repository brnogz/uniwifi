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

package net.w3blog.uniwifi3.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

public class NetworkConfig {

	protected WifiConfiguration config;

	@SuppressWarnings("rawtypes")
	private Class[] classes = WifiConfiguration.class.getClasses();
	@SuppressWarnings("rawtypes")
	private Class EnterpriseField = null;
	public boolean noEnterprise = false;
	private Field[] fields = WifiConfiguration.class.getFields();
	private Method setValue = null;

	private Field fAnonymousId = null, fCaCertificate = null,
			fClientCertificate = null, fEap = null, fIdentity = null,
			fPassword = null, fPhase2 = null, fPrivateKey = null;
	
	public NetworkConfig(){
		
	}

	@SuppressWarnings("rawtypes")
	public NetworkConfig(WifiConfiguration config) {
		this.config = config;
		try {
			for (Class c : this.classes) {
				if (c.getName().equals(
						"android.net.wifi.WifiConfiguration$EnterpriseField")) {
					this.EnterpriseField = c;
					Log.d("buwifi", "EnterPriseField is found");
				}
				Log.d("uniwifi", "classes: " + c.getName());
			}

			if (EnterpriseField == null) {
				Log.d("buwifi", "EnterPriseField is null");
				this.noEnterprise = true;
			}

			for (Field f : this.fields) {
				if (config != null && f != null)
					Log.d("uniwifi", "fields: " + f.getName() + " value: "
							+ f.get(config));
				String fName = f.getName().trim();
				if (fName.equals("anonymous_identity"))
					this.fAnonymousId = f;
				else if (fName.equals("ca_cert"))
					this.fCaCertificate = f;
				else if (fName.equals("client_cert"))
					this.fClientCertificate = f;
				else if (fName.equals("eap"))
					this.fEap = f;
				else if (fName.equals("identity"))
					this.fIdentity = f;
				else if (fName.equals("password"))
					this.fPassword = f;
				else if (fName.equals("phase2"))
					this.fPhase2 = f;
				else if (fName.equals("private_key"))
					this.fPrivateKey = f;
			}

			if (!this.noEnterprise)
				for (Method m : this.EnterpriseField.getMethods()) {
					if (m.getName().equals("setValue")) {
						Log.d("buwifi", "setValue() method is found");
						this.setValue = m;
					}

					Log.d("uniwifi", "methods: " + m.getName());
				}
		} catch (IllegalArgumentException e) {
			Log.e("uniwifi",
					"NetworkConfig(): IllegalArgumentException "
							+ e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e("uniwifi",
					"NetworkConfig(): IllegalAccessException " + e.getMessage());
		}
	}

	public void setStatus(int status) {
		this.config.status = status;
	}

	public void setPriority(int priority) {
		this.config.priority = priority;
	}

	public void setSSID(String v) {
		this.config.SSID = v;
	}

	public void setBSSID(String v) {
		this.config.BSSID = v;
	}

	public void setHiddenSSID(boolean v) {
		this.config.hiddenSSID = v;
	}

	public void clearKeyManagement() {
		this.config.allowedKeyManagement.clear();
	}

	private void setKeyManagemet(int v) {
		this.config.allowedKeyManagement.set(v);
	}

	public void setKeyManagement(int[] v) {
		for (int i : v)
			this.setKeyManagemet(i);
	}

	public void clearGroupCiphers() {
		this.config.allowedGroupCiphers.clear();
	}

	public void clearPairwiseCiphers() {
		this.config.allowedPairwiseCiphers.clear();
	}

	private void setPairwiseCiphers(int v) {
		this.config.allowedPairwiseCiphers.set(v);
	}

	public void setPairwiseCiphers(int[] v) {
		for (int i : v)
			this.setPairwiseCiphers(i);
	}

	public void clearAuthAlgorithm() {
		this.config.allowedAuthAlgorithms.clear();
	}

	public void clearProtocols() {
		this.config.allowedProtocols.clear();
	}

	private void setProtocols(int v) {
		this.config.allowedProtocols.set(v);
	}

	public void setProtocols(int[] v) {
		for (int i : v)
			this.setProtocols(i);
	}

	public void setPreSharedKey(String v) {
		this.config.preSharedKey = v;
	}

	public void setEap(Object o) {
		this.setEnterpriseField(this.fEap, o);
	}

	public void setAnonymousID(Object o) {
		this.setEnterpriseField(this.fAnonymousId, o);
	}

	public void setCaCertificate(Object o) {
		this.setEnterpriseField(this.fCaCertificate, o);
	}

	public void setClientCertificate(Object o) {
		this.setEnterpriseField(this.fClientCertificate, o);
	}

	public void setPhase2(Object o) {
		this.setEnterpriseField(this.fPhase2, o);
	}

	public void setPrivateKey(Object o) {
		this.setEnterpriseField(this.fPrivateKey, o);
	}

	public void setIdentity(Object o) {
		this.setEnterpriseField(this.fIdentity, o);
	}

	public void setPassword(Object o) {
		this.setEnterpriseField(this.fPassword, o);
	}

	private void setEnterpriseField(Field f, Object o) {
		try {
			if (this.noEnterprise)
				f.set(this.config, o);
			else
				this.setValue.invoke(f.get(this.config), o);
		} catch (Exception e) {
			Log.e("uniwifi", "setEnterpriseField(): " + e.getMessage());
		}
	}

	public WifiConfiguration getConfiguration() {
		return this.config;
	}

	public static boolean isEnterpriseReachable(Context c) {
		if(Build.VERSION.SDK_INT >= 18)
			return false;
		
		WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> wirelessList = wifiManager
			.getConfiguredNetworks();
		WifiConfiguration wifi = new WifiConfiguration();
		for (WifiConfiguration wifiConfig : wirelessList) {
				if (wifiConfig.SSID.equals("\"20\"")) {
					wifi = wifiConfig;
				}
			}
		return (new NetworkConfig(wifi)).noEnterprise;
	}
}
