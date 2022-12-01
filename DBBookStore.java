import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

// DB 연결
class DB_Conn {
	Connection con = null;

	public DB_Conn() {
		String url = "jdbc:oracle:thin:@211.108.193.119:1521:XE";
		String id = "C##BSAdMIN";
		String password = "1234";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("드라이버 적재 성공");
			con = DriverManager.getConnection(url, id, password);
			System.out.println("DB 연결 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("No Driver.");
		} catch (SQLException e) {
			System.out.println("Connection Fail");
		}
	}
}

class JTableExample extends JFrame implements ActionListener {
	String bookAttribute[] = { "책번호", "제목", "저자", "출판사", "가격", "재고량" };
	String userAttribute[] = { "회원번호", "이름", "주민번호", "전화번호", "가입일", "마일리지" };
	// Table 생성
	private DefaultTableModel bookModel = new DefaultTableModel(bookAttribute, 0);
	private DefaultTableModel userModel = new DefaultTableModel(userAttribute, 0);
	// Table에 들어갈 데이터 목록들 (헤더정보, 추가 될 row 개수)
	private JTable table = new JTable();
	private JScrollPane jsp = new JScrollPane(table);
	private JButton btn = new JButton("출력");

	private JTextField adminCodeTf;
	private JTextField adminPwTf;
	private JTextField searchTf;

	private String comData[] = { "책", "회원", "우수고객", "재고량부족 책목록" };
	private JComboBox tableComboBox;
	private String tableType = "책";

	private JButton loginBtn;
	private JButton searchBtn;
	private JButton addBtn;
	private JButton updateBtn;
	private JButton sellBtn;

	public JTableExample() {
		super("DB 서점 프로그램");
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);

		JLabel lblNewLabel = new JLabel("DB 서점 프로그램");
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
		lblNewLabel.setBounds(305, 10, 200, 24);
		add(lblNewLabel);

		table.setModel(bookModel);
		jsp.setBorder(new LineBorder(new Color(128, 128, 128), 2, true));
		jsp.setBounds(12, 88, 760, 320);
		jsp.setBackground(Color.white);
		add(jsp);

		adminCodeTf = new JTextField();
		adminCodeTf.setBounds(349, 60, 120, 24);
		add(adminCodeTf);
		adminCodeTf.setColumns(10);

		JLabel adminCodeLabel = new JLabel("관리자코드");
		adminCodeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		adminCodeLabel.setBounds(264, 60, 85, 24);
		add(adminCodeLabel);

		adminPwTf = new JTextField();
		adminPwTf.setColumns(10);
		adminPwTf.setBounds(548, 60, 120, 24);
		add(adminPwTf);

		JLabel adminPwLabel = new JLabel("비밀번호");
		adminPwLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		adminPwLabel.setBounds(481, 60, 65, 24);
		add(adminPwLabel);

		// 로그인 버튼
		loginBtn = new JButton("로그인");
		loginBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		loginBtn.setBounds(680, 60, 90, 22);
		loginBtn.setFocusable(false);
		loginBtn.addActionListener(this);
		loginBtn.setActionCommand("login");
		add(loginBtn);

		// 검색 버튼
		searchBtn = new JButton("검색");
		searchBtn.setBounds(333, 418, 80, 35);
		searchBtn.setFocusable(false);
		searchBtn.addActionListener(this);
		searchBtn.setActionCommand("search");
		add(searchBtn);

		// 추가 버튼
		addBtn = new JButton("추가");
		addBtn.setBounds(425, 418, 80, 35);
		addBtn.setFocusable(false);
		addBtn.addActionListener(this);
		addBtn.setActionCommand("add");
		add(addBtn);

		// 변경 버튼
		updateBtn = new JButton("변경");
		updateBtn.setBounds(517, 418, 80, 35);
		updateBtn.setFocusable(false);
		updateBtn.addActionListener(this);
		updateBtn.setActionCommand("update");
		add(updateBtn);

