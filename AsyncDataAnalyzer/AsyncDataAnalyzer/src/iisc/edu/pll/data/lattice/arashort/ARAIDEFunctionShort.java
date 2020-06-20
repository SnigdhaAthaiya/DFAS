/*******************************************************************************
 * Copyright (C) 2020 Snigdha Athaiya
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package iisc.edu.pll.data.lattice.arashort;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.misc.RrefGaussJordanRowPivot_DDRM;
import org.ejml.simple.SimpleMatrix;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public class ARAIDEFunctionShort implements TFunction {

	Set<short[][]> matrixSet;

	boolean isTop;
	boolean isBot;

	/**
	 * Create an Empty set of type short[][]
	 */
	public ARAIDEFunctionShort() {
		// TODO Auto-generated constructor stub
		matrixSet = new HashSet<short[][]>();
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method to calculate the basis of two transfer function.
	 * 
	 * @return Transfer function obtained after finding the basis
	 */
	@Override
	public TFunction join(TFunction f) {
		// TODO Auto-generated method stub
		ARAIDEFunctionShort fun = (ARAIDEFunctionShort) f;// typecasting to class object
		Set<short[][]> A = matrixSet;// current object's matrixset
		Set<short[][]> B = fun.matrixSet;// passed object's matrixset
		int dim = Globals.numberOfVariables + 1;// dimension of matrix
		int columnLimit = A.size() + B.size();// number of columns in the truncated matrix
		double[][] coordinateVectors = new double[dim * dim][columnLimit];// object to store the matrices in the form of
		// vectore
		short[][][] includeDMatrixRMajs = new short[columnLimit][dim][dim];
		Iterator<short[][]> itrA = A.iterator();
		Iterator<short[][]> itrB = B.iterator();
		short[][] matrixRMaj = new short[dim][dim];
		int columnCount = 0;
		int rowCount;
		// converting every matrix of A into column vectors. The matrices are converted
		// to column vectors row wise.
		while (itrA.hasNext()) {
			rowCount = 0;
			matrixRMaj = itrA.next();
			includeDMatrixRMajs[columnCount] = matrixRMaj;
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					coordinateVectors[rowCount][columnCount] = matrixRMaj[i][j];
					rowCount++;
				}
			}
			columnCount++;

		}
		// converting every matrix of B into column vectors. The matrices are converted
		// to column vectors row wise.
		while (itrB.hasNext()) {
			rowCount = 0;
			matrixRMaj = itrB.next();
			try {
				includeDMatrixRMajs[columnCount] = matrixRMaj;
			} catch (Exception e) {
				System.out.println(e);
				// TODO: handle exception
			}

			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					coordinateVectors[rowCount][columnCount] = matrixRMaj[i][j];
					rowCount++;
				}
			}
			columnCount++;
		}
		// Converting to DMatrixRMaj object for matrix calculations

		DMatrixRMaj dataMatrix = new DMatrixRMaj(coordinateVectors);
		// Calculating RREF of truncated matrix
		RrefGaussJordanRowPivot_DDRM dataM = new RrefGaussJordanRowPivot_DDRM();
		dataM.reduce(dataMatrix, columnLimit);

		boolean[] requiredColumns = new boolean[columnLimit];

		for (int i = 0; i < columnLimit; i++) {
			requiredColumns[i] = true;
		}

		for (int j = 0; j < columnLimit; j++) {
			for (int i = 0; i < dim * dim; i++) {
				if (dataMatrix.get(i, j) == 1) {
					for (int k = j - 1; k >= 0; k--) {
						if (dataMatrix.get(i, k) != 0) {
							requiredColumns[j] = false;
						}
					}
				}
			}
		}
		// picking the required matrix in the basis set.
		// A set is picked if the column vector of rref matrix has a 1 such that it is
		// preceed with only 0's in the previous columns
		// creating new transfer function object
		ARAIDEFunctionShort resultAraideFunction = new ARAIDEFunctionShort();
		for (int i = 0; i < columnLimit; i++) {
			if (requiredColumns[i]) {
				if (includeDMatrixRMajs[i] == null) {
					System.out.println(i);
				}
				resultAraideFunction.matrixSet.add(includeDMatrixRMajs[i]);

			}
		}
		return resultAraideFunction;

	}

	/**
	 * Method to muliply two transfer functions
	 * 
	 * @return Transfer function corresponding to the product
	 */
	@Override
	public TFunction compose(TFunction f) {

		ARAIDEFunctionShort function = (ARAIDEFunctionShort) f;
		Set<short[][]> A = matrixSet;// matrix set of current object
		Set<short[][]> AA = new HashSet<short[][]>();// set to store the product

		Iterator<short[][]> itrA = A.iterator();
		Iterator<short[][]> itrB;
		SimpleMatrix LeftMatrix;
		SimpleMatrix RightMatrix;
		SimpleMatrix tempMatrix;

		double[][] doubleMat = new double[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];
		short[][] shortMat = new short[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];
		while (itrA.hasNext()) {
			doubleMat = ARAIDEFunctionShort.convertdouble(itrA.next(), (Globals.numberOfVariables + 1),
					(Globals.numberOfVariables + 1));// getting next element in the set and converting it to double
			// matrix
			LeftMatrix = new SimpleMatrix(doubleMat);
			// Set<DMatrixRMaj>B = function.matrixSet;
			itrB = function.matrixSet.iterator();
			while (itrB.hasNext()) {
				doubleMat = ARAIDEFunctionShort.convertdouble(itrB.next(), Globals.numberOfVariables + 1,
						Globals.numberOfVariables + 1);// getting next element in the set and converting it to double
				// matrix
				RightMatrix = new SimpleMatrix(doubleMat);
				tempMatrix = LeftMatrix.mult(RightMatrix);
				shortMat = convertshort(tempMatrix, Globals.numberOfVariables + 1, Globals.numberOfVariables + 1);// converting
				// back
				// to
				// short
				AA.add(shortMat);// addding to set
			}
		}

		ARAIDEFunctionShort resultAraideFunction = new ARAIDEFunctionShort();
		resultAraideFunction = (ARAIDEFunctionShort) findBasis(AA);// finding basis of the set containing product
		// matrices
		return resultAraideFunction;
		// TODO Auto-generated method stub
	}

	/**
	 * This method is used to convert a 2D short matrix to a 2D double matrix
	 * 
	 * @param a       A 2D matrix of type short
	 * @param rows    Number of row in the matrix
	 * @param columns Number of columns in the matrix
	 * @return double[][] 2D double matrix
	 */
	public static double[][] convertdouble(short[][] a, int rows, int columns) {
		double[][] doubleCordinate = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				doubleCordinate[i][j] = (double) a[i][j];// double typecasting individual elements

			}
		}
		return doubleCordinate;

	}

	/**
	 * Function to convert a SimpleMatrix to a 2D matrix
	 * 
	 * @param a       A 2D matrix object of type SimpleMatrix
	 * @param rows    Number of rows in the matrix
	 * @param columns Number of columns in the matrix
	 * @return A 2D matrix of type short
	 */
	public static short[][] convertshort(SimpleMatrix a, int rows, int columns) {
		short[][] doubleCordinate = new short[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				doubleCordinate[i][j] = (short) a.get(i, j);

			}
		}
		return doubleCordinate;

	}

	/**
	 * Function to check if one transfer function is greater than or equal to the
	 * other. A transfer function A is greater than or equal to another transfer
	 * function B if A.matrixSet and B.matrixset are both empty or
	 * size(A.join(B).matrixset)<= size(A.matrixset)
	 * 
	 * @return boolean if transfer function is greater than or equal to another
	 *         transfer function.
	 */
	@Override
	public boolean isGreater(TFunction v) {
		boolean result = false;
		ARAIDEFunctionShort function = (ARAIDEFunctionShort) v;
		if (this.matrixSet.isEmpty() && function.matrixSet.isEmpty()) {// check if both are empty
			result = true;
		} else {
			ARAIDEFunctionShort unionFunction = (ARAIDEFunctionShort) this.join(v);
			if (unionFunction.matrixSet.size() <= this.matrixSet.size()) {// check if size basis set of union is less
				// than original set size
				result = true;
			}
		}
		return result;

	}

	/**
	 * Function to check if one transfer function is strictly greater than the
	 * other. A transfer function A is greater than or equal to another transfer
	 * function B if A.matrixSet and B.matrixset are both empty or
	 * size(A.join(B).matrixset)<= size(A.matrixset)
	 * 
	 * @param v Transfer function
	 * @return boolean if transfer function is greater than or equal to another
	 *         transfer function.
	 */
	public boolean isStrictlyGreater(TFunction v) {
		boolean result = false;
		ARAIDEFunctionShort function = (ARAIDEFunctionShort) v;
		if (this.matrixSet.isEmpty()) {
			return result;
		} else {
			boolean flagA = false;
			boolean flagB = false;
			ARAIDEFunctionShort unionFunctionA = (ARAIDEFunctionShort) this.join(v);
			ARAIDEFunctionShort unionFunctionB = (ARAIDEFunctionShort) function.join(this);
			if (unionFunctionA.matrixSet.size() < matrixSet.size()) {
				flagA = true;
			}
			if (unionFunctionB.matrixSet.size() < function.matrixSet.size()) {
				flagB = true;
			}
			if (flagA && (!flagB)) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public void setIsBot(boolean b) {
		isBot = b;

	}

	@Override
	public boolean isBot() {
		// TODO Auto-generated method stub
		return isBot;
	}

	@Override
	public void setIsTop(boolean b) {
		isTop = b;

	}

	@Override
	public boolean isTop() {
		// TODO Auto-generated method stub
		return isTop;
	}

	@Override
	public TFunction makeDeepCopy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		return matrixSet.size();
	}

	@Override
	public boolean isConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Function to check if a transfer function is ID.
	 * 
	 * @return boolean true if transfer function is ID
	 */
	@Override
	public boolean isID() {
		boolean flag = false;
		int dim = Globals.numberOfVariables + 1;
		if (this.matrixSet.size() == 1) {
			flag = true;
			Iterator<short[][]> aIterator = matrixSet.iterator();
			short[][] A = aIterator.next();
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					if (i == j && A[i][j] != 1) {
						flag = false;
						break;
					}
					if (i != j && A[i][j] != 0) {
						flag = false;
						break;
					}
				}
			}
		}
		// TODO Auto-generated method stub
		return flag;
	}

	/**
	 * Function to find the basis of transfer function
	 * 
	 * @param A Set Matrices of type short
	 * @return Transfer function containing the basis set
	 */
	public static TFunction findBasis(Set<short[][]> A) {

		// TODO Auto-generated method stub

		int dim = Globals.numberOfVariables + 1;
		;// dimension of the matrix
		int columnLimit = A.size();// number of columns in the truncated matrix
		double[][] coordinateVectors = new double[dim * dim][columnLimit];
		short[][][] includeDMatrixRMajs = new short[columnLimit][dim][dim];

		Iterator<short[][]> itrA = A.iterator();
		short[][] matrixRMaj = new short[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];
		int columnCount = 0;
		int rowCount;
		// converting every matrix of A into column vectors. The matrices are converted
		// to column vectors row wise.
		while (itrA.hasNext()) {
			rowCount = 0;
			matrixRMaj = itrA.next();
			includeDMatrixRMajs[columnCount] = matrixRMaj;
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					coordinateVectors[rowCount][columnCount] = matrixRMaj[i][j];
					rowCount++;
				}
			}
			columnCount++;
		}

		DMatrixRMaj dataMatrix = new DMatrixRMaj(coordinateVectors);
		// dataMatrix.print();
		RrefGaussJordanRowPivot_DDRM dataM = new RrefGaussJordanRowPivot_DDRM();
		//calculating rref of the truncated matrix
		dataM.reduce(dataMatrix, columnLimit);

		boolean[] requiredColumns = new boolean[columnLimit];

		for (int i = 0; i < columnLimit; i++) {
			requiredColumns[i] = true;
		}

		// picking the required matrix in the basis set.
		// A set is picked if the column vector of rref matrix has a 1 such that it is
		// preceed with only 0's in the previous columns
		for (int j = 0; j < columnLimit; j++) {
			for (int i = 0; i < dim * dim; i++) {
				if (dataMatrix.get(i, j) == 1) {
					for (int k = j - 1; k >= 0; k--) {
						if (dataMatrix.get(i, k) != 0) {
							requiredColumns[j] = false;
						}
					}
				}
			}
		}
		// creating new transfer function object
		ARAIDEFunctionShort resultAraideFunction = new ARAIDEFunctionShort();

		for (int i = 0; i < columnLimit; i++) {
			if (requiredColumns[i]) {
				resultAraideFunction.matrixSet.add(includeDMatrixRMajs[i]);
			}
		}
		return resultAraideFunction;

	}
	/**
	 * Method to print the transfer function in String form
	 */
	@Override
	public String toString() {
		StringBuffer content = new StringBuffer();
		Iterator<short[][]> iterator = matrixSet.iterator();
		short[][] dMaj = new short[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];

		if (matrixSet.isEmpty())
			content.append("ID Function");
		else {
			while (iterator.hasNext()) {
				content.append("*******************\n");
				dMaj = iterator.next();
				for (int i = 0; i < Globals.numberOfVariables + 1; i++) {
					for (int j = 0; j < Globals.numberOfVariables + 1; j++) {
						content.append(dMaj[i][j] + " ");
					}
					content.append("\n");
				}
			}

		}

		return content.toString();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matrixSet == null) ? 0 : matrixSet.hashCode());
		return result;
	}
	/**
	 * checks if a short matrix is equal to another short matrix. All the
	 * elements in the matrices should be equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ARAIDEFunctionShort other = (ARAIDEFunctionShort) obj;
		if (matrixSet == null) {
			if (other.matrixSet != null)
				return false;
		} else if (!matrixSet.equals(other.matrixSet))
			return false;
		return true;
	}

	@Override
	public Set<String> getUse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDef() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNotConstantFor(String use) {
		// TODO Auto-generated method stub
		return false;
	}

}
