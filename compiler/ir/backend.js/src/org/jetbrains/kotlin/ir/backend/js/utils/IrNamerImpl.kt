/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.utils

import org.jetbrains.kotlin.ir.backend.js.JsIrBackendContext
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.js.backend.ast.JsName

class IrNamerImpl(private val newNameTables: NameTables) : IrNamerBase() {
    override fun getNameForStaticDeclaration(declaration: IrDeclarationWithName): JsName =
        newNameTables.getNameForStaticDeclaration(declaration).toJsName()

    override fun isDeclarationEliminated(declaration: IrDeclarationWithName): Boolean {
        return !newNameTables.globalNames.names.containsKey(declaration)
    }

    override fun getNameForMemberFunction(function: IrSimpleFunction): JsName {
        require(function.dispatchReceiverParameter != null)
        val name = jsFunctionSignature(function)
        return name.toJsName()
    }

    override fun getNameForMemberField(field: IrField): JsName {
        require(!field.isStatic)
        return newNameTables.getNameForMemberField(field).toJsName()
    }
}
