import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroProcessor {
    public static String apply(String text) {
        Map<String, Macro> macros = getMacros(text);
        String plainText = eraseMacroDefinitions(text);
        return substituteAll(plainText, macros);
    }

    private static String eraseMacroDefinitions(final String text) {
        return text.replaceAll(MACRO_DEFINITION, "");
    }

    static Map<String, Macro> getMacros(final String text) {
        Matcher matcher = Pattern.compile(MACRO_DEFINITION).matcher(text);
        Map<String, Macro> macros = new HashMap<>();
        while(matcher.find()) {
            String params = matcher.group("params");
            Macro macro;
            if(!params.replaceAll("\\s", "").isEmpty())
                macro = new Macro(matcher.group("name"),
                    matcher.group("body"),
                    params.replaceAll("\\s\\s", " ").trim().split("\\s"));
            else macro = new Macro(matcher.group("name"), matcher.group("body"));

            if(macros.containsKey(macro.getName()))
                throw new IllegalStateException("Macro " + macro.getName() + " already defined");
            else macros.put(macro.getName(), macro);
        }
        return macros;
    }

    static String substituteAll(final String text, final Map<String, Macro> macros) {
        Matcher matcher = Pattern.compile(MACRO_CALL).matcher(text);
        String result = text;
        String replacement = "";
        while(matcher.find()) {
            String rawParams = matcher.group("params");
            if(!macros.containsKey(matcher.group("name")))
                throw new IllegalStateException("Macro " + matcher.group("name") + " not defined");
            Macro macro = macros.get(matcher.group("name"));
            if(rawParams != null) {
                String[] params = rawParams.trim().replaceAll("\\s", "").split(",");
                replacement =  macro.apply(params);
            }
            else replacement = macro.getBody();
            replacement = substituteAll(replacement, macros);
            result = result.replace(text.substring(matcher.start(), matcher.end()), replacement);
        }
        return result;
    }

    private static final String NAME = "[a-zA-Z0-9]+";
    private static final String PARAM = "&[a-zA-Z0-9]+;";
    private static final String MACRO_LOAD_LIBRARY = "##\\s*MACRO\\s+(?<path>.*)";
    private static final String MACRO_DEFINITION = "##\\s*(?<name>"+NAME+")\\s+(?<params>("+NAME+"\\s+)*)MACRO\\s+(?<body>.*)?\\s+##\\s*ENDM\\s*";
    private static final String MACRO_CALL = "##\\s*(?<name>"+NAME+")\\s*\\(\\s*(?<params>(("+NAME+"\\s*,\\s*)*"+NAME+")\\s*)?\\)";
}
