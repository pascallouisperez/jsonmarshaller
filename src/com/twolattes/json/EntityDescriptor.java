package com.twolattes.json;

import java.util.Set;

/**
 * Entity descriptor describing entities which are instances of {@code T}.
 */
interface EntityDescriptor<T> extends Descriptor<T, Json.Value> {

  /**
   * Gets the set of field descriptors describing fields of this entity.
   */
  Set<FieldDescriptor> getFieldDescriptors();

  Set<FieldDescriptor> getAllFieldDescriptors();

  /**
   * Gets this entity's discriminator. This is an optional operation which
   * should be defined for entities mentioned in an {@link Entity#subclasses()}
   * list.
   */
  String getDiscriminator();

}
