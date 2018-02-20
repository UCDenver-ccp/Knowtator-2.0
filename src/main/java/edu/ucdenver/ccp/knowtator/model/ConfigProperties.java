/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model;

import java.io.File;

public class ConfigProperties {

    private File projectLocation;
    private File articlesLocation;
    private File ontologiesLocation;


    public ConfigProperties() {
    }

    public File getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(File projectLocation) {
        this.projectLocation = projectLocation;
    }

    /*
     * Returns input string with environment variable references expanded, e.g. $SOME_VAR or ${SOME_VAR}
     */
//    private String resolveEnvVars(String input)
//    {
//        if (null == input)
//        {
//            return null;
//        }
//        // match ${ENV_VAR_NAME} or $ENV_VAR_NAME
//        Pattern p = Pattern.compile("\\$[{]*(\\w+\\.\\w+|\\w+)[}]*");
//        Matcher m = p.matcher(input); // get a matcher object
//        StringBuffer sb = new StringBuffer();
//        while (m.find()){
//            String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
//            String envVarValue = System.getProperty(envVarName);
//            envVarValue = envVarValue.replaceAll("\\\\+", "/");
//            m.appendReplacement(sb, envVarValue);
//        }
//        m.appendTail(sb);
//        return sb.toString();
//    }

    public File getArticlesLocation() {
        return articlesLocation;
    }

    public void setArticlesLocation(File articlesLocation) {
        this.articlesLocation = articlesLocation;
    }

    public File getOntologiesLocation() {
        return ontologiesLocation;
    }

    public void setOntologiesLocation(File ontologiesLocation) {
        this.ontologiesLocation = ontologiesLocation;
    }
}
