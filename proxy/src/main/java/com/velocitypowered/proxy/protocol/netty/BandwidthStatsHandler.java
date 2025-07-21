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

package com.velocitypowered.proxy.protocol.netty;

import com.velocitypowered.proxy.connection.MinecraftConnection;
import com.velocitypowered.proxy.connection.MinecraftConnectionAssociation;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.connection.player.VelocityPlayerBandwidthStats;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * A Netty handler for tracking bandwidth statistics.
 */
public class BandwidthStatsHandler extends ChannelDuplexHandler {

  private final MinecraftConnection connection;

  public BandwidthStatsHandler(MinecraftConnection connection) {
    this.connection = connection;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    try {
      if (msg instanceof ByteBuf) {
        ByteBuf buf = (ByteBuf) msg;
        recordBytesReceived(buf.readableBytes());
      }
      recordPacketReceived();
    } catch (Exception e) {
      // Ignore bandwidth tracking errors to avoid affecting the main pipeline
    }
    super.channelRead(ctx, msg);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    try {
      if (msg instanceof ByteBuf) {
        ByteBuf buf = (ByteBuf) msg;
        recordBytesSent(buf.readableBytes());
      }
      recordPacketSent();
    } catch (Exception e) {
      // Ignore bandwidth tracking errors to avoid affecting the main pipeline
    }
    super.write(ctx, msg, promise);
  }

  private void recordBytesSent(long bytes) {
    VelocityPlayerBandwidthStats stats = getPlayerBandwidthStats();
    if (stats != null) {
      stats.recordBytesSent(bytes);
    }
  }

  private void recordBytesReceived(long bytes) {
    VelocityPlayerBandwidthStats stats = getPlayerBandwidthStats();
    if (stats != null) {
      stats.recordBytesReceived(bytes);
    }
  }

  private void recordPacketSent() {
    VelocityPlayerBandwidthStats stats = getPlayerBandwidthStats();
    if (stats != null) {
      stats.recordPacketSent();
    }
  }

  private void recordPacketReceived() {
    VelocityPlayerBandwidthStats stats = getPlayerBandwidthStats();
    if (stats != null) {
      stats.recordPacketReceived();
    }
  }

  private VelocityPlayerBandwidthStats getPlayerBandwidthStats() {
    MinecraftConnectionAssociation association = connection.getAssociation();
    if (association instanceof ConnectedPlayer) {
      ConnectedPlayer player = (ConnectedPlayer) association;
      return (VelocityPlayerBandwidthStats) player.getBandwidthStats().orElse(null);
    }
    return null;
  }
}