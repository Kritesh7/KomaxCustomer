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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.MachinesDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Objects;

public class MachinesDetailActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppMachineSalesInfo";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppMachineSalesInfo";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";


    String saleId = "";


    TextView txt_customer, txt_plant, txt_principal, txt_machine_model, txt_machine_sub_model,
            txt_machine_serial_no, txt_sw_version, txt_product_key, txt_date_of_supply, txt_date_install, txt_machine_status,
            txt_comment, txt_warranty_s_date, txt_warranty_e_date, txt_amc_month, txt_amc_start_date, txt_amc_end_date;


    ProgressDialog progressDialog;

    LinearLayout maincontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machines_detail);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }


        txt_customer = (TextView)
                findViewById(R.id.txt_customer);
        txt_plant = (TextView) findViewById(R.id.txt_plant);
        txt_principal = (TextView)
                findViewById(R.id.txt_principal);
        txt_machine_model = (TextView)
                findViewById(R.id.txt_machine_model);
        txt_customer = (TextView)
                findViewById(R.id.txt_customer);
        txt_machine_serial_no = (TextView)
                findViewById(R.id.txt_machine_serial_no);
        //textView1Priority = (TextView) item.findViewById(R.id.textView1Priority);
        txt_sw_version = (TextView)
                findViewById(R.id.txt_sw_version);
        txt_product_key = (TextView)
                findViewById(R.id.txt_product_key);
        txt_date_of_supply = (TextView)
                findViewById(R.id.txt_date_of_supply);
        txt_date_install = (TextView)
                findViewById(R.id.txt_date_install);
        txt_machine_status = (TextView)
                findViewById(R.id.txt_machine_status);
        txt_comment = (TextView)
                findViewById(R.id.txt_comment);
        txt_warranty_s_date = (TextView)
                findViewById(R.id.txt_warranty_s_date);
        txt_warranty_e_date = (TextView)
                findViewById(R.id.txt_warranty_e_date);

        txt_amc_start_date = (TextView)
                findViewById(R.id.txt_amc_start_date);
        txt_amc_end_date = (TextView)
                findViewById(R.id.txt_amc_end_date);

        maincontainer = findViewById(R.id.maincontainer);


        Config_Customer.isOnline(MachinesDetailActivity.this);
        if (Config_Customer.internetStatus == true) {

            new MachineDetailAsync().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesDetailActivity.this);
        }


    }

    // Asyntask class
    public class MachineDetailAsync extends AsyncTask<String, String, String> {
        int flag;
        String msgstatus, machine_detail_value;
        JSONArray machineDetail;
        ProgressDialog fillListDialog;

        String LoginStatus;
        String invalid = "LoginFailed";


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MachinesDetailActivity.this, "", "Please wait...", true, false, null);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Bundle bundle = getIntent().getExtras();
            //String complainno = bundle.getString("ComplainNo");
            saleId = bundle.getString("SaleID");
            String ContactPersonId = Config_Customer.getSharedPreferences(
                    MachinesDetailActivity.this, "pref_Customer", "ContactPersonId", "");
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            String AuthCode = Config_Customer.getSharedPreferences(MachinesDetailActivity.this, "pref_Customer", "AuthCode", "");
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("SaleID", saleId);
            request.addProperty("AuthCode", AuthCode);

            Log.e("authcode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    machine_detail_value = result.getProperty(0).toString();

                    // JSONObject jsonObject = new JSONObject(machine_detail_value);
                    machineDetail = new JSONArray(machine_detail_value);
                    JSONObject jsonObject = machineDetail.getJSONObject(0);
                    // machineDetail = jsonObject.toString();


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
            return machine_detail_value;
        }

        @Override
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, MachinesDetailActivity.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(machine_detail_value);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String CustomerName = jsonObject.getString("ParentCustomerName").toString();
                    txt_customer.setText(CustomerName);
                    String ParentCustomerName = jsonObject.getString("SiteAddress").toString();
                    txt_plant.setText(ParentCustomerName);
                    String PrincipleName = jsonObject.getString("PrincipleName").toString();
                    txt_principal.setText(PrincipleName);
                    String ModelName = jsonObject.getString("ModelName").toString();
                    txt_machine_model.setText(ModelName);
                    String MachineSerialNo = jsonObject.getString("SerialNo").toString();
                    txt_machine_serial_no.setText(MachineSerialNo);
                    String SWVersion = jsonObject.getString("SWVersion").toString();
                    txt_sw_version.setText(SWVersion);
                    String ProductKey = jsonObject.getString("ProductKey").toString();
                    txt_product_key.setText(ProductKey);
                    String DateOfSupply = jsonObject.getString("DateOfSupply").toString();
                    txt_date_of_supply.setText(DateOfSupply);
                    String DateOfInstallation = jsonObject.getString("DateOfInstallation");
                    txt_date_install.setText(DateOfInstallation);
                    String MachineStatus = jsonObject.getString("TransactionTypeName").toString();
                    txt_machine_status.setText(MachineStatus);

                    String Comments = jsonObject.getString("Comments").toString();
                    txt_comment.setText(Comments);
                    String WarrantyStartDate = jsonObject.getString("WarrantyStartDate").toString();
                    txt_warranty_s_date.setText(WarrantyStartDate);
                    String WarrantyEndDate = jsonObject.getString("WarrantyEndDate").toString();
                    txt_warranty_e_date.setText(WarrantyEndDate);


                    String AMCStartDate = jsonObject.getString("AMCStartDate").toString();
                    txt_amc_start_date.setText(AMCStartDate);

                    String AMCEndDate = jsonObject.getString("AMCEndDate").toString();
                    txt_amc_end_date.setText(AMCEndDate);


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", MachinesDetailActivity.this);

            } else {
                if (flag == 4) {

                    Config_Customer.toastShow(msgstatus, MachinesDetailActivity.this);
                    Intent i = new Intent(MachinesDetailActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else if (flag == 5) {

                    ScanckBar();

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
                        Config_Customer.isOnline(MachinesDetailActivity.this);
                        if (Config_Customer.internetStatus == true) {

                            new MachineDetailAsync().execute();

                        } else {
                            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesDetailActivity.this);
                        }

                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MachinesDetailActivity.this, MachinesActivity.class);
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
                intent = new Intent(MachinesDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(MachinesDetailActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(MachinesDetailActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(MachinesDetailActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(MachinesDetailActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(MachinesDetailActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(MachinesDetailActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(MachinesDetailActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(MachinesDetailActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(MachinesDetailActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(MachinesDetailActivity.this);
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
}
