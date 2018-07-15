
package C195_Appointment_Manager_Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 *
 * @author Mothy
 */
public class Customer {
    
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty address;
    private final StringProperty phone;
    private final StringProperty city;
    private final IntegerProperty active;
    

    //TN: Customer constructor:
    public Customer() {
        customerId = new SimpleIntegerProperty();
        customerName = new SimpleStringProperty();
        address = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        city = new SimpleStringProperty();
        active = new SimpleIntegerProperty();
        
    }

    //TN: Setters:
    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public void setCity(String city) {
        this.city.set(city);
    }
    
    public void setActive(int active) {
        this.active.set(active);
    }
    
    //public void setAddressId(int addressId) {
    //this.addressId.set(addressId);
    //}

    //TN: Getters:
    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public StringProperty addressProperty() {
        return address;
    }
    
    public StringProperty phoneProperty() {
    return phone;
    }
    
    public StringProperty cityProperty() {
    return city;
    }
    
    public IntegerProperty activeProperty() {
    return active;
    }
    
    public int getCustomerId() {
        return this.customerId.get();
    }

    public String getCustomerName() {
        return this.customerName.get();
    }

    public String getAddress() {
        return this.address.get();
    }

    public String getPhone() {
        return this.phone.get();
    }
    
    public String getCity() {
        return this.city.get();
    }
    
    public int getActive() {
        return active.get();
    }

}


