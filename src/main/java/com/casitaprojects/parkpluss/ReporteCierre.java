/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

/**
 *
 * @author gezer
 */
public class ReporteCierre {
    public int ingresados;
    public int retirados;
    public int inventario;
    public double totalHoy;
    public double totalFlat;
    public double totalVariable;
    public double tiempoPromedio;

    public ReporteCierre(
            int ingresados, int retirados, int inventario,
            double totalHoy, double totalFlat, double totalVariable,
            double tiempoPromedio) {

        this.ingresados = ingresados;
        this.retirados = retirados;
        this.inventario = inventario;
        this.totalHoy = totalHoy;
        this.totalFlat = totalFlat;
        this.totalVariable = totalVariable;
        this.tiempoPromedio = tiempoPromedio;
    }
}
