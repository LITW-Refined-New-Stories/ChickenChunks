package codechicken.chunkloader;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.opengl.GL11;

import codechicken.core.ServerUtils;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.util.LangProxy;

public class GuiChunkLoader extends GuiScreen {

    private static LangProxy lang = new LangProxy("chickenchunks.gui");

    public GuiButton laserButton;
    public GuiButton shapeButton;
    public GuiTextField ownerText;
    public TileChunkLoader tile;

    public GuiChunkLoader(TileChunkLoader tile) {
        this.tile = tile;
    }

    public void initGui() {
        buttonList.clear();

        buttonList.add(new GuiButton(1, width / 2 - 20, height / 2 - 45, 20, 20, "+"));
        buttonList.add(new GuiButton(2, width / 2 - 80, height / 2 - 45, 20, 20, "-"));
        buttonList.add(laserButton = new GuiButton(3, width / 2 + 7, height / 2 - 60, 75, 20, "-"));
        buttonList.add(shapeButton = new GuiButton(4, width / 2 + 7, height / 2 - 37, 75, 20, "-"));

        if (enableOwner())
            buttonList.add(new GuiButton(5, width / 2 + 7, height / 2 - 8, 75, 20, lang.translate("setowner")));

        ownerText = new GuiTextField(fontRendererObj, width / 2 - 80, height / 2 - 7, 80, 18);
        ownerText.setText(tile.getOwner());

        updateNames();

        super.initGui();
    }

    public void updateNames() {
        laserButton.displayString = tile.renderInfo.showLasers ? lang.translate("hidelasers")
                : lang.translate("showlasers");
        shapeButton.displayString = tile.shape.getName();
    }

    public void updateScreen() {
        if (mc.theWorld.getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord) != tile)// tile changed
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
        }
        updateNames();
        super.updateScreen();
        ownerText.updateCursorCounter();
    }

    public void drawScreen(int i, int j, float f) {
        drawDefaultBackground();
        drawContainerBackground();

        if (enableOwner()) ownerText.drawTextBox();

        super.drawScreen(i, j, f);// buttons

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawCentered(lang.translate("name"), width / 2 - 40, height / 2 - 74, 0x303030);
        if (tile.owner != null) drawCentered(tile.owner, width / 2 + 44, height / 2 - 72, 0x801080);
        drawCentered(lang.translate("radius"), width / 2 - 40, height / 2 - 57, 0x404040);
        drawCentered("" + tile.radius, width / 2 - 40, height / 2 - 39, 0xFFFFFF);

        int chunks = tile.countLoadedChunks();
        drawCentered(lang.format(chunks == 1 ? "chunk" : "chunks", chunks), width / 2 - 39, height / 2 - 21, 0x108000);

        // TODO: sradius = "Total "+ChunkLoaderManager.activeChunkLoaders+"/"+ChunkLoaderManager.allowedChunkloaders+"
        // Chunks";
        // fontRenderer.drawString(sradius, width / 2 - fontRenderer.getStringWidth(sradius) / 2, height / 2 - 8,
        // 0x108000);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawCentered(String s, int x, int y, int colour) {
        fontRendererObj.drawString(s, x - fontRendererObj.getStringWidth(s) / 2, y, colour);
    }

    int button;

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        button = par3;
        if (par3 == 1) par3 = 0;
        super.mouseClicked(par1, par2, par3);
        ownerText.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        ownerText.textboxKeyTyped(par1, par2);
    }

    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.id == 1) ChunkLoaderCPH.sendShapeChange(tile, tile.shape, tile.radius + 1);
        if (guibutton.id == 2 && tile.radius > 1) ChunkLoaderCPH.sendShapeChange(tile, tile.shape, tile.radius - 1);
        if (guibutton.id == 3) tile.renderInfo.showLasers = !tile.renderInfo.showLasers;
        if (guibutton.id == 4)
            ChunkLoaderCPH.sendShapeChange(tile, button == 1 ? tile.shape.prev() : tile.shape.next(), tile.radius);
        if (guibutton.id == 5) ChunkLoaderCPH.sendOwnerChange(tile, ownerText.getText());
    }

    private void drawContainerBackground() {
        boolean isOpInteract = enableOwner();
        GL11.glColor4f(1, 1, 1, 1);
        CCRenderState.changeTexture("chickenchunks:textures/gui/" + (isOpInteract ? "guiSmall2" : "guiSmall") + ".png");
        int posx = width / 2 - 88;
        int posy = height / 2 - 83;
        drawTexturedModalRect(posx, posy, 0, 0, 176, isOpInteract ? 192 : 166);
    }

    private boolean enableOwner() {
        return ChunkLoaderManager.allowOwnerChange() && (!ChunkLoaderManager.userInteract()
                || (ChunkLoaderManager.opInteract() && ServerUtils.isPlayerOP(this.tile.owner)));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
