package marf.gui.util;

import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


/**
 * <p>Utility panel class with 5-point margin border with title.</p>
 *
 * $Id: BorderPanel.java,v 1.10 2007/12/31 00:17:17 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.10 $
 * @since 0.3.0.1
 */
public class BorderPanel
extends JPanel
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 5958419285249288359L;

	/**
	 * Equivalent to JPanel(), except there is a titless border.
	 */
	public BorderPanel()
	{
		super();
		setBorderedTitle("");
	}

	/**
	 * Equivalent to JPanel(pbIsDoubleBuffered), except
	 * there is a titless border.
	 * @param pbIsDoubleBuffered if double-buffering enabled.
	 */
	public BorderPanel(boolean pbIsDoubleBuffered)
	{
		super(pbIsDoubleBuffered);
		setBorderedTitle("");
	}

	/**
	 * Equivalent to JPanel(poLayout), except
	 * there is a titless border.
	 * @param poLayout desired layout manager
	 */
	public BorderPanel(LayoutManager poLayout)
	{
		super(poLayout);
		setBorderedTitle("");
	}

	/**
	 * Equivalent to JPanel(poLayout, pbIsDoubleBuffered), except
	 * there is a titless border.
	 * @param poLayout desired layout manager
	 * @param pbIsDoubleBuffered if double-buffering enabled.
	 */
	public BorderPanel(LayoutManager poLayout, boolean pbIsDoubleBuffered)
	{
		super(poLayout, pbIsDoubleBuffered);
		setBorderedTitle("");
	}

	/**
	 * Equivalent to JPanel(), except there is a title.
	 * @param pstrBorderLabel title embedded into the border
	 */
	public BorderPanel(String pstrBorderLabel)
	{
		super();
		setBorderedTitle(pstrBorderLabel);
	}

	/**
	 * Equivalent to JPanel(pbIsDoubleBuffered), except there is a title.
	 * @param pstrBorderLabel title embedded into the border
	 * @param pbIsDoubleBuffered if double-buffering enabled.
	 */
	public BorderPanel(String pstrBorderLabel, boolean pbIsDoubleBuffered)
	{
		super(pbIsDoubleBuffered);
		setBorderedTitle(pstrBorderLabel);
	}

	/**
	 * Equivalent to JPanel(poLayout), except there is a title.
	 * @param pstrBorderLabel title embedded into the border
	 * @param poLayout desired layout manager
	 */
	public BorderPanel(String pstrBorderLabel, LayoutManager poLayout)
	{
		super(poLayout);
		setBorderedTitle(pstrBorderLabel);
	}

	/**
	 * Equivalent to JPanel(poLayout, pbIsDoubleBuffered), except there is a title.
	 * @param pstrBorderLabel title embedded into the border
	 * @param pbIsDoubleBuffered if double-buffering enabled.
	 * @param poLayout desired layout manager
	 */
	public BorderPanel(String pstrBorderLabel, LayoutManager poLayout, boolean pbIsDoubleBuffered)
	{
		super(poLayout, pbIsDoubleBuffered);
		setBorderedTitle(pstrBorderLabel);
	}

	/**
	 * Sets the 5-point margin border in the panel
	 * with the specified parameter.
	 * @param pstrBorderLabel desired title, can be null or empty
	 */
	public void setBorderedTitle(String pstrBorderLabel)
	{
		if(pstrBorderLabel == null)
		{
			pstrBorderLabel = "";
		}

		setBorder
		(
			BorderFactory.createCompoundBorder
			(
				BorderFactory.createTitledBorder(pstrBorderLabel),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
			)
		);
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 * @since 0.3.0.2
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.10 $";
	}
}

// EOF
