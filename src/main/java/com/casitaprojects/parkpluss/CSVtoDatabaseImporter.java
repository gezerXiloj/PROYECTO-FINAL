package main.java.com.casitaprojects.parkpluss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CSVtoDatabaseImporter {

    private final String carpeta = "C:\\Users\\gezer\\Desktop\\archivosCSV\\";

    public void importarTodo() {

        System.out.println("📥 Importando AREAS...");
        importarAreas();

        System.out.println("📥 Importando SPOTS...");
        importarSpots();

        System.out.println("📥 Importando VEHICULOS...");
        importarVehiculos();

        System.out.println("📥 Importando TICKETS...");
        importarTickets();

        System.out.println("\n✅ IMPORTACIÓN COMPLETA.");
    }

    // -------------------------------------------------------------
    // IMPORTAR AREAS
    // -------------------------------------------------------------
    private void importarAreas() {
        String ruta = carpeta + "areas.csv";

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("⚠ No existe: " + ruta);
            return;
        }

        String sql = "INSERT IGNORE INTO AREA (id_area, nombre, capacidad_maxima, tipo_vehiculo) VALUES (?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            String linea;
            br.readLine(); // saltar encabezado

            while ((linea = br.readLine()) != null) {
                String[] d = linea.split(",");

                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.setInt(3, Integer.parseInt(d[2]));
                ps.setString(4, d[3]);

                ps.executeUpdate();
            }

            ps.close();
            conn.close();

            System.out.println("✔ AREAS importadas.");

        } catch (Exception e) {
            System.out.println("❌ Error en AREAS: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // IMPORTAR SPOTS
    // -------------------------------------------------------------
    private void importarSpots() {
        String ruta = carpeta + "spots.csv";

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("⚠ No existe: " + ruta);
            return;
        }

        String sql = "INSERT IGNORE INTO SPOT (id_spot, id_area, tipo_vehiculo, status) VALUES (?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {

                String[] d = linea.split(",");

                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.setString(3, d[2]);
                ps.setString(4, d[3]);

                ps.executeUpdate();
            }

            ps.close();
            conn.close();

            System.out.println("✔ SPOTS importados.");

        } catch (Exception e) {
            System.out.println("❌ Error en SPOTS: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // IMPORTAR VEHICULOS
    // -------------------------------------------------------------
    private void importarVehiculos() {
        String ruta = carpeta + "vehiculos.csv";

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("⚠ No existe: " + ruta);
            return;
        }

        String sql = "INSERT IGNORE INTO VEHICULO (placa, tipo_vehiculo, tipo_area_asignada) VALUES (?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {

                String[] d = linea.split(",");

                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.setString(3, d[2]);

                ps.executeUpdate();
            }

            ps.close();
            conn.close();

            System.out.println("✔ VEHICULOS importados.");

        } catch (Exception e) {
            System.out.println("❌ Error en VEHICULOS: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // IMPORTAR TICKETS
    // -------------------------------------------------------------
    private void importarTickets() {
        String ruta = carpeta + "historico.csv";

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("⚠ No existe: " + ruta);
            return;
        }

        String sql = "INSERT IGNORE INTO TICKET (id_ticket, placa, id_area, id_spot, fecha_ingreso, fecha_salida, modo_tarifa, monto)"
                   + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {

                String[] d = linea.split(",");

                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.setString(3, d[2]);
                ps.setString(4, d[3]);

                ps.setString(5, d[4].replace("T", " "));
                ps.setString(6, d[5].isBlank() ? null : d[5].replace("T", " "));

                ps.setString(7, d[6]);
                ps.setDouble(8, Double.parseDouble(d[7]));

                ps.executeUpdate();
            }

            ps.close();
            conn.close();

            System.out.println("✔ TICKETS importados.");

        } catch (Exception e) {
            System.out.println("❌ Error en TICKETS: " + e.getMessage());
        }
    }
}