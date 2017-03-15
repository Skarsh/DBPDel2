package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class LoadDriver {

	private static String ENTER = "Enter";
	static JButton enterButton;
	public static JTextArea output;
	public static JTextField input;
	static JFrame frame;
	static JPanel panel;
	public static String testString = "test";

	public static String kolonne1;
	public static String kolonne2;
	public static String kolonne3;
	public static String kolonne4;

	public static void main(String[] args) {
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			// handle the error
		}

		connect();
		//sporring();
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		createFrame();

	}

	private static Connection conn = null;
	private static void connect(){
		try{
			conn = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no/renatbec_trening?user=renatbec_dbprosj&password=dberbest");
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	private static Statement stmt = null;
	private static ResultSet rs = null;
	private static ResultSetMetaData rsmd = null;


	private static ArrayList<ArrayList<String>> tabell;
	private static ArrayList<String> rad;
	private static int rows = 0;

	/*
	public static ArrayList<String> sporring(String query){
		try{

			stmt = conn.createStatement();

			//String query = "SELECT * FROM TRENINGSOKT";

			if (stmt.execute(query)){

				rs = stmt.getResultSet();

				rsmd = rs.getMetaData();

				System.out.println("Antall tabell: " + rsmd.getColumnCount());

			}

			/*
			int count = 0;
			String kols [] = new String[rsmd.getColumnCount()];

			while(rs.next()){
				for (String s : tabell) {
					kols[count] = rs.getString(count + 1);
					if (count + 1 < rsmd.getColumnCount()) {
						output.append(kols[count] + " - ");
					} else {
						output.append(kols[count] + "\n");
					}
					count++;
				}
				count = 0;
			}



			tabell = new ArrayList<String>();

			while(rs.next()){
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					tabell.add(rs.getString(i + 1));
					if (i + 1 < rsmd.getColumnCount()) {
						output.append(tabell.get(i) + " - ");
					} else {
						output.append(tabell.get(i) + "\n");
					}

				}
				rows++;
			}

		}catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}
		printKolonner(tabell);
		return tabell;
	}

	*/

	public static ArrayList<ArrayList<String>> sporring(String query){
		try{

			stmt = conn.createStatement();

			//String query = "SELECT * FROM TRENINGSOKT";

			if (stmt.execute(query)){

				rs = stmt.getResultSet();

				rsmd = rs.getMetaData();

				System.out.println("Antall tabell: " + rsmd.getColumnCount());

			}

			tabell = new ArrayList<ArrayList<String>>();

			while(rs.next()){
				rad = new ArrayList<String>();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					rad.add(rs.getString(i + 1));
				}
				tabell.add(rad);
			}


		}catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}
		printTabell(tabell);
		return tabell;
	}


	public static void printTabell(ArrayList<ArrayList<String>> tabell){
		for(int i = 0; i < tabell.size(); i++){
			System.out.println("");
			output.append("\n");
			for (int j = 0; j < tabell.get(i).size(); j++){
				if ((j + 1) < tabell.get(i).size()) {
					System.out.print(tabell.get(i).get(j) + " - ");
					output.append(tabell.get(i).get(j) + " - ");
				}
				else {
					System.out.print(tabell.get(i).get(j));
					output.append(tabell.get(i).get(j));
				}
			}
		}
	}

	public static void insert(String query){
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}

	public void formatQuery(String command){

		if (command.equals("HENT NOTAT")){
			sporring("SELECT notat FROM OVELSE");
		}


	}

	public static void getReport(String command){

		ArrayList<ArrayList<String>> queryTable = new ArrayList<ArrayList<String>>();

		String vekt = "";

		if (command.equals("HENT RAPPORT")){
			queryTable = sporring("SELECT MAX(vekt) FROM STYRKE");
			vekt = queryTable.get(0).get(0);
			System.out.println("vekt :" + vekt);
		}
	}
	public static void createFrame()
	{
		frame = new JFrame("Database GUI");
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(true);
		ButtonListener buttonListener = new ButtonListener();
		output = new JTextArea(15, 50);
		output.setWrapStyleWord(true);
		output.setEditable(false);
		JScrollPane scroller = new JScrollPane(output);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel inputpanel = new JPanel();
		inputpanel.setLayout(new FlowLayout());
		input = new JTextField(20);
		enterButton = new JButton("Enter");
		enterButton.setActionCommand(ENTER);
		enterButton.addActionListener(buttonListener);
		input.setActionCommand(ENTER);
		input.addActionListener(buttonListener);
		DefaultCaret caret = (DefaultCaret) output.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		panel.add(scroller);
		inputpanel.add(input);
		inputpanel.add(enterButton);
		panel.add(inputpanel);
		frame.getContentPane().add(BorderLayout.CENTER, panel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.setResizable(true);
		input.requestFocus();
	}

	public static class ButtonListener implements ActionListener
	{

		public void actionPerformed(final ActionEvent ev)
		{

			if (!input.getText().trim().equals("")){
				String cmd = ev.getActionCommand();
				if (ENTER.equals(cmd)){
					sporring(input.getText());
					getReport(input.getText());
		//			insert(input.getText());		// FINN PÅ EN BEDRE MÅTE!! FÅR EXCEPTION //
					output.append("\n");
				}
			}
			input.setText("");
			input.requestFocus();
		}
	}
}