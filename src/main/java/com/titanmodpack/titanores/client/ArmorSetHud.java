package com.titanmodpack.titanores.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.ModArmorItem;
import com.titanmodpack.titanores.item.ModArmorMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// With a full set of one mod material, the armor bar uses that material's icon
// (textures/gui/armor_<material>.png, 9x9).
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID, value = Dist.CLIENT)
public class ArmorSetHud {

    @SubscribeEvent
    public static void onRenderArmor(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ARMOR) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) mc.getCameraEntity();
        ModArmorMaterial material = ModArmorItem.fullSet(player);
        if (material == null) {
            return;
        }
        event.setCanceled(true);

        String name = material.getName().substring(material.getName().indexOf(':') + 1);
        ResourceLocation icon = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/armor_" + name + ".png");
        MatrixStack matrixStack = event.getMatrixStack();
        int left = event.getWindow().getGuiScaledWidth() / 2 - 91;
        int top = event.getWindow().getGuiScaledHeight() - ForgeIngameGui.left_height;

        RenderSystem.enableBlend();
        mc.getTextureManager().bind(icon);
        for (int i = 0; i < 10; i++) {
            AbstractGui.blit(matrixStack, left + i * 8, top, 0, 0, 9, 9, 9, 9);
        }
        RenderSystem.disableBlend();
        mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        ForgeIngameGui.left_height += 10;
    }
}
