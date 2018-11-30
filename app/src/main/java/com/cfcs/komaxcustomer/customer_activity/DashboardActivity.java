package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.broadcastReciever.AutoNofity;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.PendingFeedbackDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class DashboardActivity extends AppCompatActivity {

    Button btn_arrange_call, btn_call_us, btn_raise_complaint, btn_complaint, btn_machines, btn_feedback;

    boolean updtStatus = false;
    Stack feedValue = new Stack();

    ArrayList<PendingFeedbackDataModel> feedbackList = new ArrayList<PendingFeedbackDataModel>();

    String[] ComplainNo;
    String[] MachineSerialNo;
    String[] ComplaintTitle;
    String[] EngineerName;

    AlertDialog dialog;
    View dialogView;

    Button btnSubmit, btnCancel;
    RatingBar rbPunctuality, rbTechnicianCapability, rbCustomerCentricity, rbOverAllExperience;
    TextView tvComplainNo, complainTitle, tvSeriolNo;
    EditText etRemark;

    String currentVersion = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }


        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config_Customer.isOnline(DashboardActivity.this);
        if (Config_Customer.internetStatus == true) {

            new ForceUpdateAsync(currentVersion).execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", DashboardActivity.this);
            finish();
        }


        String PendingFeedBack = Config_Customer.getSharedPreferences(DashboardActivity.this, "pref_Customer", "PendingFeedback", "");
        if (PendingFeedBack.compareTo("1") == 0) {
            new PendingFeedbackAsync().execute();
        }

        btn_arrange_call = findViewById(R.id.btn_arrange_call);
        btn_call_us = findViewById(R.id.btn_call_us);
        btn_raise_complaint = findViewById(R.id.btn_raise_complaint);
        btn_complaint = findViewById(R.id.btn_complaint);
        btn_machines = findViewById(R.id.btn_machines);
        btn_feedback = findViewById(R.id.btn_feedback);


        btn_arrange_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, ArrangeCallActivity.class);
                startActivity(i);
            }
        });

        btn_call_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(DashboardActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(DashboardActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
            }
        });

        btn_raise_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, RaiseComplaintActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, ComplaintsActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_machines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, MachinesActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, FeedbackActivity.class);
                startActivity(i);
                finish();
            }
        });


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
                intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(DashboardActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(DashboardActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }

                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(DashboardActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(DashboardActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(DashboardActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(DashboardActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(DashboardActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(DashboardActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(DashboardActivity.this);
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

    public class PendingFeedbackAsync extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DashboardActivity.this, "", "", true, false, null);
        }

        @Override
        protected String doInBackground(String... params) {

            String feedBackList = "", msgstatus = "";

            String SOAP_ACTION1 = "http://cfcs.co.in/AppCustomerPendingFeedback";
            String NAMESPACE = "http://cfcs.co.in/";
            String METHOD_NAME1 = "AppCustomerPendingFeedback";
            String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

            String ContactPersonId = Config_Customer.getSharedPreferences(
                    DashboardActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(
                    DashboardActivity.this, "pref_Customer", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;

                if (result != null) {
                    feedBackList = result.getProperty(0).toString();

                    JSONArray jsonArray = new JSONArray(feedBackList);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("MsgNotification")) {
                        msgstatus = jsonObject.getString("MsgNotification");
                        //flag = 1;
                    } else {
                        ComplainNo = new String[jsonArray.length()];
                        MachineSerialNo = new String[jsonArray.length()];
                        ComplaintTitle = new String[jsonArray.length()];
                        // EngineerName = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                PendingFeedbackDataModel pendingbean = new PendingFeedbackDataModel();
                                pendingbean.setComplainNo(jsonObject1.getString("ComplainNo").toString());
                                pendingbean.setSerialNo(jsonObject1.getString("MachineSerialNo").toString());
                                pendingbean.setComplaintTitleName(jsonObject1.getString("ComplaintTitle").toString());
                                pendingbean.setEngName(jsonObject1.getString("EngineerName"));
                                // Add this object into the ArrayList myList
                                feedValue.push(jsonObject1.getString("ComplainNo"));
                                feedbackList.add(pendingbean);
                                //flag = 2;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if (feedbackList.size() > 0) {
                showFeedbackPopUp();
            }
        }
    }

    public void showFeedbackPopUp() {

        Log.e("feedValue", "cfcs " + feedValue.peek());

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        TextView myMsg = new TextView(this);
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.feedback_popup, null);
        dialog = alertDialog.create();

        dialog.setView(dialogView);

        myMsg.setText("Complaint FeedBack");
        myMsg.setGravity(Gravity.CENTER);
        myMsg.setTextSize(16);
        myMsg.setPadding(8, 8, 8, 8);
        myMsg.setTextColor(Color.parseColor("#FFFFFF"));
        myMsg.setBackgroundColor(Color.parseColor("#396999"));
        dialog.setCustomTitle(myMsg);

        dialog.setCanceledOnTouchOutside(false);

        TextView engName;
        btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        engName = (TextView) dialogView.findViewById(R.id.eng_nametxt);

        rbPunctuality = (RatingBar) dialogView.findViewById(R.id.rbPunctuality);
        LayerDrawable PunctualityStars = (LayerDrawable) rbPunctuality.getProgressDrawable();
        PunctualityStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        PunctualityStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        PunctualityStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        rbTechnicianCapability = (RatingBar) dialogView.findViewById(R.id.rbTechnicianCapability);
        LayerDrawable TechnicianCapabilityStars = (LayerDrawable) rbTechnicianCapability.getProgressDrawable();
        TechnicianCapabilityStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        TechnicianCapabilityStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        TechnicianCapabilityStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        rbCustomerCentricity = (RatingBar) dialogView.findViewById(R.id.rbCustomerCentricity);
        LayerDrawable CustomerCentricityStars = (LayerDrawable) rbCustomerCentricity.getProgressDrawable();
        CustomerCentricityStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        CustomerCentricityStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        CustomerCentricityStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

        rbOverAllExperience = (RatingBar) dialogView.findViewById(R.id.rbOverAllExperience);
        LayerDrawable OverAllExperienceStars = (LayerDrawable) rbOverAllExperience.getProgressDrawable();
        OverAllExperienceStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        OverAllExperienceStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        OverAllExperienceStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);


        etRemark = (EditText) dialogView.findViewById(R.id.etRemark);
        tvComplainNo = (TextView) dialogView.findViewById(R.id.tvComplainNo);
        complainTitle = (TextView) dialogView.findViewById(R.id.complainTitle);
        tvSeriolNo = (TextView) dialogView.findViewById(R.id.tvSeriolNo);
        //tvComplainNo.setText(""+feedValue.peek());

        if (feedbackList.size() > 0) {
            complainTitle.setText("" + feedbackList.get(0).getComplaintTitleName());
            tvComplainNo.setText("[ " + feedbackList.get(0).getComplainNo() + " ]");
            tvSeriolNo.setText("[ " + feedbackList.get(0).getSerialNo() + " ]");
            engName.setText(feedbackList.get(0).getEngName());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float PunctualityRating = 0.0f, TechnicianCapabilityRating = 0.0f, CustomerCentricityRating = 0.0f, OverAllExperienceRating = 0.0f;

                String remark = "";
                PunctualityRating = rbPunctuality.getRating();
                TechnicianCapabilityRating = rbTechnicianCapability.getRating();
                CustomerCentricityRating = rbCustomerCentricity.getRating();
                OverAllExperienceRating = rbOverAllExperience.getRating();
                remark = etRemark.getText().toString();

                if (PunctualityRating == 0.0 || TechnicianCapabilityRating == 0.0 || CustomerCentricityRating == 0.0 || OverAllExperienceRating == 0.0) {
                    Config_Customer.alertBox("Please rate all field !! ", DashboardActivity.this);
                } else {

                    if (PunctualityRating >= 3.0 && TechnicianCapabilityRating >= 3.0 && CustomerCentricityRating >= 3.0 && OverAllExperienceRating >= 3.0) {
                        String feedbackValue[] = {feedbackList.get(0).getComplainNo(), "" + PunctualityRating, "" + TechnicianCapabilityRating,
                                "" + CustomerCentricityRating, "" + OverAllExperienceRating, remark};
                        new ComplainFeedAsync(DashboardActivity.this).execute(feedbackValue);

                    } else {
                        if (remark.compareTo("") == 0 || remark.isEmpty()) {
                            Config_Customer.alertBox("Please write remark ", DashboardActivity.this);
                        } else {
                            String feedbackValue[] = {feedbackList.get(0).getComplainNo(), "" + PunctualityRating, "" + TechnicianCapabilityRating,
                                    "" + CustomerCentricityRating, "" + OverAllExperienceRating, remark};
                            new ComplainFeedAsync(DashboardActivity.this).execute(feedbackValue);
                        }

                    }
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedValue.empty();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public class ComplainFeedAsync extends AsyncTask<String, String, String> {

        private String SOAP_ACTION1 = "http://cfcs.co.in/AppFeedbackInsUpdt";
        private String NAMESPACE = "http://cfcs.co.in/";
        private String METHOD_NAME1 = "AppFeedbackInsUpdt";
        private String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";


        String ContactPersonId = "", AuthCode = "", jsonValue = "", ComplainNo = "", PunctualityRating = "", TechnicianCapabilityRating = "", CustomerCentricityRating = "", OverAllExperienceRating = "", remark = "";
        int flag;
        Context mContext;
        ProgressDialog progressDialog;
        String LStatus;
        String msgstatus;

        String success = "success";

        public ComplainFeedAsync(Context context) {
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
                    LStatus = jsonObject.getString("status");
                    msgstatus = jsonObject.getString("MsgNotification");
                    if (LStatus.equals(success)) {
                        flag = 2;
                        updtStatus = true;
                    } else {
                        flag = 1;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag == 1) {
                Config_Customer.toastShow(jsonValue, mContext);
            } else {
                if (flag == 2) {
                    Config_Customer.toastShow(msgstatus, mContext);

                    if (updtStatus == true) {
                        //if (feedValue.size() > 0) {
                        if (feedbackList.size() > 0) {
                            dialog.setView(dialogView);
                            feedbackList.remove(0);
                            if (feedbackList.size() > 0) {
                                complainTitle.setText("" + feedbackList.get(0).getComplaintTitleName());
                                tvComplainNo.setText("[ " + feedbackList.get(0).getComplainNo() + " ]");
                                tvSeriolNo.setText("[ " + feedbackList.get(0).getSerialNo() + " ]");

                                rbPunctuality.setRating(0);
                                rbTechnicianCapability.setRating(0);
                                rbCustomerCentricity.setRating(0);
                                rbOverAllExperience.setRating(0);
                                etRemark.setText("");
                            } else {
                                Config_Customer.toastShow("Thanks for your valuable Feedback", DashboardActivity.this);
                                dialog.dismiss();
                            }

                        } else {
                            Config_Customer.toastShow("Thanks for your valuable Feedback", DashboardActivity.this);
                            dialog.dismiss();
                        }
                    }
                } else {
                    Config_Customer.toastShow(jsonValue, mContext);
                }
            }
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config_Customer.putSharedPreferences(DashboardActivity.this, "pref_Customer", "PendingFeedback", "0");
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;

        public ForceUpdateAsync(String currentVersion) {
            this.currentVersion = currentVersion;

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null && !latestVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(latestVersion)) {
                    //show dialog
                    showForceUpdateDialog();
                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog() {
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(DashboardActivity.this,
                    R.style.AppTheme));

            alertDialogBuilder.setTitle(DashboardActivity.this.getString(R.string.youAreNotUpdatedTitle));
            alertDialogBuilder.setMessage(DashboardActivity.this.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + DashboardActivity.this.getString(R.string.youAreNotUpdatedMessage1));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.update1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DashboardActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + DashboardActivity.this.getPackageName())));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }

}
