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

public final class AK_JVM {
  /**
   * TODO.
   */
  public static final AttributeGroupKey JVM = new AttributeGroupKey(com.sandpolis.core.profile.store.DomainStore.get("com.sandpolis.plugin.sysinfo"), 3, 0);

  /**
   * JVM architecture.
   */
  public static final AttributeKey<String> ARCH = AttributeKey.newBuilder(JVM, 1).setDotPath("jvm.arch").setStatic(true).build();

  /**
   * JVM base directory.
   */
  public static final AttributeKey<String> PATH = AttributeKey.newBuilder(JVM, 2).setDotPath("jvm.path").setStatic(true).build();

  /**
   * The JVM start time.
   */
  public static final AttributeKey<Long> START_TIMESTAMP = AttributeKey.newBuilder(JVM, 3).setDotPath("jvm.start_timestamp").build();

  /**
   * The JVM vendor name.
   */
  public static final AttributeKey<String> VENDOR = AttributeKey.newBuilder(JVM, 4).setDotPath("jvm.vendor").setStatic(true).build();

  /**
   * The JVM version.
   */
  public static final AttributeKey<String> VERSION = AttributeKey.newBuilder(JVM, 5).setDotPath("jvm.version").build();
}
