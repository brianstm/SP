package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import DBConnection.DBHandler;

public class CandidateSelect {
	private DBHandler handler;
    private Connection connection;
    private PreparedStatement pst;
    
    int idtmp;
    String candidatetmp;
    String candidatefullname;
    String candidateid;
    
    HashMap<Integer, String> candidateMap = new HashMap<Integer, String>();
    HashMap<String, Integer> candidateMapOpp = new HashMap<String, Integer>();

	public void candidateDB() {
		handler = new DBHandler();
		connection = handler.getConnection();
    	String get1 = "SELECT idCandidate, candidatename FROM candidates";
    	try {
			pst = connection.prepareStatement(get1);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				candidatetmp = rs.getString("candidatename");
				idtmp = rs.getInt("idCandidate");
				candidateMap.put(idtmp, candidatetmp);
				candidateMapOpp.put(candidatetmp, idtmp);
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
	
	public String setCandidate(String can){
		String[] canSplit = can.split("_");
    	int num = Integer.valueOf(canSplit[1]);
    	candidatefullname = candidateMap.get(num);
		return candidatefullname;
	}
	
	public String getCandidate(String canName){
    	candidateid = "can_" + candidateMapOpp.get(canName);
		return candidateid;
	}

}
