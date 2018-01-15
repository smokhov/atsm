package marf;

import marf.util.MARFException;


/**
 * <p>Responsible for providing and validating version information of MARF.
 * A version should be bumped here at the beginning of every release cycle
 * according to the versioning guidelines outlined in the manual.
 * <p>
 *
 * $Id: Version.java,v 1.6 2007/12/31 00:17:05 mokhov Exp $
 *
 * @author Serguei Mokhov
 * @since 0.3.0.5
 */
public class Version
{
	/**
	 * Indicates major MARF version, like <b>1</b>.x.x.
	 */
	public static final int MAJOR_VERSION  = 0;

	/**
	 * Indicates minor MARF version, like 1.<b>1</b>.x.
	 */
	public static final int MINOR_VERSION  = 3;

	/**
	 * Indicates MARF revision, like 1.1.<b>1</b>.
	 */
	public static final int REVISION       = 0;

	/**
	 * Indicates MARF minor development revision, like 1.1.1.<b>1</b>.
	 * This is primarily for development releases. On the final release
	 * the counting stops and is reset to 0 every minor version.
	 * @see #MINOR_VERSION
	 */
	public static final int MINOR_REVISION = 6;

	/**
	 * Should be automatically replaced through scripting
	 * when compiling smaller binaries of MARF, e.g.
	 * marf-util, marf-math, or marf-nlp, etc. packages,
	 * the corresponding suffix (i.e. "util", "nlp", ...)
	 * should end up being in this constant and then compiled.
	 * This suffix is appended all string variants of the version.
	 * The default fat MARF should not contain anything.
	 */
	private static final String PACKAGE = "";
	
	/**
	 * Returns a string representation of the MARF version.
	 * 
	 * @return version String
	 * @see #MINOR_REVISION
	 */
	public static final String getStringVersion()
	{
		return
			MAJOR_VERSION + "." +
			MINOR_VERSION + "." +
			REVISION + "." +
			MINOR_REVISION +
			getPackageInfo();
	}

	/**
	 * Returns a string representation of the MARF version
	 * given its floating point equivalent. E.g. 30.5 becomes "0.3.0.5".
	 * @param pdDoubleVersion the floating point equivalent of the version 
	 * @return version String
	 */
	public static final String getStringVersion(double pdDoubleVersion)
	{
		int iIntVersion = (int)Math.ceil(pdDoubleVersion * 10);
		return getStringVersion(iIntVersion);
	}

	/**
	 * Returns a string representation of the MARF version
	 * given its integer equivalent. E.g. 1306 becomes "0.3.0.6".
	 * @param piIntVersion the integer equivalent of the version 
	 * @return version String
	 * @since 0.3.0.6
	 */
	public static final String getStringVersion(int piIntVersion)
	{
		int iMajorVersion = (int)Math.ceil(piIntVersion / 1000);
		int iMinorVersion = (int)Math.ceil((piIntVersion - iMajorVersion * 1000) / 100);
		int iRevision = (int)Math.ceil((piIntVersion - iMajorVersion * 1000 - iMinorVersion * 100) / 10);
		int iMinorRevision = piIntVersion - iMajorVersion * 1000 - iMinorVersion * 100 - iRevision;

		return
			iMajorVersion + "." +
			iMinorVersion + "." +
			iRevision + "." +
			iMinorRevision +
			getPackageInfo();
	}

	/**
	 * Returns an integer representation of the MARF version.
	 * As of 0.3.0.3, MINOR_REVISION is included into calculations
	 * and the formula changed to begin with 1000 as a MAJOR_VERSION
	 * coefficient.
	 *
	 * @return integer version as <code>MAJOR_VERSION * 1000 + MINOR_VERSION * 100 + REVISION * 10 + MINOR_REVISION</code>
	 *
	 * @see #MAJOR_VERSION
	 * @see #MINOR_VERSION
	 * @see #REVISION
	 * @see #MINOR_REVISION
	 */
	public static final int getIntVersion()
	{
		return
			MAJOR_VERSION * 1000 +
			MINOR_VERSION * 100 +
			REVISION * 10 +
			MINOR_REVISION;
	}

	/**
	 * Retrieves double version of MARF. Unlike the integer version, the double
	 * one begins with 100 and the minor revision is returned after the point,
	 * e.g. 123.4 for 1.2.3.4.
	 *
	 * @return double version as <code>MAJOR_VERSION * 100 + MINOR_VERSION * 10 + REVISION + MINOR_REVISION / 10</code>
	 *
	 * @see #MAJOR_VERSION
	 * @see #MINOR_VERSION
	 * @see #REVISION
	 * @see #MINOR_REVISION
	 */
	public static final double getDoubleVersion()
	{
		return
			MAJOR_VERSION * 100 +
			MINOR_VERSION * 10 +
			REVISION +
			(double)MINOR_REVISION / 10;
	}

