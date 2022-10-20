# Java Tracer

Understanding a large (especially legacy) Java codebase is hard. This project aims
to analyze a classpath (i.e. a set of jar files) to better understand code paths and how 
dependencies interact with each other.
Analysis of these bundles includes better understanding how entities such as Classes, Methods, Fields, 
Instructions are related to each other. Simulations can also be run to better understand 
how objects are used and passed to and from other methods. 

## Getting Started

To run this program you need to provide your classpath in a file that can be passed as an argument to the CLI.
By default, the delimiter is the typical delimiter for a path variable, 
the colon character `:` (this can be changed with the `--delimiter` option). 

```bash
git clone https://github.com/jhamill34/java-tracer
gradle shadowJar
java -jar build/libs/java-tracer-v2-1.0-all.jar \
  --classpath file_with_classpath.txt \
  --script examples/script.jte \
  --template
```


## Scripting

Passing a script as an argument to the CLI enables data extraction from the processed classpath. Scripts are composed
of a simple "c-style" language with dynamic typing. The compiler converts it to the stack based language shown below in 
the interactive mode. 

## Templates

Passing the `--template` flag turns our interpreter into a templating engine backed by the embedded scripting language. 
This is useful for creating reports around entities in the classpath (i.e. list the complexity of all methods, etc.).

Entering the scripting language is indicated by `{{#`  and closed by `}}` characters. These sections do not print anything out.
If this section is closed by `-}}` then the trailing newline character is ignored. 

A printable statement is indicated by `{{` and closed by `}}`, and expression between these characters will be printed out. 

## Interactive Mode

To enable interactive mode, add the `--interactive` or `-i` flag to your command. This will enter interactive mode after 
processing the classpath and executing a script if provided. 

Interactive mode acts as a stack machine (much like how the JVM operates). Common operations 
related to the stack include: 

| Operation | Description                                                        |
|-----------|--------------------------------------------------------------------|
| push      | Adds a value to the top of the stack                               | 
| pop       | Removes the top item from the stack                                |
| dup       | Takes the top item and duplicates it to the top of the stack       |
| swap      | Swaps the positions in the stack of the top two items in the stack |
| load      | Loads a variable with the given id from the local store to the top of the stack |
| store     | Stores the value on top of the stack to the local store identified by the provided id |
| math      | Calculates a given result for the provided operand (+, -, *, /, concatenate if a value is a string). |
| cmp       | Determines a boolean value from the values on the stack for a given operation (=, !, <, >, <=, >=). |
| queue     | Creates an empty queue and and puts it on the top of the stack |
| next      | Given the top value on the stack is a queue, replaces the value with the next value in the queue (should be used with dup). |
| offer     | Adds the top value on the stack to the next value on the stack (given it is a queue). |
| stack     | Prints out the contents of the stack                               |
| locals    | Prints out the contents of the local variables                     |
| const     | Prints out the contents of any constants (only populated after a script execution) |


Operations related to the processed data include: 

| Operation | Description                                                                                 |
|-----------|---------------------------------------------------------------------------------------------|
| summary   | Prints out a summary of all stored entities                                                 |
| id        | Based on the top values in the stack searches for the corresponding id of the entity        |
| find      | Given the top value in the stack is an ID, replaces the item with an entity object          |
| attr      | If the top value on the stack is an entity, returns a property and puts it on the top of the stack |
| list      | Fetches a list of all entities and places it on the top of the stack                        |
| sort      | Sorts the resulting list by the provided attribute |
| desc      | Given the top value in the stack is an Entity, removes the entity, and prints out a summary |
| invoke    | Simulates a provided method and class entity and computes a collection of values            |

## Entities Schema 

### Class

| Attribute | Description |
|-----------|-------------|
| id | int | 
| access | int | 
| name | String | 
| signature | String | 
| packageName | String | 
| superClass | Class | 
| interfaces | List<Class> | 
| subClasses | List<Class> | 
| implementors | List<Class> | 
| fields | List<Field> | 
| methods | List<Method> | 
| className | String | 
| accessList | List<String> | 

### Method

| Attribute | Description |
|-----------|-------------|
| id | int | 
| ownerId | int | 
| access | int | 
| name | String | 
| descriptor | String | 
| signature | String | 
| complexity | int | 
| instructions | List<Instruction> | 
| owner | Class | 
| returnType | String | 
| parameters | List<String> | 
| accessList | List<String> | 

### Field

| Attribute | Description |
|-----------|-------------|
| id | int | 
| ownerId | int | 
| access | int | 
| name | String | 
| descriptor | String | 
| signature | String | 
| owner | Class | 
| type | String | 
| accessList | List<String> | 

### Instruction

| Attribute | Description |
|-----------|-------------|
| id | int | 
| invokerId | int | 
| opCode | int | 
| lineNumber | int | 
| index | int | 
| referenceId | int | 
| referenceType | String (METHOD, FIELD, or null) | 
| produced | List<Value> | 
| consumed | List<Value> | 
| invoker | Method | 
| reference | Method, Field, or null | 
| opCodeName | String | 
| next | int | 

### Value

| Attribute | Description |
|-----------|-------------|
| id | int | 
| producers | List<Instruction> | 
| consumers | List<Instruction> | 
| proxies | List<Value> | 
| proxiedBy | Value | 
| type | String | 

### Query Example 
![query](assets/query-v1.0.gif)

### Method Simulation Example
![simulation](assets/simulate-v1.0.gif)
