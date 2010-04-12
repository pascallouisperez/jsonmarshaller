package com.twolattes.json.types;

import com.twolattes.json.Json;

/**
 * JSON type to extend basic marshalling.
 */
public interface JsonType<T, J extends Json.Value> {

  /**
   * Handles the marshalling of an object.
   * @param object the object to marshall (never <tt>null</tt>)
   */
  public J marshall(T object);

  /**
   * Handles the unmarshalling of an object.
   * @param object the object to unmarshall (never <tt>null</tt>)
   */
  public T unmarshall(J object);

}
