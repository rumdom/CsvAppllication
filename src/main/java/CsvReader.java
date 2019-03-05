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

        Order order = readOrdersPerUserPerDayPerHour ( );
        HashMap<Integer,
                HashMap<Integer, Integer>> userDayHourListMap = order.getUserDayHourMap ( );
        HashMap<Integer, Integer> orderUserListMap = order.getOrderUserMap ( );
        HashMap<Integer, String> departmentIdNameListMap = readAllDepartments ( );
        HashMap<Integer, Integer> productDepartmentMap = readProductDepartment ( );
//-----------------------------------------------reading product order list--------------------------------------------------------------------------------
        TreeMap<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> dayMap;
        dayMap = readOrderProduct ( userDayHourListMap, orderUserListMap, departmentIdNameListMap, productDepartmentMap );
        printOutput ( dayMap, departmentIdNameListMap );
    }

    /**
     * This method is used to read Order product file and fetches all the necessary details from other maps to make a consolidated map.
     *
     * @param userDayHourListMap
     * @param orderUserListMap
     * @param departmentIdNameListMap
     * @param productDepartmentMap
     * @return
     */
    private static TreeMap<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> readOrderProduct(HashMap<Integer, HashMap<Integer, Integer>> userDayHourListMap, HashMap<Integer, Integer> orderUserListMap, HashMap<Integer, String> departmentIdNameListMap, HashMap<Integer, Integer> productDepartmentMap) {
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
                    Integer day;
                    Integer hour;
                    Iterator it = DayHour.entrySet ( ).iterator ( );
                    while (it.hasNext ( )) {
                        Entry pair = (Entry) it.next ( );
                        day = (Integer) pair.getKey ( );
                        hour = (Integer) pair.getValue ( );

                        if (dayMap.containsKey ( day )) {
                            hourMap = dayMap.get ( day );

                            if (hourMap.containsKey ( hour )) {
                                userMap = hourMap.get ( hour );

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
                                hourMap.put ( hour, userMap );

                            }
                        } else {
                            hourMap = new TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> ( );
                            dayMap.put ( day, hourMap );
                        }
                    }
                }
            }

        } catch (IOException e) {
        }
        return dayMap;
    }

    /**
     * This method  returns 2 maps wrapped in 'order' object 1. UserDayHourMap=<UserID, <Day, Hour>> 2. OrderUserMap =<OrderId,UserID>
     *
     * @return Order
     */
    private static Order readOrdersPerUserPerDayPerHour() {
        Order order = new Order ( );
        HashMap<Integer, Integer> userOrderList = new HashMap<Integer, Integer> ( );
        HashMap<Integer,
                HashMap<Integer, Integer>> userDayHourMap = new HashMap<Integer,
                HashMap<Integer, Integer>> ( );

        try {
            HashMap<Integer, Integer> dayHourMap;
            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\orders.csv" );
            Iterable<CSVRecord> orderRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : orderRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    if (record.get ( 2 ).equals ( "prior" )) {

                        Integer day = Integer.valueOf ( record.get ( 4 ) );
                        Integer hour = Integer.valueOf ( record.get ( 5 ) );
                        Integer orderId = Integer.valueOf ( record.get ( 0 ) );
                        Integer userId = Integer.valueOf ( record.get ( 1 ) );
                        userOrderList.put ( orderId, userId );
                        dayHourMap = new HashMap<Integer, Integer> ( );
                        dayHourMap.put ( day, hour );
                        userDayHourMap.put ( userId, dayHourMap );
                    }
                }
            }
            order.setUserDayHourMap ( userDayHourMap );
            order.setOrderUserMap ( userOrderList );
        } catch (IOException e) {
        }
        return order;
    }

    /**
     * This method is used to read all the products and its departments <productId,departmentId>
     *
     * @return
     */
    private static HashMap<Integer, Integer> readProductDepartment() {
        HashMap<Integer, Integer> productIdDepartmentIdMap = new HashMap<Integer, Integer> ( );
        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\products.csv" );
            Iterable<CSVRecord> productRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : productRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer productId = Integer.valueOf ( record.get ( 0 ) );
                    Integer departId = Integer.valueOf ( record.get ( 3 ) );
                    productIdDepartmentIdMap.put ( productId, departId );
                }
            }
        } catch (IOException e) {
        }
        return productIdDepartmentIdMap;
    }

    /**
     * This method is used to read all the departments <ID, Name>
     *
     * @return HashMap<Integer   ,       String>
     */
    private static HashMap<Integer, String> readAllDepartments() {
        HashMap<Integer, String> allDepartmentMap = new HashMap<Integer, String> ( );
        try {

            Reader in = new FileReader ( "C:\\Users\\get2r\\Documents\\instacart_2017_05_01\\departments.csv" );
            Iterable<CSVRecord> departmentRecords = CSVFormat.RFC4180.parse ( in );
            for (CSVRecord record : departmentRecords) {
                if (record.getRecordNumber ( ) != 1) {
                    Integer deptId = Integer.valueOf ( record.get ( 0 ) );
                    String departName = record.get ( 1 );
                    allDepartmentMap.put ( deptId, departName );
                }
            }
        } catch (IOException e) {
        }
        return allDepartmentMap;
    }

    /**
     * This prints the output per day-per hour- per department the orders percentage
     *
     * @param dayMap
     * @param departmentIdNameListMap
     */
    private static void printOutput(Map<Integer, TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>>> dayMap,
                                    HashMap<Integer, String> departmentIdNameListMap) {
        TreeMap<Integer, HashMap<Integer, HashMap<String, Integer>>> hourMap;
        HashMap<Integer, HashMap<String, Integer>> userMap;
        HashMap<String, Integer> departmentsNEWTemp;

        HashMap<String, Integer> departmentsFinal = new HashMap<String, Integer> ( );
        //setting all the departments in the map
        for (String departsName : departmentIdNameListMap.values ( )) {
            departmentsFinal.put ( departsName, new Integer ( 0 ) );
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

                        if (departmentsFinal.containsKey ( temp2.getKey ( ) )) {
                            count = departmentsFinal.get ( temp2.getKey ( ) );
                            count = count + departmentsNEWTemp.get ( temp2.getKey ( ) );
                            departmentsFinal.put ( temp2.getKey ( ), count );
                        }

                    }
                }
                Integer total = 0;
                for (String key : departmentsFinal.keySet ( )) {
                    total = total + departmentsFinal.get ( key );
                }
                //--------------sorting the map in descending order---------------------------------
                //convert map to a List
                List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>> ( departmentsFinal.entrySet ( ) );

                //sorting the list with a comparator
                Collections.sort ( list, new Comparator<Entry<String, Integer>> ( ) {
                    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                        return (o2.getValue ( )).compareTo ( o1.getValue ( ) );// for ascending o1.getValue()).compareTo(o2.getValue()
                    }
                } );

                //convert sortedMap back to Map
                Map<String, Integer> departmentsSortedMap = new LinkedHashMap<String, Integer> ( );
                for (Entry<String, Integer> x : list) {
                    departmentsSortedMap.put ( x.getKey ( ), x.getValue ( ) );
                }
                //---------------------------end of sorting logic-----------------------------------
                System.out.println ( "------------------------\t\t\ttotal:" + total );
                int i = 1;
                for (String key : departmentsSortedMap.keySet ( )) {
                    Integer value = departmentsSortedMap.get ( key );
                    System.out.println ( "\t\t\t" + i + ":" + key + "= " + value * 100 / total + "%" );
                    i++;
                }
            }
            System.out.print ( "-----------------------------------------------------------------" );
        }
    }


}

