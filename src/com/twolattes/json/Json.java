package com.twolattes.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * JSON values.
 *
 * @see http://www.json.org
 */
public final class Json {

  /** A JSON value.
   */
  public interface Value {

    void write(Writer writer) throws IOException;

    <T> T visit(JsonVisitor<T> visitor);

    /**
     * Returns a JSON representation of this value.
     * @return a JSON representation of this value
     */
    java.lang.String toString();

  }

  /**
   * An object, i.e. {@code {"hello":"world"}}.
   */
  public interface Object extends Value {

    Json.Value put(Json.String key, Json.Value value);

    Json.Value get(Json.String key);

    boolean containsKey(Json.String key);

    Set<Json.String> keySet();

    Set<Map.Entry<Json.String, Json.Value>> entrySet();

    Collection<Json.Value> values();

    boolean isEmpty();

    int size();

  }

  /**
   * An array, i.e {@code [0, 1, 2, 3, 4, 5]}.
   */
  public interface Array extends Value, Iterable<Value> {

    void add(int index, Json.Value element);

    boolean add(Json.Value element);

    Json.Value get(int index);

    boolean isEmpty();

    int size();

    List<Json.Value> values();

  }

  /**
   * A string, i.e. {@code "hello"}.
   */
  public interface String extends Value, Comparable<Json.String> {

    java.lang.String getString();

    boolean isEmpty();

  }

  /**
   * A number, i.e. {@code 5}, {@code 78.90} or {@code 12728971932}.
   */
  public interface Number extends Value {

    BigDecimal getNumber();

  }

  /**
   * A boolean, i.e {@code true} or {@code false}.
   */
  public interface Boolean extends Value {

    boolean getBoolean();

  }

  /**
   * Null, i.e. {@code null}.
   */
  public interface Null extends Json.Array, Json.Boolean, Json.Number, Json.Object, Json.String {
  }

  private static abstract class BaseValue implements Json.Value {

    @Override
    public final java.lang.String toString() {
      StringWriter writer = new StringWriter();
      try {
        write(writer);
      } catch (IOException e) {
        // unreachable
        throw new RuntimeException(e);
      }
      return writer.toString();
    }

  }

  private static class ObjectImpl extends BaseValue implements Json.Object {

    private final Map<Json.String, Json.Value> delegate = new TreeMap<Json.String, Json.Value>();

    public void write(Writer writer) throws IOException {
      writer.append('{');
      java.lang.String separator = "";
      for (Json.String key : delegate.keySet()) {
        writer.append(separator);
        separator = ",";
        key.write(writer);
        writer.append(':');
        delegate.get(key).write(writer);
      }
      writer.append('}');
    }

    public <T> T visit(JsonVisitor<T> visitor) {
      return visitor.caseObject(this);
    }

    @Override
    public boolean equals(java.lang.Object o) {
      if (!(o instanceof Json.Object)) {
        return false;
      } else if (NULL.equals(o)) {
        return false;
      } else {
        return delegate.entrySet().equals(((Json.Object) o).entrySet());
      }
    }

    public Json.Value get(Json.String key) {
      return delegate.get(key);
    }

    public boolean containsKey(String key) {
      return delegate.containsKey(key);
    }

    @Override
    public int hashCode() {
      return delegate.hashCode();
    }

    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    public Set<Json.String> keySet() {
      return delegate.keySet();
    }

    public Set<Entry<String, Value>> entrySet() {
      return delegate.entrySet();
    }

    public Json.Value put(Json.String key, Json.Value value) {
      return delegate.put(key, value);
    }

    public int size() {
      return delegate.size();
    }

    public Collection<Json.Value> values() {
      return delegate.values();
    }

  }

  private static class ArrayImpl extends BaseValue implements Json.Array {

    private final List<Json.Value> delegate = new LinkedList<Json.Value>();

    public ArrayImpl(Json.Value... values) {
      for (Json.Value value : values) {
        delegate.add(value);
      }
    }

    public ArrayImpl(Iterable<Json.Value> values) {
      for (Json.Value value : values) {
        delegate.add(value);
      }
    }

