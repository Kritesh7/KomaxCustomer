package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.utils.SimpleSpanBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://tempuri.org/AppUserChangePassword";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME1 = "AppUserChangePassword";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerloginkomaxservice.asmx?";

    Button btn_submit;
    EditText txt_old_paasword, txt_new_password, txt_confirm_password;

    ProgressDialog progressDialog;
    LinearLayout maincontainer;

    TextView tv_old_password, tv_new_password, tv_confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        tv_old_password = findViewById(R.id.tv_old_password);
        tv_new_password = findViewById(R.id.tv_new_password);
        tv_confirm_password = findViewById(R.id.tv_confirm_password);

        SimpleSpanBuilder ssbOldPass = new SimpleSpanBuilder();
        ssbOldPass.appendWithSpace("Old Password");
        ssbOldPass.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_old_password.setText(ssbOldPass.build());

        SimpleSpanBuilder ssbNewPass = new SimpleSpanBuilder();
        ssbNewPass.appendWithSpace("New Password");
        ssbNewPass.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_new_password.setText(ssbNewPass.build());

        SimpleSpanBuilder ssbConfirmPass = new SimpleSpanBuilder();
        ssbConfirmPass.appendWithSpace("Re-enter Password");
        ssbConfirmPass.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_confirm_password.setText(ssbConfirmPass.build());

        txt_old_paasword = findViewById(R.id.txt_old_password);
        txt_new_password = findViewById(R.id.txt_new_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        btn_submit = findViewById(R.id.btn_submit);
        maincontainer = findViewById(R.id.maincontainer);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                String saveoldpass = Config_Customer.getSharedPreferences(ChangePasswordActivity.this, "pref_Customer", "Password", "");
                String oldpassword = txt_old_paasword.getText().toString();
                String newpassword = txt_new_password.getText().toString();
                String Repassword = txt_confirm_password.getText().toString();

                Config_Customer.isOnline(ChangePasswordActivity.this);
                if (Config_Customer.internetStatus == true) {

                    if (oldpassword.compareTo("") != 0 && newpassword.compareTo("") != 0 && Repassword.compareTo("") != 0) {
                        if (oldpassword.compareTo(saveoldpass) != 0) {
                            Config_Customer.alertBox("Old Password does not match!", ChangePasswordActivity.this);
                            txt_old_paasword.setText("");

                            txt_old_paasword.requestFocus();
                        } else {
                            if (newpassword.compareTo(Repassword) != 0) {
                                Config_Customer.alertBox("New Password and Confirm Password does not match!", ChangePasswordActivity.this);
                                txt_confirm_password.setText("");
                                txt_confirm_password.requestFocus();
                            } else {
                                btn_submit.setClickable(false);
                                new ChangePassAsync().execute();
                            }
                        }
                    } else {
                        if (oldpassword.compareTo("") == 0) {
                            Config_Customer.alertBox("Please enter Old Password", ChangePasswordActivity.this);
                            txt_old_paasword.requestFocus();
                        } else if (newpassword.compareTo("") == 0) {
                            Config_Customer.alertBox("Please enter New Password", ChangePasswordActivity.this);
                            txt_new_password.requestFocus();
                        } else if (Repassword.compareTo("") == 0) {
                            Config_Customer.alertBox("Please enter Confirm Password", ChangePasswordActivity.this);
                            txt_confirm_password.requestFocus();
                        } else if (oldpassword.compareTo("") == 0 && newpassword.compareTo("") == 0)
                            Config_Customer.alertBox("Please enter Old Password and New Password", ChangePasswordActivity.this);
                        else if (newpassword.compareTo("") == 0 && Repassword.compareTo("") == 0)
                            Config_Customer.alertBox("Please enter New Password and Confirm Password", ChangePasswordActivity.this);
                        else if (oldpassword.compareTo("") == 0)
                            Config_Customer.alertBox("Please enter Old Password", ChangePasswordActivity.this);
                        else if (newpassword.compareTo("") == 0)
                            Config_Customer.alertBox("Please enter New Password", ChangePasswordActivity.this);
                        else
                            Config_Customer.alertBox("Please enter Confirm Password", ChangePasswordActivity.this);
                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ChangePasswordActivity.this);
                }

            }
        });
    }

    private class ChangePassAsync extends AsyncTask<String, Integer, String> {

        int flag = 0;
        String jsonValue;
        String status = "";
        String msgstatus;

        String LoginStatus;
        String invalid = "LoginFailed";
        String valid = "success";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ChangePasswordActivity.this, "", "Please wait...", true, false, null);
        }

        @Override
        protected String doInBackground(String... strings) {
            String ContactPersonId = Config_Customer.getSharedPreferences(ChangePasswordActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(ChangePasswordActivity.this, "pref_Customer", "AuthCode", "");
            String oldpassword = txt_old_paasword.getText().toString();
            String newpassword = txt_new_password.getText().toString();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("OldPassword", oldpassword);
            request.addProperty("NewPassword", newpassword);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
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
                        } else if (LoginStatus.equals(valid)) {
                            flag = 2;
                        } else {
                            flag = 1;
                        }
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
                Config_Customer.toastShow(msgstatus, ChangePasswordActivity.this);
                progressDialog.dismiss();
            } else {
                if (flag == 2) {
                    Config_Customer.toastShow("Password Changed Successfully", ChangePasswordActivity.this);
                    txt_old_paasword.setText("");
                    txt_new_password.setText("");
                    txt_confirm_password.setText("");

                    Config_Customer.logout(ChangePasswordActivity.this);
                    finish();
                    Config_Customer.putSharedPreferences(ChangePasswordActivity.this, "checklogin", "status", "2");

                } else if (flag == 3) {

                    Config_Customer.toastShow("No Response", ChangePasswordActivity.this);

                } else if (flag == 4) {

                    Config_Customer.toastShow(msgstatus, ChangePasswordActivity.this);
                    Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else if (flag == 5) {
                    ScanckBar();
                    btn_submit.setEnabled(true);
                }
            }
            progressDialog.dismiss();
            btn_submit.setClickable(true);
            // finish();
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
                intent = new Intent(ChangePasswordActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ChangePasswordActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ChangePasswordActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ChangePasswordActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ChangePasswordActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(ChangePasswordActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(ChangePasswordActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(ChangePasswordActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(ChangePasswordActivity.this);
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
        Intent intent = new Intent(ChangePasswordActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
