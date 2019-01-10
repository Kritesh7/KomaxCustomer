package com.cfcs.komaxcustomer.customer_activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.adapters.ServiceReportListAdapter;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.DailyReportDataModel;
import com.cfcs.komaxcustomer.utils.IStringConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class ServiceReport extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,IStringConstant {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppDailyReportList";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppDailyReportList";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    ArrayList<DailyReportDataModel> dailyReportList = new ArrayList<DailyReportDataModel>();

    String[] DailyReportNo;
    String[] Workdone;
    String[] ComplainNo;
    String[] DailyReportDateText;
    String[] Workdone1;
    String[] NextFollowUpDateText;
    String[] NextFollowUpTimeText;
    String[] Traveltime;
    String[] Servicetime;
    String[] IsDelete;
    String[] IsEdit;

    FloatingActionButton fab, fab1, fab2;
    LinearLayout fabLayout1, fabLayout2;
    View fabBGLayout;
    boolean isFABOpen = false;
    String complainno;
    ListView list;
    CoordinatorLayout maincontainer;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_report);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_item_layout);
            view =Objects.requireNonNull(getSupportActionBar()).getCustomView();
        }


        AppCompatImageView menu_action_bar = view.findViewById(R.id.menu_action_bar);
        TextView cart_badge = view.findViewById(R.id.cart_badge);

        AppCompatImageView cart_image = view.findViewById(R.id.cart_image);

        String ComplaintCount = Config_Customer.getSharedPreferences(ServiceReport.this, "pref_Customer", "ComplaintCount", "");
        cart_badge.setText(ComplaintCount);

        menu_action_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(ServiceReport.this, v);
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

                popupMenu.setOnMenuItemClickListener(ServiceReport.this);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.show();

            }
        });

        cart_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServiceReport.this,ComplaintsActivity.class);
                startActivity(i);
            }
        });

        list = findViewById(R.id.list_view_service_report);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            complainno = getIntent().getExtras().getString("ComplainNo");
        }

        Config_Customer.isOnline(ServiceReport.this);
        if (Config_Customer.internetStatus) {

            new DailyReportAsy().execute();

        } else {
            Config_Customer.toastShow(NoInternetConnection, ServiceReport.this);
        }


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Config_Customer.menuNavigation(ServiceReport.this,item);
        return false;
    }


    private class DailyReportAsy extends AsyncTask<String, String, String> {

        int flag = 0;
        String DailyReportList;
        String LoginStatus;
        String msgstatus;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ServiceReport.this, "Lodaing", "Please wait...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            String ContactPersonId = Config_Customer.getSharedPreferences(
                    ServiceReport.this, "pref_Customer", "ContactPersonId", "");

            String AuthCode = Config_Customer.getSharedPreferences(ServiceReport.this,
                    "pref_Customer", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonID", ContactPersonId);
            request.addProperty("ComplainNo", complainno);
            request.addProperty("AuthCode", AuthCode);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    DailyReportList = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(DailyReportList);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {
                            flag = 4;
                        } else {
                            flag = 1;
                        }
                    } else {

                        dailyReportList.clear();
                        DailyReportNo = new String[jsonArray.length()];
                        Workdone = new String[jsonArray.length()];
                        ComplainNo = new String[jsonArray.length()];
                        DailyReportDateText = new String[jsonArray.length()];
                        Workdone1 = new String[jsonArray.length()];
                        NextFollowUpDateText = new String[jsonArray.length()];
                        NextFollowUpTimeText = new String[jsonArray.length()];


                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject1 = jsonArray
                                        .getJSONObject(i);

                                DailyReportDataModel dailyReportDataModel = new DailyReportDataModel(AuthCode, AuthCode, AuthCode, AuthCode,
                                        AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode,AuthCode);
                                dailyReportDataModel.setDailyReportNo(jsonObject1.getString("DailyReportNo").toString());
                                dailyReportDataModel.setDailyReportPrintNo(jsonObject1.getString("DailyReportPrintNo").toString());
                                dailyReportDataModel.setWorkdone(jsonObject1.getString("Workdone").toString());
                                dailyReportDataModel.setComplainNo(jsonObject1.getString("ComplainNo").toString());
                                dailyReportDataModel.setDailyReportDateText(jsonObject1.getString("DailyReportDateText").toString());
                                dailyReportDataModel.setWorkdone1(jsonObject1.getString("Workdone1").toString());
                                dailyReportDataModel.setNextFollowUpDateText(jsonObject1.getString("NextFollowUpDateText").toString());
                                dailyReportDataModel.setNextFollowUpTimeText(jsonObject1.getString("NextFollowUpTimeText").toString());


                                // Add this object into the ArrayList myList

                                dailyReportList.add(dailyReportDataModel);
                                flag = 2;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    flag = 3;
                    // finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error is 1 ", e.toString());
                flag = 5;

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (flag == 1) {

                Config_Customer.toastShow(msgstatus, ServiceReport.this);
                //  progressDialog.dismiss();
                list.setAdapter(null);
            } else {
                if (flag == 2) {
                    list.setAdapter(new ServiceReportListAdapter(ServiceReport.this, dailyReportList));
                } else {
                    if (flag == 3) {
                        Config_Customer.toastShow("No Response", ServiceReport.this);
                        //    progressDialog.dismiss();
                    } else {
                        if (flag == 4) {
                            Config_Customer.toastShow(msgstatus, ServiceReport.this);
                            Config_Customer.logout(ServiceReport.this);
                            Config_Customer.putSharedPreferences(ServiceReport.this, "checklogin", "status", "2");
                            finish();
                        } else if (flag == 5) {
                            ScanckBar();
                            progressDialog.dismiss();
                        }

                    }
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
                        Config_Customer.isOnline(ServiceReport.this);
                        if (Config_Customer.internetStatus) {

                            new DailyReportAsy().execute();

                        } else {
                            Config_Customer.toastShow(NoInternetConnection, ServiceReport.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ServiceReport.this, ComplaintsActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }





}
