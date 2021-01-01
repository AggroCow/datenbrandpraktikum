package personenaufgabe;

import org.junit.Test;

import java.util.HashMap;


public class PersongeneratorTest {
  Persongenerator persongenerator = new Persongenerator();

  HashMap<Integer, Integer> zipCodeWithNumberOfInhabitants = new HashMap<>();


  @Test
  public void createZipCodeList(){
      for(int i = 0; i < 1000; i++){
        zipCodeWithNumberOfInhabitants.put(i, i);
      }

      Integer[] zipCodeArray = persongenerator.createZipCodeList(10000, zipCodeWithNumberOfInhabitants);

      for (int plz: zipCodeArray) {
          System.out.println(zipCodeArray);
      }
  }
}