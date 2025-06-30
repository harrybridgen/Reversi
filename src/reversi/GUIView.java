package reversi;

import javax.swing.*;
import java.awt.*;

public class GUIView implements IView {
	IModel model;
	IController controller;
	JLabel message1 = new JLabel();
	JFrame frame1 = new JFrame();
	JPanel panel1 = new JPanel();
	JPanel board1 = new JPanel();
	JPanel buttonPanel1 = new JPanel();
	JButton button1 = new JButton("Greedy AI (play white)");
	JLabel message2 = new JLabel();
	JFrame frame2 = new JFrame();
	JPanel panel2 = new JPanel();
	JPanel board2 = new JPanel();
	JPanel buttonPanel2 = new JPanel();
	JButton button2 = new JButton("Greedy AI (play black)");
	JButton button3 = new JButton("Restart");
	JButton button4 = new JButton("Restart");

	@Override
	public void initialise(IModel model, IController controller) {
		this.model = model;
		this.controller = controller;
		message1.setFont( new Font( "Arial", Font.BOLD, 20 ));
		board1.setLayout(new GridLayout(model.getBoardHeight(), model.getBoardWidth()));
		button1.addActionListener(e -> controller.doAutomatedMove(1));
		button3.addActionListener(e -> {
			model.clear(0);
			controller.startup();
		});
		buttonPanel1.setLayout(new BorderLayout());
		buttonPanel1.add(button1,BorderLayout.NORTH);
		buttonPanel1.add(button3,BorderLayout.SOUTH);
		panel1.setLayout(new BorderLayout());
		panel1.add(message1,BorderLayout.NORTH);
		panel1.add(board1, BorderLayout.CENTER);
		panel1.add(buttonPanel1,BorderLayout.SOUTH);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setResizable(false);
		frame1.add(panel1);
		frame1.setVisible(true);
		frame1.setTitle("Reversi - white player");
		message2.setFont( new Font( "Arial", Font.BOLD, 20 ));
		board2.setLayout(new GridLayout(model.getBoardHeight(), model.getBoardWidth()));
		button2.addActionListener(e -> controller.doAutomatedMove(2));
		button4.addActionListener(e -> {
			model.clear(0);
			controller.startup();
		});
		buttonPanel2.setLayout(new BorderLayout());
		buttonPanel2.add(button2,BorderLayout.NORTH);
		buttonPanel2.add(button4,BorderLayout.SOUTH);
		panel2.setLayout(new BorderLayout());
		panel2.add(message2,BorderLayout.NORTH);
		panel2.add(board2, BorderLayout.CENTER);
		panel2.add(buttonPanel2,BorderLayout.SOUTH);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setResizable(false);
		frame2.add(panel2);
		frame2.setLocationRelativeTo(frame1);
		frame2.setVisible(true);
		frame2.setTitle("Reversi - black player");
	}

	@Override
	public void refreshView() {
		board1.removeAll();
		for (int x = 0; x < model.getBoardWidth(); x++) {
			for (int y = 0; y < model.getBoardHeight(); y++) {
				boardCell cell = new boardCell(model.getBoardContents(x, y));
				int i = x;
				int j = y;
				cell.addActionListener(e -> controller.squareSelected(1, i, j));
				board1.add(cell);
			}
		}
		frame1.pack();
		board2.removeAll();
		for (int x = model.getBoardWidth() - 1; x >= 0; x--) {
			for (int y = model.getBoardHeight() - 1; y >= 0; y--) {
				boardCell cell = new boardCell(model.getBoardContents(x, y));
				int i = x;
				int j = y;
				cell.addActionListener(e -> controller.squareSelected(2, i, j));
				board2.add(cell);
			}
		}
		frame2.pack();
	}

	@Override
	public void feedbackToUser(int player, String message) {
		if (player == 1){
			message1.setText(message);
		}
		if (player == 2){
			message2.setText(message);
		}
	}
}

class boardCell extends JButton {
	int value;
	public boardCell(int value) {
		this.value = value;
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(80,80));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!(value == 0)) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(2));
			int x = (getWidth() - (getWidth() - 10)) / 2;
			int y = (getHeight() - (getHeight() - 10)) / 2;
			int width = (getWidth() - 10);
			int height = (getHeight() - 10);
			if (value == 1) {
				g2.setColor(Color.WHITE);
				g2.fillOval(x, y, width, height);
				g2.setColor(Color.BLACK);
				g2.drawOval(x, y, width, height);
			} else if (value == 2) {
				g2.setColor(Color.BLACK);
				g2.fillOval(x, y, width, height);
				g2.setColor(Color.WHITE);
				g2.drawOval(x, y, width, height);
			}
		}
	}
}