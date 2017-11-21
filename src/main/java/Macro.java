import java.util.Arrays;

public class Macro {
    public Macro(final String name, final String body, final String ... params) {
        this.name = name;
        this.body = body;
        this.params = Arrays.copyOf(params, params.length);
    }

    public String apply(final String ... paramValues) {
        if(paramValues.length != params.length)
            throw new IllegalArgumentException("Expected " + params.length + "arguments, "
                    + paramValues.length + " given");
        String result = body;
        for (int i = 0; i < paramValues.length; i++) {
            result = result.replaceAll("&"+params[i], paramValues[i]);
        }
        return result;
    }

    private final String name;
    private final String[] params;
    private final String body;
}
