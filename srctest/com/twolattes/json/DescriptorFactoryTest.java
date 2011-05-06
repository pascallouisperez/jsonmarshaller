package com.twolattes.json;

import static com.twolattes.json.Json.string;
import static com.twolattes.json.StringDescriptor.STRING_DESC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import com.twolattes.json.types.JsonType;

public class DescriptorFactoryTest {

  @Test(expected = IllegalArgumentException.class)
  public void testNotAnEntity() {
    create(NotAnEntity.class);
  }

  @Test
  public void testBaseTypeEntity() {
    EntityDescriptor<?> d = create(BaseTypeEntity.class);
    assertEquals(BaseTypeEntity.class, d.getReturnedClass());
    assertEquals(8, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      assertEquals(f.getFieldName(), f.getJsonName());
      Descriptor<?, ?> fd = f.getDescriptor();
      assertEquals(BaseTypeEntity.fields.get(f.getFieldName()), fd.getReturnedClass());
    }
  }

  @Test
  public void testGetterSetterEntity() throws Exception {
    EntityDescriptor<?> d = create(GetterSetterEntity.class);
    assertEquals(GetterSetterEntity.class, d.getReturnedClass());
    assertEquals(1, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      assertEquals(string("name"), f.getJsonName());
      assertEquals(string("name"), f.getFieldName());
    }
  }

  @Test
  public void testEntityInterface() throws Exception {
    EntityDescriptor<?> d = create(EntityInterface.class);
    assertEquals(EntityInterface.class, d.getReturnedClass());
    assertEquals(1, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      assertEquals(string("whatever"), f.getJsonName());
      assertEquals(string("whatever"), f.getFieldName());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEntityInterfaceWrongImplementedBy() throws Exception {
    create(EntityInterfaceWrongImplementedBy.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetSetCollidingWithField() throws Exception {
    create(GetSetCollidingWithField.class);
  }

  @Test
  public void testCollectionEntity() {
    EntityDescriptor<?> d = create(CollectionEntity.class);
    assertEquals(CollectionEntity.class, d.getReturnedClass());
    assertEquals(1, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      assertEquals(string("buddies"), f.getFieldName());
      assertEquals(string("friends"), f.getJsonName());
      assertEquals(
          new CollectionDescriptor(Collection.class, STRING_DESC),
          f.getDescriptor());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testContravariantCollectionEntity() {
    create(ContravariantCollectionEntity.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testVagueWildcardEntity() {
    create(VagueWildcardEntity.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenericGetterSetterEntity() {
    create(GenericGetterSetterEntity.class);
  }

  @Test
  public void testEnitityInEntity() {
    EntityDescriptor<?> d = create(EntityInEntity.class);
    assertEquals(1, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      Descriptor<?, ?> fd = f.getDescriptor();
      assertTrue(fd instanceof EntityDescriptor);
      EntityDescriptor<?> ed = (EntityDescriptor<?>) fd;
      assertEquals(EntityInEntity.INNER_ENTITY, ed.getReturnedClass());
      assertEquals(create(EntityInEntity.INNER_ENTITY), fd);
    }
  }

  @Test
  public void testInlinedEntity1() {
    EntityDescriptor<?> d = create(User.class);
    assertEquals(1, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      fieldDescriptorIsInline(f);
    }
  }

  @Test
  public void testInlinedEntity2() {
    EntityDescriptor<?> d = create(UserInlinedEmail.class);
    assertEquals(6, d.getFieldDescriptors().size());
    int visited = 0;
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      if (f.getFieldName().equals(string("email"))) {
        fieldDescriptorIsInline(f);
        visited++;
      } else if (f.getFieldName().equals(string("inlineTrue"))) {
        fieldDescriptorIsInline(f);
        visited++;
      }
    }
    assertEquals(2, visited);
  }

  @Test
  public void testInlinedEntity3() {
    EntityDescriptor<?> d = create(UserInlinedEmail.class);
    assertEquals(6, d.getFieldDescriptors().size());
    boolean visited = false;
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      if (f.getFieldName().equals(string("emails"))) {
        assertTrue(f instanceof AbstractFieldDescriptor.DirectAccessFieldDescriptor);
        assertTrue(f.getDescriptor() instanceof MapDescriptor);
        assertTrue(((MapDescriptor) f.getDescriptor()).getValueDescriptor() instanceof InlinedEntityDescriptor);
        visited = true;
      }
    }
    assertTrue(visited);
  }

  @Test
  public void testInlinedEntity4() {
    EntityDescriptor<?> d = create(DoublyInlined.class);
    boolean visited = false;
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      if (f.getFieldName().equals(string("foo"))) {
        fieldDescriptorIsInline(f);
        visited = true;
      }
    }
    assertTrue(visited);
  }

  private void fieldDescriptorIsInline(FieldDescriptor f) {
    assertTrue(f.getFieldName().getString(),
        f.getDescriptor() instanceof InlinedEntityDescriptor);
//    assertTrue(f.getFieldName(),
//        f instanceof InlinedFieldDescriptor);
//    assertFalse(f.getFieldName(),
//        ((InlinedFieldDescriptor) f).delegate instanceof InlinedFieldDescriptor);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInlinedUninlineableEntity1() {
    create(Uninlineable1.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInlinedUninlineableEntity2() {
    create(Uninlineable2.class);
  }

  @Test
  public void testMap() throws Exception {
    EntityDescriptor<?> d = create(EntityMap.class);
    assertEquals(1, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      assertTrue(f.getDescriptor() instanceof MapDescriptor);
      MapDescriptor md = (MapDescriptor) f.getDescriptor();
      assertTrue(md.getValueDescriptor() instanceof EntityDescriptor);
      EntityDescriptor<?> ed = (EntityDescriptor<?>) md.getValueDescriptor();
      assertEquals(Email.class, ed.getReturnedClass());
    }
  }

  @Test
  public void testViews() throws Exception {
    EntityDescriptor<?> d = create(MultipleViewEntity.class);
    assertEquals(5, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      if (f.getFieldName().equals("email")) {
        assertTrue(f.isInView("full"));
        assertFalse(f.isInView("simple"));
      } else if (f.getFieldName().equals("motto")) {
        assertTrue(f.isInView("full"));
        assertFalse(f.isInView("simple"));
      } else if (f.getFieldName().equals("name")) {
        assertTrue(f.isInView("full"));
        assertTrue(f.isInView("simple"));
      } else if (f.getFieldName().equals("user")) {
        assertFalse(f.isInView("full"));
        assertTrue(f.isInView("simple"));
      } else if (f.getFieldName().equals("normal")) {
        assertTrue(f.isInView("full"));
        assertTrue(f.isInView("simple"));
      }
    }
  }

  @Ignore("not yet implemented")
  @Test
  public void testBExtendsA() {
    EntityDescriptor<?> d = create(B.class);
    // TODO: make the test pass!
    assertEquals(2, d.getFieldDescriptors().size());
    for (FieldDescriptor f : d.getFieldDescriptors()) {
      assertTrue(f.getFieldName().equals("a") || f.getFieldName().equals("b"));
    }
  }

  @Ignore("this is an uncommon case, will implement in next release")
  @Test
  public void testAbisExtendsA() {
    create(Abis.class);
    // TODO: make the test pass... what about name classes? field a in Abis and A!
    // this is the least important feature, can implement in v2 only
  }

  private EntityDescriptor<?> create(Class<?> clazz) {
    return new DescriptorFactory().create(
        clazz, new DescriptorFactory.EntityDescriptorStore(),
        new HashMap<Type, Class<? extends JsonType<?, ?>>>()).left;
  }
}
