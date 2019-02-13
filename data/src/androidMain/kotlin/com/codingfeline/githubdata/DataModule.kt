package com.codingfeline.githubdata

import android.content.Context
import com.codingfeline.githubdata.local.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.eagerSingleton
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import kotlin.coroutines.CoroutineContext

internal fun appModule(context: Context): Kodein.Module {
    return Kodein.Module(name = "app") {
        bind<Context>() with instance(context)
        bind<SqlDriver>() with eagerSingleton { AndroidSqliteDriver(Database.Schema, instance(), null) }
        bind<CoroutineContext>(tag = "uicontext") with provider { Dispatchers.Main }
    }
}


fun initKodein(context: Context): Kodein {
    return Kodein {
        import(remoteModule)
        import(localModule)
        import(dataModule)
        import(appModule(context))
    }
}
