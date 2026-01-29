package net.violetunderscore.netherrun.game;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.violetunderscore.netherrun.math.TimeConvert;
import net.violetunderscore.netherrun.network.NetherrunNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static net.violetunderscore.netherrun.NetherRun.MOD_ID;

public class GameLogic {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final MinecraftServer server;

    private boolean gameActive = false;
    private boolean roundActive = false;
    private int round = 1;
    private int turn = 1;
    private int preGameTimer = 0;
    private int currentRoundTime = 0;

    private Set<UUID> teamOnePlayers = new HashSet<>();
    private int teamOneScore = 0;
    private int targetScoreOne = TimeConvert.minuteToTick(15);
    private Set<UUID> teamTwoPlayers = new HashSet<>();
    private int teamTwoScore = 0;
    private int targetScoreTwo = TimeConvert.minuteToTick(15);

    private Set<UUID> readyPlayers = new HashSet<>();
    private Map<UUID, Integer> teleporting = new HashMap<>();
    private Map<UUID, BlockPos> teleportingPos = new HashMap<>();

    private boolean balanced = true;

    public GameLogic(MinecraftServer server) {
        this.server = server;
    }

    public void tickMaster() {
        checkSpawnTimers();
        if (gameActive) {
            if (roundActive) {
                tickRound();
            }
        }
    }
    public void tickRound() {
        tickRoundEachPlayer();

        if (preGameTimer > 0) {
            preGameTimer--;
            if (preGameTimer == 0) {
                ServerPlayerEntity rp = (ServerPlayerEntity)runningPlayer();
                BlockPos pos = runningPlayer().getBlockPos();
                spawnPlayer(rp, pos);
                for (UUID uuid : playingPlayers()) {
                    ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
                    if (p != null && p != rp) {
                        p.teleport(pos.getX(), pos.getY(), pos.getZ(), false);
                        spawnPlayerIn(uuid, pos, TimeConvert.secondToTick(5));
                    }
                }
            }
        } else if (teamOnePlayers.contains(runningPlayer().getUuid())) {
            teamOneScore++;
            currentRoundTime++;
            if (teamOneScore % 20 == 0) {
                NetherrunNetwork.UpdateOneScoreForAll(teamOneScore, 1, server);
            }
        } else if (teamTwoPlayers.contains(runningPlayer().getUuid())) {
            teamTwoScore++;
            currentRoundTime++;
            if (teamTwoScore % 20 == 0) {
                NetherrunNetwork.UpdateOneScoreForAll(teamTwoScore, 2, server);
            }
        }
    }

    public void tickRoundEachPlayer() {
        ServerPlayerEntity rp = (ServerPlayerEntity)runningPlayer();
        if (currentRoundTime <= TimeConvert.minuteToTick(2)) {
            rp.heal(0.025f);
        }
        for (UUID uuid : playingPlayers()) {
            ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
            if (p != null) {
                p.getHungerManager().setFoodLevel(16);
            }
        }
    }

