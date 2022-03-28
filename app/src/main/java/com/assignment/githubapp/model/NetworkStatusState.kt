package com.assignment.githubapp.model

/**
 * State hierarchy for different Network Status connections
 */
sealed class NetworkStatusState {

    /* Device has a valid internet connection */
    object NetworkStatusConnected : NetworkStatusState()

    /* Device has no internet connection */
    object NetworkStatusDisconnected : NetworkStatusState()
}