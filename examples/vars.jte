{{#
    include "native";

    fun join(list, delim) {
        var result = "";

        for (var item in list) {
            if (result != "") {
                result = result + delim;
            }

            result = result + item;
        }

        return result;
    }

    var class = getClass($1);
    var method = getMethod(class, $2);

    simulate(class, method);

    var values = allValues();
    values = sort(values, "id");

    for (var v in values) {
-}}
    {{ v.id }}: {{ v.type }}
{{#
        if (v.producers.size > 0) {
-}}
        - Producers:
{{#
        }
        for (var p in v.producers) {
            var invoker = p.invoker;
            var owner = invoker.owner;
-}}
            - {{ p.opCodeName }}: {{ invoker.returnType }} {{ owner.className }}.{{ invoker.name }}({{ join(invoker.parameters, ", ") }}):{{ p.lineNumber }}
{{#
            if (p.referenceId >= 0) {
                var ref = p.reference;

                if (p.referenceType == "METHOD") {
-}}
                - {{ ref.owner.className }}.{{ ref.name }}({{ join(ref.parameters, ", ") }}) [{{ ref.owner.packageName }}]
{{#
                } else {
-}}
                - {{ ref.owner.className }}.{{ ref.name }} [{{ ref.owner.packageName }}]
{{#
                }
            }
        }

        if (v.consumers.size > 0) {
-}}
        - Consumers:
{{#
        }

        for (var c in v.consumers) {
            var invoker = c.invoker;
            var owner = invoker.owner;
-}}
            - {{ c.opCodeName }}: {{ invoker.returnType }} {{ owner.className }}.{{ invoker.name }}({{ join(invoker.parameters, ", ") }}):{{ c.lineNumber }}
{{#
            if (c.referenceId >= 0) {
                var ref = c.reference;

                if (c.referenceType == "METHOD") {
-}}
                - {{ ref.owner.className }}.{{ ref.name }}({{ join(ref.parameters, ", ") }}) [{{ ref.owner.packageName }}]
{{#
                } else {
-}}
                - {{ ref.owner.className }}.{{ ref.name }} [{{ ref.owner.packageName }}]
{{#
                }
            }
        }
    }
-}}
