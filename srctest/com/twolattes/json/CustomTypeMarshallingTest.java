package com.twolattes.json;

import static com.twolattes.json.Json.array;
import static com.twolattes.json.Json.object;
import static com.twolattes.json.Json.string;
import static com.twolattes.json.TwoLattes.withType;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.twolattes.json.types.URIType;

public class CustomTypeMarshallingTest {

  Marshaller<URI> uriMarshaller =
    withType(URIType.class).createMarshaller(URI.class);

  @Test
  public void value() throws Exception {
    assertEquals(
        string("s://h"),
        uriMarshaller.marshall(new URI("s://h")));
  }

  @Test
  public void list() throws Exception {
    assertEquals(
        array(Json.NULL, string("s://h")),
        uriMarshaller.marshallList(asList(null, new URI("s://h"))));
  }

  @Test
  public void map() throws Exception {
    assertEquals(
        object(string("a"), string("s://h")),
        uriMarshaller.marshallMap(singletonMap("a", new URI("s://h"))));
  }

}
