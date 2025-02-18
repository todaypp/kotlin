/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.scopes.impl

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.scopes.FirContainingNamesAwareScope
import org.jetbrains.kotlin.fir.scopes.FirOverrideChecker
import org.jetbrains.kotlin.fir.scopes.FirTypeScope
import org.jetbrains.kotlin.fir.scopes.ProcessorAction
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.Name

abstract class AbstractFirUseSiteMemberScope(
    session: FirSession,
    overrideChecker: FirOverrideChecker,
    protected val superTypesScope: FirTypeScope,
    protected val declaredMemberScope: FirContainingNamesAwareScope
) : AbstractFirOverrideScope(session, overrideChecker) {

    private val functions = hashMapOf<Name, Collection<FirNamedFunctionSymbol>>()
    private val properties = hashMapOf<Name, Collection<FirVariableSymbol<*>>>()
    val directOverriddenFunctions = hashMapOf<FirNamedFunctionSymbol, Collection<FirNamedFunctionSymbol>>()
    protected val directOverriddenProperties = hashMapOf<FirPropertySymbol, MutableList<FirPropertySymbol>>()

    private val callableNamesCached by lazy(LazyThreadSafetyMode.PUBLICATION) {
        declaredMemberScope.getCallableNames() + superTypesScope.getCallableNames()
    }

    override fun processFunctionsByName(name: Name, processor: (FirNamedFunctionSymbol) -> Unit) {
        functions.getOrPut(name) {
            doProcessFunctions(name)
        }.forEach {
            processor(it)
        }
    }

    private fun doProcessFunctions(
        name: Name
    ): Collection<FirNamedFunctionSymbol> = mutableListOf<FirNamedFunctionSymbol>().apply {
        val overrideCandidates = mutableSetOf<FirFunctionSymbol<*>>()
        declaredMemberScope.processFunctionsByName(name) { symbol ->
            if (symbol.isStatic) return@processFunctionsByName
            val directOverridden = computeDirectOverridden(symbol)
            this@AbstractFirUseSiteMemberScope.directOverriddenFunctions[symbol] = directOverridden
            overrideCandidates += symbol
            add(symbol)
        }

        superTypesScope.processFunctionsByName(name) {
            val overriddenBy = it.getOverridden(overrideCandidates)
            if (overriddenBy == null) {
                add(it)
            }
        }
    }

    final override fun processPropertiesByName(name: Name, processor: (FirVariableSymbol<*>) -> Unit) {
        properties.getOrPut(name) {
            doProcessProperties(name)
        }.forEach {
            processor(it)
        }
    }

    protected abstract fun doProcessProperties(name: Name): Collection<FirVariableSymbol<*>>

    private fun computeDirectOverridden(symbol: FirNamedFunctionSymbol): Collection<FirNamedFunctionSymbol> {
        val result = mutableListOf<FirNamedFunctionSymbol>()
        val firSimpleFunction = symbol.fir
        superTypesScope.processFunctionsByName(symbol.callableId.callableName) { superSymbol ->
            if (overrideChecker.isOverriddenFunction(firSimpleFunction, superSymbol.fir)) {
                result.add(superSymbol)
            }
        }

        return result
    }

    override fun processDirectOverriddenFunctionsWithBaseScope(
        functionSymbol: FirNamedFunctionSymbol,
        processor: (FirNamedFunctionSymbol, FirTypeScope) -> ProcessorAction
    ): ProcessorAction =
        //directOverriddenFunctions might be not filled for functionSymbol if it is not from processFunctionsByName call
        doProcessDirectOverriddenCallables(
            functionSymbol, processor, directOverriddenFunctions, superTypesScope,
            FirTypeScope::processDirectOverriddenFunctionsWithBaseScope
        )

    override fun processDirectOverriddenPropertiesWithBaseScope(
        propertySymbol: FirPropertySymbol,
        processor: (FirPropertySymbol, FirTypeScope) -> ProcessorAction
    ): ProcessorAction =
        doProcessDirectOverriddenCallables(
            propertySymbol, processor, directOverriddenProperties, superTypesScope,
            FirTypeScope::processDirectOverriddenPropertiesWithBaseScope
        )

    override fun processClassifiersByNameWithSubstitution(name: Name, processor: (FirClassifierSymbol<*>, ConeSubstitutor) -> Unit) {
        declaredMemberScope.processClassifiersByNameWithSubstitution(name, processor)
        superTypesScope.processClassifiersByNameWithSubstitution(name, processor)
    }

    override fun processDeclaredConstructors(processor: (FirConstructorSymbol) -> Unit) {
        declaredMemberScope.processDeclaredConstructors(processor)
    }

    override fun getCallableNames(): Set<Name> = callableNamesCached

    override fun getClassifierNames(): Set<Name> {
        return declaredMemberScope.getClassifierNames() + superTypesScope.getClassifierNames()
    }
}
