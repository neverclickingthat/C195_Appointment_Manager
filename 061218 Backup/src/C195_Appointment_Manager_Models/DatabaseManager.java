
package C195_Appointment_Manager_Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.Date;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Mothy
 */
public class DatabaseManager {
    
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String db = "U03zNW";
    private static final String url = "jdbc:mysql://52.206.157.109/" + db;
    private static final String user = "U03zNW";
    private static final String pass = "53688129846";
    private static String city;
    private static String currentUser;
    //private static int openCount = 0;
    private static ObservableList<Customer> customerList = 
            FXCollections.observableArrayList();
    private static ObservableList<Appointment> appointmentList = 
            FXCollections.observableArrayList();
    private static LocalDate currentTableDate;
    private static int dateConfiguration = 1;
    private static boolean toContinue;
    private static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private static int openCount = 0;
    
    //TN: Checks login credentials:
    public static boolean verifyLogin(String username, String password) {
        int userId = getUserId(username);
        boolean correctPassword = checkPassword(userId, password);
        if (correctPassword) {
            setCurrentUser(username);
            try {
                Path path = Paths.get("UserLoginLog.txt");
                Files.write(path, Arrays.asList("User: " + currentUser + 
                        " Login date/time: " + Date.from(Instant.now()).toString() + "."),
                        StandardCharsets.UTF_8, Files.exists(path) ? 
                                StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    //TN: Checks to ensure password matches login:
    private static boolean checkPassword(int userId, String password) {
        //TN: Try with resources and catch for database connection
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement()) {
            String dbPassword;
            
            try ( 
                    ResultSet passwordSet = stmt.executeQuery
                        ("SELECT password FROM user WHERE userId = " + userId)) {
                
                dbPassword = null;
                if (passwordSet.next()) {
                    dbPassword = passwordSet.getString("password");
                }
                else {
                    return false;
                }
            }
            
            if (dbPassword.equals(password)) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get userId from userName (returns -1 if no match found for userName)
    private static int getUserId(String userName) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int userId = -1;
        
        try (Connection conn = DriverManager.getConnection(url,user,pass);
             Statement stmt = conn.createStatement(); 
                ResultSet userIdSet = stmt.executeQuery("SELECT userId FROM user "
                        + "WHERE userName = '" + userName + "'")) {


            // Sets userId to unique value and retrieves int from ResultSet
            if (userIdSet.next()) {
                userId = userIdSet.getInt("userId");
            }
        }
        catch (SQLException e) {

        }
        
        return userId;
    }
    
    private static void setCurrentUser(String userName) {
        currentUser = userName;
    }

    //TN: Method to generate login popup should the user logging in have an 
    //appointment within the next 15 minutes
    public static void logInAppointmentNotification() {
        
        System.out.println("Opened");
        // Checks to see if the main screen has already been opened during this session
        if (openCount == 0) {
            // Create ObservableList of appointments that were created by the user who just logged in
            ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();
            for (Appointment appointment : getAppointmentList()) {
                System.out.println(appointment.getContact());
                System.out.println(currentUser);
                if (appointment.getContact().equals(currentUser)) {
                    System.out.println("Appointment match");
                    userAppointments.add(appointment);
                }
            }
                        
            // Check each appointment in userAppointments to see if any start in the next 15 minutes
            for (Appointment appointment : userAppointments) {
                // Create Date object for 15 minutes from now
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Date.from(Instant.now()));
                Date currentTime = calendar.getTime();
                calendar.add(Calendar.HOUR, 24);
                Date notificationCutoff = calendar.getTime();
                
                Timestamp startDateTime = appointment.getStartTimestamp();
                Date stampConvert = new Date(startDateTime.getTime());

                //SimpleDateFormat appointmentFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                //String utcStringStartDateTime = appointmentFormat.format(startDateTime);
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                //LocalDateTime utcStartDateTime = LocalDateTime.parse(utcStringStartDateTime, formatter);
        
                System.out.println("need to match: " + notificationCutoff);
                //System.out.println(startDateTime);
                System.out.println("current: " + currentTime);
                System.out.println(stampConvert);
                // If appointment start date is before 15 minutes from now, create alert with information about appointment
                if (stampConvert.before(notificationCutoff) && stampConvert.after(currentTime)) {
                    //ResourceBundle rb = ResourceBundle.getBundle("MainScreen", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Upcoming Appointment Alert");
                    alert.setHeaderText("Appointment scheduled within 15 minutes");
                    alert.setContentText("Appointment Title: " + appointment.getTitle() + "\n");
                    alert.showAndWait();
                }
            }
            // Increment openCount so notifications won't be shown again this session
            openCount++;
        }
    }
    
    
    // Getter for customerList
    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }
    
    // Update customerRoster after database change
    public static void updateCustomerList() {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            // Retrieve customerRoster and clear
            ObservableList<Customer> customerList = getCustomerList();
            customerList.clear();
            // Create list of customerId's for all active customers
            ResultSet customerIdResultSet = stmt.executeQuery("SELECT customerId FROM customer WHERE active = 1");
            ArrayList<Integer> customerIdList = new ArrayList<>();
            while (customerIdResultSet.next()) {
                customerIdList.add(customerIdResultSet.getInt(1));
            }
            // Create Customer object for each customerId in list and add Customer to customerRoster list
            for (int customerId : customerIdList) {
                // Create new Customer object
                Customer customer = new Customer();
                // Retrieve customer information from database and set to Customer object
                ResultSet customerResultSet = stmt.executeQuery("SELECT customerName, active, addressId FROM customer WHERE customerId = " + customerId);
                customerResultSet.next();
                String customerName = customerResultSet.getString(1);
                int active = customerResultSet.getInt(2);
                int addressId = customerResultSet.getInt(3);
                customer.setCustomerId(customerId);
                customer.setCustomerName(customerName);
                customer.setActive(active);
                //customer.setAddressId(addressId);
                
                // Retrieve address information from database and set to Customer object
                ResultSet addressResultSet = stmt.executeQuery("SELECT address, address2, postalCode, phone, cityId FROM address WHERE addressId = " + addressId);
                addressResultSet.next();
                String address = addressResultSet.getString(1);
                //String address2 = addressResultSet.getString(2);
                //String postalCode = addressResultSet.getString(3);
                String phone = addressResultSet.getString(4);
                int cityId = addressResultSet.getInt(5);
                customer.setAddress(address);
                customer.setPhone(phone);
                //customer.setAddress2(address2);
                //customer.setPostalCode(postalCode);
                //customer.setCityId(cityId);
                
                // Retrieve city information from database and set to Customer object
                ResultSet cityResultSet = stmt.executeQuery("SELECT city, countryId FROM city WHERE cityId = " + cityId);
                cityResultSet.next();
                city = cityResultSet.getString(1);
                customer.setCity(city);
                //int countryId = cityResultSet.getInt(2);
                //customer.setCountryId(countryId);
                // Retrieve country information from database and set to Customer object
                //ResultSet countryResultSet = stmt.executeQuery("SELECT country FROM country WHERE countryId = " + countryId);
                //countryResultSet.next();
                //String country = countryResultSet.getString(1);
                //customer.setCountry(country);
                
                // Add new Customer object to customerRoster
                customerList.add(customer);
                
            }
        }
            catch (SQLException e) {
                e.printStackTrace();            
        }
    }
 
    
    // Add customer to database if entry does not already exist
    public static void addCustomer(String customerName, String address, String city, String phone) 
    {
        // Get country, city and address Id's
        try 
            (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) 
        {
                ResultSet activeResultSet = stmt.executeQuery("SELECT active FROM customer, address WHERE customer.customerName = '" + customerName + "' AND address.address = '" + address + "'");
            
            {
            //int countryId = calculateCountryId(country);
            //int cityId = calculateCityId(city, countryId);
            //int addressId = Integer.parseInt(address);
        // Check if customer is new or already exists in database
        if (activeResultSet.next() == true) {
          
            int active = activeResultSet.getInt(1);
            // Check if existing customer is already active or set to inactive
            switch (active) {
                case 1:
                    // Show alert if customer is already active
                    Alert customerExists = new Alert(Alert.AlertType.INFORMATION);
                    customerExists.setTitle("ERROR");
                    customerExists.setHeaderText("Error adding customer");
                    customerExists.setContentText("Customer already active in database");
                    customerExists.showAndWait();
                    break;
                
                case 0:
                    // Set customer to active if they are currently inactive
                    setCustomerActive(customerName, address);
                    break;}
                
            } else {
               
                    ResultSet topAddressID = stmt.executeQuery("SELECT MAX(addressID) FROM address");
                    topAddressID.next();
                    int largestAddressId = topAddressID.getInt(1);
                    int nextAddressId = largestAddressId + 1;
                    
                    ResultSet topCustomerID = stmt.executeQuery("SELECT MAX(customerID) FROM customer");
                    topCustomerID.next();
                    int largestCustomerId = topCustomerID.getInt(1);
                    int nextCustomerId = largestCustomerId + 1;
                    
                    ResultSet cityIdFinder = stmt.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "'");
                    cityIdFinder.next();
                    int cityId = cityIdFinder.getInt(1);
                    int activeNew = 1;
                    stmt.executeUpdate("INSERT INTO address VALUES ('" + nextAddressId + "', '" + address + "', ' ', '" + cityId + "', '00000', '" + phone + "', CURRENT_DATE, '" + currentUser + "'"
                            + ", CURRENT_TIMESTAMP, '" + currentUser + "')");
                    stmt.executeUpdate("INSERT INTO customer VALUES ('" + nextCustomerId + "', '" + customerName + "', '" + nextAddressId + "', '" + activeNew + "', CURRENT_DATE, '" + currentUser + "'"
                            + ", CURRENT_TIMESTAMP, '" + currentUser + "')");
                    }
            }
        }
        
            catch (SQLException e) 
            { e.printStackTrace();  
                }
                
    }
    
    
    public static void modifyCustomer(int customerId, String customerName, String address, String city, String phone) {
        
        try
        (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {
        
        ResultSet cityIdFinder = stmt.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "'");
                    cityIdFinder.next();
                    int cityId = cityIdFinder.getInt(1);
                    
        ResultSet addressIdFinder = stmt.executeQuery("SELECT addressId FROM customer WHERE customerId = '" + customerId + "'");
                    addressIdFinder.next();
                    int addressId = addressIdFinder.getInt(1);  
                    
         stmt.executeUpdate("UPDATE customer SET customerName = '" + customerName + "', lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE customerId = '" + customerId + "'"); 
         stmt.executeUpdate("UPDATE address SET address = '" + address + "', cityId = '" + cityId + "', phone = '" + phone + "',lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser + "' WHERE addressId = '" + addressId + "'"); 
                  
                    
        }                
                    catch (SQLException e) {
                          e.printStackTrace();  
                    }
    }
    //TN: Sets customer to inactive:
    public static void setCustomerInactive(Customer deactivatedCustomer) {
        int customerId = deactivatedCustomer.getCustomerId();
            try (Connection conn = DriverManager.getConnection(url,user,pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = " + customerId);
            }
            catch (SQLException e) {
                 e.printStackTrace();
            }
        updateCustomerList();
    }
    
    
    //TN: sets customer to active:
    public static void setCustomerActive(String customerName, String address) {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, " +
                        "lastUpdateBy = '" + currentUser + "' WHERE customerName = '" + customerName + "'");
            }
        
        catch (SQLException e) {
            e.printStackTrace();
        }
    
    }
    
