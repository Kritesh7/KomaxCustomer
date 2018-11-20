package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.adapters.ComplainListAdapter;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.ComplainListDataModel;
import com.cfcs.komaxcustomer.models.EscalationDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComplaintsActivity extends AppCompatActivity {


    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppComplainList";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppComplainList";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    private String SOAP_ACTION2 = "http://cfcs.co.in/AppEscalationLevel";
    private String METHOD_NAME2 = "AppEscalationLevel";

    private static String SOAP_ACTION3 = "http://cfcs.co.in/AppddlPlantAndTransactionType";
    private static String METHOD_NAME3 = "AppddlPlantAndTransactionType";

    private static String SOAP_ACTION4 = "http://cfcs.co.in/AppddlMachineSale";
    private static String METHOD_NAME4 = "AppddlMachineSale";


    ArrayList<ComplainListDataModel> complainList = new ArrayList<ComplainListDataModel>();
    ArrayList<EscalationDataModel> escalationList = new ArrayList<EscalationDataModel>();
    ArrayList<ComplainListDataModel> arrayTemplist = new ArrayList<ComplainListDataModel>();
    String[] ComplainNo;
    String[] ComplaintTitle;
    String[] ComplainTimeText;
    String[] ModelName;
    String[] SiteAddress;
    String[] EngineerName;
    String[] PriorityName;
    String[] WorkStatusName;
    String[] TransactionTypeName;
    String[] EscalationID;
    String[] EscalationShortCode;
    String[] EscalationName;
    String[] StatusText;
    String[] IsServiceReport;
    String[] IsFeedback;
    String[] IsChangeEscalationLevel;


    List<String> siteIDList;
    List<String> siteNameList;

    List<String> machineID;
    List<String> machineName;

    List<String> regionIDList;
    List<String> regionNameList;

    List<String> statusIDList;
    List<String> statusNameList;


    Spinner spinner_plant;
    Spinner spinner_machine;
    Spinner spinner_region;
    Spinner spinner_status;

    List<String> spinnerlistMachineID;
    List<String> spinnerlistMachineName;
    List<String> spinnerlistregion;

    String SelctedPlantID;
    String SelectedMachine;

    Button btn_add_new_complaint;
    FloatingActionButton fab, fab1, fab2;
    LinearLayout fabLayout1, fabLayout2;
    View fabBGLayout;
    boolean isFABOpen = false;
    Calendar myCalendar;

    RecyclerView recyclerView;
    ListView list;

    EditText txt_complaint_no;

    ProgressDialog progressDialog;

    TextView txt_compalint_status_header;

    ArrayAdapter<String> spinneradapterPlant;

    int Plantcount = 0;

    CoordinatorLayout maincontainer;

    String status;

    Button btn_search_find, btn_search_clear;

    AlertDialog dialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "PlantNametest");

        Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "Model");

        Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "DateFrom");

        Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "DateUpto");

        Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "ComplainNo");

        Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "Status");


        list = (ListView) findViewById(R.id.list_view_complain);
        maincontainer = findViewById(R.id.maincontainer);

        status = "1";

        Config_Customer.isOnline(ComplaintsActivity.this);
        if (Config_Customer.internetStatus == true) {

            new ComplainListAsy(status).execute();
            new EscalationLevelAsync().execute();
            new SetdataInPlantSpinner().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplaintsActivity.this);
        }


        fab = findViewById(R.id.fab);
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fabBGLayout = findViewById(R.id.fabBGLayout);

        txt_compalint_status_header = findViewById(R.id.txt_compalint_status_header);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchPopByFab();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ComplaintsActivity.this, RaiseComplaintActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);

        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));

    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);

                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    private void showSearchPopByFab() {

        final TextView txt_complaint_date_from, txt_complaint_date_to;

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.add_search_complaints, null);
        alertDialog.setView(convertView);

        spinner_plant = convertView.findViewById(R.id.spinner_plant);
        spinner_machine = convertView.findViewById(R.id.spinner_machine);
        spinner_status = convertView.findViewById(R.id.spinner_status);

        txt_complaint_date_from = convertView.findViewById(R.id.txt_complaint_date_from);
        txt_complaint_date_to = convertView.findViewById(R.id.txt_complaint_date_to);
        txt_complaint_no = convertView.findViewById(R.id.txt_complaint_no);

        btn_search_find = convertView.findViewById(R.id.btn_search_find);

        btn_search_clear = convertView.findViewById(R.id.btn_search_clear);

        dialog = alertDialog.create();

        myCalendar = Calendar.getInstance();


        //  Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this,"pref_Customer","PlantName");

        String ComparedDateFrom = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "DateFrom", "");

        txt_complaint_date_from.setText(ComparedDateFrom);

        String ComparedDateTo = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "DateUpto", "");

        txt_complaint_date_to.setText(ComparedDateTo);

        String ComparedComplainNo = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "ComplainNo", "");

        txt_complaint_no.setText(ComparedComplainNo);


        String ComparedPlantName = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "PlantNametest", "");

        spinner_plant.setAdapter(spinneradapterPlant);


        if (!ComparedPlantName.equalsIgnoreCase("")) {

            int spinnerPosition = spinneradapterPlant.getPosition(ComparedPlantName);
            spinner_plant.setSelection(spinnerPosition);

        } else if (Plantcount == 1) {
            spinner_plant.setSelection(1);
        }


        // DatePicker Listener for Date From
        final DatePickerDialog.OnDateSetListener date_from = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateFrom();
            }

            private void updateDateFrom() {

                Date date1 = new Date(String.valueOf(myCalendar.getTime()));
                String stringDate = DateFormat.getDateInstance().format(date1);
                txt_complaint_date_from.setText(stringDate);

            }

        };

        // DatePicker Listener for Date to
        final DatePickerDialog.OnDateSetListener date_to = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateTo();
            }

            private void updateDateTo() {

                Date date1 = new Date(String.valueOf(myCalendar.getTime()));
                String stringDate = DateFormat.getDateInstance().format(date1);
                txt_complaint_date_to.setText(stringDate);

            }

        };


        // DatePicker Dialog Date From
        txt_complaint_date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                new DatePickerDialog(ComplaintsActivity.this, android.R.style.Theme_Holo_Dialog, date_from, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // DatePicker Dialog Date To
        txt_complaint_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                new DatePickerDialog(ComplaintsActivity.this, android.R.style.Theme_Holo_Dialog, date_to, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        String ComparedStatus = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "Status", "");


        statusIDList = new ArrayList<String>();
        statusIDList.add(0, "1");
        statusIDList.add(1, "0");
        statusIDList.add(2, "3");
        statusNameList = new ArrayList<String>();
        statusNameList.add(0, "Open");
        statusNameList.add(1, "Completed");
        statusNameList.add(2, "All");


        ArrayAdapter<String> spinneradapterStatus = new ArrayAdapter<String>(ComplaintsActivity.this,
                android.R.layout.simple_spinner_item, statusNameList);
        spinneradapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(spinneradapterStatus);


        if (!ComparedStatus.equalsIgnoreCase("")) {
            int spinnerpostion = spinneradapterStatus.getPosition(ComparedStatus);
            spinner_status.setSelection(spinnerpostion);

        }


        spinner_plant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Config_Customer.isOnline(ComplaintsActivity.this);
                if (Config_Customer.internetStatus == true) {

                    long SelectedPlant = adapterView.getSelectedItemId();
                    SelctedPlantID = siteIDList.get((int) SelectedPlant);
                    if (SelectedPlant != 0) {
                        new MachinesSpinnerAsy().execute();

                    } else {

                        machineID = new ArrayList<String>();
                        machineID.add(0, "");
                        machineName = new ArrayList<String>();
                        machineName.add(0, "Select");


                        ArrayAdapter<String> spinneradapterMachine = new ArrayAdapter<String>(ComplaintsActivity.this,
                                android.R.layout.simple_spinner_item, machineName);
                        spinneradapterMachine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_machine.setAdapter(spinneradapterMachine);

                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplaintsActivity.this);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_search_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String CustomerID = "";
                String SaleID = "";
                String MachineModel = "";
                String StatusID = "";

                Config_Customer.isOnline(ComplaintsActivity.this);
                if (Config_Customer.internetStatus == true) {

                    String complainNo = txt_complaint_no.getText().toString();

                    long saleID = spinner_plant.getSelectedItemId();
                    CustomerID = siteIDList.get((int) saleID);

                    long MachineID = spinner_machine.getSelectedItemId();
                    SaleID = machineID.get((int) MachineID);

                    String DateFrom = txt_complaint_date_from.getText().toString();
                    String DateUpto = txt_complaint_date_to.getText().toString();

                    long statusID = spinner_status.getSelectedItemId();
                    StatusID = statusIDList.get((int) statusID);


                    complainList.clear();
                    escalationList.clear();


                    Config_Customer.putSharedPreferences(ComplaintsActivity.this, "pref_Customer", "PlantNametest", spinner_plant.getSelectedItem().toString());

                    Config_Customer.putSharedPreferences(ComplaintsActivity.this, "pref_Customer", "Model", spinner_machine.getSelectedItem().toString());

                    Config_Customer.putSharedPreferences(ComplaintsActivity.this, "pref_Customer", "DateFrom", DateFrom);

                    Config_Customer.putSharedPreferences(ComplaintsActivity.this, "pref_Customer", "DateUpto", DateUpto);

                    Config_Customer.putSharedPreferences(ComplaintsActivity.this, "pref_Customer", "ComplainNo", complainNo);

                    Config_Customer.putSharedPreferences(ComplaintsActivity.this, "pref_Customer", "Status", spinner_status.getSelectedItem().toString());

                    if (statusID == 0) {

                        txt_compalint_status_header.setText("Open");
                    } else if (statusID == 1) {
                        txt_compalint_status_header.setText("Completed");
                    } else if (statusID == 2) {
                        txt_compalint_status_header.setText("All");
                    }

                    dialog.dismiss();

                    new ComplainListAsy(CustomerID, complainNo, SaleID, DateFrom, DateUpto, StatusID).execute();
                    new EscalationLevelAsync().execute();
                    closeFABMenu();

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplaintsActivity.this);
                }

            }
        });


        btn_search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "PlantNametest");

                Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "Model");

                Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "DateFrom");

                Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "DateUpto");

                Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "ComplainNo");

                Config_Customer.getSharedPreferenceRemove(ComplaintsActivity.this, "pref_Customer", "Status");

                txt_compalint_status_header.setText("Open");

                Config_Customer.isOnline(ComplaintsActivity.this);
                if (Config_Customer.internetStatus == true) {

                    dialog.dismiss();

                    new ComplainListAsy().execute();
                    new EscalationLevelAsync().execute();
                    closeFABMenu();

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplaintsActivity.this);
                }


            }
        });


        dialog.show();
    }


    private class ComplainListAsy extends AsyncTask<String, String, String> {

        String msgstatus;
        int flag;
        String statusbtn = "";
        String LoginStatus;
        String invalid = "LoginFailed";


        String ComplainList = "";
        String CustomerID = "";
        String SaleID = "";
        String DateFrom = "";
        String DateUpto = "";
        String complainNo = "";
        String status = "";
        String ZoneID = "";

        public ComplaintsActivity complaintsActivity;

        public ComplainListAsy(String status) {
            this.status = status;
        }


        public ComplainListAsy() {

        }

        public ComplainListAsy(String customerID, String complainNo, String saleID, String dateFrom, String dateUpto, String statusID) {

            this.CustomerID = customerID;
            this.complainNo = complainNo;
            this.SaleID = saleID;
            this.DateFrom = dateFrom;
            this.DateUpto = dateUpto;
            this.status = statusID;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ComplaintsActivity.this, "", "Please wait...", true, false, null);
        }

        @Override
        protected String doInBackground(String... status1) {
            // TODO Auto-generated method stub

            // String status = status1[0];
            int count = 0;

            String ContactPersonId = Config_Customer.getSharedPreferences(
                    ComplaintsActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(
                    ComplaintsActivity.this, "pref_Customer", "AuthCode", "");
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("CustomerID", CustomerID);
            request.addProperty("SaleID", SaleID);
            request.addProperty("DateFrom", DateFrom);
            request.addProperty("DateUpto", DateUpto);
            request.addProperty("ComplainNo", complainNo);
            request.addProperty("Status", status);
            request.addProperty("ZoneID", ZoneID);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    ComplainList = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(ComplainList);
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

                        complainList.clear();
                        escalationList.clear();

                        ComplainNo = new String[jsonArray.length()];
                        ComplaintTitle = new String[jsonArray.length()];
                        ComplainTimeText = new String[jsonArray.length()];
                        ModelName = new String[jsonArray.length()];
                        SiteAddress = new String[jsonArray.length()];
                        EngineerName = new String[jsonArray.length()];
                        PriorityName = new String[jsonArray.length()];
                        WorkStatusName = new String[jsonArray.length()];
                        TransactionTypeName = new String[jsonArray.length()];
                        EscalationID = new String[jsonArray.length()];
                        EscalationShortCode = new String[jsonArray.length()];
                        EscalationName = new String[jsonArray.length()];
                        StatusText = new String[jsonArray.length()];
                        IsServiceReport = new String[jsonArray.length()];
                        IsFeedback = new String[jsonArray.length()];
                        IsChangeEscalationLevel = new String[jsonArray.length()];


                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject1 = jsonArray
                                        .getJSONObject(i);

                                ComplainListDataModel complainListDataModel = new ComplainListDataModel(AuthCode, AuthCode, AuthCode,
                                        AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode);
                                complainListDataModel.setComplainNo(jsonObject1.getString("ComplainNo").toString());
                                complainListDataModel.setComplaintTitle(jsonObject1.getString("ComplaintTitle").toString());
                                complainListDataModel.setComplainTimeText(jsonObject1.getString("ComplainTimeText").toString());
                                complainListDataModel.setTransactionTypeName(jsonObject1.getString("TransactionTypeName").toString());
                                complainListDataModel.setModelName(jsonObject1.getString("ModelName").toString());
                                complainListDataModel.setSiteAddress(jsonObject1.getString("SiteAddress").toString());
                                complainListDataModel.setEngineerName(jsonObject1
                                        .getString("EngineerName")
                                        .toString());
                                complainListDataModel.setWorkStatusName(jsonObject1
                                        .getString("WorkStatusName")
                                        .toString());
                                complainListDataModel.setStatusText(jsonObject1
                                        .getString("StatusText")
                                        .toString());
                                complainListDataModel.setEscalationName(jsonObject1
                                        .getString("EscalationName")
                                        .toString());
                                complainListDataModel.setEscalationShortCode(jsonObject1
                                        .getString("EscalationShortCode")
                                        .toString());
                                complainListDataModel.setEscalationID(jsonObject1
                                        .getString("EscalationID")
                                        .toString());
                                complainListDataModel.setIsChangeEscalationLevel(jsonObject1
                                        .getString("IsChangeEscalationLevel")
                                        .toString());
                                complainListDataModel.setIsFeedback(jsonObject1
                                        .getString("IsFeedback")
                                        .toString());
                                complainListDataModel.setPriorityName(jsonObject1
                                        .getString("PriorityName")
                                        .toString());
                                complainListDataModel.setIsServiceReport(jsonObject1
                                        .getString("IsServiceReport")
                                        .toString());

                                // Add this object into the ArrayList myList

                                complainList.add(complainListDataModel);
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (flag == 1) {

                Config_Customer.toastShow(msgstatus, ComplaintsActivity.this);
                progressDialog.dismiss();
                list.setAdapter(null);

            } else {
                if (flag == 2) {
                    list.setAdapter(new ComplainListAdapter(ComplaintsActivity.this, complainList, escalationList, status));
                } else {
                    if (flag == 3) {
                        Config_Customer.toastShow("No Response", ComplaintsActivity.this);
                        progressDialog.dismiss();
                    } else {
                        if (flag == 4) {

                            Config_Customer.toastShow(msgstatus, ComplaintsActivity.this);
                            Intent i = new Intent(ComplaintsActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else if (flag == 5) {

                            ScanckBar();
                            progressDialog.dismiss();
                            list.setAdapter(null);

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
                        Config_Customer.isOnline(ComplaintsActivity.this);
                        if (Config_Customer.internetStatus == true) {

                            new ComplainListAsy(status).execute();
                            new EscalationLevelAsync().execute();
                            new SetdataInPlantSpinner().execute();

                        } else {
                            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", ComplaintsActivity.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    public class EscalationLevelAsync extends AsyncTask<String, String, String> {


        String EscalationList = "", msgstatus = "";
        int flag;
        ProgressDialog progressDialog;
        String[] EscalationID;
        String[] EscalationShortCode;
        String[] EscalationName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ComplaintsActivity.this, "Loading", "Please wait..", true, false);
        }

        @Override
        protected String doInBackground(String... params) {

            String ContactPersonId = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("ContactPersonId", ContactPersonId);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    EscalationList = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(EscalationList);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("MsgNotification")) {
                        msgstatus = jsonObject.getString("MsgNotification");
                        flag = 1;
                    } else {
                        EscalationID = new String[jsonArray.length()];
                        EscalationShortCode = new String[jsonArray.length()];
                        EscalationName = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                EscalationDataModel escalationDataModel = new EscalationDataModel();
                                escalationDataModel.setEscalationName(jsonObject1.getString("EscalationName").toString());
                                escalationDataModel.setEscalationID(jsonObject1.getString("EscalationID").toString());
                                escalationDataModel.setEscalationShortCode(jsonObject1.getString("EscalationShortCode").toString());

                                // Add this object into the ArrayList escalationList
                                escalationList.add(escalationDataModel);
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
                flag = 5;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag == 2) {

            } else if (flag == 5) {
                ScanckBar();
                list.setAdapter(null);
            }
            progressDialog.dismiss();
        }
    }

    private class SetdataInPlantSpinner extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String plant_ransaction_detail_value, plantList;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";


        String ComparedRegion = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "Region", "");
//       int CompareplantName12 = !ComparedPlantName.equals("")?Integer.parseInt(ComparedPlantName) :0;


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ComplaintsActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);
            String ContactPersonId = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "AuthCode", "");
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION3, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {

                    plant_ransaction_detail_value = result.getProperty(0).toString();
                    Object json = new JSONTokener(plant_ransaction_detail_value).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(plant_ransaction_detail_value);
                        JSONArray plantjsonArray = object.getJSONArray("PlantList");
                        plantList = plantjsonArray.toString();
                        if (plant_ransaction_detail_value.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(plant_ransaction_detail_value);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;
                        } else {
                            flag = 2;
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(plant_ransaction_detail_value);
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
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return null;
        }


        @Override
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ComplaintsActivity.this);
            } else if (flag == 2) {
                try {

                    JSONArray jsonArray2 = new JSONArray(plantList);
                    siteIDList = new ArrayList<String>();
                    siteIDList.add(0, "");
                    siteNameList = new ArrayList<String>();
                    siteNameList.add(0, "Select");


                    for (int i = 0; i < jsonArray2.length(); i++) {
                        Plantcount += 1;
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String SiteID = jsonObject2.getString("SiteID");
                        String SiteName = jsonObject2.getString("SiteName");


                        siteIDList.add(i + 1, SiteID);
                        //siteNameList.add(SiteName);
                        siteNameList.add(i + 1, SiteName);
                    }

                    spinneradapterPlant = new ArrayAdapter<String>(ComplaintsActivity.this, android.R.layout.simple_spinner_item, siteNameList);
                    spinneradapterPlant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }
                //  fillListDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", ComplaintsActivity.this);

            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, ComplaintsActivity.this);
                Intent i = new Intent(ComplaintsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();


            } else if (flag == 5) {
                ScanckBar();
                progressDialog.dismiss();
//                list.setAdapter(null);
            }
            progressDialog.dismiss();
        }
    }


    private class MachinesSpinnerAsy extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String machine_detail, machine_list;
        int count = 0;
        String LoginStatus;
        String invalid = "LoginFailed";

        ProgressDialog progressDialog;

        String ComparedModel = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "Model", "");

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(ComplaintsActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);
            String ContactPersonId = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "AuthCode", "");
            String PlantId = SelctedPlantID;
            String ModelID = "00000000-0000-0000-0000-000000000000";
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("CustomerID", PlantId);
            request.addProperty("ModelID", ModelID);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION4, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    machine_detail = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(machine_detail);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    machine_list = jsonArray.toString();
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
            return null;
        }

        @Override
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, ComplaintsActivity.this);
            } else if (flag == 2) {
                try {
                    // Add value in Plant List Status Spinner
                    JSONArray jsonArray2 = new JSONArray(machine_list);
                    machineID = new ArrayList<String>();
                    machineID.add(0, "");
                    machineName = new ArrayList<String>();
                    machineName.add(0, "Select");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        count += 1;
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String SaleID = jsonObject2.getString("SaleID");
                        String ModelName = jsonObject2.getString("ModelName");

                        machineID.add(i + 1, SaleID);
                        machineName.add(i + 1, ModelName);
                    }

                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(ComplaintsActivity.this,
                            android.R.layout.simple_spinner_item, machineName);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_machine.setAdapter(spinneradapter);


                    if (!ComparedModel.equalsIgnoreCase("")) {

                        int spinnerPosition = spinneradapter.getPosition(ComparedModel);
                        spinner_machine.setSelection(spinnerPosition);

                    } else if (count == 1) {
                        spinner_machine.setSelection(1);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                //  fillListDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", ComplaintsActivity.this);

            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, ComplaintsActivity.this);
                Intent i = new Intent(ComplaintsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else if (flag == 5) {
                ScanckBar();
                dialog.dismiss();
            }

            progressDialog.dismiss();
        }
    }


    @Override
    public void onBackPressed() {

        if (isFABOpen) {
            closeFABMenu();
        } else {

            Intent intent = new Intent(ComplaintsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
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
                intent = new Intent(ComplaintsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ComplaintsActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }

                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ComplaintsActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ComplaintsActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(ComplaintsActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(ComplaintsActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(ComplaintsActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ComplaintsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(ComplaintsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(ComplaintsActivity.this);
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
