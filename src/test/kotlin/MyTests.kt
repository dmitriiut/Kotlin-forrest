import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec


class MyTest : FunSpec() {
    init {
        test("Проверка на создание деревьев") {
            Forest.forest = Forest()
            Forest.forest.createTrees(10)
            (Forest.forest.trees.size) shouldBe 10
        }
        test("Проверка на различные деревья"){
            Forest.forest = Forest()
            Forest.forest.createTrees(15)
            val a = mutableListOf(Forest.forest.trees)
            Forest.forest.trees.clear()
            Forest.forest.createTrees(15)

            a.equals(Forest.forest.trees) shouldBe (false)
        }
        test("Проверка еды"){
            Forest.forest = Forest()
            Forest.forest.createTrees(1)
            val food = Forest.forest.trees[0].trunk.food
            for (i in 0..50)
                Forest.forest.tick()
            (food < Forest.forest.trees[0].trunk.food) shouldBe true
        }
        test("Звери создаются"){
            Forest.forest = Forest()
            Forest.forest.createTrees(1)
            Forest.forest.createAnimals("",Species.CHIPMUNK,1,true,6)
            (Forest.forest.animals.size) shouldBe 6
        }
    }
}