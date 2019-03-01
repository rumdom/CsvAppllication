import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCsvReader {

    @Test
    public void testMethod() {
        TestClass t = new TestClass ( );
        Integer total=t.getName ( 2, 2 );
        Assertions.assertNotNull ( total);
        Assertions.assertEquals (total,2+2 );
    }

}
