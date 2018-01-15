package marf.nlp.Storage;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import marf.nlp.util.NLPStreamTokenizer;
import marf.util.MARFRuntimeException;
import marf.util.NotImplementedException;


/**
 * <p>Corpus container bean.
 * </p>
 *
 * @author Serguei Mokhov
 * @version $Revision: 1.4 $
 * @since 0.3.0.6
 */
public class Corpus
implements Serializable, Cloneable, Comparable<Object>
{
	public static final int COMPARE_CASE_SENSITIVE = 0;

	/**
	 * Lowercased prior action.
	 */
	public static final int COMPARE_LOWER_CASE = 1;

	/**
	 * Uppercased prior action.
	 */
	public static final int COMPARE_UPPER_CASE = 2;

	/**
	 * Case is not altered, but internal casing is ignored.
	 */
	public static final int COMPARE_CASE_INSENSITIVE = 3;


	protected int iCaseSensitivity = COMPARE_UPPER_CASE;

	/**
	 * Plain-text container.
	 */
	protected StringBuffer oRawCorpusTextBuffer = null;

	/**
	 * Tokenizer to use to convert a raw string to WordStat tokens.
	 */
//	protected transient StringTokenizer oTokenizer = null;
	protected transient NLPStreamTokenizer oTokenizer = null;


	protected int iMatches = 0;
	protected int iInserts = 0;
	protected int iDeletes = 0;
	protected int iModifications = 0;


	/**
	 *
	 */
	protected List<String> oTokenizedCorpus = null;

	public Corpus()
	{

	}


	//-------------------------------

	/**
	 * Compares this corpus to another one and report the differences
	 * in terms of lexeme tokens. This corpus acts like main corpus
	 * to compare to (master); and the parameter is the one being compared.
	 * The statistics is measured to count the number of the deletes, inserts,
	 * or modifications with respect to the master corpus.
	 *
	 * @param poCorpus secondary tokenized corpus to compare with this one
	 */
	public void compare(Corpus poCorpus)
	{
		if(poCorpus == null)
		{
			throw new NullPointerException("Can't compare to a null corpus.");
		}

		List<String> oOtherTokenizedCorpus = poCorpus.getTokenizedCorpus();

		if(oOtherTokenizedCorpus == null)
		{
			//XXX: maybe we can tokenize it ourselves?
			throw new MARFRuntimeException("Other (parameter) corpus is not tokenized. Tokenize it first, then call compare().");
		}

		int iOtherTokenIndex = 0;

		List<String> oTmpMasterList = new ArrayList<String>();
		List<String> oTmpOtherList = new ArrayList<String>();

		masterLoop: for(int iMasterTokenIndex = 0; iMasterTokenIndex < this.oTokenizedCorpus.size(); iMasterTokenIndex++)
		{

			switch(this.iCaseSensitivity)
			{
				case COMPARE_LOWER_CASE:
				{
					throw new NotImplementedException();
					//break;
				}

				case COMPARE_UPPER_CASE:
				{
					// In the case of the master longer than the other
					// and we ran out of the other index, all the other
					// tokens are counted as deletions
					if(iOtherTokenIndex >= oOtherTokenizedCorpus.size())
					{
						this.iDeletes += (this.oTokenizedCorpus.size() - oOtherTokenizedCorpus.size());
						break masterLoop;
					}

					String strMasterLexeme;
					String strOtherLexeme;

					strMasterLexeme = this.oTokenizedCorpus.get(iMasterTokenIndex).toString().toUpperCase();
					strOtherLexeme = oOtherTokenizedCorpus.get(iOtherTokenIndex).toString().toUpperCase();

					// If we are equal, both indexes advance and we count the matches
					if(strOtherLexeme.equals(strMasterLexeme))
					{
						this.iMatches++;
						iOtherTokenIndex++;

						// Catch accumulated modifications if the tmp lists are of the same size
						if(oTmpMasterList.size() == oTmpOtherList.size() && oTmpMasterList.size() > 0)
						{
							this.iModifications += oTmpMasterList.size();
							oTmpMasterList.clear();
							oTmpOtherList.clear();
						}

						// If the master sublist is bigger, these are deletions
						else if(oTmpMasterList.size() > oTmpOtherList.size() && oTmpOtherList.size() > 0)
						{
							this.iDeletes += (oTmpMasterList.size() - oTmpOtherList.size());

							// Retain remaining portion of the master list size.
							// The toIndex (oTmpOtherList.size()) is non-exclusive.
							oTmpMasterList = oTmpMasterList.subList(0, oTmpOtherList.size());

							// Clear the smaller list
							oTmpOtherList.clear();
						}

						// If the other sublist is bigger, then these are insertions
						else if(oTmpOtherList.size() > oTmpMasterList.size() && oTmpMasterList.size() > 0)
						{
							this.iInserts += (oTmpOtherList.size() - oTmpMasterList.size());

							// Retain remaining portion of the master list size.
							// The toIndex (oTmpOtherList.size()) is non-exclusive.
							oTmpOtherList = oTmpOtherList.subList(0, oTmpMasterList.size());

							// Clear the smaller list
							oTmpMasterList.clear();
						}

						else
						{
							throw new MARFRuntimeException("can't happen");
						}

						continue masterLoop;
					}

					// Check the newly arrived other lexeme if it appears
					// anywhere in the master index previously collected.
					// If it does, these are counted as insertions and the
					// lists are cleaned up
					for(int iTmpMasterIndex = 0; iTmpMasterIndex < oTmpMasterList.size(); iTmpMasterIndex++)
					{
						if(strOtherLexeme.equals(oTmpMasterList.get(iTmpMasterIndex)))
						{
							this.iInserts += Math.abs((iTmpMasterIndex + 1) - oTmpOtherList.size());
							oTmpMasterList = oTmpMasterList.subList((iTmpMasterIndex + 1), oTmpMasterList.size());
							oTmpOtherList.clear();
							iOtherTokenIndex++;
							oTmpMasterList.add(strMasterLexeme);
							continue masterLoop;
						}
					}

					for(int iTmpOtherIndex = 0; iTmpOtherIndex < oTmpMasterList.size(); iTmpOtherIndex++)
					{
						if(strMasterLexeme.equals(oTmpOtherList.get(iTmpOtherIndex)))
						{
							this.iDeletes += Math.abs((iTmpOtherIndex + 1) - oTmpMasterList.size());
							oTmpOtherList = oTmpOtherList.subList((iTmpOtherIndex + 1), oTmpOtherList.size());
							oTmpMasterList.clear();
							continue masterLoop;
						}
					}


					// We remember non-equal lexemes and move on until
					iOtherTokenIndex++;
					oTmpMasterList.add(strMasterLexeme);
					oTmpOtherList.add(strOtherLexeme);

					break;
				}

				case COMPARE_CASE_INSENSITIVE:
				case COMPARE_CASE_SENSITIVE:
				{
					throw new NotImplementedException();
					//break;
				}

				default:
				{
					throw new MARFRuntimeException("Invalid case sensitivity flag: " + this.iCaseSensitivity);
				}
			}
		}

	}

	//-------------------------------

	public Corpus append(Object poObjectToAppend)
	{
		// Instantiate on the first call
		if(this.oRawCorpusTextBuffer == null)
		{
			this.oRawCorpusTextBuffer = new StringBuffer();
		}

		this.oRawCorpusTextBuffer.append(poObjectToAppend);

		// Allow concatenation of append()'s
		return this;
	}

	public Corpus appendToken(String pstrTokenToAppend)
	{
		// Instantiate on the first call
		if(this.oTokenizedCorpus == null)
		{
			this.oTokenizedCorpus = new ArrayList<String>();
		}

		this.oTokenizedCorpus.add(pstrTokenToAppend);

		// Allow concatenation of append()'s
		return this;
	}

	/**
	 * Tokenizes raw contained corpus into a list of tokens.
	 */
	public void tokenize()
	throws IOException
	{
		this.oTokenizedCorpus = new ArrayList<String>();
		//this.oTokenizer = new StringTokenizer();
		//this.oTokenizer.
		this.oTokenizer = new NLPStreamTokenizer(new StringReader(this.oRawCorpusTextBuffer.toString()));

		String strToken = "";

		do
		{
			strToken = this.oTokenizer.getNextToken();

			if(strToken == null)
			{
				break;
			}

			switch(this.iCaseSensitivity)
			{
				case COMPARE_LOWER_CASE:
				{
					strToken = strToken.toLowerCase();
					break;
				}

				case COMPARE_UPPER_CASE:
				{
					strToken = strToken.toUpperCase();
					break;
				}

				default:
				{
					// Other cases are not an error and
					// need not processing at the
					// tokenization phase.
				}
			}

			this.oTokenizedCorpus.add(strToken);
		}
		while(strToken != null);
	}

	//-------------------------------

	public StringBuffer getRawCorpusTextBuffer() {
		return oRawCorpusTextBuffer;
	}


	public void setRawCorpusTextBuffer(StringBuffer poRawCorpusTextBuffer) {
		oRawCorpusTextBuffer = poRawCorpusTextBuffer;
	}


//	public StringTokenizer getTokenizer() {
	public NLPStreamTokenizer getTokenizer()
	{
		return this.oTokenizer;
	}


//	public void setTokenizer(StringTokenizer poTokenizer) {
	public void setTokenizer(NLPStreamTokenizer poTokenizer)
	{
		this.oTokenizer = poTokenizer;
	}


	public List<String> getTokenizedCorpus()
	{
		return this.oTokenizedCorpus;
	}


	public void setTokenizedCorpus(List<String> poTokenizedCorpus)
	{
		this.oTokenizedCorpus = poTokenizedCorpus;
	}


	public int getCaseSensitivity() {
		return iCaseSensitivity;
	}


	public void setCaseSensitivity(int caseSensitivity) {
		iCaseSensitivity = caseSensitivity;
	}


	public int getMatches() {
		return iMatches;
	}


	public void setMatches(int matches) {
		iMatches = matches;
	}


	public int getInserts() {
		return iInserts;
	}


	public void setInserts(int inserts) {
		iInserts = inserts;
	}


	public int getDeletes() {
		return iDeletes;
	}


	public void setDeletes(int deletes) {
		iDeletes = deletes;
	}


	public int getModifications() {
		return iModifications;
	}


	public void setModifications(int modifications) {
		iModifications = modifications;
	}

	//-------------------------------


	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize()
	throws Throwable
	{
		this.oRawCorpusTextBuffer = null;
		this.oTokenizedCorpus = null;
		this.oTokenizer = null;
		super.finalize();
	}

	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object poObjectToCompare)
	{
		if(poObjectToCompare == null)
		{
			// Per Comparable specification
			throw new NullPointerException("Corpus: object to compare to is null");
		}

		if(poObjectToCompare instanceof Corpus)
		{
			Corpus oCorpusToCompare = (Corpus)poObjectToCompare;
		}
		// TODO Auto-generated method stub
		return 0;
	}

}

// EOF
