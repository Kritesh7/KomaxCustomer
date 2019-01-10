package com.cfcs.komaxcustomer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.customer_activity.MachinesActivity;
import com.cfcs.komaxcustomer.customer_activity.MachinesDetailActivity;
import com.cfcs.komaxcustomer.models.MachinesDataModel;

import java.util.ArrayList;

/**
 * Created by Admin on 13-03-2018.
 */

public class MachineListAdapter extends BaseAdapter {

    ArrayList<MachinesDataModel> machinesList = new ArrayList<MachinesDataModel>();
    LayoutInflater inflater;
    Context mContext;

    View traning_main_lay;


    public MachineListAdapter(Context context, ArrayList<MachinesDataModel> machinesList) {
        this.machinesList = machinesList;
        this.mContext = context;
        inflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public MachinesDataModel getItem(int position) {

        return machinesList.get(position);
    }

    @Override
    public int getCount() {
        return machinesList.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        MyViewHolder mViewHolder;


        if (view == null) {
            view = inflater.inflate(R.layout.machines_list_layout,
                    viewGroup, false);
            mViewHolder = new MyViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) view.getTag();
        }
        final MachinesDataModel currentListData = getItem(i);

        mViewHolder.txt_srno.setText(" "
                + currentListData.getCounter());

        mViewHolder.txt_serial_no.setText(" "
                + currentListData.getSerialNo());
        mViewHolder.txt_principal_name.setText("" + currentListData.getPrincipleName());

        mViewHolder.txt_model.setText(" "
                + currentListData.getModelName());
        mViewHolder.txt_plant.setText(" "
                + currentListData.getPlant());
        mViewHolder.txt_customer.setText(" "
                + currentListData.getParentCustomerName());
        mViewHolder.txt_model.setText(" "
                + currentListData.getModelName());
        mViewHolder.txt_type.setText(" "
                + currentListData.getTransactionTypeName());
        //mViewHolder.textView1Priority.setText(""+currentListData.getPriority());
        mViewHolder.txt_wa_start_date.setText(" "
                + currentListData.getWarrantyStartDateText());
        mViewHolder.txt_wa_end_date.setText(" "
                + currentListData.getWarrantyEndDateText());


        mViewHolder.traning_main_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(mContext, MachinesDetailActivity.class);
                intent.putExtra("SaleID", currentListData.getSaleID());
                mContext.startActivity(intent);
                ((Activity) mContext).finish();

            }
        });
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(mContext, MachinesDetailActivity.class);
                intent.putExtra("SaleID", currentListData.getSaleID());
                mContext.startActivity(intent);
                ((Activity) mContext).finish();

            }
        });

        mViewHolder.card_view_machine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(mContext, MachinesDetailActivity.class);
                intent.putExtra("SaleID", currentListData.getSaleID());
                mContext.startActivity(intent);
                ((Activity) mContext).finish();

            }
        });


        return view;
    }

    class MyViewHolder {
        TextView txt_srno, txt_serial_no, txt_principal_name, txt_model, txt_plant, txt_customer, txt_type, txt_wa_start_date, txt_wa_end_date;
        CardView traning_main_lay;
        LinearLayout card_view_machine;

        //Button btnDetail;
        public MyViewHolder(View item) {
            txt_srno = (TextView) item
                    .findViewById(R.id.txt_srno);
            txt_serial_no = (TextView) item.findViewById(R.id.txt_serial_no);
            txt_principal_name = (TextView) item
                    .findViewById(R.id.txt_principal_name);
            txt_model = (TextView) item
                    .findViewById(R.id.txt_model);
            txt_plant = item.findViewById(R.id.txt_plant);
            txt_customer = (TextView) item
                    .findViewById(R.id.txt_customer);
            txt_type = (TextView) item
                    .findViewById(R.id.txt_type);
            txt_wa_start_date = (TextView) item
                    .findViewById(R.id.txt_wa_start_date);
            //textView1Priority = (TextView) item.findViewById(R.id.textView1Priority);
            txt_wa_end_date = (TextView) item
                    .findViewById(R.id.txt_wa_end_date);
            traning_main_lay = (CardView) item.findViewById(R.id.traning_main_lay);
            card_view_machine = item.findViewById(R.id.card_view_machine);


        }
    }
}