    //TN: Appointment list getter:
    public static ObservableList<Appointment> getAppointmentList() {
        return appointmentList;
    }
    
    //TN: Update Appointment screen table:
    public static void updateAppointmentScreenTable(LocalDate currentTableDate, int dateConfiguration) throws ParseException, SQLException {
        
        LocalDateTime currentLocalDate = LocalDateTime.now();
        //LocalDateTime weekLocalDate = currentLocalDate.plusWeeks(1);
        //Timestamp weekTimeStamp = Timestamp.valueOf(weekLocalDate.atStartOfDay());
        //System.out.println("Week: " + weekTimeStamp);
        //LocalDateTime monthLocalDate = currentLocalDate.plusMonths(1);
        //Timestamp monthTimeStamp = Timestamp.valueOf(monthLocalDate.atStartOfDay());
        //System.out.println("Month: " + monthTimeStamp);

        //TN: Database connection initiation:
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            // Retrieve customerRoster and clear
            ObservableList<Appointment> appointmentList = getAppointmentList();
            appointmentList.clear();
            
            ResultSet appointmentIDResultSet;
            //TN: Creates a list of all appointment IDs.  If timeframe variable 
            //is 1, limits time period to one week, if duration variable is 2
            //limits timeframe to one month, if any other value no limit.
            
            appointmentIDResultSet = stmt.executeQuery("SELECT appointmentId FROM appointment");
            //System.out.println("All");
                
            //appointmentIDResultSet.last();
            //int count = appointmentIDResultSet.getRow();
            //System.out.println("Count: " + count);
                
            ArrayList<Integer> appointmentIdList = new ArrayList<>();
            while (appointmentIDResultSet.next()) {
            appointmentIdList.add(appointmentIDResultSet.getInt(1));
                                    
                    }
            //TN: Create an Appointment object for eact Appointment ID and add to appointmentList:
            for (int appointmentId : appointmentIdList) {
                
                //TN: Creates new Appointment object:
                Appointment appointment = new Appointment();
                
                
                //TN: Retrieves appointment details from database
                ResultSet appointmentTableResultSet = stmt.executeQuery("SELECT appointmentId, customerId, title, location, contact, start FROM appointment WHERE appointmentId = " + appointmentId);
                appointmentTableResultSet.next();
                
                //TN: SETS APPOINTMENT START DATETIME:
                Timestamp startDateTime = appointmentTableResultSet.getTimestamp(6);
                appointment.setStartTimestamp(startDateTime);
                LocalDate localDate = startDateTime.toLocalDateTime().toLocalDate();
                
                //System.out.println("localDate " + localDate);
                //System.out.println("currentTableDate :" + currentTableDate);
                LocalDate plusOneWeek = currentTableDate.plusWeeks(1);
                //System.out.println("currentTableDate.plusWeeks(1) :" + plusOneWeek);
                LocalDate plusOneMonth = currentTableDate.plusMonths(1);
                //System.out.println("currentTableDate.plusMonths(1) :" + plusOneMonth);
                //System.out.println(dateConfiguration);
                
                switch (dateConfiguration) {
                    
                    case 1:
                        toContinue = false;
                        break;
                    case 2:
                        if (localDate.isBefore(currentTableDate) || localDate.isAfter(plusOneWeek)) {
                           toContinue = true; 
                        } else {
                           toContinue = false;
                        }; 
                        
                        break;
                    case 3:    
                        if (localDate.isBefore(currentTableDate) || localDate.isAfter(plusOneMonth)){
                           toContinue = true; 
                        } else {
                           toContinue = false;
                        }; 
                    default:
                        break;
                }
                
                if (toContinue == true) {
                    continue;
                }            
                
                //System.out.println("Got past it");
                //TN: SETS APPOINTMENT ID
                appointment.setAppointmentId(appointmentId);
                              
                //TN: SETS CUSTOMER ID
                int customerId = appointmentTableResultSet.getInt(2);
                appointment.setCustomerId(customerId);
             
                //TN: SETS TITLE
                String title = appointmentTableResultSet.getString(3);
                appointment.setTitle(title); 
                            
                //TN: SETS LOCATION
                String location = appointmentTableResultSet.getString(4);
                appointment.setLocation(location);
                               
                //TN: SETS CONTACT
                String contact = appointmentTableResultSet.getString(5);
                appointment.setContact(contact);
                
                //System.out.println("Fresh: " + startDateTime);
                String appointmentTime = timeRangeMaker(startDateTime);
                
                //System.out.println("Time Range: " + appointmentTime);

                //TN: Formats start time into string:
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy").format(startDateTime);
                
                //SimpleDateFormat appointmentFormat = new SimpleDateFormat("hh:mm a");
                
                //String formattedStartTime = appointmentFormat.format(startDateTime);
                
                //TN Adds 30 minutes to start time (all appointments are 30 minutes long):
                //Date d = appointmentFormat.parse(formattedStartTime); 
                //Calendar cal = Calendar.getInstance();
                //cal.setTime(d);
                //cal.add(Calendar.MINUTE, 30);
                //String formattedEndTime = appointmentFormat.format(cal.getTime());
                
                //System.out.println("Date: " + formattedDate + " Start: " + formattedStartTime + " End time: " + formattedEndTime);
                
                appointment.setStringDate(formattedDate);
                
                //TN: Creates appointment table representation of scheduled appointment time:

                appointment.setAppointmentTimeString(appointmentTime);
                
                //System.out.println("After Local: " + localDate);
                
                boolean startTime = false;
                
                reverseTimeRangeMaker(appointmentTime, localDate, startTime);
                
                // Retrieve city information from database and set to Customer object:
                ResultSet customerNameResultSet = stmt.executeQuery("SELECT customerName FROM customer WHERE customerId = " + customerId);
                customerNameResultSet.next();
                
                //TN: SETS NAME
                String name = customerNameResultSet.getString(1);
                appointment.setCustomerName(name);
                
                // Add new Customer object to customerRoster
                appointmentList.add(appointment);
                
            }
            
                }
        
