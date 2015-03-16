# Json.Value #

In the early version of the `JsonMarshaller`, we used an enhances version of the [org.json](http://www.json.org) library from Doug Crockford. However, this library has two major drawbacks: performance and ease of use.

We therefore decided to create `Json.Value`, a very clean implementation of a fully compliant `JSON` parser and printer. The parser uses very little memory and is 4x faster than the org.json parser. The printer uses `O(1)` memory and is 5x faster.

Let's now take a look at how to convert your code from using the [org.json](http://www.json.org) library to using `Json.Value`. In the following examples, we suppose that you have the static import

```
import static com.twolattes.json.Json.*;
```


## Creating Objects and Arrays ##

```
Json.Object o = object();
o.put(string("hello1"), string("world!"));
o.put(string("hello2"), number(5));

Json.Array a = array(FALSE, NULL);
```

vs.

```
JSONObject o = new JSONOBject();
o.put("hello1", "world!");
o.put("hello2", 5);

JSONArray a = new JSONArray();
a.put(false);
a.put(JSONObject.NULL);
```

## Reading From Streams ##

The new library `Json.Value` has a direct support for reading from streams and the parse has been optimized for that particular case.

```
Reader r = ...;
Json.Value v = read(r);
```

It also has a utility method to read from strings, by using a `StringReader` internally.

```
Json.Value v = Json.fromString("...");
```

In this case, if you are certain you are reading a `Json.Object` for instance, you can cast `v` to `Json.Object`. Otherwise, you can take advantage of the [visitor pattern](http://en.wikipedia.org/wiki/Visitor_pattern) as follows.

```
v.visit(new JsonVisitor.Empty<Integer>() {
  Integer caseArray(Json.Array array) {
    return array.size();
  }
  Integer caseObject(Json.Object object) {
    return object.size();
  }
});
```

This pattern makes it possible to have code which is conditional on the type of the value in a type-safe manner. In addition to reading nicely, it is much more efficient than using `instanceof` by making use of [double dispatch](http://en.wikipedia.org/wiki/Double_dispatch).