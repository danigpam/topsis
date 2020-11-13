package mcdm.topsis;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sun.xml.internal.stream.writers.XMLStreamWriterImpl.UTF_8;

/*
 * This is an implementation of the MCDM (Multi-Criteria Decision Making) solution using
 * TOPSIS (Technique for Order of Preference by Similarity to Ideal Solution) algorithm
 * 
 * See TopsisTest test class for example of usage
 *  
 * @author danigpam
 * https://github.com/danigpam
 * 
 */
public class Topsis {
	
	private List<Alternative> alternatives;
	
	//Attributes below are all populated from calculateOptimalSolution method
	private List<Criteria> criteria;
	private int numberOfAlternatives = 0;
	private int numberOfCriteria = 0;
	private double[][] scoreMatrix;
	private double[][] normalizedDecisionMatrix;
	private double[] idealBest;
	private double[] idealWorst;
	private double[] distancesFromIdealWorst;
	private double[] distancesFromIdealBest;

	public Topsis(List<Alternative> alternatives) {
		super();
		this.alternatives = alternatives;
	}

	public Topsis() {
		super();
	}
	
	public void addAlternative(Alternative alternative) {
		if (alternatives == null) {
			alternatives = new ArrayList<Alternative>();
		}
		this.alternatives.add(alternative);
	}
	
	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	/**
	 * Given the list of alternatives, each populated with a list of criteria values,
	 * this method will find the optimal solution, that is, which alternative
	 * has the highest closeness score to the ideal solution
	 *
	 * @return List of Alternative
	 * @throws TopsisIncompleteAlternativeDataException
	 */
	public List<Alternative> calculateOptimalSolutionSortedList() throws TopsisIncompleteAlternativeDataException {

		validateData();
		populateScoreMatrix();
		calculateNormalizedDecisionMatrix();
		findIdealBestAndWorst();
		calculateDistancesFromIdealBestAndWorst();
		calculatePerformanceScore();
		sortAlternativesByPerformanceScoreDesc();

		return alternatives; // Sorted result from the ideal solution to the worse one.
	}

	/**
	 * Given the list of alternatives, each populated with a list of criteria values, 
	 * this method will find the optimal solution, that is, which alternative
	 * has the highest closeness score to the ideal solution
	 *
	 * @return Alternative
	 * @throws TopsisIncompleteAlternativeDataException
	 */
	public Alternative calculateOptimalSolution() throws TopsisIncompleteAlternativeDataException {
		return this.calculateOptimalSolutionSortedList().get(0); // Top solution after sorting is the best score
	}