        catch (SQLException e) {
           e.printStackTrace();
        }
    
    }   
    
    
    //TN: Deletes an appointment:
    public static void deleteAppointment(Appointment deletedAppointment) {
        int appointmentId = deletedAppointment.getAppointmentId();
            try (Connection conn = DriverManager.getConnection(url,user,pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM appointment WHERE appointmentId = " + appointmentId);
            }
            catch (SQLException e) {
                 e.printStackTrace();
            }
        
    }
    
    public static String NameGetter (int customerId) throws SQLException {
        
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
        
        ResultSet customerNameResultSet = stmt.executeQuery("SELECT customerName FROM customer WHERE customerId = " + customerId);
                customerNameResultSet.next();
                
        //TN: SETS NAME
        String name = customerNameResultSet.getString(1);
        
        return name;
        }
        }
        
    public static String timeRangeMaker (Timestamp startDateTime) throws ParseException {
        
        //TN: I know there is a much, much more efficient way of doing this....
        //This method converts times from the DB to local for the appointment
        //table:
        
        SimpleDateFormat appointmentFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String utcStringStartDateTime = appointmentFormat.format(startDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime utcStartDateTime = LocalDateTime.parse(utcStringStartDateTime, formatter);
        
        ZoneId zoneIDUTC = ZoneId.of("UTC");
                
        ZonedDateTime utcZonedDateTimeStart = utcStartDateTime.atZone(zoneIDUTC);
        ZonedDateTime adjustedForLocalTimeStart = utcZonedDateTimeStart.withZoneSameInstant(ZoneId.systemDefault());
        
        String localTimeString = adjustedForLocalTimeStart.format(DateTimeFormatter.ISO_DATE_TIME);
        //System.out.println("localTimeString: " + localTimeString);
        String localTimeOnlyString = localTimeString.substring(11,16);
        //System.out.println("localTimeOnlyString: " + localTimeOnlyString);
        SimpleDateFormat appointmentTimeOnlyFormat = new SimpleDateFormat("HH:mm");
        Date formattedStartTime = appointmentTimeOnlyFormat.parse(localTimeOnlyString); 
        //System.out.println("formattedStartTime: " + formattedStartTime);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(formattedStartTime);
        cal.add(Calendar.MINUTE, 30);
        String formattedEndTime = appointmentTimeOnlyFormat.format(cal.getTime());
        
        String appointmentTime = (localTimeOnlyString + " to " + formattedEndTime);
               
        return appointmentTime;
    }
    
    public static Timestamp reverseTimeRangeMaker(String timeRange, LocalDate date, boolean startTime) throws ParseException  {
    
    String timeRangeConvertString;
    String utcTime;
    String stringStartTime;
        
    if (startTime == true) {        
        stringStartTime = (timeRange.substring(00,05));        
    } else {        
        stringStartTime = (timeRange.substring(9,14));        
    }    
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localStartTime = LocalTime.parse(stringStartTime, formatter);
        LocalDateTime localStartDateTime = LocalDateTime.of(date, localStartTime);
        //System.out.println("EST Start DateTime: " + localStartDateTime);

        //TN: Defining the local ZoneID:
        //ZoneId zoneID = ZoneId.of("UTC");
        ZoneId zoneID = ZoneId.systemDefault();
        //        System.out.println("Zone ID: " + zoneID);
                
        //TN: Setting values from DB to UTC:
        ZonedDateTime zoneDateTimeStart = localStartDateTime.atZone(zoneID);
		
        //TN: Converting local time to UTC:        
        ZonedDateTime adjustedForLocalTimeStart = zoneDateTimeStart.withZoneSameInstant(ZoneId.of("UTC"));
		
        //TN: Converting converted time to a string:
        String utcTimeString = adjustedForLocalTimeStart.format(DateTimeFormatter.ISO_DATE_TIME);
                
        //TN: pulling time from sting:        
        utcTime = utcTimeString.substring(11,16);
        
        //TN: Creating TimeStamp:
        timeRangeConvertString = date + " " + utcTime + ":00";        
        Timestamp timeRangeConvert = Timestamp.valueOf(timeRangeConvertString);
        
        return timeRangeConvert;
        
    }
    
    
    
  //  /*
    
    
    // Add customer to database if entry does not already exist
    public static void addAppointment(int customerId, String customerName, String title, String contact, String location, LocalDate date, String time, boolean modifyMarker, int apptId) throws ParseException 
    {
        // Get country, city and address Id's
        try 
            (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) 
        {
                
                    Timestamp appointmentStart = reverseTimeRangeMaker(time, date, true);
                    Timestamp appointmentEnd = reverseTimeRangeMaker(time, date, false);
                    ResultSet topAppointmentID = stmt.executeQuery("SELECT MAX(appointmentID) FROM appointment");
                        topAppointmentID.next();
                    int largestAppointmentId = topAppointmentID.getInt(1);
                    int nextAppointmentId = largestAppointmentId+ 1;
                    
                    ResultSet customerNameGrab = stmt.executeQuery("SELECT customerName FROM customer WHERE customerId = '" + customerId + "'");
                        customerNameGrab.next();
                    //String correctCustomerName = customerNameGrab.getString(1);
            
                ResultSet conflictCheckSet = stmt.executeQuery("SELECT contact, start FROM appointment, address WHERE contact = '" + contact + "' AND start = '" + appointmentStart + "'");
                        {
                    //TN: Check to see if the contact already has an appointment scheduled for that time:
                    if (conflictCheckSet.next() == true && modifyMarker == false) {
          
                    // Show alert if customer is already active
                    Alert customerExists = new Alert(Alert.AlertType.INFORMATION);
                    customerExists.setTitle("ERROR");
                    customerExists.setHeaderText("Error adding appointment");
                    customerExists.setContentText("Staff acheduling conflict, pick a different date and/or time");
                    customerExists.showAndWait();
                    
                    } else if (conflictCheckSet.next() == false && modifyMarker == false) {     
                   
                    stmt.executeUpdate("INSERT INTO appointment VALUES ('" + nextAppointmentId + "', '" + customerId + "', '" + title + "', 'see title'"
                            + ",'" + location + "', '" + contact + "', 'none', '" + appointmentStart + "', '" + appointmentEnd + "', CURRENT_DATE, '" + currentUser + "'"
                            + ", CURRENT_TIMESTAMP, '" + currentUser + "')");
                    } else {
                    
                    stmt.executeUpdate("UPDATE appointment SET title = '" + title + "', location = '" + location + "', contact = '" + contact + "', start = '" + appointmentStart + "',"
                            + " end = '" + appointmentEnd + "', lastUpdate = CURRENT_DATE, lastUpdateBy = '" + currentUser + "' WHERE appointmentId = '" + apptId + "'");
                        
                    
                }
            }
        }
        
            catch (SQLException e) 
            { e.printStackTrace();  
                }
                
    }

//*/
    //TN: Populated the city drop down on the customer add/modify screen:
    public static ObservableList<String> populateCityList() {
    
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) 
        {
    
            ObservableList<String> cities = FXCollections.observableArrayList();
     
            ResultSet cityResultSet = stmt.executeQuery("SELECT city FROM city");
                cityResultSet.next();
      
        while (cityResultSet.next()) 
        {
            cities.add(cityResultSet.getString("city.city"));
        }
    
    return cities;
    
        }
       catch (SQLException e) {
          e.printStackTrace();
            }
    
    return null;
    
    }   

    public static ObservableList<String> populateCustomerNamesList() {
    
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) 
        {
    
            ObservableList<String> customers = FXCollections.observableArrayList();
     
            ResultSet customerResultSet = stmt.executeQuery("SELECT customerID, customerName FROM customer WHERE active = 1");
            //    customerResultSet.next();
      
        while (customerResultSet.next()) 
        {
            int customerId = customerResultSet.getInt(1);
            //appointment.setCustomerId(customerId);
            
            String formattedId = String.format("%03d", customerId);
             
            String customerName = customerResultSet.getString(2);
            //appointment.setTitle(title);
            
            customers.add(formattedId + " " + customerName);
        }
    
    return customers;
    
        }
       catch (SQLException e) {
          e.printStackTrace();
            }
    
    return null;
    
    }    
    
    public static ObservableList<String> populateContactList() {
        
        ObservableList<String> contacts = FXCollections.observableArrayList();
        
        contacts.add("Tim");
        contacts.add("Lisa");
        contacts.add("Jorge");
        contacts.add("Michelle");
        contacts.add("Barack");
        
        return contacts;
    }
    
    public static ObservableList<String> populateLocationsList() {
        
        ObservableList<String> locations = FXCollections.observableArrayList();
        
        locations.add("New York office");
        locations.add("Kansas City office");
        locations.add("Los Angeles office");
        locations.add("Dayton office");
        locations.add("Hong Kong office");
        
        return locations;
    }
    
    public static ObservableList<String> populateAppointmentTimesList() {
        
        ObservableList<String>appointmentTimes = FXCollections.observableArrayList();
        
        Integer offsetInSeconds  = ZonedDateTime.now().getOffset().getTotalSeconds();
        System.out.println("offset seconds: " + offsetInSeconds);
        int offsetInHours = offsetInSeconds/3600;
        System.out.println("offset hours: " + offsetInHours);
        int midnightESTOffset = 13 + offsetInHours; 
        System.out.println("offset midnight: " + midnightESTOffset);
        
        LocalTime time = LocalTime.MIDNIGHT.plusHours(midnightESTOffset);
        
        for (int i = 0; i < 17; i++) {
            
            LocalTime start = time;
            //time = time.plusMinutes(30);
            LocalTime end = time.plusMinutes(30);
            String startTime = start.format(timeFormat);
            String endTime = end.format(timeFormat);
            String duration = String.format(startTime + " to " + endTime); 
            appointmentTimes.add(duration);
            
            time = time.plusMinutes(30);
            }
           
        
        return appointmentTimes;
    }

    // Create report for number of appointment types by month
    public static void generateAppointmentTypeByMonthReport() throws ParseException, SQLException {
        dateConfiguration = 1;
        currentTableDate = LocalDate.now();
        updateAppointmentScreenTable(currentTableDate, dateConfiguration);
        //ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Initialize report string
        String report = ("Number of Appointment Types By Month \r\n \r\n");
        ArrayList<String> monthsWithAppointments = new ArrayList<>();
        // Check year and month of each appointment. Add new year-month combos to ArrayList
        for (Appointment appointment : getAppointmentList()) {
            
            String appointmentDate = appointment.getStringDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate convertedAppointmentDate = LocalDate.parse(appointmentDate, formatter);
            java.util.Date date = java.sql.Date.valueOf(convertedAppointmentDate);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String monthYear = month + "/" + year;
            if (month < 10) {
                monthYear = "0" + month + "/" + year;
            }
            if (!monthsWithAppointments.contains(monthYear)) {
                monthsWithAppointments.add(monthYear);
            }
        }
        
        // Sort year-months
        Collections.sort(monthsWithAppointments);
        for (String monthYear : monthsWithAppointments) {
            // Get year and month values again
            int month = Integer.parseInt(monthYear.substring(0,2));
            int year = Integer.parseInt(monthYear.substring(3,7));
            // Initialize typeCount int
            int typeCount = 0;
            ArrayList<String> descriptions = new ArrayList<>();
            for (Appointment appointment : getAppointmentList()) {
                // Get appointment start date
                
                String appointmentDate = appointment.getStringDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                LocalDate convertedAppointmentDate = LocalDate.parse(appointmentDate, formatter);
                java.util.Date date = java.sql.Date.valueOf(convertedAppointmentDate);
                
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                // Get appointment year and month values
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                // If year and month match, get appointment description
                if (year == appointmentYear && month == appointmentMonth) {
                    String description = appointment.getTitle();
                    // If appointment description is not in ArrayList, add it and increment typeCount
                    if (!descriptions.contains(description)) {
                        descriptions.add(description);
                        typeCount++;
                    }
                }
            }
            // Add year-month to report
            report = report + monthYear + " appointment total: " + typeCount + "\r\n";
            report = report + ("Appointment types: \r\n");
            // Add each type description to report
            for (String description : descriptions) {
                report = report + " " + description + "\r\n";
            }
            // Remove trailing comma from type descriptions
            report = report.substring(0, report.length()-1);
            // Add paragraph break between months
            report = report + "\r\n \r\n";
        }
        // Print report to AppointmentTypeByMonth.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("AppointmentTypeByMonth.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Create report for each consultant's (user's) schedule
    public static void generateScheduleForConsultants() throws ParseException, SQLException {
        dateConfiguration = 1;
        currentTableDate = LocalDate.now();
        updateAppointmentScreenTable(currentTableDate, dateConfiguration);
        //ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Initialize report string
        String report = ("Schedule for Each Consultant\r\n \r\n");
        ArrayList<String> consultantsWithAppointments = new ArrayList<>();
        // Check createdBy of each appointment. Add new createdBy's to ArrayList
        for (Appointment appointment : getAppointmentList()) {
            String consultant = appointment.getContact();
            if (!consultantsWithAppointments.contains(consultant)) {
                consultantsWithAppointments.add(consultant);
            }
        }
        // Sort consultants
        Collections.sort(consultantsWithAppointments);
        for (String consultant : consultantsWithAppointments) {
            // Add consultant name to report
            report = report + consultant + ": \r\n";
            for (Appointment appointment : getAppointmentList()) {
                String appointmentConsultant = appointment.getContact();
                // Check if appointment createdBy matches consultant
                if (consultant.equals(appointmentConsultant)) {
                    // Get appointment date and title
                    //String date = appointment.getDateString();
                    String title = appointment.getTitle();
                    
                    String appointmentDate = appointment.getStringDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    LocalDate convertedAppointmentDate = LocalDate.parse(appointmentDate, formatter);
                    java.util.Date date = java.sql.Date.valueOf(convertedAppointmentDate);
            
                    String startTime = appointment.appointmentTimeString();

                    report = report + ("Date: ") + date + (" Description: ") + title + ("\r\n Appointment Time: ") + startTime + ("\r\n");
                }
            }
            // Add paragraph break between consultants
            report = report + "\r\n \r\n";
        }
        // Print report to ScheduleByConsultant.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByConsultant.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Create report for each consultant's (user's) schedule
    public static void generateScheduleForCustomers() throws ParseException, SQLException {
        dateConfiguration = 1;
        currentTableDate = LocalDate.now();
        updateAppointmentScreenTable(currentTableDate, dateConfiguration);
        //ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        // Initialize report string
        String report = ("Schedule for Each Customer\r\n \r\n");
        ArrayList<String> customersWithAppointments = new ArrayList<>();
        // Check createdBy of each appointment. Add new createdBy's to ArrayList
        for (Appointment appointment : getAppointmentList()) {
            String customerName = appointment.getCustomerName();
            if (!customersWithAppointments.contains(customerName)) {
                customersWithAppointments.add(customerName);
            }
        }
        // Sort consultants
        Collections.sort(customersWithAppointments);
        for (String customer : customersWithAppointments) {
            // Add consultant name to report
            report = report + customer + ": \r\n";
            for (Appointment appointment : getAppointmentList()) {
                String appointmentCustomer = appointment.getCustomerName();
                // Check if appointment createdBy matches consultant
                if (customer.equals(appointmentCustomer)) {
                    // Get appointment date and title
                    //String date = appointment.getDateString();
                    String title = appointment.getTitle();
                    
                    String appointmentDate = appointment.getStringDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    LocalDate convertedAppointmentDate = LocalDate.parse(appointmentDate, formatter);
                    java.util.Date date = java.sql.Date.valueOf(convertedAppointmentDate);
            
                    String startTime = appointment.appointmentTimeString();

                    report = report + ("Date: ") + date + (" Description: ") + title + ("\r\n Appointment Time: ") + startTime + ("\r\n");
                }
            }
            // Add paragraph break between consultants
            report = report + "\r\n \r\n";
        }
        // Print report to ScheduleByConsultant.txt. Overwrite file if exists.
        try {
            Path path = Paths.get("ScheduleByCustomer.txt");
            Files.write(path, Arrays.asList(report), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}    


    

    