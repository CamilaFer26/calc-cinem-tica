package view;

import javax.swing.*;

import util.Colors;

import java.awt.*;
import java.awt.event.*;

public class MainView extends JFrame{
	
    public MainView() {
        setTitle("Calculadora de Cinemática");
        setSize(850, 650);
        this.setBackground(Colors.get1());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(Colors.get2());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        View1D aba1D = new View1D();
        ImageIcon icon = new ImageIcon();
        tabbedPane.addTab("Cálculos 1D", icon, aba1D, "Navegue por fórmulas cinemáticas em uma dimensão!");
       
    }
    
    public static void main(String[] args) {
    	new MainView().setVisible(true);
    } 
}
