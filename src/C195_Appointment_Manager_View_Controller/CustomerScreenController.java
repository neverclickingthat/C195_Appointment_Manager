/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C195_Appointment_Manager_View_Controller;

import static C195_Appointment_Manager_Models.DatabaseManager.addCustomer;
import static C195_Appointment_Manager_Models.DatabaseManager.modifyCustomer;
import static C195_Appointment_Manager_Models.DatabaseManager.populateCityList;
import C195_Appointment_Manager_Models.Customer; 

import java.util.Optional;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
/**
 *
 * @author Mothy
 */
public class CustomerScreenController {
    
    //TN: Customer screen "SAVE" button
    @FXML private Button customerScreenSaveButton;
    
    //TN: Customer screen "CANCEL" button
    @FXML private Button customerScreenCancelButton;
    
    //TN: Customer screen customer name text field:
    @FXML
    private TextField customerScreenName;
    
    //TN: Customer screen address text field:
    @FXML
    private TextField customerScreenAddress;  
    
    //TN: Customer screen phone number text field:
    @FXML
    private TextField  customerScreenPhone;
    
    //TN: Customer screen label - will change if adding or modifying:
    @FXML
    private Label customerScreenLabel;
    
    //TN: Customer screen city dropdown selector:
    @FXML
    private ComboBox customerScreenCityDropdown;
        
    @FXML
    void handleCustomerScreenSaveButton (ActionEvent event) throws IOException {        
         }
    
    @FXML
    void handleCustomerScreenCancelButton (ActionEvent event) throws IOException {        
        
         }
    
    @FXML
    void handleCustomerScreenCityDropdown (ActionEvent event) throws IOException {        
        
        }

    ObservableList<String> cities = populateCityList();
    
    //TN: intent-to-modify variable used to determine if modify or add screen 
    //function is initiated
    boolean launchModify = PrimaryScreenController.getModifyDecider();
    
    //TN: stores customer destined for modification: 
    private static Customer customerToModify;
        
    //TN: Class constructor: 
    public CustomerScreenController() {
        this.customerToModify = PrimaryScreenController.getCustomerToModify();
    }
    
    //TN: Adds customer to database:
    @FXML
    private void saveAddCustomer(ActionEvent event) {
        
        //TN: pocket Customer ID for database write after modifications
        int customerIdToModify;  

        //TN: retrieves data from text boxes:
        String customerName = customerScreenName.getText();
        String address = customerScreenAddress.getText();
        String phone = customerScreenPhone.getText();
        
        if (customerName.equals("") || address.equals("") || phone.equals("")) {
                    Alert badInput = new Alert(Alert.AlertType.INFORMATION);
                    badInput .setTitle("ERROR");
                    badInput .setHeaderText("Error adding customer");
                    badInput .setContentText("All fields must have a value");
                    badInput .showAndWait();
        }
        
        String city = customerScreenCityDropdown.getSelectionModel().getSelectedItem().toString();
        //TN: RegEx to force phone number format:
        String phonePattern = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
        
        //TN: if/then to check for phone pattern first, if valid then decides 
        //whether to launch the modify or add methods:
        if (phone.matches(phonePattern)) {
            if (launchModify == true) {
                customerIdToModify = customerToModify.getCustomerId();
                modifyCustomer(customerIdToModify, customerName, address, city, phone);
                goToMainScreen(event);
            } else {
            addCustomer(customerName, address, city, phone);
            goToMainScreen(event);
            }
        } else {
            Alert badNumber = new Alert(Alert.AlertType.INFORMATION);
                    badNumber .setTitle("ERROR");
                    badNumber .setHeaderText("Error adding phone number");
                    badNumber .setContentText("Phone number must match the "
                            + "following formats:      (XXX)XXX XXXX, " 
                            + "XXXXXXXXXX, XXX-XXX-XXXX ");
                    badNumber .showAndWait();
        }

    }  
    
    //TN: Jump to main screen:
    @FXML
    private void goToMainScreen(ActionEvent event) {
            try {            
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("PrimaryScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
                }
            catch (IOException e) {
                    e.printStackTrace();
                    }   
        
    }
    //TN: Cancel function:
    @FXML
    private void cancelAddCustomer(ActionEvent event) {
        //TN: Launches confirmation alert:
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Click OK to cancel");
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.get() == ButtonType.OK) {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("PrimaryScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TN: Initialize screen elements
    @FXML
    public void initialize() {
        //TN: Assign actions to buttons
        customerScreenSaveButton.setOnAction(event -> saveAddCustomer(event));
        customerScreenCancelButton.setOnAction(event -> cancelAddCustomer(event));
        customerScreenCityDropdown.setItems(cities);
        customerScreenCityDropdown.setValue("Select a City");
        //TN: If intent-to-modify is true change screen label and transfer
        //values into method argument variables:
        if (launchModify == true) {        
            customerScreenLabel.setText("Modify Customer");
            String customerName = customerToModify.getCustomerName();
            String address = customerToModify.getAddress();
            String phone = customerToModify.getPhone();
            String city = customerToModify.getCity();
        //TN: Sets selected customer data into fields:    
            customerScreenName.setText(customerName);
            customerScreenAddress.setText(address);
            customerScreenPhone.setText(phone);
            customerScreenCityDropdown.setValue(city);
        }
        
    }
    
}

