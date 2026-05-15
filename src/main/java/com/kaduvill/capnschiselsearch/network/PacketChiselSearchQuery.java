package com.kaduvill.capnschiselsearch.network;

import com.kaduvill.capnschiselsearch.CapnsChiselSearchConfig;
import com.kaduvill.capnschiselsearch.api.IChiselSearchCompaction;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import team.chisel.common.inventory.ContainerChisel;

public class PacketChiselSearchQuery implements IMessage {
    private boolean updateQuery;
    private String query;
    private int scrollDelta;

    public PacketChiselSearchQuery() {
    }

    public PacketChiselSearchQuery(String query) {
        this.updateQuery = true;
        this.query = query == null ? "" : query;
        this.scrollDelta = 0;
    }

    private PacketChiselSearchQuery(int scrollDelta) {
        this.updateQuery = false;
        this.query = "";
        this.scrollDelta = scrollDelta;
    }

    public static PacketChiselSearchQuery scroll(int scrollDelta) {
        return new PacketChiselSearchQuery(scrollDelta);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.updateQuery = buf.readBoolean();
        this.query = ByteBufUtils.readUTF8String(buf);
        this.scrollDelta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.updateQuery);
        ByteBufUtils.writeUTF8String(buf, this.query == null ? "" : this.query);
        buf.writeInt(this.scrollDelta);
    }

    public static class Handler implements IMessageHandler<PacketChiselSearchQuery, IMessage> {
        @Override
        public IMessage onMessage(PacketChiselSearchQuery message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketChiselSearchQuery message, EntityPlayerMP player) {
            if (!CapnsChiselSearchConfig.enableSearchCompaction) {
                return;
            }

            Container openContainer = player.openContainer;

            if (!(openContainer instanceof ContainerChisel)) {
                return;
            }

            ContainerChisel container = (ContainerChisel) openContainer;

            if (!(container.getInventoryChisel() instanceof IChiselSearchCompaction)) {
                return;
            }

            IChiselSearchCompaction searchInventory = (IChiselSearchCompaction) container.getInventoryChisel();

            if (message.updateQuery) {
                searchInventory.capnschiselsearch$setSearchQuery(sanitizeQuery(message.query));
            } else if (message.scrollDelta != 0) {
                searchInventory.capnschiselsearch$scrollSearchOffset(message.scrollDelta);
            }

            container.getInventoryChisel().updateItems();
            container.detectAndSendChanges();
        }

        private String sanitizeQuery(String query) {
            if (query == null) {
                return "";
            }

            String clean = TextFormatting.getTextWithoutFormattingCodes(query).trim();

            if (clean.length() > 64) {
                clean = clean.substring(0, 64);
            }

            return clean;
        }
    }
}