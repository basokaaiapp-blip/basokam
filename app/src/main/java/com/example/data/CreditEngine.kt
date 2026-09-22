package com.example.data

import com.example.model.AICapability
import com.example.model.AIModel
import com.example.model.CreditWallet

sealed class CreditCheckResult {
    object AllowedFree : CreditCheckResult()
    data class AllowedPaid(val estimatedCredits: Int) : CreditCheckResult()
    data class RequiresConfirmation(val estimatedCredits: Int, val freeAlternative: AIModel?) : CreditCheckResult()
    data class InsufficientBalance(val requiredCredits: Int, val currentBalance: Int, val freeAlternative: AIModel?) : CreditCheckResult()
    data class DailyLimitReached(val limit: Int) : CreditCheckResult()
}

class CreditEngine(
    private var wallet: CreditWallet = CreditWallet()
) {
    fun getWallet(): CreditWallet = wallet

    fun updateWallet(newWallet: CreditWallet) {
        wallet = newWallet
    }

    fun setFreeOnlyMode(enabled: Boolean) {
        wallet = wallet.copy(isFreeOnlyMode = enabled)
    }

    fun setAutoApprovePaid(enabled: Boolean) {
        wallet = wallet.copy(autoApprovePaid = enabled)
    }

    fun checkAllowance(model: AIModel, freeAlternative: AIModel?): CreditCheckResult {
        // Daily limit check
        if (wallet.dailyUsed >= wallet.dailyLimit) {
            return CreditCheckResult.DailyLimitReached(wallet.dailyLimit)
        }

        // If free model or 0 credit
        if (model.isFree || model.creditCost == 0) {
            return CreditCheckResult.AllowedFree
        }

        // If user enabled "Free Only" mode
        if (wallet.isFreeOnlyMode) {
            return if (freeAlternative != null) {
                CreditCheckResult.AllowedFree
            } else {
                CreditCheckResult.InsufficientBalance(model.creditCost, 0, null)
            }
        }

        // Check wallet balance
        if (wallet.balance < model.creditCost) {
            return CreditCheckResult.InsufficientBalance(model.creditCost, wallet.balance, freeAlternative)
        }

        // If auto approve is off, ask for confirmation
        if (!wallet.autoApprovePaid) {
            return CreditCheckResult.RequiresConfirmation(model.creditCost, freeAlternative)
        }

        return CreditCheckResult.AllowedPaid(model.creditCost)
    }

    fun deductCredits(amount: Int): Boolean {
        if (amount <= 0) return true
        if (wallet.balance < amount) return false

        wallet = wallet.copy(
            balance = (wallet.balance - amount).coerceAtLeast(0),
            dailyUsed = wallet.dailyUsed + amount,
            monthlyUsed = wallet.monthlyUsed + amount
        )
        return true
    }

    fun addCredits(amount: Int) {
        wallet = wallet.copy(balance = wallet.balance + amount)
    }
}
