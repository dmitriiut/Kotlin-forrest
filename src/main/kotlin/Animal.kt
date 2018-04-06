import kotlin.math.min

enum class Species(val forage: Forage, val level: Level) {
    SQUIRREL(Forage.CONE, Level.CROWN),
    KITE(Forage.MEAT, Level.CROWN),
    FLYING_SQUIRREL(Forage.MAPLE_LEAF, Level.CROWN),
    WOODPECKER(Forage.WORM, Level.TRUNK),
    CHIPMUNK(Forage.CONE, Level.ROOTS),
    BADGER(Forage.ROOT_VEG, Level.ROOTS),
    WOLF(Forage.MEAT, Level.ROOTS)
}

enum class Forage {
    CONE, MAPLE_LEAF, WORM, MEAT, ROOT_VEG, NOTHING
}

enum class Level {
    CROWN, TRUNK, ROOTS
}

class Animal(val species: Species, var tree: Tree, private val sex: Boolean) {
    var satiety = 10
    var isTired = false
    fun action() {
        satiety -= 1
        isTired = true
        if (satiety > 8 && lookForPartner())
            println(species.toString() + " was born on turn " + Forest.cnt)
        else
            lookForFood()
    }

    private fun lookForFood(cnt: Int = 0) {
        when (species) {
            Species.KITE -> {
                val meat = this.tree.crown.animals.shuffled().find { t -> t.species != Species.KITE }
                if (meat == null) {
                    val nextTree = move()
                    if (nextTree == tree || cnt > 3)
                        return
                    tree = nextTree
                    lookForFood(cnt + 1)
                    return
                }
                if (Forest.rnd.nextBoolean()) {
                    this.satiety = min(10, satiety + 3)
                    Forest.forest.animals.remove(meat)
                    tree.crown.animals.remove(meat)
                    println(meat.species.toString() + "was killed by Kite on turn " + Forest.cnt)
                }
            }
            Species.WOLF -> {
                val meat = this.tree.roots.animals.shuffled().find { t -> t.species != Species.WOLF }
                if (meat == null) {
                    val nextTree = move()
                    if (nextTree == tree || cnt > 3)
                        return
                    tree = nextTree
                    lookForFood(cnt + 1)
                    return
                }
                if (Forest.rnd.nextBoolean()) {
                    this.satiety = min(10, satiety + 4)
                    Forest.forest.animals.remove(meat)
                    tree.roots.animals.remove(meat)
                    println(meat.species.toString() + "was killed by Wolf on turn " + Forest.cnt)
                }
            }
            else -> {
                val food = when (species.level) {
                    Level.CROWN ->
                        checkFoodTypes(tree.crown)
                    Level.TRUNK ->
                        checkFoodTypes(tree.trunk)
                    Level.ROOTS ->
                        checkFoodTypes(tree.roots)
                }
                if (food != -1) {
                    satiety += food
                    when (species.level) {
                        Level.CROWN ->
                            tree.crown.food -= food
                        Level.TRUNK ->
                            tree.trunk.food -= food
                        Level.ROOTS ->
                            tree.crown.food -= food
                    }
                    return
                } else {
                    val nextTree = move()
                    if (nextTree == tree || cnt > 10)
                        return
                    tree = nextTree
                    lookForFood(cnt + 1)
                }
            }
        }
    }

    private fun checkFoodTypes(treePart: TreePart): Int =
            if (this.species.forage == treePart.foodType)
                min(10 - this.satiety, min(treePart.food,3))
            else
                -1

    private fun move(): Tree {
        when (species.level) {
            Level.CROWN -> {
                removeFromPart(tree.crown)
                val next = tree.neighbors.toMutableList().shuffled()
                var nextTree = tree
                next.forEach { t ->
                    if (Forest.forest!!.trees[t].crown.animals.size < tree.crown.capacity) {
                        nextTree = Forest.forest!!.trees[t]
                        return@forEach
                    }
                }
                addToPart(nextTree.crown)
                return nextTree
            }
            Level.TRUNK -> {
                removeFromPart(tree.trunk)
                val next = tree.neighbors.toMutableList().shuffled()
                var nextTree = tree
                next.forEach { t ->
                    if (Forest.forest!!.trees[t].trunk.animals.size < tree.trunk.capacity) {
                        nextTree = Forest.forest!!.trees[t]
                        return@forEach
                    }
                }
                addToPart(nextTree.trunk)
                return nextTree
            }
            Level.ROOTS -> {
                removeFromPart(tree.roots)
                val next = tree.neighbors.toMutableList().shuffled()
                var nextTree = tree
                next.forEach { t ->
                    if (Forest.forest!!.trees[t].roots.animals.size < tree.roots.capacity) {
                        nextTree = Forest.forest!!.trees[t]
                        return@forEach
                    }
                }
                addToPart(nextTree.roots)
                return nextTree
            }
        }
    }

    private fun addToPart(treePart: TreePart) {
        treePart.animals.add(this)
    }

    private fun removeFromPart(treePart: TreePart) {
        treePart.animals.remove(this)
    }

    private fun lookForPartner(): Boolean {
        return when (species.level) {
            Level.CROWN -> {
                findPartnerInTreePart(tree.crown)
            }
            Level.TRUNK -> {
                findPartnerInTreePart(tree.trunk)
            }
            Level.ROOTS -> {
                findPartnerInTreePart(tree.roots)
            }
        }
    }

    private fun findPartnerInTreePart(treePart: TreePart): Boolean {
        val partner = treePart.animals.shuffled().find {
            !it.isTired && it.satiety > 7
                    && it.sex != this.sex
                    && treePart.animals.size < treePart.capacity
        }
                ?: return false
        partner.isTired = true
        this.isTired = true
        val child = Animal(this.species, this.tree, Forest.rnd.nextBoolean())
        child.isTired = true
        treePart.animals.add(child)
        Forest.forest.animals.add(child)
        return true
    }
}
