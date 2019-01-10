package com.cfcs.komaxcustomer.config_customer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxcustomer.LoginActivity;
import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.customer_activity.ArrangeCallActivity;
import com.cfcs.komaxcustomer.customer_activity.ChangePasswordActivity;
import com.cfcs.komaxcustomer.customer_activity.ComplaintsActivity;
import com.cfcs.komaxcustomer.customer_activity.DashboardActivity;
import com.cfcs.komaxcustomer.customer_activity.FeedbackActivity;
import com.cfcs.komaxcustomer.customer_activity.MachinesActivity;
import com.cfcs.komaxcustomer.customer_activity.ProfileActivity;
import com.cfcs.komaxcustomer.customer_activity.RaiseComplaintActivity;

/**
 * Created by Admin on 06-03-2018.
 */

public class Config_Customer {


 //   public static String BASE_URL = "https://app.komaxindia.co.in/";
    public static String BASE_URL = "http://192.168.1.200:8080/";

    public static boolean internetStatus = false;

    public static void putSharedPreferences(Context context, String preferences, String key, String value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSharedPreferences(Context context, String preferences, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        String defvalue = sharedPreferences.getString(key, value);
        return defvalue;
    }

    public static String getSharedPreferenceRemove(Context context, String preferences, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
        return null;
    }


    public static void alertBox(String s, Context c) {
        AlertDialog.Builder altDialog = new AlertDialog.Builder(c);
        altDialog.setMessage(s);
        altDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        altDialog.show();
    }

    public static void toastShow(String s, Context c) {
        Toast toast = Toast.makeText(c, s, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        v.setTextSize(18);
        toast.show();
    }

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            internetStatus = true;
            //Log.e("Status is ", ""+internetStatus);
            return true;
        } else {
            internetStatus = false;
            //Log.e("Status is 1", ""+internetStatus);
        }
        return true;
    }

    public static void logout(Context c) {

        Config_Customer.putSharedPreferences(c, "pref_Customer", "AuthCode", "");
        //  Config_Customer.putSharedPreferences(c, "pref_Customer", "ContactPersonId", "");
        Intent intent = new Intent(c, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
        Config_Customer.toastShow("Successfully Loged Out", c);
    }


    public static void menuNavigation(Context ctx, MenuItem item){

        Intent intent;
        switch (item.getItemId()) {
            case R.id.dashboard:
                intent = new Intent(ctx, DashboardActivity.class);
                ctx.startActivity(intent);
               break;

            case R.id.btn_call_us_menu:
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion <= 22) {
                    String CompanyContactNo = Config_Customer.getSharedPreferences(ctx, "pref_Customer", "CompanyContactNo", "");
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                    ctx.startActivity(intent);
                } else {
                    if (ctx.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ((Activity)ctx).requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        String CompanyContactNo = Config_Customer.getSharedPreferences(ctx, "pref_Customer", "CompanyContactNo", "");
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CompanyContactNo));
                        ctx.startActivity(intent);
                    }
                }

                break;

            case R.id.btn_arrange_call_menu:
                intent = new Intent(ctx, ArrangeCallActivity.class);
                ctx.startActivity(intent);
                 break;

            case R.id.btn_raise_complaint_menu:
                intent = new Intent(ctx, RaiseComplaintActivity.class);
                ctx.startActivity(intent);
                break;

            case R.id.btn_complaint_menu:
                intent = new Intent(ctx, ComplaintsActivity.class);
                ctx.startActivity(intent);
                break;

            case R.id.btn_machines_menu:
                intent = new Intent(ctx, MachinesActivity.class);
                ctx.startActivity(intent);
                break;

            case R.id.btn_feedback_menu:
                intent = new Intent(ctx, FeedbackActivity.class);
                ctx.startActivity(intent);
                break;

            case R.id.profile:
                intent = new Intent(ctx, ProfileActivity.class);
                ctx.startActivity(intent);
                break;

            case R.id.change_password:

                intent = new Intent(ctx, ChangePasswordActivity.class);
                ctx.startActivity(intent);
                break;

            case R.id.logout:

                Config_Customer.logout(ctx);
                Config_Customer.putSharedPreferences(ctx, "checklogin", "status", "2");
                break;

            case R.id.download_file:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://app.komaxindia.co.in/Customer/Customer-User-Manual.pdf"));
                ctx.startActivity(browserIntent);
                break;


        }
    }


}
