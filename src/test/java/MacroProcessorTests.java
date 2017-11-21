import org.junit.Test;
import static org.junit.Assert.*;

public class MacroProcessorTests {
    private String macroEnd = "## ENDM\n";

    @Test public void applyWithEmptyBodyTest() {
        String text = "## name MACRO\n" + macroEnd + "text ##name()text";
        String expected = "text text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyWithoutParamsTest() {
        String text =  "## name MACRO\ndata data data\n" + macroEnd +
                "other text other text\n##name()\nother text other text\n";
        String expected = "other text other text\ndata data data\nother text other text\n";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyInlineDefinitionTest() {
        String text = "##name MACRO data data data " + macroEnd + "text ##name() text";
        String expected = "text data data data text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyOneParameterTest() {
        String macroDefinitionOneParameter = "## name param1 MACRO\n" +
                "macro data macro data\n&param1;macro data &param1;\n" +
                macroEnd;
        String text = macroDefinitionOneParameter + "text text##name(value)text text";
        String expected = "text textmacro data macro data\nvaluemacro data value\ntext text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyMultipleParamsTest() {
        String macroDefinitionMultipleParams = "## name param1 param2 param3 MACRO\n" +
                "macro data &param2; &param1; &param3; macro data" +
                macroEnd;
        String text = macroDefinitionMultipleParams + "text text##name(value1, value2, value3)text text";
        String expected = "text textmacro data value2 value1 value3 macro datatext text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyMultipleDefinitionsTest() {
        String macroDefinitions = "## name1 MACRO\ndata1\n" + macroEnd + "## name2 MACRO\ndata2\n" + macroEnd;
        String text = macroDefinitions + "text ##name2() text ##name1() text";
        String expected = "text data2 text data1 text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyDefinitionInTheEndTest() {
        String text = "text ##name() text\n## name MACRO\ndata\n" + macroEnd;
        String expected = "text data text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test(expected = IllegalStateException.class)
    public void applyMacroWrongNameCall() {
        String text = "## name MACRO\ndata\n" + macroEnd + "##wrongName\n";
        MacroProcessor.apply(text);
    }

    @Test(expected = IllegalStateException.class)
    public void applyOverloadFailTest() {
        String text = "## name MACRO\ndata\n" + macroEnd +
                "## name MACRO\ndata2\n" + macroEnd;
        MacroProcessor.apply(text);
    }

    @Test public void applyLoadMacroLibraryTest() {
        String text = "## MACRO macroLibrary.txt\ntext ##name() text";
        String expected = "text data text";
        assertEquals(expected, MacroProcessor.apply(text));
    }
}
