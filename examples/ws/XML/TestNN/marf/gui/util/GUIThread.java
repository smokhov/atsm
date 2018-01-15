package marf.gui.util;

import marf.util.BaseThread;
import marf.util.ExpandedThreadGroup;


/**
 * <p>Represents a MARF GUI Thread for non-blocking GUI components.</p>
 * 
 * TODO: complete and document.
 * 
 * $Id: GUIThread.java,v 1.2 2007/12/18 03:46:08 mokhov Exp $
 * 
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Revision: 1.2 $
 */
public class GUIThread
extends BaseThread
{
	/**
	 * 
	 */
	public GUIThread()
	{
		super();
	}

	/**
	 * @param poTarget
	 */
	public GUIThread(Runnable poTarget)
	{
		super(poTarget);
	}

	/**
	 * @param poTarget
	 * @param pstrName
	 */
	public GUIThread(Runnable poTarget, String pstrName)
	{
		super(poTarget, pstrName);
	}

	/**
	 * @param poThreadGroup
	 * @param poTarget
	 * @param pstrName
	 */
	public GUIThread(ThreadGroup poThreadGroup, Runnable poTarget, String pstrName)
	{
		super(poThreadGroup, poTarget, pstrName);
	}

	/**
	 * @param poThreadGroup
	 * @param pstrName
	 */
	public GUIThread(ThreadGroup poThreadGroup, String pstrName)
	{
		super(poThreadGroup, pstrName);
	}

	/**
	 * @param pstrName
	 */
	public GUIThread(String pstrName)
	{
		super(pstrName);
	}

	/**
	 * @param poThreadGroup
	 * @param pstrName
	 */
	public GUIThread(ExpandedThreadGroup poThreadGroup, String pstrName)
	{
		super(poThreadGroup, pstrName);
	}

	/**
	 * @param poThreadGroup
	 * @param poTarget
	 */
	public GUIThread(ExpandedThreadGroup poThreadGroup, Runnable poTarget)
	{
		super(poThreadGroup, poTarget);
	}

	/**
	 * @param poThreadGroup
	 * @param poTarget
	 * @param pstrName
	 */
	public GUIThread(ExpandedThreadGroup poThreadGroup, Runnable poTarget, String pstrName)
	{
		super(poThreadGroup, poTarget, pstrName);
	}

	/**
	 * @param piTID
	 */
	public GUIThread(int piTID)
	{
		super(piTID);
	}
}

// EOF
