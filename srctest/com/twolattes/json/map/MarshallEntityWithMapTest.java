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
import com.twolattes.json.types.JsonType;

public class MarshallEntityWithMapTest {

  @Test
  public void marshallFoo() throws Exception {
    Foo foo = new Foo();
    foo.map.put(1, "a");
    foo.map.put(2, "b");

    Marshaller<Foo> marshaller = TwoLattes.createMarshaller(Foo.class);
    assertEquals(
        Json.object(
            Json.string("map"), Json.object(
                Json.string("1"), Json.string("a"),
                Json.string("2"), Json.string("b"))),
        marshaller.marshall(foo));
  }

  @Test
  public void unmarshallFoo() throws Exception {
    Marshaller<Foo> marshaller = TwoLattes.createMarshaller(Foo.class);
    Foo foo = marshaller.unmarshall(Json.object(
        Json.string("map"), Json.object(
            Json.string("1"), Json.string("a"),
            Json.string("2"), Json.string("b"))));

    Map<Integer, String> expected = new HashMap<Integer, String>();
    expected.put(1, "a");
    expected.put(2, "b");
    assertEquals(expected, foo.map);
  }

  @Test
  public void marshallBarWithString() throws Exception {
    BarWithString bar = new BarWithString();
    bar.map.put(new BazString("hello"), "a");
    bar.map.put(new BazString("world"), "b");

    Marshaller<BarWithString> marshaller =
      TwoLattes.withType(BazStringJsonType.class).createMarshaller(BarWithString.class);
    assertEquals(
        Json.object(
            Json.string("map"), Json.object(
                Json.string("hello"), Json.string("a"),
                Json.string("world"), Json.string("b"))),
        marshaller.marshall(bar));
  }

  @Test
  public void unmarshallBarWithString() throws Exception {
    Marshaller<BarWithString> marshaller =
      TwoLattes.withType(BazStringJsonType.class).createMarshaller(BarWithString.class);
    BarWithString bar = marshaller.unmarshall(Json.object(
        Json.string("map"), Json.object(
            Json.string("hello"), Json.string("a"),
            Json.string("world"), Json.string("b"))));

    Map<BazString, String> expected = new HashMap<BazString, String>();
    expected.put(new BazString("hello"), "a");
    expected.put(new BazString("world"), "b");
    assertEquals(expected, bar.map);
  }

  @Test
  public void marshallBarWithNumber() throws Exception {
    BarWithNumber bar = new BarWithNumber();
    bar.map.put(new BazNumber(1), "a");
    bar.map.put(new BazNumber(2), "b");

    Marshaller<BarWithNumber> marshaller =
      TwoLattes.withType(BazNumberJsonType.class).createMarshaller(BarWithNumber.class);
    assertEquals(
        Json.object(
            Json.string("map"), Json.object(
                Json.string("1"), Json.string("a"),
                Json.string("2"), Json.string("b"))),
        marshaller.marshall(bar));
  }

  @Test
  public void unmarshallBarWithNumber() throws Exception {
    Marshaller<BarWithNumber> marshaller =
      TwoLattes.withType(BazNumberJsonType.class).createMarshaller(BarWithNumber.class);
    BarWithNumber bar = marshaller.unmarshall(Json.object(
        Json.string("map"), Json.object(
            Json.string("1"), Json.string("a"),
            Json.string("2"), Json.string("b"))));

    Map<BazNumber, String> expected = new HashMap<BazNumber, String>();
    expected.put(new BazNumber(1), "a");
    expected.put(new BazNumber(2), "b");
    assertEquals(expected, bar.map);
  }

  @Entity
  static class Foo {

    @Value
    Map<Integer, String> map = new HashMap<Integer, String>();

  }

  @Entity
  static class BarWithString {

    @Value
    Map<BazString, String> map = new HashMap<BazString, String>();

  }

  @Entity
  static class BarWithNumber {

    @Value
    Map<BazNumber, String> map = new HashMap<BazNumber, String>();

  }

  static class BazString {

    String s;

    BazString(String number) {
      this.s = number;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + s.hashCode();
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
      BazString other = (BazString) obj;
      if (!s.equals(other.s)) {
        return false;
      }
      return true;
    }

  }

  static class BazNumber {

    int i;

    BazNumber(int i) {
      this.i = i;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + i;
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
      BazNumber other = (BazNumber) obj;
      if (i != other.i) {
        return false;
      }
      return true;
    }

  }

  static class BazStringJsonType implements JsonType<BazString, Json.String> {

    public Json.String marshall(BazString object) {
      return Json.string(object.s);
    }

    public BazString unmarshall(Json.String object) {
      return new BazString(object.getString());
    }

  }

  static class BazNumberJsonType implements JsonType<BazNumber, Json.Number> {

    public Json.Number marshall(BazNumber object) {
      return Json.number(object.i);
    }

    public BazNumber unmarshall(Json.Number object) {
      return new BazNumber(object.getNumber().intValue());
    }

  }

}
