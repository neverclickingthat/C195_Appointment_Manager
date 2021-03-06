/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C195_Appointment_Manager_View_Controller;

import C195_Appointment_Manager_Models.Customer;
import static C195_Appointment_Manager_Models.DatabaseManager.getCustomerList;
import static C195_Appointment_Manager_Models.DatabaseManager.updateCustomerList;
import static C195_Appointment_Manager_Models.DatabaseManager.setCustomerInactive;
import static C195_Appointment_Manager_Models.DatabaseManager.generateAppointmentTypeByMonthReport;
import static C195_Appointment_Manager_Models.DatabaseManager.generateScheduleForConsultants;
import static C195_Appointment_Manager_Models.DatabaseManager.generateScheduleForCustomers;
import static C195_Appointment_Manager_Models.DatabaseManager.logInAppointmentNotification;
import static C195_Appointment_Manager_Models.DatabaseManager.updateAppointmentScreenTable;


import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.util.Optional;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import javafx.scene.control.Alert.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

/**
 *
 * @author Mothy
 */
public class PrimaryScreenController {
    
    
    @FXML private Button primaryScreenNewCustomerButton;
    @FXML private Button primaryScreenModifyCustomerButton;
    @FXML private Button primaryScreenDeactivateCustomerButton;
    @FXML private Button primaryScreenNewAppointmentButton;
    @FXML private Button primaryScreenModifyAppointmentButton;
    @FXML private Button primaryScreenDeleteAppointmentButton;
    @FXML private Button primaryScreenTypesByMonthReportButton;
    @FXML private Button primaryScreenConsultantScheduleReportButton;
    @FXML private Button primaryScreenCustomerAdminReportButton;
    
    @FXML
    private TableView<Customer> primaryScreenCustomerTable;
    
    @FXML
    private TableColumn<Customer, String> primaryScreenTableNameColumn;
    
    @FXML
    private TableColumn<Customer, String> primaryScreenTableAddressColumn;
    
    @FXML
    private TableColumn<Customer, String> primaryScreenTableCityColumn;   

    @FXML
    private TableColumn<Customer, String> primaryScreenTablePhoneColumn;
            
    @FXML
    private TableColumn<Customer, Integer> primaryScreenTableActiveColumn;                
                    
    @FXML
    private TableColumn<Customer, String> primaryScreenTablePendingColumn;                         
          
    @FXML
    private TableColumn<Customer, Integer> primaryScreenTableIDColumn;  
      
    @FXML
    private Label reportStatusText;                      
                            
    
    
    @FXML
    void handlePrimaryScreenNewCustomerButton (ActionEvent event) throws IOException {  
        switchToCustomerScreen(event);        
         }
    
    @FXML
    void handlePrimaryScreenModifyCustomerButton (ActionEvent event) throws IOException {  
        switchToModifyScreen(event);
         }
    
    @FXML
    void handlePrimaryScreenDeactivateCustomerButton (ActionEvent event) throws IOException { 
        removeCustomer();
         }
    
