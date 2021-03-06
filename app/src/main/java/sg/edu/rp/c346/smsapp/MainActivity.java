package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    Button btnSendVia;
    EditText etTo;
    EditText etContent;
    BroadcastReceiver br = new SMSReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.buttonSend);
        btnSendVia = findViewById(R.id.buttonSendVia);
        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        checkPermission();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br,filter);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listPhone = new ArrayList<>();
                String phoneNo = etTo.getText().toString();
                String msgContent = etContent.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                if(phoneNo.contains(",")){
                    String[] result = phoneNo.trim().split(",");
                    for(int i = 0; i < result.length; i++){
                        listPhone.add(result[i]);
                        smsManager.sendTextMessage(result[i],null,msgContent,null,null);
                    }
                    String inString = android.text.TextUtils.join(",",listPhone);
                    Toast.makeText(getBaseContext(),"Message Sent to " + inString, Toast.LENGTH_LONG).show();
                }
                else if(phoneNo.contains(";")){
                    String[] result = phoneNo.trim().split(";");
                    for(int i = 0; i < result.length; i++){
                        listPhone.add(result[i]);
                        smsManager.sendTextMessage(result[i],null,msgContent,null,null);
                    }
                    String inString = android.text.TextUtils.join(",",listPhone);
                    Toast.makeText(getBaseContext(),"Message Sent to " + inString, Toast.LENGTH_LONG).show();
                }
                else{
                    smsManager.sendTextMessage(phoneNo,null,msgContent,null,null);
                    Toast.makeText(getBaseContext(),"Message Sent",Toast.LENGTH_LONG).show();
                }
                etContent.setText("");
            }
        });

        btnSendVia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = etTo.getText().toString();
                String msgContent = etContent.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + phoneNo));
                intent.putExtra("sms_body", msgContent);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    private void checkPermission(){
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
}
