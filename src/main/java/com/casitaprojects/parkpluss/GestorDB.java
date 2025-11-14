package main.java.com.casitaprojects.parkpluss;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import main.java.com.casitaprojects.parkpluss.ConexionDB;
import main.java.com.casitaprojects.parkpluss.Ticket;

public class GestorDB {
     // 🔹 Insertar nuevo ticket al ingresar
    public static void insertarTicket(Ticket ticket) {
        String sql = "INSERT INTO TICKET (id_ticket, placa, id_area, id_spot, fecha_ingreso, modo_tarifa, monto) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getIdTicket());
            ps.setString(2, ticket.getVehiculo().getPlaca());
            ps.setString(3, ticket.getIdArea());
            ps.setString(4, ticket.getIdSpot());
            ps.setTimestamp(5, Timestamp.valueOf(ticket.getFechaIngreso())); // ⬅️ CORRECTO
            ps.setString(6, ticket.getModoTarifa().toString());
            ps.setDouble(7, ticket.getMonto());

            ps.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "⚠️ Error al insertar ticket en BD: " + e.getMessage());
        }
    }

    // 🔹 Actualizar ticket al retirar
    public static void actualizarTicketSalida(Ticket ticket) {
        String sql = "UPDATE TICKET SET fecha_salida=?, monto=? WHERE id_ticket=?";
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
        String sql = "INSERT INTO REINGRESO (id_ticket, placa, fecha_salida, horas_permitidas) VALUES (?, ?, ?, ?)";
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
    
public static Connection getNewConnection() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                ConexionDB.URL,
                ConexionDB.USER,
                ConexionDB.PASSWORD
        );
    } catch (Exception e) {
        System.err.println("❌ Error al crear nueva conexión: " + e.getMessage());
        return null;
    }
}

