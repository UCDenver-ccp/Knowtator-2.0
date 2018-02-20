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

package edu.ucdenver.ccp.knowtator.model.owl;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
class OntologyTranslator {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);

    private static final String SEP = ":";

    private static final String PREFIX_GO = "GO";

    private static final String LOCATION_GO = "http://purl.obolibrary.org/obo/go.owl";
//    public static final String VERSION_GO = "http://purl.obolibrary.org/obo/go/releases/2017-10-11/go.owl";
    private static final String LOCATION_GO_BASIC = "http://purl.obolibrary.org/obo/go/go-basic.obo";


    private static final Map<String, String> PREFIX_LOCATION_MAP = new HashMap<String, String>() {
        {
            put(PREFIX_GO, LOCATION_GO);
        }
    };

    private static final Map<String, String> LOCATION_VERSION_MAP = new HashMap<String, String>() {
        {
            put(LOCATION_GO, LOCATION_GO_BASIC);
        }
    };

    public static String translate(String toTranslate) {
        String prefix = toTranslate.split(SEP)[0];

        return(PREFIX_LOCATION_MAP.get(prefix));
    }

    public static String whichOntologyToUse(String ontologyLocation) {
        return (LOCATION_VERSION_MAP.get(ontologyLocation));
    }
}
