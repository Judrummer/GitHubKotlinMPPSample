package com.codingfeline.githubdata

import com.squareup.sqldelight.Query

class UserDataNotifier(
    val onUpdate: (User?) -> Unit
) : QueryNotifier<User> {

    private var query: Query<User>? = null

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            query?.executeAsOneOrNull()?.let(onUpdate)
        }
    }

    override fun updateQuery(newQuery: Query<User>) {
        query?.removeListener(listener)
        query = newQuery
        newQuery.executeAsOneOrNull().let(onUpdate)
    }

    override fun dispose() {
        query?.removeListener(listener)
    }
}
