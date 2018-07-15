
package C195_Appointment_Manager_View_Controller;


import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.collections.ObservableList;
import java.util.List;

import static C195_Appointment_Manager_Models.DatabaseManager.updateAppointmentScreenTable;
import static C195_Appointment_Manager_Models.DatabaseManager.getAppointmentList;
import static C195_Appointment_Manager_Models.DatabaseManager.populateCustomerNamesList;
import static C195_Appointment_Manager_Models.DatabaseManager.populateContactList;
import static C195_Appointment_Manager_Models.DatabaseManager.populateLocationsList;
import static C195_Appointment_Manager_Models.DatabaseManager.populateAppointmentTimesList;
import static C195_Appointment_Manager_Models.DatabaseManager.deleteAppointment;

import C195_Appointment_Manager_Models.Appointment;
import static C195_Appointment_Manager_Models.DatabaseManager.addAppointment;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
/**
 *
 * @author Mothy
 */
public class AppointmentScreenControler {
    
    @FXML private Button appointmentScreenSaveButton;
    
    @FXML private Button appointmentScreenNewButton;
    
    @FXML private Button appointmentScreenModifyButton;
    
    @FXML private Button appointmentScreenDeleteButton;
    
    @FXML private Button appointmentScreenCancelButton;
    
    @FXML private Button appointmentScreenAllViewButton;
    
    @FXML private Button appointmentScreenWeekViewButton;
    
    @FXML private Button appointmentScreenMonthViewButton;
    
    @FXML private Button weekPreviousButton;
    
    @FXML private Button weekNextButton;
    
    @FXML private Button monthPreviousButton;
    
    @FXML private Button monthNextButton;
    
    @FXML
    private ComboBox appointmentScreenCustomerNameDropDown;
    
    @FXML
    private TextField appointmentScreenTitleText;
    
    @FXML
    private ComboBox appointmentScreenContactDropDown;
    
    @FXML
    private ComboBox appointmentScreenLocationDropDown;
    
    @FXML
    private DatePicker appointmentScreenDateBox;
    
    @FXML
    private ComboBox appointmentScreenTimeDropDown;
    
    @FXML
    private TableColumn<Appointment, Integer> appointmentTableIDColumn;   

    @FXML
    private TableColumn<Appointment, String> appointmentTableCustomerNameColumn;
    
    @FXML
    private TableColumn<Appointment, Integer> appointmentTableApptIDColumn; 
            
    @FXML
    private TableColumn<Appointment, String> appointmentTableTitleColumn;                
                    
    @FXML
    private TableColumn<Appointment, String> appointmentTableContactColumn; 

    @FXML
    private TableColumn<Appointment, String> appointmentTableDateColumn;                         
          
    @FXML
    private TableColumn<Appointment, String> appointmentTableTimeColumn;  
    
    @FXML
    private TableColumn<Appointment, String> appointmentTableLocationColumn;     
    
    @FXML
    private TableView<Appointment> appointmentTable;
    
    @FXML
    private TextField appointmentScreenApptIDText;
        
    @FXML
    private Label timeDisplayTextBox;
    
    ObservableList<String> customers = populateCustomerNamesList();
    
    ObservableList<String> contacts = populateContactList();
    
    ObservableList<String> locations = populateLocationsList();
    
    ObservableList<String> appointmentTimes = populateAppointmentTimesList();
    
    private static int dateConfiguration;
    
    private static LocalDate currentLocalDate;
    
    private static String timeZone = (TimeZone.getDefault().getDisplayName());
        
    
    //TN: handles a "NEW" button click by setting the modify intention marker
    //to "false" and clears all data entry fields:
    @FXML
    void handleAppointmentScreenNewButton (ActionEvent event) 
            throws IOException, ParseException {        
            modifyMarker = false;    
            appointmentScreenCustomerNameDropDown.setValue(null);
            appointmentScreenTitleText.setText(null);
            appointmentScreenContactDropDown.setValue(null);
            appointmentScreenLocationDropDown.setValue(null);
            appointmentScreenDateBox.setValue(null);
            appointmentScreenTimeDropDown.setValue(null);
            appointmentScreenApptIDText.setText(null);
        }
    
    @FXML
    void handleAppointmentScreenSaveButton (ActionEvent event) throws IOException {        
         }
    
    //TN: Handles "MODIFY" by populating the data entry fields with the selected
    //data:
    @FXML
    void handleAppointmentScreenModifyButton (ActionEvent event) throws IOException {
        populateAppointmentFields(event);
         }
    
    //TN: Handles "DELETE" by retrieving the selected appointment, sends it to 
    //the "deleteAppointment" method and resets the appointment table:
    @FXML
    void handleAppointmentScreenDeleteButton (ActionEvent event) 
            throws IOException, ParseException, SQLException {
        selectedForDeletion = appointmentTable.getSelectionModel().getSelectedItem();
        deleteAppointment(selectedForDeletion);
        updateAppointmentTableInitial();
        
         } 
    
