package client.ft;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import main.RemoteDesktopGUI;

public class FTGUI extends JFrame implements ActionListener, MouseListener, ItemListener, KeyListener {
	
	private static final long serialVersionUID = 1L;
	
	private String[] sizeUnit = {"Bytes", "KB", "MB", "GB", "TB", "PB"};
	private JTextArea log;
	private JComboBox<String> cPath, sPath;
	private JTable cTable, sTable;
	private JButton c_refresh, c_delete, c_add, c_up, c_root, send;
	private JButton s_refresh, s_delete, s_add, s_up, s_root, recv;
	
	private String c_path = "", s_path = "";
	private boolean typing = false;
	public long totalSize = 0, transfer = 0;
	
	private JDialog progressDialog;
	private JLabel progressLabel;
	private JProgressBar progress;
	private JButton progressCancel;
	
	public Thread thread;
	
	private ClientFTSocket socket;
	
	private boolean socketUsing = false;

	public FTGUI(ClientFTSocket socket) {
		super("File Transfer");
		this.socket = socket;
		init();
	}
	
	public void init() {
		InputStream is = RemoteDesktopGUI.class.getResourceAsStream("NanumBarunpenB.ttf");
		Font font = new Font("바탕", Font.BOLD, 10);
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
		font = font.deriveFont(10);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(5,5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.setContentPane(panel);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setMinimumSize(new Dimension(600, 500));

		JPanel left = new JPanel();
		left.setLayout(new BorderLayout(0, 5));
		left.setMinimumSize(new Dimension(400, 500));
		
		JLabel cName = new JLabel("Local Computer");
		cName.setFont(font);
		left.add(cName, "North");
		
		JPanel left1 = new JPanel();
		left1.setLayout(new BorderLayout());
		left.add(left1, "Center");
		
		JPanel left2 = new JPanel();
		left2.setLayout(new BorderLayout());
		left1.add(left2, "North");

		JLabel cPathLabel = new JLabel("Path");
		cPathLabel.setFont(font);
		left2.add(cPathLabel, "West");
		
		String[] test = {"/"};
		cPath = new JComboBox<String>(test);
		cPath.addItemListener(this);
		cPath.setEditable(true);
		cPath.getEditor().getEditorComponent().addKeyListener(this);
		cPath.setFont(font);
		left2.add(cPath, "Center");
		
		JPanel left3 = new JPanel();
		left3.setLayout(new BorderLayout());
		left1.add(left3, "Center");
		
		JPanel left4 = new JPanel();
		left4.setLayout(new BorderLayout());
		left3.add(left4, "North");
		
		JPanel left5 = new JPanel();
		left5.setLayout(new FlowLayout());
		left4.add(left5, "West");
		
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/refresh.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		c_refresh = new JButton(icon);
		c_refresh.setPreferredSize(new Dimension(35, 35));
		left5.add(c_refresh);
		c_refresh.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/delete.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		c_delete = new JButton(icon);
		c_delete.setPreferredSize(new Dimension(35, 35));
		left5.add(c_delete);
		c_delete.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/add.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		c_add = new JButton(icon);
		c_add.setPreferredSize(new Dimension(35, 35));
		left5.add(c_add);
		c_add.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/up.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		c_up = new JButton(icon);
		c_up.setPreferredSize(new Dimension(35, 35));
		left5.add(c_up);
		c_up.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/root.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		c_root = new JButton(icon);
		c_root.setPreferredSize(new Dimension(35, 35));
		left5.add(c_root);
		c_root.addActionListener(this);
		
		JPanel left6 = new JPanel();
		left6.setLayout(new FlowLayout());
		left4.add(left6, "East");
		
		JLabel sendLabel = new JLabel("Send");
		sendLabel.setFont(font);
		left6.add(sendLabel);

		icon = new ImageIcon(getClass().getClassLoader().getResource("img/sendto.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		send = new JButton(icon);
		send.setPreferredSize(new Dimension(35, 35));
		left6.add(send);
		send.addActionListener(this);
		
		String[] columnNames = {"Name", "Size", "Type", "Modify Time"};
		Object[][] contents = {{}};
		cTable = new JTable(new DefaultTableModel(contents, columnNames) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int colum) {
				return false;
			}
		});
		JScrollPane cTable_s = new JScrollPane(cTable);
		cTable.addMouseListener(this);
		RowSorter<TableModel> sorter = new TableRowSorter<>(cTable.getModel());
		sorter.toggleSortOrder(0);
		cTable.setRowSorter(sorter);
		left3.add(cTable_s, "Center");

		JPanel right = new JPanel();
		right.setLayout(new BorderLayout(0,5));
		right.setMinimumSize(new Dimension(400, 500));
		
		JLabel sName = new JLabel("Remote Computer");
		sName.setFont(font);
		right.add(sName, "North");
		
		JPanel right1 = new JPanel();
		right1.setLayout(new BorderLayout());
		right.add(right1, "Center");
		
		JPanel right2 = new JPanel();
		right2.setLayout(new BorderLayout());
		right1.add(right2, "North");

		JLabel sPathLabel = new JLabel("Path");
		sPathLabel.setFont(font);
		right2.add(sPathLabel, "West");
		
		sPath = new JComboBox<String>(test);
		right2.add(sPath, "Center");
		sPath.addItemListener(this);
		sPath.setEditable(true);
		sPath.getEditor().getEditorComponent().addKeyListener(this);
		sPath.setFont(font);
		
		JPanel right3 = new JPanel();
		right3.setLayout(new BorderLayout());
		right1.add(right3, "Center");
		
		JPanel right4 = new JPanel();
		right4.setLayout(new BorderLayout());
		right3.add(right4, "North");
		
		JPanel right5 = new JPanel();
		right5.setLayout(new FlowLayout());
		right4.add(right5, "East");
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/refresh.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		s_refresh = new JButton(icon);
		s_refresh.setPreferredSize(new Dimension(35, 35));
		right5.add(s_refresh);
		s_refresh.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/delete.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		s_delete = new JButton(icon);
		s_delete.setPreferredSize(new Dimension(35, 35));
		right5.add(s_delete);
		s_delete.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/add.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		s_add = new JButton(icon);
		s_add.setPreferredSize(new Dimension(35, 35));
		right5.add(s_add);
		s_add.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/up.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		s_up = new JButton(icon);
		s_up.setPreferredSize(new Dimension(35, 35));
		right5.add(s_up);
		s_up.addActionListener(this);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/root.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		s_root = new JButton(icon);
		s_root.setPreferredSize(new Dimension(35, 35));
		right5.add(s_root);
		s_root.addActionListener(this);
		
		JPanel right6 = new JPanel();
		right6.setLayout(new FlowLayout());
		right4.add(right6, "West");
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/receive.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(25*icon.getIconWidth()/icon.getIconHeight(), 25, Image.SCALE_SMOOTH));
		recv = new JButton(icon);
		recv.setPreferredSize(new Dimension(35, 35));
		right6.add(recv);
		recv.addActionListener(this);

		JLabel recvLabel = new JLabel("Receive");
		recvLabel.setFont(font);
		right6.add(recvLabel);
		
		sTable = new JTable(new DefaultTableModel(contents, columnNames) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int colum) {
				return false;
			}
		});
		JScrollPane sTable_s = new JScrollPane(sTable);
		sTable.addMouseListener(this);
		sorter = new TableRowSorter<>(sTable.getModel());
		sorter.toggleSortOrder(0);
		sTable.setRowSorter(sorter);
		right3.add(sTable_s, "Center");
		
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(1, 2, 5, 0));
		top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout(5,5));
		bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bottom.setSize(800, 100);
		
		JLabel logLabel = new JLabel("File Transfer Log");
		logLabel.setFont(font);
		
		log = new JTextArea();
		log.setLineWrap(true);
		log.setEditable(false);
		JScrollPane log_s = new JScrollPane(log);
		log_s.setSize(this.getWidth(), 100);
		log_s.setMinimumSize(new Dimension(this.getWidth(), 50));
		log_s.setPreferredSize(new Dimension(this.getWidth(), 100));

		this.add(top, "Center");
		top.add(left);
		top.add(right);
		this.add(bottom, "South");
		bottom.add(logLabel, "North");
		bottom.add(log_s, "Center");
		
		this.setLocationRelativeTo(null);
		
		progressBarInit();
		
		clientRoot();
		serverRoot();
	}
	
	public void progressBarInit() {
		InputStream is = RemoteDesktopGUI.class.getResourceAsStream("NanumBarunpenB.ttf");
		Font font = new Font("바탕", Font.BOLD, 12);
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
		font = font.deriveFont(12);
		
		progressDialog = new JDialog(this, false);
		
		progressDialog.setLayout(new BorderLayout(10, 10));
		progressDialog.setSize(400, 120);
		progressDialog.setPreferredSize(new Dimension(400, 120));
		progressDialog.setBounds(10, 10, 10, 10);
		
		JPanel panel = new JPanel(new BorderLayout(10,10));
		//panel.setSize(400, 120);
		//panel.setPreferredSize(new Dimension(400, 120));
		panel.setBounds(10, 10, 10, 10);
		
		progressDialog.add(panel, "Center");
		
		progressLabel = new JLabel("progress");
		progressLabel.setFont(font);
		
		progress = new JProgressBar(0,10000);
		progress.setStringPainted(true);
		progress.setIndeterminate(true);
		progress.setFont(font);
		progress.setPreferredSize(new Dimension(300, 30));
		
		progressCancel = new JButton("cancel");
		progressCancel.setFont(font);
		progressCancel.setSize(50, 20);
		progressCancel.setPreferredSize(new Dimension(50, 20));
		progressCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				thread.interrupt();
				progressDialog.dispose();
			}
		});
		
		panel.add(progressLabel, "North");
		panel.add(progress, "Center");
		panel.add(progressCancel, "South");
		
		progressDialog.setLocationRelativeTo(this);
		progressDialog.pack();
	}
	
	public void clientPath(String path) {
		File f = null;
		File[] files;
		if(path == null || path.equals("")) {
			files = File.listRoots();
		} else {
			f = new File(path);
			files = f.listFiles();
		}
		
		if(files == null) {
			JOptionPane.showMessageDialog(this, "Directory not found", "Directory not found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Object[][] data = new Object[files.length][];
		
		cTable.removeAll();
		for(int i=((DefaultTableModel)cTable.getModel()).getRowCount()-1; i>=0; i--) {
			((DefaultTableModel)cTable.getModel()).removeRow(i);
		}
		
		for(int i=0; i<files.length; i++) {
			if(files[i].isHidden()) {
				//continue;
			}
			data[i] = new Object[4];
			if(path == null || path.equals("")) {
				data[i][0] = files[i].getAbsolutePath();
			} else {
				data[i][0] = files[i].getName();
			}
			if(data[i][0].equals("")) {
				data[i][0] = "/";
			}
			if(files[i].isFile()) {
				int c = 0;
				float size = (float)files[i].length();
				while(size >= 1000f) {
					size /= 1000f;
					c++;
				}
				data[i][1] = String.format("%.2f", size) + sizeUnit[c];
				data[i][2] = "File";
			} else if(files[i].isDirectory()) {
				data[i][1] = "";
				data[i][2] = "Directory";
			}
			data[i][3] = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date(files[i].lastModified()));
			//((DefaultTableModel)cTable.getModel()).addRow(data[i]);
		}
		
		for(int i=0; i<files.length; i++) {
			((DefaultTableModel)cTable.getModel()).addRow(data[i]);
		}
		
		c_path = path;
		typing = false;
		
		cPath.getEditor().setItem(c_path);
	}
	
	public void serverPath(String path) {
		
		Object[][] data = socket.serverPath(path);
		
		if(data == null) {
			JOptionPane.showMessageDialog(this, "Fail to load", "Fail to load", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		sTable.removeAll();
		for(int i=((DefaultTableModel)sTable.getModel()).getRowCount()-1; i>=0; i--) {
			((DefaultTableModel)sTable.getModel()).removeRow(i);
		}
		
		for(int i=0; i<data.length; i++) {
			if(data[i][2].equals("File")) {
				int c = 0;
				float size = (long)data[i][1];
				while(size >= 1000f) {
					size /= 1000f;
					c++;
				}
				data[i][1] = String.format("%.2f", size) + sizeUnit[c];
			} else {
				data[i][1] = "";
			}
			data[i][3] = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date((long)data[i][3]));
			//((DefaultTableModel)sTable.getModel()).addRow(data[i]);
		}
		
		for(int i=0; i<data.length; i++) {
			((DefaultTableModel)sTable.getModel()).addRow(data[i]);
		}
		
		s_path = socket.getCorrectedPath(path);
		typing = false;
		
		sPath.getEditor().setItem(s_path);
	}
	
	public void clientAdd() {
		String str = JOptionPane.showInputDialog(this, "Input Name of Directory", "Create Directory", JOptionPane.PLAIN_MESSAGE);
		String new_str = c_path+"/"+str;
		File f = new File(new_str);
		
		if(f.exists()) {
			JOptionPane.showMessageDialog(this, "Directory '"+str+"' already exist", "Create Fail", JOptionPane.ERROR_MESSAGE);
		} else {
			f.mkdirs();
			clientRefresh();
			this.addLog("Created directory at local "+new_str);
		}
	}
	
	public void serverAdd() {
		String str = JOptionPane.showInputDialog(this, "Input Name of Directory", "Create Directory", JOptionPane.PLAIN_MESSAGE);
		String new_str = s_path+"/"+str;
		
		if(socket.serverAdd(new_str)) {
			serverRefresh();
			this.addLog("Created directory at server : "+socket.getCorrectedPath(new_str));
		} else {
			JOptionPane.showMessageDialog(this, "Fail to create directory", "Create Fail", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void clientDelete() {
		int[] selectedRows = cTable.getSelectedRows();
		String[] selected = new String[selectedRows.length];
		
		for(int i=0; i<selectedRows.length; i++) {
			selected[i] = (String)cTable.getValueAt(selectedRows[i], 0);
		}
		
		for(int i=0; i<selected.length; i++) {
			delete(c_path+"/"+selected[i]);
		}
		
		clientRefresh();
	}
	
	public void delete(String str) {
		File f = new File(str);
		if(f.isDirectory()) {
			String[] childs = f.list();
			
			for(int i=0; i<childs.length; i++) {
				delete(str+"/"+childs[i]);
			}
		}
		f.delete();

		this.addLog("Deleted file(directory) at local : "+f.getPath());
	}
	
	public void serverDelete() {
		int[] selectedRows = sTable.getSelectedRows();
		String[] selected = new String[selectedRows.length];
		
		for(int i=0; i<selectedRows.length; i++) {
			selected[i] = (String)sTable.getValueAt(selectedRows[i], 0);
		}
		
		for(int i=0; i<selected.length; i++) {
			socket.serverDelete(s_path+"/"+selected[i]);
			this.addLog("Deleted file(directory) at server : "+socket.getCorrectedPath(s_path+"/"+selected[i]));
		}
	}
	
	public void clientRefresh() {
		clientPath(c_path);
	}
	
	public void serverRefresh() {
		serverPath(s_path);
	}
	
	public void clientRoot() {
		File root[] = File.listRoots();
		cPath.removeAllItems();
		cPath.addItem("");
		for(int i=0; i<root.length; i++) {
			cPath.addItem(root[i].getAbsolutePath());
		}
		cPath.setSelectedIndex(0);
		
		clientPath(cPath.getItemAt(cPath.getSelectedIndex()));
	}
	
	public void serverRoot() {
		String str[] = socket.serverRoot();
		
		sPath.removeAllItems();
		sPath.addItem("");
		for(int i=0; i<str.length; i++) {
			sPath.addItem(str[i]);
		}
		sPath.setSelectedIndex(0);
		
		serverPath(sPath.getItemAt(sPath.getSelectedIndex()));
	}
	
	public void clientUp() {
		if(c_path == null || c_path.equals("")) {
			return;
		}
		File f = new File(c_path);
		if(f.getParentFile() == null) {
			clientRoot();
		} else {
			clientPath(f.getParent());
		}
	}
	
	public void serverUp() {
		if(s_path == null || s_path.equals("")) {
			return;
		}
		s_path = socket.getParent(s_path);
		if(s_path.equals("")) {
			serverRoot();
		} else {
			serverPath(s_path);
		}
	}
	
	public long sizeRecursive(String path) {
		File f = new File(path);
		File[] files;
		long size = 0;
		
		if(f.isDirectory()) {
			files = f.listFiles();
			for(int i=0; i<files.length; i++) {
				size += sizeRecursive(files[i].getPath());
			}
			return size;
		} else {
			return f.length();
		}
	}
	
	public void send() {
		int[] selectedRows = cTable.getSelectedRows();
		String[] selected = new String[selectedRows.length];
		
		totalSize = 0;
		transfer = 0;
		progress.setValue(0);
		progress.setIndeterminate(true);
		progressLabel.setText("Preparing to send files.");
		progressDialog.setVisible(true);
		
		for(int i=0; i<selectedRows.length; i++) {
			selected[i] = (String)cTable.getValueAt(selectedRows[i], 0);
			totalSize += sizeRecursive(c_path+"/"+selected[i]);
		}
		
		for(int i=0; i<selected.length; i++) {
			((DefaultTableModel)sTable.getModel()).addRow(new Object[] {selected[i], "", "", ""});
			progress.setIndeterminate(false);
			sendRecursive(selected[i]);
			if(thread.isInterrupted()) {
				break;
			}
		}

		try {
			if(thread.isInterrupted()) {
				progressLabel.setText("Canceling transfer");
			} else {
				progressLabel.setText("Send completed.");
			}
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		
		if(progressDialog.isVisible()) {
			progressDialog.dispose();
		}
		
		if(!thread.isInterrupted() && thread != null) {
			thread.interrupt();
		}
	}
	
	public void sendRecursive(String path) {
		File f = new File(c_path+"/"+path);
		File[] files;
		if(f.isDirectory()) {
			if(socket.serverAdd(s_path+"/"+path)) {
				files = f.listFiles();
				if(files == null) {
					return;
				}
				for(int i=0; i<files.length; i++) {
					sendRecursive(path+"/"+files[i].getName());
					if(thread.isInterrupted()) {
						break;
					}
				}
			} else {
				if(JOptionPane.showConfirmDialog(this, "Directory '"+f.getName()+"' already exist. Join this directory?",
						"Directory already exist", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
					files = f.listFiles();
					if(files == null) {
						return;
					}
					for(int i=0; i<files.length; i++) {
						sendRecursive(path+"/"+files[i].getName());
						if(thread.isInterrupted()) {
							break;
						}
					}
				} else {
					//none
				}
			}
		} else if(f.isFile()) {
			if(socket.isExist(s_path+"/"+path)) {
				if(JOptionPane.showConfirmDialog(this, "File '"+f.getName()+"' already exist. Overwrite this file?",
						"File already exist", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
					socket.send(path, c_path, s_path);
					if(thread.isInterrupted()) {
						//socket.serverDelete(path+"/"+s_path);
					}
					//serverRefresh();
				}
			} else {
				socket.send(path, c_path, s_path);
				if(thread.isInterrupted()) {
					//socket.serverDelete(path+"/"+s_path);
				}
				//serverRefresh();
			}
		}
	}
	
	public void progressAdd(long size) {
		transfer += size;
		progress.setValue((int)(transfer*10000/totalSize));
	}
	
	public void setProgressLabelText(String str) {
		progressLabel.setText(str);
	}
	
	public void receive() {
		int[] selectedRows = sTable.getSelectedRows();
		String[] selected = new String[selectedRows.length];
		
		totalSize = 0;
		transfer = 0;
		progress.setValue(0);
		progress.setIndeterminate(true);
		progressLabel.setText("Preparing to receive files.");
		progressDialog.setVisible(true);
		
		for(int i=0; i<selectedRows.length; i++) {
			selected[i] = (String)sTable.getValueAt(selectedRows[i], 0);
			totalSize += socket.totalSize(s_path+"/"+selected[i]);
		}
		
		for(int i=0; i<selected.length; i++) {
			if(thread.isInterrupted()) {
				break;
			}
			((DefaultTableModel)cTable.getModel()).addRow(new Object[] {selected[i], "", "", ""});
			progress.setIndeterminate(false);
			receiveRecursive(selected[i]);
		}
		
		try {
			if(thread.isInterrupted()) {
				progressLabel.setText("Canceling transfer");
			} else {
				progressLabel.setText("Receive completed.");
			}
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		if(progressDialog.isVisible()) {
			progressDialog.dispose();
		}
		
		if(!thread.isInterrupted() && thread != null) {
			thread.interrupt();
		}
	}
	
	public void receiveRecursive(String path) {
		File f = new File(c_path+"/"+path);
		
		if(socket.isDirectory(s_path+"/"+path)) {
			if(f.exists()) {
				if(JOptionPane.showConfirmDialog(this, "Directory '"+f.getName()+"' already exist. Join this directory?",
						"Directory already exist", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
					Object[][] data = socket.serverPath(s_path+"/"+path);
					if(data == null) {
						return;
					}
					for(int i=0; i<data.length; i++) {
						receiveRecursive(path+"/"+((String)(data[i][0])));
						if(thread.isInterrupted()) {
							break;
						}
					}
				}
			} else {
				f.mkdir();
				Object[][] data = socket.serverPath(s_path+"/"+path);
				if(data == null) {
					return;
				}
				for(int i=0; i<data.length; i++) {
					receiveRecursive(path+"/"+((String)(data[i][0])));
					if(thread.isInterrupted()) {
						break;
					}
				}
			}
		} else {
			if(f.exists()) {
				if(JOptionPane.showConfirmDialog(this, "File '"+f.getName()+"' already exist. Overwrite this file?",
						"File already exist", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
					socket.receive(path, c_path, s_path);
					if(thread.isInterrupted()) {
						f.delete();
					}
					//clientRefresh();
				}
			} else {
				socket.receive(path, c_path, s_path);
				if(thread.isInterrupted()) {
					f.delete();
				}
				//clientRefresh();
			}
		}
	}
	
	public void addLog(String msg) {
		if(!log.getText().equals("")) {
			log.append("\n");
		}
		log.append(msg);

		log.setCaretPosition(log.getDocument().getLength());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(c_add)) {
			clientAdd();
		} else if(e.getSource().equals(s_add)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			serverAdd();
			socketUsing = false;
		} else if(e.getSource().equals(c_delete)) {
			clientDelete();
		} else if(e.getSource().equals(s_delete)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			serverDelete();
			serverRefresh();
			socketUsing = false;
		} else if(e.getSource().equals(c_refresh)) {
			clientRefresh();
		} else if(e.getSource().equals(s_refresh)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			serverRefresh();
			socketUsing = false;
		} else if(e.getSource().equals(c_root)) {
			clientRoot();
		} else if(e.getSource().equals(s_root)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			serverRoot();
			socketUsing = false;
		} else if(e.getSource().equals(c_up)) {
			clientUp();
		} else if(e.getSource().equals(s_up)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			serverUp();
			socketUsing = false;
		} else if(e.getSource().equals(send)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			
			thread = new Thread() {
				public void run() {
					send();
					socketUsing = false;
					while(!this.isInterrupted());
					serverRefresh();
				}
			};
			thread.start();
			//send();
			//socketUsing = false;
		} else if(e.getSource().equals(recv)) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			
			thread = new Thread() {
				public void run() {
					receive();
					socketUsing = false;
					while(!this.isInterrupted());
					clientRefresh();
				}
			};
			thread.start();
			//receive();
			//socketUsing = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(cTable)) {
			if(e.getClickCount() == 2) {
				String select = (String)cTable.getValueAt(cTable.getSelectedRow(), 0);
				String new_path = c_path;
				if(c_path == null || c_path.equals("")) {
					new_path = select;
				} else if(c_path.equals("/")) {
					new_path += select;
				} else {
					new_path += ("/"+select);
				}
				if(!((new File(new_path)).isDirectory())) {
					if(socketUsing) {
						return;
					}
					socketUsing = true;
					thread = new Thread() {
						public void run() {
							send();
							socketUsing = false;
							while(!this.isInterrupted());
							serverRefresh();
						}
					};
					thread.start();
					//send();
				} else {
					clientPath(new_path);
				}
			}
		} else if(e.getSource().equals(sTable)) {
			if(e.getClickCount() == 2) {
				if(socketUsing) {
					return;
				}
				socketUsing = true;
				String select = (String)sTable.getValueAt(sTable.getSelectedRow(), 0);
				String new_path = s_path;
				if(s_path == null || s_path.equals("")) {
					new_path = select;
				} else if(s_path.equals("/")) {
					new_path += select;
				} else {
					new_path += ("/"+select);
				}
				if(!socket.isDirectory(new_path)) {
					thread = new Thread() {
						public void run() {
							receive();
							socketUsing = false;
							while(!this.isInterrupted());
							clientRefresh();
						}
					};
					thread.start();
					
					//receive();
				} else {
					serverPath(new_path);
					socketUsing = false;
				}
			}
			//socketUsing = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(cPath) && e.getStateChange() == ItemEvent.SELECTED && !typing) {
			clientPath((String)cPath.getSelectedItem());
		} else if(e.getSource().equals(sPath) && e.getStateChange() == ItemEvent.SELECTED && !typing) {
			if(socketUsing) {
				return;
			}
			socketUsing = true;
			serverPath((String)sPath.getSelectedItem());
			socketUsing = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(cPath.getEditor().getEditorComponent())) {
			typing = true;
			if(e.getKeyChar() == KeyEvent.VK_ENTER) {
				clientPath((String)cPath.getSelectedItem());
				typing = false;
			}
		} else if(e.getSource().equals(sPath.getEditor().getEditorComponent())) {
			if(socketUsing) {
				return;
			}
			typing = true;
			if(e.getKeyChar() == KeyEvent.VK_ENTER) {
				socketUsing = true;
				serverPath((String)sPath.getSelectedItem());
				socketUsing = false;
				typing = false;
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
