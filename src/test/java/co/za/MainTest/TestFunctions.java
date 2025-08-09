package co.za.MainTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import co.za.Main.QueryFunction;

public class TestFunctions {

    Long a = 5L;
    Long b = 3L;
    Long c = 2L;

    // Edge case values
    Long edgeA = 0L;
    Long edgeB = -1L;
    Long edgeC = 1_000_000L;

    QueryFunction queryFunction = new QueryFunction(a, b, c);
    QueryFunction edgeQueryFunction = new QueryFunction(edgeA, edgeB, edgeC);

    @Test
    public void testQueryFunction() {
        Assertions.assertEquals(5L, queryFunction.returnA());
        Assertions.assertEquals(3L, queryFunction.returnB());
        Assertions.assertEquals(2L, queryFunction.returnC());
    }
    @Test
    public void testEdgeQueryFunction() {
        Assertions.assertEquals(999999L, edgeQueryFunction.returnA());
        Assertions.assertEquals(-1_000_000L, edgeQueryFunction.returnB());
        Assertions.assertEquals(1L, edgeQueryFunction.returnC());
    }
}
