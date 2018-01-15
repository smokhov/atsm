package marf.nlp.Parsing.GrammarCompiler;

import marf.nlp.Parsing.Token;


/**
 * Represents terminal symbol grammar element.
 *
 * @author Serguei Mokhov
 * @version $Id: Terminal.java,v 1.12 2012/07/18 02:45:45 mokhov Exp $
 * @since 0.3.0.2
 */
public class Terminal
extends GrammarElement
{
	/**
	 * Grammar token type.
	 */
	protected GrammarTokenType oType = new GrammarTokenType();

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 * @since 0.3.0.4
	 */
	private static final long serialVersionUID = 1691151577764029404L;

	/**
	 * Constructor with the token lexeme and its ID.
	 * This token upon construction is added to the
	 * first set of itself.
	 *
	 * @param pstrLexeme the spelling of the token
	 * @param piID corresponding integer ID
	 */
	public Terminal(String pstrLexeme, int piID)
	{
		super(pstrLexeme, piID);
		this.oFirstSet.addElement(this);
		this.oType.setSubTypeByLexeme(pstrLexeme);
	}

	/**
	 * Constructor with the token and its ID.
	 * This token upon construction is added to the
	 * first set of itself.
	 *
	 * @param poToken token element that 
	 * @param piID corresponding integer ID
	 */
	public Terminal(Token poToken, int piID)
	{
		super(poToken, piID);
		this.oFirstSet.addElement(this);
		this.oType.setSubTypeByLexeme(poToken.getLexeme());
	}

	/**
	 * Checks is the parameter token is also
	 * token by having the same name.
	 *
	 * @param poToken parameter to compare token name against
	 * @return <code>true</code> if the parameter's lexeme is
	 * equal to this token's name; <code>false</code> otherwise
	 */
	public boolean isToken(final Token poToken)
	{
		//return (pToken.getTokenType().subtype == this.Type.subtype);
		return poToken.getLexeme().equals(this.strName);
	}

	/**
	 * Allows getting the enclosed grammar token type.
	 * @return the type
	 */
	public GrammarTokenType getType()
	{
		return this.oType;
	}

	/**
	 * @see marf.nlp.Parsing.GrammarCompiler.GrammarElement#isTerminal()
	 * @return <code>true</code>
	 */
	public boolean isTerminal()
	{
		return true;
	}

	/**
	 * @see marf.nlp.Parsing.GrammarCompiler.GrammarElement#isNonTerminal()
	 * @return <code>false</code>
	 */
	public boolean isNonTerminal()
	{
		return false;
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
