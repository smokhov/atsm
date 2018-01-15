/*
 * Created on Apr 20, 2004
 */
package marf.util;

import java.util.Vector;


/**
 * <p>Provides some useful extensions to java.lang.ThreadGroup one would
 * normally expect to be in a "group".</p>
 * 
 * <p>Maintains local references to the group-belonging threads
 * for extra group control in a form of a Vector.</p>
 *
 * $Id: ExpandedThreadGroup.java,v 1.17 2010/06/11 21:00:48 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.17 $
 * @since 0.3.0.1
 */
public class ExpandedThreadGroup
extends ThreadGroup
{
	/**
	 * Local references to the threads belonging to this group.
	 */
	protected Vector<Runnable> oGroup = new Vector<Runnable>(); 
	
	/**
	 * ThreadGroup and name constructor inherited from the parent.
	 * @param poParent Parent group
	 * @param pstrName Group's name
	 */
	public ExpandedThreadGroup(ThreadGroup poParent, String pstrName)
	{
		super(poParent, pstrName);
	}

	/**
	 * Mimics parent's constructor.
	 * @param pstrName Group's name
	 */
	public ExpandedThreadGroup(String pstrName)
	{ 
		super(pstrName);
	}
		
	/**
	 * Mimics parent's constructor.
	 * @param poParent Parent group
	 * @param pstrName Group's name
	 */
	public ExpandedThreadGroup(ExpandedThreadGroup poParent, String pstrName)
	{  
		super(poParent, pstrName);
	}

	/**
	 * Starts all non-started threads in this group.
	 */
	public void start()
	{
		Thread[] aoBabies = enumerate(false); 

		for(int i = 0; i < aoBabies.length; i++)
		{
			/*
			 * Since starting threads is not totally under our
			 * control, we start only those that have not been
			 * started yet.
			 */
			if(aoBabies[i].isAlive() == false)
			{
				aoBabies[i].start();
			}
		}
	}
	
	/**
	 * Wait for all the threads in the group to terminate.
	 * @throws InterruptedException if one of the threads is
	 * interrupted or a thread group has been started
	 */
	public void join()
	throws InterruptedException
	{
		Thread[] aoBabies = null;
		int i = 0;
		
		try
		{		
			aoBabies = enumerate(true);

			for(i = 0; i < aoBabies.length; i++)
			{
				// It is possible to have a null child thread if the activeCount()
				// and enumerate() disagreed due to inherent race conditions, so
				// we just silently ignore the nulls.
				if(aoBabies[i] != null)
				{
					aoBabies[i].join();
				}
			}
		}
		catch(NullPointerException e)
		{
			e.printStackTrace(System.err);

			throw new InterruptedException
			(
				"Threads (a thread group) have to be started first, " + 
				"before calling join() onto them.\n" +
				"Diagnostic: thread array: " + aoBabies + "\n" +
				"group: " + this.oGroup + "\n" + 
				(aoBabies == null ? "" : "threads enumerated: " + aoBabies.length + "\n") +
				(aoBabies == null ? "" : "thread processed last: " + i + "\n")
			);
		}
	}

	/**
	 * Provides an enumeration that allocates a new array and populates
	 * it with currently active threads with ThreadGroup's enumerate().
	 *
	 * @param pbActiveThreads if only active threads needed, false if all
	 * @return array of threads belonging to this group
	 */
	public Thread[] enumerate(boolean pbActiveThreads)
	{
		Thread[] aoBabies = new Thread[pbActiveThreads ? activeCount() : this.oGroup.size()];

		if(pbActiveThreads == true)
		{
			int iThreadsEnumerated = enumerate(aoBabies);
			
			if(iThreadsEnumerated != aoBabies.length)
			{
				System.err.println
				(
					"WARNING: ExpandedThreadGroup.enumerate(boolean): active count " + aoBabies.length
					+ " != enumerated " + iThreadsEnumerated
				);
			}
		}
		else
		{
			this.oGroup.copyInto(aoBabies);
		}

		return aoBabies;
	}

	/**
	 * Adds specified thread to the local reference list.
	 * @param poThread thread object to add
	 */
	public synchronized void addThread(Thread poThread)
	{
		this.oGroup.add(poThread);
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.17 $";
	}
}

// EOF
