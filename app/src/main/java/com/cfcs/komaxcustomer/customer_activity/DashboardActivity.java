package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.animation.Animator;
import android.app.ActionBar;
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
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.broadcastReciever.AutoNofity;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.PendingFeedbackDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

import static com.cfcs.komaxcustomer.utils.IStringConstant.NoInternetConnection;
import static com.cfcs.komaxcustomer.utils.IStringConstant.invalid;
import static java.security.AccessController.getContext;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

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
    RatingBar rbOverAllresponseTime,rbPunctuality, rbTechnicianCapability, rbCustomerCentricity, rbOverAllExperience;
    TextView tvComplainNo, complainTitle, tvSeriolNo;
    EditText etRemark;

    String currentVersion = null;
    View view;
    TextView cart_badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_item_layout);
            view =Objects.requireNonNull(getSupportActionBar()).getCustomView();
        }


        AppCompatImageView menu_action_bar = view.findViewById(R.id.menu_action_bar);
        cart_badge = view.findViewById(R.id.cart_badge);

        AppCompatImageView cart_image = view.findViewById(R.id.cart_image);

        menu_action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, v);
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

                popupMenu.setOnMenuItemClickListener(DashboardActivity.this);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.show();



            }
        });


        cart_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this,ComplaintsActivity.class);
                startActivity(i);
            }
        });





        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config_Customer.isOnline(DashboardActivity.this);
        if (Config_Customer.internetStatus == true) {

            new ForceUpdateAsync(currentVersion).execute();
            new PendingFeedbackAsync().execute();

        } else {
            Config_Customer.toastShow(NoInternetConnection, DashboardActivity.this);
            finish();
        }

 //       new PendingFeedbackAsync().execute();
//        String PendingFeedBack = Config_Customer.getSharedPreferences(DashboardActivity.this, "pref_Customer", "PendingFeedback", "1");
//        if (PendingFeedBack.compareTo("1") == 0) {
//            new PendingFeedbackAsync().execute();
//        }

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
                int currentapiVersion = Build.VERSION.SDK_INT;
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
    public void onClick(View v) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Config_Customer.menuNavigation(DashboardActivity.this, item);
        return false;
    }


    public class PendingFeedbackAsync extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        String FeedbackDetail = "",complaintCount = "";
        int flag = 0;
        String LoginStatus;
        String msgstatus1;
        String ComplainCount;

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

                    Object json = new JSONTokener(feedBackList).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(feedBackList);
                        JSONArray complainDetailArray = object.getJSONArray("Feedback");
                        FeedbackDetail = complainDetailArray.toString();
                        JSONArray complaintCount = object.getJSONArray("ComplaintCount");
                        JSONObject jsonObject = complaintCount.getJSONObject(0);
                        ComplainCount = jsonObject.getString("OpenComplaint").toString();
                      // txt_complain_no.setText(ComplainCount);

                        if(FeedbackDetail.compareTo("[]") != 0){
                            JSONArray jsonArray = new JSONArray(FeedbackDetail);
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

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        flag = 2;


                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(feedBackList);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        msgstatus1 = jsonObject.getString("MsgNotification");
                        if (jsonObject.has("status")) {

                            LoginStatus = jsonObject.getString("status");
                            msgstatus1 = jsonObject.getString("MsgNotification");
                            if (LoginStatus.equals(invalid)) {

                                flag = 4;
                            } else {

                                flag = 1;
                            }
                        }

                    }

                } else {
                    JSONArray jsonArray = new JSONArray(feedBackList);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    msgstatus1 = jsonObject.getString("MsgNotification");
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

            if(flag == 1){

                Config_Customer.toastShow(msgstatus1,DashboardActivity.this);

            }else if (flag == 2){

                if (feedbackList.size() > 0) {
                    showFeedbackPopUp();
                }
                cart_badge.setText(ComplainCount);
                Config_Customer.putSharedPreferences(DashboardActivity.this, "pref_Customer", "ComplaintCount", ComplainCount);

            }else if (flag == 3){

                Config_Customer.toastShow(msgstatus1,DashboardActivity.this);
            }else if(flag == 4){

                Config_Customer.toastShow(msgstatus1, DashboardActivity.this);
                Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

            progressDialog.dismiss();

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

        rbOverAllresponseTime = (RatingBar) dialogView.findViewById(R.id.rbOverAllresponseTime);
        LayerDrawable OverAllresponseTimeStars = (LayerDrawable) rbOverAllresponseTime.getProgressDrawable();
        OverAllresponseTimeStars.getDrawable(2).setColorFilter(Color.parseColor("#CFB53B"), PorterDuff.Mode.SRC_ATOP);
        OverAllresponseTimeStars.getDrawable(0).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);
        OverAllresponseTimeStars.getDrawable(1).setColorFilter(Color.parseColor("#808080"), PorterDuff.Mode.SRC_ATOP);

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

                float ResponseTimeRating = 0.0f,PunctualityRating = 0.0f, TechnicianCapabilityRating = 0.0f, CustomerCentricityRating = 0.0f, OverAllExperienceRating = 0.0f;

                String remark = "";
                ResponseTimeRating = rbOverAllresponseTime.getRating();
                PunctualityRating = rbPunctuality.getRating();
                TechnicianCapabilityRating = rbTechnicianCapability.getRating();
                CustomerCentricityRating = rbCustomerCentricity.getRating();
                OverAllExperienceRating = rbOverAllExperience.getRating();
                remark = etRemark.getText().toString();

                if (ResponseTimeRating == 0.0 || PunctualityRating == 0.0 || TechnicianCapabilityRating == 0.0 || CustomerCentricityRating == 0.0 || OverAllExperienceRating == 0.0) {
                    Config_Customer.alertBox("Please rate all field !! ", DashboardActivity.this);
                } else {

                    if (ResponseTimeRating >= 3.0 && PunctualityRating >= 3.0 && TechnicianCapabilityRating >= 3.0 && CustomerCentricityRating >= 3.0 && OverAllExperienceRating >= 3.0) {
                        String feedbackValue[] = {feedbackList.get(0).getComplainNo(),"" + ResponseTimeRating, "" + PunctualityRating, "" + TechnicianCapabilityRating,
                                "" + CustomerCentricityRating, "" + OverAllExperienceRating, remark};
                        new ComplainFeedAsync(DashboardActivity.this).execute(feedbackValue);

                    } else {
                        if (remark.compareTo("") == 0 || remark.isEmpty()) {
                            Config_Customer.alertBox("Please write remark ", DashboardActivity.this);
                        } else {
                            String feedbackValue[] = {feedbackList.get(0).getComplainNo(),"" + ResponseTimeRating, "" + PunctualityRating, "" + TechnicianCapabilityRating,
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


        String ContactPersonId = "", AuthCode = "", jsonValue = "", ComplainNo = "",ResponseTimeRating = "", PunctualityRating = "", TechnicianCapabilityRating = "", CustomerCentricityRating = "", OverAllExperienceRating = "", remark = "";
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
            ResponseTimeRating = params[1];
            PunctualityRating = params[2];
            TechnicianCapabilityRating = params[3];
            CustomerCentricityRating = params[4];
            OverAllExperienceRating = params[5];
            remark = params[6];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("ComplainNo", ComplainNo);
            request.addProperty("ResponseTime", "" + ResponseTimeRating);
            request.addProperty("Punctuality", "" + PunctualityRating);
            request.addProperty("TechnicianCapability", "" + TechnicianCapabilityRating);
            request.addProperty("BehaviourCommunication", "" + CustomerCentricityRating);
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

                    if (updtStatus) {
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
