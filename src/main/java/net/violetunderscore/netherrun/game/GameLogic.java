package net.violetunderscore.netherrun.game;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.violetunderscore.netherrun.math.TimeConvert;
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

    private Set<UUID> teamOnePlayers = new HashSet<>();
    private int teamOneScore = 0;
    private int targetScoreOne = TimeConvert.minuteToTick(15);
    private Set<UUID> teamTwoPlayers = new HashSet<>();
    private int teamTwoScore = 0;
    private int targetScoreTwo = TimeConvert.minuteToTick(15);

    private Set<UUID> readyPlayers = new HashSet<>();
    private Map<UUID, Integer> teleporting = new HashMap<>();

    private boolean balanced = true;

    public GameLogic(MinecraftServer server) {
        this.server = server;
    }

    public void tickMaster() {
        if (gameActive) {
            if (roundActive) {
                tickRound();
            }
        }
    }
    public void tickRound() {
        if (preGameTimer > 0) {
            preGameTimer--;
            if (preGameTimer == 0) {
                spawnPlayer((ServerPlayerEntity) runningPlayer(), runningPlayer().getBlockPos());
            }
        } else if (teamOnePlayers.contains(runningPlayer().getUuid())) {
            teamOneScore++;
        } else if (teamTwoPlayers.contains(runningPlayer().getUuid())) {
            teamTwoScore++;
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
        p.changeGameMode(GameMode.SURVIVAL);
    }

    public int startGame() {
        if (gameActive) {
            server.getPlayerManager().broadcast(Text.literal("NETHERRUN has already started"), false);
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
        server.getPlayerManager().broadcast(Text.literal("Starting NETHERRUN"), false);
        return 1;
    }
    public void startRound(boolean forced) {
        if (allReady() || forced) {
            if (runningPlayer() != null) {
                ServerPlayerEntity sp = (ServerPlayerEntity) runningPlayer();
                roundActive = true;
                preGameTimer = TimeConvert.secondToTick(15);
                server.getPlayerManager().broadcast(Text.translatable("msg.netherrun.roundstart.success"), false);
                int spawnX = new Random().nextInt(20000) - 10000;
                int spawnZ = new Random().nextInt(20000) - 10000;
                sp.teleport(server.getWorld(World.NETHER), spawnX, 100, spawnZ, Set.of(), 0, 0, true);
                sp.changeGameMode(GameMode.SPECTATOR);
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
}
