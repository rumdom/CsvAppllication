import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

public class CsvReader {
    public static void main(String[] args) {

        HashMap<Integer, Integer> userOrderList = new HashMap<Integer, Integer> ( );
        HashMap<Integer,
                HashMap<Integer, Integer>> userDayHourList = new HashMap<Integer, HashMap<Integer, Integer>> ( );
        getOrders ( userOrderList, userDayHourList );
        HashMap<Integer, String> allDepartmentList = getDepartment ( );
        HashMap<Integer, Integer> productDepartmentMap = readProductDepartment ( );
//-----------------------------------------------reading product order list--------------------------------------------------------------------------------

        Map<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> myMap = new
                TreeMap<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> ( );


        TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> hourMap;
        HashMap<Integer, HashMap<String, Integer>> userMap;
        HashMap<String, Integer> departmentOriginal;

        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\order_products__prior.csv" );
            Iterable<CSVRecord> productRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : productRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer order = Integer.valueOf ( record.get ( 0 ) );
                    Integer product = Integer.valueOf ( record.get ( 1 ) );
//getting some keys
                    Integer user = userOrderList.get ( order );
                    Integer department = productDepartmentMap.get ( product );


                    HashMap<Integer, Integer> DayHour = userDayHourList.get ( userOrderList.get ( order ) );
                    String departmentName = allDepartmentList.get ( productDepartmentMap.get ( product ) );
//--------------------------------------------------------------------------
                    Integer keyDay;
                    Integer hourDay;
                    Iterator it = DayHour.entrySet ( ).iterator ( );
                    while (it.hasNext ( )) {
                        Map.Entry pair = (Map.Entry) it.next ( );
                        keyDay = (Integer) pair.getKey ( );
                        hourDay = (Integer) pair.getValue ( );

                        if (myMap.containsKey ( keyDay )) {
                            hourMap = myMap.get ( keyDay );

                            if (hourMap.containsKey ( hourDay )) {
                                userMap = hourMap.get ( hourDay );

                                if (userMap.containsKey ( user )) {
                                    departmentOriginal = userMap.get ( user );
                                    if (!departmentOriginal.containsKey ( departmentName )) {
                                        departmentOriginal.put ( departmentName, new Integer ( 1 ) );
                                    }
                                } else {
                                    departmentOriginal = new HashMap<String, Integer> ( );
                                    userMap.put ( user, departmentOriginal );

                                }

                            } else {
                                userMap = new HashMap<Integer, HashMap<String, Integer>> ( );
                                hourMap.put ( hourDay, userMap );

                            }
                        } else {
                            hourMap = new TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> ( );
                            myMap.put ( keyDay, hourMap );
                        }


                    }
                }
            }
            HashMap<String, Integer> departmentsNEW = new HashMap<String, Integer> ( );
            HashMap<String, Integer> departmentsNEWTemp = null;
            for (String departsName : allDepartmentList.values ( )) {
                departmentsNEW.put ( departsName, new Integer ( 0 ) );
            }

            for (Map.Entry<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> entry : myMap.entrySet ( )) {
                System.out.println ( "\nDay :" + entry.getKey ( ) );
                System.out.print ( "\n" );
                hourMap = myMap.get ( entry.getKey ( ) );
                TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> tempMap = entry.getValue ( );
                for (Map.Entry<Integer, HashMap<Integer, HashMap<String, Integer>>> temp : tempMap.entrySet ( )) {
                    userMap = hourMap.get ( temp.getKey ( ) );

                    System.out.println ( "\n\t Hour :" + temp.getKey ( ) );
                    System.out.println ( "\n\t size :" + userMap.values ( ).size ( ) );
                    Integer count;
                    for (Map.Entry<Integer, HashMap<String, Integer>> temp1 : temp.getValue ( ).entrySet ( )) {
                        departmentsNEWTemp = userMap.get ( temp1.getKey ( ) );

                        for (Map.Entry<String, Integer> temp2 : temp1.getValue ( ).entrySet ( )) {

                            if (departmentsNEW.containsKey ( temp2.getKey ( ) )) {
                                count = departmentsNEW.get ( temp2.getKey ( ) );
                                count = count + departmentsNEWTemp.get ( temp2.getKey ( ) );
                                departmentsNEW.put ( temp2.getKey ( ), count );
                            }

                        }


                    }
                    Integer total = 0;
                    for (String key : departmentsNEW.keySet ( )) {
                        total = total + departmentsNEW.get ( key );
                    }
                    //--------------sorting the map in descending order---------------------------------
                    //convert map to a List
                    List<Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>> ( departmentsNEW.entrySet ( ) );

                    //sorting the list with a comparator
                    Collections.sort ( list, new Comparator<Entry<String, Integer>> ( ) {
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            return (o2.getValue ( )).compareTo ( o1.getValue ( ) );// for ascending o1.getValue()).compareTo(o2.getValue()
                        }
                    } );

                    //convert sortedMap back to Map
                    Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer> ( );
                    for (Entry<String, Integer> x : list) {
                        sortedMap.put ( x.getKey ( ), x.getValue ( ) );
                    }

                    //--------------------------------------------------------------
                    System.out.println ( "------------------------\t\t\ttotal:" + total );
                    int i = 1;
                    for (String key : sortedMap.keySet ( )) {
                        Integer value = sortedMap.get ( key );
                        System.out.println ( "\t\t\t" + i + ":" + key + "= " + value * 100 / total + "%" );
                        i++;
                    }
                }
                System.out.print ( "-----------------------------------------------------------------" );
            }


        } catch (IOException e) {
        }


    }

    private static void getOrders(HashMap<Integer, Integer> userOrderList, HashMap<Integer,
            HashMap<Integer, Integer>> userDayHourList) {
        try {
            HashMap<Integer, Integer> dayHourlist;
            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\orders.csv" );
            Iterable<CSVRecord> orderRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : orderRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    if (record.get ( 2 ).equals ( "prior" )) {

                        Integer keyDay = Integer.valueOf ( record.get ( 4 ) );
                        Integer hourDay = Integer.valueOf ( record.get ( 5 ) );
                        Integer order = Integer.valueOf ( record.get ( 0 ) );
                        Integer user = Integer.valueOf ( record.get ( 1 ) );
                        userOrderList.put ( order, user );
                        dayHourlist = new HashMap<Integer, Integer> ( );
                        dayHourlist.put ( keyDay, hourDay );
                        userDayHourList.put ( user, dayHourlist );

                    }
                }
            }
        } catch (IOException e) {
        }

    }

    private static HashMap<Integer, Integer> readProductDepartment() {
        HashMap<Integer, Integer> departmentList = new HashMap<Integer, Integer> ( );
        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\products.csv" );
            Iterable<CSVRecord> productRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : productRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer product = Integer.valueOf ( record.get ( 0 ) );
                    Integer depart = Integer.valueOf ( record.get ( 3 ) );
                    departmentList.put ( product, depart );
                }
            }
        } catch (IOException e) {
        }
        return departmentList;
    }

    private static HashMap<Integer, String> getDepartment() {
        HashMap<Integer, String> departmentList = new HashMap<Integer, String> ( );
        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\departments.csv" );
            Iterable<CSVRecord> departmentRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : departmentRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer dept = Integer.valueOf ( record.get ( 0 ) );
                    String departName = record.get ( 1 );
                    departmentList.put ( dept, departName );
                }
            }
        } catch (IOException e) {
        }
        return departmentList;
    }


}

