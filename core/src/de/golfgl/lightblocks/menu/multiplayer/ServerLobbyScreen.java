package de.golfgl.lightblocks.menu.multiplayer;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import de.golfgl.lightblocks.LightBlocksGame;
import de.golfgl.lightblocks.menu.AbstractFullScreenDialog;
import de.golfgl.lightblocks.multiplayer.ServerModels;
import de.golfgl.lightblocks.multiplayer.ServerMultiplayerManager;
import de.golfgl.lightblocks.scene2d.ProgressDialog;
import de.golfgl.lightblocks.scene2d.ScaledLabel;
import de.golfgl.lightblocks.screen.FontAwesome;

/**
 * Shows lobby of a multiplayer server
 */
public class ServerLobbyScreen extends AbstractFullScreenDialog {
    protected final ProgressDialog.WaitRotationImage waitRotationImage;
    protected final Cell contentCell;
    private final ServerMultiplayerManager serverMultiplayerManager;
    private boolean connecting;
    private long lastDoPingTime;

    public ServerLobbyScreen(LightBlocksGame app, String roomAddress) {
        super(app);
        waitRotationImage = new ProgressDialog.WaitRotationImage(app);
        closeButton.setFaText(FontAwesome.MISC_CROSS);

        // Fill Content
        fillFixContent();
        Table contentTable = getContentTable();
        contentTable.row();
        contentCell = contentTable.add().minHeight(waitRotationImage.getHeight() * 3);

        reload();
        serverMultiplayerManager = new ServerMultiplayerManager();
        serverMultiplayerManager.connect(roomAddress);
        connecting = true;
    }

    protected void fillFixContent() {
        Table contentTable = getContentTable();
        contentTable.row();
        contentTable.add(new Label(FontAwesome.NET_PEOPLE, app.skin, FontAwesome.SKIN_FONT_FA));
    }

    protected void reload() {
        contentCell.setActor(waitRotationImage);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (connecting && !serverMultiplayerManager.isConnecting() && serverMultiplayerManager.getLastErrorMsg() != null) {
            fillErrorScreen(serverMultiplayerManager.getLastErrorMsg());
            connecting = false;
        } else if (connecting && serverMultiplayerManager.isConnected()) {
            connecting = false;
            lastDoPingTime = TimeUtils.millis();
            contentCell.setActor(new LobbyTable());
        }
    }

    protected void fillErrorScreen(String errorMessage) {
        Table errorTable = new Table();
        Label errorMsgLabel = new ScaledLabel(errorMessage, app.skin, LightBlocksGame.SKIN_FONT_TITLE);
        float noWrapHeight = errorMsgLabel.getPrefHeight();
        errorMsgLabel.setWrap(true);
        errorMsgLabel.setAlignment(Align.center);
        errorTable.add(errorMsgLabel).minHeight(noWrapHeight * 1.5f).fill()
                .minWidth(LightBlocksGame.nativeGameWidth - 50);

        contentCell.setActor(errorTable).fillX();
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);
        if (parent == null && serverMultiplayerManager.isConnected()) {
            serverMultiplayerManager.disconnect();
        }
    }

    private class LobbyTable extends Table {

        private final Cell<Label> pingCell;

        public LobbyTable() {

            Table serverInfoTable = new Table(app.skin);

            ServerModels.ServerInfo serverInfo = serverMultiplayerManager.getServerInfo();

            serverInfoTable.defaults().padRight(5).padLeft(5);
            serverInfoTable.add("Server: ").right();
            serverInfoTable.add(serverInfo.name); // TODO ellipsis
            serverInfoTable.row();
            serverInfoTable.add("Version: ").right();
            serverInfoTable.add(String.valueOf(serverInfo.version)).left();
            serverInfoTable.row();
            serverInfoTable.add("Ping: ").right();
            pingCell = serverInfoTable.add("").left();

            add(serverInfoTable);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            int lastPing = serverMultiplayerManager.getLastPingTime();
            if (lastPing >= 0) {
                pingCell.getActor().setText(String.valueOf(lastPing));

                if (TimeUtils.millis() - lastDoPingTime >= 5000) {
                    lastDoPingTime = TimeUtils.millis();
                    serverMultiplayerManager.doPing();
                }
            }
        }
    }
}
