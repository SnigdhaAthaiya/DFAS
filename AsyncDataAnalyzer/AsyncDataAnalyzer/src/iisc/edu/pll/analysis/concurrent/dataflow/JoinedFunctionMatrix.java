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
package iisc.edu.pll.analysis.concurrent.dataflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.TFunction;

public class JoinedFunctionMatrix {

	/*
	 * this will be a nested vector of depth Globals.numberOfModules,
	 * representing a Multidimensional matrix of type Function[][]..[]
	 */
	Vector pathsJoinFunctionWithDemand;
	int[] sizes; // size of matrix in each dimension

	public JoinedFunctionMatrix(Vector vec, int[] sizes) {
		pathsJoinFunctionWithDemand = vec;
		this.sizes = sizes;
	}

	public JoinedFunctionMatrix() {
		pathsJoinFunctionWithDemand = new Vector<>();
	}

	public TFunction get(int[] location) {
		int level = 0;
		Vector curr = pathsJoinFunctionWithDemand;

		/*
		 * // sanity check synchronized (curr) { if (resizeRequired(location))
		 * return null; }
		 */
		try {
			while (curr != null && level <= Globals.numberOfCounters - 2) {
				synchronized (curr) {
					if (curr.size() >= (location[level] + 1)) {
						Object currValue = curr.get(location[level]);
						if (currValue != null)
							curr = (Vector) currValue;
					}
				}
				level++;
			}

			if (curr != null) {
				synchronized (curr) {
					if (curr.size() >= (location[level] + 1)) {
						Object currValue = curr.get(location[level]);
						if (currValue != null)
							return (TFunction) currValue;
					}

				}
			}
		} catch (ClassCastException cc) {
			System.out.println(cc.getMessage());
			cc.printStackTrace();
			System.out.println("the location and sizes for error at level " + level);
			for (int i = 0; i < Globals.numberOfCounters; i++)
				System.out.println("size : " + sizes[i] + " \t demand: " + location[i]);
		}
		return null;

	}

	public List<TFunction> getAllLower(int[] demand) {

		ArrayList<TFunction> list = new ArrayList<>();
		synchronized (pathsJoinFunctionWithDemand) {
			list = (ArrayList<TFunction>) getAllLower(pathsJoinFunctionWithDemand, demand, 0, list);
		}
		
		return list;

	}

	private List<TFunction> getAllLower(Vector vec, int[] demand, int level, List<TFunction> list) {

		int loc = demand[level];
		if (vec == null || vec.size() <= loc)
			return list;
		try {
			if (level == Globals.numberOfCounters - 1) {
				for (int i = 0; i <= demand[level]; i++) {
					synchronized (vec) {
						if (vec.get(i) != null)
							list.add(((TFunction) vec.get(i)));
					}

				}
				return list;
			}

			for (int i = 0; i <= demand[level]; i++) {
				Object value = vec.get(i);
				if (value != null)
					list = getAllLower((Vector) value, demand, level + 1, list);
			}
		} catch (ArrayIndexOutOfBoundsException ai) {
			System.out.println("demand and sizes");
			for (int i = 0; i < Globals.numberOfCounters; i++)
				System.out.println("size : " + sizes[i] + "\t demand : " + demand[i]);
			System.out.println("Thread " + Thread.currentThread().getId() + " out of bound " + " at level " + level
					+ " demand value : " + demand[level]);
			ai.printStackTrace();
		}

		return list;

	}

	public void set(TFunction value, int[] location) {

		int level = 0;
		Vector curr = pathsJoinFunctionWithDemand;
		if (resizeRequired(location))
			curr = expandSize(curr, location, level);
		while (level <= Globals.numberOfCounters - 2) {
			synchronized (curr) {
				curr = (Vector) curr.get(location[level]);
			}
			level++;
		}
		synchronized (curr) {
			curr.set(location[level], value);
		}

	}

	/**
	 * This function takes as input the function value and location and then
	 * adds the function value to the already existing value. In case the entry
	 * i null, it is replaced by that value
	 */
	public void setAndJoin(TFunction value, int[] location) {
		int level = 0;
		try {

			Vector curr = pathsJoinFunctionWithDemand;
			if (resizeRequired(location)) {
				curr = expandSize(curr, location, level);
			}

			while (level <= Globals.numberOfCounters - 2) {
				synchronized (curr) {
					curr = (Vector) curr.get(location[level]);
				}
				level++;
			}
			synchronized (curr) {
				int loc = location[level];
				TFunction f = (TFunction) curr.get(loc);
				if (f == null)
					curr.set(loc, value);
				else
					curr.set(loc, f.join(value));
			}
		} catch (ArrayIndexOutOfBoundsException aio) {
			System.out.println(aio.getMessage());
			aio.printStackTrace();
			System.out.println("the location and sizes for error at level " + level);
			for (int i = 0; i < Globals.numberOfCounters; i++)
				System.out.println("size : " + sizes[i] + " \t demand: " + location[i]);
		}

	}

