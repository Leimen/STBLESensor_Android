/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.preferences

import kotlinx.coroutines.flow.Flow

interface StPreferences {

    fun mustShowFwUpdate(nodeId: String, currentFw: String): Boolean

    fun doNotShowAgainFwUpdate(nodeId: String, currentFw: String)

    fun termsPrefsUpdates(): Flow<Boolean>

    fun hasAcceptedTerms(): Boolean

    fun getLevelProficiency(): String

    fun getProfileType(): String

    fun setTermsFlag(accepted: Boolean)

    fun setLevelProficiency(level: String)

    fun setProfileType(profile: String)

    fun welcomePrefsUpdates(): Flow<Boolean>

    fun mustShowWelcome(): Boolean

    fun setWelcomeFlag(completed: Boolean)

    fun setBetaApplicationFlag(enableBeta: Boolean)

    fun isBetaApplication(): Boolean

    fun setDisableHiddenDemos(disableHiddenDemos: Boolean)

    fun isDisableHiddenDemos(): Boolean

    fun isServerForced(): Boolean

    fun setServerForcedFlag(serverForced: Boolean)

    fun profilePrefsUpdates(): Flow<Boolean>

    fun hasSelectedProfile(): Boolean

    fun setProfileFlag(selected: Boolean)

    fun getFavouriteDevices(): Flow<List<String>>

    fun unsetFavouriteDevice(nodeId: String)

    fun setFavouriteDevice(nodeId: String)

    fun getDemoOrder(nodeId: String): List<String>

    fun setDemoOrder(nodeId: String, demos: List<String>)

    fun setConfiguredAzureCloudApp(cloudAppUrl: String, serializedString: String)

    fun getConfiguredAzureCloudApp(cloudAppUrl: String): String?

    fun deleteConfiguredAzureCloudApp(cloudAppUrl: String)

    fun setConfiguredMqttCloudApp(serializedString: String)

    fun getConfiguredMqttCloudApp(): String?

    fun deleteConfiguredMqttCloudApp()

    fun setBoardCatalogStatus(serializedString: String)

    fun getBoardCatalogStatus(): String?

    //Custom Entries
    fun setCustomStringForKey(key: String, serializedString: String)

    fun getCustomStringFromKey(key: String) : String?

    fun deleteCustomStringFromKey(key: String)

    fun setCustomBooleanForKey(key: String, value: Boolean)

    fun getCustomBooleanFromKey(key: String) : Boolean

    fun deleteCustomBooleanFromKey(key: String)
}
