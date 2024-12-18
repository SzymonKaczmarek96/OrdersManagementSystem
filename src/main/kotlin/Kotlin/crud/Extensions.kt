fun <T> T.updateIf(condition: Boolean, update: T.() -> Unit) {
    if (condition) update()
}