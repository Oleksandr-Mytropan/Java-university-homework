package cz.upce.boop.ex.collection;

import cz.upce.boop.ex.collection.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BGenericListTest {

    private BGenericList<String> list;

    @BeforeEach
    public void setUp() {
        list = new BGenericList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");
    }

    @Test
    public void test1_get_first() {
        assertEquals("one", list.get(0));
    }

    @Test
    public void test2_size() {
        assertEquals(4, list.size());
    }

    @Test
    public void test3_get_minus() {
        assertThrows(IllegalArgumentException.class, () -> list.get(-1));
    }

    @Test
    public void test4_get_outOfbounds() {
        assertThrows(IllegalArgumentException.class, () -> list.get(5));
    }

    @ParameterizedTest
    @CsvSource({
        "0, one",
        "1, two",
        "2, three",
        "3, four"})
    public void test5_parameters(int index, String expected) {
        assertEquals(expected, list.get(index));
    }

    @Test
    public void test6_addElement() {
        list.add("five");
        assertEquals(5, list.size());
        assertEquals("five", list.get(4));
    }

    @Test
    public void test7_readOnly() {
        BList<String> ro = list.asReadOnly();

        assertInstanceOf(BGenericList.BReadOnlyList.class, ro);
    }

    @Test
    public void test8_asReadOnly_copyOfList() {
        BList<String> ro = list.asReadOnly();

        List<String> expected = List.of("one", "two", "three", "four");

        assertIterableEquals(expected, ro);
    }

    @Test
    public void test9_asReadOnly_unusableElementBeforeCopy() {
        BList<String> ro = list.asReadOnly();

        list.add("five");

        List<String> expected = List.of("one", "two", "three", "four");

        assertIterableEquals(expected, ro);
    }

    @Test
    public void test10_asReadOnly_constantCheck() {
        BList<String> ro = list.asReadOnly();

        assertThrows(UnsupportedOperationException.class, () -> ro.add("newElement"));
        assertThrows(UnsupportedOperationException.class, () -> ro.set(0, "newElement2"));
        assertThrows(UnsupportedOperationException.class, () -> ro.remove(0));
    }
}
