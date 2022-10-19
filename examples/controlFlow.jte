{{#
    // Generates a GraphViz dot formatted file that can be used
    // to render a representation of the control flow for the method
    // (e.g. Assuming the output is redirected to a file called result.dot
    //       `dot -Tpng result.dot -o result.png`)

    include "native";

    var class = getClass($1);
    var method = getMethod(class, $2);
-}}
digraph G {
{{#
    for (var i in method.instructions) {
-}}
    {{ i.index }} [label="{{i.opCodeName}}"];
{{#
        for (var n in i.next) {
-}}
    {{ i.index }} -> {{ n }};
{{#
        }
    }
-}}
}
