package com.cfcs.komaxcustomer.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.background_task.UpdateEscalation;
import com.cfcs.komaxcustomer.config_customer.Config_Customer;
import com.cfcs.komaxcustomer.customer_activity.ComplainFeedbackActivity;
import com.cfcs.komaxcustomer.customer_activity.ComplainListDetailActivity;
import com.cfcs.komaxcustomer.customer_activity.ComplaintsActivity;
import com.cfcs.komaxcustomer.models.ComplainListDataModel;
import com.cfcs.komaxcustomer.models.EscalationDataModel;


import java.util.ArrayList;

/**
 * Created by Admin on 16-03-2018.
 */

public class ComplainListAdapter extends BaseAdapter {

    private ArrayList<ComplainListDataModel> ComplainList;
    private ArrayList<EscalationDataModel> EscaltionList;
    private LayoutInflater inflater;
    private Context context;
    private int i;

    private String Status;


    public ComplainListAdapter(Context context, ArrayList<ComplainListDataModel> complainList, ArrayList<EscalationDataModel> EscaltionList, String Status) {
        this.ComplainList = complainList;
        this.EscaltionList = EscaltionList;
        this.context = context;
        this.Status = Status;
        inflater = LayoutInflater.from(this.context);

    }

    @Override
    public int getCount() {
        return ComplainList.size();
    }

    @Override
    public ComplainListDataModel getItem(int position) {

        return ComplainList.get(position);
    }


    @Override
    public int getViewTypeCount() {


        return getCount();
    }