public static void insertarPropietario(String placa, String nombre) throws SQLException, ClassNotFoundException {
    String sql = "REPLACE INTO propietario (placa, nombre) VALUES (?, ?)";
    try (Connection conn = ConexionDB.getNewConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, placa);
        ps.setString(2, nombre);
        ps.executeUpdate();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "⚠️ Error al insertar propietario: " + e.getMessage());
    }
}
// 🔹 Insertar vehículo si no existe aún
public static void insertarVehiculoSiNoExiste(String placa, String tipo, String tipoArea) throws SQLException, ClassNotFoundException {

    String check = "SELECT placa FROM VEHICULO WHERE placa = ?";
    String insert = "INSERT INTO VEHICULO (placa, tipo_vehiculo, tipo_area_asignada) VALUES (?, ?, ?)";

    try (Connection conn = ConexionDB.getNewConnection();
         PreparedStatement psCheck = conn.prepareStatement(check);
         PreparedStatement psInsert = conn.prepareStatement(insert)) {

        psCheck.setString(1, placa);
        try (ResultSet rs = psCheck.executeQuery()) {
            if (rs.next()) return; // ya existe
        }

        psInsert.setString(1, placa);
        psInsert.setString(2, tipo);
        psInsert.setString(3, tipoArea);
        psInsert.executeUpdate();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "⚠️ Error insertarVehiculoSiNoExiste: " + e.getMessage());
    }
}
// 🔹 Búsqueda avanzada de tickets
public static ResultSet buscarTicketsAvanzado(
        String placa,
        String propietario,
        String fecha,
        Boolean interior,
        boolean filtrarAuto,
        boolean filtrarMoto) throws ClassNotFoundException {

    StringBuilder sql = new StringBuilder(
        "SELECT t.*, p.nombre AS propietario " +
        "FROM TICKET t " +
        "LEFT JOIN propietario p ON t.placa = p.placa " +
        "WHERE 1=1 "
    );

    if (placa != null && !placa.isEmpty())
        sql.append(" AND t.placa LIKE '%").append(placa).append("%' ");

    if (propietario != null && !propietario.isEmpty())
        sql.append(" AND p.nombre LIKE '%").append(propietario).append("%' ");

    if (fecha != null && !fecha.isEmpty())
        sql.append(" AND DATE(t.fecha_ingreso) = '").append(fecha).append("' ");

    if (interior != null)
        sql.append(interior ? " AND t.fecha_salida IS NULL " : " AND t.fecha_salida IS NOT NULL ");

    if (filtrarAuto)
        sql.append(" AND t.tipo = 'AUTO' ");
    if (filtrarMoto)
        sql.append(" AND t.tipo = 'MOTO' ");

    try {
        Connection conn = ConexionDB.getNewConnection();
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        return ps.executeQuery();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "⚠️ Error en búsqueda avanzada: " + e.getMessage());
        return null;
    }
}
public static void insertarVehiculo(Vehiculo v, String propietario) {
    try {
        // 1. Insertar vehículo si no existe
        insertarVehiculoSiNoExiste(
            v.getPlaca(),
            v.getTipoVehiculo().name(),
            v.getTipoArea().name()
        );

        // 2. Insertar o actualizar propietario
        insertarPropietario(v.getPlaca(), propietario);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, 
            "⚠️ Error al insertar vehículo+propietario: " + e.getMessage());
    }
}
public static java.util.List<Ticket> buscarTickets(
        String placa,
        String propietario,
        String fecha,
        String tipoVehiculo,
        String estado) {

    java.util.List<Ticket> lista = new java.util.ArrayList<>();

    String sql =
        "SELECT t.*, v.tipo_vehiculo, p.nombre " +
        "FROM ticket t " +
        "LEFT JOIN vehiculo v ON t.placa = v.placa " +
        "LEFT JOIN propietario p ON t.placa = p.placa " +
        "WHERE 1=1 ";

    if (placa != null && !placa.isEmpty())
        sql += " AND t.placa LIKE '%" + placa + "%' ";

    if (propietario != null && !propietario.isEmpty())
        sql += " AND p.nombre LIKE '%" + propietario + "%' ";

    if (fecha != null && !fecha.isEmpty())
        sql += " AND DATE(t.fecha_ingreso) = '" + fecha + "' ";

    if ("ACTIVO".equals(estado))
        sql += " AND t.fecha_salida IS NULL ";
    else if ("INACTIVO".equals(estado))
        sql += " AND t.fecha_salida IS NOT NULL ";

    if ("AUTO".equals(tipoVehiculo))
        sql += " AND v.tipo_vehiculo = 'AUTO' ";
    else if ("MOTO".equals(tipoVehiculo))
        sql += " AND v.tipo_vehiculo = 'MOTO' ";

    try (Connection conn = ConexionDB.getNewConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            Vehiculo v = new Vehiculo(
                rs.getString("placa"),
                TipoVehiculo.valueOf(rs.getString("tipo_vehiculo")),
                TipoArea.ESTUDIANTES  // no afecta búsqueda
            );

            Ticket t = new Ticket(
                rs.getString("id_ticket"),
                v,
                rs.getString("id_area"),
                rs.getString("id_spot"),
                ModoTarifa.valueOf(rs.getString("modo_tarifa"))
            );

            t.setFechaIngreso(rs.getTimestamp("fecha_ingreso").toLocalDateTime());

            Timestamp ts = rs.getTimestamp("fecha_salida");
            if (ts != null)
                t.setFechaSalida(ts.toLocalDateTime());

            t.setMonto(rs.getDouble("monto"));

            lista.add(t);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error buscarTickets: " + e.getMessage());
    }

    return lista;
}
    public static ReporteCierre generarDatosCierre(LocalDate fecha) throws SQLException, ClassNotFoundException {

        String sql = 
            "SELECT " +
            " (SELECT COUNT(*) FROM ticket WHERE DATE(fecha_ingreso)=?) AS ingresados, " +
            " (SELECT COUNT(*) FROM ticket WHERE DATE(fecha_salida)=?) AS retirados, " +
            " (SELECT COUNT(*) FROM ticket WHERE fecha_salida IS NULL) AS inventario, " +
            " (SELECT COALESCE(SUM(monto),0) FROM ticket WHERE DATE(fecha_salida)=?) AS total_hoy, " +
            " (SELECT COALESCE(SUM(monto),0) FROM ticket WHERE DATE(fecha_salida)=? AND modo_tarifa='FLAT') AS total_flat, " +
            " (SELECT COALESCE(SUM(monto),0) FROM ticket WHERE DATE(fecha_salida)=? AND modo_tarifa='VARIABLE') AS total_variable, " +
            " (SELECT AVG(TIMESTAMPDIFF(MINUTE, fecha_ingreso, fecha_salida)) FROM ticket WHERE DATE(fecha_salida)=?) AS tiempo_promedio ";

        try (Connection conn = ConexionDB.getNewConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // SOLO HAY 6 PARÁMETROS
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            ps.setDate(3, java.sql.Date.valueOf(fecha));
            ps.setDate(4, java.sql.Date.valueOf(fecha));
            ps.setDate(5, java.sql.Date.valueOf(fecha));
            ps.setDate(6, java.sql.Date.valueOf(fecha));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                double tiempoPromedio = rs.getDouble("tiempo_promedio");
                if (rs.wasNull()) tiempoPromedio = Double.NaN;

                return new ReporteCierre(
                    rs.getInt("ingresados"),
                    rs.getInt("retirados"),
                    rs.getInt("inventario"),
                    rs.getDouble("total_hoy"),
                    rs.getDouble("total_flat"),
                    rs.getDouble("total_variable"),
                    tiempoPromedio
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error generarDatosCierre: " + e.getMessage());
        }

        return null;
    }
public static void generarPDFCierre(ReporteCierre r, String ruta) {
    try {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        doc.open();

        Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font sub = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font texto = new Font(Font.FontFamily.HELVETICA, 11);

        doc.add(new Paragraph("PARKPLUSS - Cierre del Día", titulo));
        doc.add(new Paragraph("Fecha: " + LocalDate.now(), texto));
        doc.add(new Paragraph("\n=================================\n", texto));

        // SECCIÓN 1 – MOVIMIENTO
        doc.add(new Paragraph("1. MOVIMIENTOS DEL DÍA", sub));
        doc.add(new Paragraph(
            "Vehículos Ingresados: " + r.ingresados + "\n" +
            "Vehículos Retirados: " + r.retirados + "\n" +
            "Inventario Actual: " + r.inventario + "\n" +
            "Tiempo Promedio de Estancia: " +
            (Double.isNaN(r.tiempoPromedio) ? "N/A" : (r.tiempoPromedio + " min")),
            texto
        ));
        doc.add(new Paragraph("\n=================================\n", texto));

        // SECCIÓN 2 – INGRESOS
        doc.add(new Paragraph("2. RESUMEN FINANCIERO", sub));
        doc.add(new Paragraph(
            "Total tarifa FLAT: Q" + String.format("%.2f", r.totalFlat) + "\n" +
            "Total tarifa VARIABLE: Q" + String.format("%.2f", r.totalVariable) + "\n" +
            "TOTAL RECAUDADO HOY: Q" + String.format("%.2f", r.totalHoy),
            texto
        ));

        doc.close();

        JOptionPane.showMessageDialog(null, "📄 Cierre generado correctamente.");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "❌ Error generando cierre PDF: " + e.getMessage());
    }
}
}