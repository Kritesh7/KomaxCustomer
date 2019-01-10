package com.cfcs.komaxcustomer.models;

/**
 * Created by Admin on 13-03-2018.
 */

public class MachinesDataModel {

    String SaleID;
    String DateOfInstallationText;
    String WarrantyStartDateText;
    String WarrantyEndDateText;
    String AMCStartDateText;
    String AMCEndDateText;
    String DateOfSupplyText;
    String SerialNo;
    String PrincipleName;
    String ModelName;
    String ParentCustomerName;
    String TransactionTypeName;
    String Counter;
    String Plant;

    public MachinesDataModel(String SaleID, String DateOfInstallationText,
                             String WarrantyStartDateText, String WarrantyEndDateText, String AMCStartDateText,
                             String AMCEndDateText, String DateOfSupplyText, String SerialNo, String PrincipleName, String ModelName,
                             String ParentCustomerName, String TransactionTypeName, String Counter, String Plant) {
        this.SaleID = SaleID;
        this.DateOfInstallationText = DateOfInstallationText;
        this.WarrantyStartDateText = WarrantyStartDateText;
        this.WarrantyEndDateText = WarrantyEndDateText;
        this.AMCStartDateText = AMCStartDateText;
        this.AMCEndDateText = AMCEndDateText;
        this.DateOfSupplyText = DateOfSupplyText;
        this.SerialNo = SerialNo;
        this.PrincipleName = PrincipleName;
        this.ModelName = ModelName;
        this.ParentCustomerName = ParentCustomerName;
        this.TransactionTypeName = TransactionTypeName;
        this.Counter = Counter;
        this.Plant = Plant;
    }


    public String getCounter() {
        return Counter;
    }

    public void setCounter(String counter) {
        Counter = counter;
    }

    public String getSaleID() {
        return SaleID;
    }

    public void setSaleID(String saleID) {
        SaleID = saleID;
    }

    public String getDateOfInstallationText() {
        return DateOfInstallationText;
    }

    public void setDateOfInstallationText(String dateOfInstallationText) {
        DateOfInstallationText = dateOfInstallationText;
    }

    public String getPlant() {
        return Plant;
    }

    public void setPlant(String plant) {
        Plant = plant;
    }

    public String getWarrantyStartDateText() {
        return WarrantyStartDateText;
    }

    public void setWarrantyStartDateText(String warrantyEndDateText) {
        WarrantyStartDateText = warrantyEndDateText;
    }

    public String getWarrantyEndDateText() {
        return WarrantyEndDateText;
    }

    public void setWarrantyEndDateText(String warrantyEndDateText) {
        WarrantyEndDateText = warrantyEndDateText;
    }

    public String getAMCStartDateText() {
        return AMCStartDateText;
    }

    public void setAMCStartDateText(String amcStartDateText) {
        AMCStartDateText = amcStartDateText;
    }

    public String getAMCEndDateText() {
        return AMCEndDateText;
    }

    public void setAMCEndDateText(String amcEndDateText) {
        AMCEndDateText = amcEndDateText;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getPrincipleName() {
        return PrincipleName;
    }

    public void setPrincipleName(String principleName) {
        PrincipleName = principleName;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        ModelName = modelName;
    }

    public String getParentCustomerName() {
        return ParentCustomerName;
    }

    public void setParentCustomerName(String parentCustomerName) {
        ParentCustomerName = parentCustomerName;
    }

    public String getTransactionTypeName() {
        return TransactionTypeName;
    }

    public void setTransactionTypeName(String transactionTypeName) {
        TransactionTypeName = transactionTypeName;
    }

    public String getDateOfSupplyText() {
        return DateOfSupplyText;
    }

    public void setDateOfSupplyText(String dateOfSupplyText) {
        DateOfSupplyText = dateOfSupplyText;
    }

}
