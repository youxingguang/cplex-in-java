package CplexLearn;

import ilog.concert.*;
import ilog.cplex.*;

//直接由原问题获取对偶问题的极射线
public class getDualFarkas {

	public static void main(String[] args) {
		try {
			IloCplex model=new IloCplex();
			IloNumVar x1=model.numVar(0,Double.MAX_VALUE,"x1");
			IloNumVar x2=model.numVar(0,Double.MAX_VALUE,"x2");
			IloNumVar[] x= {x1,x2};
			double[] coef= {3.0,2.0};
			IloNumExpr Obj=model.scalProd(x, coef);
			model.addMinimize(Obj);
			
			//约束1
			double[] c1= {1.0,1.0};
			double[] c2= {2.0,1.0};
			IloRange r1=model.addLe(model.scalProd(x, c1), 5);
			IloRange r2=model.addGe(model.scalProd(x, c2), 6);
			IloRange r3=model.addGe(x1,6);
			
			
			model.setParam(IloCplex.Param.Preprocessing.Presolve, false);
//			model.setParam(IloCplex.Param.RootAlgorithm, IloCplex.Algorithm.Dual);
//			model.setParam(IloCplex.Param.Simplex.Display, 2);
		    
			model.exportModel("infea1.lp");
			model.solve();//只有当模型不可行时
			System.out.println("模型状态"+model.getStatus());
			if(model.getStatus()==IloCplex.Status.Infeasible)
			{
				int n=model.getNrows();
				IloRange[] r=new IloRange[n];
				double[] v=new double[n];
				model.dualFarkas(r, v);
				for(int i=0;i<r.length;i++)
				{
					System.out.println("v["+i+"]="+v[i]);
				}
			}
		
			
		} catch (IloException e) {
			
			e.printStackTrace();
		}

	}

}
