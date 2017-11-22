import org.junit.Test;

import java.util.Map;
import static java.util.Map.*;

import static org.junit.Assert.*;

public class MacroProcessorTests {
    private String macroEnd = "## ENDM";
    private Map<String, Macro> macros = Map.ofEntries(
            entry("name", new Macro("name", "data")),
            entry("name1", new Macro("name1", "data &param1; data", "param1")),
            entry("name2", new Macro("name2", "&param1; &param2;", "param1", "param2"))
    );

    @Test public void applyWithEmptyBodyTest() {
        String text = "## name MACRO\n " + macroEnd + "text ##name()text";
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
                "macro data &param1; data &param1;\n" +
                macroEnd;
        String text = macroDefinitionOneParameter + "text text##name(value)text text";
        String expected = "text textmacro data value data valuetext text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyMultipleParamsTest() {
        String macroDefinitionMultipleParams = "## name param1 param2 param3 MACRO\n" +
                "macro data &param2; &param1; &param3; macro data " +
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
        String text = "text ##name() text## name MACRO\ndata\n" + macroEnd;
        String expected = "text data text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test(expected = IllegalStateException.class)
    public void applyMacroWrongNameCall() {
        String text = "## name MACRO\ndata\n" + macroEnd + "##wrongName()\n";
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

    @Test public void applyNestedMacroTest() {
        String text = "##name MACRO data ##ENDM\n##name2 MACRO ##name() ##ENDM\ntext ##name2() text";
        String expected = "text data text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void applyDeepNestedMacroTest() {
        String text = "##name MACRO data ##ENDM\n##name2 MACRO data2 ##name() data2 ##ENDM\n" +
                "##name3 MACRO ##name2() ##ENDM" +
                "text ##name3() text";
        String expected = "text data2 data data2 text";
        assertEquals(expected, MacroProcessor.apply(text));
    }

    @Test public void macroParse() {
        String text = "##name   MACRO data data " + macroEnd;
        Macro macro = MacroProcessor.getMacros(text).get("name");
        Macro expected = new Macro("name", "data data");
        assertEquals(expected, macro);
    }

    @Test public void getMacrosSingleParameterParse() {
        String text = "## name param1 MACRO data data " + macroEnd;
        Macro macro = MacroProcessor.getMacros(text).get("name");
        Macro expected = new Macro("name", "data data", "param1");
        assertEquals(expected, macro);
    }

    @Test public void getMacrosMultipleParameterParse() {
        String text = "## name param1 param2 MACRO data data " + macroEnd;
        Macro macro = MacroProcessor.getMacros(text).get("name");
        Macro expected = new Macro("name", "data data", "param1", "param2");
        assertEquals(expected, macro);
    }

    @Test public void substituteAllTest() {
        String text = "##name()";
        String expected = macros.get("name").getBody();
        assertEquals(expected, MacroProcessor.substituteAll(text, macros));
    }

    @Test public void substituteAllSingleArgumentTest() {
        String text = "text ##name1(slavko) text";
        String expected = "text data slavko data text";
        assertEquals(expected, MacroProcessor.substituteAll(text, macros));
    }

    @Test public void substituteAllMultipleArgumentTest() {
        String text = "text ##name2(slavko, bob) text";
        String expected = "text slavko bob text";
        assertEquals(expected, MacroProcessor.substituteAll(text, macros));
    }
}
