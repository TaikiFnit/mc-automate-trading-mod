package com.villagertrading.event;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.integration.CreateIntegration;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VillagerTradingMod.MODID)
public class CreateEventHandler {
    
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!CreateIntegration.isCreateLoaded()) {
            return;
        }
        
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        Player player = event.getEntity();
        
        // Check if the player is using a deployer (this would need to be expanded 
        // based on how Create handles deployer interactions)
        if (event.getTarget() instanceof Villager villager) {
            ItemStack heldItem = player.getItemInHand(event.getHand());
            
            // This is a simplified integration - in a full implementation,
            // you would need to hook into Create's deployer mechanics more directly
            if (isPlayerUsingDeployer(player)) {
                boolean success = CreateIntegration.handleDeployerVillagerInteraction(
                    event.getLevel(), 
                    player.blockPosition(), 
                    heldItem, 
                    villager
                );
                
                if (success) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    private static boolean isPlayerUsingDeployer(Player player) {
        // This is a placeholder - in a real implementation, you would check
        // if the interaction is coming from a Create deployer
        // This might involve checking for specific NBT data, mod integration APIs, etc.
        return false;
    }
}