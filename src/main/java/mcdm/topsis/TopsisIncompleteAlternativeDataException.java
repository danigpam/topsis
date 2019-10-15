package mcdm.topsis;

/*
 * 
 * @author danigpam
 * https://github.com/danigpam
 * 
 */
public class TopsisIncompleteAlternativeDataException extends Exception {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Incomplete data used to calculate topsis. Ensure that all alternatives have a score for each of the same criteria.";
	}
}
