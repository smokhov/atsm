package marf.gui.util;

import java.awt.Panel;

import marf.util.Debug;


/**
 * <p>A GUI panel container that is capable of resizing itself.
 * </p>
 *
 * $Id: SmartSizablePanel.java,v 1.12 2007/12/18 03:46:08 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @since 0.3.0.2
 * @version $Revision: 1.12 $
 */
public class SmartSizablePanel
extends Panel
{
	/**
	 * The X position of the top-left corner. 
	 */
	protected int iX;

	/**
	 * The Y position of the top-left corner. 
	 */
	protected int iY;

	/**
	 * The width of the panel.
	 */
	protected int iWidth;

	/**
	 * The height of the panel.
	 */
	protected int iHeight;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 6046101142847554184L;

	/**
	 * Construction given parameters.
	 *
	 * @param piX X position of the top-left corner
	 * @param piY Y position of the top-left corner
	 * @param piWidth the desired width
	 * @param piHeight the desired height
	 */
	public SmartSizablePanel(int piX, int piY, int piWidth, int piHeight)
	{
		setX(piX);
		setY(piY);
		setWidth(piWidth);
		setHeight(piHeight);
		//setBounds(this.iX, this.iY, this.iWidth, this.iHeight);

		Debug.debug("SmartSizablePanel(): X=" + this.iX + ", Y=" + this.iY + ", W=" + this.iWidth + ", H=" + this.iHeight);
		Debug.debug("SmartSizablePanel(): Actual Size: W=" + getSize().width + ", H=" + getSize().height);
	}

	/**
	 * Equivalent to <code>SmartSizablePanel(0, 0, 0, 0)</code>.
	 */
	public SmartSizablePanel()
	{
		this(0, 0, 0, 0);
		Debug.debug("SmartSizablePanel(): default");
	}

	// Setters

	/**
	 * Allows setting new X position.
	 * The values of less than zero are reset to 0.
	 * @param piX the new X position
	 */
	public void setX(int piX)
	{
		if(piX < 0)
		{
			this.iX = 0;
		}
		else
		{
			this.iX = piX;
		}
	}

	/**
	 * Allows setting new Y position.
	 * The values of less than zero are reset to 0.
	 * @param piY the new Y position
	 */
	public void setY(int piY)
	{
		if(piY < 0)
		{
			this.iY = 0;
		}
		else
		{
			this.iY = piY;
		}
	}

	/**
	 * Allows setting new width of the panel.
	 * The values of less than zero are reset to 0.
	 * After setting the <code>setSize()</code> method is called.
	 * @param piWidth the new width of the panel
	 * @see java.awt.Component#setSize(int,int)
	 */
	public void setWidth(int piWidth)
	{
		if(piWidth < 0)
		{
			this.iWidth = 0;
		}
		else
		{
			this.iWidth = piWidth;
		}

		setSize(this.iWidth, this.iHeight);
	}

	/**
	 * Allows setting new height of the panel.
	 * The values of less than zero are reset to 0.
	 * After setting the <code>setSize()</code> method is called.
	 * @param piHeight the new height of the panel
	 * @see java.awt.Component#setSize(int,int)
	 */
	public void setHeight(int piHeight)
	{
		if(piHeight < 0)
		{
			this.iHeight = 0;
		}
		else
		{
			this.iHeight = piHeight;
		}

		setSize(this.iWidth, this.iHeight);
	}

	// Getters

	/**
	 * @return the current X position
	 */
	public int getX()
	{
		return this.iX;
	}

	/**
	 * @return the current Y position
	 */
	public int getY()
	{
		return this.iY;
	}

	/**
	 * @return the current width
	 */
	public int getWidth()
	{
		return this.iWidth;
	}

	/**
	 * @return the current height
	 */
	public int getHeight()
	{
		return this.iHeight;
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.12 $";
	}
}

// EOF
