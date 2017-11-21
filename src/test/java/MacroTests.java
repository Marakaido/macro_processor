import org.junit.Test;
import static org.junit.Assert.*;

public class MacroTests {
    @Test public void applyWithoutParamsTest() {
        String body = "there was a man who had a dog and Bingo was his name";
        Macro macro = new Macro("name", body);
        assertEquals(body, macro.apply());
    }

    @Test public void applyOneParameterTest() {
        String body = "there was a man who had a dog and &human was his name";
        String expected = "there was a man who had a dog and Bingo was his name";
        Macro macro = new Macro("name", body, "human");
        assertEquals(expected, macro.apply("Bingo"));
    }

    @Test public void applyMultipleParamsTest() {
        String body = "there was a &sex who had a &animal and &human was his name";
        String expected = "there was a man who had a dog and Bingo was his name";
        Macro macro = new Macro("name", body, "sex", "animal", "human");
        assertEquals(expected, macro.apply("man", "dog", "Bingo"));
    }

    @Test public void emptyBodyTest() {
        String body = "";
        Macro macro = new Macro("name", body);
        assertEquals(body, macro.apply());
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyTooFewArgumentsTest() {
        String body = "there was a &sex who had a &animal and &human was his name";
        String expected = "there was a man who had a dog and Bingo was his name";
        Macro macro = new Macro("name", body, "sex", "animal", "human");
        macro.apply("man", "dog");
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyTooManyArgumentsTest() {
        String body = "there was a &sex who had a &animal and &human was his name";
        String expected = "there was a man who had a dog and Bingo was his name";
        Macro macro = new Macro("name", body, "sex", "animal", "human");
        macro.apply("man", "dog", "Bingo", "Slavko");
    }

    @Test public void applyEscapeSymbolTest() {
        String body = "M&&M's";
        Macro macro = new Macro("name", body);
        assertEquals(body, macro.apply());
    }
}
