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

package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.model.profile.Profile;

import java.util.Date;

public class BasicAnnotation {

    private final Date date;
    public String id;
    TextSource textSource;
    Profile annotator;


    BasicAnnotation(String id, TextSource textSource, Profile annotator) {
        this.textSource = textSource;
        this.annotator = annotator;
        this.date = new Date();
        this.id = id;
    }

    public TextSource getTextSource() {
        return textSource;
    }

    public Profile getAnnotator() {
        return annotator;
    }

    public Date getDate() {
        return date;
    }

    public String getID() {
        return id;
    }

    void setID(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s", id);
    }
}
