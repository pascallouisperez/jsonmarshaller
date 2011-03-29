package com.twolattes.json.map;

import java.util.Map;

import org.junit.Test;

import com.twolattes.json.Entity;
import com.twolattes.json.Json;
import com.twolattes.json.Json.Number;
import com.twolattes.json.TwoLattes;
import com.twolattes.json.Value;
import com.twolattes.json.types.JsonType;

public class ValidMapKeysTest {

  @Test
  public void entity1() throws Exception {
    TwoLattes.createMarshaller(Entity1.class);
  }

  @Test
  public void entity2() throws Exception {
    TwoLattes.createMarshaller(Entity2.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void entity3() throws Exception {
    TwoLattes.createMarshaller(Entity3.class);
  }

  @Test
  public void entity3WithType() throws Exception {
    TwoLattes.withType(ValueTypeJsonType.class).createMarshaller(Entity3.class);
  }

  @Test
  public void entity4() throws Exception {
    TwoLattes.createMarshaller(Entity4.class);
  }

  @Entity
  static class Entity1 {
    @Value Map<String, String> map;
  }

  @Entity
  static class Entity2 {
    @Value Map<Integer, String> map;
  }

  @Entity
  static class Entity3 {
    @Value Map<ValueType, String> map;
  }

  @Entity
  static class Entity4 {
    @Value Map<Entity1, Entity2> map;
  }

  static class ValueType {
  }

  static class ValueTypeJsonType implements JsonType<ValueType, Json.Number> {

    public Number marshall(ValueType object) {
      return null;
    }

    public ValueType unmarshall(Number object) {
      return null;
    }

  }

}
