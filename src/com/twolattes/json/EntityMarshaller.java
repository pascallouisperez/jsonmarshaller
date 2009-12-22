package com.twolattes.json;

/**
 * A refinement of {@link Marshaller} for {@link Entity @Entity} types.
 * Callers that wish to avoid casting {@code marshall} return values from
 * {@link Json.Value} to the more specific {@link Json.Object} can use this
 * interface instead of {@code Marshaller}.
 *
 * @param <T> the {@code @Entity} type that this marshaller marshalls and
 *   unmarshalls
 */
public interface EntityMarshaller<T> extends Marshaller<T> {

  Json.Object marshall(T object);

  Json.Object marshall(T object, String view);

}
