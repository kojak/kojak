/*
 * Copyright 2006-2013 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.rcm.maven.plugin.buildmetadata.scm.maven;

import java.util.Date;

import org.apache.maven.scm.ScmVersion;

/**
 * The revision implementation for the Maven SCM.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class MavenRevision extends StringRevision
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   * <p>
   * The value of this constant is {@value}.
   */
  private static final long serialVersionUID = 1L;

  // --- members --------------------------------------------------------------

  /**
   * The type of the revision. May be for instance trunk, branch or tag.
   */
  private final String type;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param version the SCM version provided by Maven.
   * @param date the revision date.
   * @see com.redhat.rcm.maven.plugin.buildmetadata.scm.maven.StringRevision#StringRevision(java.lang.String,
   *      java.util.Date)
   */
  public MavenRevision(final ScmVersion version, final Date date)
  {
    super(version.getName(), date);
    this.type = version.getType();
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the type of the revision. May be for instance trunk, branch or tag.
   *
   * @return the type of the revision.
   */
  public String getType()
  {
    return type;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public String toString()
  {
    return super.toString() + " (" + type + ')';
  }
}
