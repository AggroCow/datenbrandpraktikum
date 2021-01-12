package personenaufgabe;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Persongenerator {
    private final int amountOfPersonsToGenerate = 5000000; //5 million


    private HashMap<String, Integer> firstNamesReferencedByRank = new HashMap<>();
    private HashMap<String, Integer> lastNamesReferencedByRank = new HashMap<>();
    private HashMap<Integer, Integer> zipCodeWithNumberOfInhabitants = new HashMap<>();
    private HashMap<Integer, String> zipCodeCityName = new HashMap<>();

    private ArrayList<String> streetNames = new ArrayList<>();
    private final int maximumHouseNumber = 500;
    private String nameNumberOne = "";

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

        final int[] cursor = {0};

        HashMap<String, Integer> namesAndOccurences = new HashMap<>();
        List<String> listOfNames = new ArrayList<>(namesReferencedByRank.keySet());



        namesReferencedByRank.forEach((name, rank) -> {
            namesAndOccurences.put(name, (int) Math.round((namesReferencedByRank.size()-rank+1)* factorByHowMuchANameHasToBeMultiplied));
            if(rank == 1 && !name.equals("") && name != null){
                nameNumberOne = name;
            }
            System.out.println(name);
        });

        int currentNameHasToBePutThisManyTimesInArray = 0;
        String currentName = "";

        for (int i = 0; i < nameArray.length; i++) {
            if(listOfNames.size() == 0) {
                while(i < nameArray.length){
                    nameArray[i] = nameNumberOne;
                    i++;
                }
                break;
            }
            while (currentNameHasToBePutThisManyTimesInArray <= 0) {
                do{
                    currentNameHasToBePutThisManyTimesInArray = namesAndOccurences.get(listOfNames.get(0));
                    currentName = listOfNames.get(0);
                    listOfNames.remove(0);
                }while(currentName.equals("") || currentName == null || currentName.isEmpty());
                if(currentName.equals("") || currentName == null|| currentName.isEmpty()){
                    System.out.println("\n\n\nNULL NAME GEFUNDEN\n\n\n");
                }
            }
            nameArray[i] = currentName;
            currentNameHasToBePutThisManyTimesInArray--;
        }


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
            double zipCodeOccurrenceInPercent = (double) inhabitantCount / amountOfAllInhabitants;
            zipCodesWithAmountOfOccurrences.put(zipCode,  (int) Math.round(amountOfZipcodesToGenerate*zipCodeOccurrenceInPercent));
        });

        int currentZipCodeHasToPutThisManyTimesInArray = 0;
        int currentZipCode = 0;
        List<Integer> listOfZipCodes = new ArrayList<>(zipCodeWithNumberOfInhabitants.keySet());


        int highestPopulatedZipCode = 0;
        int highestPopulationInZipCode = 0;

        for (int i = 0; i < zipCodeArray.length; i++) {
            if(listOfZipCodes.size() == 0) {
                while(i < zipCodeArray.length){
                    zipCodeArray[i] = highestPopulatedZipCode;
                    i++;
                }
                break;
            }
            while (currentZipCodeHasToPutThisManyTimesInArray <= 0) {
                currentZipCodeHasToPutThisManyTimesInArray = zipCodesWithAmountOfOccurrences.get(listOfZipCodes.get(0));
                currentZipCode = listOfZipCodes.get(0);
                listOfZipCodes.remove(0);
            }

            zipCodeArray[i] = currentZipCode;

            if(currentZipCodeHasToPutThisManyTimesInArray > highestPopulationInZipCode){
                highestPopulatedZipCode = currentZipCode;
                highestPopulationInZipCode = currentZipCodeHasToPutThisManyTimesInArray;
            }
            currentZipCodeHasToPutThisManyTimesInArray--;

        }

        List<Integer> zipCodeList = Arrays.asList(zipCodeArray);

        if (zipCodeList.size() != amountOfZipcodesToGenerate){
            System.out.println("ZU WENIGE ZIPCODES IN ZIPCODELISTE");
        }

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
        String[] streetNameArray = new String[amountOfNamesToGenerate];
        int cursorForProvidedStreetNames = 0;

        for (int i = 0; i < streetNameArray.length; i++) {
                streetNameArray[i] = streetNames.get(cursorForProvidedStreetNames);
            
            if(cursorForProvidedStreetNames >= streetNames.size() - 1){
                cursorForProvidedStreetNames = 0;
            } else {
                cursorForProvidedStreetNames++;
            }

        }

        List<String> streetNameListAsList = Arrays.asList(streetNameArray);

        Collections.shuffle(streetNameListAsList);

        streetNameListAsList.toArray(streetNameArray);
        
        
        return streetNameArray;
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
        double lapTime = System.currentTimeMillis();


        Persongenerator p = new Persongenerator();
        Connection c = p.connect();

        //Acquire Data from DB
        try {

            PreparedStatement ps = c.prepareStatement("SELECT rang, name FROM para_db.vornamen");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.firstNamesReferencedByRank.put(rs.getString("name"), rs.getInt("rang"));
            }

            System.out.println("Vornamen ausgelesen. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
            System.out.println("----------------------------------------");
            lapTime = System.currentTimeMillis();
            ps.close();

            ps = c.prepareStatement("SELECT rang, name FROM para_db.nachnamen");
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                p.lastNamesReferencedByRank.put(rs2.getString("name"), Integer.parseInt((rs2.getString("rang")).substring(0, rs2.getString("rang").length()-1).replaceAll("\\s", "")));
            }
            System.out.println("Nachnamen ausgelesen. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
            System.out.println("----------------------------------------");
            lapTime = System.currentTimeMillis();
            ps.close();

            ps = c.prepareStatement("SELECT name FROM para_db.strassen_namen");
            ResultSet rs3 = ps.executeQuery();
            while (rs3.next()) {
                p.streetNames.add(rs3.getString("name"));
            }
            System.out.println("Straßen ausgelesen. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
            System.out.println("----------------------------------------");
            lapTime = System.currentTimeMillis();
            ps.close();

            ps = c.prepareStatement("SELECT para_db.plzew.plz, para_db.plzew.einwohner, para_db.plzort.ort FROM para_db.plzew " +
                    "FULL JOIN para_db.plzort ON para_db.plzew.plz=para_db.plzort.plz");
            ResultSet rs4 = ps.executeQuery();
            while (rs4.next()) {
                p.zipCodeWithNumberOfInhabitants.put(rs4.getInt("plz"), rs4.getInt("einwohner"));
                p.zipCodeCityName.put(rs4.getInt("plz"), rs4.getString("ort"));
            }
            System.out.println("Postleitzahlen und Städte ausgelesen. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
            System.out.println("----------------------------------------");
            lapTime = System.currentTimeMillis();
            ps.close();

        } catch (SQLException se){
            se.printStackTrace();
        }
        System.out.println("----------------------------------------");
        System.out.println("Generiere Daten");
        System.out.println("----------------------------------------");

        String[] firstnames = p.createNameList(p.amountOfPersonsToGenerate, p.firstNamesReferencedByRank);
        String[] lastnames = p.createNameList(p.amountOfPersonsToGenerate, p.lastNamesReferencedByRank);
        Integer[] zipCodeList = p.createZipCodeList(p.amountOfPersonsToGenerate, p.zipCodeWithNumberOfInhabitants);
        String[] streetNameList = p.createStreetNameList(p.amountOfPersonsToGenerate, p.streetNames);
        Date[] randomDates = p.createRandomDates(p.amountOfPersonsToGenerate);
        Integer[] randomHouseNumbers = p.createHouseNumberList(p.amountOfPersonsToGenerate, 300);
        ArrayList<String> cityNames = new ArrayList<>();

        for (int zipcode: zipCodeList) {
            cityNames.add(p.zipCodeCityName.get(zipcode));
        }

        System.out.println("Daten generiert. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
        System.out.println("----------------------------------------");
        lapTime = System.currentTimeMillis();

        int batchCounter = 0;
        try{

            //SQL statement add first
            PreparedStatement ps = c.prepareStatement("INSERT INTO para_db.jdbc_personen VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            for (int i = 0; i < firstnames.length; i++) {
                if(i%1000000 == 0 && i != 0){
                    System.out.println("Batch "+ batchCounter+ " generiert. Ausführung bevorstehend. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
                    System.out.println("----------------------------------------");
                    batchCounter++;
                    ps.executeBatch();
                    c = p.connect();
                    ps = c.prepareStatement("INSERT INTO para_db.jdbc_personen VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                }

                ps.setString(1, firstnames[i]);

                if(lastnames[i] == null || lastnames[i].equals("")){
                    ps.setString(2, p.nameNumberOne);
                }else{
                    ps.setString(2, lastnames[i]);
                }

                ps.setDate(3, randomDates[i]);

                ps.setString(4, cityNames.get(i));

                if(i < (int) (firstnames.length*0.95)) {
                    ps.setString(5, cityNames.get(i));
                } else{
                    ps.setString(5, cityNames.get((int) (Math.random() * firstnames.length)));
                }
                ps.setString(6, "added via JDBC");

                ps.setString(7, streetNameList[i]);

                ps.setInt(8, randomHouseNumbers[i]);
                ps.addBatch();
            }
            System.out.println("Batch "+ batchCounter+ " generiert. Ausführung bevorstehend. Bisherige Zeit: "+ (System.currentTimeMillis()-startTime)/1000 + "s. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
            System.out.println("----------------------------------------");
            ps.executeBatch();
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
        System.out.println("##############################################################################################");
        System.out.println("##############################################################################################");
        System.out.println("##############################################################################################");
        System.out.println("");
        System.out.println("Die gesamte Operation hat "+ timeElapsed+ " Sekunden gebraucht. \nVerstrichene Zeit für diese Operation: "+(System.currentTimeMillis()-lapTime)/1000 +"s.");
        System.out.println("");
        System.out.println("##############################################################################################");
        System.out.println("##############################################################################################");
        System.out.println("##############################################################################################");
    }

}
