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
package com.redhat.rcm.maven.plugin.buildmetadata.scm;

import java.util.Date;

/**
 * Interface to allow different implementations of the revision information. Our
 * application requires only the string representation.
 *
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision:591 $
 */
public interface Revision
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the identifier of the revision.
   *
   * @return the identifier of the revision.
   */
  String getId();

  /**
   * Returns the date of the revision. This is the date the revision was checked
   * in.
   *
   * @return the date of the revision.
   */
  Date getDate();

  // --- business -------------------------------------------------------------

  /**
   * Returns the revision information as a {@link String}.
   *
   * @return the string representation of the revision.
   */
  String toString();

  // --- object basics --------------------------------------------------------

}
