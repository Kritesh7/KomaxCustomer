package com.cfcs.komaxcustomer.config_customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxcustomer.LoginActivity;

/**
 * Created by Admin on 06-03-2018.
 */

public class Config_Customer {


    public static String BASE_URL = "https://app.komaxindia.co.in/";

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


}
