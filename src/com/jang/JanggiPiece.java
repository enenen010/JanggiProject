package com.jang;

import javax.swing.ImageIcon;

public class JanggiPiece {
	boolean isMe;
	int piece;
	boolean clicked;
	boolean isCaptive;

	final static char RED = 'R';
	final static char GREEN = 'G';
	
	final static int SPACE = 0;
	final static int GKING = 1;
	final static int GKINGREVERSE = 2;
	final static int RKING = 3;
	final static int RKINGREVERSE = 4;
	final static int GENERAL = 5;
	final static int GENERALREVERSE = 6;
	final static int SCRIBE = 7;
	final static int SCRIBEREVERSE = 8;
	final static int SOLDIER = 9;
	final static int SOLDIERREVERSE = 10;
	final static int MARQUIS = 11;
	final static int MARQUISREVERSE = 12;

	private ImageIcon[] icon = { new ImageIcon("./Image/Space.png"), new ImageIcon("./Image/GKing.png"),
			new ImageIcon("./Image/GKing_R.png"), new ImageIcon("./Image/RKing.png"),
			new ImageIcon("./Image/RKing_R.png"), new ImageIcon("./Image/General.PNG"),
			new ImageIcon("./Image/General_R.png"), new ImageIcon("./Image/Scribe.PNG"),
			new ImageIcon("./Image/Scribe_R.PNG"), new ImageIcon("./Image/Soldier.PNG"),
			new ImageIcon("./Image/Soldier_R.png"), new ImageIcon("./Image/Marquis.PNG"),
			new ImageIcon("./Image/Marquis_R.PNG") ,
			new ImageIcon("./Image/General_C.PNG"), new ImageIcon("./Image/Scribe_C.PNG"),
			new ImageIcon("./Image/Soldier_C.PNG")};
	
	private void init() {
		this.clicked=false;
		this.isCaptive=false;
	}
	
	public JanggiPiece(char kingColor,boolean isMe){//왕
		this.piece=GKING;
		if(kingColor==RED) this.piece+=2;
		this.isMe=isMe;
		init();
	}
	public JanggiPiece(int piece, boolean isMe){//나머지
		this.piece=piece;
		this.isMe=isMe;
		init();
	}
	public JanggiPiece(){//빈칸
		this.piece=SPACE;
		this.isMe=true;
		init();
	}
	
	public ImageIcon getIcon() {
		int pieceIdx=this.piece;
		
		if(isCaptive) {
			if(this.piece==GENERAL) pieceIdx+=8;
			if(this.piece==SCRIBE) pieceIdx+=7;
			if(this.piece==SOLDIER) pieceIdx+=6;
			
			return icon[pieceIdx];
		}else {
			if(pieceIdx!=SPACE&&(!isMe))pieceIdx++;
			return icon[pieceIdx];
		}
	}
	
	//내 말이 맞는지
	public boolean getIsMe() {
		return isMe;
	}
	
	public int getPiece() {
		return this.piece;
	}
	
	public void setPiece(int piece) {
		this.piece=piece;
	}
	
	public void setIsMe(boolean isMe) {
		this.isMe=isMe;
	}
	
	//이동 값 구하기
	public int[] getMoved(int x,int y){//after-before 값 입력
		int[] temp= { x , y };
		if(!isMe) {
			temp[0]*=-1;
			temp[1]*=-1;
			return temp;
		}
		return temp;
	}
	
	//이동 가능 확인
	public boolean MovePossible(int x,int y){//after-before 값 입력
		switch (piece) {
		case GKING:
			return true;
		case RKING:
			return true;
		case GENERAL:
			if(Math.abs(x - y)==1)return true;
			return false;
		case SCRIBE:
			if(Math.abs(x - y)==1)return false;
			return true;
		case SOLDIER:
			if(x==0&&y==-1)return true;
			return false;
		case MARQUIS:
			if((x==-1&&y==1)||(x==1&&y==1))return false;
			return true;
		default:
			return false;
		}
	}
	
	public String getName() {
		switch (piece) {
		case SPACE:
			return "SPACE";
		case GKING:
			return "王";
		case RKING:
			return "王";
		case GENERAL:
			return "將";
		case SCRIBE:
			return "相";
		case SOLDIER:
			return "子";
		case MARQUIS:
			return "侯";
		default:
			return null;
		}
	}
	

}
