package com.cfcs.komaxcustomer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfcs.komaxcustomer.broadcastReciever.AutoNofity;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.customer_activity.DashboardActivity;
import com.cfcs.komaxcustomer.customer_activity.ForgetPasswordActivity;
import com.cfcs.komaxcustomer.customer_activity.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LoginActivity extends Activity {

    private static String SOAP_ACTION1 = "http://tempuri.org/AppContactPersonlogin";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME1 = "AppContactPersonlogin";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerloginkomaxservice.asmx?";


    EditText txt_user_name, txt_user_pass;
    Button btn_submit;
    TextView forgotPassword;
    RelativeLayout maincontainer;

    String userName, userPass, AuthCode, ClientName, ClientVersion;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize widgets
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_user_pass = findViewById(R.id.txt_user_pass);
        btn_submit = findViewById(R.id.btn_submit);
        forgotPassword = findViewById(R.id.forgotPassword);

        String Username = Config_Customer.getSharedPreferences(LoginActivity.this, "pref_Customer", "UserName", "");
        txt_user_name.setText(Username);
        userName = txt_user_name.getText().toString().trim();
        userPass = txt_user_pass.getText().toString().trim();

        if(userName.compareTo("") != 0){
            txt_user_pass.requestFocus();
        }else {
            txt_user_name.requestFocus();
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = txt_user_name.getText().toString().trim();
                userPass = txt_user_pass.getText().toString().trim();

                if (userName.compareTo("") != 0 && userPass.compareTo("") != 0) {
                    Config_Customer.isOnline(LoginActivity.this);
                    if (Config_Customer.internetStatus == true) {
                        btn_submit.setClickable(false);

                        new LoginTask().execute();
                    } else {
                        Config_Customer.toastShow("No Internet Connection", LoginActivity.this);
                    }
                } else {
                    if (userName.compareTo("") == 0 && userPass.compareTo("") == 0) {
                        Config_Customer.alertBox("Please enter Username and Password", LoginActivity.this);

                    } else {
                        if (userName.compareTo("") == 0) {
                            Config_Customer.alertBox("Please enter Username", LoginActivity.this);
                            txt_user_name.requestFocus();
                        } else {
                            Config_Customer.alertBox("Please enter Password", LoginActivity.this);
                            txt_user_name.requestFocus();
                        }
                    }
                }

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public class LoginTask extends AsyncTask<String, String, String> {

        String jsonValue, status, ContactPersonId, ContactPersonName, ZoneID, CompanyContactNo, MobileNo, AppFileName, AppVersion, AuthCode, ClientName, ClientVersion;
        int flag;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...", true, false, null);
        }

        @Override
        protected String doInBackground(String... params) {
            long randomNumber = (long) ((Math.random() * 9000000) + 1000000);
            AuthCode = String.valueOf(randomNumber);
            ClientName = Build.MANUFACTURER + " " + Build.MODEL;
            ClientVersion = Build.VERSION.RELEASE
                    + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("UserName", userName);
            request.addProperty("Password", userPass);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("ClientName", ClientName);
            request.addProperty("ClientVersion", ClientVersion);
            Log.e("auth key is:", AuthCode + " null");
            Log.e("client name is", ClientName + "null");
            Log.e("client version is", ClientVersion + "null");
            SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelop.setOutputSoapObject(request);
            envelop.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelop);
                SoapObject result = (SoapObject) envelop.bodyIn;
                if (result != null) {
                    jsonValue = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("MsgNotification")) {
                        status = jsonObject.getString("MsgNotification");
                        flag = 1;
                    } else {
                        flag = 2;
                        ContactPersonId = jsonObject.getString("ContactPersonId").toString();
                        ContactPersonName = jsonObject.getString("ContactPersonName").toString();
                        ZoneID = jsonObject.getString("ZoneID").toString();
                        CompanyContactNo = jsonObject.getString("CompanyContactNo").toString();
                        MobileNo = jsonObject.getString("MobileNo").toString();

                        //Log.e("AppInfo",newAppVersion + AppUrl + AppFileName);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "UserName", userName);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "Password", userPass);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "ContactPersonName", ContactPersonName);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "ContactPersonId", ContactPersonId);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "CompanyContactNo", CompanyContactNo);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "AuthCode", AuthCode);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "MobileNo", MobileNo);
                        Config_Customer.putSharedPreferences(LoginActivity.this, "pref_Customer", "PendingFeedback", "1");

                    }
                } else {
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (flag == 1) {
                Config_Customer.toastShow(status, LoginActivity.this);
                progressDialog.dismiss();
            } else {
                if (flag == 2) {
                    login();
                } else {
                    Config_Customer.toastShow("No Response", LoginActivity.this);
                    progressDialog.dismiss();
                }
            }
            progressDialog.dismiss();
            txt_user_name.setText("");
            txt_user_pass.setText("");
            btn_submit.setClickable(true);
        }

    }

    private void login() {
        String CheckProfile = "0";

        CheckProfile = Config_Customer.getSharedPreferences(LoginActivity.this, "pref_Customer", "FirstProfileCheck", "");

        if (CheckProfile.compareTo("1") == 0) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            Config_Customer.toastShow("Login Success", LoginActivity.this);

            startService(new Intent(getBaseContext(), AutoNofity.class));

            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
