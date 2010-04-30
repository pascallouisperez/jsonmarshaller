package com.twolattes.json.bugreports;

import static com.twolattes.json.TwoLattes.createEntityMarshaller;

import org.junit.Test;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

public class PolymorphicHierarchyInlinedTest {

  @Test(expected = IllegalArgumentException.class)
  public void marshallTopLevel() {
    createEntityMarshaller(TopLevel.class).marshall(new TopLevel() {{
      this.person = new Employee();
    }});
  }

  @Entity
  static class TopLevel {
    @Value(inline = true) Person person;
  }

  @Entity(discriminatorName = "type", subclasses = Manager.class)
  static class Person {
  }

  @Entity(discriminator = "person")
  static class Employee extends Person {
  }

  @Entity(discriminator = "manager")
  static class Manager extends Person {
  }

}
