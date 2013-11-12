package net.w3blog.uniwifi3.util;

import java.security.cert.X509Certificate;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiEnterpriseConfig.Eap;

/**
 * TODO setClientCertificate() method should be implemented
 */
public class JellyBeanNetworkConfig extends NetworkConfig {
	protected WifiEnterpriseConfig enterpriseConfig;

	public JellyBeanNetworkConfig(WifiConfiguration config) {
		super();
		this.config = config;
		this.enterpriseConfig = this.config.enterpriseConfig;
	}

	@Override
	public void setEap(Object o) {
		String eapMethod = (String) o;
		if (eapMethod.equals("PEAP"))
			enterpriseConfig.setEapMethod(Eap.PEAP);
		else if (eapMethod.equals("PWD"))
			enterpriseConfig.setEapMethod(Eap.PWD);
		else if (eapMethod.equals("TLS"))
			enterpriseConfig.setEapMethod(Eap.TLS);
		else if (eapMethod.equals("TTLS"))
			enterpriseConfig.setEapMethod(Eap.TTLS);
		else
			enterpriseConfig.setEapMethod(Eap.NONE);
	}

	@Override
	public void setAnonymousID(Object o) {
		enterpriseConfig.setAnonymousIdentity((String) o);
	}

	@Override
	public void setCaCertificate(Object o) {
		enterpriseConfig.setCaCertificate((X509Certificate) o);
	}

	@Override
	public void setPhase2(Object o) {
		String phase2 = (String) o;
		if (phase2.equals("PAP"))
			enterpriseConfig.setPhase2Method(Phase2.PAP);
		else if (phase2.equals("MSCHAP"))
			enterpriseConfig.setPhase2Method(Phase2.MSCHAP);
		else if (phase2.equals("MSCHAPV2"))
			enterpriseConfig.setPhase2Method(Phase2.MSCHAPV2);
		else if (phase2.equals("GTC"))
			enterpriseConfig.setPhase2Method(Phase2.GTC);
		else
			enterpriseConfig.setPhase2Method(Phase2.NONE);
	}

	@Override
	public void setIdentity(Object o) {
		enterpriseConfig.setIdentity((String) o);
	}

	@Override
	public void setPassword(Object o) {
		enterpriseConfig.setPassword((String) o);
	}

}
