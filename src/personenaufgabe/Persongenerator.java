package personenaufgabe;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Persongenerator {
    private final long amountOfPersonsToGenerate = 5000000; //5 million


    private HashMap<String, Integer> firstNamesReferencedByRank = new HashMap<>();
    private HashMap<String, Integer> lastNamesReferencedByRank = new HashMap<>();
    private HashMap<Integer, Integer> zipCodeWithNumberOfInhabitants = new HashMap<>();
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
    private String[] createNameList(int amountOfNamesToGenerate, HashMap<String, Integer> namesReferencedByRank) {
        String[] nameArray = new String[amountOfNamesToGenerate];

        int sumOfAllRanks = (namesReferencedByRank.size() * (namesReferencedByRank.size() +1))/2; //triangle number
        double factorByHowMuchANameHasToBeMultiplied = (double)amountOfNamesToGenerate / (double)sumOfAllRanks;


        double finalFactorByHowMuchANameHasToBeMultiplied = factorByHowMuchANameHasToBeMultiplied;


        final int[] cursor = {0};
        
        namesReferencedByRank.forEach((name, rank) -> {
            int nameOccurence = (int) Math.round((namesReferencedByRank.size()-rank+1)* finalFactorByHowMuchANameHasToBeMultiplied);
            for (int i = 0; i < nameOccurence; i++) {
                if(cursor[0] < (nameArray).length){
                    nameArray[cursor[0]] = name;
                    cursor[0]++;
                } else {
                    return;
                }
            }
        });

        List<String> nameListAsList = Arrays.asList(nameArray);
        
        Collections.shuffle(nameListAsList);
        
        nameListAsList.toArray(nameArray);

        return nameArray;
    }

    /**
     * Creates a shuffled list of length {@param amountOfPersonsToGenerate} of zipcodes in correspondence to their inhabitants.
     * More inhabitants conclude to more occurences of a given zipcode
     * @param amountOfZipcodesToGenerate
     * @param zipCodeWithNumberOfInhabitants
     * @return aforementioned list
     */
    public Integer[] createZipCodeList(int amountOfZipcodesToGenerate, HashMap<Integer, Integer> zipCodeWithNumberOfInhabitants) {

        Integer[] zipCodeArray = new Integer[amountOfZipcodesToGenerate];

        HashMap<Integer, Integer> zipCodesWithAmountOfOccurrences = new HashMap<>();

        final double amountOfAllInhabitants = zipCodeWithNumberOfInhabitants.values().stream().reduce(0, Integer::sum);


        zipCodeWithNumberOfInhabitants.forEach((zipCode, inhabitantCount) ->{
            double zipCodeOccurrenceInPercent = amountOfAllInhabitants / (double) inhabitantCount;
            zipCodesWithAmountOfOccurrences.put(zipCode, (int) (amountOfZipcodesToGenerate*zipCodeOccurrenceInPercent));
        });

        int currentZipCodeHasToPutThisManyTimesInArray = 0;
        int currentZipCode = 0;
        List<Integer> listOfZipCodes = new ArrayList<>(zipCodesWithAmountOfOccurrences.keySet());


        for (int i = 0; i < zipCodeArray.length; i++) {

            if(currentZipCodeHasToPutThisManyTimesInArray == 0){
                currentZipCodeHasToPutThisManyTimesInArray = zipCodesWithAmountOfOccurrences.get(listOfZipCodes.get(0));
                currentZipCode = listOfZipCodes.get(0);
                listOfZipCodes.remove(0);
            }

            zipCodeArray[i] = currentZipCode;


            currentZipCodeHasToPutThisManyTimesInArray--;

        }



        List<Integer> zipCodeList = Arrays.asList(zipCodeArray);

        Collections.shuffle(zipCodeList);

        zipCodeList.toArray(zipCodeArray);

        return zipCodeArray;
    }


    /**
     * Generates a shuffled list of streetnames given by {@param streetnames}. The list will be {@param amountOfNamesToGenerate} long
     * @param amountOfNamesToGenerate
     * @param streetNames
     * @return the aforementioned list
     */
    private String[] createStreetNameList(int amountOfNamesToGenerate, ArrayList<String> streetNames) {
        String[] streetNameList = new String[amountOfNamesToGenerate];
        int cursorForProvidedStreetNames = 0;
        int cursorForAddedStreetnames = 0;

        for (int i = 0; i < amountOfNamesToGenerate; i++) {
            if(cursorForAddedStreetnames < streetNameList.length){
                streetNameList[cursorForAddedStreetnames] = (streetNames.get(cursorForProvidedStreetNames));
                cursorForAddedStreetnames++;
            } else {
                break;
            }
            
            if(cursorForProvidedStreetNames == streetNames.size()){
                cursorForProvidedStreetNames = 0;
            } else {
                cursorForProvidedStreetNames++;
            }

        }

        List<String> streetNameListAsList = Arrays.asList(streetNameList);

        Collections.shuffle(streetNameListAsList);

        streetNameListAsList.toArray(streetNameList);
        
        
        return streetNameList;
    }


    /**
     * Generates a shuffled list of house numbers of length {@param amountOfPersonsToGenerate}. The maximum house number
     * is {@param maximumHousenumber}
     * @param amountOfHouseNumbersToGenerate
     * @param maximumHouseNumber
     * @return the aforementioned List
     */
    private Integer[] createHouseNumberList(int amountOfHouseNumbersToGenerate, int maximumHouseNumber){

        Integer[] houseNumbers = new Integer[amountOfHouseNumbersToGenerate];
        int currentHouseNumber = 1;

        for(int i = 0; i < amountOfHouseNumbersToGenerate; i++){
            houseNumbers[i] = currentHouseNumber;
            if(currentHouseNumber != maximumHouseNumber) {
                currentHouseNumber++;
            } else {
                currentHouseNumber = 1;
            }
        }

        List<Integer> houseNumberList = Arrays.asList(houseNumbers);

        Collections.shuffle(houseNumberList);

        houseNumberList.toArray(houseNumbers);

        return houseNumbers;
    }


    /**
     *  Generates a list of length {@param amountOfDatesToGenerate} of Random dates between the start of year 1935 and 2020.
     * @param amountOfDatesToGenerate
     * @return the before mentioned list
     */
    private java.sql.Date[] createRandomDates(int amountOfDatesToGenerate) {
        java.sql.Date[] randomDates = new Date[amountOfDatesToGenerate];
        long startTime = Timestamp.valueOf("1935-01-01 00:00:00").getTime();
        long endTime = Timestamp.valueOf("2020-12-31 00:58:00").getTime();
        long timeDifference = endTime - startTime + 1;

        for (int i = 0; i < amountOfDatesToGenerate; i++) {
            randomDates[i] = (new Date(startTime + (long) (Math.random() * timeDifference)));
        }

        return randomDates;
    }






    @SuppressWarnings("SqlResolve")
    public static void main(String[] args) {
        double startTime = System.currentTimeMillis();


        Persongenerator p = new Persongenerator();
        Connection c = p.connect();

        //Acquire Data from DB
        try {

            PreparedStatement ps = c.prepareStatement("SELECT rang, name FROM para_db.vornamen");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.firstNamesReferencedByRank.put(rs.getString("name"), rs.getInt("rang"));
            }
            System.out.println(p.firstNamesReferencedByRank);
            ps.close();

            ps = c.prepareStatement("SELECT rang, name FROM para_db.nachnamen");
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
                p.zipCodeWithNumberOfInhabitants.put(rs4.getInt("plz"), rs4.getInt("einwohner"));
                p.zipCodeCityName.put(rs4.getInt("plz"), rs4.getString("ort"));
            }
            System.out.println(p.zipCodeCityName);
            ps.close();

        } catch (SQLException se){
            se.printStackTrace();
        }

        int currentAmountOfPersonsToGenerate = 50000;
        String[] firstnames = p.createNameList(currentAmountOfPersonsToGenerate, p.firstNamesReferencedByRank);
        String[] lastnames = p.createNameList(currentAmountOfPersonsToGenerate, p.lastNamesReferencedByRank);
        Integer[] zipCodeList = p.createZipCodeList(currentAmountOfPersonsToGenerate, p.zipCodeWithNumberOfInhabitants);
        String[] streetNameList = p.createStreetNameList(currentAmountOfPersonsToGenerate, p.streetNames);
        Date[] randomDates = p.createRandomDates(currentAmountOfPersonsToGenerate);
        Integer[] randomHouseNumbers = p.createHouseNumberList(currentAmountOfPersonsToGenerate, 300);
        ArrayList<String> cityNames = new ArrayList<>();

        for (int zipcode: zipCodeList) {
            cityNames.add(p.zipCodeCityName.get(zipcode));
            System.out.println(zipcode);
        }

        try{

            //SQL statement add first
            PreparedStatement ps = c.prepareStatement("INSERT INTO para_db.jdbc_personen VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            for (int i = 0; i < firstnames.length; i++) {

                ps.setString(1, firstnames[i]);
                System.out.print(firstnames[i] + ", ");

                ps.setString(2, lastnames[i]);
                System.out.print(lastnames[i] + ", ");

                ps.setDate(3, randomDates[i]);
                System.out.print(randomDates[i] + ", ");

                ps.setString(4, cityNames.get(i));
                System.out.print(cityNames.get(i) + ", ");

                if(i < (firstnames.length*0.95)) {
                    ps.setString(5, cityNames.get(i));
                    System.out.print(cityNames.get(i) + ", ");
                } else{
                    ps.setString(5, cityNames.get((int) (Math.random() * firstnames.length)));
                    System.out.print(cityNames.get(i) + ", ");
                }
                ps.setString(6, "added via JDBC");
                System.out.print("added via JDBC" + ", ");

                ps.setString(7, streetNameList[i]);
                System.out.print(streetNameList[i] + ", ");

                ps.setInt(8, randomHouseNumbers[i]);
                System.out.println(randomHouseNumbers[i]);
                ps.addBatch();
            }
            //int[] count = ps.executeBatch();
            //System.out.println(count);
            //c.commit();

        }catch(SQLException se) {
            se.printStackTrace();
        }
        /*
        1. VORNAME      VARCHAR2(30),
        2. NACHNAME     VARCHAR2(255),
        3. GEBURTSDATUM DATE,
        4. GEBURTSORT   NUMBER,
        5. WOHNORT      NUMBER,
        6. BEMERKUNG    VARCHAR2(35),
        7. STRASSE      VARCHAR2(128),
        8. HAUSNUMMER   NUMBER
        */

        double timeElapsed = (System.currentTimeMillis()-startTime)/1000;
        System.out.println("Die Operation hat "+ timeElapsed+ " Sekunden gebraucht");
    }

}
