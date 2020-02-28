import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.event.*;

class LA_Conways extends JFrame implements ActionListener, ChangeListener, MouseListener, MouseMotionListener
{
    //Instance Variables
    Colony colony = new Colony(0.1);
    JSlider speedSldr = new JSlider ();
    Timer t;
    int ro, co, si, fil;
    JComboBox size, files;
    boolean populate, eradicate;

    public LA_Conways ()//Constructor to create the GUI attributes
    {
        // 1... Create/initialize components

        //Create JButtons
        JButton simulateBtn = new JButton ("Simulate");
        JButton populate= new JButton ("Populate");
        JButton eradicate= new JButton("Eradicate");
        JButton load= new JButton("Load");
        JButton save= new JButton("Save");
        addMouseListener(this); //Add mouse listener
        simulateBtn.addActionListener (this); //Add action listener

        String [] s= new String[colony.getRow()/2];

        for(int x = 0; x < s.length; x ++ )
            s[x]= "" + x;

        //create file as String for JComboBox files
        String [] file= {"SaveFiles.txt", "sierpinskiTriangle.txt", "triangle.txt", "Patterns.txt"};

        //set the attributes of the JComboBox
        files= new JComboBox(file);
        files.setSelectedIndex(0);
        files.addActionListener(files);

        //set the attributes of the JComboBox
        size= new JComboBox(s);
        size.setSelectedIndex(0);
        size.addActionListener(size);

        size.addActionListener(this);//add ActionListener to size

        //add ActionListener to each JButton
        populate.addActionListener(this);
        eradicate.addActionListener(this);
        load.addActionListener(this);
        save.addActionListener(this);
        speedSldr.addChangeListener (this);



        // 2... Create content pane, set layout
        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel
        JPanel north = new JPanel ();
        north.setLayout (new FlowLayout ()); // Use FlowLayout for input area

        DrawArea board = new DrawArea (500, 500);
//        board.addMouseMotionListener(this);
        // 3... Add the components to the input area.

        north.add (simulateBtn);
        north.add (populate);
        north.add (eradicate);
        north.add (load);
        north.add (save);
        north.add (size);
        north.add (files);
        north.add (speedSldr);

        content.add (north, "North"); // Input area
        content.add (board, "South"); // Output area

        // 4... Set this window's attributes.
        setContentPane (content);
        pack ();
        setTitle ("Life Simulation Demo");
        setSize (600, 600);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.
    }

    public void stateChanged (ChangeEvent e)//method called stateChanged
    {
        if (t != null)
            t.setDelay (400 - 4 * speedSldr.getValue ()); // 0 to 400 ms
    }

    public void actionPerformed (ActionEvent e) //Finds out what was pressed
    {
        fil= files.getSelectedIndex();
        si= size.getSelectedIndex();

        if (e.getActionCommand ().equals ("Simulate"))
        {
            Movement moveColony = new Movement(colony);
            t = new Timer (200, moveColony);
            t.start (); //Start simulation
        }
        if (e.getActionCommand ().equals ("Populate"))
        {
            populate = true;
            eradicate= false;
        }
        if (e.getActionCommand ().equals ("Eradicate"))
        {
            eradicate= true;
            populate= false;
        }
        if (e.getActionCommand ().equals ("Load"))
        {
            try//try
            {
                if( fil == 0 )
                    colony.load("SaveFiles.txt");//load SaveFiles.txt
                if( fil == 1 )
                    colony.load("triangle.txt");//load triangle.txt
                if( fil == 2 )
                    colony.load("sierpinskiTriangle.txt");//load sierpinskiTriangle.txt
                if( fil == 3 )
                    colony.load("Patterns.txt");//load Patterns.txt
            }
            catch (IOException ex) {}
        }
        if (e.getActionCommand ().equals ("Save"))
        {
            try
            {
                colony.save(); //Save colony
            }
            catch (IOException ex) {}
        }


        repaint ();
    }

