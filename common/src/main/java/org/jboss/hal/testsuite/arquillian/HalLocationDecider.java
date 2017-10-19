package org.jboss.hal.testsuite.arquillian;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.arquillian.graphene.location.decider.HTTPLocationDecider;
import org.jboss.arquillian.graphene.spi.location.Scheme;
import org.jboss.hal.testsuite.util.ConfigUtils;

/**
 * Appends the default profile to the location, if running in domain mode. Use this scheme for configuration pages:
 * <pre>
 * {@code @}Location(scheme = HalScheme.class, value = "#batch-jberet-configuration${profile:full}")
 * public class BatchPage {
 *     ...
 * }
 * </pre>
 */
public class HalLocationDecider extends HTTPLocationDecider {

    private final Scheme scheme;
    private final Map<Token, String> variables;

    public HalLocationDecider() {
        scheme = new HalScheme();
        variables = new HashMap<>();
        variables.put(new Token("profile", Mode.DOMAIN), ConfigUtils.getDefaultProfile());
    }

    @Override
    public Scheme canDecide() {
        return scheme;
    }

    @Override
    public String decide(String location) {
        String url = super.decide(location);
        for (Map.Entry<Token, String> entry : variables.entrySet()) {
            Token token = entry.getKey();
            Pattern pattern = Pattern.compile(token.pattern());
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                if (token.mode == Mode.DOMAIN && ConfigUtils.isDomain() ||
                        token.mode == Mode.STANDALONE && !ConfigUtils.isDomain()) {
                    String resolved = entry.getValue();
                    if (matcher.groupCount() == 1) {
                        resolved = StringUtils.stripStart(matcher.group(), ":");
                    }
                    url = matcher.replaceAll(resolved);
                } else {
                    url = matcher.replaceAll("");
                }
            }
        }
        return url;
    }


    private enum Mode {
        STANDALONE, DOMAIN
    }


    private static class Token {

        private final String variable;
        private final Mode mode;

        private Token(String variable, Mode mode) {
            this.variable = variable;
            this.mode = mode;
        }

        String pattern() {
            return "\\$\\{" + variable + "(:\\w+)?\\}";
        }
    }
}

