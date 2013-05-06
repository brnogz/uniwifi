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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.w3blog.uniwifi3.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactActivity extends Activity implements TextWatcher {
	private EditText txtEmail;
	private EditText txtTitle;
	private EditText txtMessage;
	private Button btnSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);
		init();
	}

	private void init() {
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtEmail.addTextChangedListener(this);

		txtTitle = (EditText) findViewById(R.id.txtTitle);
		txtTitle.addTextChangedListener(this);

		txtMessage = (EditText) findViewById(R.id.txtMessage);
		txtMessage.addTextChangedListener(this);

		btnSend = (Button) findViewById(R.id.btnSend);
	}

	public void sendMessage(View v) {
		final String email = txtEmail.getText().toString();
		final String title = txtTitle.getText().toString();
		final String message = txtMessage.getText().toString();
		final String id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);

		new AsyncTask<String, Void, Integer>() {
			@Override
			protected void onPostExecute(Integer result) {
				if (result == HttpStatus.SC_OK) {
					Toast.makeText(ContactActivity.this, R.string.send_sucess,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ContactActivity.this, R.string.send_fail,
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected Integer doInBackground(String... params) {
				final AndroidHttpClient httpClient = AndroidHttpClient
						.newInstance("Android");
				final HttpPost postRequest = new HttpPost(
						"http://droidpatterns.com/uniwifi/api.php?m=contact&f=post");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						4);
				nameValuePairs.add(new BasicNameValuePair("email", params[0]));
				nameValuePairs.add(new BasicNameValuePair("title", params[1]));
				nameValuePairs
						.add(new BasicNameValuePair("message", params[2]));
				nameValuePairs.add(new BasicNameValuePair("id", params[3]));

				try {
					postRequest.setEntity(new UrlEncodedFormEntity(
							nameValuePairs));

					HttpResponse response = httpClient.execute(postRequest);
					final int statusCode = response.getStatusLine()
							.getStatusCode();
					return statusCode;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

		}.execute(email, title, message, id);

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		if (txtEmail.getText().toString() != null
				&& !txtEmail.getText().toString().equals("")
				&& txtTitle.getText().toString() != null
				&& !txtTitle.getText().toString().equals("")
				&& txtMessage.getText().toString() != null
				&& !txtMessage.getText().toString().equals(""))
			btnSend.setEnabled(true);
		else
			btnSend.setEnabled(false);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}
}
