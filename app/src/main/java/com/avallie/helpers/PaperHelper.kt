package com.avallie.helpers

import com.avallie.model.ConstructionPhase
import io.paperdb.Paper

class PaperHelper {

    companion object {
        fun savePhases(phases: ArrayList<ConstructionPhase>){
            Paper.book().write("phases", phases)
        }

        fun getPhases() : ArrayList<ConstructionPhase>{
            return Paper.book().read("phases")
        }

        fun hasPhases(): Boolean{
            return Paper.book().contains("phases")
        }
    }

}