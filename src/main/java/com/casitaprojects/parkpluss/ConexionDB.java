package main.java.com.casitaprojects.parkpluss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Configuración de la conexión
    private static final String URL = "jdbc:mysql://localhost:3306/parkplus_db?useSSL=false&serverTimezone=America/Guatemala";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    /**
     * Devuelve SIEMPRE una nueva conexión activa.
     * NO usa conexión estática para evitar "connection closed".
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Cargar driver
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC de MySQL no encontrado.");
        }

        // SIEMPRE crea una nueva conexión fresca
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Cierra una conexión recibida.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
