package application;

public class style {
	public static String barNormal() {
		return "-fx-background-color: #28b498;" + 
				"-fx-text-fill: #ffffff;" + 
				"-fx-alignment: center-left;";
	}
	
	public static String barHover() {
		return "-fx-background-color: #26a38b;" + 
				"-fx-text-fill: #ffffff;" + 
				"-fx-alignment: center-left;";
	}
	
	public static String logoutNormal() {
		return "-fx-background-color: transparent;" + 
				"-fx-text-fill: #ffffff;" + 
				"-fx-border-radius: 20;" + 
				"-fx-border-color: #ffffff;" + 
				"-fx-border-width: 2;";
	}
	
	public static String logoutHover() {
		return "-fx-background-color: #88ccc7;" + 
				"-fx-text-fill: #ffffff;" + 
				"-fx-border-radius: 20;" + 
				"-fx-background-radius: 20;" + 
				"-fx-border-color: #ffffff;" + 
				"-fx-border-width: 2;";
	}
	
	public static String confirmNormal() {
		return "-fx-background-color: #28b498;" + 
				"-fx-text-fill: #ffffff;" + 
				"-fx-background-radius: 20;";
	}
	
	public static String confirmHover() {
		return "-fx-background-color: #298c79;" + 
				"-fx-text-fill: #ffffff;" + 
				"-fx-background-radius: 20;";
	}
}
