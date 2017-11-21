import java.util.Arrays;

public class Macro {
    public Macro(final String name, final String[] params, final String body) {
        this.name = name;
        this.body = body;
        this.params = Arrays.copyOf(params, params.length);
    }

    public String apply(final String ... paramValues) {
        String result = new String(body);
        for (int i = 0; i < paramValues.length; i++) {
            result = result.replaceAll("&"+params[i], paramValues[i]);
        }
        return result;
    }

    private final String name;
    private final String[] params;
    private final String body;
}
