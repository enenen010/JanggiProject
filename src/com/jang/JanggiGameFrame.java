package com.jang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;

public class JanggiGameFrame extends Frame implements ActionListener{
	
	JTextField tf;
	JTextArea ta;
	JButton startBtn;
	JButton surrenderBtn;
	JButton exitBtn;
	JButton notationBtn;
	
	PrintWriter pw=null;
	GridBagLayout gbl;
	Panel gamePanel;
	JanggiMachine janggiMachine;
	Panel myCaptiveZone;
	Panel youCaptiveZone;
	
	//방 제어 관련
	String id;
	String youID;
	String roomID;
	String roomName;
	int headcount=0;
	
	public void gbinsert(Component c, int x, int y, int w, int h,int weight){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.weightx=weight;
        gbc.weighty=weight;
        gbl.setConstraints(c,gbc);
        this.add(c);
    }
	
	public JanggiGameFrame(PrintWriter pw, String id, String roomID, String roomName, int headcount) {
		this.pw=pw;
		this.id=id;
		this.youID=youID;
		this.roomID=roomID;
		this.roomName=roomName;
		this.headcount=headcount;
		
		setTitle(roomName);
		
		Panel boardPanel=new Panel();
		boardPanel.setLayout(new BorderLayout());
		gamePanel=new Panel();
		gamePanel.setLayout(new GridLayout(4,3));
		boardPanel.add(gamePanel,BorderLayout.CENTER);
		
		myCaptiveZone=new Panel();
		youCaptiveZone=new Panel();
		gamePanel.setPreferredSize(new Dimension(350, 350));
		myCaptiveZone.setPreferredSize(new Dimension(350, 55));
		youCaptiveZone.setPreferredSize(new Dimension(350, 55));
		
		boardPanel.add(myCaptiveZone,BorderLayout.SOUTH);
		boardPanel.add(youCaptiveZone,BorderLayout.NORTH);
		
		Panel msgPanel=new Panel();
		tf = new JTextField();
		ta = new JTextArea();
		tf.addActionListener(this);
		ta.setLineWrap(true);
		ta.setColumns(1);
		msgPanel.setLayout(new BorderLayout());
		msgPanel.add(new JScrollPane(ta),BorderLayout.CENTER);
		msgPanel.add(tf,BorderLayout.SOUTH);
		
		Panel menuPanel = new Panel();
		startBtn = new JButton("게임시작");
		startBtn.addActionListener(this);
		
		surrenderBtn = new JButton("항복");
		surrenderBtn.addActionListener(this);
		
		exitBtn = new JButton("나가기");
		exitBtn.addActionListener(this);
		
		notationBtn = new JButton("기보저장");
		notationBtn.addActionListener(this);
		notationBtn.setEnabled(false);
		
		menuPanel.add(startBtn);
		menuPanel.add(surrenderBtn);
		menuPanel.add(exitBtn);
		menuPanel.add(notationBtn);
		msgPanel.add(menuPanel,BorderLayout.NORTH);
		
		gbl=new GridBagLayout();
		setLayout(gbl);
		gbinsert(new Label(""),0,0,1,1,0);
		gbinsert(new Label(""),1,0,1,1,0);
		gbinsert(new Label(""),2,0,1,1,0);
		
		msgPanel.setSize(100, 700);
		gbinsert(boardPanel,0,1,2,1,1);
		gbinsert(msgPanel,2,1,1,1,1);
		
		
		CreateJanggiMachine();
		janggiMachine.ButtonEnabled(false);
		
		
		setBounds(100,100,800,800);
		setVisible(true);
	}
	
	public void CreateJanggiMachine() {
		this.janggiMachine = new JanggiMachine( roomID, id, pw, gamePanel, myCaptiveZone, youCaptiveZone);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource().equals((Object)tf)) {
			// 텍스트필드의 입력이벤트
			String msg="";
			msg+=tf.getText();
			if(!msg.equals("")) {
				pw.println("chat:"+roomID+":"+id+":"+msg);
				pw.flush();
			}
			tf.setText("");
			tf.setText("");
			
		}else if(e.getSource().equals((Object)startBtn)) {
			
			if(this.headcount!=2) {
				JOptionPane.showMessageDialog(null, "2명으로만 게임을 시작할 수 있습니다");
				return;
			}
			CreateJanggiMachine();
			
			int choice = new Random().nextInt(2);
			choice = janggiMachine.FirstPrayerChoice(choice);
			janggiMachine.reDraw();
			
			pw.println("first:"+roomID+":"+id+":"+choice);
			pw.flush();
			startBtn.setEnabled(false);
			notationBtn.setEnabled(false);
			
		}else if(e.getSource().equals((Object)exitBtn)) {
			pw.println("roomExit:"+roomID+":"+id);
			pw.flush();
			System.out.println("나가기 실행");
		}
		else if(e.getSource().equals((Object)notationBtn)) {
			String title = JOptionPane.showInputDialog("기보 제목을 입력하세요:");
			if(title==null) {
				return;
			}
			pw.println("saveNotation:"+roomID+":"+id+":"+title+":"+janggiMachine.commadnStack);
			pw.flush();
			notationBtn.setEnabled(false);
		}
		
		
	}
	
}
