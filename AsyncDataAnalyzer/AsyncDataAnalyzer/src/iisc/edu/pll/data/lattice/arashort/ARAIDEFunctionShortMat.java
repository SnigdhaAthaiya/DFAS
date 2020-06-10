package iisc.edu.pll.data.lattice.arashort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.DoublePredicate;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.misc.RrefGaussJordanRowPivot_DDRM;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;

//import com.sun.xml.internal.bind.WhiteSpaceProcessor;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;



/**
 * 
 * @author Animesh Kumar Main Class containing all operations need for ARA
 *         lattice analysis @
 *
 */
public class ARAIDEFunctionShortMat implements TFunction {

	public Set<ShortMatrix> matrixSet;

	boolean isTop;
	boolean isBot;

	/**
	 * Create an Empty set of type ShortMatrix
	 */
	public ARAIDEFunctionShortMat() {
		// TODO Auto-generated constructor stub
		matrixSet = new HashSet<ShortMatrix>();
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
		ARAIDEFunctionShortMat fun = (ARAIDEFunctionShortMat) f;// typecasting to class object
		Set<ShortMatrix> A = matrixSet;// current object's matrixset
		Set<ShortMatrix> B = fun.matrixSet;// passed object's matrixset
		int dim = Globals.numberOfVariables + 1;// dimension of matrix
		int columnLimit = A.size() + B.size();// number of columns in the truncated matrix
		double[][] coordinateVectors = new double[dim * dim][columnLimit];// object to store the matrices in the form of
																			// vector
		List<short[][]> includeDMatrixRMajs = new ArrayList<>();
		Iterator<ShortMatrix> itrA = A.iterator();
		Iterator<ShortMatrix> itrB = B.iterator();
		short[][] matrixRMaj = new short[dim][dim];
		int columnCount = 0;
		int rowCount;
		// converting every matrix of A into column vectors. The matrices are converted
		// to column vectors row wise.
		while (itrA.hasNext()) {
			rowCount = 0;
			matrixRMaj = itrA.next().getMat();
			includeDMatrixRMajs.add(matrixRMaj);
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
			matrixRMaj = itrB.next().getMat();
			includeDMatrixRMajs.add(matrixRMaj);
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					coordinateVectors[rowCount][columnCount] = matrixRMaj[i][j];
					rowCount++;
				}
			}
			columnCount++;
		}
//Converting to DMatrixRMaj object for matrix calculations
		DMatrixRMaj dataMatrix = new DMatrixRMaj(coordinateVectors);
		// Calculating RREF of truncated matrix
		RrefGaussJordanRowPivot_DDRM dataM = new RrefGaussJordanRowPivot_DDRM();
		dataM.reduce(dataMatrix, columnLimit);

		boolean[] requiredColumns = new boolean[columnLimit];

		for (int i = 0; i < columnLimit; i++) {
			requiredColumns[i] = true;
		}
