/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

/**
 *
 * @author gezer
 */
public class Area {
    private String idArea;
    private TipoArea tipoArea;
    private int capacidad;
    private int ocupacionActual;

    // Constructor
    public Area(String idArea, TipoArea tipoArea, int capacidad) {
        this.idArea = idArea;
        this.tipoArea = tipoArea;
        this.capacidad = capacidad;
        this.ocupacionActual = 0;
    }

    // Getters y Setters
    public String getIdArea() {
        return idArea;
    }

    public TipoArea getTipoArea() {
        return tipoArea;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getOcupacionActual() {
        return ocupacionActual;
    }

    // Métodos
    public boolean hayEspacioDisponible() {
        return ocupacionActual < capacidad;
    }

    public void ocuparEspacio() {
        if (hayEspacioDisponible()) {
            ocupacionActual++;
        }
    }

    public void liberarEspacio() {
        if (ocupacionActual > 0) {
            ocupacionActual--;
        }
    }

   
}

