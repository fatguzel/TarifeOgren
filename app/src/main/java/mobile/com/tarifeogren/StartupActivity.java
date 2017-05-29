package mobile.com.tarifeogren;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.com.tarifeogren.R;

import mobile.com.tarifeogren.receivers.AlarmReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fatih on 18.05.2017.
 */

public class StartupActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private String DEFAULT_INFO = " Ek Bilgi Verilmedi";
    private String DEFAULT_OPERATOR = " Operator Bilinmiyor";
    ConnectivityManager mConnectivityManager;
    private int TWO_MINUTES_IN_MILISECONDS = 1000 * 60 * 2;

    @BindView(R.id.operator_name_text_view)
    TextView mOperatorNameTextView;
    @BindView(R.id.optional_info_edit_text)
    EditText mOptionalInfoEditText;
    @BindView(R.id.mail_edit_text)
    EditText mMailEditText;
    @BindView(R.id.time_interval_spinner)
    Spinner mTimeIntervalSpinner;

    String mSmsNumber = "";
    String mMessage = "";
    String mMail = "";
    int selection = -1;
    String mOperatorName = DEFAULT_OPERATOR;
    String mOptionalInfo = DEFAULT_INFO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ButterKnife.bind(this);

        TelephonyManager telephonyManager =((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE));
        mOperatorName = telephonyManager.getNetworkOperatorName();

        mOperatorNameTextView.setText(" "+mOperatorName);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    public void onClick(View view) {
          switch (view.getId()) {
                case R.id.save_and_exit:
                    sendSmsAccordingToTimeInterval();
                    break;
                case R.id.demo_button:
                    //Control whether there is connection, if yes then trigger the sms  action otherwise warn the user there is no internet connection
                    mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                        sendSmsImmediately();
                    }else {
                        Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getText(R.string.help_text_content));
            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (dialogInterface != null) {
                        dialogInterface.dismiss();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
           else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getText(R.string.info_text_content));
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            return true;
            }
        }

    private void sendSmsAccordingToTimeInterval() {
        //If MailEditText is not filled as expected warn the user
        if(!mMailEditText.getText().toString().trim().isEmpty() && mMailEditText.getText().toString().trim().contains("@") ) {
            mMail = mMailEditText.getText().toString().trim();
            if (!mOptionalInfoEditText.getText().toString().trim().isEmpty()){
                mOptionalInfo = mOptionalInfoEditText.getText().toString().trim();
            }
            selection = (int) mTimeIntervalSpinner.getSelectedItemId();

            //setSharedPreferences
            setSharedPreferences();

            Calendar calendar = Calendar.getInstance();
            switch (selection) {
                 case 0:
                    // Set the alarm to start at approximately 8:00 a.m.
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    //Set the Alarm once a day
                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, alarmIntent);
                    Toast.makeText(this, getResources().getString(R.string.settings_saved) + "," + getResources().getString(R.string.every_day) + " " + mMail+ " " + getResources().getString(R.string.mail_will_be_sent), Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    //Set the alarm once a week
                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            (AlarmManager.INTERVAL_DAY*7), alarmIntent);
                    Toast.makeText(this, getResources().getString(R.string.settings_saved) + "," + getResources().getString(R.string.every_week) + " " + mMail+ " " + getResources().getString(R.string.mail_will_be_sent), Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                    //Set the alarm once a month
                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            (AlarmManager.INTERVAL_DAY*30), alarmIntent);
                    Toast.makeText(this, getResources().getString(R.string.settings_saved) + "," + getResources().getString(R.string.every_month) + " " + mMail+ " " + getResources().getString(R.string.mail_will_be_sent), Toast.LENGTH_LONG).show();
                    break;
            }
            finish();
        }else{
            Toast.makeText(this, getResources().getString(R.string.enter_mail_address), Toast.LENGTH_SHORT).show();
        }
    }
    //Send an Sms and an E-Mail in 2 Minutes for demo purpose
    private void sendSmsImmediately() {

        if(!mMailEditText.getText().toString().trim().isEmpty() && mMailEditText.getText().toString().trim().contains("@")) {
            mMail = mMailEditText.getText().toString().trim();
            if (!mOptionalInfoEditText.getText().toString().trim().isEmpty()){
                mOptionalInfo = mOptionalInfoEditText.getText().toString().trim();
            }
            selection = (int) mTimeIntervalSpinner.getSelectedItemId();

            setSharedPreferences();

            alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TWO_MINUTES_IN_MILISECONDS, alarmIntent);
            Toast.makeText(this, getResources().getString(R.string.demo_message) + " "+ mMail, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, getResources().getString(R.string.enter_mail_address), Toast.LENGTH_SHORT).show();
        }
    }

    private void setSharedPreferences(){
        SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.info_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(mOperatorName.contains(getResources().getString(R.string.turkcell))){
           mSmsNumber = getResources().getString(R.string.turkcell_sms_no);
           mMessage = getResources().getString(R.string.turkcell_sms);;
        }else if(mOperatorName.contains(getResources().getString(R.string.vodafone))){
            mSmsNumber = getResources().getString(R.string.vodafone_sms_no);
            mMessage = getResources().getString(R.string.vodafone_sms);
        }else if(mOperatorName.contains(getResources().getString(R.string.telekom))){
            mSmsNumber = getResources().getString(R.string.telekom_sms_no);
            mMessage = getResources().getString(R.string.telekom_sms);
        }
        editor.putString(getResources().getString(R.string.sms_number), mSmsNumber);  //key, value
        editor.putString(getResources().getString(R.string.message), mMessage);
        editor.putString(getResources().getString(R.string.receiver_mail), mMail);
        editor.putString(getResources().getString(R.string.optional_info), mOptionalInfo);
        editor.putInt(getResources().getString(R.string.selection), selection);
        editor.apply();
    }
}
