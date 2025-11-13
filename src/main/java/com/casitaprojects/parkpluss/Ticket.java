/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.casitaprojects.parkpluss;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.swing.JOptionPane;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;


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

    // 🔹 Constructor
    public Ticket(String idTicket, Vehiculo vehiculo, String idArea, String idSpot, ModoTarifa modoTarifa) {
        this.idTicket = idTicket;
        this.vehiculo = vehiculo;
        this.idArea = idArea;
        this.idSpot = idSpot;
        this.modoTarifa = modoTarifa;
        this.fechaIngreso = LocalDateTime.now();
        this.activo = true;
    }

    // 🔹 Getters
    public String getIdTicket() { return idTicket; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public String getIdArea() { return idArea; }
    public String getIdSpot() { return idSpot; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public ModoTarifa getModoTarifa() { return modoTarifa; }
    public double getMonto() { return monto; }
    public boolean isActivo() { return activo; }
    
    public void setFechaIngreso(LocalDateTime fechaIngreso) {
    this.fechaIngreso = fechaIngreso;
}

public void setFechaSalida(LocalDateTime fechaSalida) {
    this.fechaSalida = fechaSalida;
}

public void setMonto(double monto) {
    this.monto = monto;
}

    // 🔹 Cerrar ticket (salida del vehículo)
    public void cerrarTicket() {
    this.fechaSalida = java.time.LocalDateTime.now();

    long minutos = java.time.Duration.between(fechaIngreso, fechaSalida).toMinutes();
    double montoCalculado = 0;

    if (modoTarifa == ModoTarifa.VARIABLE) {
        // 🔸 Siempre cobra mínimo Q5
        montoCalculado = Math.max(5, (minutos / 60.0) * 5);
    } else if (modoTarifa == ModoTarifa.FLAT) {
        // 🔹 En modo FLAT, si el usuario se pasa de 2 horas, cobra de nuevo
        if (minutos > 120) {
            montoCalculado = 5; // nueva tarifa después de 2 horas
        } else {
            montoCalculado = 0; // aún dentro del tiempo permitido
        }
    }

    this.monto = montoCalculado;
}

    // 🔹 Calcular monto según el modo
    public void calcularMonto() {
        if (modoTarifa == ModoTarifa.FLAT) {
            this.monto = 10.0;
        } else {
            long minutos = ChronoUnit.MINUTES.between(fechaIngreso, LocalDateTime.now());
            double horas = Math.ceil(minutos / 60.0);
            this.monto = horas * 5.0;
        }
    }

    // 🔹 Generar PDF del ticket o factura
    public void generarTicketPDF() {
        try {
        // 📂 Carpeta destino fija
        String carpeta = "C:\\Users\\gezer\\Desktop\\archivosCSV";
        File directorio = new File(carpeta);
        if (!directorio.exists()) {
            directorio.mkdirs(); // crear carpeta si no existe
        }

        // 🧾 Nombre del archivo PDF
        String nombreArchivo = carpeta + "\\Factura_" + idTicket + "_" + vehiculo.getPlaca() + ".pdf";

        // 🪶 Crear documento PDF
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
        doc.open();

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font textoFont = new Font(Font.FontFamily.COURIER, 11, Font.NORMAL);

        // 🧾 Encabezado
        Paragraph header = new Paragraph("---------------------------------------------\n", textoFont);
        header.add(new Paragraph("                 PARKPLUSS\n", tituloFont));
        header.add(new Paragraph("        Dirección: INTERIOR INFORMATICA\n", textoFont));
        header.add(new Paragraph("        Tel: 475731254\n", textoFont));
        header.add(new Paragraph("---------------------------------------------\n", textoFont));
        header.add(new Paragraph("                    FACTURA\n", tituloFont));
        header.add(new Paragraph("---------------------------------------------\n", textoFont));
        doc.add(header);

        // 🧠 Fechas
        String fechaEmision = java.time.LocalDate.now().toString();
        String fechaIng = (fechaIngreso != null) ? fechaIngreso.toString() : "N/A";
        String fechaSal = (fechaSalida != null) ? fechaSalida.toString() : "N/A";

        // 🧾 Cuerpo
        doc.add(new Paragraph("Factura ID:       " + idTicket, textoFont));
        doc.add(new Paragraph("Fecha de Emisión: " + fechaEmision, textoFont));
        doc.add(new Paragraph("---------------------------------------------", textoFont));
        doc.add(new Paragraph("Placa:            " + vehiculo.getPlaca(), textoFont));
        doc.add(new Paragraph("Tipo Vehículo:    " + vehiculo.getTipoVehiculo(), textoFont));
        doc.add(new Paragraph("Tipo de Usuario:  " + vehiculo.getTipoArea(), textoFont));
        doc.add(new Paragraph("---------------------------------------------", textoFont));
        doc.add(new Paragraph("Área Asignada:    " + idArea, textoFont));
        doc.add(new Paragraph("Lugar / Spot:     " + idSpot, textoFont));
        doc.add(new Paragraph("---------------------------------------------", textoFont));
        doc.add(new Paragraph("Fecha Ingreso:    " + fechaIng, textoFont));
        doc.add(new Paragraph("Fecha Salida:     " + fechaSal, textoFont));
        doc.add(new Paragraph("Modo de Cobro:    " + modoTarifa, textoFont));
        doc.add(new Paragraph("---------------------------------------------", textoFont));
        doc.add(new Paragraph("MONTO TOTAL A PAGAR:   Q" + String.format("%.2f", monto), tituloFont));
        doc.add(new Paragraph("---------------------------------------------", textoFont));
        doc.add(new Paragraph("* Gracias por usar nuestro estacionamiento *", textoFont));
        doc.add(new Paragraph("  Conserve esta factura como comprobante", textoFont));
        doc.add(new Paragraph("---------------------------------------------", textoFont));

        doc.close();

        // ✅ Mensaje de confirmación
        JOptionPane.showMessageDialog(null, 
            "🧾 Factura generada correctamente en:\n" + nombreArchivo);

        // 📂 Abrir automáticamente el PDF
        try {
            java.awt.Desktop.getDesktop().open(new File(nombreArchivo));
        } catch (Exception ex) {
            System.out.println("No se pudo abrir automáticamente el archivo: " + ex.getMessage());
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "❌ Error al generar PDF: " + e.getMessage());
    }
}
}

