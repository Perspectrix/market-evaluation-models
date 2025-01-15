package com.perspectrix.market.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document(collection = "people")
public class Person {
    @Id
    String id;

    //    @GeoSpatialIndexed

    @Field("location")
    GeoJsonPoint location;

    String address; // address
    String city; // city
    String state; // state
    String fName; // first name
    String lName; //  last name
    String phoneNum; // phone number combined
    String zip; // zip code
    String county; // county
    String metroArea; // metro area
    String lat; // latitude
    String lon; // longitude
    String ageRange;
    String gender; // adult gender
    String ownRent; // own / rent
    String householdIncome; // estimated household income
    String homeValue; // estimated home value
    String wealth; // wealth finder
    String fips;
    String[] competitors;


    /**
     * Empty constructor needed for retrieving the POJO
     */
    public Person(){}
    /**
     * Allows for out of order columns, matches the desired header cell indices to the row indices and then building a person object.
     * This abstract structure will allow for a more organized way of processing populations.
     * @param csvHeader
     * @param csvRow
     */
    public Person(String csvHeader, String[] csvRow){
        Map<String, Integer> columnIndexMap = parseHeader(csvHeader);

        this.address = csvRow[columnIndexMap.getOrDefault("address", -1)].trim();
        this.city = csvRow[columnIndexMap.getOrDefault("city", -1)].trim();
        this.state = csvRow[columnIndexMap.getOrDefault("state", -1)].trim();

        this.id = generateId();

        this.fName = getFieldValue(columnIndexMap, "first name", csvRow);
        this.lName = getFieldValue(columnIndexMap, "last name", csvRow);
        this.phoneNum = getFieldValue(columnIndexMap, "phone number combined", csvRow);

        this.zip = getFieldValue(columnIndexMap, "zip code", csvRow);
        this.county = getFieldValue(columnIndexMap, "county", csvRow);
        this.metroArea = getFieldValue(columnIndexMap, "metro area", csvRow);
        this.lat = getFieldValue(columnIndexMap, "latitude", csvRow);
        this.lon = getFieldValue(columnIndexMap, "longitude", csvRow);

        if(lat != null && lon != null){
            this.location = new GeoJsonPoint(Double.parseDouble(lat), Double.parseDouble(lon));
        } else {
            this.location = null;
        }

        this.ageRange = getFieldValue(columnIndexMap, "age range", csvRow);
        this.gender = getFieldValue(columnIndexMap, "adult gender", csvRow);
        this.ownRent = getFieldValue(columnIndexMap, "own / rent", csvRow);
        this.householdIncome = getFieldValue(columnIndexMap, "estimated household income", csvRow);
        this.homeValue = getFieldValue(columnIndexMap, "estimated home value", csvRow);
        this.wealth = getFieldValue(columnIndexMap, "wealth finder", csvRow);

    }
    private String generateId() {
        String addressKey = String.format("%s|%s|%s",
                getAddress().toLowerCase().trim(),
                getCity().toLowerCase().trim(),
                getState().toLowerCase().trim()
        );
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(addressKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, 24);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate person ID", e);
        }
    }

    private String getFieldValue(Map<String, Integer> columnIndexMap, String fieldName, String[] csvRow){
        if(columnIndexMap.containsKey(fieldName)){
            return csvRow[columnIndexMap.get(fieldName)];
        }
        return null;
    }

    private Map<String, Integer> parseHeader(String header) {
        Map<String, Integer> columnIndexMap = new HashMap<>();
        String[] columns = header.split(",");

        for (int i = 0; i < columns.length; i++) {
            columnIndexMap.put(columns[i].trim().toLowerCase(), i);
        }
        return columnIndexMap;
    }

    @Override
    public String toString() {
        return "Person { " + "id='" + id + '\'' + ", city='" + city + '\'' + ", state='" + state + '\'' + ", ageRange='" + ageRange + '\'' + ", householdIncome='" + householdIncome + '\'' + ", homeValue='" + homeValue + '\'' + ", wealth='" + wealth + '\'' + " }";
    }


    /**
     * Returns a value from 10,000 to 500,000 or null.
     * Based on Data Axle data dictionary.
     * @return Household Income
     */
    public Integer parseIncomeRange() {
        if (householdIncome == null || householdIncome.isEmpty())return null; // Make sure this is a lazy operator
        if (householdIncome.contains("Under")) return 10_000;
        if (householdIncome.contains("20,000")) return 25_000;
        if (householdIncome.contains("30,000")) return 35_000;
        if (householdIncome.contains("40,000")) return 45_000;
        if (householdIncome.contains("50,000")) return 55_000;
        if (householdIncome.contains("60,000")) return 65_000;
        if (householdIncome.contains("70,000")) return 75_000;
        if (householdIncome.contains("80,000")) return 85_000;
        if (householdIncome.contains("90,000")) return 95_000;
        if (householdIncome.contains("100,000")) return 112_500;
        if (householdIncome.contains("125,000")) return 137_500;
        if (householdIncome.contains("150,000")) return 162_500;
        if (householdIncome.contains("200,000")) return 225_000;
        if (householdIncome.contains("250,000")) return 275_000;
        if (householdIncome.contains("300,000")) return 350_000;
        if (householdIncome.contains("400,000")) return 450_000;
        if (householdIncome.contains("500,000")) return 500_000;
        return null;
    }

    /**
     * Returns a value from 12,500 to 1,000,000 or null.
     * Based on Data Axle data dictionary.
     * @return Home Value
     */
    public Integer parseHomeValueRange() {
        if (homeValue == null || homeValue.isEmpty()) return null;
        if (homeValue.contains("$24,999")) return 12_500;
        if (homeValue.contains("$25,000")) return 37_500;
        if (homeValue.contains("$50,000")) return 62_500;
        if (homeValue.contains("$75,000")) return 87_500;
        if (homeValue.contains("$100,000")) return 112_500;
        if (homeValue.contains("$125,000")) return 137_500;
        if (homeValue.contains("$150,000")) return 162_500;
        if (homeValue.contains("$175,000")) return 187_500;
        if (homeValue.contains("$200,000")) return 225_000;
        if (homeValue.contains("$250,000")) return 275_000;
        if (homeValue.contains("$300,000")) return 325_000;
        if (homeValue.contains("$350,000")) return 375_000;
        if (homeValue.contains("$400,000")) return 425_000;
        if (homeValue.contains("$450,000")) return 475_000;
        if (homeValue.contains("$500,000")) return 550_000;
        if (homeValue.contains("$600,000")) return 650_000;
        if (homeValue.contains("$700,000")) return 750_000;
        if (homeValue.contains("$800,000")) return 850_000;
        if (homeValue.contains("$900,000")) return 950_000;
        if (homeValue.contains("$1,000,000")) return 1_000_000;
        return null;
    }

    /**
     * Returns a value from 250 to 15,000,000 or null.
     * Based on Data Axle data dictionary.
     * @return Wealth
     */
    public Integer parseWealthRange() {
        if (wealth == null || wealth.isEmpty()) return null;
        if (wealth.contains("$549")) return 250;
        if (wealth.contains("$550")) return 2_500;
        if (wealth.contains("$5,700")) return 12_500;
        if (wealth.contains("$20,703")) return 40_000;
        if (wealth.contains("$51,303")) return 60_000;
        if (wealth.contains("$71,501")) return 85_000;
        if (wealth.contains("$97,300")) return 112_500;
        if (wealth.contains("$126,800")) return 150_000;
        if (wealth.contains("$173,850")) return 200_000;
        if (wealth.contains("$220,900")) return 260_000;
        if (wealth.contains("$295,000")) return 330_000;
        if (wealth.contains("$369,100")) return 450_000;
        if (wealth.contains("$554,050")) return 650_000;
        if (wealth.contains("$739,000")) return 950_000;
        if (wealth.contains("$1,186,300")) return 1_900_000;
        if (wealth.contains("$2,743,733")) return 3_000_000;
        if (wealth.contains("$3,218,667")) return 3_500_000;
        if (wealth.contains("$3,693,600")) return 6_100_000;
        if (wealth.contains("$8,693,520")) return 10_500_000;
        if (wealth.contains("$13,693,440")) return 15_000_000;
        return null;
    }

    /**
     * Returns a value from 21 to 80 or null.
     * Based on Data Axle data dictionary.
     * @return Age
     */
    public Short parseAgeRange(){
        if(ageRange == null || ageRange.isEmpty()) return null;
        if(ageRange.contains("18")) return 21;
        if(ageRange.contains("25")) return 27;
        if(ageRange.contains("30")) return 32;
        if(ageRange.contains("35")) return 37;
        if(ageRange.contains("40")) return 42;
        if(ageRange.contains("45")) return 47;
        if(ageRange.contains("50")) return 52;
        if(ageRange.contains("55")) return 57;
        if(ageRange.contains("60")) return 62;
        if(ageRange.contains("69")) return 67;
        if(ageRange.contains("65+")) return 70;
        if(ageRange.contains("70")) return 72;
        if(ageRange.contains("75+")) return 80;
        return null;
    }

    /**
     * Returns 0 (rents), 1 (owns), or null.
     * Based on Data Axle data dictionary.
     * @return Age
     */
    public Short parseOwnRent(){
        if(ownRent == null ||ownRent.isEmpty()) return null;
        if(ownRent.contains("firm")) return 1;
        if(ownRent.contains("wns")) return 1;
        if(ownRent.contains("ent")) return 0;
        return null;
    }
}

