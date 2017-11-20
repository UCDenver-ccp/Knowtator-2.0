package edu.ucdenver.ccp.knowtator.configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigProperties {

    public String getFormat() {
        return format;
    }

    public Boolean getAutoLoadOntologies() {
        return autoLoadOntologies;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setAutoLoadOntologies(Boolean autoLoadOntologies) {
        this.autoLoadOntologies = autoLoadOntologies;
    }

    private String format;
    private Boolean autoLoadOntologies;

    public String getDefaultSaveLocation() {
        return defaultSaveLocation;
    }

    public void setDefaultSaveLocation(String defaultSaveLocation) {
        this.defaultSaveLocation = resolveEnvVars(defaultSaveLocation);
    }

    private String defaultSaveLocation;

    public ConfigProperties() {
        autoLoadOntologies = true;
        format = "xml";
        setDefaultSaveLocation("${user.home}${file.separator}KnowtatorProjects${file.separator}");
    }

    /*
 * Returns input string with environment variable references expanded, e.g. $SOME_VAR or ${SOME_VAR}
 */
    private String resolveEnvVars(String input)
    {
        if (null == input)
        {
            return null;
        }
        // match ${ENV_VAR_NAME} or $ENV_VAR_NAME
        Pattern p = Pattern.compile("\\$[{]*(\\w+\\.\\w+|\\w+)[}]*");
        Matcher m = p.matcher(input); // get a matcher object
        StringBuffer sb = new StringBuffer();
        while (m.find()){
            String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
            String envVarValue = System.getProperty(envVarName);
            envVarValue = envVarValue.replaceAll("\\\\+", "/");
            m.appendReplacement(sb, envVarValue);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
