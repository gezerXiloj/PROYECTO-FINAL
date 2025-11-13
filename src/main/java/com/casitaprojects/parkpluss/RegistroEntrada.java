package main.java.com.casitaprojects.parkpluss;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import static main.java.com.casitaprojects.parkpluss.GestorCSV.actualizarEstadoSpot;

public class RegistroEntrada {

    private static final String RUTA_HISTORICO = "C:\\Users\\gezer\\Desktop\\archivosCSV\\historico.csv";
    private static final String RUTA_SPOTS = "C:\\Users\\gezer\\Desktop\\archivosCSV\\spots.csv";

    // 🔹 Buscar spot libre disponible
    private static String[] buscarSpotLibre(TipoVehiculo tipoVehiculo, TipoArea tipoArea) throws IOException {
        File archivo = new File(RUTA_SPOTS);
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean header = true;
            while ((linea = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] datos = linea.split(",");
                if (datos.length == 4 && datos[2].equalsIgnoreCase(tipoVehiculo.toString()) && datos[3].equalsIgnoreCase("FREE")) {
                    return new String[]{datos[0], datos[1]}; // spotId, areaId
                }
            }
        }
        return null;
    }

    // 🔹 Registrar vehículo al ingresar
    public static Ticket registrarVehiculo(Vehiculo vehiculo, ModoTarifa modoTarifa) {
        try {
            // 🚫 Evitar duplicados
            Ticket existente = buscarTicketPorPlaca(vehiculo.getPlaca());
            if (existente != null) {
                javax.swing.JOptionPane.showMessageDialog(null, "⚠️ Este vehículo ya tiene un ticket activo (no ha salido aún).");
                return null;
            }

            String[] spotLibre = buscarSpotLibre(vehiculo.getTipoVehiculo(), vehiculo.getTipoArea());
            if (spotLibre == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "🚫 No hay espacios disponibles para este tipo de vehículo o área.");
                return null;
            }

            String spotId = spotLibre[0];
            String areaId = spotLibre[1];

            // Marcar el spot como ocupado
            actualizarEstadoSpot(spotId, "OCCUPIED");

            // Crear ticket y asignar datos
            String ticketId = generarIdTicket();
            Ticket ticket = new Ticket(ticketId, vehiculo, areaId, spotId, modoTarifa);

            // Guardar en histórico (una sola vez)
            guardarEnHistorico(ticket);

            // Confirmar
            javax.swing.JOptionPane.showMessageDialog(null,
                    "✅ Vehículo ingresado correctamente.\nÁrea: " + areaId + "\nSpot: " + spotId);

            return ticket;

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "❌ Error al registrar el vehículo: " + e.getMessage());
            return null;
        }
    }

    // 🔹 Guardar o actualizar el CSV histórico
    public static void guardarEnHistorico(Ticket ticket) throws IOException {
        File archivo = new File(RUTA_HISTORICO);
        boolean existe = archivo.exists();
        File carpeta = archivo.getParentFile();
        if (!carpeta.exists()) carpeta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            if (!existe) {
                bw.write("ticket_id,placa,area_id,spot_id,fecha_ingreso,fecha_salida,modo,monto");
                bw.newLine();
            }
            bw.write(ticket.getIdTicket() + "," +
                    ticket.getVehiculo().getPlaca() + "," +
                    ticket.getIdArea() + "," +
                    ticket.getIdSpot() + "," +
                    ticket.getFechaIngreso() + "," +
                    "" + "," +
                    ticket.getModoTarifa() + "," +
                    ticket.getMonto());
            bw.newLine();
        }
    }

    // 🔹 Generar ID de ticket
    private static String generarIdTicket() {
        return "T-" + (new Random().nextInt(9000) + 1000);
    }

    // 🔹 Buscar ticket activo por placa
    public static Ticket buscarTicketPorPlaca(String placa) {
        File archivo = new File(RUTA_HISTORICO);
        if (!archivo.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean header = true;

            while ((linea = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                String[] datos = linea.split(",");
                if (datos.length < 8) continue;

                String ticketPlaca = datos[1];
                String fechaSalida = datos[5];
                String modo = datos[6];

                if (ticketPlaca.equalsIgnoreCase(placa.trim()) && (fechaSalida == null || fechaSalida.isEmpty())) {

                    Vehiculo v = new Vehiculo(
                            placa,
                            datos[2].equalsIgnoreCase("A01") ? TipoVehiculo.MOTO : TipoVehiculo.AUTO,
                            datos[2].equalsIgnoreCase("A03") ? TipoArea.CATEDRATICOS : TipoArea.ESTUDIANTES
                    );

                    ModoTarifa modoTarifa = modo.equalsIgnoreCase("FLAT") ? ModoTarifa.FLAT : ModoTarifa.VARIABLE;

                    Ticket t = new Ticket(datos[0], v, datos[2], datos[3], modoTarifa);
                    t.setFechaIngreso(LocalDateTime.parse(datos[4]));
                    return t;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "⚠️ Error al buscar ticket: " + e.getMessage());
        }

        return null;
    }

    // 🔹 Retirar vehículo (ya sin doble PDF)
    public static Ticket retirarVehiculo(String placa) {
        try {
            Ticket ticket = buscarTicketPorPlaca(placa);
            if (ticket == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "🚫 No se encontró ticket activo para la placa: " + placa);
                return null;
            }

            ticket.cerrarTicket();

            File archivo = new File(RUTA_HISTORICO);
            List<String> lineas = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                boolean header = true;

                while ((linea = br.readLine()) != null) {
                    if (header) {
                        lineas.add(linea);
                        header = false;
                        continue;
                    }

                    String[] datos = linea.split(",");
                    if (datos[1].equalsIgnoreCase(placa) && (datos[5].isEmpty() || datos[5].equals("null"))) {
                        datos[5] = ticket.getFechaSalida().toString();
                        datos[7] = String.format("%.2f", ticket.getMonto());
                        lineas.add(String.join(",", datos));
                    } else {
                        lineas.add(linea);
                    }
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String l : lineas) {
                    bw.write(l);
                    bw.newLine();
                }
            }

            // Liberar spot y actualizar área
            GestorCSV.actualizarEstadoSpot(ticket.getIdSpot(), "FREE");
            GestorCSV.actualizarCapacidadArea(ticket.getIdArea(), false);

            // ❌ Se eliminó la generación del PDF aquí (solo lo genera el botón)
            // ticket.generarTicketPDF();

            javax.swing.JOptionPane.showMessageDialog(null, "✅ Vehículo retirado exitosamente.\nMonto: Q" + ticket.getMonto());
            return ticket;

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "❌ Error al retirar vehículo: " + e.getMessage());
            return null;
        }
    }

    // 🔹 Registrar reingreso temporal (para modo FLAT)
    public static void marcarReingresoTemporal(Ticket ticket, int horasPermitidas) {
        try {
            File archivo = new File("C:\\Users\\gezer\\Desktop\\archivosCSV\\reingresos.csv");
            boolean existe = archivo.exists();
            File carpeta = archivo.getParentFile();
            if (!carpeta.exists()) carpeta.mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
                if (!existe) {
                    bw.write("ticket_id,placa,fecha_salida,horas_permitidas");
                    bw.newLine();
                }

                bw.write(ticket.getIdTicket() + "," +
                        ticket.getVehiculo().getPlaca() + "," +
                        ticket.getFechaSalida() + "," +
                        horasPermitidas);
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "⚠️ Error al registrar reingreso temporal: " + e.getMessage());
        }
    }
}