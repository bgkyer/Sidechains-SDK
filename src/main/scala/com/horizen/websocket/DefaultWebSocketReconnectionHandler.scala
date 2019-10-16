package com.horizen.websocket

import scorex.util.ScorexLogging
import com.horizen.WebSocketConnectorConfiguration

import scala.concurrent.duration.FiniteDuration

class DefaultWebSocketReconnectionHandler(conf: WebSocketConnectorConfiguration) extends WebSocketReconnectionHandler with ScorexLogging {

  var onConnectFailureCounter = 0

  override def onConnectionFailed(cause: Throwable): Boolean = {
    onConnectFailureCounter = onConnectFailureCounter + 1
    if (onConnectFailureCounter <= conf.reconnectionMaxAttempts) {
      log.info("onConnectFailure. Reconnecting... (attempt " + onConnectFailureCounter + ") " + cause.getMessage)
      true
    }
    else false
  }

  override def onDisconnection(code: DisconnectionCode.Value, reason: String): Boolean = {
    onConnectFailureCounter = onConnectFailureCounter + 1
    if (onConnectFailureCounter <= conf.reconnectionMaxAttempts && code != DisconnectionCode.ON_SUCCESS) {
      log.info("onDisconnect. Reconnecting... (attempt " + onConnectFailureCounter + ")")
      true
    } else false
  }

  override def getDelay: FiniteDuration = conf.reconnectionDelay

  override def onConnectionSuccess(): Unit = onConnectFailureCounter = 0

}
