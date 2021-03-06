package com.codingfeline.github

import co.touchlab.sqliter.DatabaseConfiguration
import com.codingfeline.github.data.GitHubRepositoryIos
import com.codingfeline.github.data.MainLoopDispatcher
import com.codingfeline.github.data.dataModule
import com.codingfeline.github.data.local.Database
import com.codingfeline.github.data.localModule
import com.codingfeline.github.data.remoteModule
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import com.squareup.sqldelight.drivers.ios.wrapConnection
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton
import kotlin.coroutines.CoroutineContext

internal fun appModule(): Kodein.Module {
    return Kodein.Module(name = "app") {
        bind<SqlDriver>() with singleton {
            //NativeSqliteDriver(Database.Schema, null)
            NativeSqliteDriver(
                configuration = DatabaseConfiguration(
                    name = "memorydb",
                    version = Database.Schema.version,
                    create = { connection ->
                        wrapConnection(connection) { Database.Schema.create(it) }
                    },
                    upgrade = { connection, oldVersion, newVersion ->
                        wrapConnection(connection) { Database.Schema.migrate(it, oldVersion, newVersion) }
                    }
                )
            )
        }
        bind<CoroutineContext>(tag = Tags.UI_CONTEXT) with provider { MainLoopDispatcher }
        bind<CoroutineContext>(tag = Tags.BG_CONTEXT) with provider { MainLoopDispatcher }
    }
}

fun initKodein(): Kodein {
    return Kodein {
        import(remoteModule)
        import(localModule)
        import(dataModule)
        import(appModule())
    }
}

fun getGitHubRepository(kodein: Kodein): GitHubRepositoryIos {
    return kodein.direct.instance()
}
