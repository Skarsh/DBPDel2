package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class LoadDriver {

	private static String ENTER = "Enter";
	static JButton enterButton;
	public static JTextArea output;
	public static JTextField input;
	static JFrame frame;
	static JPanel panel;

	public static void main(String[] args) {
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			// handle the error
		}

		connect();
		//query();
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		createFrame();
		output.append("Skriv HJELP for hjelp.\n");
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

	private static ArrayList<ArrayList<String>> table;
	private static ArrayList<String> row;

	public static ArrayList<ArrayList<String>> query(String query){
		try{

			stmt = conn.createStatement();

			if (stmt.execute(query)){

				rs = stmt.getResultSet();

				rsmd = rs.getMetaData();

				//System.out.println("Antall table: " + rsmd.getColumnCount());

			}

			table = new ArrayList<ArrayList<String>>();

			while(rs.next()){
				row = new ArrayList<String>();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					row.add(rs.getString(i + 1));
				}
				table.add(row);
			}


		}catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
		}
		printTable(table);
		return table;
	}


	public static void printTable(ArrayList<ArrayList<String>> table){
		System.out.println("Printing table:");
		for(int i = 0; i < table.size(); i++){
			System.out.println("");
			//output.append("\n");
			for (int j = 0; j < table.get(i).size(); j++){
				if ((j + 1) < table.get(i).size()) {
					System.out.print(table.get(i).get(j) + " - ");
					//output.append(table.get(i).get(j) + " - ");
				}
				else {
					System.out.print(table.get(i).get(j));
					//output.append(table.get(i).get(j));
				}
			}
		}
	}

	public static void insert(String query){
		try {
			String inputs[] = query.split("INSERT ")[1].split(", ");
			String Date = inputs[0];
			String Start = inputs[1];
			String End = inputs[2];

			stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO `TRENINGSOKT` (`dato`, `starttidspunkt`, `slutttidspunkt`) VALUES ( '"+ Date + "', '" + Start + "', '" + End + "')");

		} catch (Exception e) {
			System.out.println("SQLException: " + e.getMessage());
			output.append("Make sure you write your input in the format 'INSERT YYYY-MM-DD, hh:mm:ss, hh:mm:ss'");
		}
	}

	public static void getNotat(){
		ArrayList<ArrayList<String>> queryTable = new ArrayList<ArrayList<String>>();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		//System.out.println(dateFormat.format(date));

		ArrayList<ArrayList<String>> exerciseInfo = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> notes = new ArrayList<ArrayList<String>>();

		exerciseInfo = query("SELECT trening_ID, dato FROM TRENINGSOKT WHERE dato >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 7 DAY) AND dato <= CURRENT_TIMESTAMP");

		String exerciseIds = "";

		for (int i = 0; i < exerciseInfo.size(); i++){
			if (i == 0){
				exerciseIds += "(" + exerciseInfo.get(i).get(0) + ",";
			}
			else if (i + 1 < exerciseInfo.size()) {
				exerciseIds += exerciseInfo.get(i).get(0) + ",";
			}
			else{
				exerciseIds += exerciseInfo.get(i).get(0) + ")";
			}

		}

		System.out.println();
		System.out.println("Liste med notater:");
		System.out.println(exerciseIds);

		notes = query("SELECT notat FROM OVELSE WHERE trening_ID IN " + exerciseIds);

		output.append("Notater fra treninger den siste uken" + "\n \n");
		//output.append();

		String exerciseDate = "";

		for (int i = 0; i < notes.size(); i++){
			output.append("Notat " + i + ": " + notes.get(i).get(0) + "\n");
		}
	}


	public static void getReport(){


		ArrayList<ArrayList<String>> queryTable = new ArrayList<ArrayList<String>>();

		String weight;
		String bestExerciseId;
		String bestTrainingId;
		String bestDato;
		String startTime;
		String endTime;

		queryTable = query("SELECT MAX(vekt) FROM STYRKE");

		//Henter vekt
		weight = queryTable.get(0).get(0);

		// Henter besteOvelseId
		queryTable = query("SELECT ovelse_ID FROM STYRKE WHERE vekt='" + weight + "'");
		bestExerciseId = queryTable.get(0).get(0);

		// Henter besteTrening_Id
		queryTable = query("SELECT trening_ID FROM OVELSE WHERE ovelse_ID ='" + bestExerciseId + "'" );
		bestTrainingId = queryTable.get(0).get(0);

		// Henter dato
		queryTable = query("SELECT DATO FROM TRENINGSOKT WHERE trening_ID ='" + bestTrainingId + "'");
		bestDato = queryTable.get(0).get(0);

		// Henter start- og slutTidspunkt
		queryTable = query("SELECT starttidspunkt, slutttidspunkt FROM TRENINGSOKT WHERE trening_ID ='" + bestTrainingId + "'");
		startTime = queryTable.get(0).get(0);
		endTime = queryTable.get(0).get(1);


		System.out.println("vekt :" + " "  + weight);
		System.out.println("besteOvelseId :" + " "  + bestExerciseId);
		System.out.println("besteTreningId : " + " " + bestTrainingId);
		System.out.println("besteDato : " + " " + bestDato);

		output.append("TIDENES TRENINGSOKT" + "\n");
		output.append("Vekt løftet: " + weight + "kg" + "\n");
		output.append("Dato : " + bestDato + "\n");
		output.append("Startet kl : " + startTime + "\n");
		output.append("Sluttet kl : " + endTime + "\n");
	}

	public static void formatQuery(String query) {
		if (query.equals("HENT RAPPORT")) {
			getReport();
		}else if (query.equals("HENT NOTAT")) {
			getNotat();
		}else if (query.split(" ")[0].equals("INSERT")) {
			insert(query);
		}else if (query.split(" ")[0].equals("SELECT")) {
			query(query);
		}else if (query.equals("HJELP")) {
			output.append("KOMMANDOER:\n"
					+ "HENT RAPPORT	Henter ut den beste øvelsen noensinne.\n"
					+ "HENT NOTAT	Henter ut notater fra tidligere øvelser.\n"
					+ "INSERT		Registrer en ny treningsokt på formatet YYYY-MM-DD, hh:mm:ss, hh:mm:ss.\n"
					+ "Du kan også skrive inn dine egene sql spørringer ved hjelp av SELECT kommandoen.\n");
		}
	}
	
	
	public static void createFrame() {
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

					formatQuery(input.getText());
					output.append("\n");
				}
			}
			input.setText("");
			input.requestFocus();
		}
	}
}