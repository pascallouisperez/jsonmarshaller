package com.twolattes.json.bugreports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.twolattes.json.Entity;
import com.twolattes.json.TwoLattes;
import com.twolattes.json.Value;

public class DisallowValueOnNonEntitiesTest {

  @Test
  public void disallowValueOnNonEntities() {
    try {
      TwoLattes.createEntityMarshaller(A.class);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals(
          "cannot have @Value on non-entity class com.twolattes.json.bugrepor" +
          "ts.DisallowValueOnNonEntitiesTest$B",
          e.getMessage());
    }
  }

  static class B { @Value int value; }
  @Entity static class A extends B {}

}
