/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package capstone2_group5;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;


/**
 * FXML Controller class
 *
 * @author dewikharismawati
 */
public class MainPageController implements Initializable {

    GestureRecognizer decisionTree = new AdvancedRecognizer();
    
    @FXML
    private Label label;
    
    @FXML
    private Button btnNewProfile;
    
    @FXML 
    private Button btnProfileCancel;
    
    @FXML
    private Button btnNewGesture;
    
    @FXML
    private Button btnProfileSave;
    
    @FXML
    private TextField profileName;
    
    @FXML
    private Button start;
    
    @FXML
    private Label testLabel;
    
    @FXML
    private Label nameLabelTest;
    
    @FXML
    private ComboBox comboName;

    @FXML
    private TableView<Map.Entry<Command, Gesture>> gestureMappingTable;

    @FXML
    private TableColumn<Map.Entry<Command, Gesture>, String> columnGesture;
    
    @FXML
    private TableColumn<Map.Entry<Command, Gesture>, String> columnCommand;
 
    
    ArrayList<User> users;
    
    
    int profileListChangedHandlerId = Event.registerHandler(Event.TYPE.USER_LIST_CHANGED, (event) -> {
         this.populateProfileList();
    });
          
    
    String newName;
    
    String selectedUser;
    
    HashMap<Command, Gesture> commandAndGesture;
    ArrayList<Gesture> gestures;
    ObservableList<String> gestureCombo;
    
    @FXML
    private void handleNewGesture(ActionEvent event) throws IOException, Exception{
        if(event.getSource() == btnNewGesture){
            if(UserManager.getCurrentUser() == null){
                throw new Exception("A user profile must be selected before creating a new gesture.");
            }
            LeapService.stop();
            Stage stage = (Stage) btnNewGesture.getScene().getWindow();
            
            Parent gesturePage = FXMLLoader.load(getClass().getResource("GesturePage.fxml"));
            Scene scene = new Scene(gesturePage);
            
            stage.setScene(scene);
            stage.show();
        }  
    }
    
    @FXML
    private void handleCancelNewProfile(ActionEvent event) throws IOException, Exception{
        hideNewProfile();
    }
    
   
    
    @FXML
    private void handleStart(ActionEvent event) {
        LeapService.start(decisionTree);
        DecisionTreeViewer.start();
    }
    
    @FXML
    private void handleNewProfile(ActionEvent event){
      showNewProfile();      
    }
    
    @FXML
    private void handleSaveNewProfile(ActionEvent event) throws IOException, Exception{
        newName = profileName.getText();
        UserManager.createProfile(newName);
        populateProfileList();
        selectedUser = newName;
        comboName.getSelectionModel().select(selectedUser);
        hideNewProfile();
        UserManager.setCurrentUser(selectedUser);
        loadSelectedUser(selectedUser);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideNewProfile();
        populateProfileList();
        
        try {
            populateTable();
        } catch (Exception ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    @FXML
    public void populateTable() throws IOException, Exception{
        
        gestureCombo = FXCollections.observableArrayList();
        //hasmap data
        commandAndGesture = UserManager.getCurrentUser().getCommandsAndGestures();
        
        gestures = UserManager.getCurrentUser().getGestures();
      
        gestures.forEach((gesture) -> {
            gestureCombo.add(gesture.name);});
        
        columnCommand.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Command, Gesture>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Command, Gesture>, String> p) {
                //first column -> key (Gesture)
                return new SimpleStringProperty(p.getValue().getKey().toTableString().toLowerCase());
            }
        });
        
        
        columnGesture.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Command, Gesture>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Command, Gesture>, String> p) {
                //second column value (Command)
                System.out.println("test here");
                return new SimpleStringProperty(p.getValue().getValue() == null ? "null": p.getValue().getValue().name);
            }
        });
        columnGesture.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), gestureCombo));
        
        columnGesture.setOnEditCommit(new javafx.event.EventHandler<TableColumn.CellEditEvent<Map.Entry<Command, Gesture>, String>>(){
         @Override
         public void handle(TableColumn.CellEditEvent<Map.Entry<Command, Gesture>, String> event) {
             System.out.println("Value selected is " + event.getNewValue()); //To change body of generated methods, choose Tools | Templates.
         }
        });
        
        ObservableList<Map.Entry<Command, Gesture>> items = FXCollections.observableArrayList(commandAndGesture.entrySet());
        
        gestureMappingTable.getItems().addAll(items);
        
        gestureMappingTable.setEditable(true);
    
    }
    
    
    public void populateProfileList(){ 
        comboName.getItems().removeAll(comboName.getItems());
        users = UserManager.getAllUsers();
        for(User user : users){               
            comboName.getItems().add(user.getName()); 
        }
    }
    
    public void hideNewProfile(){
        profileName.setVisible(false);
        btnProfileCancel.setVisible(false);
        btnProfileSave.setVisible(false);
    }
    
    public void showNewProfile(){
        profileName.setVisible(true);
        btnProfileCancel.setVisible(true);
        btnProfileSave.setVisible(true); 
    }
    
    //action for selected user profile combobox
    @FXML
    private void handleComboProfile(ActionEvent event) throws IOException, Exception{
      selectedUser = (String) comboName.getSelectionModel().getSelectedItem();
      UserManager.setCurrentUser(selectedUser);
      //show the current user gesture lists
      System.out.println(UserManager.getCurrentUser().makeJSONString());
    }
    
    public void loadSelectedUser(String userSelected){
        
    }

}