	/**
	 * Makes sure the applications aren't run against older MARF version.
	 * @param pdDoubleVersion floating point version representation to validate
	 * @throws MARFException if the MARF's version is too old
	 */
	public static final void validateVersions(double pdDoubleVersion)
	throws MARFException
	{
		validateVersions(pdDoubleVersion, false);
	}

	/**
	 * Makes sure the applications aren't run against an older MARF or
	 * exact matching library version.
	 * @param pdDoubleVersion floating point version representation to validate
	 * @param pbExactMatch if set to true the exact version match will be required
	 * @throws MARFException if the MARF's version is too old or isn't matching
	 * @since 0.3.0.6
	 */
	public static final void validateVersions(double pdDoubleVersion, boolean pbExactMatch)
	throws MARFException
	{
		boolean bVersionMismatch = pbExactMatch
			? getDoubleVersion() != pdDoubleVersion
			: getDoubleVersion() < pdDoubleVersion;

		if(bVersionMismatch)
		{
			errorOut(getStringVersion(pdDoubleVersion), pbExactMatch);
		}
	}

	/**
	 * Makes sure the applications aren't run against older MARF version.
	 * @param piIntVersion integer version representation to validate
	 * @throws MARFException if the MARF library's version is too old
	 * @since 0.3.0.6
	 */
	public static final void validateVersions(int piIntVersion)
	throws MARFException
	{
		validateVersions(piIntVersion, false);
	}
	
	/**
	 * Makes sure the applications aren't run against an older MARF or
	 * exact matching library version.
	 * @param piIntVersion integer version representation to validate
	 * @param pbExactMatch if set to true the exact version match will be required
	 * @throws MARFException if the MARF library's version is too old or isn't matching
	 * @since 0.3.0.6
	 */
	public static final void validateVersions(int piIntVersion, boolean pbExactMatch)
	throws MARFException
	{
		boolean bVersionMismatch = pbExactMatch
			? getIntVersion() != piIntVersion
			: getIntVersion() < piIntVersion;

		if(bVersionMismatch)
		{
			errorOut(getStringVersion(piIntVersion), pbExactMatch);
		}
	}
	
	/**
	 * Makes sure the applications aren't run against older MARF version.
	 * @param pstrStringVersion String representation of the required version
	 * @throws MARFException if the MARF's version is too old
	 * @throws NullPointerException if the parameter is null
	 */
	public static final void validateVersions(final String pstrStringVersion)
	throws MARFException
	{
		validateVersions(pstrStringVersion, false);
	}

	/**
	 * Makes sure the applications aren't run against an older MARF or
	 * exact matching library version.
	 * @param pstrStringVersion String version representation to validate
	 * @param pbExactMatch if set to true the exact version match will be required
	 * @throws MARFException if the MARF's version is too old or isn't matching
	 * @since 0.3.0.6
	 */
	public static final void validateVersions(final String pstrStringVersion, boolean pbExactMatch)
	throws MARFException
	{
		boolean bVersionMismatch = pbExactMatch
			? getStringVersion().compareTo(pstrStringVersion) != 0
			: getStringVersion().compareTo(pstrStringVersion) < 0;

		if(bVersionMismatch)
		{
			errorOut(pstrStringVersion, pbExactMatch);
		}
	}

	/**
	 * Just a helper method for code reuse to throw an exception
	 * with the version validation error message given parameters. 
	 * @param pstrStringVersion expected version
	 * @param pbExactMatch if true modifies the error message to require exactly the mentioned version
	 * and if false, simply says this version or above.
	 * @throws MARFException with the error message of the version mismatch
	 * @since 0.3.0.6
	 */
	private static void errorOut(final String pstrStringVersion, boolean pbExactMatch)
	throws MARFException
	{
		throw new MARFException
		(
			"Your MARF version (" + getStringVersion()
			+ ") is " + (pbExactMatch ? "not matching the required one." : "too old.")
			+ " This application requires "
			+ pstrStringVersion + (pbExactMatch ? " precisely." : " or above.")
		);
	}

	/**
	 * Helper method to append package information.
	 * @return package string if it is not empty preceded with a dash.
	 * @since 0.3.0.6
	 */
	private static String getPackageInfo()
	{
		// Will only be activated through scripting of the PACKAGE member
		// at the distro time.
		return (PACKAGE.equals("") ? "" : "-" + PACKAGE);
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.6 $";
	}
}

// EOF
