package main.java.com.casitaprojects.parkpluss;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.*;
import javax.swing.*;
import main.java.com.casitaprojects.parkpluss.ConexionDB;
import main.java.com.casitaprojects.parkpluss.Ticket;

public class GestorDB {
     // 🔹 Insertar nuevo ticket al ingresar
    public static void insertarTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_id, placa, area_id, spot_id, fecha_ingreso, modo, monto, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getIdTicket());
            ps.setString(2, ticket.getVehiculo().getPlaca());
            ps.setString(3, ticket.getIdArea());
            ps.setString(4, ticket.getIdSpot());
            ps.setString(5, ticket.getFechaIngreso().toString());
            ps.setString(6, ticket.getModoTarifa().toString());
            ps.setDouble(7, ticket.getMonto());
            ps.setBoolean(8, true);
            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "⚠️ Error al insertar ticket en BD: " + e.getMessage());
        }
    }

    // 🔹 Actualizar ticket al retirar
    public static void actualizarTicketSalida(Ticket ticket) {
        String sql = "UPDATE tickets SET fecha_salida=?, monto=?, activo=false WHERE ticket_id=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getFechaSalida().toString());
            ps.setDouble(2, ticket.getMonto());
            ps.setString(3, ticket.getIdTicket());
            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "⚠️ Error al actualizar ticket en BD: " + e.getMessage());
        }
    }

    // 🔹 Insertar reingreso
    public static void insertarReingreso(Ticket ticket, int horasPermitidas) {
        String sql = "INSERT INTO reingresos (ticket_id, placa, fecha_salida, horas_permitidas) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getIdTicket());
            ps.setString(2, ticket.getVehiculo().getPlaca());
            ps.setString(3, ticket.getFechaSalida().toString());
            ps.setInt(4, horasPermitidas);
            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "⚠️ Error al insertar reingreso en BD: " + e.getMessage());
        }
    }
}