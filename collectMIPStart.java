//收集解作为下次求解的起点
public void collectMIPStart(DualSpMW dspMW) throws IloException {
	    List<IloNumVar> startVarList = new ArrayList<>();
	    List<Double> startValList = new ArrayList<>();

	    for (int z = 0; z < Data.Z; z++) {
	        for (int c = 0; c < Data.Cz[z]; c++) {
	            startVarList.add(dspMW.u2[z][c]);
	            startValList.add(dspMW.u2_value[z][c]);

	            startVarList.add(dspMW.u7[z][c]);
	            startValList.add(dspMW.u7_value[z][c]);
              }
           }
            IloNumVar[] startVar = startVarList.toArray(new IloNumVar[0]);
	    double[] startVal = startValList.stream().mapToDouble(Double::doubleValue).toArray();
	    dspMW.dualSubModel.addMIPStart(startVar, startVal);
	    startVarList.clear();
	    startValList.clear();
	}
