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

import com.velocitypowered.api.proxy.player.BandwidthSnapshot;
import com.velocitypowered.api.proxy.player.PlayerBandwidthStats;
import java.time.Instant;
import java.util.Collection;

/**
 * Implementation of {@link BandwidthSnapshot} providing aggregated bandwidth statistics.
 */
public class VelocityBandwidthSnapshot implements BandwidthSnapshot {

  private final Instant snapshotTime;
  private final Instant trackingStartTime;
  private final int trackedPlayerCount;
  private final long totalBytesSent;
  private final long totalBytesReceived;
  private final double totalDownloadSpeed;
  private final double totalUploadSpeed;
  private final double averageDownloadSpeedPerPlayer;
  private final double averageUploadSpeedPerPlayer;
  private final double peakTotalDownloadSpeed;
  private final double peakTotalUploadSpeed;
  private final long totalPacketsSent;
  private final long totalPacketsReceived;
  private final double totalOutgoingPacketRate;
  private final double totalIncomingPacketRate;
  private final long trackingDurationMs;

  public VelocityBandwidthSnapshot(Collection<PlayerBandwidthStats> playerStats,
                                   Instant trackingStartTime,
                                   double peakTotalDownloadSpeed,
                                   double peakTotalUploadSpeed) {
    this.snapshotTime = Instant.now();
    this.trackingStartTime = trackingStartTime;
    this.peakTotalDownloadSpeed = peakTotalDownloadSpeed;
    this.peakTotalUploadSpeed = peakTotalUploadSpeed;
    this.trackingDurationMs = System.currentTimeMillis() - trackingStartTime.toEpochMilli();

    this.trackedPlayerCount = playerStats.size();

    long tempTotalBytesSent = 0;
    long tempTotalBytesReceived = 0;
    double tempTotalDownloadSpeed = 0.0;
    double tempTotalUploadSpeed = 0.0;
    long tempTotalPacketsSent = 0;
    long tempTotalPacketsReceived = 0;
    double tempTotalOutgoingPacketRate = 0.0;
    double tempTotalIncomingPacketRate = 0.0;

    for (PlayerBandwidthStats stats : playerStats) {
      tempTotalBytesSent += stats.getTotalBytesSent();
      tempTotalBytesReceived += stats.getTotalBytesReceived();
      tempTotalDownloadSpeed += stats.getDownloadSpeed();
      tempTotalUploadSpeed += stats.getUploadSpeed();
      tempTotalPacketsSent += stats.getTotalPacketsSent();
      tempTotalPacketsReceived += stats.getTotalPacketsReceived();
      tempTotalOutgoingPacketRate += stats.getOutgoingPacketRate();
      tempTotalIncomingPacketRate += stats.getIncomingPacketRate();
    }

    this.totalBytesSent = tempTotalBytesSent;
    this.totalBytesReceived = tempTotalBytesReceived;
    this.totalDownloadSpeed = tempTotalDownloadSpeed;
    this.totalUploadSpeed = tempTotalUploadSpeed;
    this.totalPacketsSent = tempTotalPacketsSent;
    this.totalPacketsReceived = tempTotalPacketsReceived;
    this.totalOutgoingPacketRate = tempTotalOutgoingPacketRate;
    this.totalIncomingPacketRate = tempTotalIncomingPacketRate;

    if (trackedPlayerCount > 0) {
      this.averageDownloadSpeedPerPlayer = tempTotalDownloadSpeed / trackedPlayerCount;
      this.averageUploadSpeedPerPlayer = tempTotalUploadSpeed / trackedPlayerCount;
    } else {
      this.averageDownloadSpeedPerPlayer = 0.0;
      this.averageUploadSpeedPerPlayer = 0.0;
    }
  }

  @Override
  public Instant getSnapshotTime() {
    return snapshotTime;
  }

  @Override
  public int getTrackedPlayerCount() {
    return trackedPlayerCount;
  }

  @Override
  public long getTotalBytesSent() {
    return totalBytesSent;
  }

  @Override
  public long getTotalBytesReceived() {
    return totalBytesReceived;
  }

  @Override
  public double getTotalDownloadSpeed() {
    return totalDownloadSpeed;
  }

  @Override
  public double getTotalUploadSpeed() {
    return totalUploadSpeed;
  }

  @Override
  public double getAverageDownloadSpeedPerPlayer() {
    return averageDownloadSpeedPerPlayer;
  }

  @Override
  public double getAverageUploadSpeedPerPlayer() {
    return averageUploadSpeedPerPlayer;
  }

  @Override
  public double getPeakTotalDownloadSpeed() {
    return peakTotalDownloadSpeed;
  }

  @Override
  public double getPeakTotalUploadSpeed() {
    return peakTotalUploadSpeed;
  }

  @Override
  public long getTotalPacketsSent() {
    return totalPacketsSent;
  }

  @Override
  public long getTotalPacketsReceived() {
    return totalPacketsReceived;
  }

  @Override
  public double getTotalOutgoingPacketRate() {
    return totalOutgoingPacketRate;
  }

  @Override
  public double getTotalIncomingPacketRate() {
    return totalIncomingPacketRate;
  }

  @Override
  public Instant getTrackingStartTime() {
    return trackingStartTime;
  }

  @Override
  public long getTrackingDurationMs() {
    return trackingDurationMs;
  }
}