package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

	public static void main(String[] args) {
		try {
			// The newInstance() call is a work around for some
			// broken Java implementations

			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			// handle the error
		}


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

	public static void createFrame()
	{
		frame = new JFrame("Test");
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
		// enterButton.setEnabled(false);
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
		// Center of screen
		// frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
		input.requestFocus();
	}

	public static class ButtonListener implements ActionListener
	{

		public void actionPerformed(final ActionEvent ev)
		{
			if (!input.getText().trim().equals(""))
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
		}
	}
}