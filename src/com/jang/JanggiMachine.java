package com.jang;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.util.*;

public class JanggiMachine implements ActionListener {
	
	//맵 구성
	private JanggiPiece[][] janggiMap;
	private JButton[][] janggiBtns;
	
	//포로 세트
	private ArrayList<JanggiPiece> myCaptiveList;
	private ArrayList<JanggiPiece> youCaptiveList;
	private ArrayList<JButton> myCaptiveBtns;
	private ArrayList<JButton> youCaptiveBtns;
	
	//이전값 저장 세트
	private JButton beforeButton = null;
	private int[] beforeIdx = null;
	private JanggiPiece beforePiece = null;
	
	//Client 받아온 값
	private Panel gamePanel = null;
	private Panel myCaptiveZone = null;
	private Panel youCaptiveZone = null;
	private PrintWriter pw = null;
	String id=null;
	String roomID=null;
	
	//자신의 턴인지
	boolean myTurn;
	int choice;
	boolean myAggression;//침략
	boolean youAggression;//침략
	
	//전달 커맨드
	String command="";
	ArrayList<String> commands;
	String commadnStack="";
	
	
	public void init() {
		JanggiPiece[][] janggiMap = {
				{  new JanggiPiece(JanggiPiece.GENERAL, false)
					, new JanggiPiece(JanggiPiece.GREEN, false)
					, new JanggiPiece(JanggiPiece.SCRIBE, false) },
				{  new JanggiPiece()
					, new JanggiPiece(JanggiPiece.SOLDIER, false)
					, new JanggiPiece()},
				{  new JanggiPiece()
					, new JanggiPiece(JanggiPiece.SOLDIER, true)
					, new JanggiPiece()},
				{  new JanggiPiece(JanggiPiece.SCRIBE, true)
					, new JanggiPiece(JanggiPiece.RED, true)
					, new JanggiPiece(JanggiPiece.GENERAL, true) }
				};
				this.janggiMap = janggiMap;
				janggiBtns = new JButton[4][3];
				this.myCaptiveList=new ArrayList<JanggiPiece>();
				this.youCaptiveList=new ArrayList<JanggiPiece>();
				this.myCaptiveBtns=new ArrayList<JButton>();
				this.youCaptiveBtns=new ArrayList<JButton>();
				command="";
				commands = new ArrayList<String>();
				commadnStack="";
				myAggression=false;
				youAggression=false;
				reDraw();
	}
	
	public void ButtonEnabled(boolean enFlag) {
		for (int i = 0; i < janggiMap.length; i++) {
			for (int j = 0; j < janggiMap[i].length; j++) {
				janggiBtns[i][j].setEnabled(enFlag);
			}
		}
		
		for (int i = 0; i < myCaptiveList.size(); i++) {
			myCaptiveBtns.get(i).setEnabled(enFlag);
		}
		
		for (int i = 0; i < youCaptiveList.size(); i++) {
			youCaptiveBtns.get(i).setEnabled(enFlag);
		}
	}
	
	public int FirstPrayerChoice(int choice) {
		this.choice=choice;
		int youResult;
		if(choice==1) {
			myTurn=true;
			janggiMap[0][1]=new JanggiPiece(JanggiPiece.RED, false);
			janggiMap[3][1]=new JanggiPiece(JanggiPiece.GREEN, true);
			youResult=0;
		}else {
			myTurn=false;
			janggiMap[0][1]=new JanggiPiece(JanggiPiece.GREEN, false);
			janggiMap[3][1]=new JanggiPiece(JanggiPiece.RED, true);
			youResult=1;
		}
		return youResult;
	}
	

	
	public void reDraw() {
		gamePanel.removeAll();
		myCaptiveZone.removeAll();
		youCaptiveZone.removeAll();
		
		JButtonAdd();
		gamePanel.repaint();
		gamePanel.revalidate();
		myCaptiveZone.repaint();
		youCaptiveZone.repaint();
	}
	
	public JanggiMachine(String roomID,String id, PrintWriter pw, Panel gamePanel, Panel myCaptiveZone, Panel youCaptiveZone) {
			this.roomID = roomID;
			this.id = id;
			this.pw = pw;
			this.gamePanel = gamePanel;
			this.myCaptiveZone=myCaptiveZone;
			this.youCaptiveZone=youCaptiveZone;
			
			init();
		}
	
