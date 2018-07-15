
package C195_Appointment_Manager_View_Controller;

import java.net.URL;

import static C195_Appointment_Manager_Models.DatabaseManager.verifyLogin;

import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.util.Optional;
import java.io.IOException;
import java.util.Locale;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Alert.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;

import javafx.scene.*;
import javafx.stage.*;

import C195_Appointment_Manager_Models.Appointment;
import C195_Appointment_Manager_Models.Customer;
import c195_appointment_manager.C195_Appointment_Manager;
import java.time.ZoneId;

    
/**
 * FXML Login Screen Controller class
 *
 * @author Mothy
 */

public class LoginScreenController implements Initializable {
    
    //TN: Login entry field:
    @FXML
    private TextField loginScreenUserNameField;
    
    //TN: Password entry field:
    @FXML
    private TextField loginScreenPasswordField;  
    
    //TN: Error message display field:
    @FXML
    private Label loginScreenErrorField;
    
    //TN: Region information display field:
    @FXML
    private Label loginScreenRegionInfoField;
    
    //TN: Label references to facilitate language swap:
    @FXML
    private Label loginScreenApplicationNameLabel;
    
    //TN: User name field label:
    @FXML
    private Label loginScreenUserNameLabel;
    
    //TN: password field label:
    @FXML
    private Label loginScreenPasswordLabel;
    
    //TN: logion screen button object:
    @FXML
    private Button loginScreenLoginButton;

 
    
    
    //TN: Set to locale determined language:
    public void setLanguage() {
        
        
        //Locale.setDefault(new Locale("es", "MX"));
                
        ResourceBundle translate = ResourceBundle.getBundle
            ("Resources/LoginScreenController", Locale.getDefault());
        loginScreenApplicationNameLabel.setText(translate.getString
            ("loginScreenApplicationNameLabelRegional"));
        loginScreenUserNameLabel.setText(translate.getString
            ("loginScreenUserNameLabelRegional"));
        loginScreenPasswordLabel.setText(translate.getString
            ("loginScreenPasswordLabelRegional"));
        loginScreenLoginButton.setText(translate.getString
            ("loginScreenLoginButtonRegional"));
        loginScreenRegionInfoField.setText(translate.getString
            ("loginScreenRegionFieldPopulatorRegional"));
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
	//TN: changes login screen text basec on region
        setLanguage();
	
        //TN:Sets default time zone:
        //TO TEST TIMEZONES UNCOMMENT ONE OF THE FOLOWING:
        //TimeZone defaultTimeZone = TimeZone.getTimeZone("Asia/Calcutta");
        //TimeZone defaultTimeZone = TimeZone.getTimeZone("America/Mexico_City");
        //TimeZone defaultTimeZone = TimeZone.getTimeZone("America/Chicago");
        //AND UNCOMMENT THE FOLLOWING STATEMENT:
        //TimeZone.setDefault(defaultTimeZone);
        
        //TN: activates login screen button
        loginScreenLoginButton.setOnAction(event -> submitLogin(event));
        
    }
	
    //TN: Login authenticaton process
    @FXML
    public void submitLogin(ActionEvent event) {
		
        //TN: Retrieves user name and password from text fields:
        String username = loginScreenUserNameField.getText();
        String password = loginScreenPasswordField.getText();
        
	//TN: Finds locale: 
        ResourceBundle translate = ResourceBundle.getBundle
            ("Resources/LoginScreenController", Locale.getDefault());
        
        //TN: checks to ensure user name and password fields are populated
        //if not returns an error:
        if (username.equals("") || password.equals("")) {
            loginScreenErrorField.setText(translate.getString
                ("loginScreenBlankCredentialsErrorRegional"));
            return;
        }
    
        //TN: Checks credentials against database
        boolean correctCredentials = verifyLogin(username, password);
		
        if (correctCredentials) {
           try {
                        Parent loader = FXMLLoader.load(getClass().getResource
                            ("PrimaryScreen.fxml"));
                        Scene partsScreen = new Scene(loader);
                        Stage window = (Stage) 
                                ((Node) event.getSource()).getScene().getWindow();
                        window.setScene(partsScreen);
                        window.show();        
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //TN: LOGIN button action:
    @FXML
    void handleLoginScreenButtonPress(ActionEvent event) throws IOException {        
           }
    
}
    

