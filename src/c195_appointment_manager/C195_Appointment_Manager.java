package c195_appointment_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Mothy
 */
public class C195_Appointment_Manager extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(C195_Appointment_Manager.class.getResource
            ("/C195_Appointment_Manager_View_Controller/LoginScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);
    }
    
}
