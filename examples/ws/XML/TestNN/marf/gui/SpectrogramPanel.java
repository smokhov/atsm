package marf.gui;

import marf.gui.util.BorderPanel;
import marf.gui.util.ColoredStatusPanel;


/**
 * <p>GUI component to draw spectrograms to be used by apps.
 * TODO: complete implementation.</p>
 *
 * $Id: SpectrogramPanel.java,v 1.7 2006/01/22 19:01:19 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.7 $
 * @since 0.3.0.2
 */
public class SpectrogramPanel
extends BorderPanel
{
	/**
	 * Reference to the spectrogram object to draw.
	 */
	protected Spectrogram oSpectrogram;

	/**
	 * Status panel reference.
	 */
	protected ColoredStatusPanel oStatusPanel;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 6943369072027609738L;

	/**
	 * Default constructor.
	 */
	public SpectrogramPanel()
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
