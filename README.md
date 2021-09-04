# Cutom-avro-converter for Scala
This is a library that can be predominantly utilised for serialisation and de-serialisation of datasets in Avro with or without the schema being applied to it.
This library can be simply added as a dependency to perform custom implementation. Thereby reducing the need to write a lot of boilerplate code. This could well be extended to build similar marshalling capability for Parquet, Orc etc.
Key features -
  Generic Serialisation/De-serialisation of Scala Types into Avro Types and vice-versa
  Schema Generation from code at compile time

## Overriding a Schema
AvroSchema uses an implicit SchemaFor. This is the core typeclass which helps to generate an Avro Schemafor a given Scala Type. A similar implementation can be done for a Java Type as well.
There are SchemaFor instances for all the common JDK and SDK Types.
For overriding a schema for a particular type, there is a need to bring into scope an implicit SchemaFor for the type that needs to be overridden.
In order to achive this, a new instance of SchemaFor can be used and once its put into scope, it can generate the schema.

implicit val floatOverride = SchemaFor [FLOAT](SchemaBuilder.builder.stringType)

case class X(f:float)
val schema =AvroSchema[X]
