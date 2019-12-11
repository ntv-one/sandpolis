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

public final class AK_USER {
  /**
   * TODO.
   */
  public static final AttributeGroupKey USER = new AttributeGroupKey(com.sandpolis.core.profile.store.DomainStore.get("com.sandpolis.plugin.sysinfo"), 4, 0);

  /**
   * TODO.
   */
  public static final AttributeKey<String> ID = AttributeKey.newBuilder(USER, 1).setDotPath("user.id").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> USERNAME = AttributeKey.newBuilder(USER, 2).setDotPath("user.username").build();

  /**
   * The user's home directory.
   */
  public static final AttributeKey<String> HOME = AttributeKey.newBuilder(USER, 3).setDotPath("user.home").build();
}
