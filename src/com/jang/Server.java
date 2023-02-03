package com.jang;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class Server {
	

	public static void main(String[] args) {
		ServerSocket serv = null;
		
		//전체유저 관리
		Map<String,PrintWriter> pwMap = new HashMap<String, PrintWriter>();
		Map<String,BufferedReader> brMap = new HashMap<String, BufferedReader>();
		
		//방만들기 
		Map<String,GRoom> roomMap = new HashMap<String, GRoom>();
		
//		SQLiteManager sqLiteManager = new SQLiteManager();
		MysqlWork mysqlWork = new MysqlWork();
		
		
		try {
			serv=new ServerSocket(29898);
			while(true) {
				
				Socket sock=serv.accept();//flush				
				
				Thread thr = new Thread(new Runnable() {
					
					@Override
					public void run() {
						InputStream is = null;
						OutputStream os = null;
						InputStreamReader isr=null;
						OutputStreamWriter osw=null;
						BufferedReader br = null;
						PrintWriter pw=null;
						InetAddress addr = null;
						
						try {
							addr=sock.getInetAddress();
							is=sock.getInputStream();
							os=sock.getOutputStream();
							isr=new InputStreamReader(is);
							osw=new OutputStreamWriter(os);
							br=new BufferedReader(isr);
							pw=new PrintWriter(osw);
							
							
							
							//로그인 시작
							String[] userValues=null;
							boolean isLogin=false;
							while(!isLogin){
								String str=null;
								str=br.readLine();
								
								if(str.equals("quit")) {//로그인창 닫을경우
									return;
								}
								
								userValues=str.split(":");
								System.out.println(Arrays.toString(userValues));
								long accountNumber=-1;
								String sql="SELECT COUNT(*) AS cnt  FROM Cuser WHERE id= '"+userValues[1]+"' ";
								if(userValues[0].equals("login")) {
									sql+="AND pw='"+userValues[2]+"' ";
								}
								
//								accountNumber = sqLiteManager.RowCountSelect(sql);
								accountNumber = (long)mysqlWork.executeQueryOne(sql, 1)[0];
								
								
								if(userValues[0].equals("login")) {
									if(pwMap.containsKey(userValues[1])){
										accountNumber=2;
									}
									
									pw.println(accountNumber);
									pw.flush();
									if(accountNumber==1) isLogin=true;
								}else if(userValues[0].equals("SignUp")){
									if(accountNumber==0) {
										sql="insert into Cuser(id,pw)"
											+" values('"+userValues[1]+"','"+userValues[2]+"')";
//										sqLiteManager.insert(sql);
										mysqlWork.executeUpdate(sql);
									}
									pw.println(accountNumber);
									pw.flush();
								}
							}
							
							
							
							pwMap.put(userValues[1], pw);
							brMap.put(userValues[1], br);
							System.out.println("id 수:"+pwMap.size());
							//로그인 끝
							
							
							//실행
							String msg=null;
							while((msg=br.readLine())!=null) {//flush까지 기다림
								String[] strs = msg.split(":");
								
								System.out.println(strs[0]);//1.
								
								if(strs[0].equals("chat")) {//채팅 처리
									String roomID = strs[1];
									String id=strs[2];
									String massage = strs[3];
									
									GRoom room = roomMap.get(roomID);
									
									//UserID , printWriter
									Iterator<String> ite = room.UserConnect.keySet().iterator();

									while (ite.hasNext()) {
										String key = ite.next();

										room.UserConnect.get(key).println("chat:["+id+"] "+massage);
										room.UserConnect.get(key).flush();
									}
									
									
								}
								else if(strs[0].equals("game")) {//게임 커맨드 처리
									String roomID = strs[1];
									String id=strs[2];
									String command = strs[3];
									
									GRoom room = roomMap.get(roomID);
									
									//UserID , printWriter
									Iterator<String> ite = room.UserConnect.keySet().iterator();
									
									while(ite.hasNext()) {
										String key= ite.next();
										
										if(!key.equals(id)) {
											room.UserConnect.get(key).println("game:"+strs[3]);
											room.UserConnect.get(key).flush();
										}
									}
								}
								else if(strs[0].equals("first")) {//선공 누구 처리
									String roomID = strs[1];
									String id = strs[2];
									String choice = strs[3];
									
									GRoom room = roomMap.get(roomID);
									
									//UserID , printWriter
									Iterator<String> ite = room.UserConnect.keySet().iterator();
									
									while(ite.hasNext()) {
										String key= ite.next();
										
										if(!key.equals(id)) {
											room.UserConnect.get(key).println("first:"+choice);
											room.UserConnect.get(key).flush();
										}
									}
								}
								else if(strs[0].equals("MkRoom")) {//방만들기
									
									String id=strs[1];
									String roomID=id;
									roomID+=new SimpleDateFormat("yyyyMMdd").format(new Date());
									roomID+=roomMap.size();
									
									String roomName = strs[2];
									GRoom grm = new GRoom(roomID, roomName, pw, id);
									roomMap.put(roomID, grm);
									pw.println("MkRoom:"+roomID+":"+roomName);
									pw.flush();
								}
								else if(strs[0].equals("RefreshRoom")) {//방 목록 출력
									
									Iterator ite =  roomMap.keySet().iterator();
									String data="";
									int cnt = 1;
									while(ite.hasNext()) {
										String key = (String)ite.next();
										String rid = roomMap.get(key).roomID;
										String rname = roomMap.get(key).roomName;
										int num = roomMap.get(key).UserConnect.size();
										
										data+=rid+","+cnt+","+rname+","+num+"#";
										cnt++;
									}
									if(!data.equals("")) {
										pw.println("RefreshRoom:"+data);
										pw.flush();
									}
								}
								else if(strs[0].equals("enterRoom")) {//방 입장
									String roomID=strs[1];
									String roomName=strs[2];
									String id=strs[3];
									char flag='o';
									
									int headcount = roomMap.get(roomID).UserConnect.size();
									
									if(headcount<2) {
										roomMap.get(roomID).UserConnect.put(id, pw);
										
										GRoom room = roomMap.get(roomID);
										
										//UserID , printWriter
										Iterator<String> ite = room.UserConnect.keySet().iterator();
										
										while(ite.hasNext()) {
											String key= ite.next();
											
											if(!key.equals(id)) {
												room.UserConnect.get(key).println("headcountUP");
												room.UserConnect.get(key).flush();
											}
										}
										flag='o';
									}else {
										flag='x';
									}
									
									pw.println("enterRoom:"+roomID+":"+roomName+":"+flag);
									pw.flush();
								}else if(strs[0].equals("win")) {//승리
									pw.println("win");
									pw.flush();
								}else if(strs[0].equals("roomExit")) {//나가기
									String roomID=strs[1];
									String id=strs[2];
									
									GRoom room = roomMap.get(roomID);
									
									//UserID , printWriter
									Iterator<String> ite = room.UserConnect.keySet().iterator();

									while (ite.hasNext()) {
										String key = ite.next();

										room.UserConnect.get(key).println("chat:["+id+"]님이 퇴장하셨습니다.");
										room.UserConnect.get(key).flush();
									}
									
									int headcount = roomMap.get(roomID).UserConnect.size();
									if(headcount==2) {
										roomMap.get(roomID).UserConnect.remove(id);
									}else {
										roomMap.remove(roomID);
									}
									pw.println("roomExit");
									pw.flush();
								}
								else if(strs[0].equals("quit")) {//종료
									brMap.remove(strs[1]);
									pwMap.remove(strs[1]);
									pw.println("quit");
									pw.flush();
									return;
								}
								
							}
							
							
						} catch (IOException e) {
							e.printStackTrace();
						}finally {
							try {
								if(br!=null)br.close();
								if(pw!=null)pw.close();
								if(osw!=null)osw.close();
								if(isr!=null)isr.close();
								if(os!=null)os.close();
								if(is!=null)is.close();
								if(sock!=null)sock.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
				thr.start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
		}
		
		
		
	}
}