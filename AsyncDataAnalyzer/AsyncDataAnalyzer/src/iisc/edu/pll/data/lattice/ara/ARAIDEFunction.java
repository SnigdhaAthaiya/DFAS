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
package iisc.edu.pll.data.lattice.ara;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.dense.row.misc.RrefGaussJordanRowPivot_DDRM;
import org.ejml.ops.ConvertDMatrixStruct;
import org.ejml.simple.SimpleMatrix;
import iisc.edu.pll.analysis.Globals;
import iisc.edu.pll.data.lattice.AbstractValue;
import iisc.edu.pll.data.lattice.TFunction;

public class ARAIDEFunction implements TFunction {
	
	Set<DMatrixRMaj> matrixSet;
	
	boolean isTop;
	boolean isBot;
	
	public ARAIDEFunction() {
		// TODO Auto-generated constructor stub
		matrixSet = new HashSet<DMatrixRMaj>();
	}

	@Override
	public AbstractValue apply(AbstractValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TFunction join(TFunction f) {
		// TODO Auto-generated method stub
		ARAIDEFunction fun = (ARAIDEFunction) f;
		Set<DMatrixRMaj> A = matrixSet;
		Set<DMatrixRMaj> B = fun.matrixSet;
		int dim = Globals.numberOfVariables+1;
		int columnLimit = A.size()+B.size();
        double[][] coordinateVectors = new double[dim*dim][columnLimit];
        DMatrixRMaj[] includeDMatrixRMajs = new DMatrixRMaj[columnLimit];
        Iterator<DMatrixRMaj> itrA = A.iterator();
        Iterator<DMatrixRMaj> itrB = B.iterator();
        DMatrixRMaj matrixRMaj= new DMatrixRMaj(dim,dim);
        int columnCount =0;
        int rowCount;
        while(itrA.hasNext()){
             	rowCount= 0; 
            	matrixRMaj = itrA.next();
            includeDMatrixRMajs[columnCount]= matrixRMaj;
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    coordinateVectors[rowCount][columnCount] = matrixRMaj.get(i, j);
                    rowCount++;
                }
            }
           columnCount++;
           
        }
        while(itrB.hasNext()){
            rowCount=0;
            	matrixRMaj = itrB.next();
            	includeDMatrixRMajs[columnCount]= matrixRMaj;
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    coordinateVectors[rowCount][columnCount] = matrixRMaj.get(i,j);
                    rowCount++;
                }
            }
            columnCount++;
        }

        DMatrixRMaj dataMatrix = new DMatrixRMaj(coordinateVectors);
        RrefGaussJordanRowPivot_DDRM dataM = new RrefGaussJordanRowPivot_DDRM();
        dataM.reduce(dataMatrix,columnLimit);

        boolean[]requiredColumns = new boolean[columnLimit];

        for (int i =0;i<columnLimit;i++)
        {
          requiredColumns[i]=true;
        }
        
//        for(int i =0;i<columnLimit;i++)
//        {try {
//            if(dataMatrix.get(i,i)==1){
//                for(int j =0;j<dim*dim;j++)
//                {
//                	if(i==4) {
//                		System.out.println("remove this");
//                	}
//                    if(j!=i){
//                        if(dataMatrix.get(j,i)!=0){
//                            requiredColumns[i]=false;
//                        }
//                    }
//                }
//            }
//            else{
//                requiredColumns[i]=false;
//            }}
//        catch (Exception e) {
//			System.out.println("I am here");
//		}
//        }
        for(int j =0;j<columnLimit;j++)
        {
        	for(int i=0;i<dim*dim;i++) {
        		if(dataMatrix.get(i, j)==1) {
        			for(int k=j-1;k>=0;k-- ) {
        				if(dataMatrix.get(i, k)!=0) {
        					requiredColumns[j]=false;
        				}
        			}
        		}
        	}
        }
        
        ARAIDEFunction resultAraideFunction = new ARAIDEFunction();
        for(int i =0;i<columnLimit;i++){
            if (requiredColumns[i]){
            	if(includeDMatrixRMajs[i]== null) {
            		System.out.println(i);
            	}
            	resultAraideFunction.matrixSet.add(includeDMatrixRMajs[i]);
            	
            }
        }
        return resultAraideFunction;
		
	}

	@Override
	public TFunction compose(TFunction f) {
		
		ARAIDEFunction function = (ARAIDEFunction)f;
		Set<DMatrixRMaj> A = matrixSet;
		Set<DMatrixRMaj> AA = new HashSet<DMatrixRMaj>();
		
        Iterator<DMatrixRMaj> itrA = A.iterator();
        Iterator<DMatrixRMaj> itrB;
        SimpleMatrix LeftMatrix;
        SimpleMatrix RightMatrix;
        DMatrixRMaj typeDMatrixRMaj;
        while(itrA.hasNext())
        {
            LeftMatrix = SimpleMatrix.wrap(itrA.next());
         //    Set<DMatrixRMaj>B = function.matrixSet;
            itrB = function.matrixSet.iterator();
            while(itrB.hasNext()){
                RightMatrix = SimpleMatrix.wrap(itrB.next());
                LeftMatrix = LeftMatrix.mult(RightMatrix);
                typeDMatrixRMaj = (DMatrixRMaj)LeftMatrix.getDDRM();
                AA.add(typeDMatrixRMaj);
            }  
        }
        
        ARAIDEFunction resultAraideFunction = new ARAIDEFunction();
        resultAraideFunction = (ARAIDEFunction)findBasis(AA);
        return resultAraideFunction;
		// TODO Auto-generated method stub
	}

