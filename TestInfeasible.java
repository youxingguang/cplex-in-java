package CplexLearn;
import ilog.cplex.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ilog.concert.*;
public class TestInfeasible {

  //使用冲突诊断定位问题不可行约束
	public static void main(String[] args) throws IloException {
		getConflict();
	}
	public static void getConflict() throws IloException
	{
		IloCplex cplex=new IloCplex();
		cplex.importModel("infea1.lp");//选择读入模型
		
		IloLPMatrix lp = (IloLPMatrix)cplex.LPMatrixIterator().next();

        IloRange[] rng = lp.getRanges();//获取约束

        // 计算非二进制变量 数
        final int numVars = cplex.getNcols() - cplex.getNbinVars();
        
        // SOS约束 数
        final int numSOS = cplex.getNSOSs();

        // Gather array of constraints to be considered by the conflict
        // refiner.
        List<IloConstraint> constraints = new ArrayList<IloConstraint>();
        for (int i = 0; i < rng.length; ++i) {
           constraints.add(rng[i]);
        }

        // Add variable bounds to the constraints array.
        for (IloNumVar v : lp.getNumVars()) {
           if (v.getType() != IloNumVarType.Bool) {
              constraints.add(cplex.lowerBound(v));
              constraints.add(cplex.upperBound(v));
           }
        }

        // Add SOSs to the constraints array.
        if (numSOS > 0) {
           Iterator s1 = cplex.SOS1iterator();
           while (s1.hasNext()) {
              IloSOS1 cur = (IloSOS1)s1.next();
              constraints.add(cur);
           }
           Iterator s2 = cplex.SOS2iterator();
           while (s2.hasNext()) {
              IloSOS2 cur = (IloSOS2)s2.next();
              constraints.add(cur);
           }
        }

        // Define preferences for the constraints. Here, we give all
        // constraints a preference of 1.0, so they will be treated
        // equally.
        double[] prefs = new double[constraints.size()];
        for (int i = 0; i < prefs.length; ++i) {
           prefs[i] = 1.0;
        }

        IloConstraint[] cons = constraints.toArray(
           new IloConstraint[constraints.size()]);

        // Run the conflict refiner. As opposed to letting the conflict
        // refiner run to completion (as is done here), the user can set
        // a resource limit (e.g., a time limit, an iteration limit, or
        // node limit) and still potentially get a "possible" conflict.
        if (cplex.refineConflict(cons, prefs)) {
           // Display the solution status.
           System.out.println("Solution status = " + cplex.getCplexStatus());

           // Get the conflict status for the constraints that were specified.
           IloCplex.ConflictStatus[] conflict = cplex.getConflict(cons);

           // Count the number of conflicts found for each constraint group and
           // print the results.
           int numConConflicts = 0;
           int numBoundConflicts = 0;
           int numSOSConflicts = 0;
           for (int i = 0; i < cons.length; ++i) {
              IloConstraint c = cons[i];
              if (conflict[i] == IloCplex.ConflictStatus.Member ||
                  conflict[i] == IloCplex.ConflictStatus.PossibleMember) {
                 if (c instanceof IloRange)
                    numConConflicts++;
                 else if (c instanceof IloNumVarBound)
                    numBoundConflicts++;
                 else
                    numSOSConflicts++;
              }
           }

           // Display a conflict summary.
           System.out.println("Conflict Summary:");
           System.out.println("  Constraint conflicts     = " + numConConflicts);
           System.out.println("  Variable Bound conflicts = " + numBoundConflicts);
           System.out.println("  SOS conflicts            = " + numSOSConflicts);

           // Write the identified conflict in the LP format.
           final String confFile = "ConflictAnalysisMaster.lp";
           System.out.printf("Writing conflict file to '%s'....%n", confFile);
           cplex.writeConflict(confFile);

        }
		cplex.close();
		
	}
}
