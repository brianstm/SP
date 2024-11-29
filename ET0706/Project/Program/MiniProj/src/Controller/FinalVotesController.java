package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.swing.JFrame;
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
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FinalVotesController implements Initializable{

    @FXML
    private TableView<ModelTable> table;
    
    @FXML
    private TableColumn<ModelTable, String> col_name;
    
    @FXML
    private TableColumn<ModelTable, String> col_percent;
	
    @FXML
    private Label winner;
    
    @FXML
    private PieChart pieChart;

    @FXML
    private Label finalVotesLabel;

    @FXML
    private AnchorPane pane;

    @FXML
    private Label votedFor;
    
    @FXML
    private Label nameLabel;

    @FXML
    private Label votingStatus;
    
    @FXML
    private Button logout;

    @FXML
    private Button goFinalVotes;

    @FXML
    private Button goAdmin;
    
    @FXML
    private Button home;
    
    @FXML
    private Button goVote;
    
    @FXML
    private VBox sideBar;
    
    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    private String name;
    private String pass;
    private String DateofBirth;
    private String voteForS2;
    private String voteForStr1;
    private String voteForStr2;
    private int age;
    private int minAge;
    private int countVoteFor;
    
    private ObservableList <PieChart.Data> pieChartData;
    ArrayList <String> candidateName = new ArrayList <String> ();
    ArrayList <Integer> candidateID = new ArrayList <> ();
    
    ObservableList<ModelTable> finalTable = FXCollections.observableArrayList();
    
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		handler = new DBHandler();
		
		loadData();
		hoverEffect();
		
    	goAdmin.setVisible(false);

		goVote.setText("V\u2009O\u2009T\u2009E\u2009S");
		goFinalVotes.setText("R\u2009E\u2009S\u2009U\u2009L\u2009T\u2009S");
		logout.setText("L\u2009O\u2009G\u2009O\u2009U\u2009T");
		home.setText("H\u2009O\u2009M\u2009E");
		goAdmin.setText("A\u2009D\u2009M\u2009I\u2009N");
				
		Image vote_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\vote_white.png", 20, 20, false, false);
		Image results_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\pie.png", 20, 20, false, false);
		Image home_icon = new Image("C:\\Users\\brian\\eclipse-workspace\\MiniProj\\src\\img\\home.png", 20, 20, false, false);
		
		goVote.setGraphic(new ImageView(vote_icon));
		goFinalVotes.setGraphic(new ImageView(results_icon));
		home.setGraphic(new ImageView(home_icon));
		
		pieChart.setData(pieChartData);
		pieChart.setVisible(true);
		pieChart.setLabelLineLength(10);
		pieChart.setLegendSide(Side.BOTTOM);
		pieChart.setStartAngle(180);
		pieChart.setLabelsVisible(false);

		getPercentage();
	}
	
    void setDataFinalVotes(User u) {
    	name = u.getName();
    	pass = u.getPass();
    	voteForStr1 = u.getVote();
    	DateofBirth = u.getDOB();
    	nameLabel.setText("Name: " + name);
    	try {
			voteInfo();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    void adminFinal() {
    	nameLabel.setVisible(false);
    	votingStatus.setVisible(false);
    	nameLabel.setVisible(false);
    	votedFor.setVisible(false);
    	home.setVisible(false);
    	goVote.setVisible(false);
    	goFinalVotes.setVisible(false);
    	goAdmin.setVisible(true);
    	winner.setLayoutY(60);
    	table.setLayoutY(100);
    	pieChart.setScaleX(1.2);
    	pieChart.setScaleY(1.2);
    	pieChart.setLayoutY(140);
    }
    
    @FXML
    public void initialize() {
    	pane.setOpacity(0);
    	makeFadeInTransition();
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
    void goVote(ActionEvent event) throws IOException {
    	makeFadeOut_Voting();
    }

    @FXML
    void onClickAdmin(ActionEvent event) {
    	makeFadeOut_Admin();
    }

	void voteInfo() throws SQLException {
		voteStatus s = new voteStatus(voteForStr1, DateofBirth);
		age = s.FindAge();
		votingStatus.setText(s.VoteStatus());
		votedFor.setText(s.VoteFor());			
	}

	public void loadData() {
		connection = handler.getConnection();
		pieChartData = FXCollections.observableArrayList();

    	String get1 = "SELECT voteFor, COUNT(*) FROM votes WHERE NOT voteFor = \"0\" GROUP BY voteFor";
    	try {
			pst = connection.prepareStatement(get1);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				voteForStr2 = rs.getString("voteFor");
				countVoteFor = rs.getInt("COUNT(*)");

				CandidateSelect v = new CandidateSelect();
				v.candidateDB();
				voteForS2 = v.setCandidate(voteForStr2);
		
				pieChartData.add(new PieChart.Data(voteForS2, countVoteFor));
		
				candidateName.add(voteForS2);
				candidateID.add(countVoteFor);
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

	public void getPercentage() {
		int total = 0;
		int maxValue;
		int maxValueIndex;
		int count = 0;
		
		for (int i = 0; i < candidateID.size(); i++) {
			int tmp = candidateID.get(i);
			total += tmp;
		}
		
		maxValue = Collections.max(candidateID);
		maxValueIndex = candidateID.indexOf(maxValue);
		for (int i = 0; i < candidateID.size(); i++) {
			if(maxValue == candidateID.get(i)) {
				count++;
			}
		}
		
		if(count != 1) {	
			winner.setText("Votes are tied");
		}
		else {
			winner.setText("Winner is - " + candidateName.get(maxValueIndex));
		}
		
		for (int i = 0; i < candidateName.size(); i++) {
			finalTable.add(new ModelTable(findPercentage(candidateID.get(i), total), candidateName.get(i)));
        }
		
		col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
		col_percent.setCellValueFactory(new PropertyValueFactory<>("id"));
		
		table.setItems(finalTable);
	}
	
	public String findPercentage(int num, int total) {
		double percentage;
		percentage = ((double)num / (double)total) * 100.0;
	    percentage = (double) (Math.round(percentage * 100.0) / 100.0);
		return percentage + "%";
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
    
    private void makeFadeOut_Voting(){
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
        JFrame frame = new JFrame();

    	fadeTransition.setOnFinished((ActionEvent event)-> {
    		voteStatus s = new voteStatus(voteForStr1, DateofBirth);
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
    
    private void makeFadeOut_Admin(){
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
    	fadeTransition.setOnFinished((ActionEvent event)-> {
			loadAdmin();
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
    
    private void loadVoting() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Voting.fxml"));
    		AnchorPane root = (AnchorPane)loader.load();
    		Scene newScene = new Scene(root);
    		Stage currentStage = (Stage)pane.getScene().getWindow();
    		currentStage.setScene(newScene);
    		currentStage.show();
    		
    		VotingController votingcontroller = loader.getController();
    		User u = new User(name, pass, voteForStr1, DateofBirth);
    		votingcontroller.setDataVoting(u);

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
    	
    	goAdmin.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
            	goAdmin.setStyle(style.logoutHover());
            }
        });

    	goAdmin.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                 goAdmin.setStyle(style.logoutNormal());
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
    }
}