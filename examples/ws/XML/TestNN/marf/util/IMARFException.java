package marf.util;

/**
 * TODO: document.
 *
 *
 * $Id: IMARFException.java,v 1.1 2007/11/30 15:58:26 mokhov Exp $
 *
 * @author serguei
 * @since Nov 26, 2007
 */
public interface IMARFException
{
	IMARFException create();
	IMARFException create(String pstrMessage);
	IMARFException create(Exception poException);
	IMARFException create(String pstrMessage, Exception poException);
}

// EOF
