package com.cfcs.komaxcustomer.customer_activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.utils.IStringConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class ServiceReportDetail extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,IStringConstant {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppDailyReportdetails";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppDailyReportdetails";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    String dailyreportNo;

    TextView txt_work_done_plant, txt_work_detail, txt_suggestion, txt_next_follow_up, txt_reason_close, txt_travel_cost,
            txt_other_exp, txt_travel_time, txt_service_time, txt_engg_exp_detail, txt_sign_by_name, txt_sign_by_mobile,
            txt_customer_remark, txt_sign_by_email, txt_service_charge,txt_daily_report_print;

    LinearLayout add_card_view_spare_part;

    Bitmap bitmap = null;

    ImageView imv_signature;

    String complainno;

    LinearLayout maincontainer;

    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_report_detail);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_item_layout);
            view =Objects.requireNonNull(getSupportActionBar()).getCustomView();
        }


        AppCompatImageView menu_action_bar = view.findViewById(R.id.menu_action_bar);
        TextView cart_badge = view.findViewById(R.id.cart_badge);

        AppCompatImageView cart_image = view.findViewById(R.id.cart_image);

        String ComplaintCount = Config_Customer.getSharedPreferences(ServiceReportDetail.this, "pref_Customer", "ComplaintCount", "");
        cart_badge.setText(ComplaintCount);

        menu_action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(ServiceReportDetail.this, v);
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

                popupMenu.setOnMenuItemClickListener(ServiceReportDetail.this);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.show();

            }
        });

        cart_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServiceReportDetail.this,ComplaintsActivity.class);
                startActivity(i);
            }
        });




        txt_work_done_plant = findViewById(R.id.txt_work_done_plant);
        txt_work_detail = findViewById(R.id.txt_work_detail);
        txt_suggestion = findViewById(R.id.txt_suggestion);
        txt_next_follow_up = findViewById(R.id.txt_next_follow_up);
        txt_reason_close = findViewById(R.id.txt_reason_close);


        txt_sign_by_name = findViewById(R.id.txt_sign_by_name);
        txt_sign_by_mobile = findViewById(R.id.txt_sign_by_mobile);
        txt_sign_by_email = findViewById(R.id.txt_sign_by_email);

        txt_customer_remark = findViewById(R.id.txt_customer_remark);
        txt_daily_report_print = findViewById(R.id.txt_daily_report_print);
        imv_signature = findViewById(R.id.imv_signature);
        maincontainer = findViewById(R.id.maincontainer);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dailyreportNo = getIntent().getExtras().getString("DailyReportNo");
            complainno = getIntent().getExtras().getString("ComplainNo");
        }

        Config_Customer.isOnline(ServiceReportDetail.this);
        if (Config_Customer.internetStatus) {

            new DailyReportDetailAsy().execute();

        } else {
            Config_Customer.toastShow(NoInternetConnection, ServiceReportDetail.this);
        }


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Config_Customer.menuNavigation(ServiceReportDetail.this,item);
        return false;
    }


    private class DailyReportDetailAsy extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String dailyReport_detail_value, DailyReportdetail;
        String SparesConsumed;
        ProgressDialog progressDialog;
        String LoginStatus;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ServiceReportDetail.this, "Loading...", "Please Wait....", true, false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String ContactPersonId = Config_Customer.getSharedPreferences(
                    ServiceReportDetail.this, "pref_Customer", "ContactPersonId", "");

            String AuthCode = Config_Customer.getSharedPreferences(ServiceReportDetail.this,
                    "pref_Customer", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonID", ContactPersonId);
            request.addProperty("DailyReportNo", dailyreportNo);
            request.addProperty("AuthCode", AuthCode);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    dailyReport_detail_value = result.getProperty(0).toString();

                    Object json = new JSONTokener(dailyReport_detail_value).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(dailyReport_detail_value);
                        JSONArray dailyReportdetail = object.getJSONArray("DailyReportdetail");
                        DailyReportdetail = dailyReportdetail.toString();
                        JSONArray sparesConsumed = object.getJSONArray("SparesConsumed");
                        SparesConsumed = sparesConsumed.toString();
                        if (dailyReport_detail_value.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(dailyReport_detail_value);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;
                        } else {
                            flag = 2;
                            JSONObject jsonObject = dailyReportdetail.getJSONObject(0);
                            String signUrl = jsonObject.getString("SignatureImage1").toString();
                            setSign(Config_Customer.BASE_URL + signUrl);
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(dailyReport_detail_value);
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
                    JSONArray jsonArray = new JSONArray(dailyReport_detail_value);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    msgstatus = jsonObject.getString("MsgNotification");
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ServiceReportDetail.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(DailyReportdetail);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String DailyReportPrintNo = jsonObject.getString("DailyReportPrintNo").toString();
                    txt_daily_report_print.setText("Report No :" +" "+DailyReportPrintNo);

                    String Workdone = jsonObject.getString("Workdone").toString();
                    txt_work_done_plant.setText(Workdone);

                    String WorkDetail = jsonObject.getString("WorkDetail").toString();
                    txt_work_detail.setText(WorkDetail);

                    String Suggestion = jsonObject.getString("Suggestion").toString();
                    txt_suggestion.setText(Suggestion);

                    String NextFollowUpTimeText = jsonObject.getString("NextFollowUpTimeText").toString();
                    String NextFollowUpDateText = jsonObject.getString("NextFollowUpDateText").toString();
                    txt_next_follow_up.setText(NextFollowUpDateText + " " + NextFollowUpTimeText);

                    String ReasonForNotClose = jsonObject.getString("ReasonForNotClose").toString();
                    txt_reason_close.setText(ReasonForNotClose);

                    String SignByName = jsonObject.getString("SignByName").toString();
                    txt_sign_by_name.setText(SignByName);

                    String SignByMobile = jsonObject.getString("SignByMobile").toString();
                    txt_sign_by_mobile.setText(SignByMobile);

                    String SignByMailID = jsonObject.getString("SignByMailID").toString();
                    txt_sign_by_email.setText(SignByMailID);

                    String CustomerRemark = jsonObject.getString("CustomerRemark").toString();
                    txt_customer_remark.setText(CustomerRemark);


                    imv_signature.setImageBitmap(bitmap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    if (SparesConsumed.compareTo("[]") != 0){

                        add_card_view_spare_part = findViewById(R.id.add_card_view_spare_part);

                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lparams.setMargins(50, 8, 50, 8);
                        CardView cardView = new CardView(ServiceReportDetail.this);
                        cardView.setLayoutParams(lparams);
                        // Set CardView corner radius
                        cardView.setRadius(5);
                        // Set cardView content padding
                        // cardView.setContentPadding(15, 8, 15, 8);
                        // Set a background color for CardView
                        //  cardView.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
                        // Set the CardView maximum elevation
                        // cardView.setMaxCardElevation(20);
                        cardView.setClickable(true);
                        // Set CardView elevation
                        cardView.setCardElevation(20);
                        add_card_view_spare_part.addView(cardView);

                        LinearLayout parentInCardView = new LinearLayout(ServiceReportDetail.this);
                        parentInCardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        parentInCardView.setOrientation(LinearLayout.VERTICAL);
                        parentInCardView.setPadding(20, 20, 20, 20);
                        cardView.addView(parentInCardView);

                        LinearLayout childSpareDetail = new LinearLayout(ServiceReportDetail.this);
                        childSpareDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        childSpareDetail.setOrientation(LinearLayout.HORIZONTAL);
                        parentInCardView.addView(childSpareDetail);

                        LinearLayout.LayoutParams Textparams = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 3.3f);

                        TextView SNo = new TextView(ServiceReportDetail.this);
                        SNo.setLayoutParams(Textparams);
                        SNo.setText("SNo");
                        SNo.setTypeface(null, Typeface.BOLD);

                        LinearLayout.LayoutParams Textparams2 = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 3.3f);

                        TextView Part_No = new TextView(ServiceReportDetail.this);
                        Part_No.setLayoutParams(Textparams2);
                        Part_No.setText("Part No");
                        Part_No.setTypeface(null, Typeface.BOLD);


                        TextView Qty = new TextView(ServiceReportDetail.this);
                        Qty.setLayoutParams(Textparams);
                        Qty.setText("Qty");
                        Qty.setTypeface(null, Typeface.BOLD);

                        childSpareDetail.addView(SNo);
                        childSpareDetail.addView(Part_No);
                        childSpareDetail.addView(Qty);

                        JSONArray jsonArray = new JSONArray(SparesConsumed);
                        //  JSONObject jsonObject = jsonArray.getJSONObject(0);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String DailyReportNo = jsonObject2.getString("DailyReportNo");
                            String SpareID = jsonObject2.getString("SpareID");
                            String PartNo = jsonObject2.getString("PartNo");
                            String SpareDesc = jsonObject2.getString("SpareDesc");
                            String SpareQuantity = jsonObject2.getString("SpareQuantity");
                            String OrderBy = jsonObject2.getString("OrderBy");
                            String SpareDate = jsonObject2.getString("SpareDate");

                            LinearLayout.LayoutParams forMarginTop = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            forMarginTop.setMargins(0, 10, 0, 0);

                            LinearLayout childSpareDetailDynamic = new LinearLayout(ServiceReportDetail.this);
                            childSpareDetailDynamic.setLayoutParams(forMarginTop);
                            childSpareDetailDynamic.setOrientation(LinearLayout.HORIZONTAL);
                            parentInCardView.addView(childSpareDetailDynamic);

                            LinearLayout.LayoutParams forMarginBottom = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            forMarginBottom.setMargins(0, 10, 0, 0);

                            LinearLayout childSpareDetailDecs = new LinearLayout(ServiceReportDetail.this);
                            childSpareDetailDecs.setLayoutParams(forMarginBottom);
                            childSpareDetailDecs.setOrientation(LinearLayout.HORIZONTAL);
                            parentInCardView.addView(childSpareDetailDecs);

                            LinearLayout.LayoutParams Textparams3 = new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 3.3f);

                            TextView SNo1 = new TextView(ServiceReportDetail.this);
                            SNo1.setLayoutParams(Textparams3);
                            SNo1.setText(SpareID);
                            //  SNo1.setTypeface(null, Typeface.BOLD);

                            LinearLayout.LayoutParams Textparams4 = new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 3.3f);
                            Textparams4.setMargins(0, 10, 0, 10);

                            TextView Part_No1 = new TextView(ServiceReportDetail.this);
                            Part_No1.setLayoutParams(Textparams4);
                            Part_No1.setText(PartNo);
                            // Part_No1.setTypeface(null, Typeface.BOLD);

                            TextView Qty1 = new TextView(ServiceReportDetail.this);
                            Qty1.setLayoutParams(Textparams3);
                            Qty1.setText(SpareQuantity);
                            // Qty1.setTypeface(null, Typeface.BOLD);

                            LinearLayout.LayoutParams Textparams6 = new LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                            // Textparams6.setMargins(0,0,0,10);

                            TextView DescText = new TextView(ServiceReportDetail.this);
                            DescText.setLayoutParams(Textparams6);
                            DescText.setText("Detail : " + SpareDesc);

                            LinearLayout.LayoutParams Textparams7 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
                            Textparams7.setMargins(0, 5, 0, 0);

                            View view001 = new View(ServiceReportDetail.this);
                            view001.setLayoutParams(Textparams7);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                view001.setElevation(2);
                            }
                            view001.setBackgroundColor(Color.parseColor("#e0e0e0"));

                            childSpareDetailDynamic.addView(SNo1);
                            childSpareDetailDynamic.addView(Part_No1);
                            childSpareDetailDynamic.addView(Qty1);

                            childSpareDetailDecs.addView(DescText);

                            parentInCardView.addView(view001);

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", ServiceReportDetail.this);

            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, ServiceReportDetail.this);
                Config_Customer.logout(ServiceReportDetail.this);
                Config_Customer.putSharedPreferences(ServiceReportDetail.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {

                ScanckBar();
                progressDialog.dismiss();
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
                        Config_Customer.isOnline(ServiceReportDetail.this);
                        if (Config_Customer.internetStatus) {

                            new DailyReportDetailAsy().execute();

                        } else {
                            Config_Customer.toastShow(NoInternetConnection, ServiceReportDetail.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();

    }

    private void setSign(String s) {
        Log.e("signUrl", "cfcs " + s);
        try {
            InputStream inputStream = new java.net.URL(s).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ServiceReportDetail.this, ServiceReport.class);
        intent.putExtra("ComplainNo", complainno);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
