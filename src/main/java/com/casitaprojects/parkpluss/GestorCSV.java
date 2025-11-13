/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.io.*;
import java.util.*;

public class GestorCSV {
    private static final String RUTA_AREAS = "C:\\Users\\gezer\\Desktop\\archivosCSV\\areas.csv";
    private static final String RUTA_SPOTS = "C:\\Users\\gezer\\Desktop\\archivosCSV\\spots.csv";

    // 🔹 Actualizar ocupación del área (sumar o restar)
    public static void actualizarCapacidadArea(String areaId, boolean ocupar) throws IOException {
    File archivo = new File(RUTA_AREAS);
    List<String> lineas = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(archivo));
    String linea;
    boolean header = true;

    while ((linea = br.readLine()) != null) {
        if (header) {
            lineas.add(linea);
            header = false;
            continue;
        }

        String[] datos = linea.split(",");
        if (datos[0].equals(areaId)) {
            int capacidad = Integer.parseInt(datos[2]);

            // si ocupar = true → restamos 1 (entra un vehículo)
            // si ocupar = false → sumamos 1 (sale un vehículo)
            capacidad = ocupar ? Math.max(0, capacidad - 1) : capacidad + 1;

            // reescribimos la línea modificada
            lineas.add(datos[0] + "," + datos[1] + "," + capacidad + "," + datos[3]);
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

    // 🔹 Actualizar estado de un spot (FREE / OCCUPIED)
    public static void actualizarEstadoSpot(String spotId, String nuevoEstado) throws IOException {
        File archivo = new File(RUTA_SPOTS);
        List<String> lineas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        boolean header = true;

        while ((linea = br.readLine()) != null) {
            if (header) {
                lineas.add(linea);
                header = false;
                continue;
            }

            String[] datos = linea.split(",");
            if (datos[0].equals(spotId)) {
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
}
