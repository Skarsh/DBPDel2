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


	private static ArrayList<ArrayList<String>> tabell;
	private static ArrayList<String> rad;
	private static int rows = 0;


	public static ArrayList<ArrayList<String>> sporring(String query){
		try{

			stmt = conn.createStatement();

			//String query = "SELECT notat FROM OVELSE";

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
		System.out.println(tabell.size());
		for(int i = 0; i < tabell.size(); i++){
			System.out.println("");
			//output.append("\n");
			for (int j = 0; j < tabell.get(i).size(); j++){
				if ((j + 1) < tabell.get(i).size()) {
					System.out.print(tabell.get(i).get(j) + " - ");
					//output.append(tabell.get(i).get(j) + " - ");
				}
				else {
					System.out.print(tabell.get(i).get(j));
					//output.append(tabell.get(i).get(j));
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


	public static void getReport(){

		ArrayList<ArrayList<String>> queryTable = new ArrayList<ArrayList<String>>();

		String vekt = "";
		String besteOvelseId = "";
		String besteTreningId = "";
		String besteDato = "";
		String startTidspunkt = "";
		String sluttTidspunkt = "";

		queryTable = sporring("SELECT MAX(vekt) FROM STYRKE");

		//henter vekt
		vekt = queryTable.get(0).get(0);

		// Henter besteOvelseId
		queryTable = sporring("SELECT ovelse_ID FROM STYRKE WHERE vekt='" + vekt + "'");
		besteOvelseId = queryTable.get(0).get(0);

		// Henter besteTrening_Id
		queryTable = sporring("SELECT trening_ID FROM OVELSE WHERE ovelse_ID ='" + besteOvelseId + "'" );
		besteTreningId = queryTable.get(0).get(0);

		// Henter dato
		queryTable = sporring("SELECT DATO FROM TRENINGSOKT WHERE trening_ID ='" + besteTreningId + "'");
		besteDato = queryTable.get(0).get(0);

		// Henter start- og slutTidspunkt
		queryTable = sporring("SELECT starttidspunkt, slutttidspunkt FROM TRENINGSOKT WHERE trening_ID ='" + besteTreningId + "'");
		startTidspunkt = queryTable.get(0).get(0);
		sluttTidspunkt = queryTable.get(0).get(1);


		System.out.println("vekt :" + " "  + vekt);
		System.out.println("besteOvelseId :" + " "  + besteOvelseId);
		System.out.println("besteTreningId : " + " " + besteTreningId);
		System.out.println("besteDato : " + " " + besteDato);

		output.append("TIDENES TRENINGSOKT" + "\n");
		output.append("Vekt lÃ¸ftet: " + vekt + "kg" + "\n");
		output.append("Dato : " + besteDato + "\n");
		output.append("Startet kl : " + startTidspunkt + "\n");
		output.append("Sluttet kl : " + sluttTidspunkt + "\n");
	}
	
	
	public static void formatQuery(String query) {
		if (query.equals("HENT RAPPORT")) {
			getReport();
		}else if (query.equals("HENT NOTAT")) {
			//sikkert noe annet som skal inn her
			sporring("SELECT notat FROM OVELSE");
		}else if (query.split(" ")[0].equals("INSERT")) {
			insert(query);
		}else if (query.split(" ")[0].equals("SELECT")) {
			sporring(query);
		}else if (query.equals("HJELP")) {
			output.append("KOMMANDOER:\n"
					+ "HENT RAPPORT	Henter ut beste øvelsen fra den siste uken.\n"
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