package mcdm.topsis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * 
 * @author danigpam
 * https://github.com/danigpam
 * 
 */
public class TopsisTest   {
    
	/*
     * Test case data used from video tutorial by Manoj Mathews
     * https://www.youtube.com/watch?v=Br1NQK0Iumg
     */
    @Test
    public void testMobileMCDM() {

		Criteria criteriaPrice = new Criteria("Price", 0.35, true);
		Criteria criteriaStorage = new Criteria("Storage", 0.25);
		Criteria criteriaCamera = new Criteria("Camera", 0.25);
		Criteria criteriaLooks = new Criteria("Looks", 0.15);

		Alternative mobile1 = new Alternative("Mobile 1");
		mobile1.addCriteriaValue(criteriaPrice, 250);
		mobile1.addCriteriaValue(criteriaStorage, 16);
		mobile1.addCriteriaValue(criteriaCamera, 12);
		mobile1.addCriteriaValue(criteriaLooks, 5);
		
		Alternative mobile2 = new Alternative("Mobile 2");
		mobile2.addCriteriaValue(criteriaPrice, 200);
		mobile2.addCriteriaValue(criteriaStorage, 16);
		mobile2.addCriteriaValue(criteriaCamera, 8);
		mobile2.addCriteriaValue(criteriaLooks, 3);

		Alternative mobile3 = new Alternative("Mobile 3");
		mobile3.addCriteriaValue(criteriaPrice, 300);
		mobile3.addCriteriaValue(criteriaStorage, 32);
		mobile3.addCriteriaValue(criteriaCamera, 16);
		mobile3.addCriteriaValue(criteriaLooks, 4);

		Alternative mobile4 = new Alternative("Mobile 4");
		mobile4.addCriteriaValue(criteriaPrice, 275);
		mobile4.addCriteriaValue(criteriaStorage, 32);
		mobile4.addCriteriaValue(criteriaCamera, 8);
		mobile4.addCriteriaValue(criteriaLooks, 4);
		
		Alternative mobile5 = new Alternative("Mobile 5");
		mobile5.addCriteriaValue(criteriaPrice, 225);
		mobile5.addCriteriaValue(criteriaStorage, 16);
		mobile5.addCriteriaValue(criteriaCamera, 16);
		mobile5.addCriteriaValue(criteriaLooks, 2);
		
		Topsis topsis = new Topsis();
		topsis.addAlternative(mobile1);
		topsis.addAlternative(mobile2);
		topsis.addAlternative(mobile3);
		topsis.addAlternative(mobile4);
		topsis.addAlternative(mobile5);
		
		try {
			Alternative result = topsis.calculateOptimalSolution();
			System.out.println("The optimal solution is: " + result.getName());
			System.out.println("The optimal solution score is: " + result.getCalculatedPerformanceScore());
			
			printDetailedResults(topsis);
			
			assertEquals("Mobile 3", result.getName());
			
		} catch (TopsisIncompleteAlternativeDataException e) {
			System.err.println(e.getMessage());
		}	
    }
    
	private static void printDetailedResults(Topsis topsis) {
		
		System.out.println();
		System.out.println("Calculated closeness to ideal solution:");
		for (Alternative alternative : topsis.getAlternatives()) {
			System.out.println("Alternative: " + alternative.getName() + 
					" weight: " + alternative.getCalculatedPerformanceScore());
		}
	}
}
