package macro;

import macro.Macro;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroProcessor {
    public static String apply(String text) {
        Map<String, Macro> macros = getMacros(text);
        try { macros.putAll(loadLibraries(text)); }
        catch(IOException e) { throw new UncheckedIOException(e);}
        String plainText = eraseMacroDefinitions(text);
        return substituteAll(plainText, macros);
    }

    private static Map<String, Macro> loadLibraries(final String text) throws IOException {
        Matcher matcher = Pattern.compile(MACRO_LOAD_LIBRARY).matcher(text);
        Map<String, Macro> macros = new HashMap<>();
        while(matcher.find()) {
            Path path = Paths.get(matcher.group("path").trim());
            macros.putAll(getMacros(new FileReader(path.toFile())));
        }
        return macros;
    }

    private static String eraseMacroDefinitions(final String text) {
        return text.replaceAll(MACRO_DEFINITION, "").replaceAll(MACRO_LOAD_LIBRARY, "");
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
                throw new IllegalStateException("macro.Macro " + macro.getName() + " already defined");
            else macros.put(macro.getName(), macro);
        }
        return macros;
    }

    static Map<String, Macro> getMacros(final Reader in) throws IOException {
        String text = readerToString(in);
        return getMacros(text);
    }

    static String substituteAll(final String text, final Map<String, Macro> macros) {
        Matcher matcher = Pattern.compile(MACRO_CALL).matcher(text);
        String result = text;
        String replacement = "";
        while(matcher.find()) {
            String rawParams = matcher.group("params");
            if(!macros.containsKey(matcher.group("name")))
                throw new IllegalStateException("macro.Macro " + matcher.group("name") + " not defined");
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

    public static String readerToString(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line = null;
        StringBuilder builder = new StringBuilder();
        while ((line = in.readLine()) != null)
            builder.append(line+"\n");
        return builder.toString();
    }

    private static final String NAME = "[a-zA-Z0-9]+";
    private static final String PARAM = "&[a-zA-Z0-9]+;";
    private static final String MACRO_LOAD_LIBRARY = "##\\s*MACRO\\s+(?<path>.*)\\s+##\\s*ENDM";
    private static final String MACRO_DEFINITION = "##\\s*(?<name>"+NAME+")\\s+(?<params>("+NAME+"\\s+)*)MACRO\\s+(?<body>.*)?\\s+##\\s*ENDM\\s*";
    private static final String MACRO_CALL = "##\\s*(?<name>"+NAME+")\\s*\\(\\s*(?<params>(("+NAME+"\\s*,\\s*)*"+NAME+")\\s*)?\\)";
}
