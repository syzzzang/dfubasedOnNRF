/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.nordicsemi.android.nrftoolbox;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import no.nordicsemi.android.nrftoolbox.dfu.DfuActivity;

public class FeaturesActivity extends AppCompatActivity {
	private static final int SELECT_FILE_REQ = 1;
	private static final int SELECT_INIT_FILE_REQ = 2;
	TextView tv;
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_features);
		tv=findViewById(R.id.tv);

		Button btn=findViewById(R.id.btn);
		btn.setOnClickListener(v -> {
			//Intent intent=new Intent(getApplicationContext(),DfuActivity.class);
			//startActivity(intent);
				String path = "android.resource://" + getPackageName() + "/" + R.raw.fw_v108;
				Log.d("rawpath",path);
				Uri uri=Uri.parse(path);
				String p=uri.getPath();
				File file=new File(p);
			 	tv.setText(file.getName());
		});

		// ensure that Bluetooth exists
		if (!ensureBLEExists())
			finish();

	}
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

				// and read new one
				final Uri uri = data.getData();
				/*
				 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
				 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
				 */
				if (uri.getScheme().equals("file")) {
					// the direct path to the file has been returned
					final String path = uri.getPath();
					final File file = new File(path);
					tv.setText(file.getName());
 				}

	}
	private boolean ensureBLEExists() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
