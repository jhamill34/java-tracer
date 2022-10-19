{{#
    // This template creats a markdown summary of a provided
    // method that is simulated. The contents consist of
    // instructions and referenced methods/fields and heap values
    // that are either produced or consumed in each instruction

    include "native";

    fun argsString(args) {
        var result = "";
        for (var arg in args) {
            if (result != "") {
                result = result + " ";
            }

            result = result + arg;
        }
        return result;
    }

    var class = getClass($1);
    var method = getMethod(class, $2);
    simulate(class, method);
-}}
## {{ method.returnType }} {{ class.className }}.{{ method.name }}({{ argsString(method.parameters) }})

Complexity={{ method.complexity }}

{{#
    var currentLine = 0;
    for (var i in method.instructions) {
        if (currentLine != i.lineNumber) {
            currentLine = i.lineNumber;
-}}

### Line: {{ currentLine }}

{{#
        }

        if (i.opCode >= 0) {
            var produced = i.produced;
            var consumed = i.consumed;
-}}
    - {{ i.opCodeName }}
{{#
            if (i.referenceId > 0) {
                var ref = i.reference;
-}}
        - {{ ref.returnType }} {{ ref.owner.className }}.{{ ref.name }}({{ argsString(ref.parameters) }})
{{#
            }

            if (produced.size > 0) {
-}}
        - Produced
{{#
                for (var p in produced) {
-}}
            - {{ p.id }}
{{#
                }
            }

            if (consumed.size > 0) {
-}}
        - Consumed
{{#
                for (var c in consumed) {
-}}
            - {{ c.id }}
{{#
                }
            }
        }
    }
-}}
