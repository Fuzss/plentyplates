package fuzs.plentyplates.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;

/**
 * @deprecated replace with implementation from Puzzles Lib
 */
@Deprecated(forRemoval = true)
public class GuiGraphicsHelper {

    public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int color) {
        fillFrame(guiGraphics, posX, posY, width, height, borderSize, 0, color);
    }

    public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int z, int color) {
        fillFrameArea(guiGraphics, posX, posY, posX + width, posY + height, borderSize, z, color);
    }

    public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int color) {
        fillFrameArea(guiGraphics, minX, minY, maxX, maxY, borderSize, 0, color);
    }

    public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int z, int color) {
        // top
        guiGraphics.fill(minX, minY, maxX, minY + borderSize, z, color);
        // bottom
        guiGraphics.fill(minX, maxY - borderSize, maxX, maxY, z, color);
        // left
        guiGraphics.fill(minX, minY + borderSize, minX + borderSize, maxY - borderSize, z, color);
        // right
        guiGraphics.fill(maxX - borderSize, minY + borderSize, maxX, maxY - borderSize, z, color);
    }
}
