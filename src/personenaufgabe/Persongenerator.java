package personenaufgabe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
     * Generates a shuffled List of names in correspondence to their rank. The list will be
     * {@param amountOfPersonsToGenerate} long and uses the {@param namesReferencedByRank} HashMap as lead.
     * A name with a higher rank will be more common than a name with a lower one
     * @param amountOfPersonsToGenerate the amount of names to generate
     * @param namesReferencedByRank provides names which are referenced by a rank, i.e. their key
     * @return the aforementioned list
     */
    private ArrayList<String> createFirstName(int amountOfPersonsToGenerate, HashMap<Integer, String> namesReferencedByRank) {
        ArrayList<String> names = null;


        int sumOfAllRanks = (namesReferencedByRank.size() * (namesReferencedByRank.size() +1))/2; //triangle number
        double factorByHowMuchANameHasToBeMultiplied = amountOfPersonsToGenerate / sumOfAllRanks;


        namesReferencedByRank.forEach((rank, name) -> {

            for (int i = 0; i < (namesReferencedByRank.size()-rank+1)*factorByHowMuchANameHasToBeMultiplied; i++) {
                names.add(name);
            }
        });




        Collections.shuffle(names);

        return names;
    }


    /**
     * Generates a shuffled list of house numbers of length {@param amountOfPersonsToGenerate}. The maximum house number
     * is {@param maximumHousenumber}
     * @param amountOfPersonsToGenerate
     * @param maximumHousenumber
     * @return the aforementioned List
     */
    private ArrayList<Integer> createHouseNumberList(int amountOfPersonsToGenerate, int maximumHousenumber){

        ArrayList<Integer> houseNumbers = null;
        int currentHouseNumber = 1;

        for(int i = 0; i < amountOfPersonsToGenerate; i++){
            houseNumbers.add(currentHouseNumber);
            if(currentHouseNumber != 500) {
                currentHouseNumber++;
            } else {
                currentHouseNumber = 1;
            }
        }

        Collections.shuffle(houseNumbers);

        return houseNumbers;
    }






    public static void main(String[] args) {
        Persongenerator p = new Persongenerator();
        p.connect();
    }

}
