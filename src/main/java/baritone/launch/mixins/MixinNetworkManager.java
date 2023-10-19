/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.launch.mixins;

import baritone.Baritone;
import baritone.event.events.PacketEvent;
import baritone.event.events.type.EventState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Brady
 * @since 8/6/2018 9:30 PM
 */
@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Shadow private Channel channel;

    @Shadow protected abstract void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_) throws Exception;

    @Inject(
            method = "dispatchPacket",
            at = @At("HEAD")
    )
    private void preDispatchPacket(Packet<?> inPacket, final GenericFutureListener<? extends Future<? super Void >>[] futureListeners, CallbackInfo ci) {
        
		/* ********OpenRefactory Warning********
		 Possible null pointer dereference!
		 Path: 
			File: MixinNetworkManager.java, Line: 51
				Baritone.INSTANCE.getGameEventHandler().onSendPacket(new PacketEvent(EventState.PRE,inPacket));
				Method getGameEventHandler may return null and is referenced in method invocation.
		*/
		Baritone.INSTANCE.getGameEventHandler().onSendPacket(new PacketEvent(EventState.PRE, inPacket));
    }

    @Inject(
            method = "dispatchPacket",
            at = @At("RETURN")
    )
    private void postDispatchPacket(Packet<?> inPacket, final GenericFutureListener<? extends Future<? super Void >>[] futureListeners, CallbackInfo ci) {
        Baritone.INSTANCE.getGameEventHandler().onSendPacket(new PacketEvent(EventState.POST, inPacket));
    }

    @Inject(
            method = "channelRead0",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/Packet.processPacket(Lnet/minecraft/network/INetHandler;)V"
            )
    )
    private void preProcessPacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        
		/* ********OpenRefactory Warning********
		 Possible null pointer dereference!
		 Path: 
			File: MixinNetworkManager.java, Line: 70
				Baritone.INSTANCE.getGameEventHandler().onReceivePacket(new PacketEvent(EventState.PRE,packet));
				Method getGameEventHandler may return null and is referenced in method invocation.
		*/
		Baritone.INSTANCE.getGameEventHandler().onReceivePacket(new PacketEvent(EventState.PRE, packet));
    }

    @Inject(
            method = "channelRead0",
            at = @At("RETURN")
    )
    private void postProcessPacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (this.channel.isOpen())
            Baritone.INSTANCE.getGameEventHandler().onReceivePacket(new PacketEvent(EventState.POST, packet));
    }
}
