/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import main.java.com.casitaprojects.parkpluss.TipoArea;
import main.java.com.casitaprojects.parkpluss.TipoVehiculo;

/**
 *
 * @author gezer
 */
public class Vehiculo {
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private TipoArea tipoArea;

    // Constructor
    public Vehiculo(String placa, TipoVehiculo tipoVehiculo, TipoArea tipoArea) {
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoArea = tipoArea;
    }

    // Getters y Setters
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public TipoArea getTipoArea() {
        return tipoArea;
    }

    public void setTipoArea(TipoArea tipoArea) {
        this.tipoArea = tipoArea;
    }
    
    public void guardarEnCSV() {
    String rutaArchivo = "C:\\Users\\gezer\\Desktop\\archivosCSV\\Vehiculos.csv";
    File archivo = new File(rutaArchivo);
    List<String> lineas = new ArrayList<>();
    boolean placaExistente = false;

    try {
        // Si el archivo existe, leemos todas las líneas
        if (archivo.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                // Evita reescribir el encabezado
                if (linea.startsWith("placa")) {
                    lineas.add(linea);
                    continue;
                }

                String[] datos = linea.split(",");
                if (datos.length > 0 && datos[0].equalsIgnoreCase(this.placa)) {
                    // Si la placa ya existe, la reemplazamos con los nuevos datos
                    lineas.add(this.placa + "," + this.tipoVehiculo + "," + this.tipoArea);
                    placaExistente = true;
                } else {
                    lineas.add(linea);
                }
            }
            br.close();
        }

        // Si el archivo no existe, agregamos encabezado
        if (!archivo.exists()) {
            lineas.add("placa,tipo_vehiculo,tipo_area");
        }

        // Si no se encontró la placa, agregamos nueva línea
        if (!placaExistente) {
            lineas.add(this.placa + "," + this.tipoVehiculo + "," + this.tipoArea);
        }

        // Escribimos todo nuevamente
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        for (String linea : lineas) {
            bw.write(linea);
            bw.newLine();
        }
        bw.close();

        System.out.println("✅ Vehículo guardado correctamente en el CSV.");

    } catch (IOException e) {
        System.err.println("❌ Error al guardar el vehículo: " + e.getMessage());
    }
}
    
}
    