    //TN: Handles the "CANCEL" button by initiating the "cancelAppointmentScreen"
    //method:
    @FXML
    void handleAppointmentScreenCancelButton(ActionEvent event) 
            throws IOException { 
        cancelAppointmentScreen(event);
         }
    
    //TN: Handles the "VIEW ALL" button by reinitializing the appointment table:
    @FXML
    void handleAppointmentScreenAllViewButton(ActionEvent event) 
            throws IOException, ParseException, SQLException { 
        updateAppointmentTableInitial();
         }
    
    //TN: Handles the "WEEK" view function bysetting the "dateConfiguration"
    //variable to "2" (signaling a week view), resetting the local 
    //date to now, retrieving the appointment table values and updating
    //the on-screen table with the new data:
    @FXML
    void handleAppointmentScreenWeekViewButton (ActionEvent event) 
            throws IOException, ParseException, SQLException { 
        dateConfiguration = 2;
        currentLocalDate = LocalDate.now();
        ObservableList<Appointment> allLocations;
        allLocations = appointmentTable.getItems();
        appointmentTable.getItems().remove(allLocations);
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);
         } 
    
    //TN: Handles the "WEEK" view function bysetting the "dateConfiguration"
    //variable to "3" (signaling a month view), resetting the local 
    //date to now, retrieving the appointment table values and updating
    //the on-screen table with the new data:
    @FXML
    void handleAppointmentScreenMonthViewButton (ActionEvent event) 
            throws IOException, ParseException, SQLException {
        dateConfiguration = 3;
        currentLocalDate = LocalDate.now();
        ObservableList<Appointment> allLocations;
        allLocations = appointmentTable.getItems();
        appointmentTable.getItems().remove(allLocations);
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);       
         } 
    
    //TN: The following four methods modify the currently considered date 
    //forcing the appointment table to display only the appointments
    //within the requested timeframe:
    @FXML
    void handleWeekPreviousButton (ActionEvent event) 
            throws IOException, ParseException, SQLException {
        dateConfiguration = 2;
        currentLocalDate = currentLocalDate.minusWeeks(1);
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);        
         }
    
    @FXML
    void handleWeekNextButton (ActionEvent event) 
            throws IOException, ParseException, SQLException { 
        dateConfiguration = 2;
        currentLocalDate = currentLocalDate.plusWeeks(1);
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);
         }
    
    @FXML
    void handleMonthPreviousButton (ActionEvent event) 
            throws IOException, ParseException, SQLException { 
        dateConfiguration = 3;
        currentLocalDate = currentLocalDate.minusMonths(1);
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);
         }
    
    @FXML
    void handleMonthNextButton (ActionEvent event) 
            throws IOException, ParseException, SQLException {
        dateConfiguration = 3;
        currentLocalDate = currentLocalDate.plusMonths(1);
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);
         }
    
    @FXML
    void handleAppointmentScreenCustomerNameDropDown (ActionEvent event) 
            throws IOException {        
         }
    
    @FXML
    void handleAppointmentScreenContactDropDown (ActionEvent event) 
            throws IOException {        
         }
    
    @FXML
    void handleAppointmentScreenLocationDropDown (ActionEvent event) 
            throws IOException {        
         }
    
    @FXML
    void handleAppointmentScreenTimeDropDown (ActionEvent event) 
            throws IOException {        
        }
    
    //TN: Placeholder for appointment selected for modify:
    public static Appointment appointmentToBeModified;
    //TN: Placeholder for appointment selected for delete:
    public static Appointment selectedForDeletion;
    //TN: Timeframe variable is designed to control appointment table output
    //scope - wheras a value of "1" removes timeframe restrictions, "2"
    //outputs one week of appointments, "3" outputs one month:
    public static int timeframe;
    //TN: Keeps track as to whether or not the appointment being saved is new or
    //an existing appointment meant to be modified:
    public static boolean modifyMarker = false;
         
    //TN: Class constructor: 
    //public void AppointmentScreenController() {        
    //}
    
    //TN: Adds customer to database:
    @FXML
    private void saveAppointment(ActionEvent event) throws ParseException {
        //TN: retrieves data from text boxes:
        String customerNameandID = appointmentScreenCustomerNameDropDown
                .getSelectionModel().getSelectedItem().toString();
        String customerIdString = customerNameandID.substring(00,03);
        int customerIdInt = Integer.parseInt(customerIdString);
        String customerNameString = customerNameandID.substring(04);
        String appointmentTitle = appointmentScreenTitleText.getText();
        String appointmentContact = appointmentScreenContactDropDown
                .getSelectionModel().getSelectedItem().toString();
        String appointmentLocation = appointmentScreenLocationDropDown
                .getSelectionModel().getSelectedItem().toString();
        LocalDate appointmentDate =  appointmentScreenDateBox.getValue();
        String appointmentTime = appointmentScreenTimeDropDown
                .getSelectionModel().getSelectedItem().toString();
        boolean toModify = modifyMarker;
        int apptIdInt = 0;
        
        if (toModify == true) {
        String appointmentIdInt = appointmentScreenApptIDText.getText();
        apptIdInt = Integer.parseInt(appointmentIdInt);
            }
        
        addAppointment(customerIdInt, customerNameString, appointmentTitle, 
                appointmentContact, appointmentLocation, appointmentDate, 
                appointmentTime, toModify, apptIdInt);   

        }
    
    //TN: prepares for an appointment modification by populating the appointment 
    //data fields:
    @FXML
    public void populateAppointmentFields(ActionEvent event) {
        //TN: indicates intent to modify an entry:
        modifyMarker = true;        
        //TN: retrieves selected customer:
        appointmentToBeModified = appointmentTable.getSelectionModel().getSelectedItem();
        //TN: checks to ensure an appointment was selected to be modified, if not
        //generates error:
        if (appointmentToBeModified == null) {
            Alert customerExists = new Alert(Alert.AlertType.INFORMATION);
                    customerExists.setTitle("ERROR");
                    customerExists.setHeaderText("Error selecting appointment");
                    customerExists.setContentText("Must select appointment to modify");
                    customerExists.showAndWait();
            //return;
        } else {
        //TN: creates a name value for the drop-down that stores the customer ID
        //in the name fopr retrieval later:
            int appointmentIndexToModify = appointmentToBeModified.getAppointmentId();
            int custId = appointmentToBeModified.getCustomerId();
            String formattedId = String.format("%03d", custId);      
            String customerName = appointmentToBeModified.getCustomerName();
            String namePlusId = (formattedId + " " + customerName);
        
            String title = appointmentToBeModified.getTitle();
            String contact = appointmentToBeModified.getContact();
            String location = appointmentToBeModified.getLocation();
            
        //TN: retrieves and formats date:
            String appointmentDate = appointmentToBeModified.getStringDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate convertedLocalDate = LocalDate.parse(appointmentDate, formatter);
            String time = appointmentToBeModified.appointmentTimeString();
            String apptIdString = Integer.toString(appointmentIndexToModify);
        //TN: Sets selected customer data into fields:    
            appointmentScreenCustomerNameDropDown.setValue(namePlusId);
            appointmentScreenTitleText.setText(title);
            appointmentScreenContactDropDown.setValue(contact);
            appointmentScreenLocationDropDown.setValue(location);
            appointmentScreenDateBox.setValue(convertedLocalDate);
            
            //if (time = "")
            appointmentScreenTimeDropDown.setValue(time);
            appointmentScreenApptIDText.setText(apptIdString);
        }
    }
    
    //TN: Returns appointment to be modified
    public static Appointment getAppointmentToModify() {
        return appointmentToBeModified;
    }
    
    //TN: Cancel function returns user to Primary screen:
    @FXML
    private void cancelAppointmentScreen(ActionEvent event) {
        //TN: Launches confirmation alert:
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Confirm cancel to return to the main screen");
        alert.setContentText("Click OK to cancel");
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.get() == ButtonType.OK) {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass()
                        .getResource("PrimaryScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) 
                        ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void initialize() throws ParseException, SQLException {

        //TN: Assigns data to table view using Lamdas:
        updateAppointmentTableInitial();

        
        
        appointmentScreenSaveButton.setOnAction(event -> {
            try {
                saveAppointment(event);
                updateAppointmentTableInitial();
                
            } catch (ParseException ex) {
                Logger.getLogger(AppointmentScreenControler.class.getName())
                        .log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentScreenControler.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        });
        
        appointmentTableIDColumn.setCellValueFactory(cellData -> 
                cellData.getValue().customerIdProperty().asObject());
        appointmentTableCustomerNameColumn.setCellValueFactory(cellData -> 
                cellData.getValue().customerNameProperty());
        appointmentTableApptIDColumn.setCellValueFactory(cellData -> 
                cellData.getValue().appointmentIdProperty().asObject());
        appointmentTableTitleColumn.setCellValueFactory(cellData -> 
                cellData.getValue().titleProperty());
        appointmentTableContactColumn.setCellValueFactory(cellData -> 
                cellData.getValue().contactProperty());
        appointmentTableDateColumn.setCellValueFactory(cellData -> 
                cellData.getValue().stringDateProperty());
        appointmentTableTimeColumn.setCellValueFactory(cellData -> 
                cellData.getValue().appointmentTimeStringProperty());
        appointmentTableLocationColumn.setCellValueFactory(cellData -> 
                cellData.getValue().locationProperty());
        
        appointmentScreenCustomerNameDropDown.setItems(customers);
        appointmentScreenContactDropDown.setItems(contacts);
        appointmentScreenLocationDropDown.setItems(locations);
        appointmentScreenTimeDropDown.setItems(appointmentTimes);
        timeDisplayTextBox.setText("Time displayed in " + timeZone);

    }
    
    //TN: Updates the table with all appointments:
    public void updateAppointmentTableInitial() throws ParseException, SQLException {
        currentLocalDate = LocalDate.now();
        dateConfiguration = 1;
        updateAppointmentScreenTable(currentLocalDate, dateConfiguration);
        //appointmentTable.getColumns().clear();
        appointmentTable.setItems(getAppointmentList());
    }
    
}
