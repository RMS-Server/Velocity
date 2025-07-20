/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.proxy.player;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents bandwidth statistics for a player connected to the proxy.
 * This interface provides detailed information about a player's network usage.
 */
public interface PlayerBandwidthStats {

  /**
   * Gets the UUID of the player these statistics belong to.
   *
   * @return the player's UUID
   */
  UUID getPlayerUuid();

  /**
   * Gets the username of the player these statistics belong to.
   *
   * @return the player's username
   */
  String getPlayerUsername();

  /**
   * Gets the total number of bytes sent to the player since tracking began.
   *
   * @return total bytes sent (downstream)
   */
  long getTotalBytesSent();

  /**
   * Gets the total number of bytes received from the player since tracking began.
   *
   * @return total bytes received (upstream)
   */
  long getTotalBytesReceived();

  /**
   * Gets the current download speed in bytes per second.
   * This is typically calculated as an average over the last update interval.
   *
   * @return download speed in bytes/second
   */
  double getDownloadSpeed();

  /**
   * Gets the current upload speed in bytes per second.
   * This is typically calculated as an average over the last update interval.
   *
   * @return upload speed in bytes/second
   */
  double getUploadSpeed();

  /**
   * Gets the peak download speed recorded since tracking began.
   *
   * @return peak download speed in bytes/second
   */
  double getPeakDownloadSpeed();

  /**
   * Gets the peak upload speed recorded since tracking began.
   *
   * @return peak upload speed in bytes/second
   */
  double getPeakUploadSpeed();

  /**
   * Gets the average download speed since tracking began.
   *
   * @return average download speed in bytes/second
   */
  double getAverageDownloadSpeed();

  /**
   * Gets the average upload speed since tracking began.
   *
   * @return average upload speed in bytes/second
   */
  double getAverageUploadSpeed();

  /**
   * Gets the timestamp when bandwidth tracking started for this player.
   *
   * @return the start time of tracking
   */
  Instant getTrackingStartTime();

  /**
   * Gets the timestamp of the last update to these statistics.
   *
   * @return the last update time
   */
  Instant getLastUpdateTime();

  /**
   * Gets the duration in milliseconds that this player has been tracked.
   *
   * @return tracking duration in milliseconds
   */
  long getTrackingDurationMs();

  /**
   * Gets the number of packets sent to the player since tracking began.
   *
   * @return total packets sent
   */
  long getTotalPacketsSent();

  /**
   * Gets the number of packets received from the player since tracking began.
   *
   * @return total packets received
   */
  long getTotalPacketsReceived();

  /**
   * Gets the current packet rate for outgoing packets (packets per second).
   *
   * @return outgoing packet rate in packets/second
   */
  double getOutgoingPacketRate();

  /**
   * Gets the current packet rate for incoming packets (packets per second).
   *
   * @return incoming packet rate in packets/second
   */
  double getIncomingPacketRate();

  /**
   * Checks if the player is currently connected and being tracked.
   *
   * @return true if the player is currently connected and tracked
   */
  boolean isActive();

  /**
   * Gets the player's current server name, if connected to a backend server.
   *
   * @return the server name, or null if not connected to a backend
   */
  String getCurrentServerName();
}