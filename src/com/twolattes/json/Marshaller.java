package com.twolattes.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.twolattes.json.Json.Value;

/**
 * A thread-safe <a href="http://www.json.org/">JSON</a> marshaller.
 *
 * @param <T> the type that this marshaller marshalls and unmarshalls
 */
public interface Marshaller<T> {

  /**
   * Marshalls a {@code T} to its JSON representation.
   * @param object an object to marshall
   * @return a JSON value
   */
  Json.Value marshall(T object);

  /**
   * Marshalls a particular view of a {@code T} to its JSON representation.
   * @param object an object to marshall
   * @param view a view name (see {@link Value#views}) or {@code null}
   * @return a JSON value
   */
  Json.Value marshall(T object, String view);

  /**
   * Marshalls a collection of {@code T} to its JSON array representation.
   * Preserves ordering.
   * @param objects the objects to marshall
   * @return a JSON array
   */
  Json.Array marshallList(Collection<? extends T> objects);

  /**
   * Marshalls a collection of {@code T} to its JSON array representation.
   * Preserves ordering.
   * @param objects the objects to marshall
   * @param view a view name (see {@link Value#views}) or {@code null}
   * @return a JSON array
   */
  Json.Array marshallList(Collection<? extends T> objects, String view);

  /**
   * Marshalls a string-keyed map of {@code T} to its JSON object representation.
   * @param map the map to marshall. Values may be {@code null} but keys may not.
   * @return a JSON object
   */
  Json.Object marshallMap(Map<String, ? extends T> map);

  /**
   * Marshalls a string-keyed map of {@code T} to its JSON object representation.
   * @param map the map to marshall. Values may be {@code null} but keys may not.
   * @param view a view name (see {@link Value#views}) or {@code null}
   * @return a JSON object
   */
  Json.Object marshallMap(Map<String, ? extends T> map, String view);

  /**
   * Marshalls a map of {@code T} to its JSON object representation. Keys are
   * marshalled using the specified marshaller and wrapped into JSON strings if
   * necessary.
   * @param map the map to marshall. Values may be {@code null} but keys may not.
   * @param keyMarshaller the marshaller used to marshall the keys.
   * @return a JSON object
   */
  <K> Json.Object marshallMap(Map<K, ? extends T> map, Marshaller<K> keyMarshaller);

  /**
   * Unmarshalls the JSON representation of a {@code T}.
   * @param value a JSON value
   * @return the unmarshalled object
   */
  T unmarshall(Json.Value value);

  /**
   * Unmarshalls the JSON representation of a particular view of a {@code T}.
   * @param value a JSON value
   * @param view a view name (see {@link Value#views}) or {@code null}
   * @return the unmarshalled object
   */
  T unmarshall(Json.Value value, String view);

  /**
   * Unmarshalls a JSON array representation of a list of {@code T}. Preserves
   * ordering.
   * @param array a JSON array of entities
   * @return the unmarshalled list
   */
  List<T> unmarshallList(Json.Array array);

  /**
   * Unmarshalls a JSON array representation of a list of {@code T}. Preserves
   * ordering.
   * @param array a JSON array of entities
   * @param view a view name (see {@link Value#views}) or {@code null}
   * @return the unmarshalled list
   */
  List<T> unmarshallList(Json.Array array, String view);

  /**
   * Unmarshalls a JSON object representation of a string-keyed map of {@code T}.
   * @param object a JSON object
   * @return the unmarshalled map
   */
  Map<String, T> unmarshallMap(Json.Object object);

  /**
   * Unmarshalls a JSON object representation of a map of {@code T}. Keys are
   * unmarshalled using the specified marshaller and unwrapped from JSON strings if
   * necessary.
   * @param object a JSON object
   * @param keyMarshaller the marshaller used to unmarshall the keys.
   * @return the unmarshalled map
   */
  <K> Map<K, T> unmarshallMap(Json.Object object, Marshaller<K> keyMarshaller);

  /**
   * Unmarshalls a JSON object representation of a string-keyed map of {@code T}.
   * @param object a JSON object
   * @param view a view name (see {@link Value#views}) or {@code null}
   * @return the unmarshalled map
   */
  Map<String, T> unmarshallMap(Json.Object object, String view);

  /**
   * Unmarshalls a JSON array representation of a list of {@code T}s and calls
   * {@code Generator#yield(T)} after parsing each element.
   */
  void unmarshallStream(Reader reader, Generator<? super T> generator)
      throws IOException;

  /**
   * Function invoked inside loops to yield {@code T}s to the caller.
   */
  public static interface Generator<T> {

    /**
     * Yield a {@code T}.
     */
    void yield(T entity);

  }

}
