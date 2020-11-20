/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ues.fmocc.prototipo1.etl;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 *
 * @author jcpleitez
 */
public class ProgressBarMod extends JFrame {

    JProgressBar jb;
    int i = 0, num = 0;

    ProgressBarMod() {
        jb = new JProgressBar(0, 100);
        jb.setBounds(40, 40, 160, 30);
        jb.setValue(0);
        jb.setStringPainted(true);
        add(jb);
        setSize(250, 150);
        setLayout(null);
    }

    public void iterate() {
        while (i <= 2000) {
            jb.setValue(i);
            i = i + 20;
            try {
                
            } catch (Exception e) {
            }
        }
    }
    
    public void setV(int i) {
        
        jb.setValue(i);
        
    }
}
