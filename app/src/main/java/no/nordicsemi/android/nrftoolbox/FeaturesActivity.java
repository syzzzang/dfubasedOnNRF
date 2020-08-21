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

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import no.nordicsemi.android.nrftoolbox.dfu.DfuActivity;

public class FeaturesActivity extends AppCompatActivity {
	private static final int SELECT_FILE_REQ = 1;
	private static final int SELECT_INIT_FILE_REQ = 2;
	TextView tv;
	private DownloadManager downloadManager;
	private DownloadManager.Request request;
	private long latestId = -1;

	private BroadcastReceiver completeReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show();
		}

	};


  	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_features);
		tv=findViewById(R.id.tv);
		downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

		Button btn=findViewById(R.id.btn);
		btn.setOnClickListener(v -> {
			//Intent intent=new Intent(getApplicationContext(),DfuActivity.class);
			//startActivity(intent);
//				String path = "android.resource://" + getPackageName() + "/" + R.raw.fw_v108;
//				Log.d("rawpath",path);
//				Uri uri=Uri.parse(path);
//				String p=uri.getPath();
//				File file=new File(p);
//			 	tv.setText(file.getName());
			downloadFileFromRawFolder();
		});

 		// ensure that Bluetooth exists
		if (!ensureBLEExists())
			finish();

	}
	@Override
	public void onResume(){
		super.onResume();
		IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(completeReceiver, completeFilter);
	}


 	private void downloadImage() {
		Uri uri = Uri.parse("https://drive.google.com/file/d/1skgn1smAVO0PJfOpDBuYU4SzgKi_9JXe/view?usp=sharing");
		List<String> pathSegments = uri.getPathSegments();
		request = new DownloadManager.Request(uri);
		request.setTitle("다운로드 예제");
		request.setDescription("항목 설명");
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size()-1));
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
		latestId = downloadManager.enqueue(request);


 	}
	private void copyFiletoExternalStorage(int resourceId, String resourceName){
		String pathSDCard = Environment.getExternalStorageDirectory() + "/Android/data/" + resourceName;
		try{
			InputStream in = getResources().openRawResource(resourceId);
			FileOutputStream out = null;
			out = new FileOutputStream(pathSDCard);
			byte[] buff = new byte[1024];
			int read = 0;
			try {
				while ((read = in.read(buff)) > 0) {
					out.write(buff, 0, read);
				}
			} finally {
				in.close();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void saveResourceToFile() throws IOException {
		InputStream in = null;
		FileOutputStream fout = null;
		try {
			in = getResources().openRawResource(R.raw.fw_v108);
			String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			String filename = "myfile.zip";
			fout = new FileOutputStream(new File(downloadsDirectoryPath + filename));

			final byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
	}

	private File checkFolder() {
		String path;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			path = getExternalFilesDir(null).getAbsolutePath() + "FOLDER_NAME";
		} else {
			path = Environment.getExternalStorageDirectory() + "FOLDER_NAME";
		}
		File dir = new File(path);
		boolean isDirectoryCreated = dir.exists();
		if (!isDirectoryCreated) {
			isDirectoryCreated = dir.mkdir();
			Log.d("Folder", "Created = " + isDirectoryCreated);
		}

		Log.d("Folder", "Created ? " + isDirectoryCreated);
		return dir;
	}
	private void downloadFileFromRawFolder() {
		try {
			InputStream in = getResources().openRawResource(
					getResources().getIdentifier("fw_v108",
							"raw", getPackageName())); // use only file name here, don't use extension
			File fileWithinMyDir = new File(checkFolder(), "fw_v108.zip"); //Getting a file within the dir.
			Log.e("FILEPATH ", "fileWithinMyDir " + fileWithinMyDir);
			FileOutputStream out = new FileOutputStream(fileWithinMyDir);
			byte[] buff = new byte[1024 * 1024 * 4]; //2MB file
			int read = 0;

			try {
				while ((read = in.read(buff)) > 0) {
					out.write(buff, 0, read);
				}
			} finally {
				in.close();
				out.close();
			}
			Log.d("h", "Download Done ");
		} catch (IOException e) {
			Log.e("h", "Download Failed " + e.getMessage());
			e.printStackTrace();
		}
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
