/*
 * CPSC 211: Lab 7
 */
package cs211.labs.cgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * A GUI for the RPN Calculator
 * 
 * Class Invariant:
 *   calculator != null
 */
public class CalculatorGUI implements ActionListener, KeyListener , MouseListener, MouseMotionListener{
    // timer for animating single key movement
    private static final int ANIMATION_DELAY = 10 ; // milliseconds
    // timer to start next key movement
	private static final int DELAY_BTWN_KEYMOVES = 50 * ANIMATION_DELAY;
	// wait before starting 
	private static final int INITIALDELAY = 3000;
	// The calculator for actually calculating!
    private RPNCalculator calculator;
    // The main frame for the GUI
    private JFrame frame;
    // The menubar for the GUI
    private JMenuBar menuBar;
    // the pane to hold operations functions for calculator
    private JPanel opKeysPane;
    // for the calculator display of stack and results
    private JPanel displayPane;
    private JTextArea display;
    // line 1 will hold the stack
    private String line1 ="";
    //line 2 will hold the current input information
    private String line2="";
    // left pane for highlighting of mouse entry
    private JPanel leftPane;
    private Color leftPaneColor;
    // status lable for any dynamic updates we want to throw at user
    private JLabel statusLabel;
    // the pane that will contain the number keys that will be animated.
    private JPanel movePanel; // panel containing number keys that will move.
    // the buttons or keys that will be defined.
    final String[] numbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
    final String[] operations = { "+", "-", "*", "/" };
    // list of buttons for internal access
    private List<JButton> buttons;
    // for  randomization of colors and or directions of movement
    private Random rangen;
    // timer for single button animation
    private Timer myTimer;
    // pointer to blankButton that will be the target of animation
    private JButton blankButton; 
    // timer for interbutton moves.
    private Timer manyMoves;
    // how many times the user has requested a stop for the animation
    private int stopCount = 0;
    // timer for color changes
    private Timer cMode;
    // the next button to change the color of for color animation
    private int nextButton = 0;
    
    /**
     * Constructor
     */
    public CalculatorGUI() {
        // Initialize the calculator
        calculator = new RPNCalculator();
        buttons = new ArrayList<JButton>();
        rangen = new Random();
        theCal = Calendar.getInstance();
    }

