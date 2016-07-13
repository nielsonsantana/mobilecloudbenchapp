/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
//import android.net.SntpClient;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

/**
 *  
 * {@link TrustedTime} that connects with a remote NTP server as its trusted
 * time source.
 * 
 * Example of Usage:
 * 
 * NtpTrustedTime.setSNTPSettings("server-ip-or-domain", timeout-milliseconds);
 * final NtpTrustedTime ntp = NtpTrustedTime.getInstance(getApplicationContext());
 * ntp.currentTimeMillis();

 *
 * @hide
 */
public class NtpTrustedTime implements TrustedTime {
    private static final String TAG = "NtpTrustedTime";
    private static final boolean LOGD = false;

    private static NtpTrustedTime sSingleton;

    private final String mServer;
    private final long mTimeout;

    private boolean mHasCache;
    private long mCachedNtpTime;
    private long mCachedNtpElapsedRealtime;
    private long mCachedNtpCertainty;
    
    private static String defaultServer = null;
    private static long defaultTimeout = 0;

    private NtpTrustedTime(String server, long timeout) {
        if (LOGD) Log.d(TAG, "creating NtpTrustedTime using " + server);
        mServer = server;
        mTimeout = timeout;
    }

    public static synchronized NtpTrustedTime getInstance(Context context) {
        if (sSingleton == null) {
            final Resources res = context.getResources();
            final ContentResolver resolver = context.getContentResolver();
            

            if (defaultServer != null && defaultTimeout != 0){
            	sSingleton = new NtpTrustedTime(defaultServer, defaultTimeout);
            	sSingleton.forceRefresh();
            }
            else{
            	throw new IllegalStateException("Setup the server and timeout settings using setSNTPSettings method");
            }
            
        }

        return sSingleton;
    }
    public static void setSNTPSettings(String server, long timeout){
    	defaultServer = server;
    	defaultTimeout = timeout;
    }

    /** {@inheritDoc} */
    public boolean forceRefresh() {
        if (mServer == null) {
            // missing server, so no trusted time available
            return false;
        }

        if (LOGD) Log.d(TAG, "forceRefresh() from cache miss");
        final SntpClient client = new SntpClient();
        if (client.requestTime(mServer, (int) mTimeout)) {
            mHasCache = true;
            mCachedNtpTime = client.getNtpTime();
            mCachedNtpElapsedRealtime = client.getNtpTimeReference();
            mCachedNtpCertainty = client.getRoundTripTime() / 2;
            return true;
        } else {
            return false;
        }
    }

    /** {@inheritDoc} */
    public boolean hasCache() {
        return mHasCache;
    }

    /** {@inheritDoc} */
    public long getCacheAge() {
        if (mHasCache) {
            return SystemClock.elapsedRealtime() - mCachedNtpElapsedRealtime;
        } else {
            return Long.MAX_VALUE;
        }
    }

    /** {@inheritDoc} */
    public long getCacheCertainty() {
        if (mHasCache) {
            return mCachedNtpCertainty;
        } else {
            return Long.MAX_VALUE;
        }
    }

    /** {@inheritDoc} */
    public long currentTimeMillis() {
        if (!mHasCache) {
            throw new IllegalStateException("Missing authoritative time source");
        }
        if (LOGD) Log.d(TAG, "currentTimeMillis() cache hit");

        // current time is age after the last ntp cache; callers who
        // want fresh values will hit makeAuthoritative() first.
        return mCachedNtpTime + getCacheAge();
    }

    public long getCachedNtpTime() {
        if (LOGD) Log.d(TAG, "getCachedNtpTime() cache hit");
        return mCachedNtpTime;
    }

    public long getCachedNtpTimeReference() {
        return mCachedNtpElapsedRealtime;
    }
}