	public void JButtonAdd() {
		for (int i = 0; i < janggiMap.length; i++) {
			for (int j = 0; j < janggiMap[i].length; j++) {
				janggiBtns[i][j] = new JButton();
				janggiBtns[i][j].addActionListener(this);
				janggiBtns[i][j].setIcon(janggiMap[i][j].getIcon());
				janggiBtns[i][j].setBackground(BackgroundColor(i));
				janggiBtns[i][j].setName("NOMAL");
				gamePanel.add(janggiBtns[i][j]);
			}
		}
		
		myCaptiveBtns.clear();
		for (int i = 0; i < myCaptiveList.size(); i++) {
			myCaptiveBtns.add(new JButton());
			myCaptiveBtns.get(i).addActionListener(this);
			myCaptiveBtns.get(i).setIcon(myCaptiveList.get(i).getIcon());
			myCaptiveBtns.get(i).setBackground(new Color(255, 255, 255));
			myCaptiveBtns.get(i).setName("Captive");
			
			myCaptiveBtns.get(i).setPreferredSize(new Dimension(50, 50));
			myCaptiveZone.add(myCaptiveBtns.get(i));
		}
		
		youCaptiveBtns.clear();
		for (int i = 0; i < youCaptiveList.size(); i++) {
			youCaptiveBtns.add(new JButton());
			youCaptiveBtns.get(i).addActionListener(this);
			youCaptiveBtns.get(i).setIcon(youCaptiveList.get(i).getIcon());
			youCaptiveBtns.get(i).setBackground(new Color(255, 255, 255));
			youCaptiveBtns.get(i).setName("Captive");
			
			youCaptiveBtns.get(i).setPreferredSize(new Dimension(50, 50));
			youCaptiveZone.add(youCaptiveBtns.get(i));
		}
		
	}
	
	public Color BackgroundColor(int y) {
		if(this.choice==0) {
			if(y==0) {
				return new Color(185,255,185);//녹색
			}else if(y==3) {
				return new Color(255,208,208);//빨강
			}else {
				return new Color(255,255,255);
			}
		}else {
			if(y==0) {
				return new Color(255,208,208);//빨강
			}else if(y==3) {
				return new Color(185,255,185);//녹색
			}else {
				return new Color(255,255,255);
			}
		}
	}

	public void editBtns(boolean boo) {
		for (int i = 0; i < janggiMap.length; i++) {
			for (int j = 0; j < janggiMap[i].length; j++) {
				janggiBtns[i][j].setEnabled(boo);
			}
		}
	}

	public int[] getButtonIdx(JButton btn) {
		for (int i = 0; i < janggiMap.length; i++) {
			for (int j = 0; j < janggiMap[i].length; j++) {
				if (btn.equals(janggiBtns[i][j])) {
					int[] temp = { i, j };
					return temp;
				}
			}
		}
		return null;
	}
	
	public int[] getListButtonIdx(JButton btn,ArrayList<JButton> list) {
		for (int i = 0; i < list.size(); i++) {
			if (btn.equals(list.get(i))) {
				int[] temp = { i };
				return temp;
			}
		}
		return null;
	}
	
	public int ReverseX(int x) {
		switch(x) {
		case 0:return 2;
		case 2:return 0;
		default: return x;
		}
	}
	public int ReverseY(int y) {
		switch(y) {
		case 0:return 3;
		case 1:return 2;
		case 2:return 1;
		case 3:return 0;
		default: return y;
		}
	}
	
	public String numberByY(int y) {
		switch(y) {
		case 0:return "A";
		case 1:return "B";
		case 2:return "C";
		case 3:return "D";
		default: return "-";
		}
	}
	public int yByNumber(String num) {
		switch(num) {
		case "A":return 0;
		case "B":return 1;
		case "C":return 2;
		case "D":return 3;
		default: return -1;
		}
	}

