# Options #

## `name` option ##

When an entitiy is marshalled, the Java field name is used. To override this default behaviour, you can use the name option

```
  @Value(name = "fname")
  String firstName;
```

This would be marshalled to

```
{..., "fname": "Pascal", ...}
```

instead of

```
{..., "firstName": "Pascal", ...}
```

## `optional` option ##

The optional option indicates that a value is optional. When unmarshalling an entity, if the value is not found no exception will be thrown. This allows to define defaults to certain properties of an entity that are overridden only if a value is specified. For instance

```
@Entity
class Email {
  @Value(optional = true)
  private String email = "support@mydomain.com";
}
```

## `inline` option ##

The inline options allows you to inline an entity into another. Suppose you have a `User` entity with an email field represented by an `Email` entity

```
@Entity
class User {
  @Value(inline = true)
  private Email email;
}

@Entity
class Email {
  @Value
  private String email;
}
```

Specifying `inline = true` informs the marshaller that inlining should occur. The JSON object produced would then be

```
{..., "email": "hello@world.com", ...}
```

Without inlining, we would have

```
{..., "email": {"email": "hello@world.com"}, ...}
```

Not that an entity can be inlined only if it has only one value field.

You might also want to specify that an entity should always be inlined. For this put the `inline = true` option on the `@Entity` annotation

```
@Entity(inline = true)
class Email {
  @Value
  private String email;
}
```

In addition to locality, specifying the inlining at the entity level ensures it will be inlined even if it is in a List, Map or Set. For instance, the following class

```
@Entity
class User {
  @Value
  private Set<Email> email;
}

@Entity(inline = true)
class Email {
  @Value
  private String email;
}
```

would be marshalled to

```
{"email": ["foo@bar.edu", "hello@world.com", ...]}
```

## `type` option ##

The type option allows you to specify a user defined type for a value. Suppose your entity contains a field which is not a JSON entity

```
@Entity
class Address {
  @Value
  private String name;
  @Value(type = com.twolattes.json.types.URLType.class)
  private URL url;
}
```

here the `URL` is a value yet we cannot marshall and unmarshall it without additional information wich the `URLType` provides. We discuss the creation of user defined types in UserDefinedTypes.

## `views` option ##

Do you happen to have complex entities which need to be marshalled once with a set of fields, and in another situation with other fields? Views allow you to specify different ways to marshall entities. The views option takes a String array as parameter which are the views in which a field ought to be included. Consider the `Address` class.

```
@Entity
class Address {
  @Value(views = {"full", "simple"}
  private String name;
  @Value(views = {"full"},
         type = com.twolattes.json.types.URLType.class)
  private URL url;
}
```

The name field will be marshalled in the full and simple views, whereas the url field will only be marshalled in the full view.

To specify the view of an entity to take when marshalling or unmarshalling, please look out the updated interface of the `Marshaller`.

## `ordinal` option ##

This flag is for fields of type enum. The `ordinal = true` flag may be used in the `@Value` annotation to indicate that the integer ordinal value of the enum constant should be used in the JSON representation. e.g.

```

enum Abc { A, B, C }

@Entity
class Foo {
  @Value(ordianal = true)
  private Abc abc = Abc.A;
  ...
}
```

will marshall to `{"abc":0}`. This gives a more compact JSON object but is vulnerable to constant reordering in the enum type definition.