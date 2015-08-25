/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.library;

import java.io.*;

import android.app.Application;
import android.content.res.Resources;
import android.content.res.AssetFileDescriptor;
import android.content.Intent;
import android.net.Uri;

import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.filesystem.ZLResourceFile;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import org.geometerplus.zlibrary.ui.android.R;

public final class ZLAndroidLibrary extends ZLibrary {
	private final Application myApplication;

	ZLAndroidLibrary(Application application) {
		myApplication = application;
	}

	public void openInBrowser(String reference) {
	}

	@Override
	public ZLResourceFile createResourceFile(String path) {
		return new AndroidResourceFile(path);
	}

	@Override
	public String getVersionName() {
		try {
			return myApplication.getPackageManager().getPackageInfo(myApplication.getPackageName(), 0).versionName;
		} catch (Exception e) {
			return "";
		}
	}

	private final class AndroidResourceFile extends ZLResourceFile {
		private boolean myExists;
		private int myResourceId;

		AndroidResourceFile(String path) {
			super(path);
			final String drawablePrefix = "R.drawable.";
			try {
				if (path.startsWith(drawablePrefix)) {
					final String fieldName = path.substring(drawablePrefix.length());
					myResourceId = R.drawable.class.getField(fieldName).getInt(null);
				} else {
					final String fieldName = path.replace("/", "__").replace(".", "_").replace("-", "_").toLowerCase();
					myResourceId = R.raw.class.getField(fieldName).getInt(null);
				}
				myExists = true;
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e) {
			}
		}

		@Override
		public boolean exists() {
			return myExists;
		}

		@Override
		public long size() {
			try {
				AssetFileDescriptor descriptor =
					myApplication.getResources().openRawResourceFd(myResourceId);
				long length = descriptor.getLength();
				descriptor.close();
				return length;
			} catch (IOException e) {
				return 0;
			} catch (Resources.NotFoundException e) {
				return 0;
			} 
		}

		@Override
		public InputStream getInputStream() throws IOException {
			if (!myExists) {
				throw new IOException("File not found: " + getPath());
			}
			try {
				return myApplication.getResources().openRawResource(myResourceId);
			} catch (Resources.NotFoundException e) {
				throw new IOException(e.getMessage());
			}
		}
	}
}
