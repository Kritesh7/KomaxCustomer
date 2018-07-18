package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Spinner;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.adapters.MachineListAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MachinesActivity extends AppCompatActivity {

//    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppddlPlantAndTransactionType";
//    private static String NAMESPACE = "http://cfcs.co.in/";
//    private static String METHOD_NAME1 = "AppddlPlantAndTransactionType";
//    private static String URL = Config_Customer.BASE_URL+"Customer/webapi/customerwebservice.asmx?";


    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppMachineSalesInfoList";
    private static String SOAP_ACTION2 = "http://cfcs.co.in/AppddlPlantAndTransactionType";
    private static String SOAP_ACTION3 = "http://cfcs.co.in/AppddlModelCustomerWise";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppMachineSalesInfoList";
    private static String METHOD_NAME2 = "AppddlPlantAndTransactionType";
    private static String METHOD_NAME3 = "AppddlModelCustomerWise";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";

    FloatingActionButton fab;

    Spinner spinner_palnt;
    Spinner spinner_transaction;
    Spinner spinner_model;
    EditText txt_serial_no;
    String SelctedPlantID;

    List<String> siteIDList;
    List<String> siteNameList;

    List<String> transactionIDList;
    List<String> transactionNameList;

    List<String> modelIDList;
    List<String> modelNameLIst;

    List<String> spinnerlist;

    // int ws = -1;

    String siteID, siteName;

    ListView list;

    ArrayList<MachinesDataModel> machinesList = new ArrayList<MachinesDataModel>();
    String[] SaleID;
    String[] DateOfInstallationText;
    String[] WarrantyStartDateText;
    String[] WarrantyEndDateText;
    String[] AMCStartDateText;
    String[] AMCEndDateText;
    String[] DateOfSupplyText;
    String[] SerialNo;
    String[] PrincipleName;
    String[] ModelName;
    String[] CustomerName;
    String[] TransactionTypeName;
    String[] Plant;


    ArrayAdapter<String> spinneradapterPlant;
    ArrayAdapter<String> spinneradapterTrans;

    int Plantcount = 0;

    Button btn_search_find, btn_search_clear;

    AlertDialog dialog;

    CoordinatorLayout maincontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machines);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        fab = findViewById(R.id.fab);

        maincontainer = findViewById(R.id.maincontainer);
        list = (ListView) findViewById(R.id.machine_list_view);


        Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "SerialNo");

        Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "PlantName");

        Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "Model1");

        Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "Transaction");


        Config_Customer.isOnline(MachinesActivity.this);
        if (Config_Customer.internetStatus == true) {

            new MachinesListAsync().execute();
            new SetdataInSpinner().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesActivity.this);
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchPopByFab();
            }
        });
    }

    private void showSearchPopByFab() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.add_search_machines, null);
        alertDialog.setView(convertView);
        spinner_palnt = convertView.findViewById(R.id.spinner_plant);
        spinner_transaction = convertView.findViewById(R.id.spinner_transaction);
        spinner_model = convertView.findViewById(R.id.spinner_model);
        txt_serial_no = convertView.findViewById(R.id.txt_serial_no);


        btn_search_find = convertView.findViewById(R.id.btn_search_find);

        btn_search_clear = convertView.findViewById(R.id.btn_search_clear);

        dialog = alertDialog.create();


        String CompareSerialNo = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "SerialNo", "");

        txt_serial_no.setText(CompareSerialNo);


        String ComparePlant = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "PlantName", "");
        String CompareTran = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "Transaction", "");


        spinner_palnt.setAdapter(spinneradapterPlant);

        if (!ComparePlant.equalsIgnoreCase("")) {
            int spinnerpos = spinneradapterPlant.getPosition(ComparePlant);
            spinner_palnt.setSelection(spinnerpos);
        } else if (Plantcount == 1) {
            spinner_palnt.setSelection(1);
        }

        spinner_transaction.setAdapter(spinneradapterTrans);

        if (!CompareTran.equalsIgnoreCase("")) {
            int spinnerpos = spinneradapterTrans.getPosition(CompareTran);
            spinner_transaction.setSelection(spinnerpos);
        }


        spinner_palnt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                Config_Customer.isOnline(MachinesActivity.this);
                if (Config_Customer.internetStatus == true) {

                    long SelectedPlant = adapterView.getSelectedItemId();
                    SelctedPlantID = siteIDList.get((int) SelectedPlant);

                    if (SelectedPlant != 0) {
                        new ModelSpinnerAsy().execute();

                    } else {

                        modelIDList = new ArrayList<String>();
                        modelIDList.add(0, "");
                        modelNameLIst = new ArrayList<String>();
                        modelNameLIst.add(0, "Select");

                        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(MachinesActivity.this,
                                android.R.layout.simple_spinner_item, modelNameLIst);
                        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_model.setAdapter(spinneradapter);

                    }


                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesActivity.this);
                }


            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_search_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String modelID = "";
                String transaction = "";
                String Plant = "";
                String serialNo = "";

                Config_Customer.isOnline(MachinesActivity.this);
                if (Config_Customer.internetStatus == true) {

                    serialNo = txt_serial_no.getText().toString();
                    long PlantID = spinner_palnt.getSelectedItemId();
                    Plant = siteIDList.get((int) PlantID);
                    long ModelID = spinner_model.getSelectedItemId();
                    modelID = modelIDList.get((int) ModelID);
                    long TransactionID = spinner_transaction.getSelectedItemId();
                    transaction = transactionIDList.get((int) TransactionID);

                    Config_Customer.putSharedPreferences(MachinesActivity.this, "pref_Customer", "SerialNo", serialNo);

                    Config_Customer.putSharedPreferences(MachinesActivity.this, "pref_Customer", "PlantName", spinner_palnt.getSelectedItem().toString());

                    Config_Customer.putSharedPreferences(MachinesActivity.this, "pref_Customer", "Model1", spinner_model.getSelectedItem().toString());

                    Config_Customer.putSharedPreferences(MachinesActivity.this, "pref_Customer", "Transaction", spinner_transaction.getSelectedItem().toString());

                    dialog.dismiss();

                    new MachinesListAsync(Plant, modelID, serialNo, transaction).execute();

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesActivity.this);
                }


            }
        });


        btn_search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "SerialNo");

                Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "PlantName");

                Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "Model1");

                Config_Customer.getSharedPreferenceRemove(MachinesActivity.this, "pref_Customer", "Transaction");

                Config_Customer.isOnline(MachinesActivity.this);
                if (Config_Customer.internetStatus == true) {

                    dialog.dismiss();
                    new MachinesListAsync().execute();

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesActivity.this);
                }

            }
        });

        dialog.show();
    }


    private class SetdataInSpinner extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String plant_ransaction_detail_value, plantList, transactionList;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MachinesActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            String ContactPersonId = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "AuthCode", "");
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    plant_ransaction_detail_value = result.getProperty(0).toString();

                    Object json = new JSONTokener(plant_ransaction_detail_value).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(plant_ransaction_detail_value);
                        JSONArray plantjsonArray = object.getJSONArray("PlantList");
                        plantList = plantjsonArray.toString();
                        JSONArray trancsonArray = object.getJSONArray("TransactionTypeList");
                        transactionList = trancsonArray.toString();
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
                Config_Customer.toastShow(msgstatus, MachinesActivity.this);
            } else if (flag == 2) {
                try {
//
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

                    spinneradapterPlant = new ArrayAdapter<String>(MachinesActivity.this, android.R.layout.simple_spinner_item, siteNameList);
                    spinneradapterPlant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }
                try {

                    JSONArray jsonArray2 = new JSONArray(transactionList);
                    transactionIDList = new ArrayList<String>();
                    transactionIDList.add(0, "");
                    transactionNameList = new ArrayList<String>();
                    transactionNameList.add(0, "Select");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String TranscationID = jsonObject2.getString("TransactionTypeID");
                        String TransactionName = jsonObject2.getString("TransactionTypeName");

                        transactionIDList.add(TranscationID);
                        transactionNameList.add(TransactionName);
                    }

                    spinneradapterTrans = new ArrayAdapter<String>(MachinesActivity.this, android.R.layout.simple_spinner_item, transactionNameList);
                    spinneradapterTrans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }
                progressDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", MachinesActivity.this);
