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
            result = result.replaceAll("&"+params[i]+";", paramValues[i]);
        }
        return result;
    }

    public String getName() { return name; }
    public String[] getParams() { return params; }
    public String getBody() { return body; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Macro macro = (Macro) o;

        return getName().equals(macro.getName()) &&
            Arrays.equals(getParams(), macro.getParams()) &&
            getBody().equals(macro.getBody());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + Arrays.hashCode(getParams());
        result = 31 * result + getBody().hashCode();
        return result;
    }

    private final String name;
    private final String[] params;
    private final String body;
}
