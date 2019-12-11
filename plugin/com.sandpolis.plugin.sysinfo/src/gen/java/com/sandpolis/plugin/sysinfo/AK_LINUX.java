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

public final class AK_LINUX {
  /**
   * TODO.
   */
  public static final AttributeGroupKey LINUX = new AttributeGroupKey(com.sandpolis.core.profile.store.DomainStore.get("com.sandpolis.plugin.sysinfo"), 8, 0);

  /**
   * TODO.
   */
  public static final AttributeKey<String> DISTRIBUTION = AttributeKey.newBuilder(LINUX, 1).setDotPath("linux.distribution").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> SHELL = AttributeKey.newBuilder(LINUX, 2).setDotPath("linux.shell").build();

  /**
   * TODO.
   */
  public static final AttributeKey<String> WINDOW_MANAGER = AttributeKey.newBuilder(LINUX, 3).setDotPath("linux.window_manager").build();
}