    public void spawnPlayer(ServerPlayerEntity p, BlockPos pos) {
        World world = p.getEntityWorld();
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 3; y++) {
                for(int z = -1; z <= 1; z++) {
                    if (y == -1 || y == 3) {
                        world.setBlockState(pos.add(x, y, z), Blocks.CRYING_OBSIDIAN.getDefaultState());
                    } else {
                        world.setBlockState(pos.add(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        p.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, true);
        p.setVelocity(Vec3d.ZERO);
        p.velocityDirty = true;
        p.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(p));
        p.changeGameMode(GameMode.SURVIVAL);
    }
    public void spawnPlayerIn(UUID uuid, BlockPos pos, int i) {
        teleporting.put(uuid, i);
        teleportingPos.put(uuid, pos);
    }
    public void checkSpawnTimers() {
        for (UUID uuid : teleporting.keySet()) {
            if (teleportingPos.containsKey(uuid)) {
                ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
                if (teleporting.get(uuid) <= 0) {
                    if (p != null) {
                        spawnPlayer(p, teleportingPos.get(uuid));
                        teleporting.remove(uuid);
                        teleportingPos.remove(uuid);
                    }
                } else {
                    if (teleporting.get(uuid) % 20 == 0) {
                        if (p != null) {
                            p.sendMessage(Text.translatable("msg.netherrun.spawning", TimeConvert.tickToSecond(teleporting.get(uuid))), true);
                        }
                    }
                    teleporting.put(uuid, teleporting.get(uuid) - 1);
                }
            }
        }
    }

    public int startGame() {
        if (gameActive) {
            return 0;
        }
        if (teamOnePlayers.isEmpty()
                || teamTwoPlayers.isEmpty()) {
            return 2;
        }
        gameActive = true;
        roundActive = false;
        round = 1;
        turn = 1;
        teamOneScore = 0;
        teamTwoScore = 0;
        server.getPlayerManager().broadcast(Text.translatable("cmd.netherrun.start.success"), false);
        NetherrunNetwork.ToggleBoardForAll(gameActive, server);
        NetherrunNetwork.UpdateScoresForAll(teamOneScore, teamTwoScore, targetScoreOne, server);
        return 1;
    }
    public void startRound(boolean forced) {
        if (allReady() || forced) {
            if (runningPlayer() != null) {
                ServerPlayerEntity sp = (ServerPlayerEntity) runningPlayer();
                roundActive = true;
                preGameTimer = TimeConvert.secondToTick(15);
                currentRoundTime = 0;
                server.getPlayerManager().broadcast(Text.translatable("msg.netherrun.roundstart.success"), false);
                int spawnX = new Random().nextInt(20000) - 10000;
                int spawnZ = new Random().nextInt(20000) - 10000;
                sp.teleport(server.getWorld(World.NETHER), spawnX, 100, spawnZ, Set.of(), 0, 0, true);
                sp.changeGameMode(GameMode.SPECTATOR);
                for (UUID uuid : playingPlayers()) {
                    ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
                    if (p != null && p != sp) {
                        p.teleport(server.getWorld(World.NETHER), spawnX, 100, spawnZ, Set.of(), 0, 0, true);
                        p.changeGameMode(GameMode.SPECTATOR);
                    }
                }
            } else {
                server.getPlayerManager().broadcast(Text.translatable("msg.netherrun.roundstart.nullplayer"), false);
            }
        } else {
            server.getPlayerManager().broadcast(Text.translatable("msg.netherrun.roundstart.unready"), false);
        }
    }
    private boolean allReady() {
        boolean result = true;
        for (UUID uuid : playingPlayers()) {
            if (!readyPlayers.contains(uuid)) {
                result = false;
                break;
            }
        }
        return result;
    }
    public void endRound() {
        roundActive = false;
        readyPlayers.clear();
        for (UUID uuid : playingPlayers()) {
            ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
            if (p != null) {
                p.changeGameMode(GameMode.SPECTATOR);
            }
        }
        if (turn == (balanced?2:playingPlayers().size())) {
            turn = 1;
            if (teamOneScore >= targetScoreOne
                    || teamTwoScore >= targetScoreTwo) {
                if (determineWinner()) {
                    return;
                }
            }
            round++;
        } else { turn++; }
    }
    public int endGame(boolean silent) {
        if (!gameActive) {
            return 0;
        }
        gameActive = false;
        roundActive = false;
        round = 1;
        turn = 1;
        teamOneScore = 0;
        teamTwoScore = 0;
        if (!silent) {
            server.getPlayerManager().broadcast(Text.translatable("cmd.netherrun.end.success"), false);
        }
        NetherrunNetwork.ToggleBoardForAll(gameActive, server);
        return 1;
    }

    private Set<UUID> playingPlayers() {
        Set<UUID> players = new HashSet<>();
        players.addAll(teamOnePlayers);
        players.addAll(teamTwoPlayers);
        return players;
    }
    public PlayerEntity runningPlayer() {
        UUID id = null;
        if (balanced) {
            if (turn > teamOnePlayers.size()) {
                if (!teamTwoPlayers.isEmpty()) id = teamTwoPlayers.stream().toList().get(turn - teamOnePlayers.size() - 1);
            } else {
                if (!teamOnePlayers.isEmpty()) id = teamOnePlayers.stream().toList().get(turn - 1);
            }
        } else {
            if (turn == 2) {
                if (!teamTwoPlayers.isEmpty()) id = teamTwoPlayers.stream().toList().get((round % teamTwoPlayers.size()) - 1);
            } else {
                if (!teamOnePlayers.isEmpty()) id = teamOnePlayers.stream().toList().get((round % teamOnePlayers.size()) - 1);
            }
        }
        return server.getPlayerManager().getPlayer(id);
    }
    public boolean isPlayingPlayer(UUID uuid) {
        return playingPlayers().contains(uuid);
    }

    public boolean determineWinner() {
        float percentOne = (float)teamOneScore / targetScoreOne;
        float percentTwo = (float)teamTwoScore / targetScoreTwo;
        int winningTeam = 0;
        if (percentOne == percentTwo) {
            if (teamOneScore == teamTwoScore) {
                server.getPlayerManager().broadcast(Text.translatable("msg.netherrun.tie"), false);
                return false;
            } else if (teamOneScore > teamTwoScore) {
                winningTeam = 1;
            } else {
                winningTeam = 2;
            }
        } else {
            if (percentOne > percentTwo) {
                winningTeam = 1;
            } else {
                winningTeam = 2;
            }
        }
        server.getPlayerManager().broadcast(Text.translatable("msg.netherrun.win", winningTeam), false);
        endGame(true);
        return true;
    }

    public int joinGame(UUID uuid, int team) {
        switch (team) {
            case 1:
                leaveGame(uuid);
                teamOnePlayers.add(uuid);
                return 1;
            case 2:
                leaveGame(uuid);
                teamTwoPlayers.add(uuid);
                return 1;
            default:
                return 0;
        }
    }
    public void leaveGame(UUID uuid) {
        teamOnePlayers.remove(uuid);
        teamTwoPlayers.remove(uuid);
    }
    public void onJoinServer(ServerPlayerEntity p) {
        NetherrunNetwork.ToggleBoard(active(), p);
        NetherrunNetwork.UpdateScores(teamOneScore, teamTwoScore, targetScoreOne, p);
    }

    public void setTarget(int time) {
        targetScoreOne = time;
        targetScoreTwo = time;
    }
    public int setTarget(int time, int team) {
        switch (team) {
            case 1:
                targetScoreOne = time;
                return 1;
            case 2:
                targetScoreTwo = time;
                return 1;
            default:
                return 0;
        }
    }

    public boolean active() {
        return gameActive;
    }
}
