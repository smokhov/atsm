package marf.gui;

import marf.gui.util.BorderPanel;
import marf.gui.util.ColoredStatusPanel;

/**
 * <p>GUI component to draw wave forms to be used by apps.
 * TODO: complete implementation.</p>
 *
 * $Id: WaveGrapherPanel.java,v 1.7 2006/01/22 19:01:19 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.7 $
 * @since 0.3.0.2
 */
public class WaveGrapherPanel
extends BorderPanel
{
	/**
	 * Local reference to the wave grapher object for data.
	 */
	protected WaveGrapher oWaveGrapher;

	/**
	 * Local reference to the status panel.
	 */
	protected ColoredStatusPanel oStatusPanel;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 3804010606988001885L;

	/**
	 * Default constructor.
	 */
	public WaveGrapherPanel()
	{
	}

	/**
	 * Returns source code revision information.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.7 $";
	}
}

// EOF
