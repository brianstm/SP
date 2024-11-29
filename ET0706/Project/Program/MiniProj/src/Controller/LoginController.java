package Controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.scene.control.Label;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import DBConnection.DBHandler;
import application.User;
import application.style;

public class LoginController implements Initializable{

    @FXML
    private AnchorPane pane;
    
    @FXML
    private ImageView visibility;
    
    @FXML
    private ImageView lock;
    
    @FXML
    private Label passText;
    
    @FXML
    private Label nameText;
    
    @FXML
    private TextField username;
    
    @FXML
    private Button login;

    @FXML
    private PasswordField password;
    
    @FXML
    private TextField passwordShow = new TextField();

    @FXML
    private ImageView person;
    
    @FXML
    private Button signup;
    
    @FXML
    private Label incorrectLogin;
    
    @FXML
    private StackPane parentContainer;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    private int visibilityStatus = 1;
    private String passwordText;
    
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
    	handler = new DBHandler();
    	
    	hoverEffect();
		showVisibility();
		
		login.setText("S\u2009I\u2009G\u2009N \u0020 I\u2009N");
		signup.setText("S\u2009I\u2009G\u2009N \u0020 U\u2009P");
		
    	incorrectLogin.setMaxWidth(Double.MAX_VALUE);
    	incorrectLogin.setAlignment(Pos.CENTER);
    	
    	password.setVisible(true);
		passwordShow.setVisible(false);
	}
	    
	@FXML
	public void initialize() {
		pane.setOpacity(0);
		makeFadeInTransition();
	}

    @FXML
    void passType_off(KeyEvent event) {
    	checkPassFilled();
    	passwordText = password.getText();
    }

    @FXML
    void passType_on(KeyEvent event) {
    	checkPassFilled();
    	passwordText = passwordShow.getText();
    }
	
    @FXML
    void userType(KeyEvent event) {
    	checkUserFilled();
    }
    
    @FXML
    void userSignup(ActionEvent event) throws IOException {
    	makeFadeOut_Signup();
    }
    
    public boolean adminLogin() {
    	String getName = new String(username.getText());
    	String adminName = new String("admin");
    	String adminPass = new String("1234");

    	return (getName.equals(adminName) && passwordText.equals(adminPass));
    }
    
    @SuppressWarnings("exports")
	@FXML
    public void userLogin(ActionEvent event) throws IOException{
    	if (adminLogin()){
    		makeFadeOut_Admin();
    		System.out.println("Admin login");
    	}
    	else {
        	connection = handler.getConnection();
        	String get1 = "SELECT * from votes where names=? and password=?";
        	
        	try {
    			pst = connection.prepareStatement(get1);
    			pst.setString(1, username.getText());
    			pst.setString(2, passwordText);
    			ResultSet rs = pst.executeQuery();
    			
    			int count = 0;
    			while(rs.next()) {
    				count += 1;
    			}
    			if(count == 1) {
    				System.out.println(username.getText() + " - Login Successful");
    				makeFadeOut_Home();
    			}
    			else {
    				System.out.println("Username and/or password incorrect");
    		    	incorrectLogin.setText("Username and/or password incorrect!");
    		    	incorrectLogin.setTextFill(Color.RED);
    		    	incorrectLogin.setMaxWidth(Double.MAX_VALUE);
    		    	incorrectLogin.setAlignment(Pos.CENTER);
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
    
    private void makeFadeOut_Signup() throws IOException{    	
    	FadeTransition fadeTransition = new FadeTransition();
    	fadeTransition.setDuration(Duration.millis(300));
    	fadeTransition.setNode(pane);
    	fadeTransition.setFromValue(1);
    	fadeTransition.setToValue(0);
    	
    	fadeTransition.setOnFinished((ActionEvent event)-> {
    		loadSignUp();
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
    
    private void loadHome() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Home.fxml"));
    		AnchorPane root = (AnchorPane) loader.load();
    		Scene newScene = new Scene(root);
    		Stage currentStage = (Stage)pane.getScene().getWindow();
    		currentStage.setScene(newScene);
    		currentStage.show();
    		
    		HomeController homecontroller = loader.getController();
    		User u = new User(username.getText(),passwordText);
    		homecontroller.setData(u);	    	

    	} catch (IOException e) {
			e.printStackTrace();
		}		
    }
    
    private void loadSignUp() {
    	try {
    		Parent nextView;
			nextView = FXMLLoader.load(getClass().getResource("/FXML/Sign Up.fxml"));
			Scene newScene = new Scene(nextView);
	    	Stage currentStage = (Stage) pane.getScene().getWindow();
	    	currentStage.setScene(newScene);
	    	
    	} catch (IOException e) {
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

        login.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                login.setStyle(style.confirmHover());
            }
        });

        login.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                login.setStyle(style.confirmNormal());
            }
        });
    	signup.addEventHandler(MouseEvent.MOUSE_ENTERED,
    		new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                signup.setStyle(style.logoutHover());
            }
        });

        signup.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                signup.setStyle(style.logoutNormal());
            }
       });
    }
}