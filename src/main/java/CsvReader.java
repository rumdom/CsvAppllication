import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

public class CsvReader {
    public static void main(String[] args) {
        //retrieving the 2 list maps from orders 1. order-user listMap, 2. user-Day-Hour listMap
        Pair<HashMap<Integer,
                HashMap<Integer, Integer>>, HashMap<Integer, Integer>> ordersPerUserPerDayAndPerHour;
        ordersPerUserPerDayAndPerHour = getOrdersPerUserPerDayPerHour ( );
        HashMap<Integer,
                HashMap<Integer, Integer>> userDayHourListMap = ordersPerUserPerDayAndPerHour.getKey ( );
        HashMap<Integer, Integer> orderUserListMap = ordersPerUserPerDayAndPerHour.getValue ( );
        HashMap<Integer, String> departmentIdNameListMap = getAllDepartment ( );
        HashMap<Integer, Integer> productDepartmentMap = readProductDepartment ( );
//-----------------------------------------------reading product order list--------------------------------------------------------------------------------
        TreeMap<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> dayMap;
        dayMap = getOrderProduct ( userDayHourListMap, orderUserListMap, departmentIdNameListMap, productDepartmentMap );
        printOutput ( dayMap, departmentIdNameListMap );
    }

    private static TreeMap<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> getOrderProduct(HashMap<Integer, HashMap<Integer, Integer>> userDayHourListMap, HashMap<Integer, Integer> orderUserListMap, HashMap<Integer, String> departmentIdNameListMap, HashMap<Integer, Integer> productDepartmentMap) {
        TreeMap<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> dayMap = new
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
                    Integer user = orderUserListMap.get ( order );
                    Integer department = productDepartmentMap.get ( product );


                    HashMap<Integer, Integer> DayHour = userDayHourListMap.get ( orderUserListMap.get ( order ) );
                    String departmentName = departmentIdNameListMap.get ( productDepartmentMap.get ( product ) );
//--------------------------------------------------------------------------
                    Integer keyDay;
                    Integer hourDay;
                    Iterator it = DayHour.entrySet ( ).iterator ( );
                    while (it.hasNext ( )) {
                        Entry pair = (Entry) it.next ( );
                        keyDay = (Integer) pair.getKey ( );
                        hourDay = (Integer) pair.getValue ( );

                        if (dayMap.containsKey ( keyDay )) {
                            hourMap = dayMap.get ( keyDay );

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
                            dayMap.put ( keyDay, hourMap );
                        }


                    }
                }
            }


        } catch (IOException e) {
        }
        return dayMap;
    }


    private static Pair<HashMap<Integer,
            HashMap<Integer, Integer>>, HashMap<Integer, Integer>> getOrdersPerUserPerDayPerHour() {

        HashMap<Integer, Integer> userOrderList = new HashMap<Integer, Integer> ( );
        HashMap<Integer,
                HashMap<Integer, Integer>> userDayHourList = new HashMap<Integer,
                HashMap<Integer, Integer>> ( );

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
        return new Pair<HashMap<Integer,
                HashMap<Integer, Integer>>, HashMap<Integer, Integer>> ( userDayHourList, userOrderList );
    }

    private static HashMap<Integer, Integer> readProductDepartment() {
        HashMap<Integer, Integer> productDepartmentListMap = new HashMap<Integer, Integer> ( );
        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\products.csv" );
            Iterable<CSVRecord> productRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : productRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer product = Integer.valueOf ( record.get ( 0 ) );
                    Integer depart = Integer.valueOf ( record.get ( 3 ) );
                    productDepartmentListMap.put ( product, depart );
                }
            }
        } catch (IOException e) {
        }
        return productDepartmentListMap;
    }

    private static HashMap<Integer, String> getAllDepartment() {
        HashMap<Integer, String> allDepartmentListMap = new HashMap<Integer, String> ( );
        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\departments.csv" );
            Iterable<CSVRecord> departmentRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : departmentRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer dept = Integer.valueOf ( record.get ( 0 ) );
                    String departName = record.get ( 1 );
                    allDepartmentListMap.put ( dept, departName );
                }
            }
        } catch (IOException e) {
        }
        return allDepartmentListMap;
    }
    private static void printOutput(Map<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> dayMap, HashMap<Integer, String> departmentIdNameListMap) {
        TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> hourMap;
        HashMap<Integer, HashMap<String, Integer>> userMap;
        HashMap<String, Integer> departmentsNEWTemp;

        HashMap<String, Integer> departmentsNameCount = new HashMap<String, Integer> ( );

        for (String departsName : departmentIdNameListMap.values ( )) {
            departmentsNameCount.put ( departsName, new Integer ( 0 ) );
        }


        for (Entry<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> entry : dayMap.entrySet ( )) {
            System.out.println ( "\nDay :" + entry.getKey ( ) );
            System.out.print ( "\n" );
            hourMap = dayMap.get ( entry.getKey ( ) );
            TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> tempMap = entry.getValue ( );
            for (Entry<Integer, HashMap<Integer, HashMap<String, Integer>>> temp : tempMap.entrySet ( )) {
                userMap = hourMap.get ( temp.getKey ( ) );

                System.out.println ( "\n\t Hour :" + temp.getKey ( ) );
                System.out.println ( "\n\t size :" + userMap.values ( ).size ( ) );
                Integer count;
                for (Entry<Integer, HashMap<String, Integer>> temp1 : temp.getValue ( ).entrySet ( )) {
                    departmentsNEWTemp = userMap.get ( temp1.getKey ( ) );

                    for (Entry<String, Integer> temp2 : temp1.getValue ( ).entrySet ( )) {

                        if (departmentsNameCount.containsKey ( temp2.getKey ( ) )) {
                            count = departmentsNameCount.get ( temp2.getKey ( ) );
                            count = count + departmentsNEWTemp.get ( temp2.getKey ( ) );
                            departmentsNameCount.put ( temp2.getKey ( ), count );
                        }

                    }
                }
                Integer total = 0;
                for (String key : departmentsNameCount.keySet ( )) {
                    total = total + departmentsNameCount.get ( key );
                }
                //--------------sorting the map in descending order---------------------------------
                //convert map to a List
                List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>> ( departmentsNameCount.entrySet ( ) );

                //sorting the list with a comparator
                Collections.sort ( list, new Comparator<Entry<String, Integer>> ( ) {
                    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                        return (o2.getValue ( )).compareTo ( o1.getValue ( ) );// for ascending o1.getValue()).compareTo(o2.getValue()
                    }
                } );

                //convert sortedMap back to Map
                Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer> ( );
                for (Entry<String, Integer> x : list) {
                    sortedMap.put ( x.getKey ( ), x.getValue ( ) );
                }
                //---------------------------end of sorting logic-----------------------------------
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
    }


}

