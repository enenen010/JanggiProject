package com.jang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

public class IndexDialog extends Dialog implements ActionListener{
	PrintWriter pw=null;
	BufferedReader br=null;
	TextField tfID;
	TextField tfPassword;
	String id;
	String password;
	boolean loginSuccess=false;
	
	public IndexDialog(Frame owner, String title, boolean modal, PrintWriter pw, BufferedReader br) {
		super(owner, title, modal);
		this.pw = pw;
		this.br = br;

		setLayout(new BorderLayout());

		Panel bannerPanel = new Panel();
		JLabel mainBanner = new JLabel();
		bannerPanel.add(mainBanner);

		mainBanner.setIcon(new ImageIcon("./Image/MainBanner.jpg"));
		add(bannerPanel, BorderLayout.CENTER);

		Panel loginPanel = new Panel();
		loginPanel.setLayout(new GridLayout(5, 1));
		this.tfID = new TextField();
		this.tfPassword = new TextField();

		Panel idPanel = new Panel();
		Panel passwordPanel = new Panel();
		idPanel.setLayout(new BorderLayout());
		passwordPanel.setLayout(new BorderLayout());
		idPanel.setPreferredSize(new Dimension(100, 10));
		idPanel.add(new Label("ID:  "), BorderLayout.WEST);
		idPanel.add(tfID, BorderLayout.CENTER);
		passwordPanel.add(new Label("PW:"), BorderLayout.WEST);
		passwordPanel.add(tfPassword);

		loginPanel.add(idPanel);
		loginPanel.add(passwordPanel);
		loginPanel.add(new Label());

		Panel btnPanel = new Panel();
		JButton btnLogin = new JButton("로그인");
		JButton btnCreateUser = new JButton("회원가입");
		JButton btnQuit = new JButton("종료");
		btnLogin.addActionListener(this);
		btnCreateUser.addActionListener(this);
		btnQuit.addActionListener(this);
		btnPanel.add(btnLogin);
		btnPanel.add(btnCreateUser);
		btnPanel.add(btnQuit);

		loginPanel.add(btnPanel);

		add(loginPanel, BorderLayout.SOUTH);
		
		
		setBounds(100, 100, 400, 500);
		setVisible(true);
	}

	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		
		if(tfID.getText()=="" || tfPassword.getText()=="") {
			JOptionPane.showMessageDialog(null, "ID 혹은 PASSWORD를 입력하지 않았습니다.");
		}
		
		this.id = tfID.getText();
		this.password = tfPassword.getText();
		
		
		
		if(btn.getLabel().equals("로그인")) {
			pw.println("login:"+this.id+":"+this.password);
			pw.flush();
			loginAction();
		}else if(btn.getLabel().equals("회원가입")) {
			pw.println("SignUp:"+this.id+":"+this.password);
			pw.flush();
			SignUpAction();
		}else if(btn.getLabel().equals("종료")) {
			pw.println("quit");
			pw.flush();
			this.dispose();
		}
		
	}
	
	public boolean SignUpAction() {
		 int cnt=3;
		try {
			cnt = Integer.parseInt(br.readLine());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(cnt==0) {
			JOptionPane.showMessageDialog(null, "환영합니다 가입에 성공하셨습니다.");
		}else if(cnt==1) {
			JOptionPane.showMessageDialog(null, "이미 존재하는 ID 입니다");
		}else {
			System.out.println(cnt);
			JOptionPane.showMessageDialog(null, "무언가 잘못되었다");
		}
		return true;
	}
	
	
	public void loginAction() {
		 int cnt=3;
		try {
			cnt = Integer.parseInt(br.readLine());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(cnt==0) {
			JOptionPane.showMessageDialog(null, "ID혹은 패스워드가 잘못되었거나 없습니다.");
		}else if(cnt==1) {
			loginSuccess=true;
			this.dispose();
		}else if(cnt==2){
			JOptionPane.showMessageDialog(null, "이미 접속되어있는 사용자 입니다.");
		}else{
			System.out.println(cnt);
			JOptionPane.showMessageDialog(null, "무언가 잘못되었다");
		}
	}
}
