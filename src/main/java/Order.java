import java.util.HashMap;

public class Order {

    private HashMap<Integer, Integer> orderUserMap;
    private HashMap<Integer,
            HashMap<Integer, Integer>> userDayHourMap;

    public HashMap<Integer, Integer> getOrderUserMap() {
        return orderUserMap;
    }

    public void setOrderUserMap(HashMap<Integer, Integer> orderUsermap) {
        this.orderUserMap = orderUsermap;
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getUserDayHourMap() {
        return userDayHourMap;
    }

    public void setUserDayHourMap(HashMap<Integer, HashMap<Integer, Integer>> userDAyHourMap) {
        this.userDayHourMap = userDAyHourMap;
    }
}
