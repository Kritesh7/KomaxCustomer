package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.utils.SimpleSpanBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppContactPersonProfile";
    private static String SOAP_ACTION2 = "http://cfcs.co.in/AppContactPersonProfileUpdate";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppContactPersonProfile";
    private static String METHOD_NAME2 = "AppContactPersonProfileUpdate";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";


    Button btn_update;
    EditText txt_profile_name, txt_profile_designation, txt_profile_user_name, txt_profile_email, txt_profile_mobile, txt_country_code, txt_other_contacts;


    String profileName = "", designation = "", loginName = "", email = "", mobileNo = "", countryCode = "", otherContact = "";

    LinearLayout maincontainer;

    TextView tv_name, tv_login_user_name, tv_country_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tv_name = findViewById(R.id.tv_name);
        tv_login_user_name = findViewById(R.id.tv_login_user_name);
        tv_country_code = findViewById(R.id.tv_country_code);

        SimpleSpanBuilder ssbName = new SimpleSpanBuilder();
        ssbName.appendWithSpace("Name");
        ssbName.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_name.setText(ssbName.build());

        SimpleSpanBuilder ssbLogin_user_name = new SimpleSpanBuilder();
        ssbLogin_user_name.appendWithSpace("Login User Name");
        ssbLogin_user_name.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_login_user_name.setText(ssbLogin_user_name.build());

        SimpleSpanBuilder ssbCountryCode = new SimpleSpanBuilder();
        ssbCountryCode.appendWithSpace("Country Code + Mobile No");
        ssbCountryCode.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_country_code.setText(ssbCountryCode.build());

        txt_profile_name = findViewById(R.id.txt_profile_name);
        txt_profile_designation = findViewById(R.id.txt_profile_designation);
        txt_profile_user_name = findViewById(R.id.txt_profile_user_name);
        txt_profile_email = findViewById(R.id.txt_profile_email);
        txt_profile_mobile = findViewById(R.id.txt_profile_mobile);
        txt_country_code = findViewById(R.id.txt_country_code);
        txt_other_contacts = findViewById(R.id.txt_other_contacts);
        btn_update = findViewById(R.id.btn_update);

        maincontainer = findViewById(R.id.maincontainer);

        Config_Customer.isOnline(ProfileActivity.this);
        if (Config_Customer.internetStatus == true) {

            new ProfileAsy().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ProfileActivity.this);
        }


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileName = txt_profile_name.getText().toString().trim();
                designation = txt_profile_designation.getText().toString().trim();
                loginName = txt_profile_user_name.getText().toString().trim();
                email = String.valueOf(isValidMail(txt_profile_email.getText().toString().trim()));
                mobileNo = String.valueOf(isValidMobile(txt_profile_mobile.getText().toString().trim()));
                countryCode = txt_country_code.getText().toString().trim();
                otherContact = txt_other_contacts.getText().toString().trim();
                if ((profileName.compareTo("") != 0) && (countryCode.compareTo("") != 0) && (loginName.compareTo("") != 0) && (mobileNo.compareTo("") != 0) && isValidMobile(txt_profile_mobile.getText().toString().trim()) && isValidMail(txt_profile_email.getText().toString().trim())) {
                    btn_update.setClickable(false);

                    Config_Customer.isOnline(ProfileActivity.this);
                    if (Config_Customer.internetStatus == true) {

                        new ProfileUpdate().execute();

                    } else {
                        Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ProfileActivity.this);
                    }
                } else {
                    if (TextUtils.isEmpty(profileName)) {
                        Config_Customer.alertBox("Please Enter Profile Name", ProfileActivity.this);
                        txt_profile_name.requestFocus();
                        focusOnView();

                    } else if (TextUtils.isEmpty(loginName)) {

                        Config_Customer.alertBox("Please Enter Login Name", ProfileActivity.this);
                        txt_profile_user_name.requestFocus();
                    } else if (TextUtils.isEmpty(email)) {
                        Config_Customer.alertBox("Please Enter Email", ProfileActivity.this);
                        txt_profile_email.requestFocus();
                    } else if (TextUtils.isEmpty(countryCode)) {
                        Config_Customer.alertBox("Please Enter Country Code", ProfileActivity.this);
                        txt_country_code.requestFocus();
                    } else if (TextUtils.isEmpty(mobileNo)) {
                        Config_Customer.alertBox("Please Enter Mobile No", ProfileActivity.this);
                        txt_profile_mobile.requestFocus();
                    }
                }

            }
        });
    }

    private void focusOnView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() != 10) {
                // if(phone.length() != 10) {
                check = false;
                Config_Customer.toastShow("Not Valid Number", ProfileActivity.this);
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if (!check) {
            Config_Customer.toastShow("Not Valid Email", ProfileActivity.this);
        }
        return check;
    }

    private class ProfileAsy extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;
        int flag;
        String jsonValue;
        String msgstatus;

        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ProfileActivity.this, "Loading", "Please wait...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {


            String ContactPersonId = Config_Customer.getSharedPreferences(
                    ProfileActivity.this, "pref_Customer", "ContactPersonId", "");

            String AuthCode = Config_Customer.getSharedPreferences(ProfileActivity.this,
                    "pref_Customer", "AuthCode", "");
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
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

                            flag = 1;
                        }
                    } else {
                        flag = 2;
                    }
                } else {
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }


            return msgstatus;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ProfileActivity.this);
            } else {
                if (flag == 2) {
                    try {
                        JSONArray jsonArray = new JSONArray(jsonValue);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String ProfileName = jsonObject.getString("ContactPersonName").toString();
                        txt_profile_name.setText(ProfileName);
                        String Designation = jsonObject.getString("Designation").toString();
                        txt_profile_designation.setText(Designation);
                        String LoginUserNAme = jsonObject.getString("LoginUserName").toString();
                        txt_profile_user_name.setText(LoginUserNAme);
                        String Email = jsonObject.getString("Email").toString();
                        txt_profile_email.setText(Email);
                        String MobileNo = jsonObject.getString("Phone").toString();
                        txt_profile_mobile.setText(MobileNo);
                        String CountryCode = jsonObject.getString("CountryCode").toString();
                        txt_country_code.setText(CountryCode);
                        String OtherContact = jsonObject.getString("OtherPhoneNo").toString();
                        txt_other_contacts.setText(OtherContact);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (flag == 3) {
                    Config_Customer.toastShow("No Response", ProfileActivity.this);
                } else if (flag == 4) {


                    Config_Customer.toastShow(msgstatus, ProfileActivity.this);
                    Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                } else if (flag == 5) {

                    ScanckBar();
                    btn_update.setEnabled(false);
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
                        Config_Customer.isOnline(ProfileActivity.this);
                        if (Config_Customer.internetStatus == true) {

                            new ProfileAsy().execute();
                            btn_update.setEnabled(true);

                        } else {
                            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ProfileActivity.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    private class ProfileUpdate extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;
        int flag = 0;
        String jsonValue;
        String status = "";

        String msgstatus;

        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ProfileActivity.this, "Loading", "Please wait...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {


            String ContactPersonId = Config_Customer.getSharedPreferences(ProfileActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(ProfileActivity.this, "pref_Customer", "AuthCode", "");
            String profileName = txt_profile_name.getText().toString().trim();
            String designation = txt_profile_designation.getText().toString().trim();
            String loginName = txt_profile_user_name.getText().toString().trim();
            String email = txt_profile_email.getText().toString().trim();
            String mobileNo = txt_profile_mobile.getText().toString().trim();
            String countryCode = txt_country_code.getText().toString().trim();
            String otherContact = txt_other_contacts.getText().toString().trim();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("ContactPersonName", profileName);
            request.addProperty("Designation", designation);
            request.addProperty("LoginUserName", loginName);
            request.addProperty("EmailID", email);
            request.addProperty("Phone", mobileNo);
            request.addProperty("CountryCode", countryCode);
            request.addProperty("OtherPhoneNo", otherContact);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION2, envelope);
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

                            flag = 1;
                        }
                    } else {
                        flag = 2;
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ProfileActivity.this);
                Intent i = new Intent(ProfileActivity.this, DashboardActivity.class);
                startActivity(i);
                Config_Customer.putSharedPreferences(ProfileActivity.this, "pref_Customer", "FirstProfileCheck", "1");
                finish();
            } else {
                if (flag == 2) {
                    Config_Customer.toastShow(msgstatus, ProfileActivity.this);
                } else {
                    if (flag == 3) {
                        Config_Customer.toastShow("No Response", ProfileActivity.this);
                    } else {
                        if (flag == 4) {

                            Config_Customer.toastShow(msgstatus, ProfileActivity.this);
                            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else if (flag == 5) {

                            ScanckBar();
                            btn_update.setEnabled(false);

                        }


                    }
                }
            }
            progressDialog.dismiss();
            btn_update.setClickable(true);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
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
                intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ProfileActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ProfileActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ProfileActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ProfileActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(ProfileActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(ProfileActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(ProfileActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(ProfileActivity.this);
                finish();
                Config_Customer.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }
}
