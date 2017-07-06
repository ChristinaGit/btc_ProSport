package com.btc.prosport.core.manager.firebaseMessaging;

import android.util.Log;

import lombok.experimental.Accessors;
import lombok.val;

import com.btc.common.ConstantBuilder;
import com.firebase.jobdispatcher.JobParameters;
import com.google.firebase.iid.FirebaseInstanceId;

@Accessors(prefix = "_")
public class UnsubscribeJobService extends ProSportJobService {
    private static final String _LOG_TAG = ConstantBuilder.logTag(UnsubscribeJobService.class);

    public static final String TAG = UnsubscribeJobService.class.getName();

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
                val firebaseRegistrationId = FirebaseInstanceId.getInstance().getToken();
                if (firebaseRegistrationId != null) {
                    firebaseMessagingManager
                        .unsubscribe(true, firebaseRegistrationId)
                        .toBlocking()
                        .subscribe();
                }
            } catch (final Exception exception) {
                Log.w(_LOG_TAG, "onRunJob: ", exception);

                jobResult = RESULT_FAIL_NORETRY;
            }
        }

        return jobResult;
    }
}
