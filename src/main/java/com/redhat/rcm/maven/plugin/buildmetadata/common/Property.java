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

/**
 * Defines a property to be selected by the user to include into the build meta
 * data properties.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public final class Property
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The name of the system property to add to the build meta data properties.
   */
  private String name;

  /**
   * The name of the property to be used in the build meta data properties. This
   * allows to change the name of the property.
   */
  private String mappedName;

  /**
   * The value provided for the property. No lookup is performed. Standard Maven
   * properties are resolved.
   */
  private String value;

  /**
   * The section the property is added to. If omitted or invalid, the property
   * is added to the <code>build.misc</code> section.
   * <p>
   * Valid section identifiers are:
   * </p>
   * <ul>
   * <li><code>build.scm</code></li>
   * <li><code>build.dateAndVersion</code></li>
   * <li><code>build.runtime</code></li>
   * <li><code>build.java</code></li>
   * <li><code>build.maven</code></li>
   * </ul>
   */
  private String section;

  /**
   * The label used for reports. If unset, the name of the property is used as a
   * label.
   */
  private String label;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the name of the system property to add to the build meta data
   * properties.
   *
   * @return the name of the system property to add to the build meta data
   *         properties.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the name of the property in the build meta data properties. This
   * allows change the name of the property.
   *
   * @return the name of the property in the build meta data properties. If the
   *         mapped name is not given, the {@link #getName() name} is returned.
   */
  public String getMappedName()
  {
    return mappedName != null ? mappedName : getName();
  }

  /**
   * Returns the value provided for the property. No lookup is performed.
   * Standard Maven properties are resolved.
   *
   * @return the value provided for the property.
   */
  public String getValue()
  {
    return value;
  }

  /**
   * Returns the section the property is added to. If omitted or invalid, the
   * property is added to the <code>build.misc</code> section.
   * <p>
   * Valid section identifiers are:
   * </p>
   * <ul>
   * <li><code>build.scm</code></li>
   * <li><code>build.dateAndVersion</code></li>
   * <li><code>build.runtime</code></li>
   * <li><code>build.java</code></li>
   * <li><code>build.maven</code></li>
   * </ul>
   *
   * @return the section the property is added to.
   */
  public String getSection()
  {
    return section;
  }

  /**
   * Returns the label used for reports. If unset, the name of the property is
   * used as a label.
   *
   * @return the label used for reports.
   */
  public String getLabel()
  {
    return label;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
