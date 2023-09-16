package com.cobelpvp.atheneum.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class ArgumentProcessor implements Processor<String[], Arguments> {
    @Override
    public Arguments process(String[] value) {
        final Set<String> flags = new HashSet<String>();
        final List<String> arguments = new ArrayList<String>();
        for (final String s : value) {
            if (!s.isEmpty()) {
                if (s.charAt(0) == '-' && !s.equals("-") && this.matches(s)) {
                    final String flag = this.getFlagName(s);
                    flags.add(flag);
                } else {
                    arguments.add(s);
                }
            }
        }
        return new Arguments(arguments, flags);
    }

    private String getFlagName(String flag) {
        final Matcher matcher = Flag.FLAG_PATTERN.matcher(flag);
        if (matcher.matches()) {
            final String name = matcher.replaceAll("$2$3");
            return (name.length() == 1) ? name : name.toLowerCase();
        }
        return null;
    }

    private boolean matches(String flag) {
        return Flag.FLAG_PATTERN.matcher(flag).matches();
    }
}
