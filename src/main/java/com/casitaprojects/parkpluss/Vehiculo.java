/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.casitaprojects.parkpluss;

import java.time.LocalDateTime;

/**
 *
 * @author gezer
 */
public class Vehiculo {
    String placa;
    String marca;
    String modelo;
    LocalDateTime horaEntrada;
    LocalDateTime horaSalida;

    public Vehiculo(String placa, String marca, String modelo, LocalDateTime horaEntrada, LocalDateTime horaSalida) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    
    
    
    
    
    
}
