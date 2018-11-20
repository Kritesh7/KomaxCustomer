package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.SplashActivity;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.utils.SimpleSpanBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.URI;
import java.util.regex.Pattern;

public class ArrangeCallActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppRequestCallIns";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppRequestCallIns";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    Button btn_submit;
    EditText txt_arrange_mobile_no;

    LinearLayout maincontainer;

    TextView tv_mobile_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_call);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tv_mobile_no = findViewById(R.id.tv_mobile_no);

        SimpleSpanBuilder ssbStatus = new SimpleSpanBuilder();
        ssbStatus.appendWithSpace("Mobile No");
        ssbStatus.append("*",new ForegroundColorSpan(Color.RED),new RelativeSizeSpan(1));
        tv_mobile_no.setText(ssbStatus.build());

        txt_arrange_mobile_no = (EditText) findViewById(R.id.txt_arrange_mobile_no);
        btn_submit = findViewById(R.id.btn_submit);
        maincontainer = findViewById(R.id.maincontainer);
        String MobileNo1 = Config_Customer.getSharedPreferences(ArrangeCallActivity.this, "pref_Customer", "MobileNo", "");
        txt_arrange_mobile_no.setText(MobileNo1);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Config_Customer.isOnline(ArrangeCallActivity.this);
                if (Config_Customer.internetStatus == true) {

                    if (txt_arrange_mobile_no.getText().toString().trim().compareTo("")!=0) {
                        Config_Customer.putSharedPreferences(ArrangeCallActivity.this, "pref_Customer", "MobileNo", txt_arrange_mobile_no.getText().toString());
                        new ArrangeCallAsyntask().execute();

                    } else {
                        Config_Customer.alertBox("Please Enter Your Mobile no ",
                                ArrangeCallActivity.this);
                        txt_arrange_mobile_no.requestFocus();
                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ArrangeCallActivity.this);
                }

            }
        });


    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() != 10) {
                // if(phone.length() != 10) {
                check = false;
                Config_Customer.toastShow("Not Valid Number", ArrangeCallActivity.this);
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.dashboard:
                Intent intent;
                intent = new Intent(ArrangeCallActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ArrangeCallActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ArrangeCallActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ArrangeCallActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ArrangeCallActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(ArrangeCallActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(ArrangeCallActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(ArrangeCallActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ArrangeCallActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(ArrangeCallActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(ArrangeCallActivity.this);
                finish();
                Config_Customer.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.download_file:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://app.komaxindia.co.in/Customer/Customer-User-Manual.pdf"));
                startActivity(browserIntent);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {

            Intent intent = new Intent(ArrangeCallActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
    }

    public class ArrangeCallAsyntask extends AsyncTask<String, Integer, String> {
        int flag;
        String status, MobileNo;
        String jsonValue;
        ProgressDialog progressDialog;
        String msgstatus;

        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MobileNo = txt_arrange_mobile_no.getText().toString().trim();
            progressDialog = ProgressDialog.show(ArrangeCallActivity.this,"Loading","Please wait...",true,false);
        }

        @Override
        protected String doInBackground(String... params) {
            String ContactPersonId = Config_Customer.getSharedPreferences(
                    ArrangeCallActivity.this, "pref_Customer", "ContactPersonId", "");

            String AuthCode = Config_Customer.getSharedPreferences(ArrangeCallActivity.this,
                    "pref_Customer", "AuthCode", "");
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("Mobile", MobileNo);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    jsonValue = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {

                            flag = 4;
                        } else {

                            flag = 2;
                        }
                    } else {
                        flag = 1;
                    }
                } else {
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return null;
        }

        protected void onProgressUpdate(String... text) {
            //progressArrangeCall.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ArrangeCallActivity.this);
            } else {
                if (flag == 2) {
                    Config_Customer.toastShow("Thanks! We will call you soon",
                            ArrangeCallActivity.this);
                    Intent intent = new Intent(ArrangeCallActivity.this,
                            DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (flag == 3) {

                        Config_Customer.toastShow("No Response", ArrangeCallActivity.this);
                    } else {
                        if (flag == 4) {

                            Config_Customer.toastShow(msgstatus, ArrangeCallActivity.this);
                            Intent i = new Intent(ArrangeCallActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }else if(flag == 5){
                            ScanckBar();
                            btn_submit.setEnabled(false);
                        }
                    }
                }
            }
          progressDialog.dismiss();
        }
    }

    private void ScanckBar() {

        Snackbar snackbar = Snackbar
                .make(maincontainer, "Connectivity issues", Snackbar.LENGTH_LONG)
                .setDuration(60000)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_submit.setEnabled(true);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

}
