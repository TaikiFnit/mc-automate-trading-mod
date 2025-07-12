package com.villagertrading.menu;

import com.villagertrading.blockentity.VillagerTradingStationBlockEntity;
import com.villagertrading.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VillagerTradingStationMenu extends AbstractContainerMenu {
    private final VillagerTradingStationBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    
    public VillagerTradingStationMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level()
            .getBlockEntity(extraData.readBlockPos()));
    }
    
    public VillagerTradingStationMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.VILLAGER_TRADING_STATION.get(), containerId);
        
        if (entity instanceof VillagerTradingStationBlockEntity station) {
            this.blockEntity = station;
        } else {
            throw new IllegalStateException("Incorrect block entity class (%s) passed into VillagerTradingStationMenu!"
                .formatted(entity.getClass().getCanonicalName()));
        }
        
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        
        addSlots(playerInventory);
    }
    
    private void addSlots(Inventory playerInventory) {
        IItemHandler itemHandler = blockEntity.getItemHandler();
        
        // Trading station slots (3x3 grid)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(itemHandler, col + row * 3, 
                    62 + col * 18, 35 + row * 18));
            }
        }
        
        // Player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 
                    8 + col * 18, 84 + row * 18));
            }
        }
        
        // Player hotbar slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            if (index < 9) {
                // Moving from trading station to player inventory
                if (!this.moveItemStackTo(slotStack, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to trading station
                if (!this.moveItemStackTo(slotStack, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return itemstack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, blockEntity.getBlockState().getBlock());
    }
}