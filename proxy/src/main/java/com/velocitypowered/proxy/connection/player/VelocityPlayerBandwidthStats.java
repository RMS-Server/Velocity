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

import com.velocitypowered.api.proxy.player.PlayerBandwidthStats;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of {@link PlayerBandwidthStats} for tracking player bandwidth usage.
 */
public class VelocityPlayerBandwidthStats implements PlayerBandwidthStats {

  private final UUID playerUuid;
  private final String playerUsername;
  private final Instant trackingStartTime;
  private volatile Instant lastUpdateTime;
  private volatile boolean active;
  private volatile String currentServerName;

  private final AtomicLong totalBytesSent = new AtomicLong(0);
  private final AtomicLong totalBytesReceived = new AtomicLong(0);
  private final AtomicLong totalPacketsSent = new AtomicLong(0);
  private final AtomicLong totalPacketsReceived = new AtomicLong(0);

  private volatile double currentDownloadSpeed = 0.0;
  private volatile double currentUploadSpeed = 0.0;
  private volatile double peakDownloadSpeed = 0.0;
  private volatile double peakUploadSpeed = 0.0;
  private volatile double averageDownloadSpeed = 0.0;
  private volatile double averageUploadSpeed = 0.0;
  private volatile double outgoingPacketRate = 0.0;
  private volatile double incomingPacketRate = 0.0;

  private volatile long lastBytesSent = 0;
  private volatile long lastBytesReceived = 0;
  private volatile long lastPacketsSent = 0;
  private volatile long lastPacketsReceived = 0;
  private volatile long lastUpdateTimestamp = 0;

  public VelocityPlayerBandwidthStats(UUID playerUuid, String playerUsername) {
    this.playerUuid = playerUuid;
    this.playerUsername = playerUsername;
    this.trackingStartTime = Instant.now();
    this.lastUpdateTime = this.trackingStartTime;
    this.lastUpdateTimestamp = System.currentTimeMillis();
    this.active = true;
  }

  @Override
  public UUID getPlayerUuid() {
    return playerUuid;
  }

  @Override
  public String getPlayerUsername() {
    return playerUsername;
  }

  @Override
  public long getTotalBytesSent() {
    return totalBytesSent.get();
  }

  @Override
  public long getTotalBytesReceived() {
    return totalBytesReceived.get();
  }

  @Override
  public double getDownloadSpeed() {
    return currentDownloadSpeed;
  }

  @Override
  public double getUploadSpeed() {
    return currentUploadSpeed;
  }

  @Override
  public double getPeakDownloadSpeed() {
    return peakDownloadSpeed;
  }

  @Override
  public double getPeakUploadSpeed() {
    return peakUploadSpeed;
  }

  @Override
  public double getAverageDownloadSpeed() {
    return averageDownloadSpeed;
  }

  @Override
  public double getAverageUploadSpeed() {
    return averageUploadSpeed;
  }

  @Override
  public Instant getTrackingStartTime() {
    return trackingStartTime;
  }

  @Override
  public Instant getLastUpdateTime() {
    return lastUpdateTime;
  }

  @Override
  public long getTrackingDurationMs() {
    return System.currentTimeMillis() - trackingStartTime.toEpochMilli();
  }

  @Override
  public long getTotalPacketsSent() {
    return totalPacketsSent.get();
  }

  @Override
  public long getTotalPacketsReceived() {
    return totalPacketsReceived.get();
  }

  @Override
  public double getOutgoingPacketRate() {
    return outgoingPacketRate;
  }

  @Override
  public double getIncomingPacketRate() {
    return incomingPacketRate;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public String getCurrentServerName() {
    return currentServerName;
  }

  public void recordBytesSent(long bytes) {
    totalBytesSent.addAndGet(bytes);
  }

  public void recordBytesReceived(long bytes) {
    totalBytesReceived.addAndGet(bytes);
  }

  public void recordPacketSent() {
    totalPacketsSent.incrementAndGet();
  }

  public void recordPacketReceived() {
    totalPacketsReceived.incrementAndGet();
  }

  public void updateSpeeds(long intervalMs) {
    long currentTime = System.currentTimeMillis();
    long currentBytesSent = totalBytesSent.get();
    long currentBytesReceived = totalBytesReceived.get();
    long currentPacketsSent = totalPacketsSent.get();
    long currentPacketsReceived = totalPacketsReceived.get();

    if (lastUpdateTimestamp > 0 && intervalMs > 0) {
      double timeInSeconds = intervalMs / 1000.0;

      long bytesSentDelta = currentBytesSent - lastBytesSent;
      long bytesReceivedDelta = currentBytesReceived - lastBytesReceived;
      long packetsSentDelta = currentPacketsSent - lastPacketsSent;
      long packetsReceivedDelta = currentPacketsReceived - lastPacketsReceived;

      currentDownloadSpeed = bytesReceivedDelta / timeInSeconds;
      currentUploadSpeed = bytesSentDelta / timeInSeconds;
      outgoingPacketRate = packetsSentDelta / timeInSeconds;
      incomingPacketRate = packetsReceivedDelta / timeInSeconds;

      if (currentDownloadSpeed > peakDownloadSpeed) {
        peakDownloadSpeed = currentDownloadSpeed;
      }
      if (currentUploadSpeed > peakUploadSpeed) {
        peakUploadSpeed = currentUploadSpeed;
      }

      long totalDurationMs = getTrackingDurationMs();
      if (totalDurationMs > 0) {
        double totalTimeInSeconds = totalDurationMs / 1000.0;
        averageDownloadSpeed = currentBytesReceived / totalTimeInSeconds;
        averageUploadSpeed = currentBytesSent / totalTimeInSeconds;
      }
    }

    lastBytesSent = currentBytesSent;
    lastBytesReceived = currentBytesReceived;
    lastPacketsSent = currentPacketsSent;
    lastPacketsReceived = currentPacketsReceived;
    lastUpdateTimestamp = currentTime;
    lastUpdateTime = Instant.ofEpochMilli(currentTime);
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setCurrentServerName(String serverName) {
    this.currentServerName = serverName;
  }

  public void reset() {
    totalBytesSent.set(0);
    totalBytesReceived.set(0);
    totalPacketsSent.set(0);
    totalPacketsReceived.set(0);
    currentDownloadSpeed = 0.0;
    currentUploadSpeed = 0.0;
    peakDownloadSpeed = 0.0;
    peakUploadSpeed = 0.0;
    averageDownloadSpeed = 0.0;
    averageUploadSpeed = 0.0;
    outgoingPacketRate = 0.0;
    incomingPacketRate = 0.0;
    lastBytesSent = 0;
    lastBytesReceived = 0;
    lastPacketsSent = 0;
    lastPacketsReceived = 0;
    lastUpdateTimestamp = System.currentTimeMillis();
    lastUpdateTime = Instant.now();
  }
}