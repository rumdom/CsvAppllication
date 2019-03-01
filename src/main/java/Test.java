public class Test {
    public static void main(String[] args) {
        Test test = new Test ( );
        Integer total = test.getName ( 1, 2 );
        System.out.print ( "total" + total );
    }

    public Integer getName(int i, int j) {
        return i + j;

    }

}
