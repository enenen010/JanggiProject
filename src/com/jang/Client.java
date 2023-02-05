package com.jang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;

public class Client extends Frame{
	JanggiGameFrame janggiGameFrame=null;
	MainRoomFrame mainRoomFrame=null;
	String id=null;
	static boolean quit=false;
	
	public Client() {
//		String url="192.168.240.114";
		String url="127.0.0.1";
		
		int port=29898;
		Socket sock=null;
		InputStream is=null;
		OutputStream os=null;
		InputStreamReader isr=null;
		OutputStreamWriter osw=null;
		BufferedReader br=null;
		PrintWriter pw=null;
		
		try {
			
			sock=new Socket(url,port);//생성을위해 url:port의 프로세스를 TCP방식으로 소통
			is=sock.getInputStream();
			os=sock.getOutputStream();
			isr=new InputStreamReader(is);
			osw=new OutputStreamWriter(os);
			br=new BufferedReader(isr);
			pw=new PrintWriter(osw);
			
			IndexDialog indexDialog = new IndexDialog(this,"로그인",true,pw,br);
			if(!indexDialog.loginSuccess) {
				pw.println("quit");
				quit=true;
				return;
			}
			this.id = indexDialog.id;
			mainRoomFrame=new MainRoomFrame(id, pw);
//			janggiGameFrame =new JanggiGameFrame(pw, id);
			
			String msg=null;
			while(true) {
				msg=br.readLine();
				
				String[] strs = msg.split(":");
//				if(msg.equals("exit"))break;
//				janggiGameFrame.ta.append(msg+"\n");
				
				if(strs[0].equals("chat")) {
					String massage = strs[1];
					janggiGameFrame.ta.append(massage+"\n");
				}
				else if(strs[0].equals("game")) {
					String command = strs[1];
					janggiGameFrame.janggiMachine.OpponentAction(command);
				}else if(strs[0].equals("first")) {
					int choice = Integer.parseInt(strs[1]);
					janggiGameFrame.CreateJanggiMachine();
					janggiGameFrame.janggiMachine.FirstPrayerChoice(choice);
					janggiGameFrame.janggiMachine.reDraw();
				}else if(strs[0].equals("MkRoom")) {
					String roomID = strs[1];
					String roomName = strs[2];
					janggiGameFrame =new JanggiGameFrame(pw, id, roomID, roomName, 1);
					
					pw.println("chat:"+roomID+":"+id+":"+"님의 방이 생성되었습니다.");
					pw.flush();
					
				}else if(strs[0].equals("RefreshRoom")) {
					String[] rows = strs[1].split("#");
					String[][] data = new String[rows.length][0];
					System.out.println("RefreshRoom2");
					for (int i = 0; i < rows.length; i++) {
						data[i] = rows[i].split(",");
						System.out.println(Arrays.toString(data[i]));
					}
					
					mainRoomFrame.TableRefresh(data);
					
				}else if(strs[0].equals("enterRoom")) {
					String roomID = strs[1];
					String roomName = strs[2];
					String flag = strs[3];
					
					if(flag.equals("x")) {
						mainRoomFrame.showHeadcountFullMessageDialog();
					}else {
						janggiGameFrame =new JanggiGameFrame(pw, id, roomID, roomName, 2);
						System.out.println("손님"+id+":"+janggiGameFrame.headcount);
						mainRoomFrame.setVisible(false);
						janggiGameFrame.startBtn.setEnabled(false);
						
						pw.println("chat:"+roomID+":"+id+":"+"님이 입장하였습니다.");
						pw.flush();
					}
					
				}else if(strs[0].equals("headcountUP")) {
					janggiGameFrame.headcount++;
					System.out.println("방장"+id+":"+janggiGameFrame.headcount);
				}
				else if(strs[0].equals("win")) {
					janggiGameFrame.startBtn.setEnabled(true);
				}
				else if(strs[0].equals("roomExit")) {
					janggiGameFrame.dispose();
					mainRoomFrame.setVisible(true);
				}
				else if(strs[0].equals("gameEnd")) {
					janggiGameFrame.notationBtn.setEnabled(true);
				}
				else if(strs[0].equals("quit")) {
					quit = true;
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
				if(is!=null)is.close();
				if(os!=null)os.close();
				if(sock!=null)sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Client me =  new Client();
		me.setVisible(false);
		while(true) {
			if(quit) {
				me.dispose();
				break;
			}
		}
	}
}
