/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iei.entrega2;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author sergisanz
 */
public class FXMLDocumentController implements Initializable {
    
    private Label label;
    @FXML
    private JFXCheckBox checkAmazon;
    @FXML
    private JFXCheckBox checkElCorteIngles;
    @FXML
    private JFXCheckBox checkFnac;
    @FXML
    private JFXTextField t_libro;
    @FXML
    private JFXTextField autor;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void Buscar(ActionEvent event) {
    }
    
}
