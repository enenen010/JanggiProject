package com.jang;
import java.io.PrintWriter;
import java.util.*;

public class GRoom {
	String roomID;
	String roomName;
	
	//UserID , printWriter
	Map<String, PrintWriter> UserConnect;
	
	public GRoom(String roomID,String roomName,PrintWriter pw,String id) {
		this.roomID = roomID;
		this.roomName = roomName;
		UserConnect = new HashMap<String, PrintWriter>();
		UserConnect.put(id, pw);
	}
}
