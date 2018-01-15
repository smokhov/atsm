package marf.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * <p>ColoredStatusBox GUI component.</p>
 * 
 * TODO: complete implementation and documentation. 
 * 
 * $Id: ColoredStatusBox.java,v 1.2 2007/12/18 03:46:08 mokhov Exp $
 * 
 * @author Serguei Mokhov
 * @version $Revision: 1.2 $
 * @since 0.3.0.6
 */
public class ColoredStatusBox
extends JPanel
{
	/**
	 * 
	 */
	public static final String DEFAULT_LABEL = "UNSET";

	/**
	 * 
	 */
	protected JLabel oStatusLabel = null;

	/**
	 * @param poLayout
	 * @param pbDoubleBuffered
	 */
	public ColoredStatusBox(LayoutManager poLayout, boolean pbDoubleBuffered)
	{
		super(poLayout, pbDoubleBuffered);
		init();
	}

	/**
	 * @param poLayout
	 */
	public ColoredStatusBox(LayoutManager poLayout)
	{
		super(poLayout);
		init();
	}

	/**
	 * @param pbDoubleBuffered
	 */
	public ColoredStatusBox(boolean pbDoubleBuffered)
	{
		super(pbDoubleBuffered);
		init();
	}

	/**
	 * 
	 */
	public ColoredStatusBox()
	{
		super();
		init();
	}

	/**
	 * @param pstrLabel
	 * @param poColor
	 */
	public ColoredStatusBox(String pstrLabel, Color poColor)
	{
		init(pstrLabel, poColor, 70, 70);
	}

	/**
	 * @param pstrLabel
	 * @param poColor
	 * @param piWidth
	 * @param piHeight
	 */
	public ColoredStatusBox(String pstrLabel, Color poColor, int piWidth, int piHeight)
	{
		init(pstrLabel, poColor, piWidth, piHeight);
	}

	/**
	 * 
	 */
	protected void init()
	{
		init(DEFAULT_LABEL, Color.GRAY);
	}
	
	/**
	 * @param pstrLabel
	 * @param poColor
	 */
	protected void init(String pstrLabel, Color poColor)
	{
		init(pstrLabel, poColor, 70, 70);
	}

	/**
	 * @param pstrLabel
	 * @param poColor
	 * @param piWidth
	 * @param piHeight
	 */
	protected void init(String pstrLabel, Color poColor, int piWidth, int piHeight)
	{
		this.oStatusLabel = new JLabel(pstrLabel);
	
		this.oStatusLabel.setBackground(poColor);
		this.oStatusLabel.setPreferredSize(new Dimension(piWidth, piHeight));
		this.oStatusLabel.setOpaque(true);
		this.oStatusLabel.setBorder(BorderFactory.createEtchedBorder());
		this.oStatusLabel.setHorizontalAlignment(JLabel.CENTER);
		
		add(oStatusLabel);
	}
}

// EOF
