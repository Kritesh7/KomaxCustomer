package com.cfcs.komaxcustomer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ProgressBar;

import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.customer_activity.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SplashActivity extends Activity {


    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppCustomerLoginCheck";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppCustomerLoginCheck";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    public ProgressBar progressBar;
    private static int SPLASH_TIME_OUT = 1500;

    String currentVersion = null;

    String newVersion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar_splash);


        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config_Customer.isOnline(SplashActivity.this);
        if (Config_Customer.internetStatus == true) {

            new GetVersionCode().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", SplashActivity.this);
            finish();
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Config_Customer.isOnline(SplashActivity.this);
                if (Config_Customer.internetStatus == true) {

                    new LoginCheck().execute();

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", SplashActivity.this);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }


    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SplashActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    showForceUpdateDialog();
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }

    }

    public void showForceUpdateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(SplashActivity.this,
                R.style.AppTheme));

        alertDialogBuilder.setTitle(SplashActivity.this.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(SplashActivity.this.getString(R.string.youAreNotUpdatedMessage) + " " + newVersion + SplashActivity.this.getString(R.string.youAreNotUpdatedMessage1));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SplashActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + SplashActivity.this.getPackageName())));
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    public class LoginCheck extends AsyncTask<String, String, String> {

        String jsonValue, status;
        int flag;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.getProgress();
        }

        @Override
        protected String doInBackground(String... params) {

            String ContactPersonId = Config_Customer.getSharedPreferences(SplashActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(SplashActivity.this, "pref_Customer", "AuthCode", "");
            if (AuthCode.compareTo("") == 0) {

                flag = 1;
            } else {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
                request.addProperty("ContactPersonId", ContactPersonId);
                request.addProperty("AuthCode", AuthCode);
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
                        if (jsonObject.has("status")) {
                            status = jsonObject.getString("status");

                            if (status.compareTo("success") == 0) {

                                flag = 2;

                            } else if (status.compareTo("LoginFailed") == 0) {

                                flag = 1;

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

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (flag == 1) {
                // Config_Customer.toastShow(status, SplashActivity.this);
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else if (flag == 2) {

                Intent i = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", SplashActivity.this);
            } else if (flag == 5) {
                Config_Customer.toastShow("Server Error", SplashActivity.this);
                finish();
            }

        }

    }
}
