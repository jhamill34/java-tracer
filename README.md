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
  --interactive
```

## Interactive Mode

The default interactive mode acts as a stack machine (much like how the JVM operates). Common operations 
related to the stack include: 

| Operation | Description                                                        |
|-----------|--------------------------------------------------------------------|
| PUSH      | Adds a value to the top of the stack                               | 
| POP       | Removes the top item from the stack                                |
| DUP       | Takes the top item and duplicates it to the top of the stack       |
| SWAP      | Swaps the positions in the stack of the top two items in the stack |
| SHOW      | Prints out the contents of the stack                               |


Operations related to the processed data include: 

| Operation | Description                                                                                 |
|-----------|---------------------------------------------------------------------------------------------|
| SUMMARY   | Prints out a summary of all stored entities                                                 |
| ID        | Based on the top values in the stack searches for the corresponding id of the entity        |
| FIND      | Given the top value in the stack is an ID, replaces the item with an entity object          |
| DESCRIBE  | Given the top value in the stack is an Entity, removes the entity, and prints out a summary |


### Query Example 
![query](assets/query-v1.0.gif)

### Method Simulation Example
![simulation](assets/simulate-v1.0.gif)