    /**
     * Initialize and display the calculator
     */
    public void showCalculator() {
    	// create timers for animition of a single key, multiple keys and color changes
    	myTimer = new Timer(ANIMATION_DELAY, new TimerAction());
    	manyMoves = new Timer(DELAY_BTWN_KEYMOVES,new ManyMoves());
    	cMode = new Timer(ANIMATION_DELAY*10, new colorMarch());
    	
        // Create the main GUI components
        frame = new JFrame("RPN Scrambler Calc");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new JPanel(new GridLayout(4, 3));
        opKeysPane = new JPanel(new GridLayout(4, 1));
        displayPane = new JPanel(new GridLayout(1,1));
        leftPane = new JPanel();
        JPanel statusPanel = new JPanel();
        movePanel = new JPanel();
        
        statusLabel = new JLabel("Start Clicking!");
        statusPanel.add(statusLabel);
        
        
        initializeMenu();
        initializeOpKeyPad();
        initializeDisplay();
        initializeLeftPane();
      
        // Create the components to go into the frame and
        // stick them in a container named contents
        frame.getContentPane().add(opKeysPane, BorderLayout.LINE_END);
        frame.getContentPane().add(displayPane, BorderLayout.NORTH);
        frame.getContentPane().add(leftPane,BorderLayout.WEST);
        frame.getContentPane().add(statusPanel,BorderLayout.SOUTH);
        frame.getContentPane().add(movePanel, BorderLayout.CENTER);
        // Finish setting up the frame, and show it.
        frame.pack();       
  
        frame.setVisible(true);
        initializeMovePanel();
		// movePanel initialed later in attempt to allow other components to get their prefered sizing in put first.

		// for implementation of mouseEvents on the Frame
		frame.addMouseListener( this);
		frame.addMouseMotionListener(this);
		// set and start the repeated key movements
		manyMoves.setInitialDelay(INITIALDELAY);
		manyMoves.start();
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.MONTH) >= Calendar.NOVEMBER){
			cMode.start();
			Calendar  christmas = Calendar.getInstance();
			christmas.set(cal.get(Calendar.YEAR),Calendar.DECEMBER,25);
//			System.out.println( christmas.getTime());
//			System.out.println(cal.getTime());
			long christmasDay =christmas.getTimeInMillis();
			long now = cal.getTimeInMillis();
			long delta = christmasDay - now;
			long diffDays = delta / (24 * 60 * 60 * 1000);
			int diffdaysI = (int) diffDays;
			if (diffdaysI>0){
				statusLabel.setText("" + diffdaysI + " days  to Christmas");
			}else{
				statusLabel.setText("Merry Christmas");
			}
		}
    }
    

    /**
     * Initialize the panel that will contain animated keys moving at random 
     * Had problems reading container size, manually fixed key sizes for now but 
     * need to find out about how to sense parent window resize and dynamically
     * adjust key sizes accordingly.
     */
    private void initializeMovePanel() {
    	// using customer layout manager 
    	movePanel.setLayout(new PadLayout());  
		movePanel.setBorder(new BevelBorder(0, Color.blue, Color.blue)); 
		JButton button;
		// create buttons
    	for (int i = 0; i < numbers.length; i++) {
             button = new JButton(numbers[i]);
             button.addActionListener(this);
             button.addMouseListener(this);  // stop animation on mouse enter
             button.setVisible(true);
             
             movePanel.add(button);
             buttons.add(button);
         }
        // add enter button
        button = new JButton("Enter");
        movePanel.add(button);
        button.addActionListener(this);
        button.addMouseListener(this);
        buttons.add(button);
        
        movePanel.setVisible(true);
        movePanel.addMouseMotionListener(this);  // allow mouse tracking over movepanel
//        movePanel.addHierarchyBoundsListener(new movePanelResizeListener());
        // this works but it was simpler to use own layout manager than to build auto resizing based on this.
        
        // add the blank button that will move swap positions with the other keys
        blankButton = new JButton("Blank");
        buttons.add(blankButton);
        blankButton.addActionListener(this);  // clicking blank button to have it animate
        blankButton.addMouseListener(this);
        movePanel.add(blankButton);
        blankButton.setBackground(Color.WHITE); // make it blank
        blankButton.setForeground(Color.WHITE); // make text blank
        blankButton.setVisible(true); // has to be "true" in order to receive clicks
        // #TODO there seems to be something in the false that triggers bad logic in button decisions of which 
        // button to move.  it moves buttons on top of other buttons for some reason.
        // may be the visible not reporting a size or position parameter that we can use to calculate neighbours?
	}

    /**
     * find a list of neighbours to the blank key.
     * neighbour must be one of the keys in the movePanel to be a candidate for swapping positions.
     * @return list of candidates to move
     */
    private List<JButton> getNeighbours(){
    	List<JButton> result = new ArrayList<JButton>();
    	Component north = movePanel.getComponentAt(blankButton.getX()+blankButton.getWidth()/2 , blankButton.getY() -  blankButton.getHeight()/2 );
    	if ((north!=null)&&(buttons.contains(north))) {
    		result.add((JButton) north);
    	}
    	Component south = movePanel.getComponentAt(blankButton.getX()+blankButton.getWidth()/2 , blankButton.getY() +  blankButton.getHeight()*3/2 );
    	if ((south!=null)&&(buttons.contains(south))){
    		result.add((JButton) south);
    	}
    	Component west = movePanel.getComponentAt(blankButton.getX()+blankButton.getWidth()/2 - blankButton.getWidth(), blankButton.getY() +  blankButton.getHeight()/2 );
    	if ((west!=null)&&(buttons.contains(west))){
    		result.add((JButton) west);
    	}
    	Component east = movePanel.getComponentAt(blankButton.getX()+blankButton.getWidth()/2 + blankButton.getWidth(), blankButton.getY() +  blankButton.getHeight()/2 );
    	if ((south!=null)&&(buttons.contains(east))){
    		result.add((JButton) east);
    	}
//    	String showList ="";
//    	for (JButton button : result){
//    		showList += button.getText();
//    	}
//    	statusLabel.setText(showList);
// note that the above appears to be triggering calls to PadLayout.LayoutContainer which causes a reset in positions.
    	return result;
    }
    

    /**
     * initiate swap of buttons.  Fist button is expected to be the blank button and the second, one of the candidate keys.
     * @param A
     * @param B
     */
    public void swapPosition(JButton A , JButton B){
    	// following globals are read by myTimer.ActionListener.actionPerformed.
    	whereTo = A.getBounds();
    	buttonToMove = B;
    	
    	A.setBounds(B.getBounds());// just move the blankbutton 
    	//    	System.out.println("B = " + B.getText() + "pos = " + B.getBounds());
//    	System.out.println("A Pos = " + A.getBounds());
//   		System.out.println("deltaX" + deltaX);
   		myTimer.start(); // start firing animation timer to move the target button... triggers myTimer.actionListener.actionPerformed.
   		// this in turn calls move button (below) repeatedly according to timer settings.
    }
    

    /**
     * each call to moveButton will move it one increment closer to target position.
     * When we arrive at target position, will automatically stop timer.
     * Note, interupting calls to layoutContainer can have wierd effects while
     * button is in transition to target position.
     */
    private void moveButton(){
    	int targX = whereTo.x;
    	int targY = whereTo.y;
    	int curX = buttonToMove.getX();
    	int curY = buttonToMove.getY();

    	int ht = buttonToMove.getHeight();
    	int wt = buttonToMove.getWidth();
    	Rectangle newBound = new Rectangle (curX,curY,wt,ht	);
    	if (buttonToMove.getBounds().getLocation().equals(whereTo.getLocation())){
    		myTimer.stop();
    	}else{
    		if (curX !=targX){	
    			//    			System.out.println("deltaX = " +i);
    			if (targX>curX) {
    				newBound.setLocation(++curX , curY);
    			}else{
    				newBound.setLocation(--curX , curY);
    			}
    		}
    		if (curY != targY){
    			if (targY>curY){
    				newBound.setLocation(curX,++curY);
    			}else{
    				newBound.setLocation(curX,--curY);
    			}
 	
    		}
    		buttonToMove.setBounds(newBound);
			//itemToMove.setVisible(true);  
    	}

    }
    
    
    /**
     * entry point for single button animation.
     */
    public void moveBlank(){
    	List<JButton> candidates = getNeighbours();
    	int pickOne = rangen.nextInt(candidates.size());
    	swapPosition (blankButton, candidates.get(pickOne));
    }
    
    
    /**
     * nothing added here, but is used as a color
     * indicator for when mouse is in frame as test
     * of mouseListener
     */
	private void initializeLeftPane() {
		// TODO Auto-generated method stub
	
	}

    /**
     * initialize the calculator display
     */
	private void initializeDisplay() {
    	display = new JTextArea(6,20);
    	display.setVisible(true);
    	display.setEditable(true);
		//display = new JTextArea(2,1);
		//display.setLineWrap(true);

		JScrollPane scrollDisplay = new JScrollPane(display);
		scrollDisplay.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollDisplay.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		// for the keyboard entry of numbers and operations
		display.addKeyListener(this);
		display.setFocusable(true);
		displayPane.add(scrollDisplay);
		
		// to prevent local echo of keyboard input
		display.setEditable(false);
		display.setVisible(true);
		display.addMouseListener(this);
		display.addMouseMotionListener(this);
	}

	/**
	 * place the operations keys on the right hand side
	 */
	private void initializeOpKeyPad() {
        JButton button;
        for (int i = 0; i < operations.length; i++) {
            button = new JButton(operations[i]);
            button.addActionListener(this);
            opKeysPane.add(button);
            buttons.add(button);
        }
	}


	/**
	 * Key loop for events to trigger calculator actions.
	 */
	public void actionPerformed(ActionEvent e) {
		String input = e.getActionCommand() ;

//		System.out.println("src =  " + e.getSource() + " cmd = " + input );
//		if (!input.equals("Blank")){
//			
//			//Color bg = new Color(rangen.nextInt(256),rangen.nextInt(256),rangen.nextInt(256));
//			int buttonindex = buttons.indexOf(e.getSource()) ;
////			System.out.println("button index = " + buttonindex) ;
////			System.out.println(" color of button" + buttons.get(buttonindex).getBackground());
////			//buttons.get(buttonindex).setBackground(bg);
////			System.out.println( buttonindex + " button bounds =  " + buttons.get(buttonindex).getBounds());
//		}
		

		statusLabel.setText(e.getActionCommand());
		if ((!input.equals("Enter"))&&(!calculator.isOperator(input))){
			// have a digit to update line1.
			//    		System.out.println(input);
			if (input.equals("Blank")){
//				System.out.println("Blank");
				moveBlank();
			}else{
				if (calculator.getTopStack()==null){
					line1 = "";
				}else{
					//line1 = calculator.getTopStack();
					line1 = calculator.getStack();
				}
				line2 = line2 + e.getActionCommand().toString();
				display.setText( line1 +"\n" + line2 );

				// required to see the newlyset text
				display.setVisible(true);
			}
		}else {
			// load the digits as a string -> number
			try{// enter or command, load digits as a number
				// enter key may have line2 empty or may have number
				// in line2 and user entered operation.
				if (line2!="") calculator.performCommand(line2);
				// if operator, execute, otherwise done.
				if (calculator.isOperator(input)){
					calculator.performCommand(input);
				}

				line1 = calculator.getStack();
				line2 = "";
				display.setText(line1 +"\n" + line2 );

			} catch (Exception err){
				errorPopup(err.getClass().toString());
				display.setText(null);
				err.printStackTrace(System.out);
				System.out.println(err.getClass());
			}
		}
	}
    	

    /**
     * Initialize the menu for the GUI
     */
    private void initializeMenu() {
        JMenu menu;
        JMenuItem menuItem;
        
        // Create a menu 
        menu = new JMenu("Calculator");
        menuItem = new JMenuItem("Clear");  // clear stack and entries in display
        menuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calculator.clear();
				display.setText(null);
			}
        });
        // stop animation
        menu.add(menuItem);
        menuItem = new JMenuItem("STOP!");
        menuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				manyMoves.stop();
				stopCount++;
				manyMoves.setInitialDelay(INITIALDELAY*stopCount);
				manyMoves.start(); // pretty funny huh?  should add random element to delay for better effect
			}
        });
        menu.add(menuItem);
        // twinkly lights
        JMenuItem cMode = new JMenuItem("Christmas Mode");
        cMode.addActionListener(new ActionListener() {
        	// toggle on/off
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getCmode().isRunning()){
					getCmode().stop();
					System.out.println("stopping cmode");
				}else{
					getCmode().start();
					System.out.println("starting cmode");
				}
			}       	
        });
        menu.add(cMode);
        
        // quit item
        menuItem = new JMenuItem("Quit");
        // When quit is selected, destroy the application
        menuItem.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                // A trace message so you can see this
                // is invoked.
                System.err.println("Close window");
                frame.setVisible(false);
                frame.dispose();
            }
        });
        menu.add(menuItem);
        
        // create second menu
        JMenu menu2 = new JMenu("Help");
        // about item in second menu
        JMenuItem menuItem2a = new JMenuItem("About");
        menuItem2a.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame,"CPSC211 Lab L1B\nCrazy Colors\nMouseEvents for color reset\nKeyboard Events for display input\nbutton events for display input\nScramble number keys afte every keystroke - I dare you to use me");

			}
        });
        menu2.add(menuItem2a);
        // hint item in second menu
        JMenuItem menuItem2b = new JMenuItem("Hint");
        menuItem2b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "keypad is still linked to correct keys");
			}
        	
        });
        // where is the current blank key location 
        menu2.add(menuItem2b);
        JMenuItem menuItemLocate = new JMenuItem("Blank?");
        menuItemLocate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, blankButton.getBounds()	);
			}
        });
        menu2.add(menuItemLocate);
        
        // Create the menu bar, add menus to menubar
        menuBar = new JMenuBar();
        menuBar.add(menu);
        menuBar.add(menu2);
        frame.setJMenuBar(menuBar);
    }
    
    /**
     * Helper method for displaying an error as a pop-up
     * @param message The message to display 
     */
    private static void errorPopup(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     *  this is for implementing keyevents in the frame.  We rely on the keys themselves
     *  to do the actions as all we do is send doClick messages to the matching key
     */
    
	@Override
	public void keyPressed(KeyEvent e) {
//		System.out.println ("keypress " + e.getKeyChar());
//		System.out.println(" " + KeyEvent.getKeyText(e.getKeyCode()));
    	String input = "" + e.getKeyChar() ;
    	
    	for (JButton button : buttons){
    		if ((button.getActionCommand().equals(input)|| 
    				(button.getActionCommand().equals(KeyEvent.getKeyText(e.getKeyCode())))) ){
    			button.doClick();
//    			System.out.println( "done click " + button.getActionCommand() );
    			//display.setText(null);
    		}

    	}
    	if (calculator.getTopStack()==null){
    		line1 = "";
    	}else{
    		line1 = calculator.getTopStack();
    	}

	}
    	
	// following are methods for implementing MouseEvents

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println ("keyrel " + e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println ("keytype " + e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (display.hasFocus()){
			labelBuffer = statusLabel.getText();
			statusLabel.setText("Ready For KeyBoard Input");
		}else{
			statusLabel.setText(labelBuffer);
		}
		
	}
	
	// holder for current label which we overwrite with location until we exit 
	// note - I think this method is triggering calls to layoutContainer and messing up animations
	private String labelBuffer;
	@Override
	public void mouseEntered(MouseEvent e) {
		manyMoves.stop();
		cMode.stop();
		labelBuffer = statusLabel.getText();
		statusLabel.setText(e.getLocationOnScreen().toString());
//		System.out.println(e.getSource());
		leftPaneColor = leftPane.getBackground();
		leftPane.setBackground(Color.PINK);
//		for(JButton butt: buttons){
//			if (!butt.getText().equals("Blank") ){
//	//			butt.setBackground(Color.LIGHT_GRAY);
//			}
//		}
		
	}
	// indicator of mouse in frame
	@Override
	public void mouseExited(MouseEvent e) {
		statusLabel.setText(labelBuffer);
		leftPane.setBackground(leftPaneColor);
		manyMoves.start();
		cMode.start();
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
//		System.out.println(e.getButton());
//		System.out.println(e.getModifiers());
//		System.out.println(e.getModifiersEx());
//		System.out.println(e.isAltDown());
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		statusLabel.setText("Dragged = " + "(" + e.getLocationOnScreen().x + "," + e.getLocationOnScreen().y+ ")" );
		
	}

	/**
	 * show the current mouse position in the status label.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		statusLabel.setText("Moved = " + "(" + e.getLocationOnScreen().x + "," + e.getLocationOnScreen().y+ ")" );		
	}

	// locals to keep track of where the current button is being moved to and which one is current button
	private Rectangle whereTo;
	private JButton	buttonToMove;
	
	/**
	 * nested class to receive timer events for animation of single key movement
	 * @author rlum
	 *
	 */
	public class TimerAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			 moveButton(); // one animation frame per call
//			 movePanel.repaint();
//			 buttonToMove.repaint();
//			 frame.repaint();
		}
	}
	
	/**
	 * nested class to receive timer events for retriggering of next keymovement
	 * @author rlum
	 *
	 */
	public class ManyMoves implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!myTimer.isRunning()){
				moveBlank(); // one key movement animation cycle per event.
				// if its still running, skip the current call.
			}
		}
		
	}
	/**
	 * local class to time the changing of key colors
	 * @author rlum
	 *
	 */

	Calendar theCal;
	public class colorMarch implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {

			theCal = Calendar.getInstance();
			if (theCal.get(Calendar.MINUTE)%4 == 0 ){	
				marchColor(); // one key movement animation cycle per event.
			}else if (theCal.get(Calendar.MINUTE)%4 == 1){
				marchColor2();
			}else if  (theCal.get(Calendar.MINUTE)%4 == 2)  {
				marchColor3();
			}else {
				marchColor4();
			}
		}
	}

	
	/**
	 * attempt at implementing resizing without writing own layout. works partially but
	 * never quite gets right fill factor. Missing the layout manager ties.
	 * Deprecated in favor of layout manager.
	 * @author rlum
	 *
	 */
