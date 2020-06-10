package iisc.edu.pll.data.lattice.arashort;

import java.util.Arrays;
/**
 * Base class to store a short matrix and its operations
 * 
 *
 */

public class ShortMatrix {
	
	private short[][] mat;

	/**
	 * 
	 * @param mat 2D short matrix
	 */
	public ShortMatrix(short[][] mat) {
		super();
		this.mat = mat;
	}

/**
 * getter to return the 2D short matrix
 * @return
 */
	public short[][] getMat() {
		return mat;
	}
	
/**
 * Setter to set the 2D matrix
 * @param mat 2D short matrix
 */
	public void setMat(short[][] mat) {
		this.mat = mat;
	}
	/**
	 * Sets individual cell value in 2D matrix
	 * @param val value to assigned
	 * @param row row number
	 * @param col column number
	 */
	public void setValue(short val, int row, int col) {
		mat[row][col] = val;
	}

/**
 * Gets individual value from a cell	
 * @param row row number
 * @param col column number
 * @return value in [row][column]
 */
	public short getValue(int row, int col) {
		return mat[row][col];
	}
/**
 * Assigns a hashcode to every element in the ShortMatrix
 * @return Integer hashcode
 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(mat);
		return result;
	}
/**
 * checks if a ShortMatrix object is equal to another ShortMatrix Object
 * All the elements in the matrices should be equal
 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShortMatrix other = (ShortMatrix) obj;
		if (!Arrays.deepEquals(mat, other.mat))
			return false;
		return true;
	}
	
	

}
