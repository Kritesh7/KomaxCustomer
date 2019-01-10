package com.cfcs.komaxcustomer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.cfcs.komaxcustomer.R;
import com.cfcs.komaxcustomer.customer_activity.ServiceReportDetail;
import com.cfcs.komaxcustomer.models.DailyReportDataModel;
import java.util.ArrayList;

public class ServiceReportListAdapter extends BaseAdapter {

    private ArrayList<DailyReportDataModel> DailyReportDataModelsList = new ArrayList<DailyReportDataModel>();

    private LayoutInflater inflater;
    private Context context;

    public ServiceReportListAdapter(Context context, ArrayList<DailyReportDataModel> dailyReportDataModelsList) {
        this.DailyReportDataModelsList = dailyReportDataModelsList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);

    }

    @Override
    public int getCount() {
        return DailyReportDataModelsList.size();
    }


    @Override
    public DailyReportDataModel getItem(int position) {
        return DailyReportDataModelsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ServiceReportListAdapter.MyViewHolder mViewHolder;

        final DailyReportDataModel currentListData = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.service_report_list_layout,
                    parent, false);
            mViewHolder = new ServiceReportListAdapter.MyViewHolder(convertView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ServiceReportListAdapter.MyViewHolder) convertView.getTag();
        }

        mViewHolder.txt_report_no.setText("Report No :" +" "+currentListData.getDailyReportPrintNo());
        mViewHolder.txt_report_date.setText(currentListData.getDailyReportDateText());
        mViewHolder.txt_work_done.setText(currentListData.getWorkdone());
        mViewHolder.txt_next_follow_up.setText(currentListData.getNextFollowUpDateText());


        mViewHolder.card_view_dailyReoprt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"hello",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(context, ServiceReportDetail.class);
                intent.putExtra("DailyReportNo", currentListData.getDailyReportNo());
                intent.putExtra("ComplainNo", currentListData.getComplainNo());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });


        return convertView;
    }

    class MyViewHolder {
        TextView txt_report_no, txt_report_date, txt_work_done, txt_next_follow_up;
        LinearLayout card_view_dailyReoprt;

        //Button btnDetail;
        public MyViewHolder(View view) {
            card_view_dailyReoprt = view.findViewById(R.id.card_view_dailyReoprt);
            txt_report_no = view.findViewById(R.id.txt_report_no);
            txt_report_date = view.findViewById(R.id.txt_report_date);
            txt_work_done = view.findViewById(R.id.txt_work_done);
            txt_next_follow_up = view.findViewById(R.id.txt_next_follow_up);


        }
    }

}
