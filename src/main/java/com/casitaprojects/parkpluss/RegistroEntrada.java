/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class RegistroEntrada {
    private static final String RUTA_SPOTS = "C:\\Users\\gezer\\Desktop\\archivosCSV\\spots.csv";
    private static final String RUTA_HISTORICO = "C:\\Users\\gezer\\Desktop\\archivosCSV\\historico.csv";

    public static void registrarVehiculo(Vehiculo vehiculo, ModoTarifa modoTarifa) {
    try {
        String[] spotLibre = buscarSpotLibre(vehiculo.getTipoVehiculo(), vehiculo.getTipoArea());

        if (spotLibre == null) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "🚫 No hay espacios disponibles para este tipo de vehículo o área.");
            return;
        }

        String spotId = spotLibre[0];
        String areaId = spotLibre[1];

        actualizarEstadoSpot(spotId, "OCCUPIED");

        String ticketId = generarTicketId();
        Ticket ticket = new Ticket(ticketId, vehiculo, areaId, spotId, modoTarifa);

        guardarEnHistorico(ticket);

        javax.swing.JOptionPane.showMessageDialog(null, 
            "✅ Vehículo ingresado correctamente.\nÁrea: " + areaId + "\nSpot: " + spotId);

    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "❌ Error al registrar el vehículo: " + e.getMessage());
    }
}

    // 🔍 Buscar primer spot libre según tipo y área
    private static String[] buscarSpotLibre(TipoVehiculo tipoVehiculo, TipoArea tipoArea) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(RUTA_SPOTS));
        String linea;
        br.readLine(); // saltar encabezado

        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split(",");
            if (datos.length >= 4) {
                String spotId = datos[0];
                String areaId = datos[1];
                String tipo = datos[2];
                String estado = datos[3];

                // Coincidencia: mismo tipo y FREE
                if (tipo.equalsIgnoreCase(tipoVehiculo.name()) && estado.equalsIgnoreCase("FREE")) {
                    // Validar también que el área coincida con el tipoArea
                    if ((tipoArea == TipoArea.ESTUDIANTES && areaId.equals("A02")) ||
                        (tipoArea == TipoArea.CATEDRATICOS && areaId.equals("A03")) ||
                        (tipoVehiculo == TipoVehiculo.MOTO && areaId.equals("A01"))) {
                        br.close();
                        return new String[]{spotId, areaId};
                    }
                }
            }
        }
        br.close();
        return null; // no hay spots disponibles
    }

    // 🟢 Actualiza el estado del spot a OCCUPIED
    private static void actualizarEstadoSpot(String spotId, String nuevoEstado) throws IOException {
        File archivo = new File(RUTA_SPOTS);
        List<String> lineas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = br.readLine()) != null) {
            if (linea.startsWith("spot_id")) {
                lineas.add(linea);
                continue;
            }
            String[] datos = linea.split(",");
            if (datos.length >= 4 && datos[0].equals(spotId)) {
                datos[3] = nuevoEstado;
                lineas.add(String.join(",", datos));
            } else {
                lineas.add(linea);
            }
        }
        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        for (String l : lineas) {
            bw.write(l);
            bw.newLine();
        }
        bw.close();
    }

    // 🧾 Guarda ticket en historico.csv
    private static void guardarEnHistorico(Ticket ticket) throws IOException {
        File archivo = new File(RUTA_HISTORICO);
        boolean existe = archivo.exists();

        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true));

        if (!existe) {
            bw.write("ticket_id,placa,area_id,spot_id,fecha_ingreso,fecha_salida,modo,monto");
            bw.newLine();
        }

        bw.write(ticket.getIdTicket() + "," +
                ticket.getVehiculo().getPlaca() + "," +
                ticket.getVehiculo().getTipoArea() + "," +
                ticket.getIdSpot() + "," +
                ticket.getFechaIngreso() + "," +
                " ," + // fecha salida vacía al inicio
                ticket.getModoTarifa() + "," +
                "0.00");
        bw.newLine();
        bw.close();
    }

    // 🆔 Genera ID de ticket (simple)
    private static String generarTicketId() {
        int num = new Random().nextInt(9000) + 1000;
        return "T-" + num;
    }
    