	/**
	 * Write alternatives into CSV file
	 *
	 * @Parameters the File path & the filename
	 * @return void
	 * @throws Exception
	 */
	public void writeResultsIntoCSVFile(String path, String fileName ) throws Exception {

		if (this.alternatives.isEmpty()) throw new Exception("Alternatives should not be empty");

		final Writer writer  = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path + fileName + ".csv"), UTF_8));

		StringBuilder header = new StringBuilder();
		header.append("Alternative name").append(";")
				.append("Criteria values").append("\n");

		writer.write(header.toString());

		for (Alternative alternative : this.alternatives) {
				writer.write(alternative.getName() + ";" + alternative.getCriteriaValues().toString() + "\n");
		}
		
		writer.close();
	}

	private void validateData() throws TopsisIncompleteAlternativeDataException {
		if (!hasNullOrEmptyAlternatives() && !hasIncompleteAlternativeCriteria() ) {
			throw new TopsisIncompleteAlternativeDataException();
		}
	}
	
	private boolean hasNullOrEmptyAlternatives() {
		return alternatives == null || alternatives.size() == 0;
	}

	private boolean hasIncompleteAlternativeCriteria() {
		criteria = new ArrayList<Criteria>();
		for (Alternative alternative : alternatives) {
			
			//Alternatives cannot have null criteria values
			if (alternative.getCriteriaValues() == null || alternative.getCriteriaValues().size() == 0) {
				return false;
			}
			
			//First alternative will populate criteria list and score matrix
			if (criteria.size() == 0) {
				for (CriteriaValue cv : alternative.getCriteriaValues()) {
					criteria.add(cv.getCriteria());					
				}
				continue; //skip validation for first one
			}
			
			//Other alternatives must match same criteria list size
			if (alternative.getCriteriaValues().size() != criteria.size()) {
				return false;
			}
			
			//And same criteria object
			for (CriteriaValue cv : alternative.getCriteriaValues()) {
				if (cv.getCriteria() == null || !criteria.contains(cv.getCriteria())) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void populateScoreMatrix() {
		numberOfAlternatives = alternatives.size();
		numberOfCriteria = criteria.size();
		
		scoreMatrix = new double[numberOfAlternatives][numberOfCriteria];
		normalizedDecisionMatrix = new double[numberOfAlternatives][numberOfCriteria];
		
		int rowIndex = 0;		
		for (Alternative alternative : alternatives) {
			int columnIndex = 0;
			for (CriteriaValue cv : alternative.getCriteriaValues()) {
				scoreMatrix[rowIndex][columnIndex] = cv.getValue();
				columnIndex++;
			}
			rowIndex++;
		}
	}

	private void calculateNormalizedDecisionMatrix() {
		
		for (int c = 0; c < numberOfCriteria; c++) {
			
			double divisor = 0;
			for (int a = 0; a < numberOfAlternatives; a++) {
				divisor += scoreMatrix[a][c] * scoreMatrix[a][c]; //sum the squares
			}
			divisor = Math.pow(divisor, 0.5); //square root of the sum
			
			for (int a = 0; a < numberOfAlternatives; a++) {
				double normalizedValue = scoreMatrix[a][c] / divisor; //divide original value by the sqrt of the sum
				normalizedValue = normalizedValue * criteria.get(c).getWeight(); //apply criteria weight
				normalizedDecisionMatrix[a][c] = normalizedValue;
			}
		}
		
		//printMatrix(normalizedDecisionMatrix);
	}
	
	private void findIdealBestAndWorst() {

		idealBest = new double[numberOfCriteria];
		idealWorst = new double[numberOfCriteria];
		
		for (int c = 0; c < numberOfCriteria; c++) {
			double minValue = Double.MAX_VALUE;
			double maxValue = 0;
			
			for (int a = 0; a < numberOfAlternatives; a++) {

				if ( normalizedDecisionMatrix[a][c] > maxValue ) {
					maxValue = normalizedDecisionMatrix[a][c];
				}
				if ( normalizedDecisionMatrix[a][c] < minValue ) {
					minValue = normalizedDecisionMatrix[a][c];
				}
			}
			
			//If negative, the ideal best is the min value
			//Otherwise, ideal best is the max value
			if (criteria.get(c).isNegative()) {
				idealBest[c] = minValue;
				idealWorst[c] = maxValue;
			} else {
				idealBest[c] = maxValue;
				idealWorst[c] = minValue;
			}
		}
		
		//printIdealBestAndWorst();
	}
	

	private void calculateDistancesFromIdealBestAndWorst() {
		
		distancesFromIdealBest = new double[numberOfAlternatives];
		distancesFromIdealWorst = new double[numberOfAlternatives];
		
		for (int a = 0; a < numberOfAlternatives; a++) {
			double distanceFromBest = 0;
			double distanceFromWorst = 0;
			
			for (int c = 0; c < numberOfCriteria; c++) {

				double squareOfDifferenceFromBest = normalizedDecisionMatrix[a][c] - idealBest[c]; //subtract from ideal best
				squareOfDifferenceFromBest = squareOfDifferenceFromBest * squareOfDifferenceFromBest; //calculate square
				distanceFromBest += squareOfDifferenceFromBest; //sum squares for all criteria
				

				double squareOfDifferenceFromWorst = normalizedDecisionMatrix[a][c] - idealWorst[c]; //subtract from ideal worst
				squareOfDifferenceFromWorst = squareOfDifferenceFromWorst * squareOfDifferenceFromWorst; //calculate square
				distanceFromWorst += squareOfDifferenceFromWorst; //sum squares for all criteria
			}

			distancesFromIdealBest[a] = Math.pow(distanceFromBest, 0.5); //square root of sum
			distancesFromIdealWorst[a] = Math.pow(distanceFromWorst, 0.5); //square root of sum
		}
	}

	private void calculatePerformanceScore() {

		for (int a = 0; a < numberOfAlternatives; a++) {
			double performanceScore = distancesFromIdealWorst[a] / (distancesFromIdealBest[a] + distancesFromIdealWorst[a]);
			alternatives.get(a).setCalculatedPerformanceScore(performanceScore);
		}
	}

	private void sortAlternativesByPerformanceScoreDesc() {

		Collections.sort(alternatives, new Comparator<Alternative>() {
			
			public int compare(Alternative a1, Alternative a2) {
				return Double.compare(a2.getCalculatedPerformanceScore(), a1.getCalculatedPerformanceScore());
			}
		});
	}

	@SuppressWarnings("unused")
	private void printMatrix(double[][] matrix) {

		for (double[] row : matrix) {

			System.out.print(" [ ");
			for (double columnValue : row) {
				System.out.print(columnValue + " ");
			}
			System.out.println(" ] ");
		}
		
	}
	
	@SuppressWarnings("unused")
	private void printIdealBestAndWorst() {

		System.out.print("Ideal best is : [ ");
		for (double d : idealBest) {
			System.out.print(d + " ");
		}
		System.out.println("]");
		
		System.out.print("Ideal worst is : [ ");
		for (double d : idealWorst) {
			System.out.print(d + " ");
		}
		System.out.println("]");
	}
}
