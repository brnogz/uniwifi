package net.w3blog.uniwifi.networks;

import net.w3blog.uniwifi.util.NetworkConfig;

import android.net.wifi.WifiConfiguration;

public class YTUStudentNetwork extends NetworkConfig {
	final private String SSID = "\"yildiz-net\"";

	public YTUStudentNetwork(WifiConfiguration config,String username,String password) {
		super(config);
		
		setSSID(SSID);
		
		setStatus(WifiConfiguration.Status.DISABLED);
		
		setPriority(40);
		
		setHiddenSSID(false);
		
		setKeyManagement(new int[]{WifiConfiguration.KeyMgmt.IEEE8021X});
		
		clearGroupCiphers();
		
		clearPairwiseCiphers();
		setPairwiseCiphers(new int[]{WifiConfiguration.PairwiseCipher.TKIP,WifiConfiguration.PairwiseCipher.CCMP});
		
		setProtocols(new int[]{WifiConfiguration.Protocol.RSN,WifiConfiguration.Protocol.WPA});

		setEap("PEAP");
		setPhase2("auth=MSCHAPV2");
		setIdentity(username);
		setPassword(password);
	}

}
