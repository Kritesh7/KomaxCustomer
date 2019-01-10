package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.ActionBar;
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
import android.support.v7.widget.AppCompatImageView;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.utils.IStringConstant;
import com.cfcs.komaxcustomer.utils.SimpleSpanBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity implements IStringConstant,View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static String SOAP_ACTION1 = "http://tempuri.org/AppUserChangePassword";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME1 = "AppUserChangePassword";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerloginkomaxservice.asmx?";

    Button btn_submit;
    EditText txt_old_paasword, txt_new_password, txt_confirm_password;

    ProgressDialog progressDialog;
    LinearLayout maincontainer;

    TextView tv_old_password, tv_new_password, tv_confirm_password;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_item_layout);
            view =Objects.requireNonNull(getSupportActionBar()).getCustomView();
        }


        AppCompatImageView menu_action_bar = view.findViewById(R.id.menu_action_bar);
        TextView cart_badge = view.findViewById(R.id.cart_badge);

        AppCompatImageView cart_image = view.findViewById(R.id.cart_image);

        String ComplaintCount = Config_Customer.getSharedPreferences(ChangePasswordActivity.this, "pref_Customer", "ComplaintCount", "");
        cart_badge.setText(ComplaintCount);

        menu_action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(ChangePasswordActivity.this, v);
                try {
                    Field[] fields = popupMenu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popupMenu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popupMenu.setOnMenuItemClickListener(ChangePasswordActivity.this);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.show();

            }
        });

        cart_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePasswordActivity.this,ComplaintsActivity.class);
                startActivity(i);
            }
        });

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
                    Config_Customer.toastShow(NoInternetConnection, ChangePasswordActivity.this);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Config_Customer.menuNavigation(ChangePasswordActivity.this,item);
        return false;
    }

    private class ChangePassAsync extends AsyncTask<String, Integer, String> {

        int flag = 0;
        String jsonValue;
        String status = "";
        String msgstatus;

        String LoginStatus;
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
    public void onBackPressed() {
        Intent intent = new Intent(ChangePasswordActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
