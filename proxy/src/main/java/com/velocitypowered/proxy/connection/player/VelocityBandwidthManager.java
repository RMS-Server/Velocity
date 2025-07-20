/*
 * Copyright (C) 2018 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.velocitypowered.proxy.connection.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.BandwidthManager;
import com.velocitypowered.api.proxy.player.BandwidthSnapshot;
import com.velocitypowered.api.proxy.player.PlayerBandwidthStats;
import com.velocitypowered.proxy.VelocityServer;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of {@link BandwidthManager} for managing player bandwidth statistics.
 */
public class VelocityBandwidthManager implements BandwidthManager {

  private static final Logger logger = LogManager.getLogger(VelocityBandwidthManager.class);

  private final VelocityServer server;
  private final Map<UUID, VelocityPlayerBandwidthStats> playerStats = new ConcurrentHashMap<>();
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
      r -> new Thread(r, "Velocity Bandwidth Manager"));

  private volatile boolean trackingEnabled = true;
  private volatile long updateIntervalMs = 1000; // 1 second default
  private volatile ScheduledFuture<?> updateTask;
  private final Instant trackingStartTime = Instant.now();
  private volatile double peakTotalDownloadSpeed = 0.0;
  private volatile double peakTotalUploadSpeed = 0.0;

  public VelocityBandwidthManager(VelocityServer server) {
    this.server = server;
    startUpdateTask();
  }

  @Override
  public Optional<PlayerBandwidthStats> getPlayerBandwidthStats(Player player) {
    return getPlayerBandwidthStats(player.getUniqueId());
  }

  @Override
  public Optional<PlayerBandwidthStats> getPlayerBandwidthStats(UUID uuid) {
    if (!trackingEnabled) {
      return Optional.empty();
    }
    return Optional.ofNullable(playerStats.get(uuid));
  }

  @Override
  public Collection<PlayerBandwidthStats> getAllPlayerBandwidthStats() {
    if (!trackingEnabled) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableCollection(playerStats.values());
  }

  @Override
  public BandwidthSnapshot getTotalBandwidthSnapshot() {
    return new VelocityBandwidthSnapshot(
        getAllPlayerBandwidthStats(),
        trackingStartTime,
        peakTotalDownloadSpeed,
        peakTotalUploadSpeed
    );
  }

  @Override
  public CompletableFuture<Void> resetPlayerBandwidthStats(Player player) {
    return resetPlayerBandwidthStats(player.getUniqueId());
  }

  @Override
  public CompletableFuture<Void> resetPlayerBandwidthStats(UUID uuid) {
    return CompletableFuture.runAsync(() -> {
      VelocityPlayerBandwidthStats stats = playerStats.get(uuid);
      if (stats != null) {
        stats.reset();
        logger.debug("Reset bandwidth stats for player {}", uuid);
      }
    }, executor);
  }

  @Override
  public CompletableFuture<Void> resetAllBandwidthStats() {
    return CompletableFuture.runAsync(() -> {
      for (VelocityPlayerBandwidthStats stats : playerStats.values()) {
        stats.reset();
      }
      peakTotalDownloadSpeed = 0.0;
      peakTotalUploadSpeed = 0.0;
      logger.info("Reset bandwidth stats for all players");
    }, executor);
  }

  @Override
  public void setBandwidthTrackingEnabled(boolean enabled) {
    boolean wasEnabled = this.trackingEnabled;
    this.trackingEnabled = enabled;
    
    if (enabled && !wasEnabled) {
      startUpdateTask();
      logger.info("Bandwidth tracking enabled");
    } else if (!enabled && wasEnabled) {
      stopUpdateTask();
      logger.info("Bandwidth tracking disabled");
    }
  }

  @Override
  public boolean isBandwidthTrackingEnabled() {
    return trackingEnabled;
  }

  @Override
  public void setUpdateInterval(long intervalMs) {
    if (intervalMs <= 0) {
      throw new IllegalArgumentException("Update interval must be positive");
    }
    
    this.updateIntervalMs = intervalMs;
    
    if (trackingEnabled) {
      stopUpdateTask();
      startUpdateTask();
    }
    
    logger.debug("Bandwidth tracking update interval set to {}ms", intervalMs);
  }

  @Override
  public long getUpdateInterval() {
    return updateIntervalMs;
  }

  public void registerPlayer(Player player) {
    if (!trackingEnabled) {
      return;
    }
    
    VelocityPlayerBandwidthStats stats = new VelocityPlayerBandwidthStats(
        player.getUniqueId(),
        player.getUsername()
    );
    
    player.getCurrentServer().ifPresent(server -> 
        stats.setCurrentServerName(server.getServerInfo().getName()));
    
    playerStats.put(player.getUniqueId(), stats);
    logger.debug("Registered bandwidth tracking for player {}", player.getUsername());
  }

  public void unregisterPlayer(UUID uuid) {
    VelocityPlayerBandwidthStats stats = playerStats.remove(uuid);
    if (stats != null) {
      stats.setActive(false);
      logger.debug("Unregistered bandwidth tracking for player {}", uuid);
    }
  }

  public void recordBytesSent(UUID playerUuid, long bytes) {
    if (!trackingEnabled) {
      return;
    }
    
    VelocityPlayerBandwidthStats stats = playerStats.get(playerUuid);
    if (stats != null) {
      stats.recordBytesSent(bytes);
    }
  }

  public void recordBytesReceived(UUID playerUuid, long bytes) {
    if (!trackingEnabled) {
      return;
    }
    
    VelocityPlayerBandwidthStats stats = playerStats.get(playerUuid);
    if (stats != null) {
      stats.recordBytesReceived(bytes);
    }
  }

  public void recordPacketSent(UUID playerUuid) {
    if (!trackingEnabled) {
      return;
    }
    
    VelocityPlayerBandwidthStats stats = playerStats.get(playerUuid);
    if (stats != null) {
      stats.recordPacketSent();
    }
  }

  public void recordPacketReceived(UUID playerUuid) {
    if (!trackingEnabled) {
      return;
    }
    
    VelocityPlayerBandwidthStats stats = playerStats.get(playerUuid);
    if (stats != null) {
      stats.recordPacketReceived();
    }
  }

  public void updatePlayerServer(UUID playerUuid, String serverName) {
    VelocityPlayerBandwidthStats stats = playerStats.get(playerUuid);
    if (stats != null) {
      stats.setCurrentServerName(serverName);
    }
  }

  private void startUpdateTask() {
    if (updateTask != null && !updateTask.isCancelled()) {
      updateTask.cancel(false);
    }
    
    updateTask = executor.scheduleAtFixedRate(
        this::updateBandwidthStats,
        updateIntervalMs,
        updateIntervalMs,
        TimeUnit.MILLISECONDS
    );
  }

  private void stopUpdateTask() {
    if (updateTask != null && !updateTask.isCancelled()) {
      updateTask.cancel(false);
      updateTask = null;
    }
  }

  private void updateBandwidthStats() {
    if (!trackingEnabled) {
      return;
    }

    double totalDownloadSpeed = 0.0;
    double totalUploadSpeed = 0.0;

    for (VelocityPlayerBandwidthStats stats : playerStats.values()) {
      stats.updateSpeeds(updateIntervalMs);
      totalDownloadSpeed += stats.getDownloadSpeed();
      totalUploadSpeed += stats.getUploadSpeed();
    }

    if (totalDownloadSpeed > peakTotalDownloadSpeed) {
      peakTotalDownloadSpeed = totalDownloadSpeed;
    }
    if (totalUploadSpeed > peakTotalUploadSpeed) {
      peakTotalUploadSpeed = totalUploadSpeed;
    }
  }

  public void shutdown() {
    trackingEnabled = false;
    stopUpdateTask();
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}