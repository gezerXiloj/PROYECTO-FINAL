/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author gezer
 */
public class Ticket {
    private String idTicket;
    private Vehiculo vehiculo;
    private String idArea;
    private String idSpot;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaSalida;
    private ModoTarifa modoTarifa;
    private double monto;
    private boolean activo;

    public Ticket(String idTicket, Vehiculo vehiculo, String idArea, String idSpot, ModoTarifa modoTarifa) {
        this.idTicket = idTicket;
        this.vehiculo = vehiculo;
        this.idArea = idArea;
        this.idSpot = idSpot;
        this.modoTarifa = modoTarifa;
        this.fechaIngreso = LocalDateTime.now();
        this.activo = true;
    }

    // Getters y Setters
    public String getIdTicket() { return idTicket; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public ModoTarifa getModoTarifa() { return modoTarifa; }
    public boolean isActivo() { return activo; }
    public double getMonto() { return monto; }

    // Métodos lógicos
    public void cerrarTicket() {
        this.fechaSalida = LocalDateTime.now();
        this.activo = false;
    }

    public void calcularMonto() {
        if (modoTarifa == ModoTarifa.FLAT) {
            this.monto = 10.0;
        } else {
            long minutos = ChronoUnit.MINUTES.between(fechaIngreso, LocalDateTime.now());
            double horas = Math.ceil(minutos / 60.0);
            this.monto = horas * 5.0;
        }
    }

    // Validar si el ticket FLAT sigue siendo válido (menos de 2 horas fuera)
    public boolean esValidoParaReingreso() {
        if (modoTarifa == ModoTarifa.FLAT && fechaSalida != null) {
            long minutosFuera = ChronoUnit.MINUTES.between(fechaSalida, LocalDateTime.now());
            return minutosFuera <= 120;
        }
        return false;
    }
}
