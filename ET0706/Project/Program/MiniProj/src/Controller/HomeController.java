package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import DBConnection.DBHandler;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import application.User;
import application.style;
import application.voteStatus;

public class HomeController implements Initializable{

    @FXML
    private Button goVote;

    @FXML
    private Label votedFor;

    @FXML
    private Label votingStatus;

    @FXML
    private Label welcomeBack;
    
    @FXML
    private Label AgeLabel;

    @FXML
    private Label DOBLabel;
    
    @FXML
    private AnchorPane pane;
    
    @FXML
    private Button logout;

    @FXML
    private Button goFinalVotes;
    
    @FXML
    private Button home;

    @FXML
    private Label eligibilityLabel;
    
    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    private int age;
    private int minAge;
    private String name;
    private String pass;
    private String voteForStr;
    private String DateofBirth;

	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		handler = new DBHandler();
		
		hoverEffect();
		
		goVote.setText("V\u2009O\u2009T\u2009E\u2009S");
		goFinalVotes.setText("R\u2009E\u2009S\u2009U\u2009L\u2009T\u2009S");
		logout.setText("L\u2009O\u2009G\u2009O\u2009U\u2009T");
		home.setText("H\u2009O\u2009M\u2009E");
				
		Image vote_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\vote_white.png", 20, 20, false, false);
		Image results_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\pie.png", 20, 20, false, false);
		Image home_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\home.png", 20, 20, false, false);
		
		goVote.setGraphic(new ImageView(vote_icon));
		goFinalVotes.setGraphic(new ImageView(results_icon));
		home.setGraphic(new ImageView(home_icon));
	}
	
    void setData(User u) {
    	name = u.getName();
    	pass = u.getPass();
        welcomeBack.setText("Welcome home, " + name);
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
    void onClickVote(ActionEvent event) throws IOException {
    	makeFadeOut_Voting();
    }

    @FXML
    void onClickLogout(ActionEvent event) {
    	makeFadeOut_Login();
    }

    @FXML
    void onClickFinalVotes(ActionEvent event) {
    	makeFadeOut_FinalVotes();
    }

    void voteInfo() throws SQLException {
    	connection = handler.getConnection();
    	String get1 = "SELECT voteFor, DOBYMD FROM votes WHERE names=\"" + name +"\"";
    	try {
			pst = connection.prepareStatement(get1);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				voteForStr = rs.getString("voteFor");
				DateofBirth = rs.getString("DOBYMD");

				voteStatus s = new voteStatus(voteForStr, DateofBirth);
				age = s.FindAge();
				votingStatus.setText(s.VoteStatus());
				votedFor.setText(s.VoteFor());
				eligibilityLabel.setText(s.Eligibility());
				AgeLabel.setText("Age: " + age);
				DOBLabel.setText("Date of Birth: " + s.FormatDate());
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
    
    private void makeFadeOut_Voting(){
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
        JFrame frame = new JFrame();

    	fadeTransition.setOnFinished((ActionEvent event)-> {
    		voteStatus s = new voteStatus(voteForStr, DateofBirth);
			age = s.FindAge();
			minAge = s.getMinAge();
    		if (age >= Integer.valueOf(minAge)) {
    			loadVoting();
    		}
    		else {
    			JOptionPane.showMessageDialog(frame, "Not eligible to vote", null, JOptionPane.ERROR_MESSAGE, null);
    			loadHome();
    		}
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
    
    private void loadHome() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Home.fxml"));
    		AnchorPane root = (AnchorPane)loader.load();
    		Scene newScene = new Scene(root);
    		Stage currentStage = (Stage)pane.getScene().getWindow();
    		currentStage.setScene(newScene);
    		currentStage.show();
    		
    		HomeController homecontroller = loader.getController();
    		User u = new User(name, pass);
    		homecontroller.setData(u);	    

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
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
    		User u = new User(name, pass, voteForStr, DateofBirth);
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
    		User u = new User(name, pass, voteForStr, DateofBirth);
    		finalvotescontroller.setDataFinalVotes(u);
    
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public void hoverEffect() {
		pane.setStyle("-fx-background-color: #FFFFFF;");

    	goVote.addEventHandler(MouseEvent.MOUSE_ENTERED,
    		new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent e) {
    			goVote.setStyle(style.barHover());
    		}
    	});

    	goVote.addEventHandler(MouseEvent.MOUSE_EXITED,
    		new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent e) {
    			goVote.setStyle(style.barNormal());
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
    }
}
