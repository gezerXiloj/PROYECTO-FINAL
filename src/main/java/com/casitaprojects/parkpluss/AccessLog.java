/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.time.LocalDateTime;

/**
 *
 * @author gezer
 */
public class AccessLog {
    private int idLog;
    private String placa;
    private String razon;
    private LocalDateTime fechaHora;

    public AccessLog(String placa, String razon) {
        this.placa = placa;
        this.razon = razon;
        this.fechaHora = LocalDateTime.now();
    }
    
}
