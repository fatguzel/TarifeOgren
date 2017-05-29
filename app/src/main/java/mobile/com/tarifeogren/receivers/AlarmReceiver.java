package mobile.com.tarifeogren.receivers;

import android.com.tarifeogren.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Fatih on 18.05.2017.
 */

public class AlarmReceiver extends BroadcastReceiver{

    private String mPhoneNumber = "";
    private String mMessage = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        loadData(context);
        SmsManager sms = SmsManager.getDefault();

        if(!mPhoneNumber.isEmpty() && !mMessage.isEmpty()) {
            sms.sendTextMessage(mPhoneNumber, null, mMessage, null, null);
        }
    }

    public void loadData(Context context){
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.info_file),Context.MODE_PRIVATE);
        //Control if Shared Preferences includes all of the necessary fields
        if(pref.contains(context.getResources().getString(R.string.sms_number))&& pref.contains(context.getResources().getString(R.string.message))&&
                pref.contains(context.getResources().getString(R.string.receiver_mail))&& pref.contains(context.getResources().getString(R.string.selection)))
            //get the phone number and message from shared preferences file
            mPhoneNumber = pref.getString(context.getResources().getString(R.string.sms_number), context.getResources().getString(R.string.default_value));
            mMessage = pref.getString(context.getResources().getString(R.string.message),context.getResources().getString(R.string.default_value));
    }
}
