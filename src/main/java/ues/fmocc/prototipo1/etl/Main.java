/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ues.fmocc.prototipo1.etl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jcpleitez
 */
public class Main {

    private Connection conn;
    private PreparedStatement statement;

    public Main() {
    }

    public static void main(String[] args) throws SQLException, ParseException {
        Main program = new Main();

        String url = JOptionPane.showInputDialog("Ingresar la dirreccion IP de la base de datos:\nejemplo: 35.225.89.211");
        url = "jdbc:postgresql://" + url + ":1000/prototipo1";
        String user = "postgres";
        String password = "Cal15!";

        if (program.initConection(url, user, password)) {
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

            ProgressBarMod m=new ProgressBarMod();
            m.setVisible(true);            

            try {
                String path = chooser.getSelectedFile().getPath();
                FileReader fileReader = new FileReader(path);
                reader = new BufferedReader(fileReader);
                String line = reader.readLine();
                long lines = countLineJava8(path);
                long ilines = 0;
                while (line != null) {
                    String array[] = line.split(",");
                    for (int i = 1; i < array.length; i++) {
                        if (i == 1) {
                            String str = array[0] + " " + array[1];
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy H:m:s");
                            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
                            statement.setTimestamp(i, Timestamp.valueOf(dateTime));
                        } else {
                            statement.setDouble(i, Double.parseDouble(array[i]));
                        }

                    }
                    ilines++;
                    int progress = (int) ((ilines * 100) / lines);
                    System.out.println(progress);
                    m.setV(progress);
                    
                    
                    statement.addBatch();
                    statement.executeBatch();
                    // read next line
                    line = reader.readLine();
                }
                
                JOptionPane.showMessageDialog(null, "Se ha cargado con Exito");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Archivo no es valido");
        }
    }

    public boolean initConection(String url, String user, String password) {
        try {
            conn = connect(url, user, password);

            String SQL = "INSERT INTO data(date_time,latitude,longitude,altitude,presion,humedad,temperatura,co2a,co2b,h2s,so2) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

            statement = conn.prepareStatement(SQL);
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null, "Error de conexion a Internet o Credenciales Invalidas");
        }
        return false;

    }

    public Connection connect(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static long countLineJava8(String fileName) {

        Path path = Paths.get(fileName);

        long lines = 0;
        try {

            // much slower, this task better with sequence access
            //lines = Files.lines(path).parallel().count();
            lines = Files.lines(path).count();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;

    }

}
