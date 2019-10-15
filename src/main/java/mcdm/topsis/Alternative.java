package mcdm.topsis;

import java.util.ArrayList;
import java.util.List;

/*
 * 
 * @author danigpam
 * https://github.com/danigpam
 * 
 */

public class Alternative {

	private String name;
	private List<CriteriaValue> criteriaValues;
	private double calculatedPerformanceScore;

	public Alternative(String name, List<CriteriaValue> criteriaValues) {
		super();
		this.name = name;
		this.criteriaValues = criteriaValues;
	}
	
	public Alternative(String name) {
		super();
		this.name = name;
	}

	public Alternative() {
		super();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CriteriaValue> getCriteriaValues() {
		return criteriaValues;
	}

	public void setCriteriaValues(List<CriteriaValue> criteriaValues) {
		this.criteriaValues = criteriaValues;
	}
	
	public void addCriteriaValue(CriteriaValue criteriaValue) {
		if (criteriaValues == null) {
			criteriaValues = new ArrayList<CriteriaValue>();
		}
		this.criteriaValues.add(criteriaValue);
	}
	
	public void addCriteriaValue(Criteria criteria, double value) {
		if (criteriaValues == null) {
			criteriaValues = new ArrayList<CriteriaValue>();
		}
		this.criteriaValues.add(new CriteriaValue(criteria, value));
	}

	public double getCalculatedPerformanceScore() {
		return calculatedPerformanceScore;
	}

	protected void setCalculatedPerformanceScore(double calculatedPerformanceScore) {
		this.calculatedPerformanceScore = calculatedPerformanceScore;
	}
}