    @FXML
    void handlePrimaryScreenNewAppointmentButton (ActionEvent event) throws IOException { 
        switchToAppointmentScreen(event);
         }
    @FXML
    void handlePrimaryScreenModifyAppointmentButton (ActionEvent event) throws IOException { 
        switchToAppointmentScreen(event);
         }
    @FXML
    void handlePrimaryScreenDeleteAppointmentButton (ActionEvent event) throws IOException { 
    //    switchToModifyScreen(event);
         }
    @FXML
    void handlePrimaryScreenTypesByMonthReportButton (ActionEvent event) throws IOException, ParseException, SQLException {
        generateAppointmentTypeByMonthReport();
        reportStatusText.setText("Appointment By Month Report Generated");
         }
    @FXML
    void handlePrimaryScreenConsultantScheduleReportButton (ActionEvent event) throws IOException, ParseException, SQLException {
        generateScheduleForConsultants();
        reportStatusText.setText("Consultant Schedule Report Generated");
         }
    @FXML
    void handlePrimaryScreenCustomerAdminReportButton (ActionEvent event) throws IOException {
        try {
            generateScheduleForCustomers();
            reportStatusText.setText("Customer Schedule Report Generated");
        } catch (ParseException ex) {
            Logger.getLogger(PrimaryScreenController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
         }

//TN: Index of customer to be modified on modify screen:
    public static int customerIndexToModify;
//TN: Holds the value that dictates if the Modify or New Customer screen oprions should appear
    private static boolean isModify;
    
    public static Customer customerToBeModified;
    
    public void switchToCustomerScreen(ActionEvent event) throws IOException {
        isModify = false;
        Parent loader = FXMLLoader.load(getClass().getResource("CustomerScreen.fxml"));
        Scene partsScreen = new Scene(loader);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(partsScreen);
        window.show();
    }  
    
// Open modify customer window
    @FXML
    public void switchToModifyScreen(ActionEvent event) {
        isModify = true;
        // Get selected customer from table view
        customerToBeModified = primaryScreenCustomerTable.getSelectionModel().getSelectedItem();
        // Set the index of the customer to be modified
        
        // Check if no customer was selected
        if (customerToBeModified == null) {
            // Create alert saying a customer must be selected to be modified
            //ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
            Alert customerExists = new Alert(Alert.AlertType.INFORMATION);
                    customerExists.setTitle("ERROR");
                    customerExists.setHeaderText("Error selecting customer");
                    customerExists.setContentText("Must select customer to modify");
                    customerExists.showAndWait();
            return;
        }
        int customerIndexToModify = customerToBeModified.getCustomerId();
        // Open modify customer window
        try {
            Parent modifyCustomerParent = FXMLLoader.load(getClass().getResource("CustomerScreen.fxml"));
            Scene modifyCustomerScene = new Scene(modifyCustomerParent);
            Stage modifyCustomerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modifyCustomerStage.setScene(modifyCustomerScene);
            modifyCustomerStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
 // Return the customer index to be modified
    public static Customer getCustomerToModify() {
        return customerToBeModified;
    }
// Return the customer index to be modified
    public static int getCustomerIndexToModify() {
        return customerIndexToModify;
    }
 // Return the "isModify" value dictating which customer screen launches:
    public static boolean getModifyDecider() {
        return isModify;
    }

    public void switchToAppointmentScreen(ActionEvent event) throws IOException {
            Parent mainScreenParent = FXMLLoader.load(getClass().getResource("AppointmentScreen.fxml"));
            Scene mainScreenScene = new Scene(mainScreenParent);
            Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainScreenStage.setScene(mainScreenScene);
            mainScreenStage.show();
    }  
    
    
// Remove the selected customer
    @FXML
    private void removeCustomer() {
        // Get selected customer from table view
        Customer deactivatedCustomer = primaryScreenCustomerTable.getSelectionModel().getSelectedItem();
        // Check if no customer was selected
        //if (customerToRemove == null) {
            // Create alert saying a customer must be selected to be removed
        //    ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
        //    Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //    alert.setTitle(rb.getString("error"));
        //    alert.setHeaderText(rb.getString("errorRemovingCustomer"));
        //    alert.setContentText(rb.getString("errorRemovingCustomerMessage"));
        //    alert.showAndWait();
        //    return;
        //}
        // Submit customer to be removed
        setCustomerInactive(deactivatedCustomer);
    }
// Initialize screen elements
    @FXML
    public void initialize() throws ParseException, SQLException {
        LocalDate currentTableDate;
        int dateConfiguration = 1;
        
        dateConfiguration = 1;
        currentTableDate = LocalDate.now();
        
        updateAppointmentScreenTable(currentTableDate, dateConfiguration);;
        logInAppointmentNotification();
        // Assign actions to buttons
        //btnAddAppointment.setOnAction(event -> openAddAppointment(event));
        //btnAppointmentSummary.setOnAction(event -> openAppointmentSummary(event));
        //btnReports.setOnAction(event -> openReports(event));
        //btnMainScreenCurrentDate.setOnAction(event -> goToCurrentDate(event));
        //btnMainScreenToggleView.setOnAction(event -> toggleCalendarView(event));
        //btnAddCustomer.setOnAction(event -> openAddCustomer(event));
        //btnModifyCustomer.setOnAction(event -> openModifyCustomer(event));
        //btnRemoveCustomer.setOnAction(event -> removeCustomer(event));
        //btnExit.setOnAction(event -> exit(event));
        //// Update appointment list
        //updateAppointmentList();
        //// Create calendarView and add to gridPane
        //monthlyCalendar = new MonthlyCalendarView(YearMonth.now());
        //monthlyCalendarView = monthlyCalendar.getView();
        //mainScreenGrid.add(monthlyCalendarView, 0, 0);
        

    // Assign data to table view
        primaryScreenTableNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        primaryScreenTableAddressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        primaryScreenTablePhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        primaryScreenTableActiveColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty().asObject());
        primaryScreenTableCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        primaryScreenTableIDColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
    //TN: Update the table:
        updatePrimaryScreenCustomerTable();
        
    // Create appointment notifications if any need to be shown
        //logInAppointmentNotification();
        
    }

    //TN: Updates the primary screen table:
    public void updatePrimaryScreenCustomerTable() {
        updateCustomerList();
        primaryScreenCustomerTable.setItems(getCustomerList());
    }
    
  
    

    }
    
    
    
    
    
    
    

