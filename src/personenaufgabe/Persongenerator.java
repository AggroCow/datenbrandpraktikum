package personenaufgabe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Persongenerator {
    private final long amountOfPersonsToGenerate = 5000000; //5 million


    private HashMap<Integer, String> firstNamesReferencedByRank;
    private HashMap<Integer, String> lastNamesReferencedByRank;
    private HashMap<Integer, Long> zipCodeWithNumberOfInhabitants;
    private ArrayList<String> streetNames;
    private final int maximumHouseNumber = 500;

    private Connection conn1 = null;
    private String user = "PARA_DB";
    private String password = "para_db";

    String dbURL = "jdbc:oracle:thin:@134.106.56.42:1521:dbprak";

    /**
     * Connects do database via jdbc
     */
    private void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch(ClassNotFoundException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        try {
            conn1 = DriverManager.getConnection(dbURL, user, password);
        } catch (SQLException se) {
            se.printStackTrace();
        }
        if (conn1 != null) {
            System.out.println("Connection successful");
        }
    }


    /**
     * Generates a shuffled List of names in correspondence to their rank. The list will be around
     * {@param amountOfPersonsToGenerate} long and uses the {@param namesReferencedByRank} HashMap as lead.
     * A name with a higher rank will be more common than a name with a lower one
     * @param amountOfNamesToGenerate the amount of names to generate
     * @param namesReferencedByRank provides names which are referenced by a rank, i.e. their key
     * @return the aforementioned list
     */
    private ArrayList<String> createNameList(int amountOfNamesToGenerate, HashMap<String, Integer> namesReferencedByRank) {
        ArrayList<String> nameList = null;

        int sumOfAllRanks = (namesReferencedByRank.size() * (namesReferencedByRank.size() +1))/2; //triangle number
        double factorByHowMuchANameHasToBeMultiplied = amountOfNamesToGenerate / sumOfAllRanks;

        namesReferencedByRank.forEach((name, rank) -> {
            int nameOccurence = (int) Math.round((namesReferencedByRank.size()-rank+1)*factorByHowMuchANameHasToBeMultiplied);
            for (int i = 0; i < nameOccurence; i++) {
                nameList.add(name);
            }
        });

        Collections.shuffle(nameList);

        return nameList;
    }

    /**
     * Creates a shuffled list of length {@param amountOfPersonsToGenerate} of zipcodes in correspondence to their inhabitants.
     * More inhabitants conclude to more occurences of a given zipcode
     * @param amountOfZipcodesToGenerate
     * @param zipCodeWithNumberOfInhabitants
     * @return aforementioned list
     */
    private ArrayList<Integer> createZipCodeList(int amountOfZipcodesToGenerate, HashMap<Integer, Long> zipCodeWithNumberOfInhabitants) {
        ArrayList<Integer> zipCodeList = null;

        long amountOfAllInhabitants = 0;

        for(long count: zipCodeWithNumberOfInhabitants.values()){
            amountOfAllInhabitants += count;
        }

        long finalAmountOfAllInhabitants = amountOfAllInhabitants;
        zipCodeWithNumberOfInhabitants.forEach((zipCode, inhabitantCount) ->{
            double zipCodeOccurenceInPercent = finalAmountOfAllInhabitants / inhabitantCount;

            for (int i = 0; i < Math.round(amountOfZipcodesToGenerate*zipCodeOccurenceInPercent); i++) {
                zipCodeList.add(zipCode);
            }
        });

        Collections.shuffle(zipCodeList);

        return zipCodeList;
    }


    /**
     * Generates a shuffled list of streetnames given by {@param streetnames}. The list will be {@param amountOfNamesToGenerate} long
     * @param amountOfNamesToGenerate
     * @param streetNames
     * @return the aforementioned list
     */
    private ArrayList<String> createStreetNameList(int amountOfNamesToGenerate, ArrayList<String> streetNames) {
        ArrayList<String> streetNameList = null;
        int cursor = 0;

        for (int i = 0; i < amountOfNamesToGenerate; i++) {
            streetNameList.add(streetNames.get(cursor));
            if(cursor == streetNames.size()){
                cursor = 0;
            } else {
                cursor++;
            }

        }

        Collections.shuffle(streetNameList);
        return streetNameList;
    }


    /**
     * Generates a shuffled list of house numbers of length {@param amountOfPersonsToGenerate}. The maximum house number
     * is {@param maximumHousenumber}
     * @param amountOfHouseNumbersToGenerate
     * @param maximumHouseNumber
     * @return the aforementioned List
     */
    private ArrayList<Integer> createHouseNumberList(int amountOfHouseNumbersToGenerate, int maximumHouseNumber){

        ArrayList<Integer> houseNumbers = null;
        int currentHouseNumber = 1;

        for(int i = 0; i < amountOfHouseNumbersToGenerate; i++){
            houseNumbers.add(currentHouseNumber);
            if(currentHouseNumber != maximumHouseNumber) {
                currentHouseNumber++;
            } else {
                currentHouseNumber = 1;
            }
        }

        Collections.shuffle(houseNumbers);

        return houseNumbers;
    }






    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Persongenerator p = new Persongenerator();
        p.connect();
    }

}
