/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
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

package org.fbreader.common.android;

import java.io.*;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;

import org.fbreader.md.MDActivity;

import org.geometerplus.zlibrary.ui.android.error.BugReportActivity;
import org.geometerplus.android.fbreader.api.FBReaderIntents;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	protected final MDActivity Activity;

	public UncaughtExceptionHandler(MDActivity activity) {
		Activity = activity;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);

		Intent intent = new Intent(
			FBReaderIntents.Action.CRASH,
			new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
		);
		intent.setPackage(FBReaderIntents.DEFAULT_PACKAGE);
		try {
			Activity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			intent = new Intent(Activity, BugReportActivity.class);
			intent.putExtra(BugReportActivity.STACKTRACE, stackTrace.toString());
			Activity.startActivity(intent);
		}

		Activity.finish();

		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
