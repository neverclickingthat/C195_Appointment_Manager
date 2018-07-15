/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C195_Appointment_Manager_Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Appointment {

    private final IntegerProperty appointmentId;
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty title;
    private final StringProperty location;
    private final StringProperty contact;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private final StringProperty stringDate;
    private final StringProperty appointmentTimeString;
    private final StringProperty createdBy;

    //TN: Appointment constructor:
    public Appointment() {
        appointmentId = new SimpleIntegerProperty();
        customerId = new SimpleIntegerProperty();
        customerName = new SimpleStringProperty();
        title = new SimpleStringProperty();
        location = new SimpleStringProperty();
        contact = new SimpleStringProperty();
        stringDate = new SimpleStringProperty();
        //SimpleDateFormat formatDate = new SimpleDateFormat("MM-dd-yyyy");
        //dateString = new SimpleStringProperty(formatDate.format(date));
        //SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a z");
        appointmentTimeString = new SimpleStringProperty();
        //endString = new SimpleStringProperty(formatTime.format(endDate));
        createdBy = new SimpleStringProperty();
    }

    //TN: Setters:
    public void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    
    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    //public void setDate(Date date) {
    //    this.date = date;
    //}
    
    public void setStringDate(String stringDate) {
        this.stringDate.set(stringDate);
    }
    
    public void setAppointmentTimeString(String appointmentTimeString) {
        this.appointmentTimeString.set(appointmentTimeString);
    }

    public void setCreatedBy (String createdBy) {
        this.createdBy.set(createdBy);
    }

    
    
    
    //TN: Getetrs
    public int getCustomerId() {
        return this.customerId.get();
    }

    public String getCustomerName() {
        return this.customerName.get();
    }
    
    public int getAppointmentId() {
        return this.appointmentId.get();
    }
    
    public String getTitle() {
        return this.title.get();
    }
    
    public String getContact() {
        return this.contact.get();
    }

    //public Date getDate() {
    //    return this.date;
    //}

    //public String getDateString() {
    //    return this.dateString.get();
    //}
    
    public String getStringDate() {
        return this.stringDate.get();
    }
    
    public Timestamp getStartTimestamp() {
        return this.startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return this.endTimestamp;
    } 
    
    public String getLocation() {
        return this.location.get();
    }
    
    public String appointmentTimeString() {
        return this.appointmentTimeString.get();
    }
    
    public String getCreatedBy() {
        return this.createdBy.get();
    }

    

    public IntegerProperty customerIdProperty() {
        return customerId;
    }   

    public StringProperty customerNameProperty() {
        return customerName;
    }   

    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }  

    public StringProperty titleProperty() {
        return title;
    }
 
    public StringProperty contactProperty() {
        return contact;
    }
    
    //public Date dateProperty() {
    //    return date;
    //}
    
    //public StringProperty dateStringProperty() {
    //    return dateString;
    //}
    
    public StringProperty stringDateProperty() {
        return stringDate;
    }    
    
    public StringProperty locationProperty() {
        return location;
    }   

    public StringProperty appointmentTimeStringProperty() {
        return appointmentTimeString;
    }

}

    

