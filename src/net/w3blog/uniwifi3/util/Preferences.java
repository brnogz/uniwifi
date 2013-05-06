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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

public class Preferences {
	private Context context;
	private SharedPreferences prefs;
	private Editor editor;
	
	private final String DEFAULT_APP_KEY = "buwifi";
	
	private final String KEY_FIRST_RUN_VERSION = "versionFirst";
	private final String KEY_FIRST_RUN_EVER = "everFirst";
	private final String KEY_USERNAME = "username";
	private final String KEY_PASSWORD = "password";
	private final String KEY_NETWORK = "network";
	private final String KEY_SEND_DATA = "sendData";
	
	public Preferences(Context context){
		this.context = context;
		this.prefs = context.getSharedPreferences(DEFAULT_APP_KEY, 0);
		this.editor = prefs.edit();
	}

	public boolean isFirstRunEver(){
		return prefs.getBoolean(KEY_FIRST_RUN_EVER, true);
	}
	
	public void setFirstRunEver(){
		editor.putBoolean(KEY_FIRST_RUN_EVER, false).commit();
	}
	
	public boolean isFirstRunForVersion(){
		int oldVersion = prefs.getInt(KEY_FIRST_RUN_VERSION, 1);
		int currentVersion = oldVersion;
		
		try {
			currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return currentVersion>oldVersion;
	}
	
	public void setFirstRunForVersion(){
		try {
			editor.putInt(KEY_FIRST_RUN_VERSION, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode).commit();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername(){
		return prefs.getString(KEY_USERNAME, "");
	}
	
	public void setUsername(String username){
		editor.putString(KEY_USERNAME, username).commit();
	}

	public String getPassword(){
		return prefs.getString(KEY_PASSWORD, "");
	}
	
	public void setPassword(String password){
		editor.putString(KEY_PASSWORD, password).commit();
	}
	
	public int getNetwork(){
		return prefs.getInt(KEY_NETWORK, 0);
	}
	
	public void setNetwork(int network){
		editor.putInt(KEY_NETWORK, network).commit();
	}
	
	public boolean isSendData(){
		return prefs.getBoolean(KEY_SEND_DATA, false);
	}
	
	public void setSendData(boolean isSend){
		editor.putBoolean(KEY_SEND_DATA, isSend).commit();
	}
}