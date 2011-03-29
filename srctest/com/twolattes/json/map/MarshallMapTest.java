package com.twolattes.json.map;

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.twolattes.json.Entity;
import com.twolattes.json.Json;
import com.twolattes.json.Marshaller;
import com.twolattes.json.TwoLattes;
import com.twolattes.json.Value;

public class MarshallMapTest {

  @Test
  public void marshallMapOfStrings() throws Exception {
    Marshaller<String> marshaller = TwoLattes.createMarshaller(String.class);
    Map<String, String> map = new HashMap<String, String>();
    map.put("a", "b");
    map.put("c", "d");
    assertEquals(
        Json.object(
            Json.string("a"), Json.string("b"),
            Json.string("c"), Json.string("d")),
        marshaller.marshallMap(map, TwoLattes.createMarshaller(String.class)));
  }

  @Test
  public void unmarshallMapOfStrings() throws Exception {
    Marshaller<String> marshaller = TwoLattes.createMarshaller(String.class);

    Map<String, String> actual = marshaller.unmarshallMap(Json.object(
        Json.string("a"), Json.string("b"),
        Json.string("c"), Json.string("d")),
        TwoLattes.createMarshaller(String.class));

    Map<String, String> expected = new HashMap<String, String>();
    expected.put("a", "b");
    expected.put("c", "d");
    assertEquals(expected, actual);
  }

  @Test
  public void marshallMapOfIntegers() throws Exception {
    Marshaller<String> marshaller = TwoLattes.createMarshaller(String.class);
    Map<Integer, String> map = new HashMap<Integer, String>();
    map.put(1, "b");
    map.put(2, "d");
    assertEquals(
        Json.object(
            Json.string("1"), Json.string("b"),
            Json.string("2"), Json.string("d")),
        marshaller.marshallMap(map, TwoLattes.createMarshaller(Integer.class)));
  }

  @Test
  public void unmarshallMapOfIntegers() throws Exception {
    Marshaller<String> marshaller = TwoLattes.createMarshaller(String.class);

    Map<Integer, String> actual = marshaller.unmarshallMap(Json.object(
        Json.string("1"), Json.string("b"),
        Json.string("2"), Json.string("d")),
        TwoLattes.createMarshaller(Integer.class));

    Map<Integer, String> expected = new HashMap<Integer, String>();
    expected.put(1, "b");
    expected.put(2, "d");
    assertEquals(expected, actual);
  }

  @Test
  public void marshallMapOfIntegersWithEntityMarshaller() throws Exception {
    Marshaller<Foo> marshaller = TwoLattes.createMarshaller(Foo.class);
    Map<Integer, Foo> map = new HashMap<Integer, Foo>();
    map.put(1, new Foo(10));
    map.put(2, new Foo(11));
    assertEquals(
        Json.object(
            Json.string("1"), Json.object(Json.string("value"), Json.number(10)),
            Json.string("2"), Json.object(Json.string("value"), Json.number(11))),
        marshaller.marshallMap(map, TwoLattes.createMarshaller(Integer.class)));
  }

  @Test
  public void unmarshallMapOfIntegersWithEntityMarshaller() throws Exception {
    Marshaller<Foo> marshaller = TwoLattes.createMarshaller(Foo.class);

    Map<Integer, Foo> actual = marshaller.unmarshallMap(Json.object(
        Json.string("1"), Json.object(Json.string("value"), Json.number(10)),
        Json.string("2"), Json.object(Json.string("value"), Json.number(11))),
        TwoLattes.createMarshaller(Integer.class));

    Map<Integer, Foo> expected = new HashMap<Integer, Foo>();
    expected.put(1, new Foo(10));
    expected.put(2, new Foo(11));
    assertEquals(expected, actual);
  }

  @Test
  public void marshallMapOfEntities() throws Exception {
    Marshaller<Foo> marshaller = TwoLattes.createMarshaller(Foo.class);
    Map<Foo, Foo> map = new HashMap<Foo, Foo>();
    map.put(new Foo(1), new Foo(10));
    map.put(new Foo(2), new Foo(11));
    assertEquals(
        Json.object(
            Json.string("{\"value\":1}"), Json.object(Json.string("value"), Json.number(10)),
            Json.string("{\"value\":2}"), Json.object(Json.string("value"), Json.number(11))),
        marshaller.marshallMap(map, TwoLattes.createMarshaller(Foo.class)));
  }

  @Test
  public void unmarshallMapOfEntities() throws Exception {
    Marshaller<Foo> marshaller = TwoLattes.createMarshaller(Foo.class);

    Map<Foo, Foo> actual = marshaller.unmarshallMap(Json.object(
        Json.string("{\"value\":1}"), Json.object(Json.string("value"), Json.number(10)),
        Json.string("{\"value\":2}"), Json.object(Json.string("value"), Json.number(11))),
        TwoLattes.createMarshaller(Foo.class));

    Map<Foo, Foo> expected = new HashMap<Foo, Foo>();
    expected.put(new Foo(1), new Foo(10));
    expected.put(new Foo(2), new Foo(11));
    assertEquals(expected, actual);
  }

  @Entity
  static class Foo {

    @Value
    int value;

    Foo() {
    }

    Foo(int value) {
      this.value = value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + value;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Foo other = (Foo) obj;
      if (value != other.value) {
        return false;
      }
      return true;
    }

  }

}
