package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import DBConnection.DBHandler;

public class voteStatus {
	int age;
	int MinAge;
	String DateofBirth;
	String voteForS;
	String voteForStr;

    private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdfNew = new SimpleDateFormat("EEEE, MMM d, yyyy");
    
	public voteStatus(String voteForStr){
		this.voteForStr = voteForStr;
	}
	
	public voteStatus(String voteForStr, String DateofBirth) {
		this.voteForStr = voteForStr;
		this.DateofBirth = DateofBirth;
	}
	
	public void candidate() {
		CandidateSelect v = new CandidateSelect();
		v.candidateDB();
		voteForS = v.setCandidate(voteForStr);
	}
	
	public String VoteStatus(){
		String voteSText = null;
		String tmp = voteForStr;
		try{
            if(Integer.valueOf(voteForStr) == 0) {
    			voteSText = "Voting status: Not Voted";
    		}
        }
        catch (NumberFormatException ex){
            voteForStr = tmp;
            candidate();
			voteSText = "Voting status: Voted";
        }
		return voteSText;
	}
	
	public String VoteFor() {
		String voteText = null;
		String tmp = voteForStr;
		try{
            if(Integer.valueOf(voteForStr) == 0) {
    			voteText = "Voted for: Not Voted";
    		}
        }
        catch (NumberFormatException ex){
            voteForStr = tmp;
            candidate();
			voteText = "Voted for: " + voteForS;
        }
		return voteText;
	}
	
	public String FormatDate() {
		return FormatDate(DateofBirth);
	}
	
    public static int calculateAge(LocalDate dob) {  
    	LocalDate curDate = LocalDate.now();  
    	if ((dob != null) && (curDate != null)) {  
    		return Period.between(dob, curDate).getYears();  
    	}  
    	else  
    		return 0;  
    }
	
	public int FindAge() {
		LocalDate dob = LocalDate.parse(DateofBirth);  
		age = calculateAge(dob);
		return age;
	}
	
    private void minAge() {
		handler = new DBHandler();
		String get = "SELECT minAge FROM candidates";
		connection = handler.getConnection();
		try {
			pst = connection.prepareStatement(get);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				MinAge = rs.getInt("minAge");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    public int getMinAge() {
    	minAge();
    	return MinAge;
    }

	public String Eligibility() {
		getMinAge();
		if (age >= MinAge)
			return "Voting Eligibility: Eligible";
		else
			return "Voting Eligibility: Ineligible";
	}
    
    public static String FormatDate(String dateOLD) {
		String dateString = dateOLD;
		String datef = "";
		try {
            Date date = sdf.parse(dateString);
            datef = sdfNew.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return datef;
    }
}
