import java.util.*

enum class TreesTypes(val forage: Forage) {
    FIR(Forage.CONE),
    PINE(Forage.CONE),
    OAK(Forage.NOTHING),
    BIRCH(Forage.NOTHING),
    MAPLE(Forage.MAPLE_LEAF),
    WALNUT(Forage.CONE)
}

class Tree {
    var neighbors: Array<Int> = arrayOf()

    constructor(treeType: TreesTypes) {
        this.treeType = treeType
        when (treeType) {
            TreesTypes.FIR, TreesTypes.PINE, TreesTypes.WALNUT -> {
                crown.food = 10
                crown.foodType = Forage.CONE
                roots.foodType = Forage.ROOT_VEG

            }
            TreesTypes.MAPLE -> {
                crown.food = 10
                crown.foodType = Forage.MAPLE_LEAF
            }
        }
    }

    fun generateFood(){
        crown.generateFood()
        trunk.generateFood()
        roots.generateFood()
    }
    var treeType = TreesTypes.PINE
    val crown = Crown()
    val trunk = Trunk()
    val roots = Roots()
}

class Crown : TreePart() {
    override val capacity: Int = 12
    override var foodType: Forage = Forage.NOTHING
}

class Trunk : TreePart {
    constructor(){
        food = 1
    }
    override val capacity: Int = 5
    override var foodType = Forage.WORM
    override fun generateFood() {
        if (foodType != Forage.NOTHING && Forest.rnd.nextInt(10) > 3) {
            food += Forest.rnd.nextInt(10) + 2
        }
    }
}

class Roots : TreePart() {
    override val capacity: Int = 9
    override var foodType = Forage.ROOT_VEG
}

abstract class TreePart {
    open val capacity = 0
    var food = 0
    abstract var foodType : Forage
    var animals: MutableList<Animal> = mutableListOf()
    open fun generateFood() {
        if (foodType != Forage.NOTHING && Forest.rnd.nextInt(10) > 3) {
            food += Forest.rnd.nextInt(15) + 3
        }
    }
}