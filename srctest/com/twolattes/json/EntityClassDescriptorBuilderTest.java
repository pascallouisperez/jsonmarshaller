package com.twolattes.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class EntityClassDescriptorBuilderTest {

  private static ConcreteEntityDescriptorBuilder builder;

  @BeforeClass
  public static void once() {
    builder = new ConcreteEntityDescriptorBuilder();
  }

  @Test
  public void testIsGetter() {
    assertTrue(builder.isGetterName("getFoo"));
    assertTrue(builder.isGetterName("getFooBar"));
    assertTrue(builder.isGetterName("isNice"));

    assertFalse(builder.isGetterName("getgoo"));
    assertFalse(builder.isGetterName("fooBar"));
    assertFalse(builder.isGetterName("isice"));
  }

}
