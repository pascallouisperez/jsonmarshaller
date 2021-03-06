package com.twolattes.json;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.twolattes.json.bugreports.DisallowValueOnNonEntitiesTest;
import com.twolattes.json.bugreports.MissingNoArgumentConstructorTest;
import com.twolattes.json.bugreports.OrderStatusTest;
import com.twolattes.json.bugreports.PolymorphicHierarchyInlinedTest;
import com.twolattes.json.collection.CollectionTest;
import com.twolattes.json.embed.EmbeddingTest;
import com.twolattes.json.enumimpl.EnumTest;
import com.twolattes.json.gettersetter.GetterSetterTest;
import com.twolattes.json.inheritance1.Inheritance1Test;
import com.twolattes.json.inheritance2.Inheritance2Test;
import com.twolattes.json.inheritance3.Inheritance3Test;
import com.twolattes.json.inheritance4.Inheritance4Test;
import com.twolattes.json.inheritanceerror.InheritanceErrorTest;
import com.twolattes.json.nativetypes.BigDecimalTest;
import com.twolattes.json.nativetypes.JavaTypeMarshallingTest;
import com.twolattes.json.nativetypes.JavaTypeUnmarshallingTest;
import com.twolattes.json.nativetypes.LiteralsTest;
import com.twolattes.json.optional.OptionalTest;
import com.twolattes.json.types.TypesTest;
import com.twolattes.json.types.URLTypeTest;
import com.twolattes.json.views.ViewsTest;
import com.twolattes.json.visibility1.Visibility1Test;
import com.twolattes.json.visibility2.Visibility2Test;

@RunWith(value = Suite.class)
@SuiteClasses(value = {
  DescriptorsEqualityTest.class,
  DescriptorFactoryTest.class,
  MarshallerTest.class,
  EntityMarshallingTest.class,
  EntityUnmarshallingTest.class,
  JavaTypeMarshallingTest.class,
  JavaTypeUnmarshallingTest.class,
  EntityClassDescriptorBuilderTest.class,
  TypesTest.class,
  URLTypeTest.class,
  Inheritance1Test.class,
  Inheritance2Test.class,
  Inheritance3Test.class,
  Inheritance4Test.class,
  InheritanceErrorTest.class,
  Visibility1Test.class,
  Visibility2Test.class,
  GetterSetterTest.class,
  ViewsTest.class,
  CollectionTest.class,
  CollectionTypeTest.class,
  MapTypeTest.class,
  EnumTest.class,
  LiteralsTest.class,
  BigDecimalTest.class,
  UserTypeDescriptorTest.class,
  OrderStatusTest.class,
  EmbeddingTest.class,
  OptionalTest.class,
  PolymorphicHierarchyInlinedTest.class,
  MissingNoArgumentConstructorTest.class,
  DisallowValueOnNonEntitiesTest.class,

  // Json
  JsonTest.class,
  OrgJsonAssert.class,
  PrettyPrinterTest.class
})
public class AllJsonTests {
}
