/**
 Simple Gaming Framework

 @author  Rimantas.Vaicekauskas@mif.vu.lt
 @version 1.01

 ===========================================================
 1) Jokios teisės nesaugomos (Copyleft)
 2) Autorius nėra atsakingas už šios programos veikimą.
 3) Autorius nėra atsakingas uš blogą programos, naudojančios
 žemiau pateiktus modulius įvertinimą.
 ============================================================

 Paleidimas: "java Game"

 Jūsų valdomas žaidėjas - klasės "SimulationObjectTest" objektas
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Test class for gaming object. */

class SimulationObjectTest
{
    private int yPosition = 0;
    private int xPosition = 0;

    public boolean update()
    {
        yPosition += 10;
        return true; // true if object is to be repainted
    }

    public void left()
    {
        if (xPosition > 0) xPosition -= 10;
    }

    public void right()
    {
        xPosition += 10;
    }

    /** Paint self */
    protected void paint(Graphics g, int sizeX, int sizeY)
    {
        if (sizeX > 0 && sizeY > 0) // If window is allready shown
        {
            g.drawString("I'm alive", xPosition % sizeX, yPosition % sizeY);
        }
    }
}

/** Simulation panel sample class.
 To be changed for particular simulation model or used as is */
class TestSimulationPanel extends BaseSimulationPanel implements KeyListener
{
    SimulationObjectTest simulationObject;
    public void setSimulationObject(SimulationObjectTest simulationObject)
    {
        this.simulationObject = simulationObject;
    }

    public TestSimulationPanel()
    {
        super();
    }

    /** Overriden.
     Update status of simulation model
     */
    protected boolean updateModel()
    {
        return this.simulationObject.update(); // Return true if panel must be repainted
    }

    /** Overriden
     Paint the panel acording to model state
     */
    protected void paintModel(Graphics g)
    {
        int sizeX  = this.getWidth ();
        int sizeY  = this.getHeight();

        // ! Note accessing data is not safe here !
        if (null != this.simulationObject)
        {
            simulationObject.paint(g, sizeX, sizeY);
        }
    }

    // Key listener
    // See  http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/KeyEvent.html
    // also http://java.sun.com/docs/books/tutorial/uiswing/events/keylistener.html

    public void keyTyped(KeyEvent e) { /*System.out.println(e);*/ }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e)
    {
        //System.out.println(e);
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
                //e.consume(); // ?
                this.simulationObject.left();
                break;
            case KeyEvent.VK_RIGHT:
                //e.consume(); // ?
                this.simulationObject.right();
                break;
        }
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) { /*System.out.println(e);*/  }

    // Other methods
}

/**
 Starter. Might be used as is or modified
 */
class Game
{
    public static void main(String[] args)
    {
        // Create frame and setup frame window
        JFrame frame = new JFrame();
        frame.setSize(600, 400);
        frame.setTitle("Game Demo: press arrows <-  ->");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and setup UI elements
        Container contentPane = frame.getContentPane();

        TestSimulationPanel panel = new TestSimulationPanel();
        panel.setRefreshTimeMs(100);
        panel.setSimulationObject(new SimulationObjectTest());
        frame.addKeyListener(panel); // to listen keyboard events

        contentPane.add(panel, BorderLayout.CENTER);
        // Here we can add other UI elements

        panel.start(); // Start simulation thread
        //frame.pack();
        frame.setVisible(true); // Show frame
    }
}

/**
 Base panel for simulation. Use as is.
 */
class BaseSimulationPanel extends JPanel implements Runnable
{
    private volatile Thread timerThread = null; // Not yet created

    public static final int DEFAULT_REFRESH_TIME_MS = 1000;
    private int refreshTimeMs = DEFAULT_REFRESH_TIME_MS;

    public void setRefreshTimeMs(int refreshTimeMs){ this.refreshTimeMs=refreshTimeMs;}

    public BaseSimulationPanel(){}

    /** About private. Do not call directly */
    public final void run()
    {
        while (timerThread != null)
        {
            try
            {
                Thread.currentThread().sleep(refreshTimeMs);
            }  catch (InterruptedException exc){}

            if (updateModel())
            {
                repaint(); // Canvas
            }
        }
    }

    /** To be overriden */
    protected boolean updateModel()
    {
        System.out.println("UpdateModel() is called");
        return true;
    }

    /** To be overriden */
    protected void paintModel(Graphics g)
    {
        System.out.println("PaintModel() is called");
    }

    /** Begin simulation */
    public final void start()
    {
        if (timerThread == null)
        {
            (timerThread = new Thread(this)).start();
        }
    }

    /** About to stop simulation */
    public final void stop()
    {
        timerThread = null;
    }

    /** Overriden */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        paintModel(g);
    }
}


