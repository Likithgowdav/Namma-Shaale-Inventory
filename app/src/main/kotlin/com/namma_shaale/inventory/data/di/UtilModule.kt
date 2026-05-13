package com.namma_shaale.inventory.data.di

import android.content.Context
import com.namma_shaale.inventory.data.util.PdfReportGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Singleton
    @Provides
    fun providePdfReportGenerator(
        @ApplicationContext context: Context
    ): PdfReportGenerator {
        return PdfReportGenerator(context)
    }
}