	public boolean resizeRequired(int[] newSize) {

		for (int i = 0; i < Globals.numberOfCounters; i++)
			if (newSize[i] > (sizes[i] - 1))
				return true;

		return false;

	}

	private Vector expandSize(Vector vec, int[] loc, int level) {

		int currSize = sizes[level] > loc[level] ? sizes[level] : loc[level] + 1;
		sizes[level] = currSize;
		if (vec == null) // for the extended parts of the matrix that will
							// eventually contain null
		{
			if (level == Globals.numberOfCounters - 1) {
				Vector head = new Vector<TFunction>(currSize);
				for (int i = 0; i < currSize; i++) {
					head.add(null);
				}
				return head;
			}
			Vector head = new Vector<TFunction>(currSize);
			int newLevel = level + 1;
			for (int i = 0; i < currSize; i++) {
				head.add(expandSize(null, loc, newLevel));
			}
			return head;
		}

		if (level == Globals.numberOfCounters - 1) {
			synchronized (vec) {
				vec.setSize(currSize);
			}
			return vec;
		}
		int newLevel = level + 1;

		synchronized (vec) { // this looks like a long locking period, locking
								// the full vector
			vec.setSize(currSize);
		}
		for (int i = 0; i < vec.size(); i++) {
			Vector v = expandSize((Vector) vec.get(i), loc, newLevel);
			synchronized (v) {
				vec.set(i, v);
			}
		}
		for (int i = vec.size(); i < currSize; i++) {
			Vector v = expandSize(null, loc, newLevel);
			synchronized (v) {
				vec.set(i, v);
			}
		}
		return vec;
	}

	private Vector newExpandSize(Vector vec, int[] loc, int level) {

		// System.out.println("newExpandSize called");
		int currSize = loc[level] + 1;
		int currloc = currSize - 1;

		if (vec == null) {
			if (level == Globals.numberOfCounters - 1) {
				vec = new Vector<>(currSize);
				for (int i = 0; i < currSize; i++)
					vec.add(null);
				return vec; // base case
			}
			vec = new Vector<>(currSize);
			for (int i = 0; i < currSize; i++)
				vec.add(null);

			vec.set(currloc, expandSize(null, loc, level + 1)); // recursive
																// call to
																// populate
																// other vectors
			return vec;
		}

		// base case, vector not null
		if (level == Globals.numberOfCounters - 1) {
			synchronized (vec) {
				if (vec.size() < currSize)
					vec.setSize(currSize);
			}
			return vec;
		}

		if (vec.size() > loc[level] + 1) {
			// no need to resize this vector, simply go deeper
			// IMPORTANT : the next level entry could be null, therefore create
			// new vector
			Object vecObj = vec.get(currloc);
			if (vecObj == null) {
				vec.set(currloc, expandSize(null, loc, level + 1));
			} else {
				vec.set(currloc, expandSize((Vector) vecObj, loc, level + 1));
			}

		} else {
			// resize the current vector
			synchronized (vec) {
				vec.setSize(currSize);
			}
			// now the entry there does not exist, call expand size with null,
			// and increased level
			vec.set(currloc, expandSize(null, loc, level + 1));
		}

		return vec;
	}

	private boolean newResizeRequired(Vector vec, int[] newSize) {
		int level = 0;
		Vector currvec = vec;
		while (currvec != null && level <= Globals.numberOfCounters - 2) {
			synchronized (currvec) {
				if (currvec.size() <= newSize[level])
					return true;
			}
			Object currVecObj = currvec.get(newSize[level]);
			if (currVecObj != null)
				currvec = (Vector) currVecObj;
			else
				return true;
			level++;

		}
		if (level == Globals.numberOfCounters - 1 && currvec != null)
			synchronized (currvec) {
				if (currvec.size() <= newSize[level])
					return true;
			}

		return false;
	}

	@Override
	public synchronized String toString() {
		StringBuffer content = new StringBuffer();
		content = getMatrixContents(pathsJoinFunctionWithDemand, Globals.numberOfCounters, content);

		return content.toString();

	}

	private StringBuffer getMatrixContents(Vector vec, int level, StringBuffer content) {

		if (vec == null)
			return content;
		if (level == 1) {
			content.append("\n");
			for (int i = 0; i < vec.size(); i++)
				content.append(i + "\t" + vec.get(i) + "\n");

			return content;
		}

		for (int i = 0; i < vec.size(); i++) {
			content.append(i + "\t");
			content = getMatrixContents((Vector) vec.get(i), level - 1, content);
			content.append("\n");
		}

		return content;
	}

	public boolean newResizeRequired(int[] dVector) {

		return newResizeRequired(pathsJoinFunctionWithDemand, dVector);
	}

}