	public boolean Action(int[] beforeIdx, int[] afterIdx) {
		boolean useCommend=false;
		JanggiPiece before = janggiMap[beforeIdx[0]][beforeIdx[1]];
		JanggiPiece after = janggiMap[afterIdx[0]][afterIdx[1]];
		int y = afterIdx[0] - beforeIdx[0];
		int x = afterIdx[1] - beforeIdx[1];

		// 내말 위치로는 이동 불가
		if (after.getIsMe() && !after.getName().equals("SPACE")) {
			JOptionPane.showMessageDialog(null, "아군의 말 위로는 이동할 수 없습니다.");
		}
		// 2칸이상 무리
		else if (Math.abs(beforeIdx[0] - afterIdx[0]) > 1 || Math.abs(beforeIdx[1] - afterIdx[1]) > 1) {
			JOptionPane.showMessageDialog(null, "2칸 이상 이동할 수 없습니다.");
		}
		// 이말로 갈수없는 칸
		else if (!before.MovePossible(x, y)) {
			JOptionPane.showMessageDialog(null, "["+beforePiece.getName() + "]으로 이동할 수 없는 위치 입니다");
		}
		// 이동가능!!
		else {
			// 상대말이면
			if (!after.getName().equals("SPACE")) {
				
				if (after.getPiece()==JanggiPiece.MARQUIS) 
					after.setPiece(JanggiPiece.SOLDIER);
				after.setIsMe(true);
				after.isCaptive=true;
				myCaptiveList.add(after);
				
			}
			
			//子가 마지막 칸에 도달하면
			if (before.getPiece()==JanggiPiece.SOLDIER && afterIdx[0]==0) {
				before.setPiece(JanggiPiece.MARQUIS);
			}
			
			
			//전진 후 뒤에 공백 삽입
			janggiMap[afterIdx[0]][afterIdx[1]] = before;
			janggiMap[beforeIdx[0]][beforeIdx[1]] = new JanggiPiece();
			
			useCommend=true;
		}

		
		return useCommend;
	}
	
