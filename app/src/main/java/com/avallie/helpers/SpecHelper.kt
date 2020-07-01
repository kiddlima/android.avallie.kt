package com.avallie.helpers

import com.avallie.model.Spec

class SpecHelper {
    companion object {
        fun createSpecs(rawSpecs: String): MutableList<Spec> {
            val rawArray = rawSpecs.split(";")
            val specsArray = mutableListOf<Spec>()

            for (rawSpec in rawArray) {
                val spec = Spec()

                val splittedSpec = rawSpec.split(":")

                spec.question = splittedSpec[0]
                if (splittedSpec.size > 1) {
                    if (!splittedSpec[1].isBlank()) {
                        spec.answer = splittedSpec[1]
                    }
                }

                specsArray.add(spec)
            }

            return specsArray
        }

        fun formattedSpecsString(specs: MutableList<Spec>): String {
            var formattedSpecs = ""

            for (spec in specs) {
                if (spec.answer != null) {
                    formattedSpecs = "${formattedSpecs}${spec.question}: ${spec.answer}\n"
                }
            }

            return formattedSpecs
        }

    }


}