		// 판매 버튼
		sellBtn = new JButton("판매");
		sellBtn.setBounds(609, 418, 80, 35);
		sellBtn.setFocusable(false);
		sellBtn.addActionListener(this);
		sellBtn.setActionCommand("sell");
		add(sellBtn);

		tableComboBox = new JComboBox(comData);
		tableComboBox.setBounds(12, 62, 130, 20);
		tableComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		tableComboBox.setBackground(Color.white);
		tableComboBox.setFocusable(false);
		tableComboBox.addActionListener(this);
		add(tableComboBox);

		searchTf = new JTextField();
		searchTf.setDropMode(DropMode.INSERT);
		searchTf.setBounds(176, 421, 145, 30);
		add(searchTf);
		searchTf.setColumns(10);

		JLabel searchLabel = new JLabel("Name");
		searchLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
		searchLabel.setBounds(116, 418, 58, 30);
		add(searchLabel);

		JLabel tableLabel = new JLabel("Select Table");
		tableLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
		tableLabel.setBounds(12, 32, 120, 30);
		add(tableLabel);
		this.setVisible(true);
	}

	// 이벤트
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		String comSelect = (String) tableComboBox.getSelectedItem();
		tableType = comSelect;
		if (command.equals("login")) {
			// 로그인 이벤트
			login();
		} else if (command.equals("search")) {
			// 검색 이벤트
		} else if (command.equals("add")) {
			// 추가 이벤트
		} else if (command.equals("update")) {
			// 변경 이벤트
		} else if (command.equals("sell")) {
			// 판매 이벤트
		} // else if (comSelect.equals("책")) {
//			// 책 목록 불러오기
//			setTable(bookModel);
//			table.setModel(bookModel);
//		} else if (comSelect.equals("회원")) {
//			// 회원 목록 불러오기
//			setTable(userModel);
//			table.setModel(userModel);
//		} else if (comSelect.equals("우수고객")) {
//			// 우수고객 목록 불러오기
//			setTable(userModel);
//			table.setModel(userModel);
//		} else if (comSelect.equals("재고량부족 책목록")) {
//			// 재고량 부족 책 목록 불러오기
//			setTable(bookModel);
//			table.setModel(bookModel);
//		}
	}

	private void login() {
		String query = "select 비밀번호 from 관리자 where 관리자코드 = '" + adminCodeTf.getText() + "' and 비밀번호 = '"
				+ adminPwTf.getText() + "'";
		DB_Conn dbc = new DB_Conn();
		try {
			Statement stmt = dbc.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if(adminPwTf.getText().equals(rs.getString(1))) {
					
				}
			}
			stmt.close();
			rs.close();
			dbc.con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setTable(DefaultTableModel model) {
		String query = "";
		if (tableType.equals("책")) {
			query = "select 책번호, 제목, 저자, 출판사, 가격, 재고량 from 책";
		} else if (tableType.equals("회원")) {
			query = "select 회원번호,이름,주민번호,전화번호,가입일,마일리지 from 회원";
		} else if (tableType.equals("우수고객")) {
			// 우수고객 관련 저장프로시저 사용해야함
		} else if (tableType.equals("재고량부족 책목록")) {
			// 재고량 부족 책목록 관련 저장프로시저 사용해야함
		}

		DB_Conn dbc = new DB_Conn();
		try {
			if (model.getRowCount() > 1) {
				model.setRowCount(0);
			}
			Statement stmt = dbc.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			Object data[] = new Object[6];
			while (rs.next()) {
				data[0] = rs.getObject(1);
				data[1] = rs.getObject(2);
				data[2] = rs.getObject(3);
				data[3] = rs.getObject(4);
				data[4] = rs.getObject(5);
				data[5] = rs.getObject(6);
				model.addRow(data);
			}
			stmt.close();
			rs.close();
			dbc.con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

public class DBBookStore {
	public static void main(String[] args) {
		JTableExample jt = new JTableExample();
	}
}