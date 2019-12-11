//============================================================================//
//                                                                            //
//                Copyright © 2015 - 2020 Subterranean Security               //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation at:                                //
//                                                                            //
//    https://mozilla.org/MPL/2.0                                             //
//                                                                            //
//=========================================================S A N D P O L I S==//
package com.sandpolis.plugin.sysinfo;

import com.sandpolis.core.attribute.AttributeGroupKey;
import com.sandpolis.core.attribute.AttributeKey;

public final class AK_LOCATION {
  /**
   * TODO.
   */
  public static final AttributeGroupKey LOCATION = new AttributeGroupKey(com.sandpolis.core.profile.store.DomainStore.get("com.sandpolis.plugin.sysinfo"), 5, 0);

  /**
   * TODO.
   */
  public static final AttributeKey<String> CITY = AttributeKey.newBuilder(LOCATION, 1).setDotPath("location.city").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> COUNTRY = AttributeKey.newBuilder(LOCATION, 2).setDotPath("location.country").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> COUNTRY_CODE = AttributeKey.newBuilder(LOCATION, 3).setDotPath("location.country_code").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> LATITUDE = AttributeKey.newBuilder(LOCATION, 4).setDotPath("location.latitude").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> LONGITUDE = AttributeKey.newBuilder(LOCATION, 5).setDotPath("location.longitude").build();
}
