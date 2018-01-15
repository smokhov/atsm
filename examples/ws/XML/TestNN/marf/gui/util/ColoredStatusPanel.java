package marf.gui.util;

import java.awt.Color;
import java.awt.Graphics;

import marf.util.Debug;


/**
 * <p>Panel to be used as a status bar with color gradience.</p>
 *
 * TODO: refactor.
 *
 * $Id: ColoredStatusPanel.java,v 1.11 2007/12/18 03:46:08 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.11 $
 * @since 0.3.0.2
 */
public class ColoredStatusPanel
extends SmartSizablePanel
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 2835022627059977227L;

	/**
	 * Lower bound.
	 */
	protected int iLowerBound;

	/**
	 * Upper bound.
	 */
	protected int iUpperBound;

	/**
	 * Should be between lower and upper bounds.
	 */
	protected int iCurrentStatus;

	/**
	 * From 0% to 10% inclusively.
	 */
	public static final int RED_PERCENTAGE = 10;

	/**
	 * From 11% (RED_PERCENTAGE + 1) to 40% inclusively.
	 */
	public static final int YELLOW_PERCENTAGE = 40;

	/**
	 * From 41% (YELLOW_PERCENTAGE + 1) to 100% inclusively.
	 */
	public static final int GREEN_PERCENTAGE = 100;

	/**
	 * Construction.
	 *
	 * @param piX x position
	 * @param piY y position
	 * @param piWidth panel width
	 * @param piHeight panel height
	 * @param piLowerBound lower bound
	 * @param piUpperBound upper bound
	 * @param piCurrentStatus current status
	 */
	public ColoredStatusPanel
	(
		int piX,
		int piY,
		int piWidth,
		int piHeight,
		int piLowerBound,
		int piUpperBound,
		int piCurrentStatus
	)
	{
		super(piX, piY, piWidth, piHeight);
		//System.out.println("ColoredStatusPanel(): X=" + p_iX + ", Y=" + p_iY
		// + ", W=" + p_iWidth + ", H=" + p_iHeight);
		//System.out.println("Constructor: );
		setLowerBound(piLowerBound);
		setUpperBound(piUpperBound);
		setCurrentStatus(piCurrentStatus);
		//System.out.println("Constructor: Status=" + p_iCurrentStatus);
		setSize(piWidth, piHeight);

		Debug.debug("ColoredStatusPanel(): actual X = " + getX()
				+ ", actual Y=" + getY() + ", actual W=" + getWidth()
				+ ", actual H=" + getHeight() + ", LowerBound=" + piLowerBound
				+ ", UpperBound=" + piUpperBound + ", Status="
				+ piCurrentStatus);

		Debug.debug(this.getSize().width + " " + this.getSize().height);
	}

	/**
	 * Default panel as <code>ColoredStatusPanel(0, 0, 0, 0, 0, 100, 50)</code>.
	 * @see #ColoredStatusPanel(int, int, int, int, int, int, int)
	 */
	public ColoredStatusPanel()
	{
		this(0, 0, 0, 0, 0, 100, 50);
		Debug.debug("ColoredStatusPanel() - default constructor was called.");
	}

	/**
	 * Draws this panel.
	 * @param poGraphics underlying graphics subsystem reference
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics poGraphics)
	{
		Debug.debug(getSize().width + " " + getSize().height);
		setSize(getWidth(), getHeight());

		int i;

		for(i = 0; i < this.iUpperBound - this.iLowerBound; i++)
		{
			if(i >= 0 && i <= RED_PERCENTAGE)
			{
				poGraphics.setColor(new Color(0x99, 00, 00));
			}
			else if(i > RED_PERCENTAGE && i <= YELLOW_PERCENTAGE)
			{
				poGraphics.setColor(new Color(0xFF, 0xFF, 0x80));
			}
			else
				poGraphics.setColor(new Color(0, 0x80, 0));

			poGraphics.drawLine(i, 0, i, getHeight());
		}

		//System.out.println(iCurrentStatus + " " + iLowerBound);

		for(i = 0; i < this.iCurrentStatus - this.iLowerBound; i++)
		{
			if(i >= 0 && i <= RED_PERCENTAGE)
			{
				poGraphics.setColor(Color.red);
			}
			else if(i > RED_PERCENTAGE && i <= YELLOW_PERCENTAGE)
			{
				poGraphics.setColor(Color.yellow);
			}
			else
				poGraphics.setColor(Color.green);

			poGraphics.drawLine(i, 0, i, getHeight());
		}

		//this.resize(100,100);
		//	poGraphics.setColor(Color.white);
		//	  poGraphics.drawString("Where is my smart status???",10,10);
	}

	// Setters

	/**
	 * Sets lower bound.
	 * @param piLowerBound new lower bound
	 */
	public void setLowerBound(int piLowerBound)
	{
		this.iLowerBound = piLowerBound;
	}

	/**
	 * Sets upper bound.
	 * @param piUpperBound new upper bound
	 */
	public void setUpperBound(int piUpperBound)
	{
		this.iUpperBound = piUpperBound;
	}

	/**
	 * Sets current status to a new value and repaints.
	 * If the status values exceed boundaries, the status
	 * is aligned with the bounds instead.
	 * @param piCurrentStatus possible new current status
	 */
	public void setCurrentStatus(int piCurrentStatus)
	{
		if(piCurrentStatus < this.iLowerBound)
		{
			this.iCurrentStatus = this.iLowerBound;
		}
		else
		{
			if(piCurrentStatus > this.iUpperBound)
			{
				this.iCurrentStatus = this.iUpperBound;
			}
			else
			{
				this.iCurrentStatus = piCurrentStatus;
			}
		}

		repaint();
	}

	// Getters

	/**
	 * @return lower bound
	 */
	public int getLowerBound()
	{
		return this.iLowerBound;
	}

	/**
	 * @return upper bound
	 */
	public int getUpperBound()
	{
		return this.iUpperBound;
	}

	/**
	 * @return current status
	 */
	public int getCurrentStatus()
	{
		return this.iCurrentStatus;
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.11 $";
	}
}

// EOF
