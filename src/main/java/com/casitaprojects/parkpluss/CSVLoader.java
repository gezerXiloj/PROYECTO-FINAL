package main.java.com.casitaprojects.parkpluss;

import java.io.*;
import java.util.*;

public class CSVLoader {

    public static List<String[]> cargarCSV(String ruta) {
        List<String[]> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            boolean skipHeader = true;

            while ((linea = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                // Mantener campos vacíos al final -> limit = -1
                String[] data = linea.split(",", -1);
                // Trim de cada campo
                for (int i = 0; i < data.length; i++) data[i] = data[i].trim();
                lista.add(data);
            }

        } catch (Exception e) {
            System.out.println("⚠ Error leyendo CSV (" + ruta + "): " + e.getMessage());
        }

        return lista;
    }
    
    private String normalize(String s) {
    if (s == null) return "";
    // quitar BOM si existiera
    s = s.replace("\uFEFF", "");
    // quitar comillas si vinieran
    s = s.replace("\"", "");
    // trim y mayúsculas
    return s.trim().toUpperCase();
}
}