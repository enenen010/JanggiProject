package com.jang;
import java.sql.*;
import java.util.*;

public class SQLiteManager {
	String fileName = "db/sibiJanggi.db";
	String url = "jdbc:sqlite:" + fileName;
	Connection conn = null;
	Statement stmt = null;
	
	private Connection connect() {
        // SQLite connection string
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	public void createNewDatabase(String fileName) {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public void createNewTable(String sql) {
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public ResultSet select(String sql){   
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            ResultSet temp = rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
	
	public int RowCountSelect(String sql){   
		int cnt=-1;
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            ResultSet temp = rs;
            if(rs.next()) {
            	cnt=rs.getInt("cnt");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cnt;
    }

	public void insert(String sql) {

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public void delete(String sql) {
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
