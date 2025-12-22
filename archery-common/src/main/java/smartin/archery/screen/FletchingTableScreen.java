package smartin.archery.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import smartin.archery.Archery;
import smartin.miapi.Miapi;

public class FletchingTableScreen
         extends AbstractContainerScreen<FletchingTableScreenHandler> {
    private static final ResourceLocation CRAFTING_TABLE_LOCATION = Miapi.id(Archery.MOD_ID,"textures/gui/container/fletching_table.png");


    public FletchingTableScreen(
            FletchingTableScreenHandler handler,
            Inventory inventory,
            Component title
    ) {
        super(handler, inventory, title);
        CraftingScreen craftingScreen;
        //this.imageWidth = 176;
        //this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CRAFTING_TABLE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
