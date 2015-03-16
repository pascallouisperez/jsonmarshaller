# User Defined Types #

A user defined type must implement the interface com.twolattes.json.types.Type

```
public interface Type<T> {
  public Class<T> getReturnedClass();
  public Object marshall(T entity);
  public T unmarshall(Object object);
}
```

The `getReturnedClass` return the `Class` object which represents the type `T`. The marshall and unmarshall methods take care of marshalling and unmarshalling.

To write your own, look at the `URLType` as an example.

You can also check out [Of this and that!](http://adityagore.blogspot.com/2007/10/user-defined-types-with-jsonmarshaller.html) blog for an more detailed explanation.