//	public class movePanelResizeListener implements HierarchyBoundsListener{
//
//		@Override
//		public void ancestorMoved(HierarchyEvent arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void ancestorResized(HierarchyEvent arg0) {
////			// stop animation, otherwise we get funny placement while resizing
//////			myTimer.stop();
//////			manyMoves.stop();
////			
////			int prefButtonWidth = (movePanel.getSize().width)/4 ;
////	    	int prefButtonHt = (movePanel.getSize().height) /6;
////	    	System.out.println("resize event detected");
////	    	System.out.println("movepanel.getsize = " + movePanel.getSize());
////	    	System.out.println("movepane.getprefsize = " + movePanel.getPreferredSize());
////	    	System.out.println(movePanel.getInsets());
////	    	System.out.println(movePanel.getBorder().getBorderInsets(movePanel));
////
////	    	for(JButton button : buttons){
////	 //   		if (movePanel.isAncestorOf(button)){
////	    			int currX = button.getBounds().x;
////	    			int currY = button.getBounds().y;
////	    			int currW = button.getBounds().width;
////	    			int currH = button.getBounds().height;
////	    			int currRow = currY / currH;
////	    			int currCol = currX / currW;	
////	    			Rectangle newRect = new Rectangle (currCol*prefButtonWidth, currRow*prefButtonHt, prefButtonWidth, prefButtonHt	);
////	    			
//////	    			button.setSize(new Dimension (prefButtonWidth, prefButtonHt));
////	    			button.setBounds(newRect);
////	    			button.repaint();
////	    			button.revalidate();
////	    //		}
////	    	}
////	    	((CPanel)movePanel).setPerferedButtonSize(new Dimension(prefButtonWidth,prefButtonHt));
//	    	
//	    	
////	    	// restart animation
////	    	myTimer.start();
////	    	manyMoves.start();
//		}
//	}
	
	/**
	 * randomly change the key color 
	 * consider developing patterns or allowing composition of patterns.. eg marquee effects, waves, etc.
	 */
	public void marchColor(){
		Color color = new Color (rangen.nextInt(255),rangen.nextInt(255),rangen.nextInt(255) );
		JButton theButton = buttons.get(nextButton);
		if (!theButton.getText().equals("Blank")) theButton.setBackground(color);
		if (nextButton+1 < buttons.size()){
			nextButton++;
		}else{
			nextButton=0;
		}
	}
	
	public void marchColor2(){
		Color color = new Color (rangen.nextInt(255),rangen.nextInt(255),rangen.nextInt(255) );
		JButton theButton = buttons.get(nextButton);
		if (!theButton.getText().equals("Blank")) theButton.setBackground(color);
		if (nextButton+1 < buttons.size()){
			if (!theButton.getText().equals("Blank")) {
				if (nextButton>0){
					theButton.setBackground(buttons.get(nextButton-1).getBackground());
				}else{
					theButton.setBackground(color);
				}
			}
			nextButton++;
		}else{
			nextButton=0;
		}
	}
	
	private int stepCount = 0;
	
	public void marchColor3(){
		Color color = new Color (rangen.nextInt(255),rangen.nextInt(255),rangen.nextInt(255) );
		if (stepCount++ > 100){
			stepCount = 0;
			for (JButton button : buttons){
				if (!button.getText().equals("Blank"))  button.setBackground(color);
			}
		}
		
	}
	
	private int rColor = 0;
	private int gColor = 0;
	private int bColor =0;
	public void marchColor4(){
		int whichColor = rangen.nextInt(4);
		if (whichColor%3 == 0){
			if (rColor<254)	rColor++;		
		}else if (whichColor%3==1){
			if (gColor<254)	gColor++;
		}else {
			if (bColor<254)	bColor++;
		}
		
		Color color = new Color(rColor,gColor,bColor);
		for (JButton button : buttons){
				if (!button.getText().equals("Blank"))  button.setBackground(color);
			}
		if (rColor >= 254) rColor = 0;
		if (gColor >= 254) gColor = 0;
		if (bColor >= 254) bColor = 0;
	}
		
	
	
	public Timer getCmode(){
		return cMode;
	}
}
