package com.twolattes.json;

import static com.twolattes.json.Json.array;
import static com.twolattes.json.Json.number;
import static com.twolattes.json.Json.object;
import static com.twolattes.json.Json.string;
import static com.twolattes.json.TwoLattes.withType;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.twolattes.json.types.URIType;

public class CustomTypeUnmarshallingTest {

  Marshaller<URI> uriMarshaller =
    withType(URIType.class).createMarshaller(URI.class);

  @Test
  public void value() throws Exception {
    assertEquals(
        new URI("s://h"),
        uriMarshaller.unmarshall(string("s://h")));
  }

  @Test
  public void list() throws Exception {
    assertEquals(
        asList(null, new URI("s://h")),
        uriMarshaller.unmarshallList(array(Json.NULL, string("s://h"))));
  }

  @Test
  public void map() throws Exception {
    assertEquals(
        singletonMap("a", new URI("s://h")),
        uriMarshaller.unmarshallMap(object(string("a"), string("s://h"))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void numberAsUri() {
    uriMarshaller.unmarshall(number(1));
  }

}