public static Ticket retirarVehiculo(String placa) {
    try {
        File archivo = new File(RUTA_HISTORICO);
        if (!archivo.exists()) {
            javax.swing.JOptionPane.showMessageDialog(null, "❌ No existe el archivo histórico.");
            return null;
        }

        List<String> lineas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        boolean header = true;
        boolean encontrado = false;
        String spotId = "";
        String areaId = "";
        String modo = "";
        String fechaIngreso = "";
        String ticketId = "";

        while ((linea = br.readLine()) != null) {
            if (header) {
                lineas.add(linea);
                header = false;
                continue;
            }

            String[] datos = linea.split(",");
            if (datos.length >= 8 && datos[1].equalsIgnoreCase(placa) && datos[5].trim().isEmpty()) {
                // Ticket activo encontrado
                encontrado = true;
                ticketId = datos[0];
                areaId = datos[2];
                spotId = datos[3];
                modo = datos[6];
                fechaIngreso = datos[4];

                // Calcular monto
                double monto = calcularMonto(fechaIngreso, modo);
                String fechaSalida = LocalDateTime.now().toString();

                // Reescribir línea con salida
                lineas.add(datos[0] + "," + datos[1] + "," + datos[2] + "," + datos[3] + "," +
                           datos[4] + "," + fechaSalida + "," + modo + "," + monto);
            } else {
                lineas.add(linea);
            }
        }
        br.close();

        if (!encontrado) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "🚫 No se encontró ningún vehículo activo con la placa: " + placa);
            return null;
        }

        // 🔹 Guardar el nuevo archivo actualizado
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        for (String l : lineas) {
            bw.write(l);
            bw.newLine();
        }
        bw.close();

        // 🔹 Actualizar spot y área
        GestorCSV.actualizarEstadoSpot(spotId, "FREE");
        GestorCSV.actualizarCapacidadArea(areaId, false); // false = sumar 1 a capacidad

        javax.swing.JOptionPane.showMessageDialog(null,
            "✅ Vehículo retirado correctamente.\nMonto a pagar: Q" + calcularMonto(fechaIngreso, modo));

        // 🔹 Crear y devolver el ticket para generar PDF
        Vehiculo v = new Vehiculo(placa, TipoVehiculo.AUTO, TipoArea.ESTUDIANTES); // ajusta si quieres detectar tipo real
        Ticket ticket = new Ticket(ticketId, v, areaId, spotId, ModoTarifa.valueOf(modo));
        ticket.cerrarTicket(); // registra salida y calcula monto

        return ticket;

    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null,
            "❌ Error al retirar vehículo: " + e.getMessage());
        return null;
    }
}

// 🧮 Calcula el monto según modo y tiempo transcurrido
private static double calcularMonto(String fechaIngreso, String modo) {
    try {
        LocalDateTime ingreso = LocalDateTime.parse(fechaIngreso);
        long minutos = java.time.Duration.between(ingreso, LocalDateTime.now()).toMinutes();
        double horas = Math.ceil(minutos / 60.0);

        if (modo.equalsIgnoreCase("FLAT")) {
            return 10.00; // tarifa fija
        } else {
            return horas * 5.00; // tarifa variable
        }
    } catch (Exception e) {
        return 0.0;
    }
}
public static Ticket buscarTicketPorPlaca(String placa) {
    try {
        File archivo = new File(RUTA_HISTORICO);
        if (!archivo.exists()) return null;

        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        Ticket ultimoTicket = null;
        br.readLine(); // saltar encabezado

        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split(",");
            if (datos.length >= 8 && datos[1].equalsIgnoreCase(placa)) {
                String ticketId = datos[0];
                String area = datos[2];
                String spot = datos[3];
                String fechaIng = datos[4].trim();
                String fechaSal = datos[5].trim();
                String modo = datos[6].trim();
                double monto = Double.parseDouble(datos[7].trim());

                Vehiculo v = new Vehiculo(placa, TipoVehiculo.AUTO, TipoArea.ESTUDIANTES);
                Ticket t = new Ticket(ticketId, v, area, spot, ModoTarifa.valueOf(modo));

                // Guardar solo el último registro de esa placa (cerrado o no)
                if (!fechaSal.isEmpty()) {
                    t.setFechaSalida(LocalDateTime.parse(fechaSal));
                }
                t.setFechaIngreso(LocalDateTime.parse(fechaIng));
                t.setMonto(monto);
                ultimoTicket = t;
            }
        }
        br.close();
        return ultimoTicket; // devuelve el último (cerrado o no)
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}
   