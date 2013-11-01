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
package com.redhat.rcm.maven.plugin.buildmetadata.util;

/**
 * Simply passes through the original string.
 */
public final class FilePathNormalizer implements Normalizer
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The path to the root folder. Used to trim references to project files.
   */
  private final String baseDir;

  /**
   * The precalculated length of the {@link #baseDir} string.
   */
  private final int prefixLength;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param baseDir the path to the root folder.
   * @throws NullPointerException if {@code baseDir} is <code>null</code>.
   */
  public FilePathNormalizer(final String baseDir) throws NullPointerException
  {
    this.baseDir = baseDir.trim();
    this.prefixLength = baseDir.length();
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the path to the root folder. Used to trim references to project
   * files.
   *
   * @return the path to the root folder.
   */
  public String getBaseDir()
  {
    return baseDir;
  }

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   * <p>
   * Removed the prefix path defined by the {@code baseDir} passed in via the
   * {@link #FilePathNormalizer(String)} constructor.
   * </p>
   */
  public String normalize(final String input)
  {
    final String prefixed =
        input.startsWith(baseDir) ? input.substring(prefixLength) : input;
    final String norm = prefixed.replace('\\', '/');
    if (norm.charAt(0) == '/' && norm.length() > 1)
    {
      return norm.substring(1);
    }
    return norm;
  }

  // --- object basics --------------------------------------------------------

}
