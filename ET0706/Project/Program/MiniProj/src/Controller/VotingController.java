package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import DBConnection.DBHandler;
import application.CandidateSelect;
import application.ModelTable;
import application.User;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VotingController implements Initializable {

    @FXML
    private Button cancel;

    @FXML
    private Button goVote;
    
    @FXML
    private Button goVote1;

    @FXML
    private TableColumn<ModelTable, String> col_name;

    @FXML
    private TableColumn<ModelTable, String> col_id;

    @FXML
    private AnchorPane pane;

    @FXML
    private TableView<ModelTable> table = new TableView<ModelTable>();

    @FXML
    private Label votedFor;

    @FXML
    private Label votingLabel;

    @FXML
    private Label votingStatus;
    
    @FXML
    private Button logout;

    @FXML
    private Button goFinalVotes;
    
    @FXML
    private Button home;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    private String name;
    private String pass;
    private String canName;
    private String canID;
    private String voteForStr1;
    private String DateofBirth;
    
    ArrayList <String> candidateList = new ArrayList <String> ();
    ObservableList<ModelTable> candidateOList = FXCollections.observableArrayList();

	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		handler = new DBHandler();
		
		hoverEffect();
		
		cancel.setText("C\u2009A\u2009N\u2009C\u2009E\u2009L");
		goVote.setText("C\u2009O\u2009N\u2009F\u2009I\u2009R\u2009M \u0020 \u2009V\u2009O\u2009T\u2009E");
		
		goVote1.setText("V\u2009O\u2009T\u2009E\u2009S");
		goFinalVotes.setText("R\u2009E\u2009S\u2009U\u2009L\u2009T\u2009S");
		logout.setText("L\u2009O\u2009G\u2009O\u2009U\u2009T");
		home.setText("H\u2009O\u2009M\u2009E");
				
		Image vote_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\vote_white.png", 20, 20, false, false);
		Image results_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\pie.png", 20, 20, false, false);
		Image home_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\home.png", 20, 20, false, false);
		
		goVote1.setGraphic(new ImageView(vote_icon));
		goFinalVotes.setGraphic(new ImageView(results_icon));
		home.setGraphic(new ImageView(home_icon));

		tableValue();
	}
    
    void setDataVoting(User u) {
    	name = u.getName();
    	pass = u.getPass();
    	voteForStr1 = u.getVote();
    	DateofBirth = u.getDOB();
    	try {
			voteInfo();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    public void initialize() {
    	pane.setOpacity(0);
    	makeFadeInTransition();
    }
    
    @FXML
    void onClickVote(ActionEvent event) {
        ModelTable t = table.getSelectionModel().getSelectedItem();
        String candidateSelectedtmp = t.getName();
        
        CandidateSelect v = new CandidateSelect();
		v.candidateDB();
		canID = v.getCandidate(candidateSelectedtmp);
        System.out.println("Candidate Selected - " + candidateSelectedtmp + ", " + canID); 

        int input = JOptionPane.showConfirmDialog(null, "Confirm vote for " + candidateSelectedtmp + "?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (input == 0) {
    	    String insert = "UPDATE votes SET voteFor = \"" + canID + "\" WHERE names = \"" + name + "\"";
    	    connection = handler.getConnection();
    	    try {
    			pst = connection.prepareStatement(insert);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		} 
        	try {
   				pst.executeUpdate();
   				
   			} catch (SQLException e) {
   				e.printStackTrace();    			
   			}
        	loadVoting();
        }
    }
    
    @FXML
    void onClickCancel(ActionEvent event) {
    	makeFadeOut_Home();
    }

    @FXML
    void goHome(ActionEvent event) {
    	makeFadeOut_Home();
    }

    @FXML
    void onClickLogout(ActionEvent event) {
    	makeFadeOut_Login();
    }

    @FXML
    void onClickFinalVotes(ActionEvent event) {
    	makeFadeOut_FinalVotes();
    }
    
    private void tableValue() {
    	handler = new DBHandler();
    	connection = handler.getConnection();
    	String get = "SELECT idCandidate, candidatename FROM candidates";
    	try {
			pst = connection.prepareStatement(get);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				String idS = rs.getString("idCandidate");
				String nameS = rs.getString("candidatename");
				candidateOList.add(new ModelTable(idS, nameS));
		    }
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
		col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
		col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		table.setItems(candidateOList);
    }
    
    void getCandidateName() {
    	connection = handler.getConnection();
    	String get1 = "SELECT candidatename FROM candidates";
    	try {
			pst = connection.prepareStatement(get1);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				canName = rs.getString("candidatename");
				candidateList.add(canName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	finally {
    		try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    }

    void voteInfo() throws SQLException {
    	voteStatus s = new voteStatus(voteForStr1);
		votingStatus.setText(s.VoteStatus());
		votedFor.setText(s.VoteFor());
    }
    
	private void makeFadeInTransition() {
		FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(0);
    	fadeTransition.setToValue(1);	
    	fadeTransition.play();
	}
    
    private void makeFadeOut_Home(){
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
    	fadeTransition.setOnFinished((ActionEvent event)-> {
			loadHome();
    	});
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
    
    private void loadHome() {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Home.fxml"));
        	AnchorPane root = (AnchorPane) loader.load();
        		
        	Scene scene2 = new Scene(root);
        	Stage Window2 = (Stage)pane.getScene().getWindow();
        	Window2.setScene(scene2);
        	Window2.show();
        		
        	HomeController homecontroller = loader.getController();
    		User u = new User(name, pass);
        	homecontroller.setData(u);	    	
        } catch (IOException e) {
        	e.printStackTrace();
        }
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
    
    private void loadVoting() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Voting.fxml"));
    		AnchorPane root = (AnchorPane)loader.load();
    		Scene newScene = new Scene(root);
    		Stage currentStage = (Stage)pane.getScene().getWindow();
    		currentStage.setScene(newScene);
    		currentStage.show();
    		
    		VotingController votingcontroller = loader.getController();
    		User u = new User(name, pass, canID,DateofBirth);
    		votingcontroller.setDataVoting(u);

    	}catch(Exception e) {
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
    		User u = new User(name, pass, voteForStr1, DateofBirth);
    		finalvotescontroller.setDataFinalVotes(u);

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void hoverEffect() {
		pane.setStyle("-fx-background-color: #FFFFFF;");

    	home.addEventHandler(MouseEvent.MOUSE_ENTERED,
    		new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent e) {
    			home.setStyle(style.barHover());
    		}
    	});

    	home.addEventHandler(MouseEvent.MOUSE_EXITED,
    		new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent e) {
    			home.setStyle(style.barNormal());
    		}
    	});
    	goFinalVotes.addEventHandler(MouseEvent.MOUSE_ENTERED,
        	new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent e) {
        		goFinalVotes.setStyle(style.barHover());
        	}
        });

    	goFinalVotes.addEventHandler(MouseEvent.MOUSE_EXITED,
        	new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent e) {
        		goFinalVotes.setStyle(style.barNormal());
        	}
        });
    	
    	logout.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	logout.setStyle(style.logoutHover());
            }
        });

        logout.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	logout.setStyle(style.logoutNormal());
            }
        });
        
        goVote.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                goVote.setStyle(style.confirmHover());
            }
        });

        goVote.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	goVote.setStyle(style.confirmNormal());
            }
        });
        
        cancel.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                cancel.setStyle(style.confirmHover());
            }
        });

        cancel.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                cancel.setStyle(style.confirmNormal());
            }
        });
    }
}
