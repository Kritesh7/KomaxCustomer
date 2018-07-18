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
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ComplainListDetailActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppComplainDetail";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppComplainDetail";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";


    TextView txt_complain_no, txt_plant, txt_problem_title_detail, txt_machine_detail, txt_machine_detail_model,
            txt_principal_detail, txt_complain_type_detail, txt_complain_detail_time, txt_complain_occurrence_detail,
            txt_rectification_detail_date, txt_priority_detail, txt_escalation_detail_level, txt_service_center,
            txt_complain_detail_status, txt_work_status_detail, txt_customer_name,
            txt_description_detail, txt_engineer_name_detail, txt_engineer_mobile_detail, txt_engineer_email_detail, txt_person_name_detail, txt_mobile_detail,
            txt_sub_engineer_name_detail1, txt_sub_engineer_mobile_detail1, txt_sub_engineer_email_detail1, txt_sub_engineer_name_detail2,
            txt_sub_engineer_mobile_detail2, txt_sub_engineer_email_detail2, txt_email_detail, txt_other_contacts_detail;

    CardView card_view4, card_view5;

    String complainno = "", status = "";


    LinearLayout dynamic_linearlayout, maincontainer;

    LinearLayout.LayoutParams layoutparams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_list_detail);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        txt_complain_no = findViewById(R.id.txt_complain_no);
        txt_plant = findViewById(R.id.txt_plant);
        txt_problem_title_detail = findViewById(R.id.txt_problem_title_detail);
        txt_machine_detail = findViewById(R.id.txt_machine_detail);
        txt_machine_detail_model = findViewById(R.id.txt_machine_detail_model);
        txt_principal_detail = findViewById(R.id.txt_principal_detail);
        txt_complain_type_detail = findViewById(R.id.txt_complain_type_detail);
        txt_complain_detail_time = findViewById(R.id.txt_complain_detail_time);
        txt_complain_occurrence_detail = findViewById(R.id.txt_complain_occurrence_detail);
        txt_rectification_detail_date = findViewById(R.id.txt_rectification_detail_date);
        txt_priority_detail = findViewById(R.id.txt_priority_detail);
        txt_escalation_detail_level = findViewById(R.id.txt_escalation_detail_level);
        txt_service_center = findViewById(R.id.txt_service_center);
        txt_complain_detail_status = findViewById(R.id.txt_complain_detail_status);
        txt_work_status_detail = findViewById(R.id.txt_work_status_detail);
        txt_description_detail = findViewById(R.id.txt_description_detail);
        txt_customer_name = findViewById(R.id.txt_customer_name);

        txt_engineer_name_detail = findViewById(R.id.txt_engineer_name_detail);
        txt_engineer_mobile_detail = findViewById(R.id.txt_engineer_mobile_detail);
        txt_engineer_email_detail = findViewById(R.id.txt_engineer_email_detail);

        txt_person_name_detail = findViewById(R.id.txt_person_name_detail);
        txt_mobile_detail = findViewById(R.id.txt_mobile_detail);
        txt_email_detail = findViewById(R.id.txt_email_detail);
        txt_other_contacts_detail = findViewById(R.id.txt_other_contacts_detail);

        txt_sub_engineer_name_detail1 = findViewById(R.id.txt_sub_engineer_name_detail1);
        txt_sub_engineer_mobile_detail1 = findViewById(R.id.txt_sub_engineer_mobile_detail1);
        txt_sub_engineer_email_detail1 = findViewById(R.id.txt_sub_engineer_email_detail1);


        txt_sub_engineer_name_detail2 = findViewById(R.id.txt_sub_engineer_name_detail2);
        txt_sub_engineer_mobile_detail2 = findViewById(R.id.txt_sub_engineer_mobile_detail2);
        txt_sub_engineer_email_detail2 = findViewById(R.id.txt_sub_engineer_email_detail2);

        card_view4 = findViewById(R.id.card_view4);
        card_view5 = findViewById(R.id.card_view5);

        card_view4.setVisibility(View.GONE);
        card_view5.setVisibility(View.GONE);

        maincontainer = findViewById(R.id.maincontainer);


        // dynamic_linearlayout = findViewById(R.id.dynamic_linearlayout);


        //Bundle bundle = getIntent().getExtras();
        complainno = getIntent().getExtras().getString("ComplainNo");
        status = getIntent().getExtras().getString("status");

        Config_Customer.isOnline(ComplainListDetailActivity.this);
        if (Config_Customer.internetStatus == true) {

            new ComplainDetailAsy().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplainListDetailActivity.this);
        }


    }


    private class ComplainDetailAsy extends AsyncTask<String, String, String> {
        int flag;
        String msgstatus;
        String complain_detail_value;
        ProgressDialog progressDialog;
        String ComplaintDetail, ComplainSubordinate;

        JSONArray complainSub;

        String LoginStatus;
        String invalid = "LoginFailed";


        TextView txt_sub_engineer_name;

        int count = 0;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ComplainListDetailActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            String AuthCode = Config_Customer.getSharedPreferences(ComplainListDetailActivity.this, "pref_Customer", "AuthCode", "");
            String ContactPersonId = Config_Customer.getSharedPreferences(ComplainListDetailActivity.this, "pref_Customer", "ContactPersonId", "");
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("ComplainNo", complainno);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    complain_detail_value = result.getProperty(0).toString();

                    Object json = new JSONTokener(complain_detail_value).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(complain_detail_value);
                        JSONArray complainDetailArray = object.getJSONArray("ComplaintDetail");
                        ComplaintDetail = complainDetailArray.toString();
                        complainSub = object.getJSONArray("ComplainSubordinate");
                        ComplainSubordinate = complainSub.toString();
                        if (complain_detail_value.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(complain_detail_value);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;
                        } else {
                            flag = 2;
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(complain_detail_value);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (jsonObject.has("status")) {

                            LoginStatus = jsonObject.getString("status");
                            msgstatus = jsonObject.getString("MsgNotification");
                            if (LoginStatus.equals(invalid)) {

                                flag = 4;
                            } else {

                                flag = 1;
                            }
                        }

                    }

                } else {
                    JSONArray jsonArray = new JSONArray(complain_detail_value);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    msgstatus = jsonObject.getString("MsgNotification");
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return complain_detail_value;
        }


        @Override
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ComplainListDetailActivity.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(ComplaintDetail);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String ComplainNo = jsonObject.getString("ComplainNo").toString();
                    txt_complain_no.setText(ComplainNo);

                    String PlantName = jsonObject.getString("SiteAddress").toString();
                    txt_plant.setText(PlantName);

                    String CustomerName = jsonObject.getString("CustomerName").toString();
                    txt_customer_name.setText(CustomerName);

                    String ProblemTitle = jsonObject.getString("ComplaintTitle").toString();
                    txt_problem_title_detail.setText(ProblemTitle);

                    String MachineDetail = jsonObject.getString("MachineSerialNo").toString();
                    txt_machine_detail.setText(MachineDetail);

                    String ModelName = jsonObject.getString("SubMachineModelName").toString();
                    txt_machine_detail_model.setText(ModelName);

                    String PrincipalDetail = jsonObject.getString("PrincipalName").toString();
                    txt_principal_detail.setText(PrincipalDetail);

                    String ComplainType = jsonObject.getString("ComplaintTypeText").toString();
                    txt_complain_type_detail.setText(ComplainType);


                    String ComplainTimeText1 = jsonObject.getString("ComplainDateTimeText").toString();
                    txt_complain_detail_time.setText(ComplainTimeText1);


                    String ComplainOccure = jsonObject.getString("TimeOfOccuranceDateTimeText").toString();
                    txt_complain_occurrence_detail.setText(ComplainOccure);

                    String RectifiedDateText1 = jsonObject.getString("RectifiedDateTimeText").toString();
                    txt_rectification_detail_date.setText(RectifiedDateText1);

                    String PriorityText1 = jsonObject.getString("PriorityText").toString();
                    txt_priority_detail.setText(PriorityText1);

                    String EscalationShortCode = jsonObject.getString("EscalationName");
                    txt_escalation_detail_level.setText(EscalationShortCode);

                    String ServiceCenter = jsonObject.getString("ZoneName");
                    txt_service_center.setText(ServiceCenter);

                    String StatusText1 = jsonObject.getString("StatusText").toString();
                    txt_complain_detail_status.setText(StatusText1);

                    String WorkStatus = jsonObject.getString("WorkStatusName").toString();
                    txt_work_status_detail.setText(WorkStatus);


                    String Description = jsonObject.getString("Description").toString();
                    txt_description_detail.setText(Description);

                    String EngineerName = jsonObject.getString("EngineerName").toString();
                    txt_engineer_name_detail.setText(EngineerName);

                    String EngineerMobile = jsonObject.getString("MobileNo").toString();
                    txt_engineer_mobile_detail.setText(EngineerMobile);

                    String EngineerEmail = jsonObject.getString("EmailID").toString();
                    txt_engineer_email_detail.setText(EngineerEmail);

                    String PersonName = jsonObject.getString("ContactPersonName").toString();
                    txt_person_name_detail.setText(PersonName);

                    String MobileDetail = jsonObject.getString("ContactPersonMobile").toString();
                    txt_mobile_detail.setText(MobileDetail);

                    String Email = jsonObject.getString("ContactPersonMailID").toString();
                    txt_email_detail.setText(Email);

                    String OtherContacts = jsonObject.getString("ContactPersonContactNo").toString();
                    txt_other_contacts_detail.setText(OtherContacts);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (complainSub != null) {

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(ComplainSubordinate);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (i == 0) {
                                card_view4.setVisibility(View.VISIBLE);
                                String EngineerName = jsonObject.getString("EngineerName").toString();
                                txt_sub_engineer_name_detail1.setText(EngineerName);
                                String EmailID = jsonObject.getString("EmailID").toString();
                                txt_sub_engineer_email_detail1.setText(EmailID);
                                String MobileNo = jsonObject.getString("MobileNo").toString();
                                txt_sub_engineer_mobile_detail1.setText(MobileNo);
                            } else {
                                card_view5.setVisibility(View.VISIBLE);
                                String EngineerName1 = jsonObject.getString("EngineerName").toString();
                                txt_sub_engineer_name_detail2.setText(EngineerName1);
                                String EmailID1 = jsonObject.getString("EmailID").toString();
                                txt_sub_engineer_email_detail2.setText(EmailID1);
                                String MobileNo1 = jsonObject.getString("MobileNo").toString();
                                txt_sub_engineer_mobile_detail2.setText(MobileNo1);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (complainSub == null) {
                    card_view4.setVisibility(View.GONE);
                    card_view5.setVisibility(View.GONE);
                }
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", ComplainListDetailActivity.this);

            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, ComplainListDetailActivity.this);
                Intent i = new Intent(ComplainListDetailActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            } else if (flag == 5) {

                ScanckBar();

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
                        Config_Customer.isOnline(ComplainListDetailActivity.this);
                        if (Config_Customer.internetStatus == true) {

                            new ComplainDetailAsy().execute();

                        } else {
                            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplainListDetailActivity.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ComplainListDetailActivity.this, ComplaintsActivity.class);
        intent.putExtra("status", status);
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
                intent = new Intent(ComplainListDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ComplainListDetailActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ComplainListDetailActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }

                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ComplainListDetailActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ComplainListDetailActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(ComplainListDetailActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(ComplainListDetailActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(ComplainListDetailActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ComplainListDetailActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(ComplainListDetailActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(ComplainListDetailActivity.this);
                finish();
                Config_Customer.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }
}
