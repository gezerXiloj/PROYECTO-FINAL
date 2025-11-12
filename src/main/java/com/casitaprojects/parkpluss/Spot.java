/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

/**
 *
 * @author gezer
 */
public class Spot {
    private String idSpot;
    private String idArea;
    private TipoVehiculo tipoVehiculo;
    private boolean ocupado;

    // Constructor
    public Spot(String idSpot, String idArea, TipoVehiculo tipoVehiculo) {
        this.idSpot = idSpot;
        this.idArea = idArea;
        this.tipoVehiculo = tipoVehiculo;
        this.ocupado = false;
    }

    // Getters y Setters
    public String getIdSpot() { return idSpot; }
    public String getIdArea() { return idArea; }
    public TipoVehiculo getTipoVehiculo() { return tipoVehiculo; }
    public boolean isOcupado() { return ocupado; }

    public void ocupar() { ocupado = true; }
    public void liberar() { ocupado = false; }
}
