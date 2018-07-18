package com.cfcs.komaxcustomer.background_task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.customer_activity.ComplaintsActivity;
import com.cfcs.komaxcustomer.customer_activity.FeedbackActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Admin on 30-03-2018.
 */

public class UpdateEscalation extends AsyncTask<String, String, String> {

    private String SOAP_ACTION = "http://cfcs.co.in/AppEscalationInsUpdt";
    private String NAMESPACE = "http://cfcs.co.in/";
    private String METHOD_NAME = "AppEscalationInsUpdt";
    private String URL = Config_Customer.BASE_URL + "Customer/webapi/customerwebservice.asmx?";
    String EscalationList = "", msgstatus = "", status = "";
    int flag;
    Context context;
    ProgressDialog progressDialog;

    String LoginStatus;
    String invalid = "LoginFailed";

    public UpdateEscalation(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "", "Please wait...", true, false, null);
    }

    @Override
    protected String doInBackground(String... params) {

        String ContactPersonId = "", ComplainNo = "", EscalationID = "", AuthCode = "";
        ContactPersonId = params[0];
        ComplainNo = params[1];
        EscalationID = params[2];
        AuthCode = params[3];


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("ContactPersonId", ContactPersonId);
        request.addProperty("ComplainNo", ComplainNo);
        request.addProperty("EscalationID", EscalationID);
        request.addProperty("AuthCode", AuthCode);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result != null) {
                EscalationList = result.getProperty(0).toString();
                JSONArray jsonArray = new JSONArray(EscalationList);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if (jsonObject.has("status")) {
                    LoginStatus = jsonObject.getString("status");
                    msgstatus = jsonObject.getString("MsgNotification");
                    if (LoginStatus.equals(invalid)) {

                        flag = 4;
                    } else {

                        flag = 1;
                    }
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
            Config_Customer.toastShow(msgstatus, context);
            Log.e("msgstatus", msgstatus);

            Intent intent = new Intent(context, ComplaintsActivity.class);
            intent.putExtra("status", status);
            context.startActivity(intent);

        } else if (flag == 3) {
            Config_Customer.toastShow("No Response", context);

        } else if (flag == 4) {
            Config_Customer.toastShow(msgstatus, context);
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);

        }
        progressDialog.dismiss();
    }
}
