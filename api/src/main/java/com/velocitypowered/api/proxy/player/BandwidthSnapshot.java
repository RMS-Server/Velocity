/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.proxy.player;

import java.time.Instant;

/**
 * Represents a snapshot of bandwidth statistics across the entire proxy.
 * This interface provides aggregated bandwidth information for all connected players.
 */
public interface BandwidthSnapshot {

  /**
   * Gets the timestamp when this snapshot was taken.
   *
   * @return the snapshot timestamp
   */
  Instant getSnapshotTime();

  /**
   * Gets the total number of players currently being tracked.
   *
   * @return the number of tracked players
   */
  int getTrackedPlayerCount();

  /**
   * Gets the total number of bytes sent to all players.
   *
   * @return total bytes sent across all players
   */
  long getTotalBytesSent();

  /**
   * Gets the total number of bytes received from all players.
   *
   * @return total bytes received across all players
   */
  long getTotalBytesReceived();

  /**
   * Gets the current total download speed across all players in bytes per second.
   *
   * @return total download speed in bytes/second
   */
  double getTotalDownloadSpeed();

  /**
   * Gets the current total upload speed across all players in bytes per second.
   *
   * @return total upload speed in bytes/second
   */
  double getTotalUploadSpeed();

  /**
   * Gets the average download speed per player in bytes per second.
   *
   * @return average download speed per player in bytes/second
   */
  double getAverageDownloadSpeedPerPlayer();

  /**
   * Gets the average upload speed per player in bytes per second.
   *
   * @return average upload speed per player in bytes/second
   */
  double getAverageUploadSpeedPerPlayer();

  /**
   * Gets the peak total download speed recorded since tracking began.
   *
   * @return peak total download speed in bytes/second
   */
  double getPeakTotalDownloadSpeed();

  /**
   * Gets the peak total upload speed recorded since tracking began.
   *
   * @return peak total upload speed in bytes/second
   */
  double getPeakTotalUploadSpeed();

  /**
   * Gets the total number of packets sent to all players.
   *
   * @return total packets sent across all players
   */
  long getTotalPacketsSent();

  /**
   * Gets the total number of packets received from all players.
   *
   * @return total packets received across all players
   */
  long getTotalPacketsReceived();

  /**
   * Gets the current total outgoing packet rate across all players.
   *
   * @return total outgoing packet rate in packets/second
   */
  double getTotalOutgoingPacketRate();

  /**
   * Gets the current total incoming packet rate across all players.
   *
   * @return total incoming packet rate in packets/second
   */
  double getTotalIncomingPacketRate();

  /**
   * Gets the timestamp when bandwidth tracking started.
   *
   * @return the tracking start time
   */
  Instant getTrackingStartTime();

  /**
   * Gets the duration in milliseconds that bandwidth tracking has been active.
   *
   * @return tracking duration in milliseconds
   */
  long getTrackingDurationMs();
}