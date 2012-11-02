/*
 * This file is part of BUwifi.
 *
 *  BUwifi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BUwifi is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BUwifi.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.w3blog.uniwifi.activity;

import net.w3blog.uniwifi.R;
import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help);
	}

}
