import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The Minesweeper class. Contains constructors, setters, and getters.
 * 
 * @author Bee
 */
public class Minesweeper {

	//the frame
	private JFrame frame;

	//the panels
	private JPanel gameBoard;
	private JPanel settingsPanel;

	//buttons and textfields
	private JButton newGame;
	private JTextField width, height, numMines;

	private JButton wBtn;
	private JButton hBtn;
	private JButton mBtn;

	//strings to store info
	private String h;
	private String w;
	private String m;

	// The number of rows and columns on the game board 
	private int rows, cols;

	private JButton[][] buttons;

	// The number of mines on the game board
	private int mines;

	// The number of coordinates that have been clicked so far
	private int clicked;

	// The game board
	private char[][] board;
	private boolean[][] mineBoard;

	// Is the game running?
	private boolean isRunning;

	/**
	 * Constructs a Minesweeper game.
	 */
	public Minesweeper() {

		isRunning = false;

		frame = new JFrame("Minesweeper");
		frame.setLayout(new BorderLayout());
		frame.setSize(new Dimension(600,400));
		frame.setLocation(0, 0);

		gameBoard = new JPanel(new GridLayout());
		settingsPanel = new JPanel(new GridLayout());

		//creates buttons and text fields
		newGame = new JButton("New Game");
		width = new JTextField();
		height = new JTextField();
		numMines = new JTextField();

		wBtn=new JButton("Width:");
		hBtn=new JButton("Height:");
		mBtn=new JButton("Mines:");

		width.setEditable(true);
		height.setEditable(true);
		numMines.setEditable(true);

		settingsPanel.add(newGame);
		settingsPanel.add(hBtn);
		settingsPanel.add(height);
		settingsPanel.add(wBtn);
		settingsPanel.add(width);
		settingsPanel.add(mBtn);
		settingsPanel.add(numMines);

		//adds functionality to "New Game" button
		newGame.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {                    

				//cleans the game board
				gameBoard.removeAll();

				h = height.getText();
				w = width.getText();
				m = numMines.getText();

				try{
					int hV=Integer.parseInt(h);
					int wV=Integer.parseInt(w);
					int mV=Integer.parseInt(m);

					//checks for valid numbers
					if((hV<=0)||(wV<=0)||(mV<=0)||(mV>(hV*wV))){
						JOptionPane.showMessageDialog(frame, "Invalid Input"); 
					}
					else{
						rows=hV;
						cols=wV;
						mines=mV;
						newGame(); //creates new game if all is good
					}
				}//try
				catch(NumberFormatException e){
					JOptionPane.showMessageDialog(frame, "Invalid Input");
				}//catch

			}//actionPerformed

		});//addActionListener


		//add components to the frame
		frame.add(settingsPanel, BorderLayout.NORTH);
		frame.add(gameBoard, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true); //activating visibility prior to some things may cause errors.

	} // Minesweeper


	/**
	 * Resets and creates a new game.
	 */
	private void newGame(){

		isRunning=true;
		clicked=0;

		board = new char[rows][cols];
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j <board[i].length; j++){
				board[i][j] = 'e'; //'e'=empty
			}
		}

		mineBoard = new boolean[rows][cols];
		Random gen = new Random();

		for(int i = 0; i < mines; i++){
			int randomRow = gen.nextInt(rows);
			int randomCol = gen.nextInt(cols);
			
			while(mineBoard[randomRow][randomCol] == true){
				randomRow = gen.nextInt(rows);
				randomCol = gen.nextInt(cols);
			}//while
			
			mineBoard[randomRow][randomCol] = true;
		}//for

		//set layout
		gameBoard.setLayout(new GridLayout(rows,cols));

		//create buttons
		buttons = new JButton[rows][cols];
		for(int i = 0; i < buttons.length;i++){
			
			for(int j = 0; j < buttons[i].length; j++){
				
				buttons[i][j] = new JButton();
				buttons[i][j].setPreferredSize(new Dimension(60, 60));
				gameBoard.add(buttons[i][j]);
				//adds functionality to buttons
				buttons[i][j].addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent ae) {
						
						for(int i = 0; i < buttons.length; i++){
							
							for(int j = 0; j < buttons[i].length; j++){
								
								if(ae.getSource().equals(buttons[i][j])){
									//disable the ability of being clicked to prevent exploit                                     
									buttons[i][j].setEnabled(false);
									click(i, j);
								}

							}
						}
					}

				});
			}
		}
		//updates board with new info
		gameBoard.updateUI();

	}//newGame

	/**
	 * Updates the game board.
	 */
	public void update() {

		if(isRunning){
			
			if((clicked + mines) == (rows * cols)){ //if player wins
				this.isRunning = false;
				JOptionPane.showMessageDialog(frame, "Victory!");
				clicked=0;

			}
			else{
				for(int i = 0; i < rows; i++){
					for(int j = 0; j < cols; j++){
						if(inBounds(i,j)){
							if(isAdjacent(i, j) && board[i][j] != 'c'){
								if(getNumAdjacentMines(i,j) == 0 && !mineBoard[i][j]){
									buttons[i][j].setEnabled(false);
									click(i, j);
									update();
								}else{
									board[i][j] = (char) ((char)getNumAdjacentMines(i,j) + 48);
									buttons[i][j].setText(Character.toString(board[i][j]));
								}
							}//if
						}//if
					}//for
				}//for
			}//else
			
		}//if(isRunning)

	} // update


	/**
	 * Handles clicked events of the selected button.
	 * 
	 * @param row The selected/clicked button's row number
	 * @param col The selected/clicked button's col number
	 */
	public void click(int row, int col) {

		if(isRunning){

			if(mineBoard[row][col] == true){//if player clicks a mine
				isRunning = false;
				buttons[row][col].setText("MINE");
				buttons[row][col].setBackground(Color.red);

				JOptionPane.showMessageDialog(frame, "Game Over!");

			}//if
			else{

				//removes the number of mines on the button and set color to white
				buttons[row][col].setText("");
				buttons[row][col].setBackground(Color.white);

				clicked++;
				board[row][col] = 'c'; //'c'=clicked
			}//else

			update();

		}
	} // click


	/**
	 * Returns true if and only if the coordinate is both in bounds and hasn't
	 * been already been clicked.
	 * 
	 * @param row The row number being checked
	 * @param col The column number being checked
	 * @return boolean Returns true if in bounds and not clicked and false otherwise
	 */
	private boolean inBounds(int row, int col) {

		if(row >= 0 && col >= 0 && row < this.board.length && col < this.board[0].length){
			return true;
		}//if
		return false;
	} // inBounds

	/**
	 * Gets the number of mines adjacent to the desired coordinate.
	 * 
	 * @param row The selected button's row number
	 * @param col The selected button's column number
	 * @return int Returns the number of mines next to the selected button
	 */
	private int getNumAdjacentMines(int row, int col) {

		int numMines = 0;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (row + i >= 0 && row + i < board.length && col + j >= 0 && col + j < board[0].length) {
					
					//if not itself and it contains a mine
					if (!(row+i==row && col+j==col)&&(mineBoard[row+i][col+j]==true)) {
						numMines ++;
					}//if
				}//if
			}//for
		}//for

		return numMines;
	} // getNumAdjacentMines


	/**
	 * Returns whether or not the coordinate is adjacent to a clicked tile.
	 * 
	 * @param row The selected button's row number
	 * @param cols The selected button's col number
	 * @return boolean Returns true if it is a neighbor and false otherwise. 
	 */
	public boolean isAdjacent(int row, int col){
		if(this.inBounds(row, col)){
			for(int i = row-1; i <= row+1; i++){
				for(int j = col-1; j <= col+1; j++){
					
					if(this.inBounds(i,j) && this.board[i][j] == 'c'){
						return true;
					}
				}
			}
		}
		return false;
	}//isAdjacent





	/**
	 * Sets the number of rows.
	 * 
	 * @param i The number of rows.
	 */
	public void setRows(int i){
		this.rows = i;
	}
	
	/**
	 * Sets the number of columns.
	 * 
	 * @param j The number of columns.
	 */
	public void setCols(int j){
		this.cols = j;
	}

	/**
	 * Sets the number of mines.
	 * 
	 * @param m The number of mines
	 */
	public void setMines(int m){
		this.mines = m;
	}

	/**
	 * Sets the number of clicks.
	 * 
	 * @param iClicked The clicked number input.
	 */
	public void setClicked(int iClicked){
		clicked=iClicked;
	}//setClicked



	/**
	 * Gets the number of rows.
	 * 
	 * @return int Returns the number of rows in the gameboard.
	 */
	public int getRows() {

		return this.rows;

	} // getRows

	/**
	 * Gets the number of columns.
	 * 
	 * @return int Returns the number of columns in the gameboard.
	 */
	public int getCols() {

		return this.cols;

	} // getCols



	/**
	 * Gets the number of mines.
	 * 
	 * @return int The total amount of mines.
	 */
	public int getMines(){
		return mines;
	}//getMines


	/**
	 * Gets the number of clicks.
	 * 
	 * @return int The total number of clicks
	 */
	public int getClicked(){
		return clicked;
	}//getClicked

	/**
	 * Tells if the game is running or not.
	 * 
	 * @return boolean Returns whether or not the game is currently running.
	 */
	public boolean isRunning() {

		return this.isRunning;

	} // isRunning



} // Minesweeper