package com.cfcs.komaxcustomer.models;

public class PendingFeedbackDataModel {

    String ComplainNo;
    String MachineSerialNo;
    String ComplaintTitle;
    String EngineerName;

    public String getEngName() {
        return EngineerName;
    }

    public void setEngName(String engName) {
        this.EngineerName = engName;
    }

    public String getSerialNo() {
        return MachineSerialNo;
    }

    public void setSerialNo(String serialNo) {
        MachineSerialNo = serialNo;
    }

    public String getComplaintTitleName() {
        return ComplaintTitle;
    }

    public void setComplaintTitleName(String complaintTitleName) {
        ComplaintTitle = complaintTitleName;
    }

    public String getComplainNo() {
        return ComplainNo;
    }

    public void setComplainNo(String complainNo) {
        ComplainNo = complainNo;
    }
}