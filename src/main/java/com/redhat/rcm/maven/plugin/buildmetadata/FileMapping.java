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
package com.redhat.rcm.maven.plugin.buildmetadata;

import java.io.File;

/**
 * Maps an properties output file location to a packaging.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class FileMapping
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The project's packaging as specified in the <code>packaging</code> element
   * of a POM to be mapped to a location to write the
   * <code>build.properties</code>.
   */
  private String packaging;

  /**
   * The name of the properties file to write.
   */
  private File outputFile;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor for Maven.
   */
  public FileMapping()
  {
  }

  /**
   * Default constructor for Maven.
   *
   * @param packaging the project's packaging as specified in the
   *          <code>packaging</code> element of a POM to be mapped to a location
   *          to write the <code>build</code>.
   * @param outputFile the name of the properties file to write.
   */
  public FileMapping(final String packaging, final File outputFile)
  {
    this.packaging = packaging;
    this.outputFile = outputFile;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the project's packaging as specified in the <code>packaging</code>
   * element of a POM to be mapped to a location to write the
   * <code>build. properties</code>.
   *
   * @return the project's packaging as specified in the <code>packaging</code>
   *         element of a POM to be mapped to a location to write the
   *         <code>build</code>.
   */
  public String getPackaging()
  {
    return packaging;
  }

  /**
   * Returns the name of the properties file to write.
   *
   * @return the name of the properties file to write.
   */
  public File getOutputFile()
  {
    return outputFile;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
