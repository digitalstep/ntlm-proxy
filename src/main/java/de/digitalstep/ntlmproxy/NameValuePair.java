package de.digitalstep.ntlmproxy;

public class NameValuePair {

    private final String name, value;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (null == name) {
            return value;
        } else {
            return name + ": " + value;
        }
    }

}
