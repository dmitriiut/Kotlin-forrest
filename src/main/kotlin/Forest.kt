import java.util.*
import kotlin.concurrent.timerTask

class Forest  {
    val trees: MutableList<Tree> = mutableListOf()
    val animals: MutableList<Animal> = mutableListOf()
    fun tick() {
        Forest.cnt++
        trees.forEach { t -> t.generateFood() }

        animals.forEach({ t -> t.isTired = false })
        animals.shuffled().forEach { t ->
            if (t.satiety == 0) {
                println(t.species.toString() + " died from hunger on turn " + cnt)
                animals.remove(t)
            } else if (!t.isTired)
                t.action()
        }
    }
    private fun printAll(){
        println(animals.size)
        println("\tБелок: \t" + animals.filter { it.species == Species.SQUIRREL }.size)
        println("\tКоршунов: \t" + animals.filter { it.species == Species.KITE }.size)
        println("\tЛетяг: \t" + animals.filter { it.species == Species.FLYING_SQUIRREL }.size)
        println("\tДятлов: \t" + animals.filter { it.species == Species.WOODPECKER }.size)
        println("\tБурундуков: \t" + animals.filter { it.species == Species.CHIPMUNK }.size)
        println("\tБарсуков: \t" + animals.filter { it.species == Species.BADGER }.size)
        println("\tВолков: \t" + animals.filter { it.species == Species.WOLF }.size)

    }
    companion object {
        lateinit var forest: Forest
        var rnd = Random()
        var cnt = 0

        @JvmStatic
        fun main(args: Array<String>) {
            Forest.forest = Forest()

            print("Введите число деревьев: ")
            val nTrees = readLine()!!.toInt()
            Forest.forest.createTrees(nTrees)

            println("Введите числа ")
            val map = mapOf(
                    Pair("белок", Species.SQUIRREL),
                    Pair("летяг", Species.FLYING_SQUIRREL),
                    Pair("дятлов", Species.WOODPECKER),
                    Pair("бурундуков", Species.CHIPMUNK),
                    Pair("барсуков", Species.BADGER),
                    Pair("коршунов", Species.KITE),
                    Pair("волков", Species.WOLF)
            )
            map.forEach { Forest.forest.createAnimals(it.key, it.value, nTrees) }
            val t = Timer()
            t.schedule(timerTask {
                if (Forest.forest.animals.size <= 0)
                    return@timerTask
                Forest.forest.tick()
                Forest.forest.printAll()
            },0,2000)
        }
    }

    fun createAnimals(name: String, species: Species, nTrees: Int, isTest: Boolean = false, testN: Int = 4 ) {
        val n: Int
        if (!isTest) {
            print("\t$name: ")
            n = readLine()!!.toInt()
        }
        else
            n = testN
        for (i in 0 until n) {
            this.animals.add(Animal(species, trees[chooseTree(nTrees, species.level, trees)], i % 2 == 0))
        }
    }
    fun createTrees(nTrees: Int){
        val rnd = Random()
        for (i in 0 until nTrees) {
            when (rnd.nextInt(12)) {
                0, 1 -> Forest.forest.trees.add(Tree(TreesTypes.MAPLE))
                2 -> Forest.forest.trees.add(Tree(TreesTypes.WALNUT))
                3 -> Forest.forest.trees.add(Tree(TreesTypes.FIR))
                5 -> Forest.forest.trees.add(Tree(TreesTypes.OAK))
                6 -> Forest.forest.trees.add(Tree(TreesTypes.BIRCH))
                else -> Forest.forest.trees.add(Tree(TreesTypes.PINE))
            }
            Forest.forest.trees[i].neighbors = generateNeighbors(i, nTrees)
        }
    }
}


fun chooseTree(n: Int, level: Level, trees: MutableList<Tree>): Int {
    val rnd = Random()
    var i = rnd.nextInt(n)
    when (level) {
        Level.CROWN -> {
            while (trees[i].crown.animals.size >= trees[i].roots.capacity)
                i = rnd.nextInt(n)
        }
        Level.ROOTS -> {
            while (trees[i].roots.animals.size >= trees[i].roots.capacity)
                i = rnd.nextInt(n)
        }
        Level.TRUNK -> {
            while (trees[i].trunk.animals.size >= trees[i].trunk.capacity)
                i = rnd.nextInt(n)
        }
    }
    return i
}

fun generateNeighbors(i: Int, n: Int): Array<Int> {
    val rnd = Random()
    val ans = mutableSetOf<Int>()
    for (j in 0 until n) {
        if (j != i && rnd.nextDouble() > 0.6)
            ans.add(j)
    }
    return ans.toTypedArray()
}
