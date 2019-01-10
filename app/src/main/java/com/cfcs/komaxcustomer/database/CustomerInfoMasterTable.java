package com.cfcs.komaxcustomer.database;

public class CustomerInfoMasterTable {
    public static final String tableName = "CustomerMaster";
    public static final String name = "Name";
    public static final String email = "Email";
    public static final String phone = "Phone";


    public static final String customerMasterTableData =
            "create table " + tableName +
                    " (" +
                    name + " text, " +
                    email + " text, " +
                    phone + " text" +
                    ");";
}
