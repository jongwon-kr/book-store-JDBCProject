import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
	private DefaultTableModel searchBookModel = new DefaultTableModel(bookAttribute, 0);
	private DefaultTableModel searchUserModel = new DefaultTableModel(userAttribute, 0);
	// Table에 들어갈 데이터 목록들 (헤더정보, 추가 될 row 개수)
	private JTable table = new JTable();
	private JScrollPane jsp = new JScrollPane(table);
	private JButton btn = new JButton("출력");

	private JTextField adminCodeTf; // 관리자 코드 입력 textField
	private JTextField adminPwTf; // 관리자 비밀번호 입력 textField
	private JTextField searchTf; // 검색 내용 입력 textField

	private String comData[] = { "책", "회원", "우수고객", "재고량부족 책목록" }; // 테이블 선택 데이터
	private String searchData[];
	private JComboBox tableComboBox;
	private JComboBox searchComboBox;
	private String tableType = "책";

	private JButton loginBtn; // 로그인 버튼
	private JButton searchBtn; // 검색 버튼
	private JButton addBtn; // 추가 버튼
	private JButton updateBtn; // 수정 버튼
	private JButton sellBtn; // 판매 버튼
	private JLabel loginCheckLabel; // 로그인 확인 라벨
	private JLabel adminCodeLabel;
	private JLabel adminPwLabel;
	private JDialog alertDialog; // 알림창 다이얼로그
	private JDialog addDialog; // 추가 다이얼로그
	private JDialog updateDialog; // 수정 다이얼로그
	private JDialog sellDialog; // 판매 다이얼로그

	private boolean adminLogin = false;

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

		setTable(bookModel, "책");
		table.setModel(bookModel);
		table.changeSelection(0, 0, false, false);
		jsp.setBorder(new LineBorder(new Color(128, 128, 128), 2, true));
		jsp.setBounds(12, 88, 760, 320);
		jsp.setBackground(Color.white);
		add(jsp);

		adminCodeTf = new JTextField();
		adminCodeTf.setBounds(349, 60, 120, 24);
		adminCodeTf.setActionCommand("enterLogin");
		adminCodeTf.addActionListener(this);
		add(adminCodeTf);
		adminCodeTf.setColumns(10);

		adminCodeLabel = new JLabel("관리자코드");
		adminCodeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		adminCodeLabel.setBounds(264, 60, 85, 24);
		add(adminCodeLabel);

		loginCheckLabel = new JLabel();
		loginCheckLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		loginCheckLabel.setBounds(464, 60, 300, 24);
		loginCheckLabel.setVisible(false);
		add(loginCheckLabel);

		adminPwTf = new JTextField();
		adminPwTf.setColumns(10);
		adminPwTf.setActionCommand("enterLogin");
		adminPwTf.addActionListener(this);
		adminPwTf.setBounds(548, 60, 120, 24);
		add(adminPwTf);

		adminPwLabel = new JLabel("비밀번호");
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

		searchComboBox = new JComboBox(bookAttribute);
		searchComboBox.setBounds(74, 421, 100, 30);
		searchComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		searchComboBox.setBackground(Color.white);
		searchComboBox.setFocusable(false);
		add(searchComboBox);

		searchTf = new JTextField();
		searchTf.setDropMode(DropMode.INSERT);
		searchTf.setBounds(176, 421, 145, 30);
		searchTf.setActionCommand("enterSearch");
		searchTf.addActionListener(this);
		add(searchTf);
		searchTf.setColumns(10);

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
		if (command.equals("login") || command.equals("enterLogin")) { // 로그인 이벤트
			login();
		} else if (command.equals("search") || command.equals("enterSearch")) { // 검색 이벤트
			if (tableType.equals("책") || tableType.equals("재고량부족 책목록")) {
				search(searchBookModel);
			} else if (tableType.equals("회원") || tableType.equals("우수고객")) {
				search(searchUserModel);
			}
			table.changeSelection(0, 0, false, false);
		} else if (command.equals("add")) { // 추가 이벤트
			if (adminLogin) {
				add();
			} else {
				Alert("경고", "관리자 로그인이 필요합니다!");
			}
		} else if (command.equals("update")) { // 변경 이벤트
			if (adminLogin) {
				update();
			} else {
				Alert("경고", "관리자 로그인이 필요합니다!");
			}

		} else if (command.equals("sell")) { // 판매 이벤트
			if (adminLogin) {
				String selectedBook = "";
				selectedBook = (String) table.getValueAt(table.getSelectedRow(), 1);
				if (!selectedBook.equals("") && tableType.equals("책")) {
					System.out.println(selectedBook);
					sell(selectedBook);
				} else {
					Alert("경고", "판매할 책을 선택해주세요");
				}
			} else {
				Alert("경고", "관리자 로그인이 필요합니다!");
			}
		} else if (comSelect.equals("책")) { // 책 목록 불러오기
			searchComboBox.setModel(new DefaultComboBoxModel(bookAttribute));
			setTable(bookModel, comSelect);
			table.setModel(bookModel);
			table.changeSelection(0, 0, false, false);
		} else if (comSelect.equals("회원")) { // 회원 목록 불러오기
			searchComboBox.setModel(new DefaultComboBoxModel(userAttribute));
			setTable(userModel, comSelect);
			table.setModel(userModel);
			table.changeSelection(0, 0, false, false);
		} else if (comSelect.equals("우수고객")) { // 우수고객 목록 불러오기
			setTable(userModel, comSelect);
			table.setModel(userModel);
			table.changeSelection(0, 0, false, false);
		} else if (comSelect.equals("재고량부족 책목록")) { // 재고량 부족 책 목록 불러오기
			setTable(bookModel, comSelect);
			table.setModel(bookModel);
			table.changeSelection(0, 0, false, false);
		}
	}

	// 검색
	private void search(DefaultTableModel searchModel) {
		searchModel.setNumRows(0); // 검색된 테이블모델 초기화
		String searchKeyword = searchTf.getText(); // 검색어
		int searchColumn = searchComboBox.getSelectedIndex();
		Object[] searchData = new Object[6];
		System.out.println(searchColumn);
		if (tableType.equals("책") || tableType.equals("재고량부족 책목록")) { // bookModel 검색
			table.setModel(bookModel);
			if (searchKeyword.equals("")) { // 검색 창이 빈칸일 때
				setTable(bookModel, "책");
			} else {
				for (int i = 0; i < table.getRowCount(); i++) {
					if (bookModel.getValueAt(i, searchColumn).toString().contains(searchKeyword)) {
						for (int j = 0; j < 6; j++) {
							searchData[j] = bookModel.getValueAt(i, j).toString();
						}
						searchModel.addRow(searchData);
					}
				}
				table.setModel(searchModel);
			}
		} else if (tableType.equals("회원") || tableType.equals("우수고객")) { // userModel 검색
			setTable(userModel, "회원");
			if (searchKeyword.equals("")) { // 검색 창이 빈칸일 때
				table.setModel(userModel);
			} else {
				for (int i = 0; i < userModel.getRowCount(); i++) {
					if (userModel.getValueAt(i, searchColumn).toString().contains(searchKeyword)) {
						for (int j = 0; j < 6; j++) {
							searchData[j] = userModel.getValueAt(i, j).toString();
						}
						searchModel.addRow(searchData);
					}
				}
				table.setModel(searchModel);
			}
		}
	}

	// 추가
	private void add() {
		addDialog = new JDialog(this, "추가", true);
		JPanel addPanel = new JPanel();
		addPanel.setLayout(null);

		JLabel addDataLabel = new JLabel("데이터 추가");
		addDataLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		addDataLabel.setBounds(86, 10, 96, 30);
		addPanel.add(addDataLabel);

		JLabel sLabel1 = new JLabel("속성1");
		sLabel1.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel1.setBounds(12, 47, 70, 20);
		addPanel.add(sLabel1);

		JLabel sLabel2 = new JLabel("속성2");
		sLabel2.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel2.setBounds(12, 68, 70, 20);
		addPanel.add(sLabel2);

		JLabel sLabel3 = new JLabel("속성3");
		sLabel3.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel3.setBounds(12, 89, 70, 20);
		addPanel.add(sLabel3);

		JLabel sLabel4 = new JLabel("속성4");
		sLabel4.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel4.setBounds(12, 110, 70, 20);
		addPanel.add(sLabel4);

		JLabel sLabel5 = new JLabel("속성5");
		sLabel5.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel5.setBounds(12, 131, 70, 20);
		addPanel.add(sLabel5);

		JLabel sLabel6 = new JLabel("속성6");
		sLabel6.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel6.setBounds(12, 152, 70, 20);
		addPanel.add(sLabel6);

		JTextField sTf1 = new JTextField();
		sTf1.setDropMode(DropMode.INSERT);
		sTf1.setBounds(86, 49, 177, 20);
		addPanel.add(sTf1);
		sTf1.setColumns(10);

		JTextField sTf2 = new JTextField();
		sTf2.setDropMode(DropMode.INSERT);
		sTf2.setColumns(10);
		sTf2.setBounds(86, 70, 177, 20);
		addPanel.add(sTf2);

		JTextField sTf3 = new JTextField();
		sTf3.setDropMode(DropMode.INSERT);
		sTf3.setColumns(10);
		sTf3.setBounds(86, 91, 177, 20);
		addPanel.add(sTf3);

		JTextField sTf4 = new JTextField();
		sTf4.setDropMode(DropMode.INSERT);
		sTf4.setColumns(10);
		sTf4.setBounds(86, 112, 177, 20);
		addPanel.add(sTf4);

		JTextField sTf5 = new JTextField();
		sTf5.setDropMode(DropMode.INSERT);
		sTf5.setColumns(10);
		sTf5.setBounds(86, 133, 177, 20);
		addPanel.add(sTf5);

		JTextField sTf6 = new JTextField();
		sTf6.setDropMode(DropMode.INSERT);
		sTf6.setColumns(10);
		sTf6.setBounds(86, 154, 177, 20);
		addPanel.add(sTf6);

		JButton dataAddBtn = new JButton("추가");
		dataAddBtn.setFocusable(false);
		dataAddBtn.setBounds(55, 202, 80, 35);
		dataAddBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// preparedStatement 이벤트
			}

		});
		addPanel.add(dataAddBtn);

		JButton cancelBtn = new JButton("취소");
		cancelBtn.setFocusable(false);
		cancelBtn.setBounds(147, 202, 80, 35);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDialog.setVisible(false);
				addDialog.dispose();
			}

		});
		addPanel.add(cancelBtn);

		addDialog.setSize(300, 300);
		addDialog.setLayout(null);
		addDialog.setLocationRelativeTo(this);
		addDialog.setContentPane(addPanel);
		addDialog.setVisible(true);
	}

	// 수정
	private void update() {
		updateDialog = new JDialog(this, "수정", true);
		JPanel updatePanel = new JPanel();
		updatePanel.setLayout(null);

		JLabel updateLabel = new JLabel("데이터 수정");
		updateLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		updateLabel.setBounds(195, 8, 96, 30);
		updatePanel.add(updateLabel);

		JLabel oDataLabel = new JLabel("기존 데이터");
		oDataLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		oDataLabel.setBounds(63, 24, 86, 20);
		updatePanel.add(oDataLabel);

		JLabel nDataLabel = new JLabel("수정 데이터");
		nDataLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		nDataLabel.setBounds(341, 20, 86, 20);
		updatePanel.add(nDataLabel);

		JLabel dLabel1 = new JLabel("데이터1");
		dLabel1.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		dLabel1.setBounds(12, 48, 200, 20);
		updatePanel.add(dLabel1);

		JLabel dLabel2 = new JLabel("데이터2");
		dLabel2.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		dLabel2.setBounds(12, 69, 200, 20);
		updatePanel.add(dLabel2);

		JLabel dLabel3 = new JLabel("데이터3");
		dLabel3.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		dLabel3.setBounds(12, 90, 200, 20);
		updatePanel.add(dLabel3);

		JLabel dLabel4 = new JLabel("데이터4");
		dLabel4.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		dLabel4.setBounds(12, 111, 200, 20);
		updatePanel.add(dLabel4);

		JLabel dLabel5 = new JLabel("데이터5");
		dLabel5.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		dLabel5.setBounds(12, 132, 200, 20);
		updatePanel.add(dLabel5);

		JLabel dLabel6 = new JLabel("데이터6");
		dLabel6.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		dLabel6.setBounds(12, 153, 200, 20);
		updatePanel.add(dLabel6);

		JLabel sLabel1 = new JLabel("속성1");
		sLabel1.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel1.setBounds(221, 48, 70, 20);
		updatePanel.add(sLabel1);

		JLabel sLabel2 = new JLabel("속성2");
		sLabel2.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel2.setBounds(221, 69, 70, 20);
		updatePanel.add(sLabel2);

		JLabel sLabel3 = new JLabel("속성3");
		sLabel3.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel3.setBounds(221, 90, 70, 20);
		updatePanel.add(sLabel3);

		JLabel sLabel4 = new JLabel("속성4");
		sLabel4.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel4.setBounds(221, 111, 70, 20);
		updatePanel.add(sLabel4);

		JLabel sLabel5 = new JLabel("속성5");
		sLabel5.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel5.setBounds(221, 132, 70, 20);
		updatePanel.add(sLabel5);

		JLabel sLabel6 = new JLabel("속성6");
		sLabel6.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sLabel6.setBounds(221, 153, 70, 20);
		updatePanel.add(sLabel6);

		JTextField sTf1 = new JTextField();
		sTf1.setDropMode(DropMode.INSERT);
		sTf1.setBounds(295, 50, 177, 20);
		updatePanel.add(sTf1);
		sTf1.setColumns(10);

		JTextField sTf2 = new JTextField();
		sTf2.setDropMode(DropMode.INSERT);
		sTf2.setColumns(10);
		sTf2.setBounds(295, 71, 177, 20);
		updatePanel.add(sTf2);

		JTextField sTf3 = new JTextField();
		sTf3.setDropMode(DropMode.INSERT);
		sTf3.setColumns(10);
		sTf3.setBounds(295, 92, 177, 20);
		updatePanel.add(sTf3);

		JTextField sTf4 = new JTextField();
		sTf4.setDropMode(DropMode.INSERT);
		sTf4.setColumns(10);
		sTf4.setBounds(295, 113, 177, 20);
		updatePanel.add(sTf4);

		JTextField sTf5 = new JTextField();
		sTf5.setDropMode(DropMode.INSERT);
		sTf5.setColumns(10);
		sTf5.setBounds(295, 134, 177, 20);
		updatePanel.add(sTf5);

		JTextField sTf6 = new JTextField();
		sTf6.setDropMode(DropMode.INSERT);
		sTf6.setColumns(10);
		sTf6.setBounds(295, 155, 177, 20);
		updatePanel.add(sTf6);

		JButton dataAddBtn = new JButton("변경");
		dataAddBtn.setFocusable(false);
		dataAddBtn.setBounds(161, 202, 80, 35);
		updatePanel.add(dataAddBtn);

		JButton cancelBtn = new JButton("취소");
		cancelBtn.setFocusable(false);
		cancelBtn.setBounds(253, 202, 80, 35);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDialog.setVisible(false);
				updateDialog.dispose();
			}

		});
		updatePanel.add(cancelBtn);

		updateDialog.setSize(500, 300);
		updateDialog.setLayout(null);
		updateDialog.setLocationRelativeTo(this);
		updateDialog.setContentPane(updatePanel);
		updateDialog.setVisible(true);
	}

	// 판매
	private void sell(String bookName) {
		sellDialog = new JDialog(this, "판매", true);
		JPanel sellPanel = new JPanel();
		sellPanel.setLayout(null);

		JLabel sellBookLabel = new JLabel("책 판매");
		sellBookLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		sellBookLabel.setBounds(110, 10, 70, 30);
		sellPanel.add(sellBookLabel);

		JLabel sBookLabel = new JLabel("판매 책 이름");
		sBookLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sBookLabel.setBounds(12, 38, 251, 20);
		sellPanel.add(sBookLabel);

		JLabel sBookNameLabel = new JLabel(bookName);
		sBookNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		sBookNameLabel.setBounds(12, 58, 251, 20);
		sellPanel.add(sBookNameLabel);

		JLabel sellCntLabel = new JLabel("구매 수량");
		sellCntLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sellCntLabel.setBounds(12, 88, 70, 20);
		sellPanel.add(sellCntLabel);

		JLabel sellUserLabel = new JLabel("구매회원번호");
		sellUserLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		sellUserLabel.setBounds(12, 109, 100, 20);
		sellPanel.add(sellUserLabel);

		JTextField sellCntTf = new JTextField();
		sellCntTf.setDropMode(DropMode.INSERT);
		sellCntTf.setColumns(10);
		sellCntTf.setBounds(113, 90, 150, 20);
		sellPanel.add(sellCntTf);

		JTextField sellUserTf = new JTextField();
		sellUserTf.setDropMode(DropMode.INSERT);
		sellUserTf.setColumns(10);
		sellUserTf.setBounds(113, 111, 150, 20);
		sellPanel.add(sellUserTf);

		JLabel payLabel = new JLabel("결제방법");
		payLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		payLabel.setBounds(142, 131, 60, 20);
		sellPanel.add(payLabel);
		
		JComboBox comboBox = new JComboBox(new String[] { "현금", "카드" });
		comboBox.setBackground(Color.white);
		comboBox.setBounds(203, 131, 60, 20);
		comboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		comboBox.setFocusable(false);
		sellPanel.add(comboBox);

		JButton sellBtn = new JButton("판매");
		sellBtn.setFocusable(false);
		sellBtn.setBounds(56, 161, 80, 35);
		sellPanel.add(sellBtn);

		JButton cancelBtn = new JButton("취소");
		cancelBtn.setFocusable(false);
		cancelBtn.setBounds(148, 161, 80, 35);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sellDialog.setVisible(false);
				sellDialog.dispose();
			}

		});
		sellPanel.add(cancelBtn);

		sellDialog.setSize(300, 245);
		sellDialog.setLayout(null);
		sellDialog.setLocationRelativeTo(this);
		sellDialog.setContentPane(sellPanel);
		sellDialog.setVisible(true);
	}

	// 테이블 세팅
	public void setTable(DefaultTableModel model, String type) {
		String query = "";
		if (type.equals("책")) {
			query = "select 책번호, 제목, 저자, 출판사, 가격, 재고량 from 책";
		} else if (type.equals("회원")) {
			query = "select 회원번호,이름,주민번호,전화번호,가입일,마일리지 from 회원";
		} else if (type.equals("우수고객")) {
			// 우수고객 관련 저장프로시저 사용해야함
		} else if (type.equals("재고량부족 책목록")) {
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

	// 로그인
	private void login() {
		String query = "select 관리자코드,비밀번호 from 관리자";
		boolean ida = false;
		boolean pwa = false;
		DB_Conn dbc = new DB_Conn();
		try {
			Statement stmt = dbc.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (adminCodeTf.getText().equals(rs.getString(1))) {
					ida = true;
				}
				if (adminPwTf.getText().equals(rs.getString(2))) {
					pwa = true;
				}
				if (ida && pwa) {
					adminLogin = true;
					Alert("로그인 성공", adminCodeTf.getText() + "(으)로 로그인이 되었습니다.");
					loginCheckLabel.setText("관리자 " + adminCodeTf.getText() + "님으로 접속되었습니다.");
					loginCheckLabel.setVisible(true);
					adminCodeLabel.setVisible(false);
					adminPwLabel.setVisible(false);
					adminPwTf.setVisible(false);
					adminCodeTf.setVisible(false);
					loginBtn.setVisible(false);
					break;
				}
			}
			if (!ida && !pwa) {
				Alert("로그인 실패", "계정 정보가 없습니다.");
			} else {
				if (!ida) {
					Alert("로그인 실패", "아이디가 틀렸습니다!");
				} else if (!pwa) {
					Alert("로그인 실패", "비밀번호가 틀렸습니다!");
				}
			}
			stmt.close();
			rs.close();
			dbc.con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 다이얼로그 알림창
	private void Alert(String alert_title, String alert_message) {
		// alert 메소드
		alertDialog = new JDialog(this, alert_title, true);
		JLabel lll = new JLabel(alert_message + "    ");
		JButton yes = new JButton("확인");
		yes.setFocusable(false);
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alertDialog.setVisible(false);
				alertDialog.dispose();
			}

		});
		lll.setVerticalTextPosition(SwingConstants.CENTER);
		lll.setHorizontalTextPosition(SwingConstants.CENTER);
		JPanel ttt = new JPanel();
		ttt.add(lll);
		ttt.add(yes);
		alertDialog.setLayout(null);
		alertDialog.setLocationRelativeTo(this);
		alertDialog.setSize(320, 90);
		alertDialog.setContentPane(ttt);
		alertDialog.setVisible(true);
	}
}

public class DBBookStore {
	// Main
	public static void main(String[] args) {
		JTableExample jt = new JTableExample();
	}
}
