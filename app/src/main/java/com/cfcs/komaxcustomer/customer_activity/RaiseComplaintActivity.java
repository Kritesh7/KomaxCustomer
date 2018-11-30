package com.cfcs.komaxcustomer.customer_activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.models.DecodeImageBean;
import com.cfcs.komaxcustomer.utils.SimpleSpanBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RaiseComplaintActivity extends AppCompatActivity {


    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppComplainInsUpdt";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppComplainInsUpdt";
    private static String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";


    private static String SOAP_ACTION2 = "http://cfcs.co.in/AppddlPlantAndTransactionType";
    private static String METHOD_NAME2 = "AppddlPlantAndTransactionType";

    private static String SOAP_ACTION3 = "http://cfcs.co.in/AppddlModelCustomerWise";
    private static String METHOD_NAME3 = "AppddlModelCustomerWise";

    private static String SOAP_ACTION4 = "http://cfcs.co.in/AppddlMachineSale";
    private static String METHOD_NAME4 = "AppddlMachineSale";

    private static String SOAP_ACTION5 = "http://cfcs.co.in/AppCustomerMachineChange";
    private static String METHOD_NAME5 = "AppCustomerMachineChange";

    List<String> complainTitleIDList;
    List<String> complainTitleNameList;

    List<String> siteIDList;
    List<String> siteNameList;

    List<String> modelIDList;
    List<String> modelNameLIst;

    List<String> machineSerialID;
    List<String> machineSerialName;

    Spinner spinner_plant;
    Spinner spinner_machine_model;
    Spinner spinner_machine_serial;
    Spinner spinner_complain_title;

    String SelctedPlantID;
    String SelectedModelID;
    String SelectedMachineSerial;
    String SelectedComplainTitleID;
    String SelectedProblemTitle;
    String SelctedMachineID;

    List<String> spinnerlist;
    List<String> spinnerlist1;

    ArrayAdapter<String> spinneradapterComplainTitle;


    EditText txt_complaint_time, txt_problem_description, txt_other_contacts;
    Calendar myCalendar;
    Button btn_submit, btn_clear;

    String problemDesc = "", occurAtDate = "", occurAtTime = "", otherContact = "", occurAtDateTime = "";

    ScrollView scroll_raise_complain;
    CardView card_view_raise_complain;

    LinearLayout maincontainer;

    TextView tv_request_title, tv_problem_date, tv_plant, tv_machine_serial, tv_machine_model,txt_complaint_date,tv_problem_description,asm_txt_show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_complaint);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        tv_request_title = findViewById(R.id.tv_request_title);
        tv_problem_date = findViewById(R.id.tv_problem_date);
        tv_machine_serial = findViewById(R.id.tv_machine_serial);
        tv_plant = findViewById(R.id.tv_plant);
        tv_machine_model = findViewById(R.id.tv_machine_model);
        tv_problem_description = findViewById(R.id.tv_problem_description);
        asm_txt_show = findViewById(R.id.asm_txt_show);

        SimpleSpanBuilder ssbRequest = new SimpleSpanBuilder();
        ssbRequest.appendWithSpace("Request Title");
        ssbRequest.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_request_title.setText(ssbRequest.build());

        SimpleSpanBuilder ssbProblemDesc = new SimpleSpanBuilder();
        ssbProblemDesc.appendWithSpace("Request Description");
        ssbProblemDesc.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_problem_description.setText(ssbProblemDesc.build());

        SimpleSpanBuilder ssbProblemDate = new SimpleSpanBuilder();
        ssbProblemDate.appendWithSpace("Problem Occurred At");
        ssbProblemDate.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_problem_date.setText(ssbProblemDate.build());

        SimpleSpanBuilder ssbMachineSerial = new SimpleSpanBuilder();
        ssbMachineSerial.appendWithSpace("Machine Serial No");
        ssbMachineSerial.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_machine_serial.setText(ssbMachineSerial.build());

        SimpleSpanBuilder ssbPlant = new SimpleSpanBuilder();
        ssbPlant.appendWithSpace("Plant");
        ssbPlant.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_plant.setText(ssbPlant.build());

        SimpleSpanBuilder ssbMachineModel = new SimpleSpanBuilder();
        ssbMachineModel.appendWithSpace("Machine Model");
        ssbMachineModel.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_machine_model.setText(ssbMachineModel.build());

        txt_complaint_date = findViewById(R.id.txt_complaint_date);
        spinner_plant = findViewById(R.id.spinner_plant);
        spinner_machine_model = findViewById(R.id.spinner_machine_model);
        spinner_machine_serial = findViewById(R.id.spinner_machine_serial);
        txt_problem_description = findViewById(R.id.txt_problem_description);
        spinner_complain_title = findViewById(R.id.spinner_complain_title);
        txt_other_contacts = findViewById(R.id.txt_other_contacts);
        btn_submit = findViewById(R.id.btn_submit);
        btn_clear = findViewById(R.id.btn_clear);
        scroll_raise_complain = findViewById(R.id.scroll_raise_complain);
        maincontainer = findViewById(R.id.maincontainer);

        c = Calendar.getInstance();

        Config_Customer.isOnline(RaiseComplaintActivity.this);
        if (Config_Customer.internetStatus) {

            new SetdataInSpinner().execute();

        } else {
            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", RaiseComplaintActivity.this);
        }

        myCalendar = Calendar.getInstance();


        Date date1 = new Date();
        String stringDate = DateFormat.getDateInstance().format(date1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String dateforrow = dateFormat.format(myCalendar.getTime());
        txt_complaint_date.setText(stringDate + " " + dateforrow);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config_Customer.isOnline(RaiseComplaintActivity.this);
                if (Config_Customer.internetStatus == true) {

                    int complaintTilte = spinner_complain_title.getSelectedItemPosition();
                    int plantPos = spinner_plant.getSelectedItemPosition();
                    int machinePos = spinner_machine_model.getSelectedItemPosition();
                    int serialPos = spinner_machine_serial.getSelectedItemPosition();

                    if (complaintTilte == 0) {
                        Config_Customer.alertBox("Please Select Request Title",
                                RaiseComplaintActivity.this);
                        spinner_complain_title.requestFocus();
                        focusOnView();
                    } else if (txt_problem_description.getText().toString().equalsIgnoreCase("")) {
                        Config_Customer.alertBox("Please Enter Your Description",
                                RaiseComplaintActivity.this);
                        txt_problem_description.requestFocus();
                    } else if (plantPos == 0) {
                        Config_Customer.alertBox("Please Select Your Plant",
                                RaiseComplaintActivity.this);
                        spinner_plant.requestFocus();
                    } else if (machinePos == 0) {
                        Config_Customer.alertBox("Please Select Your Machine Model",
                                RaiseComplaintActivity.this);
                        spinner_machine_model.requestFocus();
                    } else if (serialPos == 0) {
                        Config_Customer.alertBox("Please Select Your Machine Serial No",
                                RaiseComplaintActivity.this);
                        spinner_machine_serial.requestFocus();
                    } else {

                        long SelectedComplainTitle = spinner_complain_title.getSelectedItemId();
                        SelectedComplainTitleID = complainTitleIDList.get((int) SelectedComplainTitle);
                        SelectedProblemTitle = spinner_complain_title.getSelectedItem().toString();
                        long SelectedSerial = spinner_machine_serial.getSelectedItemId();
                        SelectedMachineSerial = machineSerialID.get((int) SelectedSerial);


                        problemDesc = txt_problem_description.getText().toString();
                        otherContact = txt_other_contacts.getText().toString();
                        occurAtDateTime = txt_complaint_date.getText().toString();


                        if (!occurAtDateTime.equals(null)) {
                            String[] parts = occurAtDateTime.split(" ");
                            occurAtDate = parts[0];
                            occurAtTime = parts[1];
                        }

                        new SubmitBtnAsy().execute();
                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", RaiseComplaintActivity.this);
                }


            }


        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_complain_title.setSelection(0);
                txt_problem_description.setText("");
                spinner_plant.setSelection(0);
                spinner_complain_title.requestFocus();
            }
        });