//	@Override
//	public boolean isGreater(TFunction v) {
//		
//		ARAIDEFunction function = (ARAIDEFunction)v;
//		
//		if(this.matrixSet.size()>= function.matrixSet.size()) {
//		return true;	
//		}
//		else {
//			return false;
//		}
//	}
	
	@Override
	public boolean isGreater(TFunction v) {
		boolean result = false;
		ARAIDEFunction function = (ARAIDEFunction)v;
		if(this.matrixSet.isEmpty()&&function.matrixSet.isEmpty()) {
				result=true;
		}
		else {
			ARAIDEFunction unionFunction =(ARAIDEFunction) this.join(v);
			if(unionFunction.matrixSet.size()<= this.matrixSet.size()) {
				result=true;
			}
		}
		return result;
		
	}
	
	public boolean isStrictlyGreater(TFunction v) {
		boolean result = false;
		ARAIDEFunction function = (ARAIDEFunction)v;
		if(this.matrixSet.isEmpty()) {
			return result;
			}
		else {
			boolean flagA=false;
			boolean flagB=false;
			ARAIDEFunction unionFunctionA =(ARAIDEFunction) this.join(v);
			ARAIDEFunction unionFunctionB =(ARAIDEFunction) function.join(this);
			if(unionFunctionA.matrixSet.size()<matrixSet.size()) {
				flagA = true;
			}
			if(unionFunctionB.matrixSet.size()<function.matrixSet.size()) {
				flagB=true;
			}
			if(flagA&&(!flagB)) {
				result =true;
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

	@Override
	public boolean isID() {
		boolean flag = false; 
		if(this.matrixSet.size()==1) {
			flag=true;
			Iterator<DMatrixRMaj>aIterator = matrixSet.iterator();
			DMatrixRMaj A = aIterator.next();
			  for(int i = 0; i < A.getNumRows(); i++){    
	                for(int j = 0; j < A.getNumCols(); j++){    
	                  if(i == j && A.get(i, j) != 1){    
	                      flag = false;    
	                      break;    
	                  }    
	                  if(i != j && A.get(i, j) != 0){    
	                      flag = false;    
	                      break;    
	                  }    
	                }    
	            }    
		}
		// TODO Auto-generated method stub
		return flag;
	}
	
	public static TFunction findBasis(Set<DMatrixRMaj> A) {
		
		// TODO Auto-generated method stub
		
				int dim = Globals.numberOfVariables+1;
				int columnLimit = A.size();
		        double[][] coordinateVectors = new double[dim*dim][columnLimit];
		        DMatrixRMaj[] includeDMatrixRMajs = new DMatrixRMaj[columnLimit];
		      
		       
		        
		        Iterator<DMatrixRMaj> itrA = A.iterator();

		        int columnCount =0;
		        int rowCount;
		        while(itrA.hasNext()){
		             rowCount= 0;
		            DMatrixRMaj matrixRMaj = itrA.next();
		            includeDMatrixRMajs[columnCount]= matrixRMaj;
		            for (int i = 0; i < dim; i++) {
		                for (int j = 0; j < dim; j++) {
		                    coordinateVectors[rowCount][columnCount] = matrixRMaj.get(i, j);
		                    rowCount++;
		                }
		            }
		           columnCount++;
		           
		        }
		        
		        DMatrixRMaj dataMatrix = new DMatrixRMaj(coordinateVectors);
		       // dataMatrix.print();
		        RrefGaussJordanRowPivot_DDRM dataM = new RrefGaussJordanRowPivot_DDRM();
		        dataM.reduce(dataMatrix,columnLimit);

		        boolean[]requiredColumns = new boolean[columnLimit];

		        for (int i =0;i<columnLimit;i++)
		        {
		          requiredColumns[i]=true;
		        }


//		        for(int i =0;i<columnLimit;i++)
//		        {
//		            if(dataMatrix.get(i,i)==1){
//		                for(int j =0;j<dim*dim;j++)
//		                {
//		                    if(j!=i){
//		                        if(dataMatrix.get(j,i)!=0){
//		                            requiredColumns[i]=false;
//		                        }
//		                    }
//		                }
//		            }
//		            else{
//		                requiredColumns[i]=false;
//		            }
//		        }
		        for(int j =0;j<columnLimit;j++)
		        {
		        	for(int i=0;i<dim*dim;i++) {
		        		if(dataMatrix.get(i, j)==1) {
		        			for(int k=j-1;k>=0;k-- ) {
		        				if(dataMatrix.get(i, k)!=0) {
		        					requiredColumns[j]=false;
		        				}
		        			}
		        		}
		        	}
		        }
		        ARAIDEFunction resultAraideFunction = new ARAIDEFunction();
		      
		        for(int i =0;i<columnLimit;i++){
		            if (requiredColumns[i]) {
		            	resultAraideFunction.matrixSet.add(includeDMatrixRMajs[i]);
		            }
		        }
		        return resultAraideFunction;
				
		
	}
	@Override
	public String toString() {
		StringBuffer content = new StringBuffer();
		Iterator<DMatrixRMaj> iterator = matrixSet.iterator();
		
		if (matrixSet.isEmpty())
			content.append("ID Function");
		else {
			while(iterator.hasNext()) {
				content.append("*******************\n");
				DMatrixRMaj dMaj =iterator.next();
				for (int i =0;i<dMaj.numRows;i++){
					for(int j=0;j<dMaj.numCols;j++)
					{
						content.append(dMaj.get(i, j)+" ");
					}
					content.append("\n");
				}
			}

		}

		return content.toString();

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