    @Override
    public int getItemViewType(int position) {


        return position;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        final ComplainListDataModel currentListData = getItem(position);

        String Escalationid = currentListData.getEscalationID();
        String cc = currentListData.getComplainNo();
        Log.e("ID 125", "eid " + Escalationid);
        Log.e("ID cc", "ccno " + cc);
        if (view == null) {
            view = inflater.inflate(R.layout.complain_list_layout,
                    viewGroup, false);
            mViewHolder = new MyViewHolder(view);

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT);
            final AlertDialog.Builder altDialog;
            altDialog = new AlertDialog.Builder(context);
            altDialog.setMessage("Do you want to update Escalation level ?");
            Log.e("ID 123", "modeleid " + Escalationid);
            Log.e("ID ccno", "ccno " + cc);
            for (i = 0; i < EscaltionList.size(); i++) {
                Log.e("ID id", "elistid " + EscaltionList.get(i).getEscalationID());
                RadioButton radio = new RadioButton(context);
                radio.setText(EscaltionList.get(i).getEscalationShortCode());
                int textColor = Color.parseColor("#0000ff");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    radio.setButtonTintList(ColorStateList.valueOf(textColor));
                }
                radio.setTextColor(Color.BLACK);
                radio.setTextSize(12);

                params.weight = 1.0f;
                params.setMargins(15, 5, 5, 15);
                mViewHolder.radioGroup.addView(radio, params);

                if (Escalationid.compareTo(EscaltionList.get(i).getEscalationID()) == 0) {
                    radio.setChecked(true);
                }


                radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        altDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                int selectedRadioButtonID = mViewHolder.radioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = viewGroup.findViewById(selectedRadioButtonID);
                                final String selectedRadioButtonText = selectedRadioButton.getText().toString();
                                Log.e("selectedRadioButtonText", " cfcs " + selectedRadioButtonText);

                                String ContactPersonId = Config_Customer.getSharedPreferences(context, "pref_Customer", "ContactPersonId", "");
                                String ComplainNo = currentListData.getComplainNo();
                                String AuthCode = Config_Customer.getSharedPreferences(context, "pref_Customer", "AuthCode", "");
                                String newEscalationID = "";
                                for (int i = 0; i < EscaltionList.size(); i++) {
                                    if (EscaltionList.get(i).getEscalationShortCode().compareTo(selectedRadioButtonText) == 0) {
                                        newEscalationID = EscaltionList.get(i).getEscalationID();
                                        break;
                                    }
                                }
                                String escDetails[] = {ContactPersonId, ComplainNo, newEscalationID, AuthCode};

                                new UpdateEscalation(context).execute(escDetails);
                                dialog.dismiss();
                            }
                        });

                        altDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(context, ComplaintsActivity.class);
                                //                               intent.putExtra("status", currentListData.getStatus());
                                context.startActivity(intent);
                            }
                        });
                        altDialog.show();
                    }
                });


            }
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) view.getTag();
        }

        int FeedbackStatus = Integer.parseInt(currentListData.getIsFeedback());
        if (FeedbackStatus == 0) {
            mViewHolder.linearLayoutfeedback.setVisibility(View.GONE);
        } else {
            mViewHolder.linearLayoutfeedback.setVisibility(View.VISIBLE);
        }

        int EscalationLevel = Integer.parseInt(currentListData.getIsChangeEscalationLevel());
        if (EscalationLevel != 1) {
            mViewHolder.radioGroup.setVisibility(View.GONE); // hide radio button when complain completed
        } else {
            mViewHolder.radioGroup.setVisibility(View.VISIBLE);
        }

        mViewHolder.txt_complaint_title.setText(currentListData.getComplaintTitle());
        mViewHolder.txt_complain_no.setText(currentListData.getComplainNo());
        mViewHolder.txt_complain_date.setText(currentListData.getComplainTimeText());
        mViewHolder.txt_complain_type.setText(currentListData.getTransactionTypeName());
        mViewHolder.txt_machine.setText(currentListData.getModelName());
        mViewHolder.txt_engineer.setText(currentListData.getEngineerName());
        mViewHolder.txt_compalint_status.setText(currentListData.getWorkStatusName());
        mViewHolder.txt_plant_name.setText(currentListData.getSiteAddress());
        mViewHolder.txt_status.setText(currentListData.getStatusText());
        mViewHolder.txt_level.setText(currentListData.getEscalationName());

        mViewHolder.card_view_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context,"hello",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(context, ComplainListDetailActivity.class);
                intent.putExtra("ComplainNo", currentListData.getComplainNo());
                intent.putExtra("status", Status);
                context.startActivity(intent);
                ((Activity) context).finish();

            }
        });


        mViewHolder.linearLayoutfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComplainFeedbackActivity.class);
                intent.putExtra("ComplainNo", currentListData.getComplainNo());
                intent.putExtra("ModelName", currentListData.getModelName());
                intent.putExtra("EnginerName", currentListData.getEngineerName());
                intent.putExtra("status", "");
                context.startActivity(intent);
            }
        });


        return view;
    }

    class MyViewHolder {
        TextView txt_complaint_title, txt_complain_no, txt_complain_date, txt_complain_type, txt_machine, txt_plant_name,
                txt_engineer, txt_compalint_status, txt_status, txt_level;
        RadioGroup radioGroup;
        CardView card_view;
        LinearLayout card_view_linear, linearLayoutfeedback;

        MyViewHolder(View view) {
            card_view = view.findViewById(R.id.card_view);
            card_view_linear = view.findViewById(R.id.card_view_linear);
            txt_complaint_title = view.findViewById(R.id.txt_complaint_title);
            txt_complain_no = view.findViewById(R.id.txt_complain_no);
            txt_complain_date = view.findViewById(R.id.txt_complain_date);
            txt_complain_type = view.findViewById(R.id.txt_complain_type);
            txt_machine = view.findViewById(R.id.txt_machine);
            txt_engineer = view.findViewById(R.id.txt_engineer);
            txt_compalint_status = view.findViewById(R.id.txt_compalint_status);
            txt_plant_name = view.findViewById(R.id.txt_plant_name);
            txt_status = view.findViewById(R.id.txt_status);
            txt_level = view.findViewById(R.id.txt_level);
            radioGroup = view.findViewById(R.id.radioGroup);
            linearLayoutfeedback = view.findViewById(R.id.linearLayoutfeedback);

        }
    }
}