//        txt_complaint_date = (TextView) findViewById(R.id.txt_complaint_date);

        txt_complaint_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                datePicker();
            }
        });


        spinnerlist = new ArrayList<String>();
        spinnerlist.add("Select");
        ArrayAdapter<String> spinneradapter_model = new ArrayAdapter<String>(RaiseComplaintActivity.this,
                android.R.layout.simple_spinner_item, spinnerlist);
        spinneradapter_model.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_machine_model.setAdapter(spinneradapter_model);
        // spinner_machine_serial.setAdapter(spinneradapter_model);



        spinner_plant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Config_Customer.isOnline(RaiseComplaintActivity.this);
                if (Config_Customer.internetStatus == true) {

                    long SelectedPlant = adapterView.getSelectedItemId();
                    SelctedPlantID = siteIDList.get((int) SelectedPlant);
                    if (SelectedPlant != 0) {

                        new ModelSpinnerAsy().execute();


                    } else {

                        spinnerlist = new ArrayList<String>();
                        spinnerlist.add("Select");
                        ArrayAdapter<String> spinneradapter_model = new ArrayAdapter<String>(RaiseComplaintActivity.this,
                                android.R.layout.simple_spinner_item, spinnerlist);
                        spinneradapter_model.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_machine_model.setAdapter(spinneradapter_model);

                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", RaiseComplaintActivity.this);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner_machine_model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Config_Customer.isOnline(RaiseComplaintActivity.this);
                if (Config_Customer.internetStatus == true) {

                    long SelectedModel = adapterView.getSelectedItemId();

                    if (SelectedModel != 0) {
                        SelectedModelID = modelIDList.get((int) SelectedModel);
                        new MachinesSerialSpinnerAsy().execute();
                    } else {

                        spinnerlist1 = new ArrayList<String>();
                        spinnerlist1.add("Select");
                        ArrayAdapter<String> spinneradapter_model1 = new ArrayAdapter<String>(RaiseComplaintActivity.this,
                                android.R.layout.simple_spinner_item, spinnerlist1);
                        spinneradapter_model1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_machine_serial.setAdapter(spinneradapter_model1);
                    }

                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", RaiseComplaintActivity.this);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        spinner_machine_serial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Config_Customer.isOnline(RaiseComplaintActivity.this);
                if (Config_Customer.internetStatus) {

                    long SelectedMachine = parent.getSelectedItemId();
                    if (SelectedMachine != 0) {
                        SelctedMachineID = machineSerialID.get((int) SelectedMachine);
                        new AddMachineChange().execute();

                    } else {

                        asm_txt_show.setVisibility(View.GONE);
                    }


                } else {
                    Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", RaiseComplaintActivity.this);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    String date_time1;
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;

    Calendar c;

    public void datePicker() {

        // Get Current Date
        this.mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;


                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Date date1 = new Date(String.valueOf(c.getTime()));
                        String date_time = DateFormat.getDateInstance().format(date1);

                        Calendar calendar2 = Calendar.getInstance();
                        // calendar2.add(Calendar.DATE, -1);
                        Date today = calendar2.getTime();
                        String todayDate = DateFormat.getDateInstance().format(today);
                        Date currentDate = new Date();
                        Date selectedNextDate = new Date();

                        try {
                            currentDate = DateFormat.getDateInstance().parse(todayDate);
                            selectedNextDate = DateFormat.getDateInstance().parse(date_time);
                            if (selectedNextDate.equals(currentDate)) {
                                tiemPicker(date_time);
                            } else if (selectedNextDate.before(currentDate)) {
                                tiemPicker(date_time);
                            } else {
                                txt_complaint_date.setText("");
                                Config_Customer.alertBox("Please select correct date before today ", RaiseComplaintActivity.this);
                            }
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }, this.mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    private void tiemPicker(String date_time) {
        this.date_time1 = date_time;
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        int hour = hourOfDay;
                        int minutes = minute;


                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12) {
                            timeSet = "PM";
                        } else {
                            timeSet = "AM";
                        }

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes;
                        else
                            min = String.valueOf(minutes);

                        // Append in a StringBuilder
                        String aTime = new StringBuilder().append(hour).append(':')
                                .append(min).append(" ").append(timeSet).toString();

                        txt_complaint_date.setText(date_time1 + " " + aTime);


                    }
                }, 0, 0, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }


    private void focusOnView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                scroll_raise_complain.smoothScrollTo(0, 0);
            }
        });
    }


    private class SetdataInSpinner extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String plant_ransaction_detail_value, plantList, complainTitleList;
        ProgressDialog progressDialog;
        int count = 0;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RaiseComplaintActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            String ContactPersonId = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "AuthCode", "");
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
                        JSONArray probleTitleArray = object.getJSONArray("ComplainTitle");
                        complainTitleList = probleTitleArray.toString();
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
                    JSONArray jsonArray = new JSONArray(plant_ransaction_detail_value);
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
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Customer.toastShow(msgstatus, RaiseComplaintActivity.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray2 = new JSONArray(complainTitleList);
                    complainTitleIDList = new ArrayList<String>();
                    complainTitleIDList.add(0, "");
                    complainTitleNameList = new ArrayList<String>();
                    complainTitleNameList.add(0, "Select");

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String ComplaintTitleID = jsonObject2.getString("ComplaintTitleID");
                        String ComplaintTitleName = jsonObject2.getString("ComplaintTitleName");

                        complainTitleIDList.add(i + 1, ComplaintTitleID);
                        complainTitleNameList.add(i + 1, ComplaintTitleName);
                    }

                    spinneradapterComplainTitle = new ArrayAdapter<String>(RaiseComplaintActivity.this, android.R.layout.simple_spinner_item, complainTitleNameList);
                    spinneradapterComplainTitle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_complain_title.setAdapter(spinneradapterComplainTitle);

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                try {
                    JSONArray jsonArray2 = new JSONArray(plantList);
                    siteIDList = new ArrayList<String>();
                    siteIDList.add(0, "");
                    siteNameList = new ArrayList<String>();
                    siteNameList.add(0, "Select");


                    for (int i = 0; i < jsonArray2.length(); i++) {
                        count += 1;
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String SiteID = jsonObject2.getString("SiteID");
                        String SiteName = jsonObject2.getString("SiteName");


                        siteIDList.add(i + 1, SiteID);
                        //siteNameList.add(SiteName);
                        siteNameList.add(i + 1, SiteName);
                    }

                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(RaiseComplaintActivity.this, android.R.layout.simple_spinner_item, siteNameList);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_plant.setAdapter(spinneradapter);

                    if (count == 1) {

                        spinner_plant.setSelection(1);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                //  fillListDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", RaiseComplaintActivity.this);
//                fillListDialog.dismiss();
                finish();
            } else {
                if (flag == 4) {

                    Config_Customer.toastShow(msgstatus, RaiseComplaintActivity.this);
                    Intent i = new Intent(RaiseComplaintActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else if (flag == 5) {

                    ScanckBar();
                    btn_submit.setEnabled(false);

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
                        Config_Customer.isOnline(RaiseComplaintActivity.this);
                        if (Config_Customer.internetStatus == true) {

                            new SetdataInSpinner().execute();
                            btn_submit.setEnabled(true);

                        } else {
                            Config_Customer.toastShow("No Internet Connection! Please Reconnect Your Internet", RaiseComplaintActivity.this);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }


    private class ModelSpinnerAsy extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String model_detail_value, model_list;
        int count = 0;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(RaiseComplaintActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);
            String ContactPersonId = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "AuthCode", "");
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

                    if (jsonObject.has("MsgNotification")) {
                        msgstatus = jsonObject.getString("MsgNotification");
                        flag = 1;
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
                Config_Customer.toastShow(msgstatus, RaiseComplaintActivity.this);
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
                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(RaiseComplaintActivity.this,
                            android.R.layout.simple_spinner_item, modelNameLIst);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_machine_model.setAdapter(spinneradapter);
                    if (count == 1) {

                        spinner_machine_model.setSelection(1);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                //  fillListDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", RaiseComplaintActivity.this);
//                fillListDialog.dismiss();
            } else if (flag == 5) {
                ScanckBar();
                btn_submit.setEnabled(false);
            }
            progressDialog.dismiss();
        }
    }

    private class MachinesSerialSpinnerAsy extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String machine_serial_detail, machine_serial_list;
        int count = 0;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(RaiseComplaintActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);
            String ContactPersonId = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "AuthCode", "");
            String PlantId = SelctedPlantID;
            String ModelID = SelectedModelID;
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
                    machine_serial_detail = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(machine_serial_detail);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    machine_serial_list = jsonArray.toString();

                    if (jsonObject.has("MsgNotification")) {
                        msgstatus = jsonObject.getString("MsgNotification");
                        flag = 1;
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
                Config_Customer.toastShow(msgstatus, RaiseComplaintActivity.this);
            } else if (flag == 2) {
                try {
                    // Add value in Plant List Status Spinner
                    JSONArray jsonArray2 = new JSONArray(machine_serial_list);
                    machineSerialID = new ArrayList<String>();
                    machineSerialID.add(0, "");
                    machineSerialName = new ArrayList<String>();
                    machineSerialName.add(0, "Select");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        count += 1;
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String SaleID = jsonObject2.getString("SaleID");
                        String ModelName = jsonObject2.getString("ModelName");

                        machineSerialID.add(i + 1, SaleID);
                        machineSerialName.add(i + 1, ModelName);
                    }
                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(RaiseComplaintActivity.this,
                            android.R.layout.simple_spinner_item, machineSerialName);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_machine_serial.setAdapter(spinneradapter);

                    if (count == 1) {
                        spinner_machine_serial.setSelection(1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                //  fillListDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", RaiseComplaintActivity.this);
//                fillListDialog.dismiss();
            } else if (flag == 5) {
                ScanckBar();
                btn_submit.setEnabled(false);
            }
            progressDialog.dismiss();
        }
    }

    private class AddMachineChange extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String machine_change_detail_value;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RaiseComplaintActivity.this, "Loading...", "Please Wait....", true, false);
            asm_txt_show.setText("");
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME5);
            String ContactPersonId = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "AuthCode", "");
            String MachineID = SelctedMachineID;
            request.addProperty("SaleID", MachineID);
            request.addProperty("ContactPersonId", ContactPersonId);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION5, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    machine_change_detail_value = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(machine_change_detail_value);
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
                Config_Customer.toastShow(msgstatus, RaiseComplaintActivity.this);
            } else if (flag == 2) {
                try {

                    JSONArray jsonArray2 = new JSONArray(machine_change_detail_value);
                    JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
                    String TransactionType = jsonObject2.getString("TransactionType");
                    String AMCMsg = jsonObject2.getString("AMCMsg");
                    String OpenComplainMsg = jsonObject2.getString("OpenComplainMsg");
                    asm_txt_show.setVisibility(View.VISIBLE);
                    asm_txt_show.setText(AMCMsg);

                    int Transaction = Integer.parseInt(TransactionType);

                    if (Transaction <= 2) {
                        asm_txt_show.setVisibility(View.VISIBLE);
                        asm_txt_show.setTextColor(Color.parseColor("#00CC00"));
                    } else if (Transaction == 4) {
                        asm_txt_show.setVisibility(View.VISIBLE);
                        asm_txt_show.setTextColor(Color.parseColor("#0000FF"));
                    } else {
                        asm_txt_show.setVisibility(View.VISIBLE);
                        asm_txt_show.setTextColor(Color.parseColor("#FF0000"));
                    }

                    if(OpenComplainMsg.compareTo("") != 0){
                        OpenAlertMsg(OpenComplainMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }
                progressDialog.dismiss();
            } else if (flag == 3) {
                Config_Customer.toastShow("No Response", RaiseComplaintActivity.this);
            } else if (flag == 4) {

                Config_Customer.toastShow(msgstatus, RaiseComplaintActivity.this);
                Config_Customer.logout(RaiseComplaintActivity.this);
                Config_Customer.putSharedPreferences(RaiseComplaintActivity.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {
                ScanckBar();
                btn_submit.setEnabled(false);
                btn_clear.setEnabled(false);
                progressDialog.dismiss();

            }
            progressDialog.dismiss();
        }
    }

    private void OpenAlertMsg(String openComplainMsg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(RaiseComplaintActivity.this,
                R.style.AppTheme));

        alertDialogBuilder.setTitle(RaiseComplaintActivity.this.getString(R.string.notification));
        alertDialogBuilder.setMessage(openComplainMsg);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();

    }


    private class SubmitBtnAsy extends AsyncTask<String, String, String> {
        int flag;
        String jsonValue, msg;

        String LoginStatus;
        String invalid = "LoginFailed";
        String valid = "success";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RaiseComplaintActivity.this, "Loading", "Please wait...", true, false);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String ContactPersonId = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "ContactPersonId", "");
            String AuthCode = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("CustomerID", SelctedPlantID);
            request.addProperty("ModelID", SelectedModelID);
            request.addProperty("SaleID", SelectedMachineSerial);
            request.addProperty("ComplaintTitle", SelectedProblemTitle);
            request.addProperty("ComplaintTitleID", SelectedComplainTitleID);
            request.addProperty("DateOfOccurance", occurAtDate);
            request.addProperty("TimeOfOccurance", occurAtTime);
            request.addProperty("Description", problemDesc);
            request.addProperty("OtherContactNos", otherContact);
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
                    jsonValue = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status")) {

                        LoginStatus = jsonObject.getString("status");
                        msg = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {

                            flag = 4;
                        } else if (LoginStatus.equals(valid)) {

                            flag = 2;
                        }else {
                            flag = 1;
                        }
                    } else {
                        flag = 1;
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("jsonValue", "cfcs" + jsonValue);
            if (flag == 1) {
                Config_Customer.toastShow(msg, RaiseComplaintActivity.this);
            } else {
                if (flag == 2) {
                    Config_Customer.toastShow(msg, RaiseComplaintActivity.this);
                    Intent intent = new Intent(RaiseComplaintActivity.this, ComplaintsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (flag == 3) {
                    Config_Customer.toastShow("No Response", RaiseComplaintActivity.this);
                } else if (flag == 4) {
                    Config_Customer.toastShow(msg, RaiseComplaintActivity.this);
                    Intent i = new Intent(RaiseComplaintActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else if (flag == 5) {

                    ScanckBar();
                    btn_submit.setEnabled(false);
                }
            }
            progressDialog.dismiss();
            btn_submit.setClickable(true);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RaiseComplaintActivity.this, DashboardActivity.class);
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
                intent = new Intent(RaiseComplaintActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    startActivity(intent);
                } else {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(RaiseComplaintActivity.this, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        startActivity(intent);
                    }
                }
                return (true);

            case R.id.btn_arrange_call_menu:
                intent = new Intent(RaiseComplaintActivity.this, ArrangeCallActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(RaiseComplaintActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complaint_menu:
                intent = new Intent(RaiseComplaintActivity.this, ComplaintsActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines_menu:
                intent = new Intent(RaiseComplaintActivity.this, MachinesActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_feedback_menu:
                intent = new Intent(RaiseComplaintActivity.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(RaiseComplaintActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.change_password:

                intent = new Intent(RaiseComplaintActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Customer.logout(RaiseComplaintActivity.this);
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
