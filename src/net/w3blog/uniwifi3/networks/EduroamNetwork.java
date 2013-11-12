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
package net.w3blog.uniwifi3.networks;

import net.w3blog.uniwifi3.util.SchoolNetwork;
import android.net.wifi.WifiConfiguration;

public class EduroamNetwork extends SchoolNetwork {

	@Override
	public void init(String username, String password) {
		String SSID = "\"eduroam\"";

		setSSID(SSID);

		setStatus(WifiConfiguration.Status.DISABLED);

		setPriority(40);

		setHiddenSSID(false);

		setKeyManagement(new int[] { WifiConfiguration.KeyMgmt.IEEE8021X });

		clearGroupCiphers();

		clearPairwiseCiphers();
		setPairwiseCiphers(new int[] { WifiConfiguration.PairwiseCipher.TKIP,
				WifiConfiguration.PairwiseCipher.CCMP });

		setProtocols(new int[] { WifiConfiguration.Protocol.RSN,
				WifiConfiguration.Protocol.WPA });

		setEap("TTLS");
		setPhase2("auth=PAP");
		setIdentity(username);
		setPassword(password);

	}

}