    public ArrayImpl(Json.Array value) {
      delegate.add(value);
    }

    public void write(Writer writer) throws IOException {
      writer.append('[');
      java.lang.String separator = "";
      for (Json.Value value : delegate) {
        writer.append(separator);
        separator = ",";
        value.write(writer);
      }
      writer.append(']');
    }

    public <T> T visit(JsonVisitor<T> visitor) {
      return visitor.caseArray(this);
    }

    public List<Value> values() {
      return delegate;
    }

    public void add(int index, Json.Value element) {
      delegate.add(index, element);
    }

    public boolean add(Json.Value e) {
      return delegate.add(e);
    }

    @Override
    public boolean equals(java.lang.Object o) {
      if (!(o instanceof Json.Array)) {
        return false;
      } else if (NULL.equals(o)) {
        return false;
      } else {
        return delegate.equals(((Json.Array) o).values());
      }
    }

    public Json.Value get(int index) {
      return delegate.get(index);
    }

    @Override
    public int hashCode() {
      return delegate.hashCode();
    }

    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    public int size() {
      return delegate.size();
    }

    public Iterator<Value> iterator() {
      return delegate.iterator();
    }

  }

  private static class BooleanImpl extends BaseValue implements Json.Boolean {

    private final boolean b;

    public BooleanImpl(boolean b) {
      this.b = b;
    }

    public void write(Writer writer) throws IOException {
      writer.append(java.lang.Boolean.toString(b));
    }

    public <T> T visit(JsonVisitor<T> visitor) {
      return visitor.caseBoolean(this);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
      if (!(obj instanceof BooleanImpl)) {
        return false;
      } else {
        return !(this.b ^ ((BooleanImpl) obj).b);
      }
    }

    @Override
    public int hashCode() {
      if (b) {
        return 982451653;
      } else {
        return 941083987;
      }
    }

    public boolean getBoolean() {
      return b;
    }

  }

  private static abstract class NumberImpl extends BaseValue implements Json.Number {

