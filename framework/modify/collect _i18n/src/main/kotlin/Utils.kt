fun CharSequence.indexOfFunEnd(start: Int = 0): Int {
    var count = 1
    for (index in start until length) {
        if (get(index)== ')') count--
        else if (get(index)== '(') count++
        if (count == 0) return index
    }
    return -1
}
