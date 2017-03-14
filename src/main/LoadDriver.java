package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

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



	public static void sporring(String query){
		try{

			stmt = conn.createStatement();

			//String query = "SELECT * FROM TRENINGSOKT";

			if (stmt.execute(query)){

				rs = stmt.getResultSet();

				rsmd = rs.getMetaData();

				System.out.println("Antall kolonner: " + rsmd.getColumnCount());

			}

			/*

				VELGER IKKE ALLE KOLONNENE,

			 */

		/*	int count = 0;

			for (int i = 1; i <= rsmd.getColumnCount(); ++i){
				while (rs.next()) {
					kolonne1 = rs.getString(i);
					//kolonne2 = rs.getString(2);
					//System.out.println(kolonne1 + " - " + kolonne2);
					output.append(kolonne1  + "\n");

					System.out.println("i = " + i);
					count++;
					System.out.println("Count = " + count);
				}

			}

		*/

		/*	int countTest = 0;
			while (rs.next() && countTest < 2) {
				kolonne1 = rs.getString(1);
				System.out.println("Kolonne1");
				kolonne2 = rs.getString(2);
				System.out.println("Kolonne2");
				kolonne3 = rs.getString(3);
				System.out.println("Kolonne3");
				kolonne4 = rs.getString(4);
				System.out.println("Kolonne4");
				output.append(kolonne1 + " - " + kolonne2 + " - " + kolonne3 + " - " + kolonne4 + "\n");

				countTest++;

				System.out.println(countTest);
			}
		*/
			String kolonner [] = new String[rsmd.getColumnCount()];

			int count = 0;

			while(rs.next()){
				for (String s : kolonner) {
					kolonner[count] = rs.getString(count + 1);
					if (count + 1 < rsmd.getColumnCount()) {
						output.append(kolonner[count] + " - ");
					} else {
						output.append(kolonner[count] + "\n");
					}
					count++;
				}
				count = 0;
			}


				//NÆRMER SEG, BRUK EN TABELL FOR Å LEGGE KOLONNENE I, OG SÅ ITERER OVER HVAR AV DE MED rs.next() ????? //
			/*	int count = 1;
				while (rs.next() && count <= rsmd.getColumnCount()) {
					kolonne1 = rs.getString(count);
					//kolonne2 = rs.getString(2);
					//System.out.println(kolonne1 + " - " + kolonne2);
					output.append(kolonne1  + "\n");

					System.out.println("i = " + count);

					count++;

				}
				*/

		}catch (SQLException e){
			System.out.println("SQLException: " + e.getMessage());
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
		/*	if (!input.getText().trim().equals(""))
			{
				String cmd = ev.getActionCommand();
				if (ENTER.equals(cmd))
				{
					output.append(input.getText());
					if (input.getText().trim().equals(testString)) output.append(" = " + testString);
					else output.append(" != " + testString);
					output.append("\n");
				}
			}
			input.setText("");
			input.requestFocus();

		*/

			if (!input.getText().trim().equals("")){
				String cmd = ev.getActionCommand();
				if (ENTER.equals(cmd)){
					sporring(input.getText());
					output.append("\n");
				}
			}
			input.setText("");
			input.requestFocus();
		}
	}
}
