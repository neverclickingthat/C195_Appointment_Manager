/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package C195_Appointment_Manager_Models;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Mothy
 */
public class SQLConnection {
    
    private static final String SQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String SQL_DB_URL = "jdbc:mysql://52.206.157.109:3306/U03HA0";
    private static final String SQL_USERNAME = "U03HA0";
    private static final String SQL_PASSWORD = "53687976576";

    private static Connection conn = null;
   // private static Statement stmnt = null;

    static {
        try {
           Class.forName(SQL_DRIVER);
            conn = DriverManager.getConnection(SQL_DB_URL, SQL_USERNAME, SQL_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static Connection getConn(){
        return conn;
    }
    
}
