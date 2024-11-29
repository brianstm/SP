package application;

public class User {
	String name;
	String password;
	String voteFor;
	String DateofBirth;
	
	public User(String name, String password){
		this.name = name;
		this.password = password;
	}
	
	public User(String name, String password, String voteFor, String DateofBirth){
		this.name = name;
		this.password = password;
		this.voteFor = voteFor;
		this.DateofBirth = DateofBirth;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPass() {
		return password;
	}
	
	public void setPass(String password) {
		this.password = password;
	}
	
	public String getVote() {
		return voteFor;
	}
	
	public void setVote(String voteFor) {
		this.voteFor = voteFor;
	}
	
	public String getDOB() {
		return DateofBirth;
	}
	
	public void setDOB(String DateofBirth) {
		this.DateofBirth = DateofBirth;
	}
}