//picking the required matrix in the basis set.
//A set is picked if the column vector of rref matrix has a 1 such that it is preceed with only 0's in the previous columns
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
		ARAIDEFunctionShortMat resultAraideFunction = new ARAIDEFunctionShortMat();
		for (int i = 0; i < columnLimit; i++) {
			if (requiredColumns[i]) {
				if (includeDMatrixRMajs.get(i) == null) {
					System.out.println(i);
				}
				resultAraideFunction.matrixSet.add(new ShortMatrix(includeDMatrixRMajs.get(i)));

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

		ARAIDEFunctionShortMat function = (ARAIDEFunctionShortMat) f;
		Set<ShortMatrix> A = matrixSet;// matrix set of current object
		Set<ShortMatrix> AA = new HashSet<ShortMatrix>();// set to store the product

		Iterator<ShortMatrix> itrA = A.iterator();
		Iterator<ShortMatrix> itrB;
		SimpleMatrix LeftMatrix;
		SimpleMatrix RightMatrix;
		SimpleMatrix tempMatrix;

		double[][] doubleMat = new double[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];
		short[][] shortMat = new short[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];
		while (itrA.hasNext()) {
			doubleMat = ARAIDEFunctionShortMat.convertdouble(itrA.next().getMat(), (Globals.numberOfVariables + 1),
					(Globals.numberOfVariables + 1));// getting next element in the set and converting it to double
														// matrix
			LeftMatrix = new SimpleMatrix(doubleMat);
			itrB = function.matrixSet.iterator();
			while (itrB.hasNext()) {
				doubleMat = ARAIDEFunctionShortMat.convertdouble(itrB.next().getMat(), Globals.numberOfVariables + 1,
						Globals.numberOfVariables + 1);// getting next element in the set and converting it to double
														// matrix
				RightMatrix = new SimpleMatrix(doubleMat);
				tempMatrix = LeftMatrix.mult(RightMatrix);// finding product of matrices
				shortMat = convertshort(tempMatrix, Globals.numberOfVariables + 1, Globals.numberOfVariables + 1);// converting
																													// back
																													// to
																													// short
				AA.add(new ShortMatrix(shortMat));// addding to set
			}
		}

		ARAIDEFunctionShortMat resultAraideFunction = new ARAIDEFunctionShortMat();
		resultAraideFunction = (ARAIDEFunctionShortMat) findBasis(AA);// finding basis of the set containing product
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
		ARAIDEFunctionShortMat function = (ARAIDEFunctionShortMat) v;
		if (this.matrixSet.isEmpty() && function.matrixSet.isEmpty()) {// check if both are empty
			result = true;
		} else {
			ARAIDEFunctionShortMat unionFunction = (ARAIDEFunctionShortMat) this.join(v);
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
	 * @param v Transfer function
	 * @return boolean if transfer function is greater than or equal to another
	 *         transfer function.
	 */
	public boolean isStrictlyGreater(TFunction v) {
		boolean result = false;
		ARAIDEFunctionShortMat function = (ARAIDEFunctionShortMat) v;
		if (this.matrixSet.isEmpty()) {
			return result;
		} else {
			boolean flagA = false;
			boolean flagB = false;
			ARAIDEFunctionShortMat unionFunctionA = (ARAIDEFunctionShortMat) this.join(v);
			ARAIDEFunctionShortMat unionFunctionB = (ARAIDEFunctionShortMat) function.join(this);
			if (unionFunctionA.matrixSet.size() <= matrixSet.size()) {
				flagA = true;
			}
			if (unionFunctionB.matrixSet.size() > function.matrixSet.size()) {
				flagB = true;
			}
			if (flagA && flagB) {
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
			Iterator<ShortMatrix> aIterator = matrixSet.iterator();
			short[][] A = aIterator.next().getMat();
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					if (i == j && A[i][j] != 1) {// check diagonal elements
						flag = false;
						break;
					}
					if (i != j && A[i][j] != 0) {// all other elements
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
	 * @param A Set Matrices of type short
	 * @return Transfer function containing the basis set
	 */
	public static TFunction findBasis(Set<ShortMatrix> A) {

		// TODO Auto-generated method stub

		int dim = Globals.numberOfVariables + 1;// dimension of the matrix
		int columnLimit = A.size();// number of columns in the truncated matrix
		double[][] coordinateVectors = new double[dim * dim][columnLimit];
		short[][][] includeDMatrixRMajs = new short[columnLimit][dim][dim];

		Iterator<ShortMatrix> itrA = A.iterator();
		short[][] matrixRMaj = new short[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];
		int columnCount = 0;
		int rowCount;
		// converting every matrix of A into column vectors. The matrices are converted
		// to column vectors row wise.
		while (itrA.hasNext()) {
			rowCount = 0;
			matrixRMaj = itrA.next().getMat();
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
		ARAIDEFunctionShortMat resultAraideFunction = new ARAIDEFunctionShortMat();

		for (int i = 0; i < columnLimit; i++) {
			if (requiredColumns[i]) {
				resultAraideFunction.matrixSet.add(new ShortMatrix(includeDMatrixRMajs[i]));
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
		Iterator<ShortMatrix> iterator = matrixSet.iterator();
		short[][] dMaj = new short[Globals.numberOfVariables + 1][Globals.numberOfVariables + 1];

		if (matrixSet.isEmpty())
			content.append("ID Function");
		else {
			while (iterator.hasNext()) {
				content.append("*******************\n");
				dMaj = iterator.next().getMat();
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
	 * checks if a ShortMatrix object is equal to another ShortMatrix Object All the
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
		ARAIDEFunctionShortMat other = (ARAIDEFunctionShortMat) obj;
		if (matrixSet == null) {
			if (other.matrixSet != null)
				return false;
		} else if (!matrixSet.equals(other.matrixSet))
			return false;
		return true;
	}

	/**
	 * Method to verify the results.To check if a vector belongs to the Span of a
	 * vector Space
	 * 
	 * @param f Transfer function which is to be Spanned
	 * @param g Transfer function needed which is to checked with the Span
	 * @return booleon true if verified false if unverified
	 */
	public static boolean verify(TFunction f, TFunction g) {
		boolean flag = true;
		ARAIDEFunctionShortMat FfunctionShortMat = (ARAIDEFunctionShortMat) f;// typecasting to class object
		ARAIDEFunctionShortMat GFunctionShortMat = (ARAIDEFunctionShortMat) g;// typecasting to type object
		Set<ShortMatrix> A = FfunctionShortMat.matrixSet;// matrix set of f
		Set<ShortMatrix> B = GFunctionShortMat.matrixSet;// matrix set of g
		int dim = Globals.numberOfVariables + 1;// dimension of matrix
		int columnLimit = A.size();// number of columns in truncated matrix
		double[][] coordinateVectors = new double[dim * dim][columnLimit + 1];
		Iterator<ShortMatrix> itrA = A.iterator();
		short[][] matrixRMaj = new short[dim][dim];
		int columnCount = 0;
		int rowCount;
		// converting every matrix of A into column vectors. The matrices are converted
		// to column vectors row wise.
		while (itrA.hasNext()) {
			rowCount = 0;
			matrixRMaj = itrA.next().getMat();
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					coordinateVectors[rowCount][columnCount] = matrixRMaj[i][j];
					rowCount++;
				}
			}
			columnCount++;
		}
		RrefGaussJordanRowPivot_DDRM dataM = new RrefGaussJordanRowPivot_DDRM();
		DMatrixRMaj dataMatrix = new DMatrixRMaj(coordinateVectors);
		Iterator<ShortMatrix> itrB = B.iterator();

		double[][] checkVector = new double[dim * dim][1];
		//converting every matrix in matrix set of B into cloumn vector and adding it to matrix composing of matrices of set A
		while (itrB.hasNext()) {

			rowCount = 0;
			matrixRMaj = itrB.next().getMat();
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					checkVector[rowCount][0] = matrixRMaj[i][j];
					rowCount++;
				}
			}

			DMatrixRMaj rREFMatrixRMaj = new DMatrixRMaj(dataMatrix);

			for (int i = 0; i < rREFMatrixRMaj.getNumRows(); i++) {
				rREFMatrixRMaj.set(i, columnLimit, checkVector[i][0]);
			}
//finding rref of the truncated matrix
			dataM.reduce(rREFMatrixRMaj, rREFMatrixRMaj.getNumCols() - 1);
// result is verified if all the columns in  is zero or any other column is non zero if last column is non zero.
// This should be true for all the rows
			int count = 0;
			for (int i = 0; i < rREFMatrixRMaj.getNumRows(); i++) {
				count = 0;

				for (int j = 0; j < (rREFMatrixRMaj.getNumCols() - 1); j++) {
					if (rREFMatrixRMaj.get(i, j) == 0) {
						count++;
					}
				}
				if (count == (rREFMatrixRMaj.getNumCols() - 1)
						&& (rREFMatrixRMaj.get(i, (rREFMatrixRMaj.getNumCols() - 1)) != 0)) {
					flag = false;
					return flag;
				}
			}

		}
		// clearing data structures
		A = null;
		B = null;
		coordinateVectors = null;
		itrA = null;
		matrixRMaj = null;
		dataM = null;
		dataMatrix = null;
		itrB = null;

		return flag;
	}

	/**
	 * This method is used to check if the results obtained satisfy the assertion
	 * check. Assertion check is satisfied if product of every matrix in the
	 * transfer function with the assertion vector is a zero vector.
	 * 
	 * @author Animesh Kumar
	 * @param f      Tfunction Object containing the set of matrices to be checked
	 *               with the assertion vector
	 * @param vector Assertion vector obtained by converting the assertion equation
	 *               in vector form with all the terms on the same side of the
	 *               equality sign.
	 * @return boolean Returns True if check is passed else returns false
	 */
	public static boolean resultCheck(TFunction f, short[][] vector) {
		boolean flag = true;// flag to print the result for complete label

		ARAIDEFunctionShortMat FfunctionShortMat = (ARAIDEFunctionShortMat) f;
		Set<ShortMatrix> A = FfunctionShortMat.matrixSet;
		int dim = Globals.numberOfVariables + 1;

		Iterator<ShortMatrix> itrA = A.iterator();
		short[][] matrixRMaj = new short[dim][dim];
		// converting the assertion vector from short to double
		double[][] vectorDoubles = convertdouble(vector, dim, 1);
		// creating a SimpleMatrix object of the assertion vector to call multiply
		// operation
		SimpleMatrix bMatrix = new SimpleMatrix(vectorDoubles);
		double[][] matrixRMajDouble = new double[dim][dim];
		while (itrA.hasNext()) {
			matrixRMaj = itrA.next().getMat();
			matrixRMajDouble = convertdouble(matrixRMaj, dim, dim);
			// creating SimpleMatrix object of a matrix from the result set
			SimpleMatrix aMatrix = new SimpleMatrix(matrixRMajDouble);
			// multiplying with assertion vector
			SimpleMatrix resuMatrix = aMatrix.mult(bMatrix);
			// the assertion check is true only if the vector obtained by multiplying the
			// result matrix with assertion vector is a zero vector
			for (int i = 0; i < resuMatrix.numRows(); i++) {
				if (resuMatrix.get(i, 0) != 0) {
					flag = false;// setting the final flag to false if check is not passed
					
					return flag;// returning if flag is not true
				}
			}
		}
		return flag;
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
