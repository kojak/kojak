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
package com.redhat.rcm.maven.plugin.buildmetadata.data;

import java.util.Map;

/**
 * Configuration instance to create instances of
 * {@link com.redhat.rcm.maven.plugin.buildmetadata.data.MetaDataProvider} by the
 * {@link com.redhat.rcm.maven.plugin.buildmetadata.data.MetaDataProviderBuilder}.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public class Provider
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The name of the property to indicate that a provider is to be run with the
   * build point mojo. Usually this kind of provider export the build
   * information to a backend system or measures time at a given point of the
   * build.
   * <p>
   * The value of this constant is {@value}.
   */
  public static final String RUN_AT_BUILD_POINT = "runAtBuildPoint";

  // --- members --------------------------------------------------------------

  /**
   * The class to instantiate.
   */
  private String type;

  /**
   * Properties to set.
   */
  private Map<String, String> properties;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the class to instantiate.
   *
   * @return the class to instantiate.
   */
  public final String getType()
  {
    return type;
  }

  /**
   * Returns the value for properties.
   * <p>
   * Properties to set.
   *
   * @return the value for properties.
   */
  public final Map<String, String> getProperties()
  {
    return properties;
  }

  /**
   * Checks if the provider is configured to be run at the end of the build.
   * Usually this kind of provider export the build information to a backend
   * system.
   *
   * @return <code>true</code> if the provider runs at the end of the build,
   *         <code>false</code> if it runs at the start.
   */
  public final boolean isRunAtEndOfBuild()
  {
    return ("true".equals(properties.get(RUN_AT_BUILD_POINT)));
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
