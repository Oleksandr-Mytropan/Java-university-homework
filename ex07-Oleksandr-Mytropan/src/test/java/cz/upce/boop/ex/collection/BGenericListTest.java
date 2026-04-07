package cz.upce.boop.ex.collection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BGenericListTest {

    // Úkol 1 – klonování BGenericList
    @Test
    void clone_ListAsNewObjectAndSameElements() {
        BGenericList<String> original = new BGenericList<>();
        original.add("a");
        original.add("b");
        original.add("c");

        BGenericList<String> clone = original.clone();

        assertNotSame(original, clone, "Klon musí být nový objekt");
        assertEquals(original, clone,   "Klon musí být shodný s originálem");
    }

    @Test
    void clone_addToOriginal_doesNotAffectClone() {
        BGenericList<String> original = new BGenericList<>();
        original.add("x");
        original.add("y");

        BGenericList<String> clone = original.clone();
        
        original.add("z");         

        assertEquals(2, clone.size(), "Klon nesmí být ovlivněn přidáním do originálu");
    }

    @Test
    void clone_removeFromOriginal_doesNotAffectClone() {
        BGenericList<Integer> original = new BGenericList<>();
        original.add(1);
        original.add(2);
        original.add(3);

        BGenericList<Integer> clone = original.clone();
        
        original.remove(0);

        assertEquals(3, clone.size(), "Klon nesmí být ovlivněn odebráním z originálu");
    }

    // Úkol 2 – klonování BGenericList.BReadOnlyList
    @Test
    void readOnlyList_cloneReturnsSameObject() {
        BGenericList<String> list = new BGenericList<>();
        list.add("hello");
        list.add("world");

        BGenericList.BReadOnlyList<String> readOnly =
                (BGenericList.BReadOnlyList<String>) list.asReadOnly();

        BGenericList<String> clone = readOnly.clone();

        assertSame(readOnly, clone, "BReadOnlyList.clone() musí vrátit stejný objekt (immutable)");
    }

    // Úkol 3 – statická tovární metoda BList.of(...)
    @Test
    void bListOf_containsCorrectElements() {
        BList<String> list = BList.of("one", "two", "three");

        assertEquals(3,       list.size(),    "Velikost musí být 3");
        assertEquals("one",   list.get(0),    "První prvek musí být 'one' ");
        assertEquals("two",   list.get(1),    "Druhý prvek musí být 'two' ");
        assertEquals("three", list.get(2),    "Třetí prvek musí být 'three'" );
    }

    @Test
    void bListOf_NonEditable_check() {
        BList<String> list = BList.of("a", "b");
        assertThrows(UnsupportedOperationException.class,
                () -> list.add("c"),
                "add() musí vyhodit UnsupportedOperationException");
    }

    @Test
    void bListOf_NonRemovable_check() {
        BList<Integer> list = BList.of(1, 2, 3);
        assertThrows(UnsupportedOperationException.class,
                () -> list.remove(0),
                "remove() musí vyhodit UnsupportedOperationException");
    }

    @Test
    void bListOf_NonSettable_check() {
        BList<Integer> list = BList.of(10, 20);
        assertThrows(UnsupportedOperationException.class,
                () -> list.set(0, 231),
                "set() musí vyhodit UnsupportedOperationException");
    }
}