    public void mouseClicked(MouseEvent e) { // Respond to mouse click
        //determines which cell in the colony/grid is pressed
        co = (e.getX())/5 - 2;
        ro = (e.getY() - 2)/5 - 12;

        //determines which size is selected from the JComboBox
        si= size.getSelectedIndex();

        if(populate)//determines whether populate evaluates to true
            colony.populate(ro, co, si);//activate method populate
        if(eradicate)//determines whether eradicate evaluates to true
            colony.eradicate(ro, co, si);//activate method eradicate

    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    public void mouseDragged(MouseEvent e){

    }
    public void mouseMoved(MouseEvent e){

    }


    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)//constructor called DrawArea
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)//method called PaintComponent
        {
            colony.show (g);//activate method called show
        }
    }

    class Movement implements ActionListener//method called Movement
    {
        private Colony colony;//declare instant variable

        public Movement (Colony col)//constructor called Movement
        {
            colony = col;//colony is equal to col
        }

        public void actionPerformed (ActionEvent event)//method called actionPerformed
        {
            colony.advance ();//activate method advance
            repaint ();//repaint
        }
    }

    class Colony {

        //Instance Variables
        private boolean grid[] [];
        private Scanner sc;

        public Colony (double density)
        {
            grid = new boolean [100] [100]; //Create 100 x 100 2D grid

            //Loop through grid
            for (int row = 0 ; row < grid.length ; row++)
                for (int col = 0 ; col < grid[0].length ; col++)
                    grid [row] [col] = Math.random () < density;
        }

        public void show (Graphics g)
        {
            //Loops throughout the cells of the grid
            for (int row = 0 ; row < grid.length ; row++)
                for (int col = 0 ; col < grid [0].length ; col++)
                {
                    if (grid [row] [col]) //If there's life
                        g.setColor (Color.black); //Set color to black
                    else
                        g.setColor (Color.white); //By default set color to white
                    g.fillRect (col * 5 + 2, row * 5 + 2, 5, 5); //Draw life form
                }
        }

        public int getCol() { //Public method that returns column
            return grid[0].length;
        }

        public int getRow() { //Public method that returns row
            return grid.length;
        }

        public boolean live (int row, int col)//method called live
        {
            // count number of life forms surrounding to determine life/death
            int factors= 0;

            for( int rows= row - 1; rows <= row + 1; rows ++ )
                for( int cols= col - 1; cols <= col + 1; cols ++ )
                    if( rows >= 0 && rows < grid.length - 1 && cols >= 0 && cols < grid[0].length - 1 )
                        if( rows != row || cols != col )
                            if( grid[rows][cols] )
                                factors ++;

            //Game of Life Rules
            if( factors == 2 && grid[row][col] ) //If there are two neighbour cells and cell is alive
                return true;

            else if( factors == 3 && grid[row][col]) //If there are three neighbour cells and cell is alive
                return true;

            else if( factors == 3 && grid[row][col] == false ) //If there are three neighbour cells and cell is dead
                return true;

            return false;

        }

        public void advance () //Public method that updates the life forms
        {
            boolean nextGen[] [] = new boolean [grid.length] [grid [0].length]; // create next generation of life forms
            for (int row = 0 ; row < grid.length ; row++)
                for (int col = 0 ; col < grid [0].length ; col++)
                    nextGen [row] [col] = live (row, col); // determine life/death status
            grid = nextGen; // update life forms
        }

        public void populate( int row, int col, int size ) //Public method that populates life forms
        {
            try
            {
                for(int rows = row - size; rows <= row + size; rows ++ )
                    for( int cols= col - size; cols < col + size; cols ++ )
                        if( rows >= 0 && rows < grid.length - 1 && cols >= 0 && cols < grid[0].length - 1 )
                            grid[rows][cols]= Math.random() < 0.75;

            } catch(Exception e){}//catch
        }

        public void eradicate( int row, int col, int size ) //Public method that eradicates life forms
        {
            try{
                for( int rows= row- size; rows <= row + size; rows ++ )
                    for( int cols= col - size; cols < col + size; cols ++ )
                        if( rows >= 0 && rows < grid.length - 1 && cols >= 0 && cols < grid[0].length - 1 )
                            grid[rows][cols]= Math.random() < 0.1;
            }
            catch(Exception e){}

        }

        public void load(String fi) throws IOException
        {
            try
            {
                File file= new File(fi);
                Scanner s= new Scanner(file);

                while(s.hasNextBoolean()) //While there are more boolean files to read
                    for( int row= 0; row < 100; row ++ )
                        for(int col= 0; col < 100; col ++ )
                            grid[row][col]= s.nextBoolean();
            }
            catch(IOException e){}
        }

        public void save() throws IOException
        {
            try
            {
                PrintWriter output= new PrintWriter("SaveFiles.txt");
                for( int row= 0; row < 100; row ++ )
                {
                    for( int col= 0; col < 100; col ++ )
                        output.print( "" + grid[row][col] + " ");
                    output.println();
                }
                output.close();
            }
            catch(IOException e)
            {
            }
        }

    }

    public static void main (String[] args)//main
    {
        LA_Conways window = new LA_Conways ();
        window.setVisible (true);
    }
}




