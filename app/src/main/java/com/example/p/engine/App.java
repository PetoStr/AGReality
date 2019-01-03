package com.example.p.engine;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

public class App extends Application {

	private static Application sApplication;

	public static Application getApplication() {
		return sApplication;
	}

	public static Context getContext() {
		return getApplication().getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sApplication = this;
		if (LeakCanary.isInAnalyzerProcess(this)) {
			return;
		}
		LeakCanary.install(this);
	}

}
