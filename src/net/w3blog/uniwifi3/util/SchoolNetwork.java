package net.w3blog.uniwifi3.util;

import android.net.wifi.WifiConfiguration;

public abstract class SchoolNetwork extends JellyBeanNetworkConfig{
	public SchoolNetwork() {
		super(new WifiConfiguration());
	}

	public abstract void init(String username, String password);
}
