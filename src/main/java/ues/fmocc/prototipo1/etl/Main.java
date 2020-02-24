/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ues.fmocc.prototipo1.etl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.util.converter.LocalDateTimeStringConverter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jcpleitez
 */
public class Main {

    private final String url = "jdbc:postgresql://34.70.49.21:1000/prototipo1";
    private final String user = "postgres";
    private final String password = "Cal15!";
    private final String SQL = "INSERT INTO data(date_time,latitude,longitude,altitude,presion,humedad,temperatura,co2a,co2b,h2s,so2) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

    private Connection conn;
    private PreparedStatement statement;

    public Main() {
    }

    public static void main(String[] args) throws SQLException, ParseException {
        Main program = new Main();
        if (program.initConection()) {
            program.star();
        }
    }

    public void star() throws SQLException, ParseException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo con comas", "txt", "csv");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " + chooser.getSelectedFile().getPath());
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(chooser.getSelectedFile().getPath()));
                String line = reader.readLine();
                while (line != null) {
                    String array[] = line.split(",");
                    for (int i = 1; i < array.length; i++) {
                        if (i == 1) {
                            String str = array[0] + " " + array[1];                            
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy H:m:s");
                            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
                            statement.setTimestamp(i, Timestamp.valueOf(dateTime));
                        } else {
                            statement.setDouble(i, Double.parseDouble(array[i]));
                        }

                    }
                    statement.addBatch();

                    statement.executeBatch();
                    // read next line
                    line = reader.readLine();
                }

                reader.close();
                JOptionPane.showMessageDialog(null, "Se ha cargado con Exito");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Archivo no es valido");
        }
    }

    public boolean initConection() {
        try {
            conn = connect();
            statement = conn.prepareStatement(SQL);
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null, "Error de conexion a Internet o Credenciales Invalidas");
        }
        return false;

    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
