package CplexLearn;
import ilog.concert.*;
import ilog.cplex.*;
public class getDual {

  //直接从原问题获得对偶值
	public static void main(String[] args) throws IloException {
		IloCplex model=new IloCplex();
		int colNum=2;
		IloNumVar[] x=new IloNumVar[colNum];
	
		x[0]=model.numVar(0,Double.MAX_VALUE);
		x[1]=model.numVar(0,Double.MAX_VALUE);
//		x[0]=model.numVar(0,2);
//		x[1]=model.numVar(0,2);
		//model.setParam(IloCplex.Param.Preprocessing.Presolve, false);
		model.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Dual);
		double[] c1= {1.0,1.0};
		double[] c2= {2.0,2.0};
		IloRange r1=model.addGe(model.scalProd(c1, x),4.0,"r1");
		IloRange r2=model.addGe(model.scalProd(c2, x),8.0,"r2");
		IloRange r3=model.addLe(x[0],2.0,"r3");
		IloRange r4=model.addLe(x[1],2.0,"r4");
		
		double[] obj_coeff= {2.0,3.0};
		IloNumExpr Obj=model.scalProd(obj_coeff,x);
		model.addMinimize(Obj);
		
		if(model.solve()) {
			System.out.println("原问题目标："+model.getObjValue());
			System.out.println("原问题的状态："+model.getStatus());
		
			double pi1=model.getDual(r1);
			double pi2=model.getDual(r2);
			double pi3=model.getDual(r3);
			double pi4=model.getDual(r4);
			
			System.out.println("pi1="+pi1);
			System.out.println("pi2="+pi2);
			System.out.println("pi3="+pi3);
			System.out.println("pi4="+pi4);
			
			double dualObj=4*pi1+4*pi2+2*pi3+2*pi4;
			//double dualObj=4*pi1+8*pi2;
			
			System.out.println("对偶目标: "+dualObj);
		
		}
		model.end();
	}

}
