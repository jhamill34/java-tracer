{{#
    include "native";

    fun accessString(aList) {
        var result = "";

        for (var access in aList) {
            if (result != "") {
                result = result + " ";
            }

            result = result + access;
        }

        return result;
    }

    var classes = allClasses();
    for (var class in classes) {
-}}
    {{ accessString(class.accessList) }} {{ class.className }}
{{#
    }
-}}
