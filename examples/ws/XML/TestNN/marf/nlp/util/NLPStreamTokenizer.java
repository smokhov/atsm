package marf.nlp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Stack;

import marf.nlp.Parsing.Token;


/**
 * <p>NLP Stream Tokenizer. Allows pushing back multiple
 * tokens and has a reader reverence to be able to reset it.
 * </p>
 *
 * @author Serguei Mokhov
 * @version $Id: NLPStreamTokenizer.java,v 1.27 2010/10/17 19:46:53 mokhov Exp $
 * @since 0.3.0.2
 */
public class NLPStreamTokenizer
extends StreamTokenizer
{
	/**
	 * Needed for reset to work.
	 * @see #reset()
	 * @since 0.3.0.6
	 */
	protected static final int DEFAULT_RESET_MARK_BYTES = 32768;

	/**
	 * Default push backup level.
	 * @since 0.3.0.6
	 */
	protected static final int DEFAULT_PUSHBACK_LEVEL = 2;

	/**
	 * Keep a reference to the Reader ourselves to be able
	 * to <code>reset()</code>.
	 * @see #reset()
	 */
	protected transient Reader oReader = null;

	/**
	 * Allows for customization of the reset mark.
	 * @since 0.3.0.6
	 * @see #DEFAULT_RESET_MARK_BYTES
	 * @see #oReader
	 * @see #reset()
	 */
	protected Integer iResetMark = DEFAULT_RESET_MARK_BYTES;
	
	/**
	 * A stack to push back tokens.
	 */
	protected Stack<Token> oPushBackup = new Stack<Token>();

	/**
	 * Push backup level.
	 * @see #DEFAULT_PUSHBACK_LEVEL
	 */
	protected Integer iPushBackupLevel = DEFAULT_PUSHBACK_LEVEL;

	/**
	 * Reference to the token on the top of
	 * our own stack.
	 * @since 0.3.0.5
	 */
	protected Token oTopToken = null;

	/**
	 * NLP Stream Tokenizer based on a reader.
	 * @param poReader reader to use to read tokens/lexemes
	 */
	public NLPStreamTokenizer(Reader poReader)
	throws IOException
	{
		this(poReader, DEFAULT_RESET_MARK_BYTES);
	}
	
	/**
	 * @param poReader
	 * @param piResetMark
	 * @throws IOException
	 * @since 0.3.0.6
	 */
	public NLPStreamTokenizer(Reader poReader, Integer piResetMark)
	throws IOException
	{
		super(poReader);

		this.oReader = poReader;
		this.oPushBackup.ensureCapacity(this.iPushBackupLevel);
		
		setResetMark(piResetMark);
	}

	/**
	 * @param poInputStream
	 * @throws IOException
	 * @since 0.3.0.6
	 */
	public NLPStreamTokenizer(InputStream poInputStream)
	throws IOException
	{
		this(poInputStream, DEFAULT_RESET_MARK_BYTES);
	}
	
	/**
	 * @param poInputStream
	 * @param piResetMark
	 * @throws IOException
	 * @since 0.3.0.6
	 */
	public NLPStreamTokenizer(InputStream poInputStream, Integer piResetMark)
	throws IOException
	{
		this(new BufferedReader(new InputStreamReader(poInputStream)), piResetMark);
	}


	/**
	 * Returns a next token from the NLP stream or the stack if
	 * any were pushed back.
	 * @return next lexeme token if there is any or null
	 * @throws java.io.IOException
	 */
	public String getNextToken()
	throws java.io.IOException
	{
/*		String strNewToken =
			(nextToken() == TT_EOF) ?
			null : new String((char)ttype + "");
*/
		String strNewToken = null;

		while(nextToken() != TT_EOF)
		{
			if
			(
//				(this.ttype <= '\u0020')
				(this.ttype < '\u0020')
/*				||
				!(
					(this.ttype >= 'A' && this.ttype <= 'Z')
					||
					(this.ttype >= 'a' && this.ttype <= 'z')
					||
					(this.ttype >= '0' && this.ttype <= '9')
//					||
//					(this.ttype >= '\u00A0' && this.ttype <= '\u00FF')
				)
*/			)
			{
				continue;
			}

//			strNewToken = new String(Character.toLowerCase((char)ttype) + "");
			strNewToken = new String((char)ttype + "");
			break;
		}

		return strNewToken;
	}

	/**
	 * Resets the internal reader's stream.
	 * @throws java.io.IOException
	 */
	public void reset()
	throws java.io.IOException
	{
		if(this.oReader == null)
		{
			throw new NullPointerException("NLPStreamTokenizer.reset() - Reader is null.");
		}

		this.oReader.reset();
	}

	/**
	 * Retrieves the next token from the stream or local
	 * stack. The tokens retrieved from the local stack
	 * always if the stack is not empty. When the stack
	 * is empty the <code>nextToken()</code> of the parent
	 * is called.
	 *
	 * @return the value of the token type (ttype field)
	 * @throws java.io.IOException
	 * @since 0.3.0.5
	 * @see #ttype
	 */
	public int nextToken()
	throws java.io.IOException
	{
		if(this.oPushBackup.empty())
		{
			return super.nextToken();
		}
		else
		{
			this.oTopToken = this.oPushBackup.pop();

			this.sval = this.oTopToken.getLexeme();
			this.ttype = this.oTopToken.getTokenType().getType();
			this.nval = this.oTopToken.getNumericalValue();

			return this.ttype;
		}
	}

	/**
	 * Overridden to place tokens back onto to stack, virtually
	 * of any number of tokens.
	 * @since 0.3.0.5
	 */
	public void pushBack()
	{
		this.oTopToken = new Token(this.sval, super.lineno(), this.ttype, this.nval);
		this.oPushBackup.push(this.oTopToken);
		//super.pushBack();
	}

	/**
	 * Returns the current line number (of the latest/top token).
	 * Overridden to account for tokens stored in the local stack.
	 * @return the current line number of this tokenizer
	 * @since 0.3.0.5
	 */
	public int lineno()
	{
		if(this.oTopToken != null)
		{
			return this.oTopToken.getLineNumber();
		}
		else
		{
			return super.lineno();
		}
	}

	/**
	 * Allows querying for the current reset mark.
	 * @return returns the value iResetMark field.
	 * @since 0.3.0.6
	 * @see #reset()
	 */
	public Integer getResetMark()
	{
		return this.iResetMark;
	}

	/**
	 * Allows setting the reset mark value.
	 * @param piResetMark the new value of iResetMark to set.
	 * @since 0.3.0.6
	 * @see #reset()
	 */
	public void setResetMark(Integer piResetMark)
	throws IOException
	{
		this.iResetMark = piResetMark;

		if(this.oReader.markSupported())
		{
			this.oReader.mark(this.iResetMark);
		}
		else
		{
			System.err.println
			(
				"WARNING: NLPStreamTokenizer: stream marking is not supported for "
				+ this.oReader.getClass().getName()
			);
		}
	}

	/**
	 * Allows querying for the current push back level.
	 * @return returns the value iPushBackupLevel field.
	 * @since 0.3.0.6
	 */
	public Integer getPushBackupLevel()
	{
		return this.iPushBackupLevel;
	}

	/**
	 * Allows setting the push back level.
	 * @param piPushBackupLevel the new value of iPushBackupLevel to set.
	 * @since 0.3.0.6
	 * @see #iPushBackupLevel
	 */
	public void setPushBackupLevel(Integer piPushBackupLevel)
	{
		this.iPushBackupLevel = piPushBackupLevel;
	}

	/**
	 * Retrieves class' revision.
	 * @return revision string
	 */
	public static String getMARFSourceCodeRevision()
	{
		return "$Revision: 1.27 $";
	}
}

// EOF
