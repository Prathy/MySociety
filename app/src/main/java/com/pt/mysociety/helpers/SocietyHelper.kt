package com.pt.mysociety.helpers

class SocietyHelper {

    fun getWings(): Array<String> {
        return arrayOf("A", "B", "C")
    }

    fun getFloors(): Array<String> {
        var floors: Array<String> = arrayOf()
        for(i in 1..17){
            floors = floors.plusElement("$i")
        }
        return floors
    }

    fun getHouse(wing: String): Array<String> {
        var houses: Array<String> = arrayOf()
        val houseOnFloor = if( wing == "C") 2 else if(wing == "B") 4 else 12
        for(i in 1..houseOnFloor){
            houses = houses.plusElement("$i")
        }
        return houses
    }
}