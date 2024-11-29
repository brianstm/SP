package Controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import DBConnection.DBHandler;
import application.style;

public class SignupController implements Initializable{

    @FXML
    private DatePicker DOB;

    @FXML
    private Label errorLabel;

    @FXML
    private RadioButton female;

    @FXML
    private ToggleGroup gender;

    @FXML
    private ImageView lock;

    @FXML
    private RadioButton male;

    @FXML
    private Label nameText;

    @FXML
    private RadioButton others;

    @FXML
    private AnchorPane pane;

    @FXML
    private Label passText;

    @FXML
    private PasswordField password;

    @FXML
    private TextField passwordShow;

    @FXML
    private ImageView person;

    @FXML
    private Button signin;

    @FXML
    private Button signup;

    @FXML
    private TextField username;

    @FXML
    private ImageView visibility;
    
    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;
    
	public String genderSelected = "";
    private String DOBFormatMDY;
    private int visibilityStatus = 1;

	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
    	handler = new DBHandler();
    	
    	hoverEffect();
    	showVisibility();
		
		signin.setText("S\u2009I\u2009G\u2009N \u0020 I\u2009N");
		signup.setText("S\u2009I\u2009G\u2009N \u0020 U\u2009P");
		
    	password.setVisible(true);
		passwordShow.setVisible(false);
	}
	
    @FXML
    public void initialize() {
    	pane.setOpacity(0);
    	makeFadeInTransition();
    }
    
    @FXML
    void userType(KeyEvent event) {
    	checkUserFilled();
    }
    
    @FXML
    void passType_off(KeyEvent event) {
    	checkPassFilled();
    }

    @FXML
    void passType_on(KeyEvent event) {
    	checkPassFilled();
    }
    
    @FXML
    void getDOB(ActionEvent event) {
    	LocalDate DateofBirth = DOB.getValue();
    	DOBFormatMDY = DateofBirth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	System.out.println("Date of Birth: " + DOBFormatMDY);
    }
    
    @FXML
    void getGender(ActionEvent event) {
    	RadioButton selectedRadioButton = (RadioButton) gender.getSelectedToggle();
    	genderSelected = selectedRadioButton.getText();
    	System.out.println("Gender selected: " + genderSelected);
    }

    @FXML
    void userLogin(ActionEvent event) {
    	makeFadeOut_Login();
    }

    @FXML
    void userSignup(ActionEvent event) {
    	if ((username.getText().isEmpty()) || (password.getText().isEmpty()) || (gender.getSelectedToggle() == null) || (DOB.getValue() == null)) {
    		errorMessage();
    	}
    	else {
        	String insert = "INSERT INTO votes(names, password, gender, voteFor, DOBYMD)" + "VALUES (?,?,?,?,?)";
        	connection = handler.getConnection();
        	try {
    			pst = connection.prepareStatement(insert);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
        	
        	try {
    			pst.setString(1, username.getText());
    			pst.setString(2, password.getText());
    			pst.setString(3, genderSelected);
    			pst.setString(4, "0");
    			pst.setString(5,  DOBFormatMDY);
    			
    			pst.executeUpdate();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
        	
        	System.out.println(username.getText() +  " - Sign up Succesfully!");
            JFrame frame = new JFrame();
        	JOptionPane.showMessageDialog(frame, "Sign up successful, please sign in to your account.", null, JOptionPane.PLAIN_MESSAGE);
        	makeFadeOut_Login();
    	}
    }

    public void checkUserFilled() {
		if(!username.getText().trim().isEmpty()) {
			person.setVisible(false);
			nameText.setVisible(false);
		}
		else {
			person.setVisible(true);
			nameText.setVisible(true);
		}
	}
    
    public void checkPassFilled() {
    	if(!password.getText().trim().isEmpty() || !passwordShow.getText().trim().isEmpty()) {
			lock.setVisible(false);
			passText.setVisible(false);
		}
		else {
			lock.setVisible(true);
			passText.setVisible(true);
		}
	} 
    
    void showVisibility() {
		InputStream on = getClass().getResourceAsStream("/img/visibility.png");
    	InputStream off = getClass().getResourceAsStream("/img/visibility_off.png");
		Image vOn = new Image(on); 
    	Image vOff = new Image(off); 

    	visibility.setPickOnBounds(true);
    	visibility.setOnMouseClicked((MouseEvent e) -> {
    		if(visibilityStatus == 1) {
    			visibilityStatus = 0;
    			visibility.setImage(vOff);
    			passwordShow.setText(password.getText());
                passwordShow.setVisible(true);
                password.setVisible(false);
    		}
    		else {
    			visibilityStatus = 1;
    			visibility.setImage(vOn);
    			password.setText(passwordShow.getText());
        		password.setVisible(true);
        		passwordShow.setVisible(false);
    		}
        });
    }	
	
    void errorMessage() {
    	System.out.println("Field must be filled up!");
		errorLabel.setText("Field must be filled up!");
    	errorLabel.setTextFill(Color.RED);
    	errorLabel.setMaxWidth(Double.MAX_VALUE);
    	errorLabel.setAlignment(Pos.CENTER);
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
    		loadLoginLogin();
    	});
    	fadeTransition.play();
    }
    
    private void loadLoginLogin() {
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
    
    public void hoverEffect() {
		pane.setStyle("-fx-background-color: #FFFFFF;");

        signup.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                signup.setStyle(style.confirmHover());
            }
        });

        signup.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                signup.setStyle(style.confirmNormal());
            }
        });
    	signin.addEventHandler(MouseEvent.MOUSE_ENTERED,
    		new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                signin.setStyle(style.logoutHover());
            }
        });

        signin.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                signin.setStyle(style.logoutNormal());
            }
       });
    }
}
