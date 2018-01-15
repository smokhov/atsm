package marf.math;

import java.io.Serializable;


/**
 * <p>Implements complex number arithmetic.</p>
 * 
 * $Id: ComplexNumber.java,v 1.3 2007/12/18 03:45:42 mokhov Exp $
 * 
 * @author Serguei Mokhov
 * @since 0.3.0.6
 * @version $Revision: 1.3 $
 */
public final class ComplexNumber
implements Cloneable, Serializable
{
	/**
	 * 
	 */
	protected double dReal = 0;

	/**
	 * 
	 */
	protected double dImaginary = 0;

	/**
	 * 
	 */
	public ComplexNumber()
	{
		super();
	}

	/**
	 * @param pdReal
	 * @param pdImaginary
	 */
	public ComplexNumber(double pdReal, double pdImaginary)
	{
		this.dReal = pdReal;
		this.dImaginary = pdImaginary;
	}

	/**
	 * @param pdReal
	 */
	public ComplexNumber(double pdReal)
	{
		this(pdReal, 0);
	}

	/**
	 * Copy-constructor.
	 * @param poNumber the complex number to copy
	 */
	public ComplexNumber(final ComplexNumber poNumber)
	{
		this(poNumber.dReal, poNumber.dImaginary);
	}

	/**
	 * C3 = C1 + C2.
	 * @param poNumber1 C1
	 * @param poNumber2 C2
	 * @return C3
	 */
	public static ComplexNumber add(final ComplexNumber poNumber1, final ComplexNumber poNumber2)
	{
		return new ComplexNumber(poNumber1.dReal + poNumber2.dReal, poNumber1.dImaginary + poNumber2.dImaginary);
	}
	
	/**
	 * this = this + C.
	 * @param poNumber2 C
	 * @return new this
	 */
	public ComplexNumber add(final ComplexNumber poNumber2)
	{
		set(add(this, poNumber2)); 
		return this;
	}
	
	/**
	 * C3 = C1 - C2.
	 * @param poNumber1 C1
	 * @param poNumber2 C2
	 * @return C3
	 */
	public static ComplexNumber subtract(final ComplexNumber poNumber1, final ComplexNumber poNumber2)
	{
		return new ComplexNumber(poNumber1.dReal - poNumber2.dReal, poNumber1.dImaginary - poNumber2.dImaginary);
	}
	
	/**
	 * this = this - C.
	 * @param poNumber2 C
	 * @return new this
	 */
	public ComplexNumber subtract(final ComplexNumber poNumber2)
	{
		set(subtract(this, poNumber2)); 
		return this;
	}

	
	/**
	 * C3 = C1 * C2.
	 * @param poNumber1 C1
	 * @param poNumber2 C2
	 * @return C3
	 */
	public static ComplexNumber multiply(final ComplexNumber poNumber1, final ComplexNumber poNumber2)
	{
		return new ComplexNumber
		(
			poNumber1.dReal * poNumber2.dReal - poNumber1.dImaginary * poNumber2.dImaginary,
			poNumber1.dImaginary * poNumber2.dReal + poNumber1.dReal * poNumber2.dImaginary 
		);
	}
	
	/**
	 * this = this * C.
	 * @param poNumber2 C
	 * @return new this
	 */
	public ComplexNumber multiply(final ComplexNumber poNumber2)
	{
		set(multiply(this, poNumber2)); 
		return this;
	}
	
	/**
	 * C3 = C1 / C2.
	 * @param poNumber1 C1
	 * @param poNumber2 C2
	 * @return C3
	 */
	public static ComplexNumber divide(final ComplexNumber poNumber1, final ComplexNumber poNumber2)
	{
		return new ComplexNumber
		(
			(poNumber1.dReal * poNumber2.dReal + poNumber1.dImaginary * poNumber2.dImaginary) /
				(poNumber2.dReal * poNumber2.dReal + poNumber2.dImaginary * poNumber2.dImaginary),
			(poNumber1.dImaginary * poNumber2.dReal - poNumber1.dReal * poNumber2.dImaginary) / 
				(poNumber2.dReal * poNumber2.dReal + poNumber2.dImaginary * poNumber2.dImaginary)
		);
	}
	
	/**
	 * this = this / C.
	 * @param poNumber2 C
	 * @return new this
	 */
	public ComplexNumber divide(final ComplexNumber poNumber2)
	{
		set(divide(this, poNumber2)); 
		return this;
	}
	
	/**
	 * Magnitude = abs(z) = |z|.
	 * @param poNumber z
	 * @return |z|
	 */
	public static double abs(final ComplexNumber poNumber)
	{
		return Math.sqrt(poNumber.dReal * poNumber.dReal + poNumber.dImaginary * poNumber.dImaginary);
	}
	
	/**
	 * abs(this) = |this|.
	 * @return new this
	 */
	public double abs()
	{
		return abs(this);
	}

	/**
	 * @param poNumber
	 * @return arg(z)
	 */
	public static double arg(final ComplexNumber poNumber)
	{
		return poNumber.getPhaseAngle();
	}

	/**
	 * @return arg(this)
	 */
	public double arg()
	{
		return getPhaseAngle();
	}

	
	/**
	 * z1 = z ^ n.
	 * @param poNumber z
	 * @param pdPower n
	 * @return z1
	 */
	public static ComplexNumber pow(final ComplexNumber poNumber, double pdPower)
	{
		ComplexNumber oNumber = new ComplexNumber(poNumber);
		oNumber.setPhaseAngle(oNumber.getPhaseAngle() * pdPower);
		return oNumber;
	}
	
	/**
	 * this = this ^ n.
	 * @param pdPower n
	 * @return this
	 */
	public ComplexNumber pow(double pdPower)
	{
		setPhaseAngle(getPhaseAngle() * pdPower);
		return this;
	}

	/**
	 * this = C.
	 * @param poNumber C
	 * @return old this
	 */
	public ComplexNumber set(final ComplexNumber poNumber)
	{
		ComplexNumber oCopy = new ComplexNumber(this);
		this.dReal = poNumber.dReal;
		this.dImaginary = poNumber.dImaginary;
		return oCopy;
	}

	/**
	 * Allows querying for TODO.
	 * @return returns the value dImaginary field.
	 */
	public synchronized double getImaginary()
	{
		return this.dImaginary;
	}

	/**
	 * Allows setting TODO.
	 * @param pdImaginary the new value of dImaginary to set.
	 */
	public synchronized void setImaginary(double pdImaginary)
	{
		this.dImaginary = pdImaginary;
	}

	/**
	 * Allows querying for TODO.
	 * @return returns the value dReal field.
	 */
	public synchronized double getReal()
	{
		return this.dReal;
	}

	/**
	 * Allows setting TODO.
	 * @param pdReal the new value of dReal to set.
	 */
	public synchronized void setReal(double pdReal)
	{
		this.dReal = pdReal;
	}

	/**
	 * Sets real and imaginary parts given the phase angle
	 * in the polar coordinates. Radius is assumed to be "1".
	 * @param pdPhaseAngle the phase angle to use
	 */
	public void setPhaseAngle(double pdPhaseAngle)
	{
		setPolarCoordinates(pdPhaseAngle, 1.0);
	}

	/**
	 * @param pdPhaseAngle
	 * @param pdRadius
	 */
	public void setPolarCoordinates(double pdPhaseAngle, double pdRadius)
	{
		this.dReal = pdRadius * Math.cos(pdPhaseAngle);
		this.dImaginary = pdRadius * Math.sin(pdPhaseAngle);
	}
	
	/**
	 * @return
	 */
	public double getPhaseAngle()
	{
		return Math.atan2(this.dImaginary, this.dReal);
	}
	
	/**
	 * @return
	 */
	public double getRadius()
	{
		return abs(this);
	}
	
	/**
	 * Two complex numbers are equal if and only if their real parts are
	 * equal and their imaginary parts are equal.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object poNumber)
	{
		if(poNumber instanceof ComplexNumber)
		{
			ComplexNumber oNumber = (ComplexNumber)poNumber;
			return (this.dReal == oNumber.dReal) && (this.dImaginary == oNumber.dImaginary);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return new StringBuffer()
			.append("(").append(this.dReal).append(" + ")
			.append(this.dImaginary).append("i)")
			.toString();
	}
}

// EOF
