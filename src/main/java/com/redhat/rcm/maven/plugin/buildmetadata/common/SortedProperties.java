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
package com.redhat.rcm.maven.plugin.buildmetadata.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Class for sorting properties.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision: 9143 $
 */
public final class SortedProperties extends Properties
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The class version identifier.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final long serialVersionUID = 1L;

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @see java.util.Hashtable#keys()
   */
  @SuppressWarnings("all")
  public synchronized Enumeration<Object> keys()
  {
    final Enumeration<Object> keysEnum = super.keys();
    final List keyList = new ArrayList<Object>();
    while (keysEnum.hasMoreElements())
    {
      keyList.add(keysEnum.nextElement());
    }
    Collections.sort(keyList);
    return Collections.enumeration(keyList);
  }

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Factory method to create an instance of sorted properties.
   *
   * @param properties the properties to return a sorted instance for.
   * @return sorted properties.
   */
  public static Properties createSorted(final Properties properties)
  {
    final SortedProperties sortedProperties = new SortedProperties();
    sortedProperties.putAll(properties);
    return sortedProperties;
  }

  // --- object basics --------------------------------------------------------

}
