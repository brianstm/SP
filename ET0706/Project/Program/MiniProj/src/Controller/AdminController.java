package Controller;	

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import DBConnection.DBHandler;
import application.ModelTable;
import application.style;
import application.voteStatus;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminController implements Initializable{

    @FXML
    private TableView<ModelTable> table = new TableView<ModelTable>();

    @FXML
    private TableColumn<ModelTable, String> col_id;

    @FXML
    private TableColumn<ModelTable, String> col_name;

    @FXML
    private Button confirmChange;
    
    @FXML
    private Button finalVotes;
    
    @FXML
    private Label insertCandidate;
    
    @FXML
    private TextField newCandidate;

    @FXML
    private Button logout;
    
    @FXML
    private TextField minAge;
    
    @FXML
    private Label minAgeText;

    @FXML
    private AnchorPane pane;

    @FXML
    private Label adminPage;
    
    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    private String nameNew;
	private String idNew;
	private String MinAge;
	private String NewCandidate;
	private int MinimumAge;

    ObservableList<ModelTable> candidateList = FXCollections.observableArrayList();

    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	tableValue();
    	hoverEffect();
    	
    	confirmChange.setText("C\u2009O\u2009N\u2009F\u2009I\u2009R\u2009M");
		logout.setText("L\u2009O\u2009G\u2009O\u2009U\u2009T");
		finalVotes.setText("R\u2009E\u2009S\u2009U\u2009L\u2009T\u2009S");
	}
    
    public void on() {
        getMinAge();
    }
    
	@FXML		
    public void initialize() {
    	pane.setOpacity(0);
    	makeFadeInTransition();
    }
	
    @FXML
    void onClickLogout(ActionEvent event) {
    	makeFadeOut_Login();
    }
    
    @FXML
    void onClickFinalVotes(ActionEvent event) {
    	makeFadeOut_FinalVotes();
    }
	
    @FXML
    void onClickConfirm(ActionEvent event) {
    	int input = JOptionPane.showConfirmDialog(null, "Confirm changes?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
    	if (input == 0) {
    		if(nameNew != null) {
    			System.out.println("Candidate " + idNew + " changed to " + nameNew);
        		String get1 = "UPDATE candidates SET candidatename = \"" + nameNew + "\" WHERE idCandidate = \"" + idNew + "\"";
    			connection = handler.getConnection();
    	    	try {
    				pst = connection.prepareStatement(get1);
    				pst.executeUpdate();
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
        		String get3 = "DELETE from candidates where candidatename=''";
    			connection = handler.getConnection();
    	    	try {
    				pst = connection.prepareStatement(get3);
    				pst.executeUpdate();
    				
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
        	}
        	
        	MinAge = minAge.getText();
        	if(!MinAge.isEmpty()) {
            	System.out.println("Minimum age set to " + MinAge);
        		String get2 = "UPDATE candidates SET minAge = \"" + MinAge + "\"";
    			connection = handler.getConnection();
    	    	try {
    				pst = connection.prepareStatement(get2);
    				pst.executeUpdate();
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
        	}
        	getMinAge();
        	
        	NewCandidate = newCandidate.getText();
        	if(!NewCandidate.isEmpty()) {
        		String insert = "INSERT INTO candidates(candidatename, minAge) " + "VALUES (\"" + NewCandidate + "\",\"" + MinimumAge + "\")";
            	connection = handler.getConnection();
            	try {
        			pst = connection.prepareStatement(insert);
        			pst.executeUpdate();
        			loadAdmin();
        		} catch (SQLException e) {
        			e.printStackTrace();
        		}
            	System.out.println("Added new candidate - " + NewCandidate);
        	}
    	}
    	loadAdmin();
    }
	
    private void tableValue() {
    	setTableEditable();
    	handler = new DBHandler();
    	connection = handler.getConnection();
    	String get = "SELECT idCandidate, candidatename FROM candidates";
    	try {
			pst = connection.prepareStatement(get);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				String idS = rs.getString("idCandidate");
				String nameS = rs.getString("candidatename");
				candidateList.add(new ModelTable(idS, nameS));
		    }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
		col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
		
		col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
		col_name.setCellFactory(TextFieldTableCell.forTableColumn());
		col_name.setOnEditCommit(new EventHandler <CellEditEvent <ModelTable, String>>() {
			public void handle(CellEditEvent<ModelTable, String> event) {
				ModelTable t = event.getRowValue();
				nameNew = event.getNewValue();
				idNew = t.getId();
				t.setName(event.getNewValue());
			}
		});	
		table.setItems(candidateList);
    }
    
    private void setTableEditable() {
        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);
    }
	
	private void getMinAge() {
		voteStatus s = new voteStatus(null);
		MinimumAge = s.getMinAge();
		minAgeText.setText("Minimum age to vote (" + MinimumAge + "): ");
	}
	
	private void makeFadeInTransition() {
		FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(0);
    	fadeTransition.setToValue(1);	
    	fadeTransition.play();
	}	
	
	private void makeFadeOut_Login(){
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
    	fadeTransition.setOnFinished((ActionEvent event)-> {
			loadLogin();
    	});
    	fadeTransition.play();
    }
	
    private void makeFadeOut_FinalVotes(){
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
    	fadeTransition.setOnFinished((ActionEvent event)-> {
			loadFinalVotes();
    	});
    	fadeTransition.play();
    }

	private void loadLogin() {
    	try {
    		Parent nextView;
			nextView = FXMLLoader.load(getClass().getResource("/FXML/LoginMain.fxml"));
			Scene newScene = new Scene(nextView);
	    	Stage currentStage = (Stage) pane.getScene().getWindow();
	    	currentStage.setScene(newScene);
	    	
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void loadFinalVotes() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/FinalVotes.fxml"));
    		AnchorPane root = (AnchorPane)loader.load();
    		Scene newScene = new Scene(root);
    		Stage currentStage = (Stage)pane.getScene().getWindow();
    		currentStage.setScene(newScene);
    		currentStage.show();
    		
    		FinalVotesController finalvotescontroller = loader.getController();
    		finalvotescontroller.adminFinal();

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void loadAdmin() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Admin.fxml"));
    		AnchorPane root = (AnchorPane) loader.load();
    		Scene newScene = new Scene(root);
    		Stage currentStage = (Stage)pane.getScene().getWindow();
    		currentStage.setScene(newScene);
    		currentStage.show();
    		
    		AdminController admincontroller = loader.getController();
    		admincontroller.on();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void hoverEffect() {
		pane.setStyle("-fx-background-color: #FFFFFF;");

    	finalVotes.addEventHandler(MouseEvent.MOUSE_ENTERED,
        	new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent e) {
        		finalVotes.setStyle(style.confirmHover());
        	}
        });
    	finalVotes.addEventHandler(MouseEvent.MOUSE_EXITED,
        	new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent e) {
        		finalVotes.setStyle(style.confirmNormal());
        	}
        });
    	logout.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	logout.setStyle(style.confirmHover());
            }
        });
        logout.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	logout.setStyle(style.confirmNormal());
            }
        });
        confirmChange.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	confirmChange.setStyle(style.confirmHover());
            }
        });
        confirmChange.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	confirmChange.setStyle(style.confirmNormal());
            }
        });
    }
}