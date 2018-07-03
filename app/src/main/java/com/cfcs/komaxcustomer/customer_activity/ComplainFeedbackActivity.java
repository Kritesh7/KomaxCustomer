package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ComplainFeedbackActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppFeedbackInsUpdt";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppFeedbackInsUpdt";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";


    RatingBar rbPunctuality, rbTechnicianCapability, rbCustomerCentricity, rbOverAllExperience;
    Button btnSubmit, buttonHome, buttonBack;
    TextView tvComplainNo, complainTitle, tvSeriolNo, engNameTxt;
    EditText etRemark;
    String remark = "", ComplainNo = "", ModelNo = "", ComplainTitle = "", status = "", engName = "";

    ProgressBar progressBarMsg;
    float PunctualityRating = 0.0f, TechnicianCapabilityRating = 0.0f, CustomerCentricityRating = 0.0f, OverAllExperienceRating = 0.0f;
    ImageButton ibMenu;
    Context context = ComplainFeedbackActivity.this;

    LinearLayout maincontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_feedback);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        maincontainer = findViewById(R.id.maincontainer);

        rbPunctuality = (RatingBar) findViewById(R.id.rbPunctuality);
        LayerDrawable TimeLineStars = (LayerDrawable) rbPunctuality.getProgressDrawable();
        TimeLineStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        TimeLineStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        TimeLineStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        rbTechnicianCapability = (RatingBar) findViewById(R.id.rbTechnicianCapability);
        LayerDrawable JobSatisfyStars = (LayerDrawable) rbTechnicianCapability.getProgressDrawable();
        JobSatisfyStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        JobSatisfyStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        JobSatisfyStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        rbCustomerCentricity = (RatingBar) findViewById(R.id.rbCustomerCentricity);
        LayerDrawable CustomerCentricityStars = (LayerDrawable) rbCustomerCentricity.getProgressDrawable();
        CustomerCentricityStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        CustomerCentricityStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        CustomerCentricityStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        rbOverAllExperience = (RatingBar) findViewById(R.id.rbOverAllExperience);
        LayerDrawable OverAllExperienceStars = (LayerDrawable) rbOverAllExperience.getProgressDrawable();
        OverAllExperienceStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        OverAllExperienceStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        OverAllExperienceStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        tvComplainNo = (TextView) findViewById(R.id.tvComplainNo);
        engNameTxt = (TextView) findViewById(R.id.eng_nametxt);
        etRemark = (EditText) findViewById(R.id.etRemark);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);


        ComplainNo = getIntent().getExtras().get("ComplainNo").toString();
        ModelNo = getIntent().getExtras().get("ModelName").toString();
        status = getIntent().getExtras().get("status").toString();
        engName = getIntent().getExtras().get("EnginerName").toString();

        tvComplainNo.setText(ComplainNo);
        engNameTxt.setText(engName);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PunctualityRating = rbPunctuality.getRating();
                TechnicianCapabilityRating = rbTechnicianCapability.getRating();
                CustomerCentricityRating = rbCustomerCentricity.getRating();
                OverAllExperienceRating = rbOverAllExperience.getRating();
                remark = etRemark.getText().toString();

                Config_Customer.isOnline(ComplainFeedbackActivity.this);
                if (Config_Customer.internetStatus == true) {

                    if (PunctualityRating == 0.0 || TechnicianCapabilityRating == 0.0 || CustomerCentricityRating == 0.0 || OverAllExperienceRating == 0.0) {
                        Config_Customer.alertBox("Please rate all field !! ", context);
                    } else {

                        if (PunctualityRating >= 3.0 && TechnicianCapabilityRating >= 3.0 && CustomerCentricityRating >= 3.0 && OverAllExperienceRating >= 3.0) {
                            String feedbackValue[] = {ComplainNo, "" + PunctualityRating, "" + TechnicianCapabilityRating,
                                    "" + CustomerCentricityRating, "" + OverAllExperienceRating, remark};
                            new ComplainFeedbackAsync(context).execute(feedbackValue);

                        } else {
                            if (remark.compareTo("") == 0 || remark.isEmpty()) {
                                Config_Customer.alertBox("Please write remark ", context);
                            } else {
                                String feedbackValue[] = {ComplainNo, "" + PunctualityRating, "" + TechnicianCapabilityRating,
                                        "" + CustomerCentricityRating, "" + OverAllExperienceRating, remark};
                                new ComplainFeedbackAsync(context).execute(feedbackValue);
                            }
                        }
                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplainFeedbackActivity.this);
                }

            }
        });

    }

    public class ComplainFeedbackAsync extends AsyncTask<String, String, String> {


        String ContactPersonId = "", AuthCode = "", jsonValue = "", msgstatus, ComplainNo = "", PunctualityRating = "", TechnicianCapabilityRating = "", CustomerCentricityRating = "", OverAllExperienceRating = "", remark = "";
        int flag;
        Context mContext;
        ProgressDialog progressDialog;

        String LoginStatus;
        String invalid = "LoginFailed";
        String valid = "success";

        public ComplainFeedbackAsync(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true, false, null);

            ContactPersonId = Config_Customer.getSharedPreferences(mContext, "pref_Customer", "ContactPersonId", "");
            AuthCode = Config_Customer.getSharedPreferences(mContext, "pref_Customer", "AuthCode", "");
        }

        @Override
        protected String doInBackground(String... params) {

            ComplainNo = params[0];
            PunctualityRating = params[1];
            TechnicianCapabilityRating = params[2];
            CustomerCentricityRating = params[3];
            OverAllExperienceRating = params[4];
            remark = params[5];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("ComplainNo", ComplainNo);
            request.addProperty("Punctuality", "" + PunctualityRating);
            request.addProperty("TechnicianCapability", "" + TechnicianCapabilityRating);
            request.addProperty("CustomerCentricity", "" + CustomerCentricityRating);
            request.addProperty("OverAllExperience", "" + OverAllExperienceRating);
            request.addProperty("Feedback", remark);
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
                flag =5;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, mContext);
            } else {
                if (flag == 2) {
                    // updtStatus = true;
                    Config_Customer.toastShow("Thanks for your feedback", mContext);
                    Intent intent11 = new Intent(ComplainFeedbackActivity.this, ComplaintsActivity.class);
                    intent11.putExtra("status", status);
                    startActivity(intent11);
                    finish();
                } else if (flag == 3) {
                    Config_Customer.toastShow(msgstatus, mContext);
                } else if (flag == 4) {

                    Config_Customer.toastShow(msgstatus, ComplainFeedbackActivity.this);
                    Intent i = new Intent(ComplainFeedbackActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }else if(flag == 5){
                    ScanckBar();
                    btnSubmit.setEnabled(false);
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
                        btnSubmit.setEnabled(true);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ComplainFeedbackActivity.this, ComplaintsActivity.class);
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
                intent = new Intent(ComplainFeedbackActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ComplainFeedbackActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ComplainFeedbackActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ComplainFeedbackActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ComplainFeedbackActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(ComplainFeedbackActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(ComplainFeedbackActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(ComplainFeedbackActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ComplainFeedbackActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(ComplainFeedbackActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(ComplainFeedbackActivity.this);
                finish();
                Config_Customer.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }
}
