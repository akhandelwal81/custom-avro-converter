# Cutom-avro-converter for Scala
This is a library that can be predominantly utilised for serialisation and de-serialisation of datasets in Avro with or without the schema being applied to it.
This library can be simply added as a dependency to perform custom implementation. Thereby reducing the need to write a lot of boilerplate code. This could well be extended to build similar marshalling capability for Parquet, Orc etc.
Key features -
  Generic Serialisation/De-serialisation of Scala Types into Avro Types and vice-versa
  Schema Generation from code at compile time

## Schema

Avro is a schema based format and this is one of its main differentiator with Json format. During various implementations there is always a need to define/generate schemas for the data that needs to be ingested into the application. Data is at the heart of all application implementations. Trying to generate all of these schemas manually could be a huge tedious job. In the current world where Data Model and Data Schemas are key, this functionality enables the developer community to generate schemas directly using Scala Case Classes construct and that too at compile time using macros.
This in nutshell becomes very powerful when it comes to the ease of code generation without expliciting running a specific step as well as bypass the challenge in terms of performance if its done using reflection at runtime.

Sample Implementation:
```
case class Employee(firstname: String,lastname: String, salary: Double, address: String)
case class Organisation(name: String, department: String, departmentId: Int, employees: Seq[Employee])
```
In order to generate an Avro Schema, only thing required is to use AvroSchema object that is passed to the target type as a type parameter. This returns an org.apache.avro.Schema instance.

```
import com.akhandelwal
val schema = AvroSchema[Orgsanisation]
```
Generated Schema:
```
{
   "type":"record",
   "name":"Organisation",
   "namespace":"com.akhandelwal",
   "fields":[
      {
         "name":"name",
         "type":"string"
      },
      {
         "name":"department",
         "type":"string"
      },
      {
         "name":""departmentId,
         "type":"integer"
      },
      {
         "name":"employees",
         "type":{
            "type":"array",
            "items":{
               "type":"record",
               "name":"Employee",
               "fields":[
                  {
                     "name":"firstname",
                     "type":"string"
                  },
                  {
                     "name":"lastname",
                     "type":"string"
                  },
                  {
                     "name":"salary",
                     "type":"double"
                  },
                  {
                     "name":"adress",
                     "type":"string"
                  },
               ]
            }
         }
      }
   ]
}

```



## Overriding a Schema
AvroSchema uses an implicit SchemaFor. This is the core typeclass which helps to generate an Avro Schemafor a given Scala Type. A similar implementation can be done for a Java Type as well.
There are SchemaFor instances for all the common JDK and SDK Types.
For overriding a schema for a particular type, there is a need to bring into scope an implicit SchemaFor for the type that needs to be overridden.
In order to achive this, a new instance of SchemaFor can be used and once its put into scope, it can generate the schema.
```
implicit val floatOverride = SchemaFor [FLOAT](SchemaBuilder.builder.stringType)

case class X(f:float)
val schema =AvroSchema[X]

```
## Overriding - Name of the field in the data
AvroName annotation can be used to override the name of the fields . This is quite helpful when at the time of data ingestion , there is a need to standardise the specific attributes across multiple types of datasets.
```
package com.akhandelwal
case class X(p: String, @AveroName("t") q: String)
```
Schema would be generated as below:
```
{
  "type":"record",
  "name":"X",
  "namespace":"com.akhandelwal",
  "fields":[
    {
      "name":"p",
      "type":"string"
    },
    {
      "name":"t",
      "type":"string"
    }    
  ]
}
```

## How to add properties and documentation reference to Schemas

There is a doc field supported by Avro which allows to add arbitrary key-values to the schema that gets generated at compile time. This library uses AvroDoc and AvroProp as annotations to support similar functionality
```
package com.akhandelwal
@AvroDoc("Department ID is the unique/primary key for Departments")
case class X(@AvroDoc("This is type converted to String from Int") str: String, @AvroDoc("This is a decimal") decimal: Decimal, int: Int)
```
Above is translated to-
```
{  
  "type": "record",
  "name": "X",
  "namespace": "com.akhandelwal",
  "doc":"Department ID is the unique/primary key for Departments",
  "fields": [  
    {  
      "name": "str",
      "type": "string",
      "doc" : "This is type converted to String from Int"
    },
    {  
      "name": "decimal",
      "type": "decimal",
      "doc" : "This is a decimal"
    },
    {  
      "name": "int",
      "type": "int"
    }
  ]
}
```
# Integration with Cats
Cats is a Library that provides abstractions for functional programming in the Scala Programming Language. The name is derived from category.
Cats strive to provide functional programming abstractions that are core, binary compatible, modular, approachable and efficient.