//
            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, MachinesActivity.this);
                Intent i = new Intent(MachinesActivity.this, LoginActivity.class);
                startActivity(i);
                finish();


            } else if (flag == 5) {
                ScanckBar();
                list.setAdapter(null);
            }
            progressDialog.dismiss();
        }
    }

    private class ModelSpinnerAsy extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String model_detail_value, model_list;
        String CompareModel = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "Model1", "");
        ProgressDialog progressDialog;
        int count = 0;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(MachinesActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);
            String ContactPersonId = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "AuthCode", "");
            String PlantId = SelctedPlantID;
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("PlantID", PlantId);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION3, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    model_detail_value = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(model_detail_value);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    model_list = jsonArray.toString();

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
                Config_Customer.toastShow(msgstatus, MachinesActivity.this);
            } else if (flag == 2) {
                try {
                    // Add value in Plant List Status Spinner
                    JSONArray jsonArray2 = new JSONArray(model_list);
                    modelIDList = new ArrayList<String>();
                    modelIDList.add(0, "");
                    modelNameLIst = new ArrayList<String>();
                    modelNameLIst.add(0, "Select");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        count += 1;
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String ModelID = jsonObject2.getString("ModelID");
                        String ModelName = jsonObject2.getString("ModelName");

                        modelIDList.add(i + 1, ModelID);
                        modelNameLIst.add(i + 1, ModelName);
                    }

                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(MachinesActivity.this,
                            android.R.layout.simple_spinner_item, modelNameLIst);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_model.setAdapter(spinneradapter);

                    if (!CompareModel.equalsIgnoreCase("")) {
                        int spinnerpos = spinneradapter.getPosition(CompareModel);
                        spinner_model.setSelection(spinnerpos);
                    } else if (count == 1) {
                        spinner_model.setSelection(1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                //  fillListDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", MachinesActivity.this);

            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, MachinesActivity.this);
                Intent i = new Intent(MachinesActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else if (flag == 5) {
                ScanckBar();
                dialog.dismiss();
            }
            progressDialog.dismiss();
        }
    }

    public class MachinesListAsync extends AsyncTask<String, String, String> {

        String msgstatus;
        int flag;
        String statusbtn = "";

        String LoginStatus;
        String invalid = "LoginFailed";
        ProgressDialog progressDialog;
        String PlantID = "";
        String SearchStr = "";
        String ModelID = "";
        String TransactionType = "";


        public MachinesActivity machinesActivity;

        public MachinesListAsync(String statusbtn) {
            this.machinesActivity = machinesActivity;
            this.statusbtn = statusbtn;
        }


        public MachinesListAsync() {

        }

        public MachinesListAsync(String plant, String modelID, String serialNo, String transaction) {
            this.PlantID = plant;
            this.ModelID = modelID;
            this.SearchStr = serialNo;
            this.TransactionType = transaction;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MachinesActivity.this, "", "Please wait...", true, false, null);
        }

        @Override
        protected String doInBackground(String... status1) {
            // TODO Auto-generated method stub

            // String status = status1[0];

            String MachinesList = "";

            int count = 0;

            String ContactPersonId = Config_Customer.getSharedPreferences(
                    MachinesActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(
                    MachinesActivity.this, "pref_Customer", "AuthCode", "");
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("PlantID", PlantID);
            request.addProperty("SearchStr", SearchStr);
            request.addProperty("ModelID", ModelID);
            request.addProperty("TransactionType", TransactionType);
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
                    MachinesList = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(MachinesList);
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

                        machinesList.clear();

                        SaleID = new String[jsonArray.length()];
                        DateOfInstallationText = new String[jsonArray.length()];
                        WarrantyStartDateText = new String[jsonArray.length()];
                        WarrantyEndDateText = new String[jsonArray.length()];
                        AMCStartDateText = new String[jsonArray.length()];
                        AMCEndDateText = new String[jsonArray.length()];
                        DateOfSupplyText = new String[jsonArray.length()];
                        SerialNo = new String[jsonArray.length()];
                        PrincipleName = new String[jsonArray.length()];
                        ModelName = new String[jsonArray.length()];
                        CustomerName = new String[jsonArray.length()];
                        TransactionTypeName = new String[jsonArray.length()];
                        Plant = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                count += 1;
                                JSONObject jsonObject1 = jsonArray
                                        .getJSONObject(i);
                                MachinesDataModel machinesDataModel = new MachinesDataModel(AuthCode, AuthCode, AuthCode,
                                        AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode, AuthCode,
                                        AuthCode, AuthCode, AuthCode);
                                machinesDataModel.setSaleID(jsonObject1.getString("SaleID").toString());
                                machinesDataModel.setSerialNo(jsonObject1.getString("SerialNo").toString());
                                machinesDataModel.setPrincipleName(jsonObject1.getString("PrincipleName").toString());
                                machinesDataModel.setModelName(jsonObject1.getString("ModelName").toString());
                                machinesDataModel.setCustomerName(jsonObject1.getString("CustomerName").toString());
                                machinesDataModel.setTransactionTypeName(jsonObject1.getString("TransactionTypeName").toString());
                                machinesDataModel.setWarrantyStartDateText(jsonObject1
                                        .getString("WarrantyStartDateText")
                                        .toString());
                                machinesDataModel.setWarrantyEndDateText(jsonObject1
                                        .getString("WarrantyEndDateText")
                                        .toString());
                                machinesDataModel.setPlant(jsonObject1.getString("SiteAddress").toString());

                                machinesDataModel.setCounter(String.valueOf(count));

                                // Add this object into the ArrayList myList
                                machinesList.add(machinesDataModel);
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
                Config_Customer.toastShow(msgstatus, MachinesActivity.this);
                list.setAdapter(null);
                progressDialog.dismiss();
            } else {
                if (flag == 2) {
                    list.setAdapter(new MachineListAdapter(MachinesActivity.this, machinesList));
                } else {
                    if (flag == 3) {
                        Config_Customer.toastShow("No Response", MachinesActivity.this);
                        progressDialog.dismiss();
                    } else {
                        if (flag == 4) {

                            Config_Customer.toastShow(msgstatus, MachinesActivity.this);
                            Intent i = new Intent(MachinesActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else if (flag == 5) {

                            ScanckBar();
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
                        Config_Customer.isOnline(MachinesActivity.this);
                        if (Config_Customer.internetStatus == true) {

                            new MachinesListAsync().execute();
                            new SetdataInSpinner().execute();

                        } else {
                            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", MachinesActivity.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MachinesActivity.this, DashboardActivity.class);
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
                intent = new Intent(MachinesActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(MachinesActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(MachinesActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(MachinesActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(MachinesActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(MachinesActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(MachinesActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(MachinesActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(MachinesActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(MachinesActivity.this);
                finish();
                Config_Customer.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }
}
