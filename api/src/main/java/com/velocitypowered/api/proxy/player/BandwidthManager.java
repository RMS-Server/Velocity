/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.proxy.player;

import com.velocitypowered.api.proxy.Player;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages bandwidth statistics for players connected to the proxy.
 * This interface provides methods to track and retrieve bandwidth usage
 * information for individual players and the entire proxy.
 */
public interface BandwidthManager {

  /**
   * Gets the bandwidth statistics for a specific player.
   *
   * @param player the player to get bandwidth stats for
   * @return an Optional containing the player's bandwidth stats, empty if not available
   */
  Optional<PlayerBandwidthStats> getPlayerBandwidthStats(Player player);

  /**
   * Gets the bandwidth statistics for a specific player by UUID.
   *
   * @param uuid the UUID of the player to get bandwidth stats for
   * @return an Optional containing the player's bandwidth stats, empty if not available
   */
  Optional<PlayerBandwidthStats> getPlayerBandwidthStats(UUID uuid);

  /**
   * Gets bandwidth statistics for all players currently connected to the proxy.
   *
   * @return a collection of bandwidth stats for all connected players
   */
  Collection<PlayerBandwidthStats> getAllPlayerBandwidthStats();

  /**
   * Gets a snapshot of total bandwidth usage across the entire proxy.
   *
   * @return a snapshot containing aggregated bandwidth statistics
   */
  BandwidthSnapshot getTotalBandwidthSnapshot();

  /**
   * Resets bandwidth statistics for a specific player.
   *
   * @param player the player to reset bandwidth stats for
   * @return a CompletableFuture that completes when the reset is done
   */
  CompletableFuture<Void> resetPlayerBandwidthStats(Player player);

  /**
   * Resets bandwidth statistics for a specific player by UUID.
   *
   * @param uuid the UUID of the player to reset bandwidth stats for
   * @return a CompletableFuture that completes when the reset is done
   */
  CompletableFuture<Void> resetPlayerBandwidthStats(UUID uuid);

  /**
   * Resets bandwidth statistics for all players.
   *
   * @return a CompletableFuture that completes when the reset is done
   */
  CompletableFuture<Void> resetAllBandwidthStats();

  /**
   * Enables or disables bandwidth tracking.
   *
   * @param enabled true to enable bandwidth tracking, false to disable
   */
  void setBandwidthTrackingEnabled(boolean enabled);

  /**
   * Checks if bandwidth tracking is currently enabled.
   *
   * @return true if bandwidth tracking is enabled, false otherwise
   */
  boolean isBandwidthTrackingEnabled();

  /**
   * Sets the interval for bandwidth statistics updates in milliseconds.
   *
   * @param intervalMs the update interval in milliseconds
   */
  void setUpdateInterval(long intervalMs);

  /**
   * Gets the current bandwidth statistics update interval in milliseconds.
   *
   * @return the update interval in milliseconds
   */
  long getUpdateInterval();
}