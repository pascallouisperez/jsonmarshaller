package com.twolattes.json.bugreports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.twolattes.json.Entity;
import com.twolattes.json.TwoLattes;
import com.twolattes.json.Value;

public class MissingNoArgumentConstructorTest {

  @Test
  public void weNeedToWarnAboutMissingNoArgConstructor1() {
    verify(EntityWithoutNoArgConstructor.class);
  }

  @Entity
  static class EntityWithoutNoArgConstructor
      implements IfaceEntityWithoutNoArgConstructor {
    public EntityWithoutNoArgConstructor(int value) {
    }
  }

  @Test
  public void weNeedToWarnAboutMissingNoArgConstructor2() {
    verify(EntityWithValuePointingToEntityWithoutNoArgConstructor.class);
  }

  @Entity
  static class EntityWithValuePointingToEntityWithoutNoArgConstructor
      implements IfaceEntityWithValuePointingToEntityWithoutNoArgConstructor {
    @Value EntityWithoutNoArgConstructor value;
  }

  @Test
  public void weNeedToWarnAboutMissingNoArgConstructor3() {
    verify(IfaceEntityWithoutNoArgConstructor.class);
  }

  @Entity(implementedBy = EntityWithoutNoArgConstructor.class)
  interface IfaceEntityWithoutNoArgConstructor {
  }

  @Test
  public void weNeedToWarnAboutMissingNoArgConstructor4() {
    verify(IfaceEntityWithoutNoArgConstructor.class);
  }

  @Entity(implementedBy = EntityWithValuePointingToEntityWithoutNoArgConstructor.class)
  interface IfaceEntityWithValuePointingToEntityWithoutNoArgConstructor {
  }

  private void verify(Class<?> entityClass) {
    try {
      TwoLattes.createEntityMarshaller(entityClass);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals(
          "class com.twolattes.json.bugreports.MissingNoArgumentConstructorTe" +
          "st$EntityWithoutNoArgConstructor does not have a no argument const" +
          "ructor",
          e.getMessage());
    }
  }

}
