package com.twolattes.json;


interface FieldDescriptor {

  void marshall(Object entity, String view, Json.Object jsonObject);

  void unmarshall(Object entity, String view, Json.Object jsonObject);

  /**
   * Gets the field's descriptor.
   */
  Descriptor<?, ?> getDescriptor();

  /**
   * Gets the field's Java name.
   */
  Json.String getFieldName();

  /**
   * Gets the field's JSON name.
   */
  Json.String getJsonName();

  /**
   * Gets the described field's value.
   */
  Object getFieldValue(Object entity);

  byte getFieldValueByte(Object entity);

  char getFieldValueChar(Object entity);

  boolean getFieldValueBoolean(Object entity);

  short getFieldValueShort(Object entity);

  int getFieldValueInt(Object entity);

  long getFieldValueLong(Object entity);

  float getFieldValueFloat(Object entity);

  double getFieldValueDouble(Object entity);

  /**
   * Sets the described field's value.
   */
  void setFieldValue(Object entity, Object value);

  void setFieldValueByte(Object entity, byte value);

  void setFieldValueChar(Object entity, char value);

  void setFieldValueBoolean(Object entity, boolean value);

  void setFieldValueShort(Object entity, short value);

  void setFieldValueInt(Object entity, int value);

  void setFieldValueLong(Object entity, long value);

  void setFieldValueFloat(Object entity, float value);

  void setFieldValueDouble(Object entity, double value);

  /**
   * Whether or not to use the {@link Enum#ordinal()} value to represent enum
   * constants if the field is of type {@link Enum}.
   *
   * @return returns {@code true} if the ordinal representation should be used,
   *         {@code false} otherwise.
   */
  boolean useOrdinal();

  /**
   * Tests whether the field is in a specific view.
   */
  boolean isInView(String view);

  /**
   * Pretty prints the descriptor.
   */
  String toString(int pad);

}