	public void OpponentAction(String command) {
		String[] commands = command.split("");
		System.out.println(Arrays.toString(commands));
		System.out.println(commands[2]);
		
		int beforeY=ReverseY(yByNumber(commands[0]));
		int beforeX=ReverseX(Integer.parseInt(commands[1]));
		int afterY=ReverseY(yByNumber(commands[3]));
		int afterX=ReverseX(Integer.parseInt(commands[4]));
		
		System.out.println(beforeY+","+beforeX+","+afterY+","+afterX);
		
		JanggiPiece temp=null;
		if(beforeY != -1) {//포로 놓기가 아니면
			//子가 마지막 칸에 도달하면
			if (janggiMap[beforeY][beforeX].getPiece()==JanggiPiece.SOLDIER && afterY==3) {
				janggiMap[beforeY][beforeX].setPiece(JanggiPiece.MARQUIS);
			}
			
			//이동
			temp = janggiMap[afterY][afterX];//잡혀서 이제 포로된애
			janggiMap[afterY][afterX] = janggiMap[beforeY][beforeX];
			janggiMap[beforeY][beforeX] = new JanggiPiece();
			
			//MARQUIS가 포로가되면 SOLDIER로 취급한다
			if (temp.getPiece()==JanggiPiece.MARQUIS) 
				temp.setPiece(JanggiPiece.SOLDIER);
			
			//포로 세팅 및 공백이 아니면 잡음
			temp.setIsMe(false);
			temp.isCaptive=true;
			if(!temp.getName().equals("SPACE")) youCaptiveList.add(temp);
			
			if(commands[6].equals("W")){//상대가 침략함
				youAggression=true;
				reDraw();
				JOptionPane.showMessageDialog(null,"침략당함!");
			}
			
			//여기에 이기면 해야될 동작 정의
			if(command.charAt(5)=='王') {
				reDraw();
				JOptionPane.showMessageDialog(null, "상대가 당신의 王을 잡아 당신의 패배입니다..");
				ButtonEnabled(false);
				pw.println("gameEnd");
				pw.flush();
			}else if(myAggression){//침략을 성공했다.
				reDraw();
				JOptionPane.showMessageDialog(null, "침략이 성공했습니다!! 당신의 승리입니다!!");
				ButtonEnabled(false);
				pw.println("win");
				pw.flush();
				pw.println("gameEnd");
				pw.flush();
			}
		}
		else {//포로 놓기 이면
			beforeX=Integer.parseInt(commands[1]);
			janggiMap[afterY][afterX] = youCaptiveList.get(beforeX);
			janggiMap[afterY][afterX].isCaptive=false;
			youCaptiveList.remove(beforeX);
		}
		myTurn=true;
		this.commadnStack+=numberByY(beforeY)+beforeX+commands[2]
				         +numberByY(afterY)+afterX+commands[5]+commands[6]+"/";
		System.out.println(commadnStack);
		reDraw();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(!myTurn) {
			JOptionPane.showMessageDialog(null, "내 턴이 아닙니다.");
			return;
		}
		
		boolean useCommend=false;
		JButton pieceBtn=(JButton)e.getSource();
		
		int[] idx=null;
		JanggiPiece pieceObj=null;
		
		//포로면 포로쪽에서 아니면 보드쪽에서 말 객체를 가져옴
		if(pieceBtn.getName().equals("Captive")) {
			idx=getListButtonIdx(pieceBtn,myCaptiveBtns);
			pieceObj=myCaptiveList.get(idx[0]);
		}else {
			idx=getButtonIdx(pieceBtn);
			pieceObj=janggiMap[idx[0]][idx[1]];
		}
		
		//가져온 말로 Action 수행
		if(beforeButton!=null) {
			//포로 아닐때
			if(!beforePiece.isCaptive) { 
				if(!pieceObj.clicked) {//내 버튼 다시 클릭
					useCommend=Action(beforeIdx,idx);
				}
			//포로일때
			} else {
				if(!pieceObj.getName().equals("SPACE")) {
					JOptionPane.showMessageDialog(null, "포로는 공백에만 배치할 수 있습니다.");
				}
				else if(idx[0]==0) {
					JOptionPane.showMessageDialog(null, "포로는 상대방 진영 외 9칸에만 배치 가능합니다.");
				}
				else {//포로 배치
					beforePiece.isCaptive=false;
					janggiMap[idx[0]][idx[1]]=beforePiece;
					myCaptiveList.remove(beforePiece);
					useCommend=true;
					
				}
			}
			
			//정상적인 명령만 서버에 전송가능!
			if(useCommend) {
				command+=numberByY(idx[0])+idx[1];
				//침략 승리 조건
				char aggressionFlag = '-';
				System.out.println("command.charAt(2)="+command.charAt(2) +"command.charAt(3):"+command.charAt(3) );
				if(command.charAt(2)=='王' && command.charAt(3)=='A') {
					aggressionFlag = 'W';
					myAggression = true;
					reDraw();
					JOptionPane.showMessageDialog(null,"침략!");
				}
				
				//서버에 정상 메시지 전송
				String name=pieceObj.getName();
				if(name.equals("SPACE"))name="x";
				command+=name+aggressionFlag;
				pw.println("game:"+roomID+":"+id+":"+command);
				pw.flush();
				myTurn=false;
				this.commadnStack+=command+"/";
				//전송 끝
				
				//여기에 이기면 해야될 동작 정의
				if(command.charAt(5)=='王') {
					//사용한 값 초기화
					command="";
					pieceObj.clicked=false;
					beforeButton=null;
					beforeIdx=null;
					beforePiece=null;
					
					reDraw();
					
					JOptionPane.showMessageDialog(null, "당신이 상대의 王을 잡아 당신의 승리입니다!!");
					ButtonEnabled(false);
					pw.println("win");
					pw.flush();
					pw.println("gameEnd");
					pw.flush();
				}else if(youAggression){//침략을 막지 못했다....
					reDraw();
					JOptionPane.showMessageDialog(null, "침략을 막지 못했습니다.. 당신의 패배입니다..");
					ButtonEnabled(false);
					pw.println("gameEnd");
					pw.flush();
				}
			}
			
			//사용한 값 초기화
			command="";
			pieceObj.clicked=false;
			beforeButton=null;
			beforeIdx=null;
			beforePiece=null;
			
			reDraw();
			
		}else {//첫클릭
			if(pieceObj.getName().equals("SPACE")) {
				JOptionPane.showMessageDialog(null, "공백은 이동할 수 없습니다");
				return;
			}else if(!pieceObj.getIsMe()) {
				JOptionPane.showMessageDialog(null, "아군편 말만 이동할 수 있습니다.");
				return;
			}
			pieceBtn.setBackground(new Color(255,255,0));
			pieceObj.clicked=true;
			beforeButton=pieceBtn;
			beforeIdx=idx;
			beforePiece=pieceObj;
//			command="A2자B2x";
			
			if(pieceObj.isCaptive) {
				command+="-"+myCaptiveList.indexOf(pieceObj)+pieceObj.getName();
			}else {
				command+=numberByY(idx[0])+idx[1]+pieceObj.getName();
			}
		}
		
		
	}

	


}
