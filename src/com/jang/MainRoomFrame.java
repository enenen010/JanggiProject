package com.jang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class MainRoomFrame extends Frame implements ActionListener{
	JTextField tfRoomName;
	JLabel laID;
	JTable tblRoomList;
	ArrayList<String[]> dataList = new ArrayList<String[]>();
	
	String[] columnType;
	DefaultTableModel dataSource;
	
	JanggiGameFrame janggiGameFrame=null;
	
	//받아온값
	PrintWriter pw;
	String id;
	
	public MainRoomFrame(String id, PrintWriter pw) {
		this.id = id;
		this.pw = pw;
		setLayout(new BorderLayout());
		
		
		Panel topPanel = new Panel();
		topPanel.add(new JLabel("장기 게임 방 목록"));
		
		Panel eastPanel = new Panel();
		eastPanel.setLayout(new GridLayout(9,1));
		laID = new JLabel(id);
		eastPanel.add(laID);
		
		tfRoomName = new JTextField();
		Panel tfPanel=new Panel();
		tfPanel.setLayout(new GridLayout(3,1));
		tfPanel.add(new Label());
		tfPanel.add(tfRoomName);
		tfPanel.add(new Label());
		
		
		JButton btnRoomMake = new JButton("방 만들기");
		JButton btnRoomRefresh = new JButton("새로고침");
		JButton btnExit = new JButton("종료");
		btnRoomMake.addActionListener(this);
		btnRoomRefresh.addActionListener(this);
		btnExit.addActionListener(this);
		eastPanel.setPreferredSize(new Dimension(200,500));
		eastPanel.add(tfPanel);
		eastPanel.add(btnRoomMake);
		eastPanel.add(new Label());
		eastPanel.add(btnRoomRefresh);
		eastPanel.add(new Label());
		eastPanel.add(btnExit);
		
		
//		setDefaultCloseOperation(J);
		
		Panel centerpPanel = new Panel();
		columnType = new String[]{ "roomID", "No",  "방제", "인원"};
		tblRoomList = new JTable(new String[0][4],columnType);
		TableRefresh(new String[0][4]);
		
		JScrollPane scrollPane = new JScrollPane(tblRoomList);
		tblRoomList.setFillsViewportHeight(true);
		tblRoomList.setFocusable(false);
		tblRoomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//더블 클릭시 방 입장 이벤트
				if (e.getClickCount() == 2) {    
	                JTable target = (JTable)e.getSource();
	                int row = target.getSelectedRow(); 
	                
	                String roomID = (String)tblRoomList.getValueAt(row, 0);
	                String roomName = (String)tblRoomList.getValueAt(row, 2);
	                
	                pw.println("enterRoom:"+roomID+":"+roomName+":"+id);
	                pw.flush();
	             }
			}
		});

		
		
		
		centerpPanel.add(scrollPane);
		
		add(topPanel,BorderLayout.NORTH);
		add(eastPanel,BorderLayout.EAST);
		add(centerpPanel,BorderLayout.CENTER);
		setBounds(100,100,800,600);
		setVisible(true);
		
		//들어오면 방 출력
		pw.println("RefreshRoom");
		pw.flush();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn.getLabel().equals("방 만들기")) {
			String roomName = tfRoomName.getText();
			
			pw.println("MkRoom:"+id+":"+roomName);
			pw.flush();
			this.setVisible(false);
			
//			pw.println("RefreshRoom");
//			pw.flush();
			
		}else if(btn.getLabel().equals("새로고침")) {
			pw.println("RefreshRoom");
			pw.flush();
		}else if(btn.getLabel().equals("종료")) {
			pw.println("quit:"+id);
			pw.flush();
			this.dispose();
		}
	}
	
	public void TableRefresh(String[][] data) {
		
		tblRoomList.setModel(new DefaultTableModel(data,columnType){

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		});
		tblRoomList.getColumnModel().getColumn(0).setMaxWidth(0);
		tblRoomList.getColumnModel().getColumn(0).setWidth(0);
		tblRoomList.getColumnModel().getColumn(0).setMinWidth(0);
		tblRoomList.getColumnModel().getColumn(0).setMaxWidth(0);
		tblRoomList.getColumnModel().getColumn(1).setMaxWidth(80);
		tblRoomList.getColumnModel().getColumn(3).setMaxWidth(80);
		tblRoomList.revalidate();
		tblRoomList.repaint();
	}
	
	public void showHeadcountFullMessageDialog() {
		JOptionPane.showMessageDialog(null, "이미 최대 인원 입니다.");
	}
}
