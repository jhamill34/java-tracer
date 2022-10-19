{{#
    include "native";

    var q = queue();

    offer(q, 1);
    offer(q, 2);
    offer(q, 3);

    for (var i in q) {
-}}
    {{ i }}
{{#
    }
-}}

{{ q }}

{{#
    while (q.size > 0) {
        var n = next(q);

-}}
        {{ n }}
{{#
    }
-}}

{{ q }}
