package mobile.com.tarifeogren.receivers;

import android.com.tarifeogren.BuildConfig;
import android.com.tarifeogren.R;
import mobile.com.tarifeogren.mail.GMailSender;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Fatih on 18.05.2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message = "";
    private String mReceiverMail = "";
    private String mOptionalInfo = "";
    private String mRemainedTariff = "";
    ConnectivityManager mConnectivityManager;
    private static final String PASSWORD = BuildConfig.PASSWORD;

    String wholeMessage = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        loadData(context);
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    //Read the whole message
                    for (Object aObject : pdu_Objects) {
                        currentSMS = getIncomingMessage(aObject, bundle);
                        message = currentSMS.getDisplayMessageBody();
                        wholeMessage += message;
                    }
                    if (wholeMessage.contains("itibari")) {
                        //Send the received message to relate e-mail address
                        MailTask task = new MailTask();

                        //Control whether there is connection, if yes then initLoader otherwise warn the user there is no internet connection
                        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
                        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                            if(!wholeMessage.isEmpty()) {
                                task.execute(wholeMessage, mReceiverMail, mRemainedTariff,PASSWORD);
                                Toast.makeText(context, context.getResources().getString(R.string.mail_sent), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                        }
                    }
                    this.abortBroadcast();
                }
            }
        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    private class MailTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                GMailSender sender = new GMailSender("tarifesorgusonucu@gmail.com", params[3]);
                sender.sendMail(params[2],
                        params[0],
                        "tarifesorgusonucu@gmail.com",
                        params[1]);
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public void loadData(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.info_file), Context.MODE_PRIVATE);
        //Control if Shared Preferences includes all of the necessary fields
        if (pref.contains(context.getResources().getString(R.string.sms_number)) && pref.contains(context.getResources().getString(R.string.message)) &&
            pref.contains(context.getResources().getString(R.string.receiver_mail)))
            //get the phone number and message from shared preferences file
                mReceiverMail = pref.getString(context.getResources().getString(R.string.receiver_mail), context.getResources().getString(R.string.default_value));
                mOptionalInfo = pref.getString(context.getResources().getString(R.string.optional_info), context.getResources().getString(R.string.no_info));
                mRemainedTariff = context.getResources().getString(R.string.remained_tariff) + " "+ context.getResources().getString(R.string.extra_info)+ " " + mOptionalInfo;
    }
}