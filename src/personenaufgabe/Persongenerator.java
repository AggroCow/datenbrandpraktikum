package personenaufgabe;

import com.sun.deploy.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Persongenerator {
    private final long amountOfPersonsToGenerate = 5000000; //5 million


    private HashMap<String, Integer> firstNamesReferencedByRank = new HashMap<>();
    private HashMap<String, Integer> lastNamesReferencedByRank = new HashMap<>();
    private HashMap<Integer, Long> zipCodeWithNumberOfInhabitants = new HashMap<>();
    private HashMap<Integer, String> zipCodeCityName = new HashMap<>();

    private ArrayList<String> streetNames = new ArrayList<>();
    private final int maximumHouseNumber = 500;

    private Connection conn1 = null;
    private String user = "PARA_DB";
    private String password = "para_db";

    String dbURL = "jdbc:oracle:thin:@134.106.56.42:1521:dbprak";

    /**
     * Connects do database via jdbc
     */
    private Connection connect() {
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
            return conn1;
        }
        return null;
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
        Connection c = p.connect();


        try {

            PreparedStatement ps = c.prepareStatement("SELECT rang, name FROM para_db.vornamen");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.firstNamesReferencedByRank.put(rs.getString("name"), rs.getInt("rang"));
            }
            System.out.println(p.firstNamesReferencedByRank);
            ps.close();

            /*ps = c.prepareStatement("SELECT rang, name FROM para_db.nachnamen");
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                p.lastNamesReferencedByRank.put(rs2.getString("name"), Integer.parseInt((rs2.getString("rang")).substring(0, rs2.getString("rang").length()-1).replaceAll("\\s", "")));
            }
            System.out.println(p.lastNamesReferencedByRank);
            ps.close();

            ps = c.prepareStatement("SELECT name FROM para_db.strassen_namen");
            ResultSet rs3 = ps.executeQuery();
            while (rs3.next()) {
                p.streetNames.add(rs3.getString("name"));
            }
            System.out.println(p.streetNames);
            ps.close();



            ps = c.prepareStatement("SELECT para_db.plzew.plz, para_db.plzew.einwohner, para_db.plzort.ort FROM para_db.plzew " +
                    "FULL JOIN para_db.plzort ON para_db.plzew.plz=para_db.plzort.plz");
            ResultSet rs4 = ps.executeQuery();
            while (rs4.next()) {
                p.zipCodeWithNumberOfInhabitants.put(rs4.getInt("plz"), rs4.getLong("einwohner"));
                p.zipCodeCityName.put(rs4.getInt("plz"), rs4.getString("ort"));
            }
            System.out.println(p.zipCodeCityName);
            ps.close();
            */
        } catch (SQLException se){
            se.printStackTrace();
        }

        ArrayList<String> firstnames = p.createNameList(50000, p.firstNamesReferencedByRank);
       /* ArrayList<String> lastnames = p.createNameList(50000, p.lastNamesReferencedByRank);
        ArrayList<Integer> zipCodeList = p.createZipCodeList(50000, p.zipCodeWithNumberOfInhabitants);
        ArrayList<String> streetNameList = p.createStreetNameList(50000, p.streetNames);
        ArrayList<String> cityNames = new ArrayList<>();

        for (int zipcode: zipCodeList) {
            cityNames.add(p.zipCodeCityName.get(zipcode));
        }
    */
        double timeElapsed = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("Die Operation hat "+ timeElapsed+ " Sekunden gebraucht");
    }

}
