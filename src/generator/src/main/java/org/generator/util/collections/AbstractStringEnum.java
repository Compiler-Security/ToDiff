package org.generator.util.collections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractStringEnum implements  StringEnum{
    public AbstractStringEnum(String template) {
        this.template = template;
    }

    @Override
    public boolean match(String st) {
        Pattern pattern = Pattern.compile(template);
        Matcher matcher = pattern.matcher(st);
        return matcher.matches();
    }

    String template;
}
