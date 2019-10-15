package mcdm.topsis;

/*
 * 
 * @author danigpam
 * https://github.com/danigpam
 * 
 */
public class CriteriaValue {

	private Criteria criteria;
	private double value;

	public CriteriaValue(Criteria criteria, double value) {
		super();
		this.criteria = criteria;
		this.value = value;
	}

	public CriteriaValue() {
		super();
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
