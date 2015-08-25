package com.bookclub.epub;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		MyEpub bookModel = new MyEpub("/sdcard/c.epub");
	}
}
