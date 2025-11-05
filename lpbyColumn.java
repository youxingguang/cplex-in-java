package solver;
import ilog.concert.*;
import ilog.cplex.*;
/*
 * max <(6,4,7,5),x>
 * s.t. Ax<=b
 * A={{1,2,1,2},{6,5,3,2},{3,4,9,12}};
 * b={20,100,75}
 * x_i>=0
 */
public class lpbyColumn {
	static void populateByColumn(IloMPModeler model,
			IloNumVar[][] var,IloRange[][] rng)throws IloException
	{
		//先定框架,再逐一填充
		IloObjective obj=model.addMaximize();
		rng[0]=new IloRange[3];
		double[] lhs= {-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE};
		double[] rhs= {20,100,75};
		for(int i=0;i<3;i++)
		{
		 rng[0][i]=model.addRange(lhs[i],rhs[i]);
		}
		//到目前为止，这只是一个空的框架,但是限定了边界
		//逐列添加
		var[0]=new IloNumVar[4];
		double[] c= {6,4,7,5};
		double[][] A={{1,2,1,2},{6,5,3,2},{3,4,9,12}};
		
		/*
		 * 对每一列,包括目标和约束,下面仅给出x[0]
		 * rng[0][0]--第一行约束,rng[0][1]--第二行约束...
		 * 
		 * IloColumn col=model.column(obj,6).and(
		 * model.column(rng[0][0],1)).and(
		 * rng[0][1],6).and(model.column(rng[0][2],3));
		 * 
		 * var[0][0]=model.numVar(col,lb,ub)
		 */
		IloColumn[] col=new IloColumn[4];
		for(int j=0;j<4;j++)
		{
			col[j]=model.column(obj,c[j]);
			for(int k=0;k<3;k++)
			{
				col[j]=col[j].and(model.column(rng[0][k],A[k][j]));
			}
			var[0][j]=model.numVar(col[j],0,Double.MAX_VALUE);
		}
	}
		
	public static void main(String[] args) {
		try {
			IloCplex cplex=new IloCplex();
			IloNumVar[][] var=new IloNumVar[1][];
			IloRange[][] rng=new IloRange[1][];
			populateByColumn(cplex,var,rng);
			if(cplex.solve())
			{
				double[] x=cplex.getValues(var[0]);

				System.out.println("obj value="+cplex.getObjValue());
				for(int i=0;i<4;i++)
				{
					System.out.println("x["+(i+1)+"]="+x[i]);
				}
			}
			
			}catch(IloException ex) {
				System.out.println("Concert Error: " + ex);
			}

	}

}
