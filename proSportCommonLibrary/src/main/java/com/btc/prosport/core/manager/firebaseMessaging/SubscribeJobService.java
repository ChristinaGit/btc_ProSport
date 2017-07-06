package com.btc.prosport.core.manager.firebaseMessaging;

import android.util.Log;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.firebase.jobdispatcher.JobParameters;

@Accessors(prefix = "_")
public class SubscribeJobService extends ProSportJobService {
    private static final String _LOG_TAG = ConstantBuilder.logTag(SubscribeJobService.class);

    public static final String TAG = SubscribeJobService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        getProSportApplicationComponent().inject(this);
    }

    @Override
    public int onRunJob(final JobParameters job) {
        val firebaseMessagingManager = getFirebaseMessagingManager();

        @JobResult int jobResult = RESULT_SUCCESS;

        if (firebaseMessagingManager != null) {
            try {
                firebaseMessagingManager.userSubscribe(true).toBlocking().subscribe();
            } catch (final Exception exception) {
                Log.w(_LOG_TAG, "Fail to subscribe on push notifications.", exception);

                jobResult = RESULT_FAIL_NORETRY;
            }
        }

        return jobResult;
    }
}
