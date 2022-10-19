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
        // Control Flow
        var next = i.next;
        if (next.size > 1) {
-}}
            i{{ i.index }} [shape="diamond" style="filled" fillcolor="cyan" label="{{ i.opCodeName }}:{{ i.lineNumber }}"];
{{#
            for (var n in next) {
                if (next.size > 2) {
-}}
            i{{ i.index }} -> i{{ n }};
{{#
                } else {
                    if (i.index + 1 == n) {
-}}
            i{{ i.index }} -> i{{ n }} [color="red" label="false"];
{{#
                    } else {
-}}
            i{{ i.index }} -> i{{ n }} [color="green" label="true"];
{{#
                    }
                }
            }
        } else {
            if (i.referenceId >= 0) {
-}}
            i{{ i.index }} [style="filled" fillcolor="orange" label="{{ i.opCodeName }}:{{ i.lineNumber }}"];
{{#
            } else {
-}}
            i{{ i.index }} [label="{{ i.opCodeName }}:{{ i.lineNumber }}"];
{{#
            }

            for (var n in next) {
-}}
            i{{ i.index }} -> i{{ n }};
{{#
            }
        }
    }
-}}
}
