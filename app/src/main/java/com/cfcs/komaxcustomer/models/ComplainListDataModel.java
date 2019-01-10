package com.cfcs.komaxcustomer.models;

/**
 * Created by Admin on 16-03-2018.
 */

public class ComplainListDataModel {

    String EscalationID;
    String EngineerName;
    String PriorityName;
    String StatusText;
    String ComplainNo;
    String IsServiceReport;
    String IsFeedback;
    String SiteAddress;
    String ComplainTimeText;
    String EscalationShortCode;
    String TransactionTypeName;
    String ComplaintTitle;
    String WorkStatusName;
    String IsChangeEscalationLevel;
    String ModelName;
    String EscalationName;


    public ComplainListDataModel(String ComplainNo, String ComplaintTitle, String ComplainTimeText, String ModelName, String SiteAddress,
                                 String EngineerName, String PriorityName, String WorkStatusName, String TransactionTypeName, String EscalationID,
                                 String EscalationShortCode, String EscalationName, String StatusText, String IsServiceReport, String IsFeedback, String IsChangeEscalationLevel) {
    }


    public String getEscalationID
            () {
        return EscalationID;
    }

    public void setEscalationID(String EscalationID) {
        this.EscalationID = EscalationID;
    }


    public String getEngineerName() {
        return EngineerName;
    }

    public void setEngineerName(String EngineerName) {
        this.EngineerName = EngineerName;
    }

    public String getSiteAddress() {
        return SiteAddress;
    }

    public void setSiteAddress(String SiteAddress) {
        this.SiteAddress = SiteAddress;
    }

    public String getPriorityName() {
        return PriorityName;
    }

    public void setPriorityName(String PriorityName) {
        this.PriorityName = PriorityName;
    }

    public String getComplaintTitle() {
        return ComplaintTitle;
    }

    public void setComplaintTitle(String ComplaintTitle) {
        this.ComplaintTitle = ComplaintTitle;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String StatusText) {
        this.StatusText = StatusText;
    }


    public String getComplainNo() {
        return ComplainNo;
    }

    public void setComplainNo(String ComplainNo) {
        this.ComplainNo = ComplainNo;
    }


    public String getIsServiceReport() {
        return IsServiceReport;
    }

    public void setIsServiceReport(String IsServiceReport) {
        this.IsServiceReport = IsServiceReport;
    }


    public String getIsFeedback() {
        return IsFeedback;
    }

    public void setIsFeedback(String IsFeedback) {
        this.IsFeedback = IsFeedback;
    }


    public String getComplainTimeText() {
        return ComplainTimeText;
    }

    public void setComplainTimeText(String ComplainTimeText) {
        this.ComplainTimeText = ComplainTimeText;
    }

    public String getEscalationShortCode() {
        return EscalationShortCode;
    }

    public void setEscalationShortCode(String EscalationShortCode) {
        this.EscalationShortCode = EscalationShortCode;
    }

    public String getTransactionTypeName() {
        return TransactionTypeName;
    }

    public void setTransactionTypeName(String TransactionTypeName) {
        this.TransactionTypeName = TransactionTypeName;
    }

    public String getWorkStatusName() {
        return WorkStatusName;
    }

    public void setWorkStatusName(String WorkStatusName) {
        this.WorkStatusName = WorkStatusName;
    }

    public String getIsChangeEscalationLevel() {
        return IsChangeEscalationLevel;
    }

    public void setIsChangeEscalationLevel(String IsChangeEscalationLevel) {
        this.IsChangeEscalationLevel = IsChangeEscalationLevel;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String ModelName) {
        this.ModelName = ModelName;
    }

    public String getEscalationName() {
        return EscalationName;
    }

    public void setEscalationName(String EscalationName) {
        this.EscalationName = EscalationName;
    }


//    @Override
//    public String toString()
//    {
//        return "ClassPojo [EngineerName = "+EngineerName+", ZoneName = "+ZoneName+", PriorityName = "+PriorityName+", ComplainDevice = "+ComplainDevice+", StatusText = "+StatusText+", ComplainServiceTypeName = "+ComplainServiceTypeName+", ComplainNo = "+ComplainNo+", LogByFullName = "+LogByFullName+", IsServiceReportFill = "+IsServiceReportFill+", ComplainDateText = "+ComplainDateText+", IsFeedback = "+IsFeedback+", CustomerName = "+CustomerName+", ParentCustomerName = "+ParentCustomerName+", ComplainTimeText = "+ComplainTimeText+", EscalationShortCode = "+EscalationShortCode+", TransactionTypeName = "+TransactionTypeName+", Status = "+Status+", Colour = "+Colour+", WorkStatusName = "+WorkStatusName+", IsChangeEscalationLevel = "+IsChangeEscalationLevel+", ModelName = "+ModelName+", LoggedBy = "+LoggedBy+", FeedbackStatus = "+FeedbackStatus+", ShortCode = "+ShortCode+", EscalationName = "+EscalationName+", RectifiedDateText = "+RectifiedDateText+"]";
//    }

}
