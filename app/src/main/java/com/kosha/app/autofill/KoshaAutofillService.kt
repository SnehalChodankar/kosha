package com.kosha.app.autofill

import android.app.PendingIntent
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.view.autofill.AutofillId
import android.widget.RemoteViews
import com.kosha.app.R

class KoshaAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val context = request.fillContexts.last()
        val structure = context.structure

        val usernameIds = mutableListOf<AutofillId>()
        val passwordIds = mutableListOf<AutofillId>()
        traverseStructure(structure.getWindowNodeAt(0).rootViewNode, usernameIds, passwordIds)

        if (usernameIds.isEmpty() && passwordIds.isEmpty()) {
            callback.onSuccess(null)
            return
        }

        val packageName = structure.activityComponent.packageName
        var webDomain = ""
        // Try to find web domain in the root node
        val rootNode = structure.getWindowNodeAt(0).rootViewNode
        val domain = rootNode.webDomain
        if (domain != null) {
            webDomain = domain
        }

        val presentation = RemoteViews(getPackageName(), android.R.layout.simple_list_item_1).apply {
            setTextViewText(android.R.id.text1, "Unlock Kosha")
        }

        val authIntent = Intent(this, AutofillAuthActivity::class.java).apply {
            putExtra("package_name", packageName)
            putExtra("web_domain", webDomain)
            putParcelableArrayListExtra("username_ids", ArrayList(usernameIds))
            putParcelableArrayListExtra("password_ids", ArrayList(passwordIds))
        }

        val intentSender = PendingIntent.getActivity(
            this,
            1001,
            authIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        ).intentSender

        val dataset = android.service.autofill.Dataset.Builder()
            .setAuthentication(intentSender)
        
        usernameIds.forEach { id -> dataset.setValue(id, null, presentation) }
        passwordIds.forEach { id -> dataset.setValue(id, null, presentation) }

        val response = FillResponse.Builder()
            .addDataset(dataset.build())
            .build()

        callback.onSuccess(response)
    }

    override fun onSaveRequest(request: android.service.autofill.SaveRequest, callback: android.service.autofill.SaveCallback) {
        callback.onSuccess()
    }

    private fun traverseStructure(node: android.app.assist.AssistStructure.ViewNode, usernameIds: MutableList<AutofillId>, passwordIds: MutableList<AutofillId>) {
        val autofillId = node.autofillId
        if (autofillId != null) {
            if (isPasswordField(node)) {
                passwordIds.add(autofillId)
            } else if (isUsernameField(node)) {
                usernameIds.add(autofillId)
            }
        }
        for (i in 0 until node.childCount) {
            traverseStructure(node.getChildAt(i), usernameIds, passwordIds)
        }
    }

    private fun isPasswordField(node: android.app.assist.AssistStructure.ViewNode): Boolean {
        val hints = node.autofillHints
        if (hints != null) {
            for (hint in hints) {
                if (hint.lowercase().contains("password")) return true
            }
        }
        val inputType = node.inputType
        if (inputType != 0) {
            val variation = inputType and android.text.InputType.TYPE_MASK_VARIATION
            if (variation == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD) {
                return true
            }
        }
        return false
    }

    private fun isUsernameField(node: android.app.assist.AssistStructure.ViewNode): Boolean {
        val hints = node.autofillHints
        if (hints != null) {
            for (hint in hints) {
                val lowerHint = hint.lowercase()
                if (lowerHint.contains("username") || lowerHint.contains("email") || lowerHint.contains("phone")) return true
            }
        }
        if (node.className?.contains("EditText") == true) return true
        return false
    }
}
