package com.example.filmgratis.preference;

import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindString;
import butterknife.ButterKnife;
import com.example.filmgratis.R;
import com.example.filmgratis.services.SchedulerTask;
import com.example.filmgratis.utils.AlarmReceiver;

public class MyPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @BindString(R.string.key_reminder_daily)
    String reminder_daily;

    @BindString(R.string.key_reminder_upcoming)
    String reminder_upcoming;

    @BindString(R.string.key_setting_locale)
    String setting_locale;

    private String mDaily = "07:00";
    private String mNowMovie = "08:00";

    private AlarmReceiver alarmReceiver = new AlarmReceiver();

    private int jobId = 10;

    private Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ButterKnife.bind(this, getActivity());

        findPreference(reminder_daily).setOnPreferenceChangeListener(this);
        findPreference(reminder_upcoming).setOnPreferenceChangeListener(this);
        findPreference(setting_locale).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String key = preference.getKey();
        boolean isOn = (boolean) o;

        if (key.equals(reminder_daily)) {
            if (isOn) {
                alarmReceiver.setRepeatingAlarm(getActivity(), alarmReceiver.TYPE_REPEATING,
                        mDaily, getString(R.string.lb_daily_reminder));
            } else {
                alarmReceiver.cancelAlarm(getActivity(), alarmReceiver.TYPE_REPEATING);
            }

            Toast.makeText(mContext, getString(R.string.lb_daily_reminder)
                    + " " + (isOn ? getString(R.string.lb_activated) : getString(R.string.lb_deactivated)), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (key.equals(reminder_upcoming)) {
            if (isOn) {
                /*startJob();*/
                String dateNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                alarmReceiver.setOneTimeAlarm(getActivity(), alarmReceiver.TYPE_ONE_TIME, dateNow,
                        mNowMovie, getString(R.string.lb_upcoming_reminder));
            } else cancelJob();

            Toast.makeText(mContext, getString(R.string.lb_upcoming_reminder)
                    + " " + (isOn ? getString(R.string.lb_activated) : getString(R.string.lb_deactivated)), Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        if (key.equals(setting_locale)) {
            Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(intent);
            return true;
        }

        return false;
    }

    public void startJob() {
        ComponentName mServiceComponent = new ComponentName(mContext, SchedulerTask.class);

        JobInfo.Builder builder = new JobInfo.Builder(jobId, mServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPeriodic(AlarmManager.INTERVAL_DAY);
        /*builder.setPeriodic(1000);*/

        JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        Log.d("startJob", "mulai");
    }

    private void cancelJob() {
        JobScheduler tm = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(jobId);
    }
}
