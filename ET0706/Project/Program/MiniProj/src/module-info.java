module MiniProj {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires java.sql;
	requires mysql.connector.java;
	requires java.base;
	requires javafx.base;
	
	opens Controller to javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;	
	
	exports Controller;
	exports application;
}
