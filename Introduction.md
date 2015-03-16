# Introduction #

Let start with some examples. Suppose your are building a bookstore and want to represent books and authors. You might have two Java classes similar to (we will discuss the annotations in a minute):

```
@Entity
class Book {
  @Value
  private String title;
  @Value
  private String isbn;
  @Value
  private Set<Author> authors;
}

@Entity
class Author {
  @Value
  private String firstName;
  @Value
  private String lastName;
}
```

and from an instance of a book, build the JSON object

```
{"title":   "Vocation Createurs",
 "isbn":    "2829302680",
 "authors": [{"firstName": "Barbara", "lastName": "Polla"},
             {"firstName": "Pascal",  "lastName": "Perez"}]}
```

or vice-versa: you have a JSON representation and wish to create Java instances automatically from it. JsonMarshaller offers exactly that.

```
Book vocationCreateurs = ...;
Marshaller<Book> m = TwoLattes.createMarshaller(Book.class);
JSONObject o = m.marshall(vocationCreateurs);
```

and

```
JSONObject o = ...;
Marshaller<Book> m = TwoLattes.createMarshaller(Book.class);
Book vocationCreateurs = m.unmarshall(o);
```

## Entities ##

Entities represent the domain model. They are objects holding data, such as `Book`, `Author`, `User`, or `Account`. On the other hand, an InputStream object for instance, represents computation.

To work with the JsonMarshaller, your entities should provide a no argument constructor. This allows the marshaller to create fresh instances and populate them.

## Annotations ##

The JsonMarshaller uses two annotations to describe entities: `@Entity` and `@Value`.

As we have seen

```
@Entity
class Book {
```

the `@Entity` annotates a class and informs the marshaller that it is a JSON entity. Again, entities should have a no argument constructor.

The second annotation

```
  @Value
  String firstName;
```

informs the marshaller that the field should be persisted to JSON. Non annotated fields are considered transient (will not be persisted).
Everything needed is automatically inferred from the bytecode of the Book class!

Please see the [options documentation](http://code.google.com/p/jsonmarshaller/wiki/Options) for more details on using these annotations.

# Built in Type Support #
`JsonMarshaller` supports all base types, their corresponding wrapper classes, String and Enum types. To (un)marshall user defined types please refer to the UserDefinedTypes tutorial.

## Enum Support ##
Because the library has built in support for enum types, no `@Entity` annotation is needed in the type definition. If we have an enum type `Abc`

```
enum Abc {
  A, B, C;
}
```

and an entity `Foo`

```
@Entity
class Foo {
  @Value
  private Abc abc = Abc.A;
}
```
This will marshall into the expected JSON representation `{"abc": "A"}` and vice-versa.

If your enum type has other fields they will simply be ignored. This is due to the fact that run-time instantiation of enum types is forbidden by the JVM.

See the [options documentation](http://code.google.com/p/jsonmarshaller/wiki/Options) for information on using the ordinal value in place of the enum constant.