    public <T> T visit(JsonVisitor<T> visitor) {
      return visitor.caseNumber(this);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof Json.Number)) {
        return false;
      }
      if (obj.equals(NULL)) {
        return false;
      }
      return this.getNumber().compareTo(((Json.Number) obj).getNumber()) == 0;
    }

    @Override
    public int hashCode() {
      return (int) getNumber().doubleValue();
    }

  }

  private static class NumberImplShort extends NumberImpl {

    private final short number;

    public NumberImplShort(short number) {
      this.number = number;
    }

    public void write(Writer writer) throws IOException {
      writer.append(Short.toString(number));
    }


    public BigDecimal getNumber() {
      return BigDecimal.valueOf(number);
    }

  }

  private static class NumberImplInt extends NumberImpl {

    private final int number;

    public NumberImplInt(int number) {
      this.number = number;
    }

    public void write(Writer writer) throws IOException {
      writer.append(Integer.toString(number));
    }


    public BigDecimal getNumber() {
      return BigDecimal.valueOf(number);
    }

  }

  private static class NumberImplLong extends NumberImpl {

    private final long number;

    public NumberImplLong(long number) {
      this.number = number;
    }

    public void write(Writer writer) throws IOException {
      writer.append(Long.toString(number));
    }


    public BigDecimal getNumber() {
      return BigDecimal.valueOf(number);
    }

  }

  private static class NumberImplFloat extends NumberImpl {

    private final float number;

    public NumberImplFloat(float number) {
      this.number = number;
    }

    public void write(Writer writer) throws IOException {
      writer.append(Float.toString(number));
    }


    public BigDecimal getNumber() {
      return new BigDecimal(Float.toString(number));
    }

  }

  private static class NumberImplDouble extends NumberImpl {

    private final double number;

    public NumberImplDouble(double number) {
      this.number = number;
    }

    public void write(Writer writer) throws IOException {
      writer.append(Double.toString(number));
    }


    public BigDecimal getNumber() {
      return BigDecimal.valueOf(number);
    }

  }

  private static class NumberImplBigDecimal extends NumberImpl {

    private final BigDecimal number;

    public NumberImplBigDecimal(BigDecimal number) {
      this.number = number;
    }

    public void write(Writer writer) throws IOException {
      writer.append(number.toPlainString());
    }


    public BigDecimal getNumber() {
      return number;
    }

  }

  private static class StringImpl extends BaseValue implements Json.String {

    private final java.lang.String string;

    public StringImpl(java.lang.String string) {
      this.string = string;
    }

    public void write(Writer writer) throws IOException {
      StringBuilder builder = new StringBuilder(string.length() << 1);
      builder.append('"');
      int length = string.length();
      char c;
      boolean safe = true;
      for (int i = 0; safe && i < length; i++) {
        c = string.charAt(i);
        safe = c != '"' && c != '\\' && c >= 0x20 && (c < 0x7f || c >= 0xa0);
      }
      if (safe) {
        builder.append(string);
      } else {
        for (int i = 0; i < length; i++) {
          switch (c = string.charAt(i)) {
            case '\b': builder.append("\\b"); break;
            case '\n': builder.append("\\n"); break;
            case '\f': builder.append("\\f"); break;
            case '\r': builder.append("\\r"); break;
            case '\t': builder.append("\\t"); break;
            case '"': builder.append("\\\""); break;
            case '\\': builder.append("\\\\"); break;
            default:
              if (c < 0x20 || (c >= 0x7f && c < 0xa0)) {
                builder.append(java.lang.String.format("\\u%04x", Integer.valueOf(c)));
              } else {
                builder.append(c);
              }
              break;
          }
        }
      }
      builder.append('"');
      writer.append(builder.toString());
    }

    public <T> T visit(JsonVisitor<T> visitor) {
      return visitor.caseString(this);
    }

    public int compareTo(Json.String that) {
      return this.string.compareTo(that.getString());
    }

    @Override
    public boolean equals(java.lang.Object obj) {
      if (!(obj instanceof Json.String)) {
        return false;
      } else if (NULL.equals(obj)) {
        return false;
      } else {
        return this.getString().equals(((Json.String) obj).getString());
      }
    }

    @Override
    public int hashCode() {
      return string.hashCode();
    }

    public java.lang.String getString() {
      return string;
    }

    public boolean isEmpty() {
      return string.length() == 0; // 1.5 compatible
    }

  }

  private static class NullImpl extends BaseValue implements Json.Null {

    public void write(Writer writer) throws IOException {
      writer.append("null");
    }

    public <T> T visit(JsonVisitor<T> visitor) {
      return visitor.caseNull();
    }

    @Override
    public boolean equals(java.lang.Object obj) {
      return obj instanceof Json.NullImpl;
    }

    @Override
    public int hashCode() {
      return 900772187;
    }

    public boolean getBoolean() {
      throw new NullPointerException();
    }

    public BigDecimal getNumber() {
      throw new NullPointerException();
    }

    public java.lang.String getString() {
      throw new NullPointerException();
    }

    public int compareTo(Json.String o) {
      throw new NullPointerException();
    }

    public Value get(String key) {
      throw new NullPointerException();
    }

    public boolean containsKey(String key) {
      throw new NullPointerException();
    }

    public boolean isEmpty() {
      throw new NullPointerException();
    }

    public Set<String> keySet() {
      throw new NullPointerException();
    }

    public Set<Entry<String, Value>> entrySet() {
      throw new NullPointerException();
    }

    public Value put(String key, Value value) {
      throw new NullPointerException();
    }

    public int size() {
      throw new NullPointerException();
    }

    public List<Value> values() {
      throw new NullPointerException();
    }

    public void add(int index, Value element) {
      throw new NullPointerException();
    }

    public boolean add(Value element) {
      throw new NullPointerException();
    }

    public Value get(int index) {
      throw new NullPointerException();
    }

    public Iterator<Value> iterator() {
      throw new NullPointerException();
    }

  }

  /**
   * {@code new Json.Null()}.
   */
  public static final Json.Null NULL = new Json.NullImpl();

  /**
   * {@code new Json.Boolean(true)}.
   */
  public static final Json.Boolean TRUE = new Json.BooleanImpl(true);

  /**
   * {@code new Json.Boolean(false)}.
   */
  public static final Json.Boolean FALSE = new Json.BooleanImpl(false);

  /**
   * Read a JSON value from a reader.
   */
  public static Json.Value read(Reader reader) throws IOException {
    CharStream stream = new CharStream(reader);
    return read(stream, skip(stream));
  }

  /**
   * Read a JSON array from a reader and call {@code Generator#yield(Json.Value)}
   * after parsing each element.
   */
  public static void generate(Reader reader, Generator generator) throws IOException {
    CharStream stream = new CharStream(reader);
    generate(stream, skip(stream), generator);
  }

  private static int skip(CharStream reader) throws IOException {
    int c;
    while (Character.isWhitespace(c = reader.read())) {
    }
    return c;
  }

  private static void generate(CharStream reader, int c, Generator generator) throws IOException {
    switch (c) {
      // array
      case '[':
        // Unrolling to avoid state, note that the first time around we must not
        // skip when reading the value added to the array.
        c = skip(reader);
        if (c == ']') {
          return;
        }
        generator.yield(read(reader, c));
        c = skip(reader);
        do {
          if (c == ']') {
            return;
          }
          generator.yield(read(reader, skip(reader)));
        } while ((c = skip(reader)) == ',' || c == ']');
        throw new IllegalArgumentException("non terminated array literal");
      default:
        throw new IllegalArgumentException("array expected");
    }
  }

  private static Json.Value read(CharStream reader, int c) throws IOException {
    StringBuilder sb;
    switch (c) {
      // null
      case 'n': {
        int u = reader.read(), l1 = reader.read(), l2 = reader.read();
        if (u == 'u' && l1 == 'l' && l2 == 'l') {
          return Json.NULL;
        } else {
          throw new IllegalArgumentException("null expected");
        }
      }

      // true
      case 't': {
        int r = reader.read(), u = reader.read(), e = reader.read();
        if (r == 'r' && u == 'u' && e == 'e') {
          return Json.TRUE;
        } else {
          throw new IllegalArgumentException("true expected");
        }
      }

      // false
      case 'f': {
        int a = reader.read(), l = reader.read(), s = reader.read(), e = reader.read();
        if (a == 'a' && l == 'l' && s == 's' && e == 'e') {
          return Json.FALSE;
        } else {
          throw new IllegalArgumentException("false expected");
        }
      }

      // object
      case '{':
        Json.ObjectImpl object = new Json.ObjectImpl();
        // Unrolling to avoid state, note that the first time around we must not
        // skip when reading the key of the object.
        c = skip(reader);
        if (c == '}') {
          return object;
        }
        Json.StringImpl key = (Json.StringImpl) read(reader, c);
        if (skip(reader) != ':') {
          throw new IllegalArgumentException(": expected");
        }
        object.put(key, read(reader, skip(reader)));
        c = skip(reader);
        do {
          if (c == '}') {
            return object;
          }
          key = (Json.StringImpl) read(reader, skip(reader));
          if (skip(reader) != ':') {
            throw new IllegalArgumentException(": expected");
          }
          object.put(key, read(reader, skip(reader)));
        } while ((c = skip(reader)) == ',' || c == '}');
        throw new IllegalArgumentException(java.lang.String.format(
            "Non terminated object literal. Last character read was %s. " +
            "Partial object literal read %s.",
            Character.toString((char) c),
            object.toString()));

      // array
      case '[':
        Json.ArrayImpl array = new Json.ArrayImpl();
        // Unrolling to avoid state, note that the first time around we must not
        // skip when reading the value added to the array.
        c = skip(reader);
        if (c == ']') {
          return array;
        }
        array.add(read(reader, c));
        c = skip(reader);
        do {
          if (c == ']') {
            return array;
          }
          array.add(read(reader, skip(reader)));
        } while ((c = skip(reader)) == ',' || c == ']');
        throw new IllegalArgumentException("non terminated array literal");

      // string
      case '"':
        sb = new StringBuilder();
        do {
          switch (c = reader.read()) {
            case '"':
              return new Json.StringImpl(sb.toString());
            case '\\':
              switch (c = reader.read()) {
                case '"':
                case '\\':
                case '/':
                  sb.append((char) c);
                  break;
                case 'b':
                  sb.append('\b');
                  break;
                case 'f':
                  sb.append('\f');
                  break;
                case 'n':
                  sb.append('\n');
                  break;
                case 'r':
                  sb.append('\r');
                  break;
                case 't':
                  sb.append('\t');
                  break;
                case 'u':
                  int h1 = reader.read(), h2 = reader.read(), h3 = reader.read(), h4 = reader.read();
                  if (('a' <= h1 && h1 <= 'f' || 'A' <= h1 && h1 <= 'F' || '0' <= h1 && h1 <= '9') &&
                      ('a' <= h2 && h2 <= 'f' || 'A' <= h2 && h2 <= 'F' || '0' <= h2 && h2 <= '9') &&
                      ('a' <= h3 && h3 <= 'f' || 'A' <= h3 && h3 <= 'F' || '0' <= h3 && h3 <= '9') &&
                      ('a' <= h4 && h4 <= 'f' || 'A' <= h4 && h4 <= 'F' || '0' <= h4 && h4 <= '9')) {
                    sb.append((char) (fromHex(h1) << 12 | fromHex(h2) << 8 | fromHex(h3) << 4 | fromHex(h4)));
                  } else {
                    throw new IllegalArgumentException("invalid hex code");
                  }
              }
              break;
            case -1:
              throw new IllegalArgumentException("non terminated string");
            default:
              sb.append((char) c);
          }
        } while (true);

      // number
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        sb = new StringBuilder();
        do {
          sb.append((char) c);
        } while ((c = reader.read()) <= '9' && '0' <= c);
        switch (c) {
          case '.':
            do {
              sb.append((char) c);
            } while ((c = reader.read()) <= '9' && '0' <= c);
          case 'e':
          case 'E':
            if (c != 'e' && c != 'E') {
              reader.unread(c);
              return new Json.NumberImplBigDecimal(new BigDecimal(sb.toString()));
            }
            sb.append((char) c);
            c = reader.read();
            if (c != '+' && c != '-' && c < '0' && '9' < c) {
              reader.unread(c);
              return new Json.NumberImplBigDecimal(new BigDecimal(sb.toString()));
            }
            do {
              sb.append((char) c);
            } while ((c = reader.read()) <= '9' && '0' <= c);
          default:
            reader.unread(c);
            return new Json.NumberImplBigDecimal(new BigDecimal(sb.toString()));
        }

      // error
      default:
        throw new IllegalArgumentException("illegal character " + (char) c);
    }
  }

  /* Visible for testing. */
  static int fromHex(int codePoint) {
    // '9' = 57, 'F' = 70, 'f' = 102
    return (codePoint <= '9') ? codePoint - '0' :
        (codePoint <= 'F') ? codePoint - 'A' + 10 : codePoint - 'a' + 10;
  }

  /**
   * Create a JSON value from a string.
   */
  public static Json.Value fromString(java.lang.String input) {
    try {
      return read(new StringReader(input));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Json.Array array(Json.Value... values) {
    return new Json.ArrayImpl(values);
  }

  public static Json.Array array(Iterable<Json.Value> values) {
    return new Json.ArrayImpl(values);
  }

  public static Json.Array array(Json.Array value) {
    return new Json.ArrayImpl(value);
  }

  public static Json.Array array(java.lang.Object... values) {
    Json.Value[] jsonValues = new Json.Value[values.length];
    for (int i = 0; i < values.length; i++) {
      jsonValues[i] = value(values[i]);
    }
    return new Json.ArrayImpl(jsonValues);
  }

  public static Json.Object object() {
    return new Json.ObjectImpl();
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1) {
    Json.Object o = object();
    o.put(k1, v1);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2) {
    Json.Object o = object(k1, v1);
    o.put(k2, v2);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3) {
    Json.Object o = object(k1, v1, k2, v2);
    o.put(k3, v3);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3,
      Json.String k4, Json.Value v4) {
    Json.Object o = object(k1, v1, k2, v2, k3, v3);
    o.put(k4, v4);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3,
      Json.String k4, Json.Value v4,
      Json.String k5, Json.Value v5) {
    Json.Object o = object(k1, v1, k2, v2, k3, v3, k4, v4);
    o.put(k5, v5);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3,
      Json.String k4, Json.Value v4,
      Json.String k5, Json.Value v5,
      Json.String k6, Json.Value v6) {
    Json.Object o = object(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    o.put(k6, v6);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3,
      Json.String k4, Json.Value v4,
      Json.String k5, Json.Value v5,
      Json.String k6, Json.Value v6,
      Json.String k7, Json.Value v7) {
    Json.Object o = object(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    o.put(k7, v7);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3,
      Json.String k4, Json.Value v4,
      Json.String k5, Json.Value v5,
      Json.String k6, Json.Value v6,
      Json.String k7, Json.Value v7,
      Json.String k8, Json.Value v8) {
    Json.Object o = object(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    o.put(k8, v8);
    return o;
  }

  public static Json.Object object(
      Json.String k1, Json.Value v1,
      Json.String k2, Json.Value v2,
      Json.String k3, Json.Value v3,
      Json.String k4, Json.Value v4,
      Json.String k5, Json.Value v5,
      Json.String k6, Json.Value v6,
      Json.String k7, Json.Value v7,
      Json.String k8, Json.Value v8,
      Json.String k9, Json.Value v9) {
    Json.Object o = object(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    o.put(k9, v9);
    return o;
  }

  public static Json.Object object(Json.Value... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Number of arguments must be even");
    }
    Json.Object o = object();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      Json.Value key = keyValuePairs[i];
      if (key instanceof Json.String) {
        o.put((Json.String) key, keyValuePairs[i + 1]);
      } else {
        throw new IllegalArgumentException("Keys must be JSON strings");
      }
    }
    return o;
  }

  public static Json.Object object(java.lang.Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Number of arguments must be even");
    }
    Json.Object o = object();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      java.lang.Object key = keyValuePairs[i];
      if (key instanceof java.lang.String) {
        o.put(string((java.lang.String) key), value(keyValuePairs[i + 1]));
      } else if (key instanceof Json.String) {
        o.put((Json.String) key, value(keyValuePairs[i + 1]));
      } else {
        throw new IllegalArgumentException("Keys must be strings");
      }
    }
    return o;
  }

  public static Json.Number number(short number) {
    return new Json.NumberImplShort(number);
  }

  public static Json.Number number(int number) {
    return new Json.NumberImplInt(number);
  }

  public static Json.Number number(long number) {
    return new Json.NumberImplLong(number);
  }

  public static Json.Number number(float number) {
    return new Json.NumberImplFloat(number);
  }

  public static Json.Number number(double number) {
    return new Json.NumberImplDouble(number);
  }

  public static Json.Number number(BigDecimal number) {
    return new Json.NumberImplBigDecimal(number);
  }

  public static Json.String string(java.lang.String string) {
    return new Json.StringImpl(string);
  }

  private static <T> Json.Value value(T object) {
    if (object == null) {
      return NULL;
    }
    @SuppressWarnings("unchecked")
    Descriptor<T, ?> descriptor = (Descriptor<T, ?>)
      DescriptorFactory.baseTypes.get(object.getClass());
    if (descriptor != null) {
      return descriptor.marshall(object, null);
    } else if (object instanceof Json.Value) {
      return (Json.Value) object;
    } else {
      throw new IllegalArgumentException(
          "Cannot auto-convert " + object.getClass() + " to Json.Value");
    }
  }

  /** See {@link #TRUE}, {@link #FALSE}. */
  static Json.Boolean booleanValue(boolean b) {
    return new Json.BooleanImpl(b);
  }

  /** See {@link #NULL}. */
  static Json.Null nullValue() {
    return new Json.NullImpl();
  }

  /**
   * Function invoked inside loops to yield {@code Json.Value}s to the caller.
   */
  public static interface Generator {

    /**
     * Yield a {@code Json.Value}.
     */
    void yield(Json.Value value);

  }

}
