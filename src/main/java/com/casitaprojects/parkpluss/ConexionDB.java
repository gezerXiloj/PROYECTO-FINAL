/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionDB {
    
    // Configuración de la conexión
    private static final String URL = "jdbc:mysql://localhost:3306/parkplus_db?serverTimezone=America/Guatemala";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; 
    
    private static Connection connection = null;

    /**
     * Establece la conexión con la base de datos si aún no está conectada.
     * @return El objeto Connection activo o null si falla.
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // 1. Cargar el driver JDBC (esto ya no es estrictamente necesario 
                //    en JDBC 4.0+ pero es buena práctica)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // 2. Establecer la conexión
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                System.out.println("Conexión a la base de datos exitosa.");
                
            } catch (ClassNotFoundException e) {
                // Esto ocurre si el conector JDBC no está en Libraries
                System.err.println("Error: Driver JDBC de MySQL no encontrado. Agregue la librería.");
                JOptionPane.showMessageDialog(null, "Error al cargar el driver JDBC: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (SQLException e) {
                // Error si la URL, usuario o contraseña son incorrectos
                System.err.println("Error de conexión a la base de datos.");
                JOptionPane.showMessageDialog(null, "Error al conectar a MySQL. Verifique usuario/contraseña: " + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Reiniciar la variable
                System.out.println("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
}
