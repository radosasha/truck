package com.truck.alexander.db.states;

public class DistrictOfColumbia  implements StatesInfo{
	double DistrictOfColumbia [][] = new double[][]{
			{ 38.79652767570669 , -77.03393022270488 },
			{ 38.89282591871032 , -76.90947694534954 },
			{ 38.99517098355342 , -77.0411644923601 },
			{ 38.93828934360418 , -77.11530636345501 },
			{ 38.93547 , -77.1134 },
			{ 38.90457 , -77.06169 },
			{ 38.88391840322657 , -77.0509406408088 },
			{ 38.87003504228508 , -77.03471752371866 },
			{ 38.85616664917477 , -77.02942434188603 },
			{ 38.81083669664584 , -77.03484014173415 },
			{ 38.79652767570669 , -77.03393022270488 }
	};

	@Override
	public double[][] getData() {
		// TODO Auto-generated method stub
		return DistrictOfColumbia;
	}

	@Override
	public String stateName() {
		// TODO Auto-generated method stub
		return "District Of Columbia";
	}
}
