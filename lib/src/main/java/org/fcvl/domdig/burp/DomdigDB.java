package org.fcvl.domdig.burp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DomdigDB {
	Connection connection = null;
	String dbFileName;

	public DomdigDB(String dbFileName){
		this.dbFileName = dbFileName;
	}
	
	private void connect() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public String getStatus() {
		connect();
		try{
			Statement statement = connection.createStatement();
			// statement.setQueryTimeout(30);

			ResultSet rs = statement.executeQuery("select * from scan_info order by id desc limit 1");
			rs.next();
			return rs.getString("status");
		}
		catch(SQLException e){
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		finally{
			close();
		}
		return null;
	}
	
	public ArrayList<DomdigRequest> getRequests(int lastID){
		connect();
		ArrayList<DomdigRequest> reqList = new ArrayList<>();
		try{
			PreparedStatement statement = connection.prepareStatement("select * from request where id > ? order by id asc");
			statement.setInt(1, lastID);
			ResultSet rs = statement.executeQuery();
			
			 while(rs.next()){
				 reqList.add(new DomdigRequest(rs.getInt("id"), rs.getString("type"), rs.getString("method"), rs.getString("url"), rs.getString("headers"), rs.getString("data"), rs.getString("trigger")));
			 }
			 return reqList;
		} catch(SQLException e){
			System.err.println(e.getMessage());
		}
		finally{
			close();
		}
		return null;		
	}

	public ArrayList<DomdigVulnerability> getVulnerabilities(int lastID){
		connect();
		ArrayList<DomdigVulnerability> vulnList = new ArrayList<>();
		try{
			PreparedStatement statement = connection.prepareStatement("select * from vulnerability where id > ? order by id asc");
			statement.setInt(1, lastID);
			ResultSet rs = statement.executeQuery();
			
			 while(rs.next()){
				 vulnList.add(new DomdigVulnerability(rs.getInt("id"), rs.getString("type"), rs.getString("url"), rs.getString("description"), rs.getString("element"), rs.getString("payload"), rs.getBoolean("confirmed")));
			 }
			 return vulnList;
		} catch(SQLException e){
			System.err.println(e.getMessage());
		}
		finally{
			close();
		}
		return null;		
	}
	
	private void close() {
		try {
			if(connection != null) {
				connection.close();
				connection = null;
			}
		} catch(SQLException e){
			// connection close failed.
			System.err.println(e.getMessage());
		}
	}
}
