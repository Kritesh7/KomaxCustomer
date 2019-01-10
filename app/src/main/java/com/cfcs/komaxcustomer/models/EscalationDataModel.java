package com.cfcs.komaxcustomer.models;

/**
 * Created by Admin on 16-03-2018.
 */

public class EscalationDataModel {

    String EscalationID;
    String EscalationShortCode;
    String EscalationName;

    public String getEscalationName() {
        return EscalationName;
    }

    public void setEscalationName(String escalationName) {
        EscalationName = escalationName;
    }

    public String getEscalationID() {
        return EscalationID;
    }

    public void setEscalationID(String escalationID) {
        EscalationID = escalationID;
    }

    public String getEscalationShortCode() {
        return EscalationShortCode;
    }

    public void setEscalationShortCode(String escalationShortCode) {
        EscalationShortCode = escalationShortCode;
    }
}
