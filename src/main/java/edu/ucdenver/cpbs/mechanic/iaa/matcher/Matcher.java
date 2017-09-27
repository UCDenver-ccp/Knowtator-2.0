/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Knowtator.
 *
 * The Initial Developer of the Original Code is University of Colorado.  
 * Copyright (C) 2005 - 2008.  All Rights Reserved.
 *
 * Knowtator was developed by the Center for Computational Pharmacology
 * (http://compbio.uchcs.edu) at the University of Colorado Health 
 *  Sciences Center School of Medicine with support from the National 
 *  Library of Medicine.  
 *
 * Current information about Knowtator can be obtained at 
 * http://knowtator.sourceforge.net/
 *
 * Contributor(s):
 *   Philip V. Ogren <philip@ogren.info> (Original Author)
 */
package edu.ucdenver.cpbs.mechanic.iaa.matcher;

import edu.ucdenver.cpbs.mechanic.iaa.Annotation;
import edu.ucdenver.cpbs.mechanic.iaa.IAA;

import java.util.Set;


public interface Matcher {
	/**
	 * @param annotation
	 *            the annotation that is to be matched
	 * @param compareSetName
	 *            the set of annotations that we will look for a match in
	 * @param excludeAnnotations
	 *            a set of annotations that cannot be the return value
	 * @param iaa
	 *            an instance of IAA from which to get information about
	 *            annotations
	 * @param matchResult
	 *            instantiate a new MatchType and pass it to the method. The
	 *            method must set the result to one of the four result values
	 *            given in MatchResult.
	 * @return the annotation that was matched. If none exists then return null.
	 */
	Annotation match(Annotation annotation, String compareSetName, Set<Annotation> excludeAnnotations, IAA iaa,
					 MatchResult matchResult);

	String getName();

	String getDescription();

	boolean returnsTrivials();
}
