package com.jang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

class MysqlWork {
	Map<String, String> env = System.getenv();

	String url = "jdbc:mysql://localhost:3306/12kchess";
	
	Properties info = null;
	
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	long rowCount;
	
	public MysqlWork() {
		info = new Properties();
		info.setProperty("user", env.get("MYSQL_USER"));
		info.setProperty("password", env.get("MYSQL_PW"));
	}
	
	//다수의 값을 List로 반환받는 메서드 
	public List<Object[]> executeQuery(String sql, int colSize) {
		List<Object[]> list = new ArrayList<Object[]>();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, info);
			stmt = conn.createStatement();
			rs =  stmt.executeQuery(sql);
			
			while(rs.next()) {
				Object[] temp = new Object[colSize];
				for (int i = 0; i < colSize; i++) {
					temp[i] = rs.getObject(i+1);
				}
				list.add(temp);
			}
			rowCount=list.size();
		} catch (SQLException e) {
			System.out.println(e.toString()+"문법 오류 또는 실행오류");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 확인");
			System.out.println("드라이버 등록이 잘못되었습니다.");
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	//하나의 값을 List로 반환받는 메서드 
	public Object[] executeQueryOne(String sql, int colSize) {
		Object[] temp = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, info);
			stmt = conn.createStatement();
			rs =  stmt.executeQuery(sql);
			
			if(rs.next()) {
				rowCount=1;
				temp = new Object[colSize];
				for (int i = 0; i < colSize; i++) {
					temp[i] = rs.getObject(i+1);
				}
			}
			if(temp==null) rowCount=0;
		} catch (SQLException e) {
			System.out.println("문법 오류 또는 실행오류");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 확인");
			System.out.println("드라이버 등록이 잘못되었습니다.");
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}
	
	//입력, 수정, 삭제 등에 사용하는 메서드
	public void executeUpdate(String sql) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, info);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			
		} catch (SQLException e) {
			System.out.println("문법 오류 또는 실행오류");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 확인");
			System.out.println("드라이버 등록이 잘못되었습니다.");
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
